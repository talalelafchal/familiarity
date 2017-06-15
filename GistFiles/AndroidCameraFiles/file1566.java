// when click a button
//
private void runThread() {
    new Thread() {
        public void run() {
            while (i++ < 1000) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn.setText("#" + i);
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }.start();
}