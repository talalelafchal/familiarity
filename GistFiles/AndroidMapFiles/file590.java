package com.fairyteller.lolcoiffeur.fragments.list;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fairyteller.lolcoiffeur.LOLCoiffeurFresqueActivity;
import com.fairyteller.lolcoiffeur.R;
import com.fairyteller.lolcoiffeur.content.impl.Coiffure;
import com.fairyteller.lolcoiffeur.content.impl.LOLCoiffeur;
import com.fairyteller.tools.DrawableManager;

public class LOLCoiffeurListFragment extends ListFragment {

	private static final String TAG = "LOLCoiffeurListFragment";
	private LayoutInflater inflater;
	private LOLCoiffeurAdapter adapter;
	private DrawableManager drawableManager;
	
	
	private static Map<Uri, SoftReference<Bitmap>> cache;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View res = inflater.inflate(R.layout.listfragment, container, false);
		this.inflater = inflater;
		this.adapter = new LOLCoiffeurAdapter();
		this.cache = new HashMap<Uri, SoftReference<Bitmap>>();
		this.drawableManager = new DrawableManager();
		setListAdapter(adapter);
		return res;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);
		Log.d(TAG, "clic sur l'item "
				+ adapter.coiffeurs.get(position).getMessage());
//		((LOLCoiffeurImageFragment) getFragmentManager().findFragmentById(
//				R.id.detailCoiffeur)).setLOLCoiffeur(adapter.coiffeurs
//				.get(position));
		LOLCoiffeur coiffeur = adapter.coiffeurs
				.get(position);
//		Intent intent = new Intent(getActivity(), LOLCoiffeurDetailsActivity.class);
		Intent intent = new Intent(getActivity(), LOLCoiffeurFresqueActivity.class);
		intent.putExtra("targetCoiffeur", coiffeur);
		intent.putExtra("targetCoiffeurArrayList", (ArrayList<LOLCoiffeur>)adapter.coiffeurs);
		intent.putExtra("targetCoiffeurPosition", position);
		startActivity(intent);

	}

	public void refresh() {
		List<LOLCoiffeur> res = new ArrayList<LOLCoiffeur>();
		Coiffure.getLOLCoiffeur(getActivity(), LOLCoiffeur.CONTENT_URI, res);
		cache.clear();
		adapter.setCoiffeurs(res);
	}

	public class LOLCoiffeurAdapter extends BaseAdapter {
		List<LOLCoiffeur> coiffeurs;

		public void setCoiffeurs(List<LOLCoiffeur> coiffeurs) {
			if (coiffeurs != null) {
				Iterator<LOLCoiffeur> iter = coiffeurs.iterator();
				while (iter.hasNext()) {
					LOLCoiffeur coiffeur = iter.next();
					if (coiffeur.getLocalFileUri() == null) {
						iter.remove();
					}
				}
			}
			this.coiffeurs = coiffeurs;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return coiffeurs != null ? coiffeurs.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return coiffeurs != null ? coiffeurs.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return coiffeurs != null ? coiffeurs.get(position).get_id() : -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.listitem, null);
			TextView textView = (TextView) convertView
					.findViewById(R.id.coiffeurText);
			LOLCoiffeur coiffeur = (LOLCoiffeur) getItem(position);
			if(coiffeur.getMessage()!=null){
				textView.setText(Html.fromHtml(coiffeur.getMessage()));
//				textView.setText(coiffeur.getMessage());
			} else {
				textView.setText(null);
			}
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.coiffeurListImage);
//			BitmapUtils.setImageResize(getActivity(), imageView, coiffeur, cache);
			if(coiffeur.getLocalFileUri()!=null){
				drawableManager.fetchDrawableOnThread(getActivity(), coiffeur.getLocalFileUri(), imageView);
			}
			return convertView;
		}
	}
}