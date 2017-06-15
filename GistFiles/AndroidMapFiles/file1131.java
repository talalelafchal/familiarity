import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExampleExtendingAdapter extends ContentWithTitlesAdapter<String> {
    public ExampleExtendingAdapter(HashMap<String, List<String>> data) {
        super(data, R.layout.example_title_layout, R.id.example_title_textview);
    }

    @Override
    RecyclerView.ViewHolder onCreateContentHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_content_layout,
                viewGroup, false);
        return new ExampleViewHolder(v);
    }

    private int iterations = 0;

    @Override
    void onBindContentHolder(RecyclerView.ViewHolder holder, int position) {
        ExampleViewHolder exampleHolder = (ExampleViewHolder) holder;
        exampleHolder.exampleText.setText(getContent(position)+" (shown "+iterations+" times)");
        iterations += 1;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView exampleText;

        ExampleViewHolder(View itemView) {
            super(itemView);
            exampleText = (TextView) itemView.findViewById(R.id.example_content_Text);
        }
    }
}