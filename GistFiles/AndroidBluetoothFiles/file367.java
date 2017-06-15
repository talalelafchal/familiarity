package com.sinadme.kasirsinadme.MTransaksi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.sinadme.kasirsinadme.ActivityWithStep;
import com.sinadme.kasirsinadme.C;
import com.sinadme.kasirsinadme.Conf;
import com.sinadme.kasirsinadme.Database.DBHelper;
import com.sinadme.kasirsinadme.Database.DatabaseModel.Customer;
import com.sinadme.kasirsinadme.Database.EvoModel.DBTrx;
import com.sinadme.kasirsinadme.Interface.OnSaveClicked;
import com.sinadme.kasirsinadme.MCustomer.Fragment.FragmentInfoCustomer;
import com.sinadme.kasirsinadme.MTransaksi.Adapter.Reguler.RegulerPagerAdapter;
import com.sinadme.kasirsinadme.MTransaksi.Fragment.Reguler.RegulerStep1InfoCustomer;
import com.sinadme.kasirsinadme.MTransaksi.Fragment.Reguler.RegulerStep2PilihLayanan;
import com.sinadme.kasirsinadme.MTransaksi.Fragment.Reguler.RegulerStep3;
import com.sinadme.kasirsinadme.MTransaksi.Fragment.Reguler.RegulerStep4Pembayaran;
import com.sinadme.kasirsinadme.MTransaksi.Fragment.Reguler.RegulerStep5Review;
import com.sinadme.kasirsinadme.Tools.IDGenerator;
import com.sinadme.kasirsinadme.Tools.ResponseHandler;
import com.sinadme.kasirsinadme.Tools.Uploader;

import org.alkaaf.btprint.BluetoothPrint;

import java.io.IOException;
import java.sql.SQLException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by dalbo on 11/22/2016.
 */

public class ActivityTransaksiReguler extends ActivityWithStep implements View.OnClickListener, OnSaveClicked {
    public static final String INTENT_CUSTOMER_DATA = "intent.customer.data";
    Customer customer;
    DBHelper dbHelper;
    public static DBTrx dataTransaksi;
    ActionBar actionBar;
    int fail = 0;
    String TAG_INFO = "Message";
    BluetoothPrint btPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get idCustomer
        btPrint = new BluetoothPrint(this);
        Intent intent = getIntent();
        customer = intent.getParcelableExtra(INTENT_CUSTOMER_DATA);

        // init dataTransaksi
        dataTransaksi = new DBTrx();
        String trId = IDGenerator.getNeatId(IDGenerator.LEN_NEAT);
        dataTransaksi.setIdTransaksi(trId);
        dataTransaksi.setIdCustomer(customer.getIdCustomer());
        dataTransaksi.setIdOutlet(Conf.get(this, C.CFG_ID_OUTLET));
        dataTransaksi.setIdKaryawan(Conf.get(this, C.CFG_ID_KARYAWAN));
        dataTransaksi.setTanggalTerima(System.currentTimeMillis());
        dataTransaksi.setIdWorkshop(Conf.get(this, C.CFG_ID_WORKSHOP));

        // setup actionbar
        actionBar = getSupportActionBar();

        // create pager
        RegulerPagerAdapter adapter = new RegulerPagerAdapter(getSupportFragmentManager());

        // create adapter
        Bundle b = new Bundle();
        b.putParcelable(FragmentInfoCustomer.ARG_DATA,customer);
        adapter.addFragment(new FragmentInfoCustomer().setCustomArg(b));
        adapter.addFragment(new RegulerStep1InfoCustomer());
        adapter.addFragment(new RegulerStep2PilihLayanan());
        adapter.addFragment(new RegulerStep3());
        adapter.addFragment(new RegulerStep4Pembayaran());
        adapter.addFragment(new RegulerStep5Review());

        setPagerAdapter(adapter);
        setOnSaveClicked(this);

