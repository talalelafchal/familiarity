package test.fragment.list;

import java.util.ArrayList;
import java.util.List;

import test.fragment.R;
import test.fragment.list.ImageFactory.Type;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * サンプル(ListFragment + LruCache)<BR>
 * 1) 別スレで画像を読み込む<BR>
 * 2) 読込中は default画像 を表示<BR>
 * 3) 読込完了したら後から読み込んだ画像を動的に反映
 *
 * @author daichan4649
 */
public class TestListFragment extends ListFragment {

    // testdata
    private static final List<String> dataList;
    static {
        dataList = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
            dataList.add(Integer.toString(i));
        }
    }

    /** 保持cache最大数(Bitmap数) */
    private static final int CACHE_SIZE_MAX = 20;

    private BitmapLruCache lruCache;
    private TestAdapter adapter;

    public static TestListFragment newInstance() {
        return new TestListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cache初期化
        lruCache = new BitmapLruCache(CACHE_SIZE_MAX);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setFastScrollEnabled(true);

        adapter = new TestAdapter(getActivity());
        adapter.addAll(dataList);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // cache解放処理(全要素)
        lruCache.evictAll();
    }

    private class TestAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;

        public TestAdapter(Context context) {
            super(context, 0);
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_column, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setImageBitmap(getCachedBitmap(position));
            holder.text.setText(getItem(position));
            return convertView;
        }

        private class ViewHolder {
            private ImageView icon;
            private TextView text;
        }
    }

    private static class BitmapLruCache extends LruCache<Integer, Bitmap> {

        public BitmapLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(Integer key, Bitmap value) {
            // ココで返却したサイズ と コンストラクタに渡すmaxSize で
            // 自動的に保持キャッシュサイズの調整が行われる。
            // 例えば、保持キャッシュサイズを
            // 1) 要素数で制限したい場合、1 を返却(superと一緒)
            // 2) 要素保持データサイズ合計値(保持Bitmapサイズ量合計)で制限したい場合、
            //    各要素のサイズ(取得Bitmapのサイズ) を返却
            return super.sizeOf(key, value);
        }

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
            // cache解放処理
            if (!oldValue.isRecycled()) {
                oldValue.recycle();
                oldValue = null;
            }
        }
    }

    /**
     * Bitmap(cache)取得
     * @param position リスト表示位置
     * @return
     */
    private Bitmap getCachedBitmap(int position) {
        Bitmap cache = lruCache.get(position);
        if (cache == null || cache.isRecycled()) {
            // cacheがない(or 使えない)場合
            // - cache読込要求
            // - 読込中画像表示
            requestCacheLoad(new Integer[] { position });
            return ImageFactory.getBitmap(getActivity(), Type.LOADING);
        }
        return cache;
    }

    /**
     * cache読込要求
     * @param positions リスト表示位置(複数)
     */
    private void requestCacheLoad(final Integer[] positions) {
        // 非同期でデータ取得
        // 取得完了時
        //   データをcacheにつめる
        //   リスト再読込(該当cacheがリスト表示中要素の場合)
        // -->
        // AsyncTask作りすぎると RejectedExecutionException が発生する
        // (スレッド処理が重いとき、空きスレッド数より作成要求が増えたとき)
        // キューイングの仕組みをかます必要あり。
        new LoadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, positions);
    }

    private class LoadAsyncTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... positions) {
            for (int position : positions) {
                // cache読込(読込時間に 100msec かかった体で)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                Bitmap bitmap = ImageFactory.getBitmap(getActivity(), Type.DROID);
                if (lruCache != null) {
                    lruCache.put(position, bitmap);
                }

                // notify
                publishProgress(position);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            notifyCacheUpdate(values[0]);
        }
    }

    private void notifyCacheUpdate(int position) {
        if (adapter != null) {
            // 現在表示中項目の場合のみ、ListView再描画
            if (isVisibleRange(position)) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private boolean isVisibleRange(int position) {
        if (!isResumed()) {
            return false;
        }
        ListView listView = getListView();
        int firstPosition = listView.getFirstVisiblePosition();
        int lastPosition = listView.getLastVisiblePosition();
        if (firstPosition <= position && position <= lastPosition) {
            return true;
        }
        return false;
    }
}
