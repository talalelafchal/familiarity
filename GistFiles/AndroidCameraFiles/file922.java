package com.bgood.danny.hockeyliguevirtuelle;

/**
 * Created by Danny on 2014-10-15.
 */
public class FormatException {
    public static String FormatExceptionMessage(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Message:");
        sb.append(e.toString());
        sb.append("\n");
        sb.append("Stack:");
        sb.append("\n");

        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            sb.append(stack[i].getClassName());
            sb.append(",");
            sb.append(stack[i].getMethodName());
            sb.append(",");
            sb.append(stack[i].getLineNumber());
            sb.append("\n");
        }

        return sb.toString();
    }
}
