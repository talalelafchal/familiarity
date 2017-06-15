public class RxPermissionChecker extends Fragment {

    //region CONSTANTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String TAG = RxPermissionChecker.class.getSimpleName();
    private static final int REQUEST_CODE = new Random().nextInt();

    //endregion

    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final PublishRelay<Signal> mRelay = PublishRelay.create();
    private String[] mPermissionsToCheck;

    //endregion

    //region INJECTED DEPENDENCIES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //endregion

    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //endregion

    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param permissions the permissions to check.
     * @return if all supplied permissions are granted.
     */
    private boolean haveAllPermissions(@NonNull String... permissions) {
        final Activity activity = getActivity();
        for (int i = 0; i < permissions.length; i++) {
            if (PermissionChecker.checkSelfPermission(activity, permissions[i]) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes this behavioral component fragment when it's functional responsibility is no longer needed.
     */
    private void complete() {
        getFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    //endregion

    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static Observable<Signal> checkPermission(Fragment fragment,
                                                     String... permissions) {
        return checkPermission(fragment.getFragmentManager(), permissions);
    }

    public static Observable<Signal> checkPermission(Activity activity,
                                                     String... permissions) {
        return checkPermission(activity.getFragmentManager(), permissions);
    }

    private static Observable<Signal> checkPermission(FragmentManager fragmentManager, String... permissions) {
        return Observable.defer(() -> {
            RxPermissionChecker rxPermissionChecker = new RxPermissionChecker();
            rxPermissionChecker.mPermissionsToCheck = permissions;
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionChecker, TAG)
                    .commit();
            return rxPermissionChecker.mRelay;
        });
    }

    //endregion

    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //endregion

    //region {Fragment} ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mPermissionsToCheck != null) {
            if (haveAllPermissions(mPermissionsToCheck)) {
                mRelay.call(new Signal(mPermissionsToCheck, true));
                complete();
            } else {
                FragmentCompat.requestPermissions(this, mPermissionsToCheck, REQUEST_CODE);
            }
        } else {
            Log.e(TAG, "RxPermissionChecker component was created without a list of permissions to check!");
            complete();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            mRelay.call(new Signal(permissions, grantResults));
        }
        complete();
    }

    //endregion

    //region INNER CLASSES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static class Signal {
        private Map<String, Boolean> mResults = new HashMap<>();

        private Signal(@NonNull String[] permissions, Boolean allPermission) {
            for (int i = 0; i < permissions.length; i++) {
                mResults.put(permissions[i], allPermission);
            }
        }

        private Signal(@NonNull String[] permissions,
                       @NonNull int[] grantResults) {
            if (permissions.length == grantResults.length) {
                for (int i = 0; i < permissions.length; i++) {
                    mResults.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
                }
            }
        }

        public boolean isPermissionGranted(String permission) {
            return Boolean.TRUE.equals(mResults.get(permission));
        }

        public boolean allPermissionsGranted() {
            for (Boolean value: mResults.values()) {
                if (Boolean.FALSE.equals(value)) {
                    return false;
                }
            }
            return true;
        }

        public boolean anyPermissionGranted() {
            for (Boolean value: mResults.values()) {
                if (Boolean.TRUE.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    //endregion

}