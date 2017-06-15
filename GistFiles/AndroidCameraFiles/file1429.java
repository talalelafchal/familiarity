package com.socket9.fleet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.socket9.fleet.API.FleetApiService;
import com.socket9.fleet.Manager.DatabaseSyncManager;
import com.socket9.fleet.Manager.SharedPrefHelper;
import com.socket9.fleet.Models.ActivityDetail;
import com.socket9.fleet.Models.ActivityModel;
import com.socket9.fleet.Models.ResultMessage;
import com.socket9.fleet.Utils.Camera;
import com.socket9.fleet.Utils.MyCallback;
import com.socket9.fleet.Utils.RespondListener;
import com.socket9.fleet.Utils.Singleton;
import com.socket9.fleet.ViewHolders.CustomFormRecyclerAdapter;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Mast3ro on 8/31/2015.
 */
public class CustomFormActivity extends AppCompatActivity {
    private static final String TAG = "CUSTOM_FORM_ACTIVITY";
    @Bind(R.id.my_toolbar)
    Toolbar myToolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    TextView progressBar;
    @Bind(R.id.btnClearForm)
    Button btnClearForm;
    @Bind(R.id.layoutBtn)
    LinearLayout layoutBtn;
    @Bind(R.id.layoutProgress)
    LinearLayout layoutProgress;
    private ActionBar mActionBar;
    private String activityId;
    private int activityIndex;
    private CustomFormRecyclerAdapter.CustomFormViewHolderListener cardViewListener;
    private CustomFormRecyclerAdapter myAdapter;
    private List<ActivityDetail> localActivityList;
    private MaterialDialog builder;
    private ArrayList<String> optionShowList = new ArrayList<>();
    Camera camera;
    private int cameraIndex;
    public String pristineData = "";
    ImageView ivPhotoCustom;
    private ImageView targetImageView;
    private boolean isManualChange = false;
    String cacthStringListsOption = "";
    int selectedSingleChoice = -1;
    Integer[] selectedMultipleChoice = {};
    Boolean isFromAddTemplate = false;

    public void setPristineData(String s) {
        this.pristineData = s;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        ButterKnife.bind(this);

        activityId = getIntent().getStringExtra("activityId");
        activityIndex = getIntent().getIntExtra("activityIndex", 0);

        isFromAddTemplate = getIntent().getBooleanExtra("fromAddTemplate", false);
        Log.d("CustomForm","is from template:"+isFromAddTemplate);
        camera = new Camera(this);
        ActivityModel at_current;

        /*if(Singleton.getInstance().isFromToday()){
            at_current = SharedPrefHelper.getCurrentActivity();
            if(at_current.isFinish==1){
                Singleton.getInstance().setActivityMode(Singleton.ACTIVITY_READ);
            }
        }*/
        if(isFromAddTemplate){
            Singleton.getInstance().setActivityMode(Singleton.ACTIVITY_READ);
        }
        /*Log.d("CustomForm","ac_id:"+at_current.id);
        Log.d("CustomForm extra","ac_id:"+activityId);*/
        //Log.d("customForm","isFinish:"+at_current.isFinish);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        setToolbar("" + getIntent().getStringExtra("activityName"));

        if (Singleton.getInstance().getActivityMode() == Singleton.ACTIVITY_WRITE) {

            cardViewListener = new CustomFormRecyclerAdapter.CustomFormViewHolderListener() {
                @Override
                public void onViewClicked(ActivityDetail optionsList) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myToolbar.getWindowToken(), 0);
                }

                @Override
                public void onBtnClicked(final ActivityDetail optionsList,int index) {

                    if (optionsList.valueType.name.equals("Single Choice")) {
                        optionsList.setMaterialDialog(getSingleChoiceDialog(optionsList, optionsList.getMaterialDialog().getSelectedIndex(),index));
                        selectedSingleChoice = optionsList.getMaterialDialog().getSelectedIndex();
                    }else{
                        selectedMultipleChoice = optionsList.getMaterialDialog().getSelectedIndices();
                    }
                    //Singleton.toast(CustomFormActivity.this, "index:"+index, Toast.LENGTH_SHORT);

                    optionsList.getMaterialDialog().show();
                }

                @Override
                public void onPhotoClicked(ImageView imageView, int index) {
                    camera.startCamera();
                    cameraIndex = index;
                    targetImageView = imageView;
//                    Timber.d(TAG, "onBtnClicked " + cameraIndex);
                }

                @Override
                public void onTextChange(EditText etCustomForm, int index, Editable editable) {
                    if (isManualChange) {
                        isManualChange = false;
                        return;
                    }
                    try {
                        localActivityList.get(index).value = (editable.toString().replace(",", ""));
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String yourFormattedString = "";
                        if(!editable.toString().equals(""))
                            yourFormattedString = formatter.format(Double.parseDouble(editable.toString().replace(",", "")));
                        isManualChange = true;
                        etCustomForm.setText(yourFormattedString);
                        etCustomForm.setSelection(yourFormattedString.length());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };

        } else {
            //Log.d("custom form:","activity read");
            layoutBtn.setVisibility(View.GONE);
        }
        getActivityModel();
    }

