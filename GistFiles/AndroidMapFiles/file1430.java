public Notification buildNotification(String alert, Map<String, String> extras) {
          Context c = UAirship.shared().getApplicationContext();
     
          if(!hs.isForeground(c)) {
              return super.buildNotification(alert, extras);
          } else {
              return null;
        }
}