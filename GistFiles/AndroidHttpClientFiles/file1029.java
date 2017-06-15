Handler myHandler = new Handler() {  
  public void handleMessage(Message msg) {   
    switch (msg.what) {   
      case 0: 
        String data = (String) msg.obj;  
        // Do Something
        break;
    }   
    super.handleMessage(msg);
  }   
};