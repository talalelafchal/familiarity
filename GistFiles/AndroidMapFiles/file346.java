package svk.vk.market.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.util.HashMap;

/**
 * Хелпер для управления показом вьюх по id.
 * Toggle one view visible and another gone
 * При show(int id) - показыват только одна из всех.
 */
public class VisibleUtil {
    private HashMap<Integer,View> contentViews;
    private static final int ANIMATION_TIME = 200;

    public VisibleUtil() {

    }

    /**
     * @param views список вьюх
     */
    public void onCreate(View... views){
        contentViews = new HashMap<>();
        for (View view : views) {
            if (view == null) throw new IllegalArgumentException();
            contentViews.put(view.getId(), view);
        }
    }

    public void onDestroy(){
        if(contentViews != null){
            contentViews.clear();
        }
    }

    public void saveState(Bundle saveState) {
        saveState.putParcelable(VisibleStates.KEY_STATE, getState());
    }

    public Parcelable getState() {
        return new VisibleStates(contentViews.values());
    }

    public void retainState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        if (!savedInstanceState.containsKey(VisibleStates.KEY_STATE)) {
            return;
        }

        VisibleStates state = savedInstanceState.getParcelable(VisibleStates.KEY_STATE);
        if(state == null){
            return;
        }
        int k = 0;
        for (int keyView : state.viewIds) {
            final View contentView = contentViews.get(keyView);
            if (contentView.getVisibility() != state.viewVisibilities[k]) {
                contentView.setVisibility(state.viewVisibilities[k]);
            }
            k++;
        }
    }

    /**
     * показываем только один элемент из contentViews по id
     */
    public void show(int id) {
        if (!contentViews.containsKey(id)) {
            throw new IllegalArgumentException("Haven't content with this id " + id);
        }

        for (int keyView : contentViews.keySet()) {
            final boolean show = id == keyView;
            final View contentView = contentViews.get(keyView);
            final int finalVisibility = show ? View.VISIBLE : View.GONE;
            if (contentView.getVisibility() != finalVisibility) {
                contentView.setVisibility(finalVisibility);
                contentView.animate()
                        .setDuration(ANIMATION_TIME)
                        .alpha(show ? 1 : 0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                contentView.setVisibility(finalVisibility);
                            }
                        });
            }

        }
    }



}



