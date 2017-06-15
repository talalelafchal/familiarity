package service.meta;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class TestMeta implements Serializable {
    private String msg;
    private int i;
    private boolean tf;
    private Date now;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public boolean isTf() {
        return tf;
    }

    public void setTf(boolean tf) {
        this.tf = tf;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }
}
