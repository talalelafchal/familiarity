import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultiTouchSwipeableAdapter<T extends MultiTouchSwipeableAdapter.Identifiable> extends ArrayAdapter<T> {

    private AdapterView parent;

    public MultiTouchSwipeableAdapter(Context context, int textViewResourceId,
                                      List<T> objects, AdapterView parent) {
        super(context, textViewResourceId, objects);
        this.parent = parent;
        int minHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        parent.setMinimumHeight(minHeight);
    }

    public interface Identifiable {
        String getId();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    protected void resetView(View view) {
        view.setAlpha(1);
        view.setTranslationX(0);
    }

    protected void bindView(View view) {
        view.setOnTouchListener(new SwipeListener<>(this));
    }

    private static class SwipeListener<T extends Identifiable> implements View.OnTouchListener {

        private static final int SWIPE_DURATION = 250;
        private static final int MOVE_DURATION = 200;
        private static final int INTERVAL = 50;

        private static boolean mSwiping = false;
        private static int mDownCount = 0;

        private float mDownX;
        private int mSwipeSlop = -1;
        private Context context;
        private AdapterView parent;
        private MultiTouchSwipeableAdapter<T> multiTouchSwipeableAdapter;

        HashMap<Long, CoOrds> mItemIdCoOrdsMap = new HashMap<>();

        private class CoOrds {
            int top;
            int left;

            public CoOrds(int top, int left) {
                this.top = top;
                this.left = left;
            }
        }

        private static List<ViewPendingRemoval> viewsPendingRemoval = new ArrayList<>();

        private static class ViewPendingRemoval implements Comparable<ViewPendingRemoval> {
            int position;
            View view;

            public ViewPendingRemoval(View view, int position) {
                this.position = position;
                this.view = view;
            }

            @Override
            public int compareTo(ViewPendingRemoval another) {
                return Integer.valueOf(position).compareTo(another.position);
            }
        }

        private SwipeListener(MultiTouchSwipeableAdapter<T> multiTouchSwipeableAdapter) {
            this.context = multiTouchSwipeableAdapter.getContext();
            this.parent = multiTouchSwipeableAdapter.parent;
            this.multiTouchSwipeableAdapter = multiTouchSwipeableAdapter;
        }

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(context).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownCount++;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mSwiping = false;
                    parent.setEnabled(true);
                    parent.requestDisallowInterceptTouchEvent(false);
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mDownCount--;
                    if (mDownCount == 0) {
                        beginRemoval();
                    }
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        if (fractionCovered > 1) {
                            fractionCovered = 1;
                        }
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        parent.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                        if (remove) {
                            int pos = 0;
                            for (int i = 0; i < parent.getChildCount(); i++) {
                                if (v == parent.getChildAt(i)) {
                                    pos = i;
                                }
                            }
                            queueRemoval(v, pos);
                        } else {
                            mSwiping = false;
                            parent.setEnabled(true);
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }
                mDownCount--;
                if (mDownCount == 0) {
                    beginRemoval();
                }
                break;
                default:
                    return false;
            }
            return true;
        }


        private void queueRemoval(View view, int position) {
            if (view != null) {
                viewsPendingRemoval.add(new ViewPendingRemoval(view, position));
            }
        }

        private void beginRemoval() {
            if (viewsPendingRemoval.size() == 0) {
                return;
            }
            Collections.sort(viewsPendingRemoval);
            View[] views = new View[viewsPendingRemoval.size()];
            for (int i = 0; i < views.length; i++) {
                views[i] = viewsPendingRemoval.get(i).view;
            }
            animateRemoval(views);
            viewsPendingRemoval.clear();
        }


        private void animateRemoval(final View... viewsToRemove) {
            int firstVisiblePosition = parent.getFirstVisiblePosition();
            for (View viewToRemove : viewsToRemove) {
                for (int i = 0; i < parent.getChildCount(); ++i) {
                    View child = parent.getChildAt(i);
                    if (child != viewToRemove) {
                        int position = firstVisiblePosition + i;
                        long itemId = multiTouchSwipeableAdapter.getItemId(position);
                        if (mItemIdCoOrdsMap.get(itemId) == null) {
                            mItemIdCoOrdsMap.put(itemId, new CoOrds(child.getTop(), child.getLeft()));
                        }
                    }
                }
            }
            int removalCount = 0;
            for (View viewToRemove : viewsToRemove) {
                int position = parent.getPositionForView(viewToRemove);
                multiTouchSwipeableAdapter.remove(multiTouchSwipeableAdapter.getItem(position - removalCount++));
            }
            final ViewTreeObserver observer = parent.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    boolean firstAnimation = true;
                    int firstAnimationPos = 0;
                    int firstVisiblePosition = parent.getFirstVisiblePosition();
                    for (int i = 0; i < parent.getChildCount(); ++i) {
                        final View child = parent.getChildAt(i);
                        int position = firstVisiblePosition + i;
                        long itemId = multiTouchSwipeableAdapter.getItemId(position);
                        CoOrds coOrds = mItemIdCoOrdsMap.get(itemId);
                        int top = child.getTop();
                        int left = child.getLeft();
                        if (coOrds != null) {
                            int startTop = coOrds.top;
                            int startLeft = coOrds.left;

                            int deltaTop = startTop - top;
                            int deltaLeft = startLeft - left;
                            if (deltaTop != 0 || deltaLeft != 0) {
                                child.setTranslationY(deltaTop);
                                child.setTranslationX(deltaLeft);
                                child.animate().setStartDelay((i - firstAnimationPos) * INTERVAL).setDuration(MOVE_DURATION).translationY(0).translationX(0);
                                if (firstAnimation) {
                                    child.animate().withEndAction(new Runnable() {
                                        public void run() {
                                            mSwiping = false;
                                            parent.setEnabled(true);
                                            parent.requestDisallowInterceptTouchEvent(false);
                                        }
                                    });
                                    firstAnimation = false;
                                }
                            } else {
                                firstAnimationPos = i + 1;
                            }

                        } else {
                            int childHeight = child.getHeight();
                            int childWidth = child.getWidth();
                            int startTop = top + (i - viewsToRemove.length > 0 ? childHeight : -childHeight);
                            int oldPos = (i + viewsToRemove.length) % 2;
                            int startLeft = oldPos * childWidth;
                            int deltaTop = startTop - top;
                            int deltaLeft = startLeft - left;
                            child.setTranslationY(deltaTop);
                            child.setTranslationX(deltaLeft);
                            child.animate().setStartDelay((i - firstAnimationPos) * INTERVAL).setDuration(MOVE_DURATION).translationY(0).translationX(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        parent.setEnabled(true);
                                        parent.requestDisallowInterceptTouchEvent(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    }
                    mItemIdCoOrdsMap.clear();
                    return true;
                }
            });
        }
    }
}