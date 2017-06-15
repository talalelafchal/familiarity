/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.telecom;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothHeadsetPhone;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telecom.CallState;
import android.telecom.PhoneAccount;
import android.telecom.PhoneCapabilities;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.server.telecom.CallsManager.CallsManagerListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// SS BLUETOOTH : import
import com.android.server.telecom.BluetoothVoIPService;

import android.telephony.SubscriptionManager;
import com.samsung.android.telephony.MultiSimManager;
import com.android.server.telecom.secutils.TelecomUtilsMultiSIM;

import com.android.services.telephony.common.PhoneFeature;
import com.android.services.telephony.common.SystemDBInterface;
import com.android.server.telecom.secutils.TelecomUtils;

// SS BLUETOOTH : import

/**
 * Bluetooth headset manager for Telecom. This class shares the call state with the bluetooth device
 * and accepts call-related commands to perform on behalf of the BT device.
 */
public final class BluetoothPhoneService extends Service {
    /**
     * Request object for performing synchronous requests to the main thread.
     */
    private static class MainThreadRequest {
        Object result;
        int param;

        MainThreadRequest(int param) {
            this.param = param;
        }

        void setResult(Object value) {
            result = value;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private static final String TAG = "BluetoothPhoneService";

    private static final int MSG_ANSWER_CALL = 1;
    private static final int MSG_HANGUP_CALL = 2;
    private static final int MSG_SEND_DTMF = 3;
    private static final int MSG_PROCESS_CHLD = 4;
    private static final int MSG_GET_NETWORK_OPERATOR = 5;
    private static final int MSG_LIST_CURRENT_CALLS = 6;
    private static final int MSG_QUERY_PHONE_STATE = 7;
    private static final int MSG_GET_SUBSCRIBER_NUMBER = 8;
// SS BLUETOOTH : add addtional msg
    private static final int MSG_QUERY_CALL_STATE = 9;
// SS BLUETOOTH : add addtional msg

    // match up with bthf_call_state_t of bt_hf.h
    private static final int CALL_STATE_ACTIVE = 0;
    private static final int CALL_STATE_HELD = 1;
    private static final int CALL_STATE_DIALING = 2;
    private static final int CALL_STATE_ALERTING = 3;
    private static final int CALL_STATE_INCOMING = 4;
    private static final int CALL_STATE_WAITING = 5;
    private static final int CALL_STATE_IDLE = 6;

    // match up with bthf_call_state_t of bt_hf.h
    // Terminate all held or set UDUB("busy") to a waiting call
    private static final int CHLD_TYPE_RELEASEHELD = 0;
    // Terminate all active calls and accepts a waiting/held call
    private static final int CHLD_TYPE_RELEASEACTIVE_ACCEPTHELD = 1;
    // Hold all active calls and accepts a waiting/held call
    private static final int CHLD_TYPE_HOLDACTIVE_ACCEPTHELD = 2;
    // Add all held calls to a conference
    private static final int CHLD_TYPE_ADDHELDTOCONF = 3;

    private int mNumActiveCalls = 0;
    private int mNumHeldCalls = 0;
    private int mBluetoothCallState = CALL_STATE_IDLE;
    private String mRingingAddress = null;
    private int mRingingAddressType = 0;
    private Call mOldHeldCall = null;
    private int mOtherNumHeldCalls = 0;

    /**
     * Binder implementation of IBluetoothHeadsetPhone. Implements the command interface that the
     * bluetooth headset code uses to control call.
     */
    private final IBluetoothHeadsetPhone.Stub mBinder = new IBluetoothHeadsetPhone.Stub() {
        @Override
        public boolean answerCall() throws RemoteException {
            enforceModifyPermission();
            Log.i(TAG, "BT - answering call");
            if(SystemDBInterface.isTPhoneMode() && SystemDBInterface.isTPhoneRelaxMode()){
                Log.w(TAG, "not support when TPhone RelaxMode");
                return false;
            }
            return sendSynchronousRequest(MSG_ANSWER_CALL);
        }

        @Override
        public boolean hangupCall() throws RemoteException {
            enforceModifyPermission();
            Log.i(TAG, "BT - hanging up call");
            if(SystemDBInterface.isTPhoneMode() && SystemDBInterface.isTPhoneRelaxMode()){
                Log.w(TAG, "not support when TPhone RelaxMode");
                return false;
            }
            return sendSynchronousRequest(MSG_HANGUP_CALL);
        }

        @Override
        public boolean sendDtmf(int dtmf) throws RemoteException {
            enforceModifyPermission();
            Log.i(TAG, "BT - sendDtmf %c", Log.DEBUG ? dtmf : '.');
            return sendSynchronousRequest(MSG_SEND_DTMF, dtmf);
        }

        @Override
        public String getNetworkOperator() throws RemoteException {
            Log.i(TAG, "getNetworkOperator");
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_GET_NETWORK_OPERATOR);
        }

        @Override
        public String getSubscriberNumber() throws RemoteException {
            Log.i(TAG, "getSubscriberNumber");
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_GET_SUBSCRIBER_NUMBER);
        }

        @Override
        public boolean listCurrentCalls() throws RemoteException {
            // only log if it is after we recently updated the headset state or else it can clog
            // the android log since this can be queried every second.
            boolean logQuery = mHeadsetUpdatedRecently;
            mHeadsetUpdatedRecently = false;

            if (logQuery) {
                Log.i(TAG, "listcurrentCalls");
            }
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_LIST_CURRENT_CALLS, logQuery ? 1 : 0);
        }

        @Override
        public boolean queryPhoneState() throws RemoteException {
            Log.i(TAG, "queryPhoneState");
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_QUERY_PHONE_STATE);
        }

        @Override
        public boolean processChld(int chld) throws RemoteException {
            Log.i(TAG, "processChld %d", chld);
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_PROCESS_CHLD, chld);
        }

        @Override
        public void updateBtHandsfreeAfterRadioTechnologyChange() throws RemoteException {
            Log.d(TAG, "RAT change");
            // deprecated
        }

        @Override
        public void cdmaSetSecondCallState(boolean state) throws RemoteException {
            Log.d(TAG, "cdma 1");
            // deprecated
        }

        @Override
        public void cdmaSwapSecondCallState() throws RemoteException {
            Log.d(TAG, "cdma 2");
            // deprecated
        }

