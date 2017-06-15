public class ReactiveTextViewHolder<T> extends ReactiveViewHolder<T> {

    private TextView label;
    private T currentItem;

    public ReactiveTextViewHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(android.R.id.text1);
    }

    @Override
    public void setCurrentItem(T currentItem) {
        this.currentItem = currentItem;
        this.label.setText(currentItem.toString());
    }

    public T getCurrentItem() {
        return currentItem;
    }
}

ReactiveRecylerAdapter.ReactiveViewHolderFactory<String> viewAndHolderFactory = (parent, pViewType) -> {
    View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
    return new ReactiveRecylerAdapter.ReactiveViewHolderFactory.ViewAndHolder<>(
            view,
            new ReactiveRecylerAdapter.ReactiveTextViewHolder<>(view)
    );
};
ReactiveRecylerAdapter reactiveRecylerAdapter = new ReactiveRecylerAdapter(Observable.just("Here", "we", "go"), viewAndHolderFactory);