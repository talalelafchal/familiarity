package ru.itis.androidlab.thekfuvoice.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.itis.androidlab.thekfuvoice.R;
import ru.itis.androidlab.thekfuvoice.activities.ChooserActivity;
import ru.itis.androidlab.thekfuvoice.fragments.base.BaseLoadableFragment;
import ru.itis.androidlab.thekfuvoice.models.ComplaintTag;
import ru.itis.androidlab.thekfuvoice.network.events.complaints.AddComplaintIsSuccessEvent;
import ru.itis.androidlab.thekfuvoice.utils.Constants;

public class NewComplaintFragment extends BaseLoadableFragment {

    @BindColor(R.color.md_btn_selected_dark) int mTagBackgroundColor;
    @BindColor(R.color.colorMainText) int mTagTextColor;
    @BindDrawable(R.drawable.roundrect) Drawable mRoundrect;

    @Bind(R.id.complaint_edit_text) EditText mComplaintEditText;
    @Bind(R.id.tag_linear_layout) LinearLayout mTagLinearLayout;
    @Bind(R.id.faculty_image_view) ImageView mFacultyImageView;
    @Bind(R.id.map_image_view) ImageView mMapImageView;

    @OnClick(R.id.faculty_image_view)
    public void chooserFaculty() {
        startChooserActivity(Constants.FACULTY_CHOOSER_CODE);
    }

    @OnLongClick(R.id.faculty_image_view)
    public boolean longChooserFaculty() {
        onActivityResult(Constants.FACULTY_CHOOSER_CODE, Activity.RESULT_CANCELED, null);
        return true;
    }

    @OnClick(R.id.map_image_view)
    public void chooserMap() {
        startChooserActivity(Constants.LOCATION_CHOOSER_CODE);
    }

    @OnLongClick(R.id.map_image_view)
    public boolean longChooserMap() {
        onActivityResult(Constants.LOCATION_CHOOSER_CODE, Activity.RESULT_CANCELED, null);
        return true;
    }

    @OnClick(R.id.photo_image_view)
    public void chooserPhoto() {
        Toast.makeText(getActivity(), "photo", Toast.LENGTH_SHORT).show();
    }

    @OnLongClick(R.id.photo_image_view)
    public boolean longChooserPhoto() {
        Toast.makeText(getActivity(), "long photo", Toast.LENGTH_SHORT).show();
        return true;
    }

    private LinearLayout[] mLinearLayouts;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_complaint, container, false);
        mView = inflater.inflate(R.layout.tag_text_view, container);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initLinearLayouts();
        return view;
    }

    private void initLinearLayouts(){
        mLinearLayouts = new LinearLayout[Constants.COUNT_DATA];
        for(int i = 0; i < mLinearLayouts.length; i++){
            mLinearLayouts[i] = new LinearLayout(getContext());
            mLinearLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
            mLinearLayouts[i].setPadding(5, 5, 5, 5);
        }
    }

    private void startChooserActivity(int code) {
        Intent intent = new Intent(getActivity(), ChooserActivity.class);
        intent.putExtra(Constants.CHOOSER_FLAG, code);
        startActivityForResult(intent, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int code = setImage(requestCode, resultCode);
        switch(code){
            case Constants.FACULTY_CANCELED:
                mTagLinearLayout.removeView(mLinearLayouts[requestCode]);
                break;
            case Constants.LOCATION_CANCELED:
                mTagLinearLayout.removeView(mLinearLayouts[requestCode]);
                break;
            case Constants.PHOTO_CANCELED:
                //TODO
                break;
            case Constants.FACULTY_OK:
                addTags(data.getParcelableArrayListExtra(Constants.SELECTED_FLAG), requestCode);
                break;
            case Constants.LOCATION_OK:
                addTags(data.getParcelableArrayListExtra(Constants.SELECTED_FLAG), requestCode);
                break;
            case Constants.PHOTO_OK:
                //TODO
                break;
        }
    }

    private void addTags(List<ComplaintTag> tags, int code){
        mTagLinearLayout.removeView(mLinearLayouts[code]);
        mLinearLayouts[code].removeAllViews();
        for(int i = 0; i < tags.size(); i++){
            mLinearLayouts[code].addView(getTextView(tags.get(i).getName()));
        }
        mTagLinearLayout.addView(mLinearLayouts[code]);
    }

    private TextView getTextView(String tagText){
        TextView textViews = (TextView) mView.findViewById(R.id.tag);
        textViews.setText(tagText);
        return textViews;
    }

    private int setImage(int requestCode, int resultCode){
        int x = 0;
        if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == Constants.FACULTY_CHOOSER_CODE){
                mFacultyImageView.setImageResource(R.drawable.faculty);
                x = Constants.FACULTY_CANCELED;
            } else if(requestCode == Constants.LOCATION_CHOOSER_CODE){
                mMapImageView.setImageResource(R.drawable.map);
                x = Constants.LOCATION_CANCELED;
            } else {
                mMapImageView.setImageResource(R.drawable.photo);
                x = Constants.PHOTO_CANCELED;
            }
        } else {
            if(requestCode == Constants.FACULTY_CHOOSER_CODE) {
                mFacultyImageView.setImageResource(R.drawable.faculty_added);
                x = Constants.FACULTY_OK;
            } else if(requestCode == Constants.LOCATION_CHOOSER_CODE) {
                mMapImageView.setImageResource(R.drawable.map_added);
                x = Constants.LOCATION_OK;
            } else {
                mMapImageView.setImageResource(R.drawable.photo_added);
                x = Constants.PHOTO_OK;
            }
        }
        return x;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.send:
                sendNewComplaint();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_complaint_activity_menu, menu);
    }

    private void sendNewComplaint() {
        //TODO
    }

    @Subscribe
    public void onEvent(AddComplaintIsSuccessEvent event) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