// SS BLUETOOTH : add queryCallState
        @Override
        public boolean queryCallState() throws RemoteException {
            Log.i(TAG, "queryCallState()");
            enforceModifyPermission();
            return sendSynchronousRequest(MSG_QUERY_CALL_STATE);
        }
// SS BLUETOOTH : add queryCallState

// SS Bluetooth : add API for AT CMD start
        @Override
        public String getSubscriberId() {
            Log.i(TAG, "getSubscriberId()");
            String mSubscriberId = null;
            mSubscriberId = TelephonyManager.from(BluetoothPhoneService.this).getSubscriberId();

            return mSubscriberId;
        }

        @Override
        public String getDeviceId() {
            Log.i(TAG, "getDeviceId()");
            String mDeviceId = null;
            mDeviceId = TelephonyManager.from(BluetoothPhoneService.this).getDeviceId();

            return mDeviceId;
        }
//SS Bluetooth : add API for AT CMD end
    };

    /**
     * Main-thread handler for BT commands.  Since telecom logic runs on a single thread, commands
     * that are sent to it from the headset need to be moved over to the main thread before
     * executing. This handler exists for that reason.
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MainThreadRequest request = msg.obj instanceof MainThreadRequest ?
                    (MainThreadRequest) msg.obj : null;
            CallsManager callsManager = getCallsManager();
            Call call = null;
            int phoneId = -1;

            Log.d(TAG, "handleMessage(%d) / param %s",
                    msg.what, request == null ? null : request.param);

            switch (msg.what) {
                case MSG_ANSWER_CALL:
                    try {
                        if (PhoneFeature.hasFeature(PhoneFeature.MultiSIM.FEATURE_MULTISIM_DSDA)) {
                            phoneId = TelecomUtilsMultiSIM.getMultiSimDSDAForegoundPhoneId();
                            Log.d(TAG, "MSG_ANSWER_CALL! phoneId = %d", phoneId);
                            call = TelecomUtilsMultiSIM.getRingingCall(phoneId);
                            if (call == null) {
                                call = callsManager.getRingingCall();
                                Log.d(TAG, "MSG_ANSWER_CALL! get again call = " + call);
                            }
                        } else {
                            call = callsManager.getRingingCall();
                        }
                        if (call != null) {
// SS Bluetooth : fix AOSP
                            callsManager.answerCall(call, 0);
// SS Bluetooth : fix AOSP
                        }
                    } finally {
                        request.setResult(call != null);
                    }
                    break;

                case MSG_HANGUP_CALL:
                    try {
                        if (PhoneFeature.hasFeature(PhoneFeature.MultiSIM.FEATURE_MULTISIM_DSDA)) {
                            phoneId = TelecomUtilsMultiSIM.getMultiSimDSDAForegoundPhoneId();
                            Log.d(TAG, "MSG_HANGUP_CALL! phoneId = %d", phoneId );
                            Call otherSlotActiveCall = null;
                            Call currentSlotHeldCall = TelecomUtilsMultiSIM.getHeldCall(phoneId);
                            if(phoneId == 1) {
                                otherSlotActiveCall = TelecomUtilsMultiSIM.getActiveCall(0);
                                if (otherSlotActiveCall != null) {
                                    Log.d(TAG, "MSG_HANGUP_CALL! set mOtherNumHeldCalls to 1 phoneId = %d", phoneId );
                                    mOtherNumHeldCalls = 1;
                                    if(currentSlotHeldCall != null) {
                                        mOtherNumHeldCalls = 0;
                                        updateHeadsetWithCallState(true);
                                    }
                                }
                            } else {
                                otherSlotActiveCall = TelecomUtilsMultiSIM.getActiveCall(1);
                                if (otherSlotActiveCall != null) {
                                    Log.d(TAG, "MSG_HANGUP_CALL! set mOtherNumHeldCalls to 1 phoneId = %d", phoneId );
                                    mOtherNumHeldCalls = 1;
                                    if(currentSlotHeldCall != null) {
                                        mOtherNumHeldCalls = 0;
                                        updateHeadsetWithCallState(true);
                                    }
                                }
                            }
                            TelecomUtilsMultiSIM.setMultiSimLastRejectIncomingCallPhoneId(phoneId);
                        }
                        call = callsManager.getForegroundCall();
                        if (call != null) {
                            callsManager.disconnectCall(call);
                        }
                    } finally {
                        request.setResult(call != null);
                    }
                    break;

                case MSG_SEND_DTMF:
                    try {
                        call = callsManager.getForegroundCall();
                        if (call != null) {
                            // TODO: Consider making this a queue instead of starting/stopping
                            // in quick succession.
                            callsManager.playDtmfTone(call, (char) request.param);
                            callsManager.stopDtmfTone(call);
                        }
                    } finally {
                        request.setResult(call != null);
                    }
                    break;

                case MSG_PROCESS_CHLD:
                    Boolean result = false;
                    try {
                        result = processChld(request.param);
                    } finally {
                        request.setResult(result);
                    }
                    break;

                case MSG_GET_SUBSCRIBER_NUMBER:
                    String address = null;
                    try {
                        PhoneAccount account = getBestPhoneAccount();
                        if (account != null) {
                            Uri addressUri = account.getAddress();
                            if (addressUri != null) {
                                address = addressUri.getSchemeSpecificPart();
                            }
                        }

                        if (TextUtils.isEmpty(address)) {
                            address = TelephonyManager.from(BluetoothPhoneService.this)
                                    .getLine1Number();

                            if(address == null) {
                                // in case of No Network state(ex No SIM), it makes AT CMDs stuck.
                                Log.i(TAG, "address is null due to No Service.");
                                address = "";
                            }
                        }
                    } finally {
                        request.setResult(address);
                    }
                    break;

                case MSG_GET_NETWORK_OPERATOR:
                    String label = null;
                    try {
                        if(PhoneFeature.hasFeature(PhoneFeature.MultiSIM.FEATURE_MULTISIM)) {
                            long subId = SubscriptionManager.getDefaultVoiceSubId();
                            Log.d(TAG, "subId = %d", subId );
                            label = TelephonyManager.getDefault().getNetworkOperatorName(subId);
                        } else {
                            PhoneAccount account = getBestPhoneAccount();
                            if (account != null) {
                                label = account.getLabel().toString();
                            } else {
                                // Finally, just get the network name from telephony.
                                label = TelephonyManager.from(BluetoothPhoneService.this)
                                        .getNetworkOperatorName();
                            }
                        }
                    } finally {
                        // TODO: if needed, get NetworkOperatorName for MultiSim
                        request.setResult(label);
                    }
                    break;

                case MSG_LIST_CURRENT_CALLS:
                    try {
/* SS Bluetooth : Always print clcc log
                        sendListOfCalls(request.param == 1);
SS Bluetooth : Always print clcc log */
                        sendListOfCalls(true);
                    } finally {
                        request.setResult(true);
                    }
                    break;

                case MSG_QUERY_PHONE_STATE:
                    try {
                        updateHeadsetWithCallState(true /* force */);
                    } finally {
                        if (request != null) {
                            request.setResult(true);
                        }
                    }
                    break;
