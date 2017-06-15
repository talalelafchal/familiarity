import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SampleFragment extends ListFragment {
    private OnArticleSelectedListener mListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};

        ArrayList list = new ArrayList();

        for (int i = 0; i < values.length; i++) {
            HashMap hashMap = new HashMap();
            hashMap.put("name", values[i]);
            hashMap.put("description", "some description");
            hashMap.put("icon", R.drawable.ic_launcher);
            list.add(hashMap);
        }

        String[] from = new String[]{"name", "description", "icon"};
        int[] to = new int[]{R.id.firstLine, R.id.secondLine, R.id.icon};
        this.setListAdapter(new SimpleAdapter(getActivity(), list, R.layout.detail, from, to));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleSelectedListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onArticleSelected();
    }

    public interface OnArticleSelectedListener {
        public void onArticleSelected();
    }
}