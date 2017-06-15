package com.genyware.core;

//missed

public class App extends Application implements Bus.Subscriber {
 
  public static TManager tm;
  private static App self;
  
  public static App getInstance() {
    return self;
  }
  @Override
  public void onCreate() {
    tm = TManager.getInstance();
    // start service if not running
    super.onCreate();
    self = this;
  }
}