import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;

import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.DisplayPhoto;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ContactsPhotoCameraCallback
{
  private static Matrix sCameraRotationMatrix = new Matrix();
	private static int    sThumbnailSize = -1;
	
	static{
		sCameraRotationMatrix.postRotate(270);
	}
	
	public static Camera.PictureCallback create(Context context, String displayName, String mobileNumber)
	{
		return new ContactsPhotoCallback(context, displayName, mobileNumber);
	}
	
	public static int getThumbnailSize(Context context)
	{
		if(sThumbnailSize == -1){
			final Cursor c = context.getContentResolver().query(DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI,
				new String[ ] {	DisplayPhoto.THUMBNAIL_MAX_DIM }, null, null, null);
			try{
				c.moveToFirst();
				sThumbnailSize = c.getInt(0);
			}
			finally{
				c.close();
			}
		}
		return sThumbnailSize;
	}
	
	private static class ContactsPhotoCallback implements Camera.PictureCallback
	{
		public static final String TAG = ContactsPhotoCallback.class.getSimpleName();
		
		private Context mContext;
		private String  mDisplayName;
		private String  mMobileNumber;
		
		
		public ContactsPhotoCallback(Context context, String displayName, String mobileNumber)
		{
			mContext      = context;
			mDisplayName  = displayName;
			mMobileNumber = mobileNumber;
		}
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.build());
			
			if(mDisplayName != null){
				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					/* 0 is referencing index 0 in this ContentProviderOperation ArrayList */
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME, mDisplayName)
					.build());
			}
			
			if(mMobileNumber != null){
				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					/* 0 is referencing index 0 in this ContentProviderOperation ArrayList */
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.withValue(Phone.NUMBER, mMobileNumber)
					.build());
			}
			
			 Bitmap originalPhoto = BitmapFactory.decodeByteArray(data, 0, data.length);
			final Bitmap rotatedPhoto  = Bitmap.createBitmap(originalPhoto, 0, 0, 
				originalPhoto.getWidth(), originalPhoto.getHeight(), sCameraRotationMatrix, false);
			originalPhoto.recycle();
			data = null;
			
			final int size = getThumbnailSize(mContext);
			final Bitmap scaledPhoto = Bitmap.createScaledBitmap(rotatedPhoto, size, size, false);
			byte[] scaledPhotoData   = bitmapToPNGByteArray(scaledPhoto);
			scaledPhoto.recycle();
			
			byte[] fullSizePhotoData = bitmapToPNGByteArray(rotatedPhoto);
			rotatedPhoto.recycle();
			
			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				/* 0 is referencing index 0 in this ContentProviderOperation ArrayList */
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
				.withValue(Photo.PHOTO, scaledPhotoData)
				//.withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
				.build());
			
			final ContentProviderResult[] results;
			try{
				try{
					results = mContext.getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
				}
				finally{
					scaledPhotoData = null;
				}
			}
			catch(TransactionTooLargeException e){
				Log.e(TAG, "Unable to insert raw contact: " + e.toString(), e);
				return;
			}
			catch(RemoteException e){
				Log.e(TAG, "Unable to insert raw contact: " + e.toString(), e);
				return;
			}
			catch(OperationApplicationException e){
				Log.e(TAG, "Unable to insert raw contact: " + e.toString(), e);
				return;
			}
			
			long rawContactId = ContentUris.parseId(results[0].uri);
			
			final Uri displayPhotoUri = Uri.withAppendedPath(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId),
					RawContacts.DisplayPhoto.CONTENT_DIRECTORY);
			try{
				final FileOutputStream photoStream = mContext.getContentResolver()
					.openAssetFileDescriptor(displayPhotoUri, "rw").createOutputStream();
				try{
					int bufferSize = 16 * 1024;
					for(int offset = 0; offset < fullSizePhotoData.length; offset += bufferSize){
						bufferSize = Math.min(bufferSize, (fullSizePhotoData.length - offset));
						photoStream.write(fullSizePhotoData, offset, bufferSize);
					}
				}
				finally{
					photoStream.close();
					fullSizePhotoData = null;
				}
			}
			catch(IOException e){
				Log.e(TAG, "Failed to update full size display photo", e);
				return;
			}
		}
		
		public static byte[] bitmapToPNGByteArray(Bitmap bitmap)
		{
			final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
			final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
			try {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
				return out.toByteArray();
			}
			catch(IOException e){
				Log.w(TAG, "Unable to serialize photo: " + e.toString());
				return null;
			}
		}
	}
}