    @Override
    protected void onResume() {
        Log.d("CustomformActivity","Resume from some sleep");

       //getActivityModel();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Camera.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Timber.d("onActivityResult " + cameraIndex);
            ivPhotoCustom = targetImageView;
            layoutProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Boolean isOnline = Singleton.getInstance().getSharedPrefs().getBoolean(Singleton.SHARE_PREF_KEY_Is_Online,Boolean.TRUE);
            //if Online
            if(isOnline){
                Log.d("CustomFormActivity","upload photo online");
                FleetApiService.getFleetApiEndpointInterface().uploadPhotoBase64(Singleton.getInstance().getAppToken(),Singleton.getInstance().getAppConStr(),
                        "data:image/jpg;base64," + camera.getBase64Img(camera.getPhotoPath()), new MyCallback<ResultMessage>() {

                            @Override
                            public void success(ResultMessage resultMessage, Response response) {
                                try {
                                    if (resultMessage.result) {
                                        layoutProgress.setVisibility(View.GONE);
                                        ivPhotoCustom.setImageBitmap(camera.getBitmapImg(camera.getPhotoPath()));
                                        //ivPhotoCustom.setImageBitmap(camera.getBitmapImg());
                                        ivPhotoCustom.setVisibility(View.VISIBLE);
//                                      Log.d("CustomFormActivity","on uploadbase64 child count:"+recyclerView.getChildCount());

                                        localActivityList.get(cameraIndex).value = (resultMessage.data.pathUse);
                                    } else {
                                        Singleton.toast(CustomFormActivity.this, getString(R.string.alert_please_try_again), Toast.LENGTH_SHORT);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void good(ResultMessage model) {
//                            Timber.d(CustomFormActivity.class.getSimpleName(), "use: " + model.data.pathUse);
//                            Timber.d(CustomFormActivity.class.getSimpleName(), "pathSave: " + model.data.pathSave);
//                            ivPhotoCustom.setImageBitmap(camera.getBitmapImg(camera.getPhotoPath()));
//                            ivPhotoCustom.setVisibility(View.VISIBLE);
//                            localActivityList.get(cameraIndex).value = (model.data.pathUse);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                try {
//                                Singleton.toast(CustomFormActivity.this, FleetApiService.API_TOAST_FAILURE_MESSAGE, Toast.LENGTH_SHORT);
                                    layoutProgress.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
            }else{
                Timber.d("set photo when offline, camera index:"+cameraIndex+", path:"+camera.getPhotoPath());
                localActivityList.get(cameraIndex).value = camera.getPhotoPath();
                layoutProgress.setVisibility(View.GONE);
                ivPhotoCustom.setImageBitmap(camera.getBitmapImg(camera.getPhotoPath()));
                ivPhotoCustom.setVisibility(View.VISIBLE);
            }


        } else if (resultCode == RESULT_CANCELED) {
            camera.deletePhotoFile();
        }
    }

    private void getActivityModel(){
        if(Singleton.getInstance().isFromToday())
            if(SharedPrefHelper.IS_TEMPLATE)
                getActivity(SharedPrefHelper.getSharedTemplateList(activityIndex));
            else
                getActivity(SharedPrefHelper.getSharedActivityList());
        else{
            layoutProgress.setVisibility(View.VISIBLE);
            DatabaseSyncManager.getActivity(activityId, new RespondListener() {
                @Override
                public void onActivityComplete(ActivityModel model) {
                    layoutProgress.setVisibility(View.GONE);
                    getActivity(model.data);
                }
            });
        }
    }

    public void getActivity(List<ActivityDetail> activityList) {
        try {
            localActivityList = activityList;
            for (ActivityDetail row : activityList) {
                boolean isSingleChoice = row.valueType.name.equals("Single Choice");
                boolean isMultipleChoice = row.valueType.name.equals("Multiple Choice");

                if (isSingleChoice) {
                    row.setMaterialDialog(getSingleChoiceDialog(row, -1,activityList.indexOf(row)));
                    if (row.getMaterialDialog().getSelectedIndex() != -1)
                        row.value = (row.lists.get(row.getMaterialDialog().getSelectedIndex()).title);
                } else if (isMultipleChoice) {
                    row.setMaterialDialog(getMultipleChoiceDialog(row, activityList.indexOf(row)));
                    String list = "";
                    for (Integer i : row.getMaterialDialog().getSelectedIndices()) {
                        list += row.lists.get(i).title + " ";
                    }
                    //Log.d(TAG,"list from multiple choice:"+list);
                    row.value = (list);
                }
            }

            if (myAdapter == null) {
                myAdapter = new CustomFormRecyclerAdapter(activityList);
                myAdapter.setCustomFormViewHolderListener(cardViewListener);
            }else {
                myAdapter.setItems(activityList);
                myAdapter.notifyDataSetChanged();
            }
            recyclerView.setAdapter(myAdapter);
            pristineData = getTextValue();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutProgress.getVisibility() == View.VISIBLE) {
            Singleton.toast(this, getString(R.string.alert_wait_sync_data_from_server), Toast.LENGTH_SHORT);
            return;
        }
        checkDirty();
    }

    public MaterialDialog getMultipleChoiceDialog(final ActivityDetail optionsList, final int index) {
        optionShowList = new ArrayList<>();
        for (ActivityDetail.ListEntity option : optionsList.lists) {
            optionShowList.add(option.title);
        }

        ArrayList<Integer> integers = new ArrayList<>();
        if (optionsList.lists != null) {
            for (ActivityDetail.ListEntity row : optionsList.lists) {
                if (row.isCheck == 1) {
                    integers.add(optionsList.lists.indexOf(row));
                }
            }
        }
        Log.d("CustomFormActivity","on multiplechoice child count:"+recyclerView.getChildCount());

        builder = new MaterialDialog.Builder(CustomFormActivity.this)
                .title(optionsList.title)
                .titleColor(getResources().getColor(R.color.primary))
                .items(optionShowList.toArray(new CharSequence[optionShowList.size()]))
                .itemsCallbackMultiChoice(integers.toArray(new Integer[integers.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        String selectedOptions = "";
                        for (CharSequence choice : charSequences) {
                            selectedOptions += choice + " ";
                        }
//                        catchStringListsOption = selectedOptions;
//
//                        TextView tvMultipleChoice = ((TextView) recyclerView.getChildAt(index).findViewById(R.id.tvCustomForm));
//                        tvMultipleChoice.setText(selectedOptions);

                        optionsList.value = (selectedOptions);
                        optionsList.setMaterialDialog(materialDialog);

                        myAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .positiveText(getString(R.string.label_save))
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeText(getString(R.string.label_cancel))
                .negativeColor(getResources().getColor(R.color.colorTextSubtitle))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        //Singleton.toast(CustomFormActivity.this,"on click save",Toast.LENGTH_LONG);

//                        optionsList.value = (catchStringListsOption);
//                        //optionsList.setMaterialDialog(dialog);
//                        //myAdapter.notifyDataSetChanged();
//                        Log.d("CustomFormActivity", "index of Selected List in Custom Form:" + index);
//                        Log.d("CustomFormActivity", "on multiplechoice child count:" + recyclerView.getChildCount());
//                        //myAdapter.notifyDataSetChanged();
//                        try {
//                            final TextView tvMultipleChoice = ((TextView) recyclerView.getChildAt(index).findViewById(R.id.tvCustomForm));
//                            tvMultipleChoice.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //Log.d(TAG,"set ui");
//                                    tvMultipleChoice.setText(cacthStringListsOption);
//                                }
//                            });
//                        }catch(Exception e){
//                            Timber.d("ERROR" + e);
//                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.setSelectedIndices(selectedMultipleChoice);
                        //optionsList.value = ("");
                        //Singleton.toast(CustomFormActivity.this,"on click cancel",Toast.LENGTH_LONG);
                    }
                })
                .build();

        return builder;
    }

    public MaterialDialog getSingleChoiceDialog(final ActivityDetail optionsList, int preSelected, final int index) {
        optionShowList = new ArrayList<>();

        for (ActivityDetail.ListEntity option : optionsList.lists) {
            optionShowList.add(option.title);
        }

        Log.d("CustomFormActivity","on singlechoice child count:"+recyclerView.getChildCount());

        builder = new MaterialDialog.Builder(CustomFormActivity.this)
                .title(optionsList.title)
                .titleColor(getResources().getColor(R.color.primary))
                .items(optionShowList.toArray(new CharSequence[optionShowList.size()]))
                .itemsCallbackSingleChoice(preSelected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
//                        TextView tvMultipleChoice = ((TextView) recyclerView.getChildAt(index).findViewById(R.id.tvCustomForm));
//                        tvMultipleChoice.setText(charSequence);
                        try {
                            cacthStringListsOption = charSequence.toString();
                            //optionsList.value = (charSequence.toString());
                            //optionsList.setMaterialDialog(materialDialog);
                            //myAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .positiveText(getString(R.string.label_save))
                .positiveColor(getResources().getColor(R.color.primary))
                .negativeText(getString(R.string.label_cancel))
                .negativeColor(getResources().getColor(R.color.colorTextSubtitle))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        //Singleton.toast(CustomFormActivity.this,"on click save",Toast.LENGTH_LONG);

                        optionsList.value = (cacthStringListsOption);
                        optionsList.setMaterialDialog(dialog);
                        Log.d("CustomFormActivity","index of child:"+index);
                        //myAdapter.notifyDataSetChanged();
                        final TextView tvMultipleChoice = ((TextView) recyclerView.getChildAt(index).findViewById(R.id.tvCustomForm));
                        tvMultipleChoice.post(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d(TAG,"set ui");
                                tvMultipleChoice.setText(cacthStringListsOption);
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                       //* dialog.setSelectedIndex(-1);
                        //optionsList.value = ("");
                        //Singleton.toast(CustomFormActivity.this,"on click cancel",Toast.LENGTH_LONG);
                        dialog.setSelectedIndex(selectedSingleChoice);
                    }
                })
                .build();

        if (optionsList.lists != null && preSelected == -1) {
            for (ActivityDetail.ListEntity row : optionsList.lists) {
                if (row.isCheck == 1) {
                    builder.setSelectedIndex(optionsList.lists.indexOf(row));
                }
            }
        }
        return builder;
    }

    public void setToolbar(@NonNull String title) {
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(" " + title);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setLogo(ContextCompat.getDrawable(this, R.mipmap.action_store));
    }

    public void saveCustomForm(String jsonArray) {
        //get current time and date
        String timeStamp = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        String dateStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());


        DatabaseSyncManager.saveCustomForm(activityId, SharedPrefHelper.getCurrentClientActivity().id + "", jsonArray, timeStamp, dateStamp, SharedPrefHelper.CURRENT_CLIENT_ACTIVITY_INDEX, SharedPrefHelper.CURRENT_ACTIVITY_INDEX,
                new RespondListener() {
                    @Override
                    public void onActivityComplete(ActivityModel model) {
                        Singleton.globalAppInterface.onCustomFormSyncedCompleted();
                    }
                });
    }

    private String getTextValue() {
        String temp = "";
        try{
            for (ActivityDetail data : localActivityList) {
                temp += data.value;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return temp;
    }

    public boolean checkDirty() {
        if (getTextValue().equals(pristineData)) {
            finish();
        } else {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(getString(R.string.label_leave_page))
                    .titleColor(getResources().getColor(R.color.colorPrimary))
                    .content(getString(R.string.label_leave_without_save))
                    .icon(ContextCompat.getDrawable(CustomFormActivity.this, R.mipmap.ic_warning))
                    .positiveText(getString(R.string.label_stay))
                    .positiveColor(getResources().getColor(R.color.colorPrimary))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            finish();
                        }
                    })
                    .negativeText(getString(R.string.label_leave))
                    .negativeColor(getResources().getColor(R.color.colorTextSubtitle))
                    .build();
            dialog.show();
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                checkDirty();
                break;
            case R.id.action_done:
                // Open Dialog
                if(isValidated()){
                    SharedPrefHelper.updateCustomForm(localActivityList);
                    saveCustomForm(new Gson().toJson(SharedPrefHelper.getCurrentActivity()));
                    finish();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myToolbar.getWindowToken(), 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidated(){
        boolean isFailed = false;
        for (ActivityDetail data : localActivityList) {
            String type = data.valueType.name;
            if (type.equals("Single Choice") || type.equals("Multiple Choice")) {
                if (type.equals("Single Choice") && data.getMaterialDialog().getSelectedIndex() == -1) {
                    Timber.d("Single Choice Toast");
                    Singleton.toast(CustomFormActivity.this, getString(R.string.alert_please_enter_all_fleids), Toast.LENGTH_SHORT);
                    isFailed = true;
                } else if (type.equals("Multiple Choice") && data.getMaterialDialog().getSelectedIndices().length == 0) {
                    Timber.d("Multiple Choice Toast");
                    Singleton.toast(CustomFormActivity.this, getString(R.string.alert_please_enter_all_fleids), Toast.LENGTH_SHORT);
                    isFailed = true;
                }
            } else {
                if ((data.value == null || data.value.equals(""))) {
                    Singleton.toast(CustomFormActivity.this, getString(R.string.alert_please_enter_all_fleids), Toast.LENGTH_SHORT);
                    isFailed = true;
                }
                Timber.d("localActivityList value "+ data.value);
            }
        }
        if(isFailed)
            return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_form, menu);

        if (Singleton.getInstance().getActivityMode() == Singleton.ACTIVITY_READ)
            menu.findItem(R.id.action_done).setVisible(false);
        else if (SharedPrefHelper.IS_TEMPLATE)
            menu.findItem(R.id.action_done).setVisible(false);
        return true;
    }
}
