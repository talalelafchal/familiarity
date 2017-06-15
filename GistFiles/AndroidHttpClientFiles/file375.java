//调试日誌
System.out.printf("app load time:"+(time2-time1));
Log.i(TAG, "app load time:"+(time2-time1));
Logger.initialize(mContext).debug(true).i(TAG, url);


//中英文资源
String tips = String.format(mContext.getResources().getString(R.string.house_list_tip_area_down),  orderNameArr[1]);

// string轉int
int sectionId = Integer.parseInt(sectionIdStr);

// 文字設置
TextView tv_linkman = (TextView) findViewById(R.id.tv_linkman);
tv_linkman.setTextColor(0xff000000);
SpannableString spanableInfo = new SpannableString(tips);		
spanableInfo.setSpan(new ForegroundColorSpan(0xfff74400), 0, tips.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
spanableInfo.setSpan(new RelativeSizeSpan(1.5f), 0, tips.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// 文字設置
String title = "利率<font color=\"#f74400\">"+ tips +"</font>";
tv_linkman.setText(Html.fromHtml(title));

// 頁面轉跳
Intent intent = new Intent();
intent.setClass(HouseDetailActivity.this, HouseReportActivity.class);
Bundle bundle = new Bundle();
bundle.putSerializable("house", mHouse);
intent.putExtras(bundle);
startActivity(intent);

// 值接收
Bundle extras = getIntent().getExtras();
if (extras != null) {
  mHouse = (House) extras.getSerializable("house");
}

// ArrayList轉,字符
ArrayList<String> mMaxPhotoList = new ArrayList<String>();
mMaxPhotoList.add("http://p1.debug.591.com.hk/house/active/2015/04/23/142976048097370903_378x269.jpg");
mMaxPhotoList.add("http://p1.debug.591.com.hk/house/active/2015/04/23/142976048097370903_378x269.jpg");
String str = TextUtils.join(", ", mMaxPhotoList);