// SS BLUETOOTH : add addtional msg
                case MSG_QUERY_CALL_STATE:
                    try {
                        handleQueryCallState();
                    } finally {
                        if (request != null) {
                            request.setResult(true);
                        }
                    }
                    break;
// SS BLUETOOTH : add addtional msg
            }
        }
    };

    /**
     * Listens to call changes from the CallsManager and calls into methods to update the bluetooth
     * headset with the new states.
     */
    private CallsManagerListener mCallsManagerListener = new CallsManagerListenerBase() {
        @Override
        public void onCallAdded(Call call) {
            updateHeadsetWithCallState(false /* force */);
        }

        @Override
        public void onCallRemoved(Call call) {
            mClccIndexMap.remove(call);

// SS BLUETOOTH : when disconnect call on CDMA network, we will ignore first changing event.
            int phoneType = getCurrentCallPhoneType(call);
            if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                return;
            }
// SS BLUETOOTH : when disconnect call on CDMA network, we will ignore first changing event.

            updateHeadsetWithCallState(false /* force */);
        }

        @Override
        public void onCallStateChanged(Call call, int oldState, int newState) {
            // If a call is being put on hold because of a new connecting call, ignore the
            // CONNECTING since the BT state update needs to send out the numHeld = 1 + dialing
            // state atomically.
            // When the call later transitions to DIALING/DISCONNECTED we will then send out the
            // aggregated update.
            if (oldState == CallState.ACTIVE && newState == CallState.ON_HOLD) {
                for (Call otherCall : CallsManager.getInstance().getCalls()) {
                    if (otherCall.getState() == CallState.CONNECTING) {
                        return;
                    }
                }
            }

            // To have an active call and another dialing at the same time is an invalid BT
            // state. We can assume that the active call will be automatically held which will
            // send another update at which point we will be in the right state.
            if (CallsManager.getInstance().getActiveCall() != null
                    && oldState == CallState.CONNECTING && newState == CallState.DIALING) {
                return;
            }

// SS BLUETOOTH : when user swap, we will ignore first changing event.
            Collection<Call> mCalls = getCallsManager().getCalls();
            int parentCallSize = 0;
            int numParentActiveCall = 0;
            int numChildActiveCall = 0;
            int numChildRingingCall = 0;
            int numParentHeldCall = getCallsManager().getNumHeldCalls();
            boolean isConference = false;

            for (Call tempCall : mCalls) {
                if (tempCall.getParentCall() == null) {
                    parentCallSize++;
                    if (tempCall.getState() == CallState.ACTIVE) {
                        numParentActiveCall++;
                    }
                }
                if (tempCall.isConference()) {
                    isConference = true;
                    continue;
                } else if (tempCall.getState() == CallState.ACTIVE) {
                    numChildActiveCall++;
                } else if (tempCall.getState() == CallState.RINGING) {
                    numChildRingingCall++;
                }
            }

            Log.d(TAG, "old : " + oldState + ", new : " + newState + ", numPCall : " + parentCallSize
                    + ", numPActive : " + numParentActiveCall + ", numCActive : " + numChildActiveCall + ", numPHeld : " + numParentHeldCall
                    + ", numCRinging : " + numChildRingingCall + ", isConference : " + isConference);

            if (oldState == CallState.ACTIVE && newState == CallState.ON_HOLD
                    || oldState == CallState.ON_HOLD && newState == CallState.ACTIVE) {
                if (parentCallSize > 1) {
                    if (call.getParentCall() != null) {
                        Log.d(TAG, "Skip childCall state change");
                        return;
                    } else {
                        if (parentCallSize == numParentHeldCall) {
                            Log.d(TAG, "Now swaping active to hold, update when hold to active");
                            return;
                        } else if (parentCallSize == (numChildRingingCall + numParentHeldCall)) {
                            Log.d(TAG, "Now answer waiting call, update when waiting to active");
                            return;
                        } else if (!isConference && (parentCallSize == numParentActiveCall)) {
                            Log.d(TAG, "Now swaping hold to active, update when active to hold");
                            return;
                        }
                    }
                } else if (parentCallSize == 1 && numParentActiveCall == 0 && numChildActiveCall > 0) {
                    if (call.getParentCall() != null) {
                        Log.d(TAG, "When merge call to conference call, wait getActiveCall is not null");
                        return;
                    }
                }
            }
// SS BLUETOOTH : when user swap, we will ignore first changing event.

// SS BLUETOOTH : when disconnect call on CDMA network, we will ignore first changing event except Ringing.
            int phoneType = getCurrentCallPhoneType(call);
            if (phoneType == TelephonyManager.PHONE_TYPE_CDMA
                && oldState != CallState.RINGING && newState == CallState.DISCONNECTED) {
                if (numParentHeldCall + numParentActiveCall > 0) {
                    Log.d(TAG, "When disconnect call while conference call on CDMA network, update when all call to disconnect");
                    return;
                }
            }
// SS BLUETOOTH : when disconnect call on CDMA network, we will ignore first changing event except Ringing.

            updateHeadsetWithCallState(false /* force */);
        }

        @Override
        public void onForegroundCallChanged(Call oldForegroundCall, Call newForegroundCall) {
            // The BluetoothPhoneService does not need to respond to changes in foreground calls,
            // which are always accompanied by call state changes anyway.
        }

        @Override
        public void onIsConferencedChanged(Call call) {
            /*
             * Filter certain onIsConferencedChanged callbacks. Unfortunately this needs to be done
             * because conference change events are not atomic and multiple callbacks get fired
             * when two calls are conferenced together. This confuses updateHeadsetWithCallState
             * if it runs in the middle of two calls being conferenced and can cause spurious and
             * incorrect headset state updates. One of the scenarios is described below for CDMA
             * conference calls.
             *
             * 1) Call 1 and Call 2 are being merged into conference Call 3.
             * 2) Call 1 has its parent set to Call 3, but Call 2 does not have a parent yet.
             * 3) updateHeadsetWithCallState now thinks that there are two active calls (Call 2 and
             * Call 3) when there is actually only one active call (Call 3).
             */
            if (call.getParentCall() != null) {
                // If this call is newly conferenced, ignore the callback. We only care about the
                // one sent for the parent conference call.
// SS BLUETOOTH : Log fix
                Log.d(TAG, "Ignoring onIsConferenceChanged from child call with new parent");
// SS BLUETOOTH : Log fix
                return;
            }

            if (call.getChildCalls().size() == 1) {
                // If this is a parent call with only one child, ignore the callback as well since
                // the minimum number of child calls to start a conference call is 2. We expect
                // this to be called again when the parent call has another child call added.
// SS BLUETOOTH : Log fix
                Log.d(TAG, "Ignoring onIsConferenceChanged from parent with only one child call");
// SS BLUETOOTH : Log fix
                return;
            }

/* SS BLUETOOTH : Do not anything, Conference is managed by onCallStateChanged too.
            updateHeadsetWithCallState(false);
SS BLUETOOTH : Do not anything, Conference is managed by onCallStateChanged too. */
            Log.d(TAG, "Do not anything on onIsConferenceChanged");
            return;
        }
    };

    /**
     * Listens to connections and disconnections of bluetooth headsets.  We need to save the current
     * bluetooth headset so that we know where to send call updates.
     */
    private BluetoothProfile.ServiceListener mProfileListener =
            new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            mBluetoothHeadset = (BluetoothHeadset) proxy;
        }

        @Override
        public void onServiceDisconnected(int profile) {
            mBluetoothHeadset = null;
        }
    };

    /**
     * Receives events for global state changes of the bluetooth adapter.
     */
    private final BroadcastReceiver mBluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            Log.d(TAG, "Bluetooth Adapter state: %d", state);
            if (state == BluetoothAdapter.STATE_ON) {
                mHandler.sendEmptyMessage(MSG_QUERY_PHONE_STATE);
            }
        }
    };

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
// SS BLUETOOTH : for Voip Call
    private BluetoothVoIPService mVoipService;
    public static int mLastIndex = 0;
