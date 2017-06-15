package android.stickynotes;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import android.os.PatternMatcher;
import android.stickynotes.NdefConverter;

public class NfcUtil {

	private static final String TAG = NfcUtil.class.getSimpleName();
	public static String URI_TYPE = "http";
	public static String URI_HOST = "myhost";
	public static String URI_PREFIX = "mypath";
	public static String URI_ID_FIELD = "id";
	public static String BASE_NDEF_URI;

	private Activity activity;
	private NfcAdapter nfcAdapter;
	private PendingIntent nfcPendingIntent;
	private IntentFilter[] ndefReadFilters;
	private IntentFilter[] writeFilters;
	private boolean mWriteMode = false;

	public NfcUtil(Activity activity) {
		this.activity = activity;
		nfcAdapter = getNfcAdapter();
		nfcPendingIntent = getPendingIntent();
		ndefReadFilters = getReadFiltersForText(); //getReadFiltersForUrl();
		writeFilters = getWriteFilters();
	}

	public IntentFilter[] getReadFiltersForUrl() {
		BASE_NDEF_URI = URI_TYPE + "://" + URI_HOST + URI_PREFIX + "?"
		+ URI_ID_FIELD + "=";
		// Intent filters for reading specifically formatted urls from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		ndefDetected.addDataScheme(URI_TYPE);
		ndefDetected.addDataAuthority(URI_HOST, null);
		ndefDetected.addDataPath(URI_PREFIX, PatternMatcher.PATTERN_PREFIX);
		return new IntentFilter[] { ndefDetected };
	}
	
	public IntentFilter[] getReadFiltersForText() {
		// Intent filters for reading text from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain"); //"*/*");
		} catch (MalformedMimeTypeException e) { }
		return new IntentFilter[] { ndefDetected };
	}
	
	public IntentFilter[] getWriteFilters(){
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		return new IntentFilter[] { tagDetected };
	}
	
	public PendingIntent getPendingIntent(){
		return PendingIntent.getActivity(activity, 0, new Intent(
				activity, activity.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	public NfcAdapter getNfcAdapter() {
		if (hasNfcSupport()) {
			NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
			return nfcAdapter;
		}
		return null;
	}

	public boolean isNfcEnabled() {
		NfcAdapter adapter = getNfcAdapter();
		return adapter != null && adapter.isEnabled();
	}

	public boolean checkNdefIntentMatch(Intent intent) {
		return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
	}

	public boolean checkTagIntentMatch(Intent intent) {
		return NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction());
	}

	// Pre: Intent must match ACTION_NDEF_DISCOVERED
	public byte[] getNdefPayload(Intent intent) {
		NdefMessage[] messages = getNdefMessages(intent);
		byte[] payload = messages[0].getRecords()[0].getPayload();
		return payload;
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
		} else {
			// Unknown tag type
			byte[] empty = new byte[] {};
			NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
			NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
			msgs = new NdefMessage[] { msg };
		}
		return msgs;
	}

	public Tag getTag(Intent intent) {
		return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	}
	
	public void enableNdefExchangeMode(NdefMessage msg) {
		enableForegroundNdefPush(msg);
		enableForegroundDispatch();
	}

	public void enableEnhancedNdefExchangeMode(NdefConverter entity, String pckgName) {
		nfcAdapter.enableForegroundNdefPush(activity,
				getIcsEnhancedNdefMessage(entity.asNdef(), pckgName));
		enableForegroundDispatch();
	}

	public void disableNdefExchangeMode() {
		disableForegroundNdefPush();
		disableForegroundDispatch();
	}

	public void enableTagWriteMode() {
		mWriteMode = true;
		enableForegroundDispatch(writeFilters);
	}

	public void disableTagWriteMode() {
		mWriteMode = false;
		disableForegroundDispatch();
	}
	
	public boolean isWriteMode(){
		return mWriteMode;
	}

	public boolean writeTag(NdefRecord record, Tag tag) {
		NdefMessage message = new NdefMessage(new NdefRecord[] { record });
		return writeTag(message, tag);
	}

	public boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;
		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();
				if (!ndef.isWritable()) {
					shortToast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					shortToast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}
				ndef.writeNdefMessage(message);
				shortToast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						shortToast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						shortToast("Failed to format tag.");
						return false;
					}
				} else {
					shortToast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			shortToast("Failed to write tag");
		}
		return false;
	}

	public static NdefRecord getIcsAppRecord(String pckgName) {
		return NdefRecordIcs.createApplicationRecord(pckgName);
	}

	public NdefMessage getIcsEnhancedNdefMessage(NdefRecord firstRecord,
			String pckgName) {
		return new NdefMessage(new NdefRecord[] { firstRecord,
				getIcsAppRecord(pckgName) });
	}

	public static NdefRecord getByUri(String uriString) {
		return NdefRecordIcs.createUri(uriString);
	}

	public void disableForegroundNdefPush(Activity activity) {
		nfcAdapter.disableForegroundNdefPush(activity);
	}

	public void disableForegroundNdefPush() {
		disableForegroundNdefPush(activity);
	}

	public void enableForegroundNdefPush(Activity activity, NdefMessage msg) {
		nfcAdapter.enableForegroundNdefPush(activity, msg);
	}

	public void enableForegroundNdefPush(NdefMessage msg) {
		enableForegroundNdefPush(activity, msg);
	}

	public void disableForegroundDispatch(Activity activity) {
		nfcAdapter.disableForegroundDispatch(activity);
	}

	public void disableForegroundDispatch() {
		disableForegroundDispatch(activity);
	}

	public void enableForegroundDispatch(Activity activity,
			PendingIntent intent, IntentFilter[] filters, String[][] techLists) {
		nfcAdapter.enableForegroundDispatch(activity, intent, filters,
				techLists);
	}

	public void enableForegroundDispatch(Activity activity,
			PendingIntent intent, IntentFilter[] filters) {
		enableForegroundDispatch(activity, intent, filters, null);
	}

	public void enableForegroundDispatch(PendingIntent intent,
			IntentFilter[] filters) {
		enableForegroundDispatch(activity, intent, filters, null);
	}

	public void enableForegroundDispatch(PendingIntent intent) {
		enableForegroundDispatch(activity, intent, ndefReadFilters, null);
	}

	public void enableForegroundDispatch() {
		enableForegroundDispatch(activity, nfcPendingIntent, ndefReadFilters, null);
	}

	public void enableForegroundDispatch(IntentFilter[] filters) {
		enableForegroundDispatch(activity, nfcPendingIntent, filters, null);
	}
	
	public static boolean hasNfcSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }
	
	public void shortToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
	
	public void longToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
    }
	
	public void requestNfcActivation(){
		if (!isNfcEnabled()) {
			longToast("Please activate NFC");
			activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}
	
}
