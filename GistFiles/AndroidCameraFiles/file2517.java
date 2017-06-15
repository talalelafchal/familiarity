package com.ctrlsmart.excuter;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ctrlsmart.Net.MyHttpClient;
import com.ctrlsmart.bean.EditListInfo;
import com.ctrlsmart.bean.ImeiEncode;
import com.ctrlsmart.fpcx.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

/**
 * Created by Administrator on 2015/3/31.
 */
public class MyAsyncTask extends AsyncTask <Void, Void, String>{
    Context context;
    Toast toast;
    ReturnStringCallBack rCB;
    EditListInfo info;
    public MyAsyncTask(Context context,EditListInfo info, ReturnStringCallBack rCB){
        this.context = context;
        this.info = info;
        this.rCB = rCB;
    }

    public interface ReturnStringCallBack{
        public void changeFragment();
    }

    private boolean checkResponse(org.json.JSONObject paramJSONObject)
    {
        try
        {
            if ("true".equals(paramJSONObject.getString("status"))) {
                return true;
            }
            String str = paramJSONObject.getString("errMsg");
            toast.makeText(context, str,  Toast.LENGTH_SHORT).show();
            return false;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
        return false;
    }

    private String parseJSON(String paramString)
    {
        try
        {
            boolean bool = checkResponse(new org.json.JSONObject(paramString));
            if (bool) {
                return "1";
            }
            return "0";
        }
        catch (JSONException localJSONException)
        {
            localJSONException.printStackTrace();
        }
        return null;
    }

    protected String doInBackground(Void... paramVarArgs)
    {
        String str4 = ((TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        str4 = str4.substring(0,9)+(int)(Math.random()*(10000000));
//        Log.e("MyAsyncTask","1111111111getDeviceId 356521052619884 === "+str4);
        str4 = CalcMEIDCheckDigit(str4.substring(0,14));

//        Log.e("MyAsyncTask","222222222getDeviceId 356521052619884 === "+str4);
        String str5 = new ImeiEncode().GetEncodeData(str4);
        String str3 = new ImeiEncode().GetEncodeData( this.info.getIssue()+this.info.getBillNumber() );
        String url =this.context.getString(R.string.IP) + this.context.getString(R.string.ServletPath) + "/QueryBarcodeInput?Barcode=" + str3 + "&Imei=" + str5;
//        Log.e("MyAsyncTask","url==="+url);
        return new MyHttpClient().executeGet(url);
    }

    protected void onPostExecute(String paramString)
    {
        if (paramString != "")
        {
            String str1 = parseJSON(paramString.toString());
            if ((StringUtils.isNotEmpty(str1)) && ("1".equals(str1))) {
                try
                {
                    String str3 = new org.json.JSONObject(paramString).getString("billInfo");
                    EditListInfo resultBillInfo = ((EditListInfo)com.alibaba.fastjson.JSONObject.parseObject(str3, EditListInfo.class));
                    //this.info = resultBillInfo;
                    this.info.setBillNumber(resultBillInfo.getBillNumber());
                    this.info.setCompany(resultBillInfo.getCompany());
                    this.info.setExplain(resultBillInfo.getExplain());
                    this.info.setIssue(resultBillInfo.getIssue());
                    this.info.setStatus(resultBillInfo.getStatus());
                    this.info.setNum(resultBillInfo.getNum());
                    this.info.setTime(resultBillInfo.getTime());
                    Log.e("info","info msg="+info+"info.billNum"+info.getBillNumber()+"info.getStatus"+info.getStatus()+"info.getCompany"+info.getCompany());
                    // InputResultActivity.this.handler.sendEmptyMessage(0);
                    rCB.changeFragment();
                    return;
                }
                catch (JSONException localJSONException2)
                {
                    localJSONException2.printStackTrace();
                    return;
                }
            }
            try
            {
                String str2 = new org.json.JSONObject(new org.json.JSONObject(paramString).getString("resetPW")).getString("errMsg");
                if ((!str2.isEmpty()) || ("".equals(str2)))
                {
                    toast.makeText(context, str2, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            catch (JSONException localJSONException1)
            {
                localJSONException1.printStackTrace();
                return;
            }
            toast.makeText(context, "网络异常,请稍后再试!!!",  Toast.LENGTH_SHORT).show();
            return;
        }
       toast.makeText(context, "网络异常,请稍后再试!!!",  Toast.LENGTH_SHORT).show();
    }

    protected void onPreExecute()
    {
//        InputResultActivity.this.progressDialog.show();
    }


    private String CalcMEIDCheckDigit(String strMEIDHex)
    {
        if(strMEIDHex.length()!=14 ) return "";
        Log.e("MyAsyncTask","!!!!getDeviceId 356521052619884 === "+strMEIDHex);
        int nTotal=0;
        //先累加奇数位数字
        for(int i=0;i<14;i+=2)
        {
            nTotal+=Character.getNumericValue(strMEIDHex.charAt(i));
        }

        //偶数位乘以2，然后累加乘2以后的十六进制结果的两位数字
        String strTemp;
        int nTemp;
        for(int i=1;i<14;i+=2)
        {
            nTemp=2*Character.getNumericValue(strMEIDHex.charAt(i));
            int highB = nTemp/10;
            int lowB = nTemp%10;
            nTotal+=highB;
            nTotal+=lowB;
        }
        Log.e("My","ntotal ="+nTotal);
        //获取十六进制的末位数
        nTotal=((nTotal % 10)==0) ? 0:(10-(nTotal % 10));

        return (strMEIDHex+nTotal);
    }
}
