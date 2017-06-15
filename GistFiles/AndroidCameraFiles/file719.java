package com.ctrlsmart.dataWrap;

/**
 * Created by Administrator on 2015/3/31.
 */
public class UCRand
{
    private long v_uiPerVal = 0L;

    public long Rand()
    {
        this.v_uiPerVal = (0xFFFFFFF & 2531011L + 214013L * this.v_uiPerVal);
//    Log.e("v_uiPerVal", Long.toString(this.v_uiPerVal));
        return this.v_uiPerVal;
    }

    public void SetSRand(long paramLong)
    {
        this.v_uiPerVal = paramLong;
        Long.toString(v_uiPerVal);
    }
}

