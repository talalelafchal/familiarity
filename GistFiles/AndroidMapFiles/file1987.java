// ・AsyncTaskで処理を実行
// ・ProgressDialogで進行状況を表示
// ・途中キャンセル可能
//
// という実装をしたいのだけどどうにもうまく行ってないコード。
// (普通に使う分には問題ないけどonPause時に例外を吐いて落ちる)

package com.example.cancelasyncprogress;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import java.io.Serializable;

public class MainActivity extends Activity {
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // タスクを実行するボタンを定義
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // AsyncTaskの実行
                CancellableAsyncTask task = new CancellableAsyncTask();
                task.execute();
            }
        });
    }
    
    // ProgressDialogを出しつつキャンセル可能なAsyncTask(目標)
    public class CancellableAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProcessingDialog dialog;
        
        @Override
        protected void onPreExecute() {
            // ProgressDialogに設定するパラメータの設定
            Bundle argsD = new Bundle();
            argsD.putString("title", "実行中");
            argsD.putString("message", "しばらくお待ちください");
            argsD.putSerializable("listener", new ProcessingDialog.CancelListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void canceled(DialogInterface _interface) {
                    Toast.makeText(activity, "タスクをキャンセルします", Toast.LENGTH_SHORT).show();
                    
                    // BACKキーでDialogを消す際にTaskを停止。
                    cancel(true);
                }
            });
            
            // ProgressDialogを表示する
            dialog = new ProcessingDialog();
            dialog.setArguments(argsD);
            dialog.show(activity.getFragmentManager(), "setdata");
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            // 1秒ごとにProgressDialogを更新するだけの簡単なお仕事
            int count = 0;
            while (count < 10) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                publishProgress(++count);
            }
            return true;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.updateProgress(values[0]);
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.getDialog().dismiss();
            Toast.makeText(activity, "タスクが終了しました", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected void onCancelled() {
            Toast.makeText(activity, "キャンセルされました", Toast.LENGTH_SHORT).show();
        }

    }

    // AsyncTaskから生成されるProgressDialog(を生成するDialogFragment)
    public static class ProcessingDialog extends DialogFragment {
        // キャンセルリスナー用インターフェースの定義
        public interface CancelListener extends Serializable {
            public void canceled(DialogInterface di);
        }

        // Bundleで渡されるリスナーの保管場所
        private CancelListener listener = null;

        // ProgressDialog本体
        private ProgressDialog progressDialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Bundleからリスナーを取得
            listener = (CancelListener) getArguments().getSerializable("listener");

            // ダイアログが生成ずみの場合はそれを返す
            if (progressDialog != null) {
                return progressDialog;
            }

            // ダイアログをキャンセル可能にする
            setCancelable(true);

            // ダイアログを生成する
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(getArguments().getString("title"));
            progressDialog.setMessage(getArguments().getString("message"));
            progressDialog.setMax(10);

            return progressDialog;
        }

        // キャンセルリスナーの実装(？)
        @Override
        public void onCancel(DialogInterface di) {
            listener.canceled(di);
        }
        
        // プログレスバーの更新
        public void updateProgress(int value) {
            if (progressDialog != null) {
                progressDialog.setProgress(value);
            }
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            progressDialog = null;
        }
    }

}
