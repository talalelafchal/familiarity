package com.speakup.ui;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allysonpower.ui.R;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.speakup.parse.PartANewsInfo;

public class parta extends ListActivity {    
    private ParseQuery<PartANewsInfo> query;

    private List<Map<String, String>> newsData = new ArrayList<Map<String,String>>();  
    
    private int pullDownTimes = 0;  //如果总共有50条news 用户在第六次pull down的时候 我们就不去query服务器了 因为再次query 程序会fc
    //TT：有处理但是有问题；我发现有一点问题 就是有时候总是在刷新 不知道哪出的问题 还没发现突破口
    //连pull两次就会出现问题 第一次query 没有返回 再次pulldown 又发起了一次query 这个时候会fc还是 一直刷新..记不太清楚了

    private int howManyNews = 0;  //Parse中 总共存储了多少news
    private int numOfItmesInOnePage = 10;  //自定义的 每一页显示多少条news


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parta_home);
        //TT：需要修改的地方在这儿
        
        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
        
        //Register the subclass for ParseObject first and then call Parse.initialize
        ParseObject.registerSubclass(PartANewsInfo.class);
        Parse.initialize(this, "bDExeWi2vct7yqm52r5WPnEiuNyorLu9B2tSFREW", "MvzGGKqOt5Q56inQCPNpbYLAaiJmtykCjgh93C1K");
        
		query = ParseQuery.getQuery(PartANewsInfo.class);
		
		query.orderByDescending("createAt");
		query.setLimit(numOfItmesInOnePage); 
		//Launch partb => load the latest 10 news automatically
		//TT:意思是说在地图上只能加载最后的10条信息么？
		query.findInBackground(new FindCallback<PartANewsInfo>() {
			@SuppressWarnings("deprecation")
			@Override
			public void done(List<PartANewsInfo> newsList, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					Log.i("com.allysonpower.ui.GetNewsFromParse", "Retrieved " + newsList.size() + " news from Parse.");
					//TT这儿没打看懂
					//组合list中每一行的string内容  list中每一行中包含两小行 第一行大字体显示news信息（android.R.id.text1）  第二行小字体显示 上传时间（android.R.id.text2）
					//TT:应该是需要修改这儿吧。list中需要组合的不仅仅是文字和时间，还有事件地点 用户ID等信息
					for(int i = 0; i < newsList.size(); i++)
					{
						Map<String, String> map = new HashMap<String, String>();
				        map.put("News_Text", newsList.get(i).getEventText()); 
						Log.i("com.allysonpower.ui.paartb.GetNewsFromParse", "GetNewsEventText:" + newsList.get(i).getEventText());
						if(newsList.get(i).getPositive())
							map.put("News_Property", "Positive!   Post@" + newsList.get(i).getPostTime().toLocaleString());
						//TT：这儿应该也要修改吧
						else
							map.put("News_Property", "Negative!   Post@" + newsList.get(i).getPostTime().toLocaleString());
						
						newsData.add(map);
						Log.e("News_Property", newsData.toString());
					}
					//将从parse获得的数据 绑定到list中
					setListAdapter(new SimpleAdapter(getApplicationContext(),newsData,R.layout.parta_listview_two_text_item, 
							//TT：是不是要在这修改单条信息的XML
			                new String[]{"News_Text","News_Property"},             
			                new int[]{android.R.id.text1,android.R.id.text2})
					);
	
					//Get how many news after the first qurey
					query.countInBackground(new CountCallback() {
						  public void done(int count, ParseException e) {
						    if (e == null) {
						    	howManyNews = count;
						    	//如果第一次查询 返回的news不足一页 则隐藏PullToreFresh
						    	if(count < numOfItmesInOnePage) ((PullToRefreshListView) getListView()).hiddenPullToRefresh();
						    }
						    else howManyNews = numOfItmesInOnePage;
						  }
						});
				} else {
					Log.d("com.speakup.ui.partb.GetNewsFromParse", "Error:" + e.getMessage());
				}
			}
		});
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
        	
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {	
        	pullDownTimes++;
        	if(howManyNews - pullDownTimes*numOfItmesInOnePage < numOfItmesInOnePage)
        	{
        		//如果此页news不足一页 则隐藏PullToRefresh
            	((PullToRefreshListView) getListView()).hiddenPullToRefresh();
        	}
        	//如果还有更多news 则继续query from parse 
        	if(pullDownTimes*numOfItmesInOnePage < howManyNews) 
        	{
        		query.setSkip(pullDownTimes*numOfItmesInOnePage);   //You can skip the first results with setSkip. This can be useful for pagination:
        		query.findInBackground(new FindCallback<PartANewsInfo>() {
        			@Override
        			public void done(List<PartANewsInfo> newsList, ParseException e) {
        				// TODO Auto-generated method stub
        				if (e == null) {
        					//清空之前第一页的内容
        					newsData.clear();
        					//组合list中每一行的string内容  list中每一行中包含两小行 第一行大字体显示news信息（android.R.id.text1）  第二行小字体显示 上传时间（android.R.id.text2） 
        					for(int i = 0; i < newsList.size(); i++)
        					{
        						Map<String, String> map = new HashMap<String, String>();
        				        map.put("News_Text", newsList.get(i).getEventText()); 
        						Log.i("com.speakup.ui.paartb.GetNewsFromParse", "GetNewsEventText:" + newsList.get(i).getEventText());
        						if(newsList.get(i).getPositive())
        							map.put("News_Property", "Positive!   Post@" + newsList.get(i).getPostTime().toLocaleString());
        						else
        							map.put("News_Property", "Negative!   Post@" + newsList.get(i).getPostTime().toLocaleString());
        						
        						newsData.add(map);
        						Log.e("News_Property", newsData.toString());
        					}
        					//将从parse获得的数据 绑定到list中
        					setListAdapter(new SimpleAdapter(getApplicationContext(),newsData,R.layout.parta_listview_two_text_item,  
        			                new String[]{"News_Text","News_Property"},             
        			                new int[]{android.R.id.text1,android.R.id.text2})
        					);
        				} 
        				else {
        					Log.d("com.speakup.ui.partb.GetNewsFromParse", "Error:" + e.getMessage());
        				}
        			}
        		});
        		// Call onRefreshComplete when the list has been refreshed.
        		((PullToRefreshListView) getListView()).onRefreshComplete();
        		super.onPostExecute(result);
        	}
        }
   }

    private String[] mStrings = {"AllysonPower", "Pull to refresh."};
    //TT：这个什么意思
}
        