// SS BLUETOOTH : for Voip Call

    // A map from Calls to indexes used to identify calls for CLCC (C* List Current Calls).
    private Map<Call, Integer> mClccIndexMap = new HashMap<>();

    private boolean mHeadsetUpdatedRecently = false;

    public BluetoothPhoneService() {
        Log.v(TAG, "Constructor");
    }

    public static final void start(Context context) {
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            context.startService(new Intent(context, BluetoothPhoneService.class));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Binding service");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

// SS BLUETOOTH : for Voip Call
        mVoipService = new BluetoothVoIPService();
// SS BLUETOOTH : for Voip Call
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "BluetoothPhoneService shutting down, no BT Adapter found.");
            return;
        }
        mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothAdapterReceiver, intentFilter);

        CallsManager.getInstance().addListener(mCallsManagerListener);
        updateHeadsetWithCallState(false /* force */);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        CallsManager.getInstance().removeListener(mCallsManagerListener);
        super.onDestroy();
    }

    private boolean processChld(int chld) {
        CallsManager callsManager = CallsManager.getInstance();
        Call activeCall = callsManager.getActiveCall();
        Call ringingCall = callsManager.getRingingCall();
        Call heldCall = callsManager.getHeldCall();

// SS BLUETOOTH : Log fix
        // TODO: Keeping as Log.i for now.  Move to Log.d after L release if BT proves stable.
        Log.i(TAG, "Active: %s\n"
                + TAG + ": Ringing: %s\n"
                + TAG + ": Held: %s", activeCall, ringingCall, heldCall);
// SS BLUETOOTH : Log fix

// SS BLUETOOTH : ECC
        Log.i(TAG, "CHLD Value is : " + chld);
        int idx = 0;
        boolean result = false;
        int phoneType = getCurrentCallPhoneType(null);

        if(chld > 4) {    // Call processChldIdx when CHLD > 4.
            if(chld < 10) // Since CLCC call index starts from 1, CHLD can start from 11 or 21 for ECC
            {
                Log.w(TAG,"Invlaid CHLD");
                return false;
            } else {    // Extract the idx value from CHLD
                idx = (chld%10);
                chld = (chld/10);

                if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) { // check
                    Log.w(TAG,"CDMA Network can't control ECC");
                } else {
                    result = processChldIdx(chld,idx);
                    return result;
                }
            }
        }
