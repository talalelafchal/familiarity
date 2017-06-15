        // ActivityManagerServiceからはこんな風に呼ばれていました。
        mUsageStatsService = IUsageStats.Stub.asInterface(ServiceManager.getService("usagestats"));

        // リフレクションではこう呼んだり。。ServiceLocatorは上記ブログ参照。。
        mUsageStatsService = ServiceLocator.getServiceStub("usagestats",
                "com.android.internal.app.IUsageStats$Stub");

        // 関数のコールもリフレクション。。
        Class<?> clazz = Class.fromName("com.android.internal.app.IUsageStats$Stub");
        Method method = clazz.getDeclaredMethod("getAllPkgUsageStats");
        method.setAccessible(true);
        PkgUsageStats[] result = (PkgUsageStats[]) method.invoke(mUsageStatsService);
