package com.cube.arc.hzd;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainApplication extends Application
{
  private Tracker tracker;
  
	public Tracker getTracker()
	{
		if (tracker == null)
		{
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(R.xml.ga_config);
			t.setScreenName(null);

			tracker = t;
		}

		return tracker;
	}
}
