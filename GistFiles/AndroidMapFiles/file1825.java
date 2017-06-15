package com.example.xxx;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

public class SearchFM extends Fragment{
	
	Context  mContext;
	//TextView tx;
	Button btnNext;
	FragmentActivity mActivity;
	SupportMapFragment mapFragment;
	View rootView;
	GoogleMap mMap;
	
    int[] resId = { R.drawable.post6
            , R.drawable.post4, R.drawable.post7
            , R.drawable.post6, R.drawable.post7
            , R.drawable.post5, R.drawable.post3
            , R.drawable.travel9, R.drawable.post4
            , R.drawable.travel9, R.drawable.travel9 };

    String[] list = { "�Ѵ��س�Ҫ������", "�Ѵ�������ѵ���ʴ����", "�Ѵ���વؾ������ѧ�������Ҫ����������"
            , "��з����觨ѡ����һ���ҷ", "�Ѵ�������", "�Ѵ�ҹ����", "Sephiroth"
            , "Tifa Lockhart", "Vincent Valentine", "Yuffie Kisaragi"
            , "ZackFair" };
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	    if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null)
	            parent.removeView(rootView);
	    }
	    try {
	    	
			mActivity = getActivity();
			rootView = inflater.inflate(R.layout.fm_serach, container, false);
			ListView lvTour = (ListView)rootView.findViewById(R.id.lv_serachTourist);
			Spinner spTypeLocation = (Spinner)rootView.findViewById(R.id.spTypeLocation);
			Spinner spDistance = (Spinner)rootView.findViewById(R.id.spDistance);
			btnNext = (Button) rootView.findViewById(R.id.btnNext);
			
			SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
	        mMap =  mMapFragment.getMap();
	        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
	        transaction.replace(R.id.fmGooglemap, mMapFragment).commit();
			
			mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fmGooglemap);
			List<String> list_Location = new ArrayList<String>();
			List<String> list_Distance = new ArrayList<String>();
			
			list_Location.add("All");
			list_Location.add("����ѵ���ʵ��");
			list_Location.add("�����ҵ�");
			list_Location.add("��Ż��Ѳ�����");
			list_Location.add("���������ҧ����");
			list_Location.add("�����");
			
			list_Distance.add("1 Km");
			list_Distance.add("5 Km");
			list_Distance.add("10 Km");
			
			ArrayAdapter<String> locatAdapter = new ArrayAdapter<String> (mActivity, android.R.layout.simple_spinner_item,list_Location);
			locatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spTypeLocation.setAdapter(locatAdapter);
			
			ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String> (mActivity, android.R.layout.simple_spinner_item,list_Distance);
			distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spDistance.setAdapter(distanceAdapter);
			CustAdapSearch adapter= new CustAdapSearch(getActivity(), list,resId);
			lvTour.setAdapter(adapter);
			
			btnNext.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(getActivity(), SearchLocationDetail.class);
					//it.putExtra("imagefrom", "gallery");
					startActivity(it);
				}
			});
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    	
	    	Log.d("Error:", ""+e);
	    }

		return rootView;
		
	}
	
    public class searchHolder {
    	ImageView imgvMap;
    	Spinner spTypeLocation,spDistance;
    	Button btnNext;
    	ListView lvSearchTour;
    }

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.d("mapfragment", "="+mapFragment);
		if(mapFragment != null) {
			try {
				getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}  

	
	
}
