package com.stonete.qrtoken.utils;

import org.json.JSONObject;

/**
 * Created by kangwei on 2015/2/15.
 */
public class JsonUtils {

    public static IError isJsonValid(JSONObject jo, String... pars) {

        for (String par : pars) {
            par = par.trim();
            if (jo.isNull(par)) {
                IError error = new IError();
                error.errorMsg = jo + " has no param " + par;
                return error;
            }
        }
        return null;
    }


}
