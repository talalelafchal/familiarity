...

    private void initWpDb() {
        if (!createAndVerifyWpDb()) {
            AppLog.e(T.DB, "Invalid database, sign out user and delete database");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            currentBlog = null;
            editor.remove(WordPress.WPCOM_USERNAME_PREFERENCE);
            editor.remove(WordPress.WPCOM_PASSWORD_PREFERENCE);
            editor.remove(WordPress.ACCESS_TOKEN_PREFERENCE);
            editor.commit();
            if (wpDB != null) {
                wpDB.updateLastBlogId(-1);
                wpDB.deleteDatabase(this);
            }
            wpDB = new WordPressDB(this);
        }
    }

    private boolean createAndVerifyWpDb() {
        try {
            wpDB = new WordPressDB(this);
            // verify account data
            List<Map<String, Object>> accounts = wpDB.getAllAccounts();
            for (Map<String, Object> account : accounts) {
                if (account == null || account.get("blogName") == null || account.get("url") == null) {
                    return false;
                }
            }
            return true;
        } catch (SQLiteException sqle) {
            AppLog.e(T.DB, sqle);
            return false;
        } catch (RuntimeException re) {
            AppLog.e(T.DB, re);
            return false;
        }
    }
    
...