package jinsheng.com.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2014/12/7.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime(){
        //生成唯一标识符
        mId= UUID.randomUUID();
        mDate = new Date();

    }
    @Override
    public String toString(){
        return mTitle;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }



    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
