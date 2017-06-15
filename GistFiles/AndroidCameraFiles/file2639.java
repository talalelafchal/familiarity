package com.ibookey.book.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibookey.book.R;
import com.ibookey.book.activity.ShowMaterialActivity;

import com.ibookey.book.eventBusBean.BaseBusBean;
import com.ibookey.book.eventBusBean.FuckBB;
import com.ibookey.book.helpers.LoginHep;
import com.ibookey.book.utils.BusUtil;
import com.ibookey.book.utils.LU;
//import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreationFrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreationFrg extends BaseFragment {
	public static final String SELECTED_PICTURE_PATH_LIST = "selectedPicturePathList";
	final static int TO_CHOOSE_PICTURE = 2365;
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	ArrayList<String> selectedPicturePathList;
	@Bind(R.id.llReturn)
	LinearLayout llReturn;
	@Bind(R.id.tvTitle)
	TextView tvTitle;
	@Bind(R.id.ivRightAbove)
	ImageView ivRightAbove;
	@Bind(R.id.tvRightAbove)
	TextView tvRightAbove;
	@Bind(R.id.ivAddImage)
	ImageView ivAddImage;
	@Bind(R.id.rlMainBoard)
	RelativeLayout rlMainBoard;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	public CreationFrg() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment CreationFrg.
	 */
	// TODO: Rename and change types and number of parameters
	public static CreationFrg newInstance(String param1, String param2) {
		CreationFrg fragment = new CreationFrg();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
		selectedPicturePathList = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_create, container, false);
		ButterKnife.bind(this, view);
		initViews();
		return view;
	}

	private void initViews() {
		ivRightAbove.setVisibility(View.GONE);
		tvRightAbove.setVisibility(View.GONE);
		llReturn.setVisibility(View.GONE);
		tvTitle.setText(R.string.create);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			rlMainBoard.setBackgroundDrawable(getResources().getDrawable(R.drawable.create_bg));
		} else {
			rlMainBoard.setBackground(getResources().getDrawable(R.drawable.create_bg));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void choosePicture() {
//		MultiImageSelector.create(this.getContext()).count(50).showCamera(true).multi().origin(selectedPicturePathList).start(this, TO_CHOOSE_PICTURE);
		traditionalSel();
//		BusUtil.post(new FuckBB());
	}

	private void traditionalSel() {

		Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
		// whether show camera
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
		// max select image amount
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 50);
		// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
		// default select images (support array list)
		intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectedPicturePathList);
		startActivityForResult(intent, TO_CHOOSE_PICTURE);
	}

	@DebugLog
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		LU.d("fuck out--requestCode--" + requestCode + "--resultCode--" + resultCode + "--RESULT_OK--" + FragmentActivity.RESULT_OK);
		if (requestCode == TO_CHOOSE_PICTURE && data != null) {
			LU.d("fuck in--requestCode--" + requestCode + "--resultCode--" + resultCode);
			// Get the result list of select image paths
			selectedPicturePathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			Intent toMaterialShowIntent = new Intent(getActivity(), ShowMaterialActivity.class);
			toMaterialShowIntent.putStringArrayListExtra(SELECTED_PICTURE_PATH_LIST, selectedPicturePathList);
			Log.d("addPhoto_first", selectedPicturePathList.size() + "");
			startActivity(toMaterialShowIntent);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick(R.id.ivAddImage)
	public void onClick() {
		if (LoginHep.interceptLogin())
			return;
		choosePicture();
	}
}
