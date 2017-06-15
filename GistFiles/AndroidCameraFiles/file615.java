// Example 1: View.post(Runnable)
// For example, below is some code for a click listener that downloads an image from a separate thread and displays it in an ImageView:
// make a worker thread

Cách này là sd interface Runnable có method run và class Thread trong java thuần để chạy background, sd post() để kéo lên UI thread -> ko tiện bằng asynctask
public void onClick(View v) {
    new Thread(new Runnable() {
        public void run() {
            final Bitmap bitmap = loadImageFromNetwork("http://example.com/image.png");
            // access the UI thread from worker thread.
            mImageView.post(new Runnable() {
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }
    }).start();
}