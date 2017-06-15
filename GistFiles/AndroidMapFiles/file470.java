package com.example.android.searchjsonhttp;

/**
 * Created by Alex on 2017/2/24.
 * Create a Custom Book List to store Book Title and Publisher
 */

public class BookList {
    //Store books title
    private String mTitle;
    //Store books publisher
    private String mPublisher;

    //Create a custom object to allow input title and publisher
    public BookList(String title,String publisher) {
        mTitle=title;
        mPublisher=publisher;
    }

    //Create a public method to allow get title
    public String getmTitle(){
        return mTitle;
    }
    //Create a punlic method to allow get title
    public String getmPublisher() {
        return mPublisher;
    }
}
