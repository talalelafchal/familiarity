//Basado en: http://thepseudocoder.wordpress.com/2011/10/04/android-tabs-the-fragment-way/

	/**
	* Setup TabHost
	*/
	private void initialiseTabHost(Bundle savedInstanceState) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		//****** tab1 start
		TabHost.TabSpec tabSpec_statistics = mTabHost.newTabSpec("STATS");
		tabSpec_statistics.setIndicator("Estadísticas");
		tabSpec_statistics.setContent(new TabFactory(this));
		String tagStats = tabSpec_statistics.getTag();
		
		FragmentManager fm = this.getSupportFragmentManager();
		fragmentStats = fm.findFragmentByTag(tagStats);
		if(fragmentStats == null)
			fragmentStats = Fragment.instantiate(this, SingleBikeStation_tabStatistics.class.getName(), savedInstanceState);
		
		mTabHost.addTab(tabSpec_statistics);
		//****** tab1 end
		
		//****** tab2 start
		TabHost.TabSpec tabSpec_map = mTabHost.newTabSpec("MAP");
		tabSpec_map.setIndicator("Mapa");
		tabSpec_map.setContent(new TabFactory(this));
		String tagMap = tabSpec_map.getTag();
		
		fm = this.getSupportFragmentManager();
		fragmentMap = fm.findFragmentByTag(tagMap);
		if(fragmentMap == null)
			fragmentMap = Fragment.instantiate(this, SingleBikeStation_tabMap.class.getName(), savedInstanceState);
		
		mTabHost.addTab(tabSpec_map);
		//****** tab2 end
		
		OnTabChangeListener listener = null;
		mTabHost.setOnTabChangedListener(listener=new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabTagToOpen) {
				Fragment newTab = null;
				if(tabTagToOpen == "STATS")
					newTab = fragmentStats;
				else
					newTab = fragmentMap;
				
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(android.R.id.tabcontent, newTab, tabTagToOpen);
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
			}
		});
		
		// Default to first tab
		listener.onTabChanged("STATS");
	}