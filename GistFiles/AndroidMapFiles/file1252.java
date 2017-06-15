package meta;

import java.io.Serializable;

/**
 * Description:
 * <p/>
 * Date: 14-2-1
 * Author: Administrator
 */
public class RemoteResponse implements Serializable {
    private Serializable res;
    private String error;

    public Serializable getRes() {
        return res;
    }

    public void setRes(Serializable res) {
        this.res = res;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
