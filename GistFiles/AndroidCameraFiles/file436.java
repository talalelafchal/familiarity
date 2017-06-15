
TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
tabLayout.setupWithViewPager(mViewPager);

tabLayout.getTabAt(0).setCustomView(R.layout.design_fragment_icon_camera);
tabLayout.getTabAt(1).setCustomView(R.layout.design_fragment_line_weight);

