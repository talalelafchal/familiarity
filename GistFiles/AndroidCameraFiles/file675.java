package com.biciq.biciqscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.biciq.biciqscan.tasks.QRCodeScanTask;
import com.biciq.biciqscan.tasks.TaskListener;
import com.biciq.biciqscan.utils.AlertDialogBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by hpneo on 8/08/14.
 */
public class QRScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
  private ZXingScannerView scannerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    scannerView = new ZXingScannerView(this);
    setContentView(scannerView);

    ArrayList<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
    formats.add(BarcodeFormat.QR_CODE);

    scannerView.setFormats(formats);
    scannerView.setAutoFocus(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
    scannerView.setResultHandler(this);
    scannerView.startCamera();
  }

  @Override
  protected void onPause() {
    super.onPause();
    scannerView.stopCamera();
  }

  @Override
  public void handleResult(Result result) {
    Log.i("QR_SCANNER_ACTIVITY_HANDLE_RESULT", result.getText());

    if (!result.getText().startsWith("/tickets")) {
      AlertDialog.Builder dialog = AlertDialogBuilder.create(QRScannerActivity.this,
              "Biciq",
              "Tiquete no válido para este evento");

      dialog.setPositiveButton(R.string.login_dialog_ok_button, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int id) {
          Intent resultIntent = new Intent();
          setResult(RESULT_CANCELED, resultIntent);
          finish();
        }
      });

      dialog.show();
    }
    else {
      QRCodeScanTask qrCodeScanTask = new QRCodeScanTask();

      qrCodeScanTask.setTaskListener(getQRCodeScanTask());
      qrCodeScanTask.execute(result.getText());
    }
  }

  private TaskListener getQRCodeScanTask() {
    return new TaskListener() {
      @Override
      public void onTaskCompleted(Object result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("scanned_result", result + "");
        setResult(RESULT_OK, resultIntent);

        finish();
      }

      @Override
      public void onTaskError(Object result) {

      }

      @Override
      public void onTaskCancelled(Object result) {

      }
    };
  }
}