package com.example.manu.camgraf; 
 
import android.text.Spannable;
import android.text.style.URLSpan;
 
 
public class StringUtil { 
    /** 
     * S
     *
     */ 
    public static void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);
 
        for(URLSpan span:spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        } 
    } 
} 