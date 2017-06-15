    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_ADD_ACCOUNT);
    // 対象をGoogleアカウントに限定
    intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES,new String[]{"com.google"});
    startActivity(intent);