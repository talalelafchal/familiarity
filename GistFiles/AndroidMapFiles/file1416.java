package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.activeandroid.query.From;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.SentenceBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;

public class SentencesAdapter extends ArrayAdapter<Sentence> {

    private static final int PAGE_SIZE = 100;
    private final From select;
    Map<String, Language> languages = new HashMap<>();
    private int offset;

    public <T extends BaseActivity> SentencesAdapter(T activity, From select) {
        super(activity, R.layout.sentence_collection, new ArrayList<Sentence>());
        this.select = select;
        this.offset = 0;
        loadMore();
    }

    private void loadMore() {
        List<Sentence> rows = select.offset(offset).limit(PAGE_SIZE).execute();
        this.offset = offset + PAGE_SIZE;
        this.addAll(rows);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SentenceBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.sentence, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        binding.setSentence(getItem(position));

        if (position == offset - 1) {
            System.out.println("bu");
            loadMore();
        }

        return binding.getRoot();
    }

}
