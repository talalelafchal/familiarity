package cn.changsha.xzw.Gen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import cn.changsha.xzw.Gen.User.UserLoginRequestActivity;

/**
 * Created by zcoffice on 2014/12/4.
 */
public class BaseGen extends Activity {

    protected static UserLoginRequestActivity userLoginRequestActivity;

    /**
     * 显示会员相册图片数据的模块调用来源类型 0:未设定 1:全部数据 2:分类数据 3:个人数据
     */
    //protected static UserAlbumPicListShowModule userAlbumPicListShowModule;

    //protected static UserLoginRequestActivity userLoginRequestActivity;
    /**
     * 检查会员是否登录
     * @param context
     */
    protected boolean UserCheckIsLogined(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String userName = sp.getString("USER_NAME","");
        //TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        if(userName.equals("")){ //not login
            return false;
        }else{ //已经登录
            return true;
        }
    }

    /**
     * 当前会员id
     * @param context
     * @return
     */
    protected int GetNowUserId(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(sp != null){
            int userId = sp.getInt("USER_ID",0);
            return userId;
        }else{
            return -1;
        }
    }

    /**
     * 当前登录的会员帐号
     * @param context
     * @return
     */
    protected String GetNowUserName(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(sp != null){
            String userName = sp.getString("USER_NAME","");
            return userName;
        }else{
            return null;
        }
    }

    /**
     * 当前登录的会员密码（经过md5加密）
     * @param context
     * @return
     */
    protected String GetNowUserPass(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(sp != null){
            String userName = sp.getString("USER_PASS","");
            return userName;
        }else{
            return null;
        }
    }

    /**
     * 当前登录的会员点卷
     * @param context
     * @return
     */
    protected int GetNowUserPoint(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(sp != null){
            int userId = sp.getInt("USER_POINT",0);
            return userId;
        }else{
            return -1;
        }
    }

    /**
     * 检查SDCard是否装载
     * @return 逻辑值
     */
    protected boolean CheckExternalStorage(){
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}
