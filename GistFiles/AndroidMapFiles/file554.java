package com.stonete.qrtoken.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.stonete.qrtoken.dialog.GetToast;

public class JudgeUsername {

    public static boolean getjudgeusername(Context context, String username) {

        if (!judgeUsernameValid(username)) {
            GetToast.showToast("用户名格式不正确", context);
            return false;
        }
        return true;
    }

    public static boolean getValidPhoneNum(Context context, String phone){
        if(!RegexChecks.checkPhoneNum(phone)){
            Toast.makeText(context, "请正确填写11位手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public static boolean judgeUsernameValid(String username) {
        return RegexChecks.checkUserNameRegex(username);
    }

    //正则表达式
    public static boolean editText(String username) {
//            ^(?!_)[a-zA-Z0-9_]*$
//            "([a-z]|[A-Z]|[0-9_]|[\\u4e00-\\u9fa5])+"
        Pattern p = Pattern.compile("^(?!_)[a-zA-Z0-9_]*$");
        Matcher m = p.matcher(username);
        if (m.matches()) {
            return true;

        }
        return false;

    }

    public static boolean getValidSmsCode(Context context, String smsCode) {
        if(TextUtils.isEmpty(smsCode)){
            Toast.makeText(context, "验证码错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean judgeEmail(Context context, String email) {
        if(!RegexChecks.checkEmailRegex(email)){
            Toast.makeText(context, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
