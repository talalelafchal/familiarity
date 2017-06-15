package com.sgt_tibs.demo.listcolor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lt_tibs on 9/15/15.
 */
public class RowItem implements Parcelable{

    public String text;
    public boolean selected;

    public RowItem(){}

    public RowItem(Parcel source){
        text = source.readString();
        selected = source.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(selected ? 1 : 0);
    }

    public static Parcelable.Creator<RowItem> CREATOR = new Parcelable.Creator<RowItem>(){
        @Override
        public RowItem createFromParcel(Parcel source) {
            return new RowItem(source);
        }

        @Override
        public RowItem[] newArray(int size) {
            return new RowItem[size];
        }
    };
}