// SS BLUETOOTH : ECC

        if (chld == CHLD_TYPE_RELEASEHELD) {
            if (ringingCall != null) {
                callsManager.rejectCall(ringingCall, false, null);
                return true;
            } else if (heldCall != null) {
                callsManager.disconnectCall(heldCall);
                return true;
            }
// SS BLUETOOTH : CHLD, no action, but return true
            return true;
// SS BLUETOOTH : CHLD, no action, but return true
        } else if (chld == CHLD_TYPE_RELEASEACTIVE_ACCEPTHELD) {
            if (activeCall != null) {
                callsManager.disconnectCall(activeCall);
                if (ringingCall != null) {
// SS BLUETOOTH : On CDMA network, when disconnect 1 call, all call is disconnected
                    if (getCurrentCallPhoneType(ringingCall) == TelephonyManager.PHONE_TYPE_CDMA) {
                        callsManager.disconnectCall(ringingCall);
// SS BLUETOOTH : On CDMA network, when disconnect 1 call, all call is disconnected
                    } else {
                        callsManager.answerCall(ringingCall, 0);
                    }
                } else if (heldCall != null) {
                    //callsManager.unholdCall(heldCall); //P141112-05579
                }
                return true;
// SS BLUETOOTH : L AOSP bug fix, ringing/hoid call should be accept
            } else {
                if (ringingCall != null) {
                    callsManager.answerCall(ringingCall, 0);
                } else if (heldCall != null) {
                    callsManager.unholdCall(heldCall);
                }
                return true;
            }
// SS BLUETOOTH : L AOSP bug fix, ringing/hoid call should be accept
        } else if (chld == CHLD_TYPE_HOLDACTIVE_ACCEPTHELD) {
/* [temp_wonder] Wait RIL/Telecomm modification
            if (activeCall != null && activeCall.can(PhoneCapabilities.SWAP_CONFERENCE)) {
                activeCall.swapConference();
                return true;
            } else
[temp_wonder] */
            if (ringingCall != null) {
                callsManager.answerCall(ringingCall, 0);
                return true;
            } else if (heldCall != null) {
                // CallsManager will hold any active calls when unhold() is called on a
                // currently-held call.
                callsManager.unholdCall(heldCall);
                return true;
            } else if (activeCall != null && activeCall.can(PhoneCapabilities.HOLD)) {
                callsManager.holdCall(activeCall);
                return true;
            }
// SS BLUETOOTH : CHLD, no action, but return true
            return true;
// SS BLUETOOTH : CHLD, no action, but return true
        } else if (chld == CHLD_TYPE_ADDHELDTOCONF) {
            if (activeCall != null) {
/* [temp wonder] Wait RIL/Telecomm modification
                if (activeCall.can(PhoneCapabilities.MERGE_CONFERENCE)) {
                    activeCall.mergeConference();
                    return true;
                } else {
[temp wonder] */
                    List<Call> conferenceable = activeCall.getConferenceableCalls();
                    if (!conferenceable.isEmpty()) {
                        callsManager.conference(activeCall, conferenceable.get(0));
                        return true;
                    }
// [temp wonder]               }
            }
// SS BLUETOOTH : CHLD, no action, but return true
            return true;
// SS BLUETOOTH : CHLD, no action, but return true
        }
        return false;
    }

// SS BLUETOOTH : ECC
    // To Process CHLD with IDX value for ECC support.
    // n.keshri, prabhu.mc
    public synchronized boolean processChldIdx(int chld, int idx) {
        Log.i(TAG, "Process CHLD Idx, Chld :" + chld + " idx :" + idx);
        enforceModifyPermission();
        CallsManager callsManager = getCallsManager();
        Call foregroundCall = callsManager.getForegroundCall();
        boolean isForeground = false;
        boolean isConference = false;

        Collection<Call> mCalls = callsManager.getCalls();
        Call mCall = null;
        for (Call call : mCalls) {
            // We don't send the parent conference call to the bluetooth device.
            if (!call.isConference()) {
                isForeground = foregroundCall == call;
                if (convertCallState(call.getState(), isForeground) != CALL_STATE_IDLE) {
                    if (idx == getIndexForCall(call)) {
                        mCall = call;
                        continue;
                    }
                }
            }
        }

        if (mCall == null) {
            Log.w(TAG, "Wrong idx value");
            return false;
        } else {
            isConference = (mCall.getParentCall() != null) ?  true : false;
        }

        if (chld == CHLD_TYPE_RELEASEACTIVE_ACCEPTHELD) {  //process CHLD = 1<idx>
            Log.i(TAG, "Trying to hangup " + mCall.getHandle().getSchemeSpecificPart());
            callsManager.disconnectCall(mCall);
            return true;
        } else if (chld == CHLD_TYPE_HOLDACTIVE_ACCEPTHELD) { //process CHLD = 2<idx>
            Log.i(TAG, "Hold all Calls except, idx : " + idx);
            if (mCall.getState() == CallState.RINGING
                    || mCall.getState() == CallState.ON_HOLD) {
                Log.w(TAG, "Idx call is waiting or held call object");
                return true; // no action, but return true
            }

            Log.d(TAG, "foregroundCall: %s / isConference: " + isConference, foregroundCall);
            if (foregroundCall != null && isConference) {
                Log.i(TAG, "Heldcall Num : " + callsManager.getNumHeldCalls());
                if (callsManager.getNumHeldCalls() > 0) {
                    Log.i(TAG, "Merging calls");
/* [temp_wonder] Wait RIL/Telecomm modification
                    if (foregroundCall.can(PhoneCapabilities.MERGE_CONFERENCE)) {
                        foregroundCall.mergeConference();
                    } else {
[temp_wonder] */
                        List<Call> conferenceable = foregroundCall.getConferenceableCalls();
                        if (!conferenceable.isEmpty()) {
                            callsManager.conference(foregroundCall, conferenceable.get(0));
                       }
// [temp_wonder]                   }
                    Log.i(TAG, "Separate the Call : " + mCall.getHandle().getSchemeSpecificPart());
                    mCall.splitFromConference();
                    return true;
                }
                Log.i(TAG, "Separate the Call : " + mCall.getHandle().getSchemeSpecificPart());
                mCall.splitFromConference();
                return true;
            }
// SS BLUETOOTH : CHLD, no action, but return true
            else {
                Log.i(TAG, "ForegroundCall is null or idx call is not conference");
                return true;
            }
// SS BLUETOOTH : CHLD, no action, but return true
        } else {
            Log.i(TAG, "not supported ECC chld");
            return false;
        }
    }