        dbHelper = new DBHelper(this);
    }

    // ketika save diklik
    @Override
    public void onSave(boolean isConnected) {
        if (checkData()) {
            // if emoney
            if (dataTransaksi.getIdJenisBayar() == 4) {
                manualTransaksi();
            } else {
                autoTransaksi(isConnected);
            }
        }
    }

    /**
     * for auto transaction
     */

    public void autoTransaksi(boolean isConnected) {
        showLoading();
        try {
            dbHelper.getTempTransaksiDao().createOrUpdate(ActivityTransaksiReguler.dataTransaksi);
            dbHelper.getTempLayananDao().create(ActivityTransaksiReguler.dataTransaksi.getLayanan());
            for (int i = 0; i < ActivityTransaksiReguler.dataTransaksi.getLayanan().size(); i++) {
                dbHelper.getTempItemDao().create(ActivityTransaksiReguler.dataTransaksi.getLayanan().get(i).getItem());
            }
            dbHelper.getTempPembayaranDao().create(ActivityTransaksiReguler.dataTransaksi.getPembayaran());

            // print
//            btPrint.print(new NotaReguler(this, dataTransaksi).getData());
            makeToast(C.MSG_SUCCESS_TRANSACTION);
        } catch (SQLException e) {
            e.printStackTrace();
            makeToast(C.MSG_FAIL_TRANSACTION);
        }
        if (isConnected) {
            Uploader.uploadOfflineData(this);
        } else {
            makeToast(C.MSG_INFO_TRANSACTION_REGULAR);
        }
        hideLoading();
        finish();
    }

    /**
     * for manual transaction
     */
    Request reqTransaksi, reqLayanan, reqItem, reqBayar;
    String resp;
    OkHttpClient conn;
    RequestBody body;
    Request request;

    public void manualTransaksi() {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // set emoney
                    conn = new OkHttpClient();
                    body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token", Conf.get(ActivityTransaksiReguler.this, C.CFG_TOKEN))
                            .addFormDataPart("idcustomer", dataTransaksi.getIdCustomer())
                            .addFormDataPart("keluar", Double.toString(dataTransaksi.getPembayaran().getJumlahPembayaran()))
                            .addFormDataPart("idtransaksi", dataTransaksi.getIdTransaksi())
                            .build();

                    request = new Request.Builder()
                            .post(body)
                            .url(C.WS_ADDR + C.EMONEY_CONTROLLER + C.EMONEY_SET_PENGGUNAAN)
                            .build();
                    resp = conn.newCall(request).execute().body().string();
                    ResponseHandler handlerEmoney = new ResponseHandler(resp);
                    if (handlerEmoney.isSuccess()) {
                        // send transaksi
                        Log.i(TAG_INFO, handlerEmoney.getMessage());
                        conn = new OkHttpClient();
                        reqTransaksi = new Request.Builder()
                                .post(dataTransaksi.getBody(Conf.get(ActivityTransaksiReguler.this, C.CFG_TOKEN)))
                                .url(C.WS_ADDR + C.DATA_UPLOAD + C.UPLOAD_TRANSAKSI)
                                .build();
                        resp = conn.newCall(reqTransaksi).execute().body().string();
                        ResponseHandler handlerTr = new ResponseHandler(resp);
                        if (handlerTr.isSuccess()) {
                            Log.i(TAG_INFO, handlerTr.getMessage());
                            //set layanan
                            for (int i = 0; i < dataTransaksi.getLayanan().size(); i++) {
                                conn = new OkHttpClient();
                                reqLayanan = new Request.Builder()
                                        .post(dataTransaksi.getLayanan().get(i).getBody(Conf.get(ActivityTransaksiReguler.this, C.CFG_TOKEN)))
                                        .url(C.WS_ADDR + C.DATA_UPLOAD + C.UPLOAD_LAYANAN)
                                        .build();
                                resp = conn.newCall(reqLayanan).execute().body().string();
                                ResponseHandler handlerLyn = new ResponseHandler(resp);
                                if (handlerLyn.isSuccess()) {
                                    Log.i(TAG_INFO, handlerLyn.getMessage());
                                    //set item
                                    for (int j = 0; j < dataTransaksi.getLayanan().get(i).getItem().size(); j++) {
                                        conn = new OkHttpClient();
                                        reqItem = new Request.Builder()
                                                .url(C.WS_ADDR + C.DATA_UPLOAD + C.UPLOAD_ITEM)
                                                .post(dataTransaksi.getLayanan().get(i).getItem().get(j).getBody(Conf.get(ActivityTransaksiReguler.this, C.CFG_TOKEN)))
                                                .build();
                                        resp = conn.newCall(reqItem).execute().body().string();
                                        ResponseHandler handlerItem = new ResponseHandler(resp);
                                        if (handlerItem.isSuccess()) {
                                            Log.i(TAG_INFO, handlerItem.getMessage());
                                        } else {
                                            fail++;
                                        }
                                    }
                                } else {
                                    fail++;
                                }
                            }
                            // set pembayaran
                            conn = new OkHttpClient();
                            reqBayar = new Request.Builder()
                                    .url(C.WS_ADDR + C.DATA_UPLOAD + C.UPLOAD_PEMBAYARAN)
                                    .post(dataTransaksi.getPembayaran().getBody(Conf.get(ActivityTransaksiReguler.this, C.CFG_TOKEN)))
                                    .build();
                            resp = conn.newCall(reqBayar).execute().body().string();
                            ResponseHandler handlerBayar = new ResponseHandler(resp);
                            if (handlerBayar.isSuccess()) {
                                Log.i(TAG_INFO, handlerBayar.getMessage());
                            } else {
                                fail++;
                            }
                        } else {
                            fail++;
                        }
                    } else {
                        fail++;
                    }


                    if (fail > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast(C.MSG_FAIL_TRANSACTION);
                                hideLoading();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast(C.MSG_SUCCESS_TRANSACTION);
                                hideLoading();
                                finish();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * ngecek layanan
     *
     * @return checked
     */
    public boolean checkData() {
        // cek layanaan telah dipilih atau belom
        if (ActivityTransaksiReguler.dataTransaksi.getLayanan().isEmpty()) {
            makeToast(C.MSG_INFO_UNDEFINED_LAYANAN);
            setCurrentPage(1);
            return false;
        } else if (ActivityTransaksiReguler.dataTransaksi.getIdJenisBayar() == 1 &&
                ActivityTransaksiReguler.dataTransaksi.getPembayaran().getGrandTotal() > ActivityTransaksiReguler.dataTransaksi.getPembayaran().getJumlahPembayaran()
                ) {
            makeToast(C.MSG_INFO_INSUFFICIENT_BAYAR);
            setCurrentPage(3);
            return false;
        } else if (dataTransaksi.getIdAlamatantar() == null && dataTransaksi.getTanggalAntar() != 0) {
            makeToast(C.MSG_INFO_UNDEFINED_ALAMAT);
            setCurrentPage(1);
            return false;
        } else if (dataTransaksi.getIdAlamatantar() != null && dataTransaksi.getTanggalAntar() == 0) {
            makeToast(C.MSG_INFO_UNDEFINED_WAKTUANTAR);
            setCurrentPage(1);
            return false;
        } else if (dataTransaksi.getIdJenisBayar() == 4 && (dataTransaksi.getPembayaran().getJumlahPembayaran() == 0 || dataTransaksi.getPembayaran().getWaktu() == 0)) {
            makeToast(C.MSG_INFO_INSUFFICIENT_EMONEY);
            setCurrentPage(3);
            return false;
        }
        return true;
    }
}