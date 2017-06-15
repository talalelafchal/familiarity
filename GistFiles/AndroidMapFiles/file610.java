package com.example.xxx;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchFM extends Fragment{
	
	Context  mContext;
	//TextView tx;
	Button btnNext;
	FragmentActivity mActivity;
	SupportMapFragment mapFragment;
	
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
		mActivity = getActivity();
		
		View rootView = inflater.inflate(R.layout.fm_serach, container, false);
		ListView lvTour = (ListView)rootView.findViewById(R.id.lv_serachTourist);
		Spinner spTypeLocation = (Spinner)rootView.findViewById(R.id.spTypeLocation);
		Spinner spDistance = (Spinner)rootView.findViewById(R.id.spDistance);
		//Fragment mapFragment = (Fragment)rootView.findViewById(R.id.fmGooglemap);
		btnNext = (Button) rootView.findViewById(R.id.btnNext);
		
		//FragmentManager fm = getSupportFragmentManager();
		//Fragment fragment_byID = fm.findFragmentById(R.id.fmGooglemap);
		//FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
				getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		super.onDestroyView();
	}  

	
}
