package com.ozateck.oz_wearsyncdata;

/**
 * Created by ShimejiOzaki on 5/4/15.
 */

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Asset;

public class ImageDecoder{

	private static final String TAG = "ImageDecoder";
	private static final int TIMEOUT_MS = 3000;

	// Use in [ mobile ] project
	public static Asset decBmp2Ast(GoogleApiClient mGoogleApiClient, Bitmap bitmap){

		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
		Asset asset = Asset.createFromBytes(byteStream.toByteArray());
		return asset;
	}

	// Use in [ wear ] project
	public static Bitmap decAst2Bmp(GoogleApiClient mGoogleApiClient, Asset asset){

		ConnectionResult result = mGoogleApiClient.blockingConnect(
				TIMEOUT_MS, TimeUnit.MILLISECONDS);
		if(result.isSuccess() == false){
			Log.w(TAG, "Failed:timeout");
			return null;
		}

		InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
				mGoogleApiClient, asset).await().getInputStream();

		if (assetInputStream == null) {
			Log.w(TAG, "Requested an unknown Asset.");
			return null;
		}

		Bitmap bitmap = BitmapFactory.decodeStream(assetInputStream);
		return bitmap;

	}
}
