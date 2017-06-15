import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * A {@link android.support.v7.widget.RecyclerView.Adapter} used for displaying content w/ titles</br>
 * While it works for me, it might be better to edit this file if you want to customize what happens
 * to the titles (For example, if you want to do more to the title views other than just setting
 * the text)
 *
 * @param <K> Data type that you're supplying to the RecyclerView for your normal views
 */
public abstract class ContentWithTitlesAdapter<K> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int titleLayoutId, textViewId;
    // The data is essentially broken into multiple lists, each with a title. For example:
    // Map<TitleString, List<DataForThisSection>>
    private Map<String, List<K>> data;

    /**
     * Creates an instance of the class for use
     * @param data The data is essentially broken into multiple lists,
     *             each with a title. For example:</br> Map<TitleString, List<DataForThisSection>>
     * @param titleLayoutId The android ID of the layout to be used in the title areas
     * @param textViewId The android ID of the TextView to be used as the title text
     */
    public ContentWithTitlesAdapter(Map<String, List<K>> data, int titleLayoutId, int textViewId) {
        this.titleLayoutId = titleLayoutId;
        this.textViewId = textViewId;
        this.data = data;
    }

    abstract RecyclerView.ViewHolder onCreateContentHolder(ViewGroup viewGroup, int viewType);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // If title, inflate. Otherwise, let the extending class handle
        if (viewType == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(titleLayoutId, viewGroup,
                    false);
            return new TitleViewHolder(v, textViewId);
        } else
            return onCreateContentHolder(viewGroup, viewType);
    }

    abstract void onBindContentHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            titleHolder.textContainer.setText(getTitle(position));
        } else
            onBindContentHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        itemCount += data.keySet().size();
        for (Map.Entry<String, List<K>> set : data.entrySet()) {
            itemCount += set.getValue().size();
        }
        return itemCount;
    }

    public String getTitle(int position) {
        int checkPos = 0;
        for (Map.Entry<String, List<K>> set : data.entrySet()) {
            // If we passed the title
            if (checkPos > position + 1) break;
            // If it's the title
            if (checkPos == position)
                return set.getKey();
            checkPos += 1;
            checkPos += set.getValue().size();
        }
        return "Not Found";
    }

    public K getContent(int position) {
        int checkPos = 0;
        for (Map.Entry<String, List<K>> set : data.entrySet()) {
            // If it's the title
            if (checkPos == position)
                return null;
            checkPos += 1;
            for (K data : set.getValue()) {
                // If it's one of the values of the inside
                if (checkPos == position)
                    return data;
                checkPos += 1;
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        int checkPos = 0;
        for (Map.Entry<String, List<K>> set : data.entrySet()) {
            // If it's the title
            if (checkPos == position)
                return 0;
            checkPos += 1;
            for (K ignored : set.getValue()) {
                // If it's one of the values of the inside
                if (checkPos == position)
                    return 1;
                checkPos += 1;
            }
        }
        return -1;
    }

    public void setData(Map<String, List<K>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    protected static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView textContainer;

        TitleViewHolder(View itemView, int textViewId) {
            super(itemView);
            textContainer = (TextView) itemView.findViewById(textViewId);
        }
    }
}