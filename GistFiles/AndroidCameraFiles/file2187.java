Trong hàm onCreate() yêu cầu có permission
 protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // TODO: 5/23/2016  request permission on Android 6
    // Request permissions to support Android Marshmallow and above devices
    if (Build.VERSION.SDK_INT >= 23) {
        checkPermissions();
    }
}

// TODO: 5/23/2016 implement check permission, show dialog to make user to choose to allow
// START PERMISSION CHECK0

final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
https://developer.android.com/training/permissions/requesting.html#perm-check
@TargetApi(Build.VERSION_CODES.M)
private void checkPermissions() {
    List<String> permissions = new ArrayList<>();
    String message = "OSMDroid permissions:";

    // chỉ check dangerous permission thôi ko cần permission khác.
    // TODO: 5/23/2016  so sánh với quá khứ, trong quá khứ ta có allow access ko (có permission nào nằm trong group chưa), nếu ko thì add cái nào ko vào 1 cái list
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        message += "\nStorage access to store map tiles.";
    }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        message += "\nLocation to show user location.";
    }

    if (!permissions.isEmpty()) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        String[] params = permissions.toArray(new String[permissions.size()]);
        // request là hiện hộp thoại, nếu chưa cho phép thì phải request, yêu cầu người dùng cho phép, sau đó gọi hàm onRequestPermissionsResult()   
        requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    } // else: We already have permissions, so handle as normal

    // TODO: 5/23/2016 sau khi check permission trước đó ta chưa có allow, thì app hiện tại bảng thông báo cho người dùng allow , nếu trong quá khứ đã allow rồi thì ko hiện cái bảng này nữa
}

// sau khi hein65 thông báo xong thì chạy hàm này
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {

    	// check xem ta đã allow và deny những permission gì rồi, từ đó xuất ra đoạn output tương ứng.
        case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
            Map<String, Integer> perms = new HashMap<>();
            // Initial
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with results
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
            // Manifest.permission. : do mọi permission của ANdroid đều nằm trong đây.
            Boolean location = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            // TODO: 5/23/2016 4 when you permit 2 permission this toast is show + they can use app without crash
            if (location && storage) {
                // All Permissions Granted
                Toast.makeText(MainActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            }

            // TODO: 5/23/2016 3 when you deny the dialog, this toast is showed
            else if (location) {
                Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage.", Toast.LENGTH_LONG).show();
            } else if (storage) {
                Toast.makeText(this, "Location permission is required to show the user's location on map.", Toast.LENGTH_LONG).show();
            } else { // !location && !storage case
                // Permission Denied
                Toast.makeText(MainActivity.this, "Storage permission is required to store map tiles to reduce data usage and for offline usage." +
                        "\nLocation permission is required to show the user's location on map.", Toast.LENGTH_SHORT).show();
            }
        }
        break;
        default:
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
// END PERMISSION CHECK
# Request Permission:
Trong nhưng năm 1990s, có 1 loại virus lây lan qua email trong OutLook bằng cách copy chính nó qua từng contacts email
trong Outlook.

Ngày này, những Android app secure contacts bằng cách phải bắt user tự họ cho phép app được quyền access contact hay ko?
(tùy bản Android mà nó hỏi lúc cài app hay lúc chạy app).

https://developer.android.com/guide/topics/security/permissions.html#defining
Quy tắc an toàn trong ANdroid: mọi app ko có quyền tạo các hành động ảnh hưởng đến app khác, hay là hệ điều hành, hay user. Vd:
. read/write user private data.(contacts emails).
. read/write đến file của app khác.
. tạo kết nối internet.
. luôn giữ cho device awake.
-> nếu như app mà muốn làm những việc trên thì nó phải khai báo permission và phải thông báo cho user biết.


#######################################################
Nếu mà bạn tạo 1 service hay data(content provider) và cho muốn app khác muốn access vào thì app đó phải xin phép.

1. request permission
dùng lệnh "uses-permission" trong Android Manifest
<uses-permission android:name="android.permission.ACCESS_LOCATION" />

Nếu mà ta ko request permissio mà ta dùng nó sẽ bị lỗi "SecurityException"

# Requesting Permissions at Run Time
https://developer.android.com/training/permissions/requesting.html

>api 23, thì user phải cho phép permission của app lúc chạy nó (khác những bản trước).
-> control app hơn, vd ta có quyền cho phép sd camera nhưng ko cho phép xác định location.
-> khi cho phép rồi thì app ko hỏi lại, chưa cho phép thì nó hỏi hoài, có thể sữa những cái này trong Setting của app
-> lý do thứ 2 là code cho phép permission có quyền đặt bất kì đâu, từ đầu chtr, hay khi bắt đầu intent 1 activity nào đó, hay khi start 1 service.
System permissions: 2 loại:
. normal: nếu app sd permission này thì system cho ko cần hỏi user (do permission này ko gây hại/ rất ít gì cho user cả)
https://developer.android.com/guide/topics/security/normal-permissions.html
. dangerous: là permission cho phép app access dữ liệu riêng tư của người dùng -> phải hỏi người dùng trước khi sử dụng
https://developer.android.com/guide/topics/security/permissions.html#defining
2 cái trên:
* giống: đều phải khai báo trng android manifest.
* khác: 
	- trước android 6: app sẽ liệt kê mọi dangerous permission trước khi cài app -> user phải đồng ý hết mới dc cài.
	- sau android 6: app sẽ thông báo từng dangerous permission cho user, user có quyền allow cái này, deny cái kia
	và app sẽ chỉ chạy 1 phần khả năng của nó tùy thuộc vào sự cho phép của user.

# Permission groups:
1 nhóm dangerous permission sẽ thuộc 1 group nào đó. Khi request có 2 TH:
. Nếu như app chưa có pcho phép trong group thì hiện hộp thoại khi request.
Hộp thoại sẽ hỏi group này có dc cho phép ko chứ ko có hỏi permission này có dc ko?
. Nếu như app đã dc cho phép 1 permission, và dang reqeust permission khác nhưng nằm trong group có permission đã allow, thì cho allow luôn ko cần hỏi
ta có các group sau: https://developer.android.com/guide/topics/security/permissions.html#normal-dangerous

# Requiring Permissions: tự mình tạo ra permission
sd <permission> và app khác muốn request permission thì nó sd <uses-permission>
https://developer.android.com/guide/topics/security/permissions.html#defining


# Các bước chekc permission trong API > 23
http://stackoverflow.com/questions/32491960/android-check-permission-for-locationmanager
1. thêm permission vào Android Manifest.

2. checkSelfPermission() là check xem trong quá khứ ta đã allow permission chưa

3. nếu chưa thì request permission requestPermissions()

4. gọi bước 123 chỉ khi ta đang xét API >= 23, nen phải thêm lenh sau mới gọi được
// TODO: 5/23/2016 1 request permission on Android 6
        // Request permissions to support Android Marshmallow and above devices
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }

#######################################################
http://guides.codepath.com/android/Understanding-App-Permissions#runtime-permissions

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // In an actual app, you'd want to request a permission when the user performs an action
        // that requires that permission.
        getPermissionToReadUserContacts();
    }

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

##########
Hay ta sd library sau để cho cv dễ dàng hơn:
https://github.com/hotchemi/PermissionsDispatcher
