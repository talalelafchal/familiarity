import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;


public class ReactiveRecylerAdapter<T> extends RecyclerView.Adapter<ReactiveRecylerAdapter.ReactiveViewHolder<T>> {
    private final Observable<List<T>> observable;
    private final ReactiveViewHolderFactory<T> viewHolderFactory;
    private List<T> currentList;

    public ReactiveRecylerAdapter(Observable<List<T>> observable, ReactiveViewHolderFactory<T> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        this.currentList = Collections.emptyList();
        this.observable = observable;
        this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
            this.currentList = items;
            this.notifyDataSetChanged();
        });
    }
    private PublishSubject<T> mViewClickSubject = PublishSubject.create();

    public Observable<T> getViewClickedObservable() {
        return mViewClickSubject;
    }

    @Override
    public ReactiveViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int pViewType) {
        ReactiveViewHolderFactory.ViewAndHolder<T> viewAndHolder = viewHolderFactory.createViewAndHolder(parent, pViewType);
        ReactiveViewHolder<T> viewHolder = viewAndHolder.viewHolder;

        RxView.clicks(viewAndHolder.view)
                .takeUntil(RxView.detaches(parent))
                .map(aVoid -> viewHolder.getCurrentItem())
                .subscribe(mViewClickSubject);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReactiveViewHolder<T> holder, int position) {
        T item = currentList.get(position);
        holder.setCurrentItem(item);
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    public static abstract class ReactiveViewHolder<T> extends RecyclerView.ViewHolder {
        public ReactiveViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setCurrentItem(T t);
        public abstract T getCurrentItem();
    }

    public interface ReactiveViewHolderFactory<T> {
        class ViewAndHolder<T> {
            public final View view;
            public final ReactiveViewHolder<T> viewHolder;

            public ViewAndHolder(View view, ReactiveViewHolder<T> viewHolder) {
                this.view = view;
                this.viewHolder = viewHolder;
            }
        }
        ViewAndHolder<T> createViewAndHolder(@NonNull ViewGroup parent, int pViewType);
    }
}