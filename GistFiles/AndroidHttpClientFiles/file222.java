package com.stonete.qrtoken.utils;

import com.stonete.qrtoken.application.SysApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kangwei on 2015/3/5.
 */
public class RegexChecks {

    public static boolean checkEmailRegex(String email){
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkUserNameRegex(String userName){
        String reg = "^[a-zA-Z]{1}\\w{4,19}$";
        return check(reg, userName);
    }


    public static boolean checkPhoneNum(String phone) {
        String checkReg = "^1\\d{10}";
        return check(checkReg, phone);
    }

    public static boolean isAuthCode(String code){
        String checkReg = "^\\d{6}$";
        return check(checkReg, code);
    }

    public static boolean check(String checkReg, String str){
        Pattern pattern = Pattern.compile(checkReg);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void main(String args[]) throws IOException {

        String reg = "";
        String str = "";
        while (true) {
            String input = getInput();

            if(input.startsWith("reg")){
                reg = input.substring("reg".length());
                System.out.println("输入了正则表达式" + reg);
            }

            else if(input.startsWith("str")){
                str = input.substring("str".length());
                System.out.println("输入了字符个数" + str.length());
            }

            else if(input.equals("r")){
                System.out.println("reg:" + reg + "\nstr" + str);
                try{
                    System.out.println(check(reg, str));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("无效的命令");
            }
        }
    }

    public static String getInput() throws IOException {
        System.out.println("请输入：");
        //^[a-zA-Z]{1}\w{4,19}$
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

}
