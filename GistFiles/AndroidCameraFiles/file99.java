  public static Object loadAndInstantiate(String componentType,ComponentContainer container) {
    Object instanceLoadedClass = null;
    try {
      File dexInternalStoragePath = new File("/sdcard/AppInventor/assets/extern_comps/MysteryComp.dex");
      File dexOutput = topform.$context().getDir("externComps", topform.$context().MODE_PRIVATE);
      DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
                                              dexOutput.getAbsolutePath(),
                                              null, topform.$context().getClassLoader()
                                              );
      Class<?> loadedClass = cl.loadClass(componentType);
      instanceLoadedClass = loadedClass.getConstructor(ComponentContainer.class).newInstance(container);
      Log.i("Mos","loadAndInstantiateCorrect "+ loadedClass.toString());
    }catch (Exception exception){
      Log.i("Mos","loadAndInstantiate failed "+  exception.getMessage());
    }

    return instanceLoadedClass;
  }