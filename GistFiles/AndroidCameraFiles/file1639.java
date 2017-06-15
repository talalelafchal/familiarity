package com.cube.arc.hzd.fragment;

import android.os.Bundle;

import com.cube.arc.hzd.MainApplication;
import com.google.android.gms.analytics.HitBuilders;

public class Test extends Fragment
{
	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		((MainApplication)getActivity().getApplication()).getTracker().setScreenName("Test screen view");
		((MainApplication)getActivity().getApplication()).getTracker().send(new HitBuilders.ScreenViewBuilder().build());
	}
}
