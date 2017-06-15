package edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.utils;

/**
 * Created by uddhav on 2/14/17.
 */

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import edu.ualr.cpsc7398.updatechecker.controller.service.utils.DataBean;
import edu.ualr.cpsc7398.updatechecker.controller.service.utils.HttpRequestRunnable;
import edu.ualr.cpsc7398.updatechecker.view.activity.MainActivity;

public class EditTextLocker {

    private EditText editText;
    private int charactersLimit;
    private int fractionLimit;
    private MainActivity mainActivity = new MainActivity();

    private OnKeyListener editTextOnKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_DEL) {
                startStopEditing(false);
            }

            return false;
        }
    };

    private TextWatcher editTextWatcherForCharacterLimits = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!editText.getText().toString().equalsIgnoreCase("")) {

                int editTextLength = editText.getText().toString().trim().length();

                if (editTextLength >= charactersLimit) {

                    startStopEditing(true);

                }

            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher editTextWatcherForFractionLimit = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!editText.getText().toString().equalsIgnoreCase("")) {

                String editTextString = editText.getText().toString().trim();
                int decimalIndexOf = editTextString.indexOf(".");

                if (decimalIndexOf >= 0) {

                    if (editTextString.substring(decimalIndexOf).length() > fractionLimit) {

                        startStopEditing(true);

                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public EditTextLocker(EditText editText) {

        this.editText = editText;
        editText.setOnKeyListener(editTextOnKeyListener);
    }

    public void limitCharacters(final int limit) {

        this.charactersLimit = limit;
        editText.addTextChangedListener(editTextWatcherForCharacterLimits);
    }

    public void limitFractionDigitsinDecimal(int fractionLimit) {

        this.fractionLimit = fractionLimit;
        editText.addTextChangedListener(editTextWatcherForFractionLimit);
    }

    public void unlockEditText() {

        startStopEditing(false);
    }

    public void startStopEditing(boolean isLock) {

        if (isLock) {

            editText.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
                }
            }});

        } else {

            editText.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return null;
                }
            }});
        }
    }

    public void range1to600NoLeading01(int i) {
        this.charactersLimit = i; //I already got the editText from the constructor
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!editText.getText().toString().equalsIgnoreCase("")) {

                    int editTextLength = editText.getText().toString().trim().length();

                    if (editTextLength >= charactersLimit) {

                        startStopEditing(true);

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().equalsIgnoreCase("")) { //if not null

                    int length = editText.length();
                    if (length == 1 && editText.getText().toString().equals("0"))
                        editText.setText("");

                    DataBean.setTotalUpdates(Integer.parseInt(editText.getText().toString()));

                }

            }
        });


    }

    public void range1to600NoLeading02(int i) {
        this.charactersLimit = i; //I already got the editText from the constructor
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!editText.getText().toString().equalsIgnoreCase("")) {

                    int editTextLength = editText.getText().toString().trim().length();

                    if (editTextLength >= charactersLimit) {

                        startStopEditing(true);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().equalsIgnoreCase("")) { //if not null

                    int length = editText.length();
                    if (length == 1 && editText.getText().toString().equals("0"))
                        editText.setText("");
                    DataBean.setSeed(Integer.parseInt(editText.getText().toString()));

                }

            }
        });


    }

    public void updateBeanUrl(final Context context) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(editText.getText().equals(null) || editText.getText().equals(""))) { //if not null
                    //check if the URL resource is not dynamic. If last-modifed is more than 10 minutes, treat that as static

                    String urlChanging = editText.getText().toString();

                    String url = null;
                    if (urlChanging.contains(" ")) {
                        url = urlChanging.substring(0, urlChanging.indexOf(" "));
                        if (!url.equals(null)) {
                            Log.i("urlchecked: ", url);
                            if (HttpRequestRunnable.isUrlResourceStatic(url)) {
                                //Toast should be in UI thread
                                /*
Why use Runnable over Thread?

Runnable separates code that needs to run asynchronously, from how the code is run. This keeps your code flexible. For instance, asynchronous code in a runnable can run on a threadpool, or a dedicated thread.

A Thread has state your runnable probably doesn't need access to. Having access to more state than necessary is poor design.

Threads occupy a lot of memory. Creating a new thread for every small actions takes processing time to allocate and deallocate this memory.
What is runOnUiThread actually doing?

Android's runOnUiThread queues a Runnable to execute on the UI thread. This is important because you should never update UI from multiple threads.  runOnUiThread uses a Handler.

Be aware if the UI thread's queue is full, or the items needing execution are lengthy, it may be some time before your queued Runnable actually runs.
What is a Handler?

A handler allows you to post runnables to execute on a specific thread. Behind the scenes, runOnUi Thread queues your Runnable up with Android's Ui Handler so your runnable can execute safely on the UI thread.
                                 */
                                Log.i("Inside toast", "inside toast");
                                DataBean.setUrl(null);
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(context, "This is the static resource, plz. use dynamic resource!", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                            } else {
                                DataBean.setUrl(editText.getText().toString());

                            }
                        }
                    }

                }
            }
        });
    }
}
