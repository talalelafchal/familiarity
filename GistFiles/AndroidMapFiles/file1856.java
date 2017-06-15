import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class HashMapAdapter<K, V> extends BaseAdapter{

  private HashMap<K, V> mData = new HashMap<K, V>();
	private ArrayList<K> mKeys;

	public HashMapAdapter(HashMap<K, V> data) {
		mData = data;
		mKeys = new ArrayList<K>(mData.keySet());
		Collections.sort(mKeys, Collections.reverseOrder());		
	}
	
	public K getKey(int position)
	{
		return (K) mKeys.get(position);	
	}
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public V getItem(int position) {
		return mData.get(mKeys.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		K key = getKey(pos);
		V value = getItem(pos);		

		return onGetView(pos, key, value, convertView, parent );
	}

	protected abstract View onGetView(int pos, K key, V value, View convertView,
			ViewGroup parent);
}