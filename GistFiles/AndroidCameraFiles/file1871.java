public class MainActivity extends Activity implements CvCameraViewListener2 {

	private CameraBridgeViewBase mOpenCvCameraView;
	private static CascadeClassifier face_cascade; //계산을 위한 변수(얼굴용)
	private static CascadeClassifier eyes_cascade; //계산을 위한 변수(눈용)
	MatOfRect faces;
	Mat mRgba; //컬러처리를 위한 화면 기본 변수

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				face_cascade = loadCascade(
						R.raw.haarcascade_frontalface_alt2,
						"haarcascade_frontalface_alt2.xml"
						);
				//OpenCV가 제공하는 학습된 얼굴를 데이터 불러옴.
				eyes_cascade = loadCascade(
						R.raw.haarcascade_eye_tree_eyeglasses,
						"haarcascade_eye_tree_eyeglasses.xml"
						);
				//OpenCV가 제공하는 학습된 눈의 데이터 불러옴.

				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};



	private CascadeClassifier loadCascade(int RID, String fileName)
	{
		//파일 로드를 위한 함수
		CascadeClassifier mJavaDetector = null;

		try {
			// load cascade file from application resources
			InputStream is = getResources().openRawResource(RID);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
			File mCascadeFile = new File(cascadeDir, fileName);
			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
			if (mJavaDetector.empty()) {
				Log.e("", "Failed to load cascade classifier");
				mJavaDetector = null;
			} else
				Log.i("", 
						"Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

			cascadeDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("", "Failed to load cascade. Exception thrown: " + e);
		}

		return mJavaDetector;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		mOpenCvCameraView = 
				(CameraBridgeViewBase)findViewById(
						R.id.tutorial1_activity_java_surface_view
						);

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat();
		faces = new MatOfRect();
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Core.flip(inputFrame.rgba(), mRgba, 1);

		mRgba = detectAndDraw(mRgba,face_cascade,eyes_cascade,1,true);

		return mRgba;
	}

	public Mat detectAndDraw(    		
			Mat src,
			CascadeClassifier cascade,
			CascadeClassifier nestedCascade,
			double scale, boolean tryflip) {

		//cpp sample source

		int i = 0;
		double t = 0;

		//vector<Rect> faces, faces2;
		List<Rect> faces = new ArrayList<Rect>(); 
		MatOfRect faces2 = new MatOfRect();


		/* 
		 * const static Scalar colors[] =  { CV_RGB(0,0,255),
		 * CV_RGB(0,128,255),
		 * CV_RGB(0,255,255),
		 * CV_RGB(0,255,0),
		 * CV_RGB(255,128,0),
		 * CV_RGB(255,255,0),
		 * CV_RGB(255,0,0),
		 * CV_RGB(255,0,255)};
		 */
		ArrayList<Scalar> colors = new ArrayList<Scalar>();
		colors.add(new Scalar(00,0,255));
		colors.add(new Scalar(0,128,255));
		colors.add(new Scalar(0,255,255));
		colors.add(new Scalar(0,255,0));
		colors.add(new Scalar(255,128,0));
		colors.add(new Scalar(255,255,0));
		colors.add(new Scalar(255,0,0));
		colors.add(new Scalar(255,0,255));    	

		Mat gray = new Mat();

		//smallImg( cvRound (img.rows/scale), cvRound(img.cols/scale), CV_8UC1 );
		Mat smallImg = new Mat(
				(int)Math.round(src.rows()/scale),
				(int)Math.round(src.cols()/scale), CvType.CV_8UC1 
				);

		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.resize(gray, smallImg, smallImg.size(),0,0,Imgproc.INTER_LINEAR);
		Imgproc.equalizeHist(smallImg, smallImg);

		MatOfRect mr = new MatOfRect();
		mr.fromList(faces);

		cascade.detectMultiScale(smallImg, mr, 
				1.1, 2, 0,new Size(30, 30), new Size());

		if(tryflip){
			Core.flip(smallImg, smallImg, 1);
			cascade.detectMultiScale(smallImg, faces2,
					1.1, 2, 0,new Size(30, 30), new Size());

			Rect[] lr = faces2.toArray();

			for(Rect r : lr){
				faces.add(new Rect(
						smallImg.cols()-r.x-r.width,
						r.y,r.width,r.height)
						);
			}
		}

		for(Rect r : faces)
		{
			i++;
			MatOfRect nestedObjects = new MatOfRect();
			Point center = new Point();

			Scalar color = colors.get(i%8);
			int radius;

			double aspect_ratio = (double)r.width/r.height;

			if( 0.75 < aspect_ratio && aspect_ratio < 1.3 )
			{
				//cvRound -> (int)Math.round
				center.x = (int)Math.round((r.x + r.width*0.5)*scale);
				center.y = (int)Math.round(Math.round((r.y + r.height*0.5)*scale));
				radius = (int)Math.round((r.width + r.height)*0.25*scale);
				Core.circle( src, center, radius, color, 3, 8, 0 );
			}
			else
				Core.rectangle(src,
					new Point(
						(int)Math.round(r.x*scale), 
						 (int)Math.round(r.y*scale)
						),
					new Point(
						(int)Math.round((r.x + r.width-1)*scale),
						(int)Math.round((r.y + r.height-1)*scale)
						),
					color, 3, 8, 0);

			if( nestedCascade.empty() )
				continue;

			//smallImgROI = smallImg(*r);
			Mat smallImgROI = new Mat(src,r);
			nestedCascade.detectMultiScale(
					smallImgROI, 
					nestedObjects, 
					1.1, 2, 0,
					new Size(30, 30), new Size());


			for(Rect nr : nestedObjects.toList())
			{
				center.x = (int)Math.round((r.x + nr.x + nr.width*0.5)*scale);
				center.y = (int)Math.round((r.y + nr.y + nr.height*0.5)*scale);
				radius = (int)Math.round((nr.width + nr.height)*0.25*scale);
				Core.circle( src, center, radius, color, 3, 8, 0 );
			}
		}

		return src;
	}
}