// SS BLUETOOTH : ECC

    private void enforceModifyPermission() {
        enforceCallingOrSelfPermission(android.Manifest.permission.MODIFY_PHONE_STATE, null);
    }

    private <T> T sendSynchronousRequest(int message) {
        return sendSynchronousRequest(message, 0);
    }

    private <T> T sendSynchronousRequest(int message, int param) {
        MainThreadRequest request = new MainThreadRequest(param);
        mHandler.obtainMessage(message, request).sendToTarget();
        synchronized (request) {
            while (request.result == null) {
                try {
                    request.wait();
                } catch (InterruptedException e) {
                    // Do nothing, go back and wait until the request is complete.
                }
            }
        }
        if (request.result != null) {
            @SuppressWarnings("unchecked")
            T retval = (T) request.result;
            return retval;
        }
        return null;
    }

    private void sendListOfCalls(boolean shouldLog) {
// SS BLUETOOTH : for Voip Call
        mLastIndex = 0;
// SS BLUETOOTH : for Voip Call

        Collection<Call> mCalls = getCallsManager().getCalls();
        for (Call call : mCalls) {
            // We don't send the parent conference call to the bluetooth device.
            if (!call.isConference()) {
                sendClccForCall(call, shouldLog);
            }
        }
        sendClccEndMarker();
    }

    /**
     * Sends a single clcc (C* List Current Calls) event for the specified call.
     */
    private void sendClccForCall(Call call, boolean shouldLog) {
        boolean isForeground = getCallsManager().getForegroundCall() == call;
        int state = convertCallState(call.getState(), isForeground);
        boolean isPartOfConference = false;

        if (state == CALL_STATE_IDLE) {
            return;
        }

/* SS Bluetooth : fix AOSP (Backout LRX06C Patch, CL3015183)
        Call conferenceCall = call.getParentCall();
        if (conferenceCall != null) {
            isPartOfConference = true;

            // Run some alternative states for Conference-level merge/swap support.
            // Basically, if call supports swapping or merging at the conference-level, then we need
            // to expose the calls as having distinct states (ACTIVE vs HOLD) or the functionality
            // won't show up on the bluetooth device.

            // Before doing any special logic, ensure that we are dealing with an ACTIVE call and
            // that the conference itself has a notion of the current "active" child call.
            Call activeChild = conferenceCall.getConferenceLevelActiveCall();
            if (state == CALL_STATE_ACTIVE && activeChild != null) {
                // Reevaluate state if we can MERGE or if we can SWAP without previously having
                // MERGED.
                boolean shouldReevaluateState =
                        conferenceCall.can(PhoneCapabilities.MERGE_CONFERENCE) ||
                        (conferenceCall.can(PhoneCapabilities.SWAP_CONFERENCE) &&
                         !conferenceCall.wasConferencePreviouslyMerged());

                if (shouldReevaluateState) {
                    isPartOfConference = false;
                    if (call == activeChild) {
                        state = CALL_STATE_ACTIVE;
                    } else {
                        // At this point we know there is an "active" child and we know that it is
                        // not this call, so set it to HELD instead.
                        state = CALL_STATE_HELD;
                    }
                }
            }
        }
*/
        isPartOfConference = (call.getParentCall() != null);
// SS Bluetooth : fix AOSP (Backout LRX06C Patch, CL3015183)

        int index = getIndexForCall(call);
        int direction = call.isIncoming() ? 1 : 0;
        final Uri addressUri;
        if (call.getGatewayInfo() != null) {
            addressUri = call.getGatewayInfo().getOriginalAddress();
        } else {
            addressUri = call.getHandle();
        }
        String address = addressUri == null ? null : addressUri.getSchemeSpecificPart();
        int addressType = address == null ? -1 : PhoneNumberUtils.toaFromString(address);

        if (shouldLog) {
// SS BLUETOOTH : Log fix
            Log.i(TAG, "sending clcc for call %d, %d, %d, %b, %s, %d",
                    index, direction, state, isPartOfConference, Log.piiHandle(address),
                    addressType);
// SS BLUETOOTH : Log fix
        }

        if (mBluetoothHeadset != null) {
            mBluetoothHeadset.clccResponse(
                index, direction, state, 0, isPartOfConference, address, addressType);
// SS BLUETOOTH : for Voip Call
            mLastIndex = index +1;
// SS BLUETOOTH : for Voip Call
        }
    }

    private void sendClccEndMarker() {
        // End marker is recognized with an index value of 0. All other parameters are ignored.
        if (mBluetoothHeadset != null) {
            mBluetoothHeadset.clccResponse(0 /* index */, 0, 0, 0, false, null, 0);
        }
    }

    /**
     * Returns the caches index for the specified call.  If no such index exists, then an index is
     * given (smallest number starting from 1 that isn't already taken).
     */
    private int getIndexForCall(Call call) {
        if (mClccIndexMap.containsKey(call)) {
            return mClccIndexMap.get(call);
        }

        int i = 1;  // Indexes for bluetooth clcc are 1-based.
        while (mClccIndexMap.containsValue(i)) {
            i++;
        }

        // NOTE: Indexes are removed in {@link #onCallRemoved}.
        mClccIndexMap.put(call, i);
        return i;
    }

    /**
     * Sends an update of the current call state to the current Headset.
     *
     * @param force {@code true} if the headset state should be sent regardless if no changes to the
     *      state have occurred, {@code false} if the state should only be sent if the state has
     *      changed.
     */
    private void updateHeadsetWithCallState(boolean force) {
// SS BLUETOOTH : add Log
        Log.i(TAG, "updateHeadsetWithCallState");
// SS BLUETOOTH : add Log

        CallsManager callsManager = getCallsManager();
        Call activeCall = callsManager.getActiveCall();
        Call ringingCall = callsManager.getRingingCall();
        Call heldCall = callsManager.getHeldCall();

        int phoneId = -1;
        if (PhoneFeature.hasFeature(PhoneFeature.MultiSIM.FEATURE_MULTISIM_DSDA)) {
            if(mOtherNumHeldCalls == 1) {
                Log.d(TAG, "updateHeadsetWithCallState! mOtherNumHeldCalls==1 phoneId = %d", phoneId );
                mOtherNumHeldCalls = 0;
                mBluetoothHeadset.phoneStateChanged(0, 1, CALL_STATE_ACTIVE, "", 0);
                return;
            }
            phoneId = TelecomUtilsMultiSIM.getMultiSimDSDAForegoundPhoneId();
            Log.d(TAG, "updateHeadsetWithCallState! phoneId = %d", phoneId);
            if (activeCall == null && ringingCall == null && heldCall == null) {
                Log.d(TAG, "updateHeadsetWithCallState!  activeCall,ringingCall,heldCall == null!!  phoneId = %d", phoneId );
                activeCall = TelecomUtilsMultiSIM.getActiveCall(phoneId);
                ringingCall = TelecomUtilsMultiSIM.getRingingCall(phoneId);
                heldCall = TelecomUtilsMultiSIM.getHeldCall(phoneId);
            }
        }

        int bluetoothCallState = getBluetoothCallStateForUpdate();

        String ringingAddress = null;
        int ringingAddressType = 128;
        if (ringingCall != null && ringingCall.getHandle() != null) {
            ringingAddress = ringingCall.getHandle().getSchemeSpecificPart();
            if (ringingAddress != null) {
                ringingAddressType = PhoneNumberUtils.toaFromString(ringingAddress);
            }
        }
        if (ringingAddress == null) {
            ringingAddress = "";
        }

        int numActiveCalls = activeCall == null ? 0 : 1;
        int numHeldCalls = callsManager.getNumHeldCalls();

        if (SystemDBInterface.isTPhoneMode() && SystemDBInterface.isTPhoneRelaxMode()) {
            Log.w(TAG, "Do not play the BluetoothHeadset ringtone when TPhone RelaxMode.");
            return;
        }

        if (PhoneFeature.hasFeature(PhoneFeature.MultiSIM.FEATURE_MULTISIM_DSDA)) {
            if (numActiveCalls == 0 && numHeldCalls == 0) {
                Log.d(TAG, "updateHeadsetWithCallState! numActiveCalls,numHeldCalls == 0");
                if(phoneId == 0) {
                    Log.d(TAG, "updateHeadsetWithCallState! change phoneId to 1 %d", phoneId);
                    activeCall = TelecomUtilsMultiSIM.getActiveCall(1);
                    ringingCall = TelecomUtilsMultiSIM.getRingingCall(1);
                    heldCall = TelecomUtilsMultiSIM.getHeldCall(1);
                } else if (phoneId == 1) {
                    Log.d(TAG, "updateHeadsetWithCallState! change phoneId to 0 %d", phoneId);
                    activeCall = TelecomUtilsMultiSIM.getActiveCall(0);
                    ringingCall = TelecomUtilsMultiSIM.getRingingCall(0);
                    heldCall = TelecomUtilsMultiSIM.getHeldCall(0);
                }
                numActiveCalls = activeCall == null ? 0 : 1;
                numHeldCalls = callsManager.getNumHeldCalls();
            }
        }

/* SS Bluetooth : fix AOSP (Backout LRX06C/LRX21K Patch, CL3015183/CL3125951)
        // For conference calls which support swapping the active call within the conference
        // (namely CDMA calls) we need to expose that as a held call in order for the BT device
        // to show "swap" and "merge" functionality.
        boolean ignoreHeldCallChange = false;
        if (activeCall != null && activeCall.isConference()) {
            if (activeCall.can(PhoneCapabilities.SWAP_CONFERENCE)) {
                // Indicate that BT device should show SWAP command by indicating that there is a
                // call on hold, but only if the conference wasn't previously merged.
                numHeldCalls = activeCall.wasConferencePreviouslyMerged() ? 0 : 1;
            } else if (activeCall.can(PhoneCapabilities.MERGE_CONFERENCE)) {
                numHeldCalls = 1;  // Merge is available, so expose via numHeldCalls.
            }

            for (Call childCall : activeCall.getChildCalls()) {
                // Held call has changed due to it being combined into a CDMA conference. Keep
                // track of this and ignore any future update since it doesn't really count as
                // a call change.
                if (mOldHeldCall == childCall) {
                    ignoreHeldCallChange = true;
                    break;
                }
            }
        }
SS Bluetooth : fix AOSP (Backout LRX06C/LRX21K Patch, CL3015183/CL3125951) */

        if (mBluetoothHeadset != null &&
                (numActiveCalls != mNumActiveCalls ||
                numHeldCalls != mNumHeldCalls ||
                bluetoothCallState != mBluetoothCallState ||
                !TextUtils.equals(ringingAddress, mRingingAddress) ||
                ringingAddressType != mRingingAddressType ||
                (heldCall != mOldHeldCall /*&& !ignoreHeldCallChange // SS Bluetooth : fix AOSP (Backout LRX06C/LRX21K Patch, CL3015183/CL3125951)*/) ||
                force)) {
// SS BLUETOOTH : For VoIP Waiting Call Control
            int mforegroundVoIPCall = mVoipService.foregroundVoIPCall;
            int mbackgroundVoIPCall = mVoipService.backgroundVoIPCall;

            if (mforegroundVoIPCall != CALL_STATE_IDLE
                    || mbackgroundVoIPCall != CALL_STATE_IDLE) {
                Log.i(TAG, "foregroundVoIPCall : " + mforegroundVoIPCall
                        + ", backgroundVoIPCall : " + mbackgroundVoIPCall);

                int mForegroundCallState = CallState.NEW;
                Call foregroundCall = callsManager.getForegroundCall();
                if (foregroundCall != null) {
                    mForegroundCallState = foregroundCall.getState();
                }
                Log.i(TAG, "mForegroundCallState : " + mForegroundCallState
                        + ", bluetoothCallState : " + bluetoothCallState);

                if (mforegroundVoIPCall == CALL_STATE_ACTIVE
                        && mBluetoothCallState == CALL_STATE_INCOMING) {
                    Log.d(TAG, "VoIP Call is ACTIVE. Send +CIEV 7,2, +CIEV:2,1");
                    mBluetoothHeadset.phoneStateChanged(0, 1, CALL_STATE_INCOMING, "", 0);
                    return;
                } else if (mbackgroundVoIPCall == CALL_STATE_HELD
                        && mForegroundCallState == CallState.ACTIVE) {
                    Log.d(TAG, "VoIP Call is HOLDING. +CIEV:7,1");
                    mBluetoothHeadset.phoneStateChanged(1, 1, CALL_STATE_IDLE, "", 0);
                    return;
                } else if ((mbackgroundVoIPCall == CALL_STATE_HELD && mForegroundCallState == CallState.DISCONNECTED)
                        || (mbackgroundVoIPCall == CALL_STATE_HELD && mBluetoothCallState == CALL_STATE_IDLE) // Reject incomming CS call
                        || (mforegroundVoIPCall == CALL_STATE_ACTIVE && mForegroundCallState == CallState.NEW)) { //CS Call Terminated
                    Log.d(TAG, "Resuming VoIP Call. Do not Send CIEV 1,0");
                    return;
                } else if  (mbackgroundVoIPCall == CALL_STATE_HELD
                        && mForegroundCallState == CallState.NEW) { //Reject incomming CS call
                    Log.d(TAG, "Reject incomming CS Call. Send CIEV 2,0");
                    mBluetoothHeadset.phoneStateChanged(0, 1, CALL_STATE_IDLE, "", 0);  //+CIEV:2,0
                    return;
                } else if (mbackgroundVoIPCall == CALL_STATE_HELD
                        && mForegroundCallState == CallState.DISCONNECTING) {    //Reject incomming CS call
                    Log.d(TAG, "Disconnecting CS Call");
                    return;
                }
            }
// SS BLUETOOTH : For VoIP Waiting Call Control

            // If the call is transitioning into the alerting state, send DIALING first.
            // Some devices expect to see a DIALING state prior to seeing an ALERTING state
            // so we need to send it first.
            boolean sendDialingFirst = mBluetoothCallState != bluetoothCallState &&
                    bluetoothCallState == CALL_STATE_ALERTING;

            mOldHeldCall = heldCall;
            mNumActiveCalls = numActiveCalls;
            mNumHeldCalls = numHeldCalls;
            mBluetoothCallState = bluetoothCallState;
            mRingingAddress = ringingAddress;
            mRingingAddressType = ringingAddressType;

            if (sendDialingFirst) {
// SS BLUETOOTH : add log
                Log.i(TAG, "Sending dialing state");
// SS BLUETOOTH : add log
                // Log in full to make logs easier to debug.
                Log.i(TAG, "updateHeadsetWithCallState " +
                        "numActive %s, " +
                        "numHeld %s, " +
                        "callState %s, " +
                        "ringing number %s, " +
                        "ringing type %s",
                        mNumActiveCalls,
                        mNumHeldCalls,
                        CALL_STATE_DIALING,
                        Log.pii(mRingingAddress),
                        mRingingAddressType);
                mBluetoothHeadset.phoneStateChanged(
                        mNumActiveCalls,
                        mNumHeldCalls,
                        CALL_STATE_DIALING,
                        mRingingAddress,
                        mRingingAddressType);

// SS BLUETOOTH : Send fake +CLCC if UconnectClccCarkit
                Log.i(TAG, "Send fake +CLCC for CDMA if UconnectClccCarkit");
                mLastIndex = 0;
                sendListOfCalls(true); // Sending ALERTING, but should be sent DIALING
// SS BLUETOOTH : Send fake +CLCC if UconnectClccCarkit
            }

            Log.i(TAG, "updateHeadsetWithCallState " +
                    "numActive %s, " +
                    "numHeld %s, " +
                    "callState %s, " +
                    "ringing number %s, " +
                    "ringing type %s",
                    mNumActiveCalls,
                    mNumHeldCalls,
                    mBluetoothCallState,
                    Log.pii(mRingingAddress),
                    mRingingAddressType);

            mBluetoothHeadset.phoneStateChanged(
                    mNumActiveCalls,
                    mNumHeldCalls,
                    mBluetoothCallState,
                    mRingingAddress,
                    mRingingAddressType);

            mHeadsetUpdatedRecently = true;
        }
    }

    private int getBluetoothCallStateForUpdate() {
        CallsManager callsManager = getCallsManager();
        Call ringingCall = callsManager.getRingingCall();
        Call dialingCall = callsManager.getDialingCall();

        //
        // !! WARNING !!
        // You will note that CALL_STATE_WAITING, CALL_STATE_HELD, and CALL_STATE_ACTIVE are not
        // used in this version of the call state mappings.  This is on purpose.
        // phone_state_change() in btif_hf.c is not written to handle these states. Only with the
        // listCalls*() method are WAITING and ACTIVE used.
        // Using the unsupported states here caused problems with inconsistent state in some
        // bluetooth devices (like not getting out of ringing state after answering a call).
        //
        int bluetoothCallState = CALL_STATE_IDLE;
        if (ringingCall != null) {
            bluetoothCallState = CALL_STATE_INCOMING;
        } else if (dialingCall != null) {
            bluetoothCallState = CALL_STATE_ALERTING;
        }
        return bluetoothCallState;
    }

    private int convertCallState(int callState, boolean isForegroundCall) {
        switch (callState) {
            case CallState.NEW:
            case CallState.ABORTED:
            case CallState.DISCONNECTED:
            case CallState.CONNECTING:
            case CallState.PRE_DIAL_WAIT:
                return CALL_STATE_IDLE;

            case CallState.ACTIVE:
                return CALL_STATE_ACTIVE;

            case CallState.DIALING:
                // Yes, this is correctly returning ALERTING.
                // "Dialing" for BT means that we have sent information to the service provider
                // to place the call but there is no confirmation that the call is going through.
                // When there finally is confirmation, the ringback is played which is referred to
                // as an "alert" tone, thus, ALERTING.
                // TODO: We should consider using the ALERTING terms in Telecom because that
                // seems to be more industry-standard.
                return CALL_STATE_ALERTING;

            case CallState.ON_HOLD:
                return CALL_STATE_HELD;

            case CallState.RINGING:
                if (isForegroundCall) {
                    return CALL_STATE_INCOMING;
                } else {
                    return CALL_STATE_WAITING;
                }
        }
        return CALL_STATE_IDLE;
    }

    private CallsManager getCallsManager() {
        return CallsManager.getInstance();
    }

    /**
     * Returns the best phone account to use for the given state of all calls.
     * First, tries to return the phone account for the foreground call, second the default
     * phone account for PhoneAccount.SCHEME_TEL.
     */
    private PhoneAccount getBestPhoneAccount() {
        TelecomApp app = (TelecomApp) getApplication();
        PhoneAccountRegistrar registry = app.getPhoneAccountRegistrar();
        Call call = getCallsManager().getForegroundCall();

        PhoneAccount account = null;
        if (call != null) {
            // First try to get the network name of the foreground call.
            account = registry.getPhoneAccount(call.getTargetPhoneAccount());
        }

        if (account == null) {
            // Second, Try to get the label for the default Phone Account.
            account = registry.getPhoneAccount(
                    registry.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL));
        }
        return account;
    }

// SS BLUETOOTH : add addtional function
    private void handleQueryCallState() {
        if (mBluetoothHeadset != null) {
            mBluetoothHeadset.updateCallState(mNumActiveCalls, mNumHeldCalls,
                    mBluetoothCallState);
        }
    }

    private int getCurrentCallPhoneType(Call call) {
        int Phonetype = TelephonyManager.getDefault().getPhoneType();
        Log.i(TAG, "getCurrentCallPhoneType, Defalut Phonetype : " + Phonetype);

        CallsManager callsManager = getCallsManager();
        if (call != null) {
            Phonetype = TelecomUtils.getPhoneTypeFromCall(call);
        } else if (callsManager.getForegroundCall() != null) {
            Phonetype = TelecomUtils.getPhoneTypeFromCall(callsManager.getForegroundCall());
        }

        Log.i(TAG, "getCurrentCallPhoneType, Current Phonetype : " + Phonetype);
        return Phonetype;
    }
// SS BLUETOOTH : add addtional function
}
