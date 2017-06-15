import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;

import java.io.Serializable;

public abstract class Model implements Serializable {

    protected abstract String getClassName();

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();

        String json = gson.toJson(this);
        if (TextUtils.isEmpty(getClassName())) {
            return json;
        }
        return getClassName() + ": " + json;
    }

    private void dump() {
        Logger.i(String.valueOf(this));
    }

    public <T extends Model> T copy(Class<T> type) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(gson.toJson(this), type);
    }

}