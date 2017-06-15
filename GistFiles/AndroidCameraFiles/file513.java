public class RequestPermissionsActivity extends BaseActivity {


    private Button sendBroadcast;

    private static final int REQUEST_FOR_SDCARD = 110;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        sendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_FOR_SDCARD);

            }
        });
    }

    private void initView() {
        sendBroadcast = (Button) findViewById(R.id.send_broadcast);
    }

    private void doSomeThing() {
        Toast.makeText(this, "可以干事情了", Toast.LENGTH_SHORT).show();
        Log.d("RequestPermissionsActivity","正在做事情");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FOR_SDCARD){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("RequestPermissionsActivity","授权成功");
                doSomeThing();
            }else {
                Log.d("RequestPermissionsActivity","授权失败");
                //4.授权失败判断是否需要进行提示，如果不需要 即 false ，则说明用户勾选上了 不在询问 则需要跳转到设置页面
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)){
                    //用户直接拒绝了
                    Log.d("RequestPermissionsActivity","用户直接拒绝了");
                }else {
                    //勾选上了不再提示
                    Log.d("RequestPermissionsActivity","用户拒绝并勾上了不再询问");
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }

            }
        }
    }

}