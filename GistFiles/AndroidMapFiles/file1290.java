package base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import my.Event;
import my.EventHandler;
import my.ModelBinder;
import utils.Handler;
import utils.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public class BaseActivity extends Activity implements EventHandler {
    private static boolean init = false;
    private ProgressDialog pd;
    private Map<String, ModelBinder> modelBinders = new HashMap<String, ModelBinder>();

    protected android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            handle(msg.what, msg.obj);
            if (pd != null) {
                pd.dismiss();
            }
        }
    };

    protected void handle(int what, Object ojb) {
    }

    protected void call(final int msgKey, final Handler handler) {
        call(msgKey, false, handler);
    }

    protected void call(final int msgKey, boolean showBusy, final Handler handler) {
        if (showBusy) {
            pd = ProgressDialog.show(this, "Load", "Loading…");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object res = handler.call();
                mHandler.obtainMessage(msgKey, res).sendToTarget();
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!init) {
            loadConfig();
            init = true;
        }
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        View rootView = findViewById(layoutResID);
        initViews(rootView);
    }

    private void initViews(View view) {
        if (view instanceof Event) {
            ((Event) view).addEventHandler(this);
        }
        if (view instanceof ModelBinder) {
            ModelBinder modelBinder = (ModelBinder) view;
            modelBinders.put(modelBinder.getField(), modelBinder);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                initViews(viewGroup.getChildAt(i));
            }
        }
    }

    protected void model2View(Object model) {
        Set<String> fields = modelBinders.keySet();
        for (String field : fields) {
            model2View(model, field);
        }
    }

    protected void model2View(Object model, String field) {
        ModelBinder modelBinder = modelBinders.get(field);
        Object value = ObjectUtils.getFieldValue(model, field);
        modelBinder.setValue(value);
    }

    protected void view2Model(Object model) {
        Set<String> fields = modelBinders.keySet();
        for (String field : fields) {
            view2Model(model, field);
        }
    }

    protected void view2Model(Object model, String field) {
        ModelBinder modelBinder = modelBinders.get(field);
        ObjectUtils.setFieldValue(model, field, modelBinder.getValue());
    }

    private void loadConfig() {
        InputStream is;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/config.properties");
            Properties properties = new Properties();
            properties.load(is);
            System.getProperties().putAll(properties);
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "error", e);
        }
    }

    @Override
    public void fire(int id, View view) {
        mHandler.obtainMessage(id, view).sendToTarget();
    }
}
