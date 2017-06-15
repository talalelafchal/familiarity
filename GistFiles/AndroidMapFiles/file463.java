package com.ozateck.oz_wearsyncdata;

import java.util.HashSet;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.net.Uri;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;

import com.google.android.gms.wearable.Asset;

public class MainActivity extends Activity{

	private static final String TAG = "MainActivity";

	private static final String MESSAGE_PATH = "/message/path";

	private static final String COUNT_PATH = "/count/path";
	private static final String COUNT_KEY = "COUNT_KEY";

	private static final String IMAGE_PATH = "/image/path";
	private static final String IMAGE_KEY = "IMAGE_KEY";

	private int count = 0;
	private GoogleApiClient mGoogleApiClient;

	private int dwIndex = 0;
	private int dwResources[] = {
			R.drawable.sample_rupin,
			R.drawable.sample_jigen,
			R.drawable.sample_goemon,
			R.drawable.sample_totuan,
			R.drawable.sample_mizuno,
			R.drawable.sample_tanaka,
			R.drawable.sample_osaka
	};

	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		Button btnMessage = (Button)this.findViewById(R.id.btn_message);
		btnMessage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				sendMessage();
			}
		});

		Button btnCount = (Button)this.findViewById(R.id.btn_count);
		btnCount.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				sendCount();
			}
		});

		Button btnImage = (Button)this.findViewById(R.id.btn_image);
		btnImage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				sendImage();
			}
		});

		// GoogleApiClient
		mGoogleApiClient = new GoogleApiClient.Builder(this).
				addApi(Wearable.API).
				addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
					@Override
					public void onConnected(Bundle bundle){
						Log.d(TAG, "GoogleApiClient:onConnected");

						// AsyncTask
						AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>(){
							@Override
							protected Void doInBackground(Void... voids){
								restoreCurrentCount();
								return null;
							}
						};
						aTask.execute();
					}

					@Override
					public void onConnectionSuspended(int i){
						Log.d(TAG, "GoogleApiClient:onConnectionSuspended");
					}
				}).build();
		mGoogleApiClient.connect();
	}

	//==========
	// Message
	//=========
	// メッセージをWearへ送信
	private void sendMessage(){
		Log.d(TAG, "sendMessage");

		// AsyncTask
		AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... voids){

				HashSet<String> hSet = getNodes();
				for(String str : hSet){
					MessageApi.SendMessageResult sMResult =
							Wearable.MessageApi.sendMessage(mGoogleApiClient, str, MESSAGE_PATH, null).await();
					if(sMResult.getStatus().isSuccess()){
						Log.d(TAG, "SUCCESSFUL:" + sMResult.getStatus());
					}else{
						Log.e(TAG, "FAILED:" + sMResult.getStatus());
					}
				}
				return null;
			}
		};
		aTask.execute();
	}

	// Node APIを使って現在接続しているWearデバイスのノードID一覧を取得する。
	private HashSet<String> getNodes(){
		HashSet<String> hSet = new HashSet();
		NodeApi.GetConnectedNodesResult nodes =
				Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
		for(Node node : nodes.getNodes()){
			hSet.add(node.getId());
		}
		return hSet;
	}

	//==========
	// Data
	//==========
	// データをWearへ送信
	private void sendCount(){
		Log.d(TAG, "sendCount");
		count++;

		// DataItemを識別するためのキーとしてパスを指定
		PutDataMapRequest pDMRequest = PutDataMapRequest.create(COUNT_PATH);
		// PutDataMapRequestというMapとして扱えるクラスに、COUNT_KEYというキーで値をセット
		pDMRequest.getDataMap().putInt(COUNT_KEY, count);
		// 接続済みのGoogleAPIClient(この例ではmGoogleApiClient)とPutDataRequestオブジェクトを指定してデータを更新
		PutDataRequest pDRequest = pDMRequest.asPutDataRequest();
		PendingResult<DataApi.DataItemResult> pResult =
				Wearable.DataApi.putDataItem(mGoogleApiClient, pDRequest);
		pResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>(){
			@Override
			public void onResult(DataApi.DataItemResult dItemResult){
				Log.d(TAG, "Send Count:" + count);
			}
		});
	}

	// データをリストア
	private void restoreCurrentCount(){

		// AsyncTask
		AsyncTask<Void, Void, Void> aTask = new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... voids){

				String localNodeID = getLocalNodeID();
				Uri uri = new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).
						authority(localNodeID).path(COUNT_PATH).build();
				Wearable.DataApi.getDataItem(mGoogleApiClient, uri).
						setResultCallback(new ResultCallback<DataApi.DataItemResult>(){
							@Override
							public void onResult(DataApi.DataItemResult dataItemResult){
								DataItem dataItem = dataItemResult.getDataItem();
								if (dataItem != null){
									DataMap dataMap = DataMapItem.fromDataItem(dataItemResult.getDataItem()).getDataMap();
									count = dataMap.getInt(COUNT_KEY);
									Log.d(TAG, "Restore Count:" + count);
								}
							}
						});
				return null;
			}
		};
		aTask.execute();
	}

	// WearのIDを取得する
	private String getLocalNodeID(){
		NodeApi.GetLocalNodeResult localNodeResult =
				Wearable.NodeApi.getLocalNode(mGoogleApiClient).await();
		return localNodeResult.getNode().getId();
	}

	//==========
	// Image ※不安定な為、未使用
	//==========
	// イメージをWearへ送信
	private void sendImage(){
		Log.d(TAG, "sendImage");

		int dwResource = dwResources[dwIndex];
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), dwResource);
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
		Asset asset = Asset.createFromBytes(byteStream.toByteArray());

		// DataItemを識別するためのキーとしてパスを指定
		PutDataMapRequest pDMRequest = PutDataMapRequest.create(IMAGE_PATH);
		// PutDataMapRequestというMapとして扱えるクラスに、COUNT_KEYというキーで値をセット
		pDMRequest.getDataMap().putAsset(IMAGE_KEY, asset);
		// 接続済みのGoogleAPIClient(この例ではmGoogleApiClient)とPutDataRequestオブジェクトを指定してデータを更新
		PutDataRequest pDRequest = pDMRequest.asPutDataRequest();

		PendingResult<DataApi.DataItemResult> pResult =
				Wearable.DataApi.putDataItem(mGoogleApiClient, pDRequest);
		pResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>(){
			@Override
			public void onResult(DataApi.DataItemResult dItemResult){
				Log.d(TAG, "Send Image:" + dItemResult.toString());
			}
		});

		if(dwIndex < dwResources.length-1){
			dwIndex++;
		}else{
			dwIndex = 0;
		}
	}
}