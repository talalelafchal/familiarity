if (Build.VERSION.SDK_INT >= 23) {
  MarshMallowPermission marshMallowPermission = new MarshMallowPermission(getActivity());
  (!marshMallowPermission.checkPermissionForExternalStorage()) {
                    marshMallowPermission.requestPermissionForExternalStorage();
                }
}