import android.content.Context;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxAdapter extends BaseAdapter {
    private static class ViewHolder {
        private final View view;
        private final TextView textView;
        private Subscription subscription;

        public ViewHolder(View view) {
            this.view = view;
            this.textView = (TextView) view.findViewById(R.id.text_view);
        }
    }

    private final int items;
    private final LayoutInflater inflater;
    private final LruCache<Integer, String> cache;

    public RxAdapter(Context context, int items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.cache = new LruCache<>(500);
    }

    @Override
    public int getCount() {
        return items;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_list, parent, false);
            view.setTag(new ViewHolder(view));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        if (holder.subscription != null)
            holder.subscription.unsubscribe();

        holder.subscription = fetch(position)
            .subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    holder.textView.setText(s);
                }
            });

        return holder.view;
    }

    private Observable<String> fetch(final int position) {
        String value = cache.get(position);
        if (value != null)
            return Observable.just(value);

        return Observable.just(position)
            .map(new Func1<Integer, String>() {
                @Override
                public String call(Integer pos) {
                    String output = String.format("Position %d", pos);
                    cache.put(pos, output);

                    return output;
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}

