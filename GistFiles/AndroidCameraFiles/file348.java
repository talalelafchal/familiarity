import android.os.AsyncTask;
import android.widget.TextView;

public class CameraActivity extends AppCompatActivity {
    ...
    private TextView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        progress = (TextView) findViewById(R.id.progress);
    }

    public void enviarFoto(View view) {
        AsyncTask<Void, Integer, Boolean> imageSender = new AsyncTask<Void, Integer, Boolean>() {
            private static final int ITERACIONES_TOTAL = 100;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    for (int i = 0; i < ITERACIONES_TOTAL; ++i) {
                        publishProgress(i * 100 / ITERACIONES_TOTAL);
                        Thread.sleep(50);
                    }
                    return true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progress.setText("Completado " + values[0] + "%");
            }

            @Override
            protected void onPostExecute(Boolean exito) {
                if (exito) {
                    progress.setText("Envío finalizado con éxito");
                    imageView.setImageResource(android.R.color.darker_gray);
                } else {
                    progress.setText("Envío finalizado con error");
                }
            }
        };
        imageSender.execute();
    }
}
