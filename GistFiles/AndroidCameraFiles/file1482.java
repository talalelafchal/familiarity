PictureCallback jpegCallback=new PictureCallback(){
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		
		if(data !=null){
		  /* I use Galaxy S3 and Supported PictureSize was width = 3264px, height = 2448px, prop = 1.3333334
		   Because inSampleSize should be integer, I set maxSize a fraction of srcSize(Supported Picturesize)
		  */
			int maxSize = 816;
			mCamera.stopPreview();
			String fileString=Environment.getExternalStorageDirectory().getPath()+"/tmp.jpg";
		    BitmapFactory.Options opt=new BitmapFactory.Options();
		    
		    /*before making an actual bitmap, check size
		    if the bitmap's size is too large,out of memory occurs. 
		    */
		    opt.inJustDecodeBounds=true;
		    BitmapFactory.decodeByteArray(data, 0, data.length,opt);
		    int srcSize=Math.max(opt.outWidth, opt.outHeight);
		    System.out.println("out w:"+opt.outWidth+" h:"+opt.outHeight);
		    
		    opt.inSampleSize=maxSize <srcSize ? (srcSize/maxSize):1;
		    
		    System.out.println("sample size "+opt.inSampleSize);

		    opt.inJustDecodeBounds=false;

	
		    Bitmap tmp=BitmapFactory.decodeByteArray(data, 0, data.length,opt);
		    
		    //Scaling and rotation
		    float scale=Math.max((float)maxSize/opt.outWidth,(float)maxSize/opt.outHeight);
		    Matrix matrix=new Matrix();
		    System.out.println("sample out w:"+opt.outWidth+" h:"+opt.outHeight);
		    int size =Math.min(opt.outWidth,opt.outHeight);
		    matrix.setRotate(90);
		    matrix.postScale(scale, scale);
		    /*
		    adjusting bitmap size to fit with camera preview size
		    
		    */
		     float previewRate=(float)previewWidth/(float)previewHeight;
		    float cameraRate=(float)opt.outHeight/(float)opt.outWidth;
		    System.out.println("preview Rate" +previewRate+" camera Rate "+cameraRate);
		    if(cameraRate>previewRate){
		    	adj=(int)(size*(cameraRate-previewRate)*0.5);
		    	System.out.println("rate adjust");
		    }else{
		    	adj=(int)(size*(previewRate-cameraRate)*0.5);

		    }
		    Bitmap source=Bitmap.createBitmap(tmp, adj+(opt.outWidth-size)/2, adj+(opt.outHeight-size)/2,size-adj*2,size-adj*2, matrix, true);

      // Use getContentResolcer to let a device recognize a picture taken
			String uriString=MediaStore.Images.Media.insertImage(getContext().getContentResolver(), source, null, null);
			uri = Uri.parse(uriString);
	        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
	        c.moveToFirst();
			try {

				OutputStream outputStream=new FileOutputStream(fileString);
				source.compress(CompressFormat.JPEG, 90, outputStream);
				outputStream.close();
			} catch (FileNotFoundException fe) {
				fe.printStackTrace();

			}catch(IOException ie){
				ie.printStackTrace();
			}
			
			//if you want to start camera preview, start from here
			//mCamera.startPreview();
			System.out.println("onsaved");
			if(isPictureSaved==false){
			isPictureSaved=true;
			}
			
			//if you are moving to another activity, move from here
			System.out.println("uri: "+uri);
			Intent intent= new Intent(mContext,CaptionActivity.class);
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			mContext.startActivity(intent);
			
			
		}else{
			System.out.println("no data");
			
		}

		
	}
    };