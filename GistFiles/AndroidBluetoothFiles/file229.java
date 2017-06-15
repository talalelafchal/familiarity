package cn.changsha.xzw;

import android.app.Application;

import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKSearch;
import sense.support.v1.DataProvider.Activity.Activity;

public class AppApplication extends Application {
    private int DisplayWidth;
    private Activity NowSelectActivity;


    public double getNowLatitude() {
        return NowLatitude;
    }

    public void setNowLatitude(double nowLatitude) {
        NowLatitude = nowLatitude;
    }

    public double getNowLongitude() {
        return NowLongitude;
    }

    public void setNowLongitude(double nowLongitude) {
        NowLongitude = nowLongitude;
    }

    private double NowLatitude;
    private double NowLongitude;

    public BMapManager mBMapMan = null;
    public MKSearch mSearch = null;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    public void onCreate() {
        super.onCreate();

        mBMapMan=new BMapManager(getApplicationContext());
        mBMapMan.init(null);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);

        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.requestLocation();
        else
            Log.e("LocSDK3", "locClient is null or not started");
    }

    public Activity getNowSelectActivity() {
        return NowSelectActivity;
    }

    public void setNowSelectActivity(Activity nowSelectActivity) {
        NowSelectActivity = nowSelectActivity;
    }

    /**
     * 返回屏幕宽度
     * @return
     */
    public int getDisplayWidth() {
        return DisplayWidth;
    }

    /**
     * 设置屏幕宽度
     * @param displayWidth
     */
    public void setDisplayWidth(int displayWidth) {
        DisplayWidth = displayWidth;
    }



    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;

            setNowLatitude(location.getLatitude());
            setNowLongitude(location.getLongitude());

            /**
             StringBuffer sb = new StringBuffer(256);
             sb.append("time : ");
             sb.append(location.getTime());
             sb.append("\nerror code : ");
             sb.append(location.getLocType());
             sb.append("\nlatitude : ");
             sb.append(location.getLatitude());
             sb.append("\nlontitude : ");
             sb.append(location.getLongitude());
             sb.append("\nradius : ");
             sb.append(location.getRadius());
             if (location.getLocType() == BDLocation.TypeGpsLocation){
             sb.append("\nspeed : ");
             sb.append(location.getSpeed());
             sb.append("\nsatellite : ");
             sb.append(location.getSatelliteNumber());
             } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
             sb.append("\naddr : ");
             sb.append(location.getAddrStr());
             }

             ToastObject.Show(getApplicationContext(),sb.toString());
             */
            //mSearch.geocode("洞井铺", "长沙");
            //logMsg(sb.toString());
        }
        public void onReceivePoi(BDLocation poiLocation) {
            /**
             //将在下个版本中去除poi功能
             if (poiLocation == null){
             return ;
             }
             StringBuffer sb = new StringBuffer(256);
             sb.append("Poi time : ");
             sb.append(poiLocation.getTime());
             sb.append("\nerror code : ");
             sb.append(poiLocation.getLocType());
             sb.append("\nlatitude : ");
             sb.append(poiLocation.getLatitude());
             sb.append("\nlontitude : ");
             sb.append(poiLocation.getLongitude());
             sb.append("\nradius : ");
             sb.append(poiLocation.getRadius());
             if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
             sb.append("\naddr : ");
             sb.append(poiLocation.getAddrStr());
             }
             if(poiLocation.hasPoi()){
             sb.append("\nPoi:");
             sb.append(poiLocation.getPoi());
             }else{
             sb.append("noPoi information");
             }
             ToastObject.Show(getApplicationContext(), sb.toString());
             */
        }
    }
}
