package codigofuente.rehapp.Presentation.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEventListener;

import codigofuente.rehapp.Business.ChatBusiness;
import codigofuente.rehapp.Business.FeedbackVideoBusiness;
import codigofuente.rehapp.DataAccess.RehappClient;
import codigofuente.rehapp.Entities.FeedbackEntity;
import codigofuente.rehapp.Presentation.Adapters.ChatAdapter;
import codigofuente.rehapp.R;
import codigofuente.rehapp.service.ConnectivityReceiver;
import codigofuente.rehapp.service.MyApplication;
import cz.msebera.android.httpclient.Header;
import io.paperdb.Paper;

public class ChatProfessionalActivity extends ActivityBase implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int SELECT_VIDEO = 1;
    private final int DRAWABLE_RIGHT = 2;

    private EditText mEditTextComment;
    private ImageButton mImgBtnAddVideo;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerViewFeeds;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mVideoPath;
    private int mExerciseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_professional);

        initialization();

        setListener();
        // Get data from API and set it to adapter
        getFeedsFromExercise(String.valueOf(mExerciseId));
    }


    @Override
    public void initialization() {
        Paper.init(this);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        mEditTextComment = (EditText) findViewById(R.id.commentEditText);
       // mButtonSend = (Button) findViewById(R.id.btnSend);
        mImgBtnAddVideo = (ImageButton) findViewById(R.id.ibAddVideo);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarFeedback);
        mRecyclerViewFeeds = (RecyclerView) findViewById(R.id.rvFeeds);

        mExerciseId = getIntent().getIntExtra(this.getResources().getString(R.string.extra_exercise_id), 0);
    }

    @Override
    public void setListener() {
        mEditTextComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // If is right button
                    if (motionEvent.getRawX() >= (mEditTextComment.getRight() - mEditTextComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        try {
                            sendFeedback();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    // If is the EditText
                    } else {
                        mEditTextComment.requestFocus();
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.showSoftInput(mEditTextComment, 0);
                    }
                    return true;
                }
                return false;
            }
        });


      mImgBtnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideo();
            }
        });
    }

    @Override
    public void setTypeface() {

    }

    private void sendFeedback() throws FileNotFoundException {
        FeedbackVideoBusiness feedbackVideoBusiness = new FeedbackVideoBusiness(this);
        String professionalId = Paper.book().read(getResources().getString(R.string.professionalKeyId, 0));
        feedbackVideoBusiness.sendVideo(mVideoPath, mEditTextComment.getText().toString(), mExerciseId, Integer.parseInt(professionalId));
    }

    public void getVideo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                mVideoPath = getPath(data.getData());
                if (mVideoPath == null) {
                    Log.v("Brandon-lp", "Video path NULL!!");
                    mVideoPath = "No se pudo cargar el video";
                    //finish();
                } else {
                    //mPreviewVideoImage.setImageBitmap(getVideothumbnail());
                    mImgBtnAddVideo.setBackgroundColor(ContextCompat.getColor(ChatProfessionalActivity.this, R.color.colorStatus1));
                    Toast.makeText(ChatProfessionalActivity.this, R.string.toast_added_video, Toast.LENGTH_SHORT).show();
                }
                Log.v("AntonioBar", "Video path"+mVideoPath);
                //mVideoPathText.setText(mVideoPath);
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    // Get feedBacks in background and update the recyclerView
    public void getFeedsFromExercise(String exerciseId){
        RequestParams params = new RequestParams();

        String url = String.format(getResources().getString(R.string.feedsExerciseUrl) + "/%s/%s", exerciseId, getResources().getString(R.string.feedsExerciseUrlKey));

        RehappClient rehappClient = new RehappClient(ChatProfessionalActivity.this);
        rehappClient.getClient().addHeader(getResources().getString(R.string.authorizationHeaderKey), getResources().getString(R.string.bearerHeaderKey)+ Paper.book().read(getResources().getString(R.string.tokenKey), ""));
        rehappClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v("AntonioBar", "Status code ->"+statusCode);
                Log.v("AntonioBar", "Response feeds list->"+response.toString());
                // Get list of feedBacks
                ArrayList<FeedbackEntity> feedBacks = ChatBusiness.convertJsonToFeedbackList(response.toString());

                mProgressBar.setVisibility(ProgressBar.GONE);
                mAdapter = new ChatAdapter(feedBacks);
                mRecyclerViewFeeds.setAdapter(mAdapter);
                mLayoutManager = new LinearLayoutManager(ChatProfessionalActivity.this);
                mRecyclerViewFeeds.setLayoutManager(mLayoutManager);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("AntonioBar", "Status code ->"+statusCode);
                Log.e("AntonioBar", "Response ->"+errorResponse.toString());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = ContextCompat.getColor(this, R.color.colorStatus1);
        } else {
            message = "Sorry! Not connected to internet";
            color = ContextCompat.getColor(this, R.color.colorStatus0);
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_chat_professional), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}