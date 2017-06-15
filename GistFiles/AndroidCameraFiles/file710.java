public static void printPermissions(Context context) {
    PackageInfo android;
    try {
        android = context.getPackageManager().getPackageInfo("android", PackageManager.GET_PERMISSIONS);
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
        return;
    }

    PermissionInfo[] permissions = android.permissions;
    SparseArrayCompat<List<String>> sparseArray = new SparseArrayCompat<>();
    PermissionInfo permissionInfo;

    int size = permissions.length;
    for (int i = 0; i < size; i++) {
        permissionInfo = permissions[i];
        List<String> list = sparseArray.get(permissionInfo.protectionLevel);
        if (list == null) {
            list = new ArrayList<>(30);
            sparseArray.put(permissionInfo.protectionLevel, list);
        }
        list.add(permissionInfo.name);
    }

    size = sparseArray.size();
    for (int i = 0; i < size; i++) {
        int level = sparseArray.keyAt(i);
        List<String> list = sparseArray.get(level);
        Collections.sort(list);

        Log.i("tag", protectionToString(level));
        for (int j = 0; j < list.size(); j++) {
            Log.i("tag", list.get(j));
        }
    }

}

/**
 * Lifted from {@link PermissionInfo}
 */
public static String protectionToString(int level) {
    String protLevel = "????";
    switch (level & PermissionInfo.PROTECTION_MASK_BASE) {
        case PermissionInfo.PROTECTION_DANGEROUS:
            protLevel = "dangerous";
            break;
        case PermissionInfo.PROTECTION_NORMAL:
            protLevel = "normal";
            break;
        case PermissionInfo.PROTECTION_SIGNATURE:
            protLevel = "signature";
            break;
        case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
            protLevel = "signatureOrSystem";
            break;
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
        protLevel += "|privileged";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
        protLevel += "|development";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_APPOP) != 0) {
        protLevel += "|appop";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_PRE23) != 0) {
        protLevel += "|pre23";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_INSTALLER) != 0) {
        protLevel += "|installer";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_VERIFIER) != 0) {
        protLevel += "|verifier";
    }
    if ((level & PermissionInfo.PROTECTION_FLAG_PREINSTALLED) != 0) {
        protLevel += "|preinstalled";
    }
    return protLevel;
}