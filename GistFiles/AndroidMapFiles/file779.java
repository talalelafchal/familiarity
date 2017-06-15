protected void onInstall(ClassLoader classLoader) throws Throwable {
        Object sCacheObj = FieldUtils.readStaticField(ServiceManagerCompat.Class(), "sCache");
        if (sCacheObj instanceof Map) {
            Map sCache = (Map) sCacheObj;
            Object Obj = sCache.get(mServiceName);
            IBinder mServiceIBinder = null;
            if (Obj != null && Obj instanceof IBinder) {
                mServiceIBinder = (IBinder) Obj;
            } else {
                sCache.remove(mServiceName);
                mServiceIBinder = ServiceManagerCompat.getService(mServiceName);
            }
            if (mServiceIBinder != null) {
                MyServiceManager.addOriginService(mServiceName, mServiceIBinder);
                Class clazz = mServiceIBinder.getClass();
                List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
                Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
                IBinder mProxyServiceIBinder = (IBinder) MyProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
                sCache.put(mServiceName, mProxyServiceIBinder);
                MyServiceManager.addProxiedServiceCache(mServiceName, mProxyServiceIBinder);
            }
        }
    }