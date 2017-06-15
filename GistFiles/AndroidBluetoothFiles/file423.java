package com.sample.akndroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import java.util.ArrayList;

import models.SelectedProduct;
import reusables.Utilities;


public class PrintReceipt extends AppCompatActivity {
	static Button btnSearch;
	static Button btnSendDraw;
	static Button btnSend;
	static Button btnClose;
	EditText edtContext;
	EditText edtPrint;
	private static final int REQUEST_ENABLE_BT = 2;
	BluetoothService mService = null;
	BluetoothDevice con_dev = null;
	private static final int REQUEST_CONNECT_DEVICE = 1;  //��ȡ�豸��Ϣ

	ArrayList<SelectedProduct> products = new ArrayList<>();
	double change = 0.0,paid = 0.0,total = 0.0,vat = 0.0;


	TextView changeText;
	String msg = "";
	String DIVIDER = "--------------------------------";
	String DIVIDER_DOUBLE = "================================";
	String BREAK = "\n";
	String SPACE5 = "     ";
	String SPACE4 = "   ";
	SharedPreferences sharedPreferences;
	static Context context;
	String header = "";
	boolean isVatEnabled = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_screen);
		context = this;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		isVatEnabled = sharedPreferences.getBoolean(Utilities.RECEIPT_TAX_INVOICE, false);

		mService = new BluetoothService(this, mHandler);
		//�����������˳�����
		if( mService.isAvailable() == false ){
            Toast.makeText(this, "Bluetooth is not available, You wont be able to print the receipt", Toast.LENGTH_LONG).show();
		}

		try {
			paid = getIntent().getExtras().getDouble("paid");
			total = getIntent().getExtras().getDouble("total");
			change = getIntent().getExtras().getDouble("change");
			vat = getIntent().getExtras().getDouble("vat");

			products = (ArrayList<SelectedProduct>) getIntent().getExtras().getSerializable("items");


			header += sharedPreferences.getString(Utilities.SHOP_COMPANY_NAME,"")+ BREAK;
			header += sharedPreferences.getString(Utilities.SHOP_BRAND_NAME,"")+ BREAK;
			header += sharedPreferences.getString(Utilities.SHOP_ADDRESS,"")+ BREAK;
			header += sharedPreferences.getString(Utilities.SHOP_PHONE,"")+ BREAK;
			msg += DIVIDER_DOUBLE + BREAK ;
			msg += "Receipt #"+System.currentTimeMillis()+ BREAK;
			msg += "Date: "+Utilities.getToday()+ BREAK;
			msg += "Cashier: Admin"+ BREAK;
			msg +=  BREAK;

			msg += DIVIDER + BREAK;
			msg += "Qty"+SPACE4+"Item(s)"+BREAK;
			msg += DIVIDER + BREAK;
			for(int g = 0;g < products.size();g++){
				msg += products.get(g).getCount() +SPACE5+ products.get(g).getProduct().getProduct_name()+BREAK;
			}



			msg +=  BREAK;
			msg += DIVIDER_DOUBLE + BREAK;
			if(isVatEnabled){
				msg += "VAT: N"+Utilities.CurrencyFormatNoNaira(String.valueOf(vat)) + BREAK;
				msg +=  BREAK;
			}
			msg += "Grand Total: N"+Utilities.CurrencyFormatNoNaira(String.valueOf(total)) + BREAK;
			msg += DIVIDER_DOUBLE + BREAK;
			msg += "Payment Type: Cash" + BREAK;
			msg += "Total Amount Paid: N"+Utilities.CurrencyFormatNoNaira(String.valueOf(paid)) + BREAK;
			msg += "Balance: N"+Utilities.CurrencyFormatNoNaira(String.valueOf(change)) + BREAK;
			msg += DIVIDER + BREAK + BREAK;
			msg += sharedPreferences.getString(Utilities.RECEIPT_FOOTER_ONE,sharedPreferences.getString(Utilities.SHOP_COMPANY_NAME,"")) + BREAK;
			msg += sharedPreferences.getString(Utilities.RECEIPT_FOOTER_TWO,"")+ BREAK;


		}catch (Exception s){

		}

		startGoogleAnalytics();
	}

	private void startGoogleAnalytics() {
//		Tracker mTracker;
//		AppController application = (AppController) getApplication();
//		mTracker = application.getDefaultTracker();
//		mTracker.setScreenName("Print Receipt");
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

    @Override
    public void onStart() {
    	super.onStart();
    	//����δ�򿪣�������

		try {
			if (mService.isBTopen() == false) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
			try {
				changeText = (TextView) findViewById(R.id.change);
				btnSendDraw = (Button) this.findViewById(R.id.btn_test);
				btnSendDraw.setOnClickListener(new ClickEvent());
				btnSearch = (Button) this.findViewById(R.id.btnSearch);
				btnSearch.setOnClickListener(new ClickEvent());
				btnSend = (Button) this.findViewById(R.id.btnSend);
				btnSend.setOnClickListener(new ClickEvent());
				btnClose = (Button) this.findViewById(R.id.btnClose);
				btnClose.setOnClickListener(new ClickEvent());
				edtContext = (EditText) findViewById(R.id.txt_content);
				btnClose.setEnabled(true);
				btnSend.setEnabled(false);
				btnSendDraw.setEnabled(false);
			} catch (Exception ex) {
				Log.e("������Ϣ", ex.getMessage());
			}

			changeText.setText(Utilities.CurrencyFormat(context, String.valueOf(change)));
		}catch (Exception s){
			Utilities.showAlert(context,"Error connecting to bluetooth printer",true);
		}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mService != null)
		mService.stop();
		mService = null;
	}

	class ClickEvent implements View.OnClickListener {
		public void onClick(View v) {
			if (v == btnSearch) {
				Intent serverIntent = new Intent(PrintReceipt.this,DeviceListActivity.class);      //��������һ����Ļ
				startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
			} else if (v == btnSend) {

				byte[] cmd = new byte[3];
				cmd[0] = 0x1b;
				cmd[1] = 0x21;
				cmd[2] |= 0x10;
				mService.write(cmd);
				mService.sendMessage(header, "GBK");
				cmd[2] &= 0xEF;
				mService.write(cmd);
				mService.sendMessage(msg, "GBK");


			} else if (v == btnClose) {
				mService.stop();
				Intent intents = new Intent(context, MainActivity.class);
				intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intents);
				finish();
			} else if (v == btnSendDraw) {
                String msg = "";
                String lang = getString(R.string.strLang);
				//printImage();

            	byte[] cmd = new byte[3];
        	    cmd[0] = 0x1b;
        	    cmd[1] = 0x21;
            	if((lang.compareTo("en")) == 0){
            		cmd[2] |= 0x10;
            		mService.write(cmd);           //���?����ģʽ
            		mService.sendMessage("Congratulations!\n", "GBK");
            		cmd[2] &= 0xEF;
            		mService.write(cmd);           //ȡ��ߡ�����ģʽ
            		msg = "  You have sucessfully created communications between your device and our bluetooth printer.\n\n"
                          +"  the company is a high-tech enterprise which specializes" +
                          " in R&D,manufacturing,marketing of thermal printers and barcode scanners.\n\n";


            		mService.sendMessage(msg,"GBK");
            	}else if((lang.compareTo("ch")) == 0){
            		cmd[2] |= 0x10;
            		mService.write(cmd);           //���?����ģʽ
        		    mService.sendMessage("��ϲ��\n", "GBK"); 
            		cmd[2] &= 0xEF;
            		mService.write(cmd);           //ȡ��ߡ�����ģʽ
            		msg = "  ���Ѿ��ɹ��������������ǵ�������ӡ��\n\n"
            		+ "  ����˾��һ��רҵ�����з��������������Ʊ�ݴ�ӡ�������ɨ���豸��һ��ĸ߿Ƽ���ҵ.\n\n";
            	    
            		mService.sendMessage(msg,"GBK");	
            	}
			}
		}
	}
	
    /**
     * ����һ��Handlerʵ�����ڽ���BluetoothService�෵�ػ�������Ϣ
     */
    private static final  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:   //������
                	Toast.makeText(context, "Connect successful",
                            Toast.LENGTH_SHORT).show();
        			btnClose.setEnabled(true);
        			btnSend.setEnabled(true);
        			btnSendDraw.setEnabled(true);
                    break;
                case BluetoothService.STATE_CONNECTING:  //��������
                	Log.d("��������","��������.....");
                    break;
                case BluetoothService.STATE_LISTEN:     //�������ӵĵ���
                case BluetoothService.STATE_NONE:
                	Log.d("��������","�ȴ�����.....");
                    break;
                }
                break;
            case BluetoothService.MESSAGE_CONNECTION_LOST:    //�����ѶϿ�����
                Toast.makeText(context, "Device connection was lost",
                               Toast.LENGTH_SHORT).show();
    			btnClose.setEnabled(false);
    			btnSend.setEnabled(false);
    			btnSendDraw.setEnabled(false);
                break;
            case BluetoothService.MESSAGE_UNABLE_CONNECT:     //�޷������豸
            	Toast.makeText(context, "Unable to connect device",
                        Toast.LENGTH_SHORT).show();
            	break;
            }
        }
        
    };
        
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    
        switch (requestCode) {
        case REQUEST_ENABLE_BT:      //���������
            if (resultCode == Activity.RESULT_OK) {   //�����Ѿ���
            	Toast.makeText(this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
            } else {                 //�û������������
            	finish();
            }
            break;
        case  REQUEST_CONNECT_DEVICE:     //��������ĳһ�����豸
        	if (resultCode == Activity.RESULT_OK) {   //�ѵ�������б��е�ĳ���豸��
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //��ȡ�б������豸��mac��ַ
                con_dev = mService.getDevByMac(address);   
                
                mService.connect(con_dev);
            }
            break;
        }
    } 
    
    //��ӡͼ��
    @SuppressLint("SdCardPath")
	private void printImage() {
    	byte[] sendData = null;
    	PrintPic pg = new PrintPic();
    	pg.initCanvas(384);     
    	pg.initPaint();
    	pg.drawImage(0, 0, "/mnt/sdcard/xyz.jpg");
    	sendData = pg.printDraw();
    	mService.write(sendData);   //��ӡbyte�����
    }
}
