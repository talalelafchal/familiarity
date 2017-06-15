package com.palmsnipe.fingerprint;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/*
 * Inspired from the Cordova and React versions:
 *   - https://github.com/mjwheatley/cordova-plugin-android-fingerprint-auth
 *   - https://github.com/jariz/react-native-fingerprint-android
 */

public class FingerprintModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String DIALOG_FRAGMENT_TAG = "FpAuthDialog";
    private static final String TAG = "silta";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    public static String packageName;

    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    public static KeyStore mKeyStore;
    public static KeyGenerator mKeyGenerator;
    public static Cipher mCipher;
    private ReactApplicationContext mReactContext;
    private FingerprintManager.CryptoObject mCryptoObject;

    private FingerprintAuthenticationDialogFragment mDialog;

    public static int mMaxAttempts = 6;  // one more than the device default to prevent a 2nd callback
    public static boolean mDisableBackup = true;
    private String mLangCode = "en_US";
    public static String mDialogTitle;
    public static String mDialogMessage;
    public static String mDialogHint;
    public static String mDialogCancel;

    //    Used in authenticate without UI
    private CancellationSignal mCancellationSignal;
    private boolean mIsCancelled = false;

    /**
     * Alias for our key in the Android Key Store
     */
    private static String mClientId;
    /**
     * Used to encrypt token
     */
    private static String mClientSecret;

    private static Callback mAuthCallback;
    private static Callback mErrorCallback;

    public FingerprintModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mReactContext = reactContext;

        reactContext.addLifecycleEventListener(this);

        packageName = reactContext.getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mKeyguardManager = (KeyguardManager) reactContext.getSystemService(Activity.KEYGUARD_SERVICE);
            mFingerprintManager = (FingerprintManager) reactContext.getSystemService(Activity.FINGERPRINT_SERVICE);

            try {
                mKeyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                mKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE);

            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
            } catch (KeyStoreException e) {
                throw new RuntimeException("Failed to get an instance of KeyStore", e);
            }

            try {
                mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException("Failed to get an instance of Cipher", e);
            }
        }
    }

    @Override
    public String getName() {
        return "Fingerprint";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @ReactMethod
    public void authenticate(ReadableMap map, Callback successCallback, Callback errorCallback) {
        if (map != null && map.hasKey("clientId") && map.hasKey("clientSecret")) {
            mClientId = map.getString("clientId");
            mClientSecret = map.getString("clientSecret");

            mAuthCallback = successCallback;
            mErrorCallback = errorCallback;

            // authenticate
            if (isFingerprintAuthAvailable()) {
                SecretKey key = getSecretKey();
                boolean isCipherInit = true;
                if (key == null) {
                    if (createKey()) {
                        key = getSecretKey();
                    }
                }
                if (key != null && !initCipher()) {
                    isCipherInit = false;
                }
                if (key != null) {
                    // Display dialog
//                    mDialog = new TestFingerprintAuthenticationDialogFragment();
//
                    Bundle args = new Bundle();
                    if (map.hasKey("dialogTitle"))
                        args.putString("title", mDialogTitle = map.getString("dialogTitle"));
                    if (map.hasKey("dialogMessage"))
                        args.putString("message", mDialogMessage = map.getString("dialogMessage"));
                    if (map.hasKey("dialogSensor"))
                        args.putString("sensor", mDialogHint = map.getString("dialogSensor"));
                    if (map.hasKey("dialogButton"))
                        args.putString("button", mDialogCancel = map.getString("dialogButton"));
//                    mDialog.setArguments(args);
//                    mDialog.setListener(this);

                    mDialog = new FingerprintAuthenticationDialogFragment();

                    if (initCipher()) {
                        mDialog.setCancelable(false);
                        mDialog.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                        mDialog.show(getCurrentActivity()
                                .getFragmentManager(), DIALOG_FRAGMENT_TAG);

                    } else {
                        if (!mDisableBackup) {
                            // This happens if the lock screen has been disabled or or a fingerprint got
                            // enrolled. Thus show the dialog to authenticate with their password
                            mDialog.setCryptoObject(new FingerprintManager
                                    .CryptoObject(mCipher));
                            mDialog.setStage(FingerprintAuthenticationDialogFragment
                                    .Stage.NEW_FINGERPRINT_ENROLLED);
                            mDialog.show(getCurrentActivity().getFragmentManager(),
                                    DIALOG_FRAGMENT_TAG);
                        } else
                            errorCallback.invoke("Failed to init Cipher and backup disabled.");
                    }
                }

            } else
                errorCallback.invoke("Fingerprint authentication is not available. Please check the settings.");
        } else
            errorCallback.invoke("clientId or clientSecret key is missing");
    }

    @ReactMethod
    public void authenticateWithoutUI(Promise promise) {
        if (isFingerprintAuthAvailable()) {
            mIsCancelled = false;
            mCancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mFingerprintManager.authenticate(mCryptoObject, mCancellationSignal, 0, new AuthenticationCallback(promise), null);
                }
            }
        }
        else {
            promise.reject(Integer.toString(FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS),
                    "Fingerprint authentication is not available. Please check the settings.");
            promise = null;
        }
    }

    @ReactMethod
    public void isAvailable(Callback successCallback, Callback errorCallback) {
        if (mKeyguardManager != null && mFingerprintManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("isAvailable", isFingerprintAuthAvailable());
            map.putBoolean("isKeyguardSecure", mKeyguardManager.isKeyguardSecure());
            map.putBoolean("hasPermission", ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED);
            map.putBoolean("isHardwareDetected", mFingerprintManager.isHardwareDetected());
            map.putBoolean("hasEnrolledFingerprints", mFingerprintManager.hasEnrolledFingerprints());

            successCallback.invoke(map);
        }
        else
            errorCallback.invoke("Android M is needed to use the fingerprint");
    }

    private boolean isFingerprintAuthAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
                && mFingerprintManager != null
                && mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private static boolean initCipher() {
        boolean initCipher = false;
        String errorMessage = "";
        String initCipherExceptionErrorPrefix = "Failed to init Cipher: ";
        try {
            SecretKey key = getSecretKey();
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            initCipher = true;
        } catch (InvalidKeyException e) {
            errorMessage = initCipherExceptionErrorPrefix
                    + "InvalidKeyException: " + e.toString();
        }
        if (!initCipher) {
            Log.e(TAG, errorMessage);
        }
        return initCipher;
    }

    private static SecretKey getSecretKey() {
        String errorMessage = "";
        String getSecretKeyExceptionErrorPrefix = "Failed to get SecretKey from KeyStore: ";
        SecretKey key = null;
        try {
            mKeyStore.load(null);
            key = (SecretKey) mKeyStore.getKey(mClientId, null);
        } catch (KeyStoreException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "KeyStoreException: " + e.toString();;
        } catch (CertificateException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "CertificateException: " + e.toString();;
        } catch (UnrecoverableKeyException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "UnrecoverableKeyException: " + e.toString();;
        } catch (IOException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "IOException: " + e.toString();;
        } catch (NoSuchAlgorithmException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "NoSuchAlgorithmException: " + e.toString();;
        }
        if (key == null) {
            Log.e(TAG, errorMessage);
        }
        return key;
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean createKey() {
        String errorMessage = "";
        String createKeyExceptionErrorPrefix = "Failed to create key: ";
        boolean isKeyCreated = false;
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(mClientId,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
            isKeyCreated = true;
        } catch (NoSuchAlgorithmException e) {
            errorMessage = createKeyExceptionErrorPrefix
                    + "NoSuchAlgorithmException: " + e.toString();;
        } catch (InvalidAlgorithmParameterException e) {
            errorMessage = createKeyExceptionErrorPrefix
                    + "InvalidAlgorithmParameterException: " + e.toString();;
        } catch (CertificateException e) {
            errorMessage = createKeyExceptionErrorPrefix
                    + "CertificateException: " + e.toString();;
        } catch (IOException e) {
            errorMessage = createKeyExceptionErrorPrefix
                    + "IOException: " + e.toString();;
        }
        if (!isKeyCreated) {
            Log.e(TAG, errorMessage);
//            setPluginResultError(errorMessage);
        }
        return isKeyCreated;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void onAuthenticated(boolean withFingerprint) {
//        JSONObject resultJson = new JSONObject();
        String message = "";
        boolean isSuccess = false;
        Log.v(TAG, "success fingerprint = " + (mAuthCallback != null) + " - " + (mErrorCallback != null));
        try {

            if (withFingerprint) {
                // If the user has authenticated with fingerprint, verify that using cryptography and
                // then return the encrypted token
                byte[] encrypted = tryEncrypt();
                message = Base64.encodeToString(encrypted, 0 /* flags */);
            }
            else {
                // Authentication happened with backup password.
                message = "with password";

                // if failed to init cipher because of InvalidKeyException, create new key
                if (!initCipher()) {
                    createKey();
                }
            }
            isSuccess = true;
        } catch (BadPaddingException e) {
            message = "Failed to encrypt the data with the generated key:" +
                    " BadPaddingException:  " + e.toString();
            Log.e(TAG, message);
        } catch (IllegalBlockSizeException e) {
            message = "Failed to encrypt the data with the generated key: " +
                    "IllegalBlockSizeException: " + e.toString();
            Log.e(TAG, message);
        }

        if (isSuccess) {
            if (mAuthCallback != null)
                mAuthCallback.invoke(message);
        }
        else {
            if (mErrorCallback != null)
                mErrorCallback.invoke(message);
        }
    }

    public static void onCancelled() {
//        mCallbackContext.error("Cancelled");
        if (mErrorCallback != null)
            mErrorCallback.invoke("cancelled");
    }

    public static void onError(CharSequence errString) {
//        mCallbackContext.error(errString.toString());
        if (mErrorCallback != null)
            mErrorCallback.invoke(errString);
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private static byte[] tryEncrypt() throws BadPaddingException, IllegalBlockSizeException {
        return mCipher.doFinal(mClientSecret.getBytes());
    }

    @ReactMethod
    public void cancelAuthentication(Promise promise) {
        try {
            if(!mIsCancelled) {
                mCancellationSignal.cancel();
                mIsCancelled = true;
            }
            promise.resolve(null);
        } catch(Exception e) {
            promise.reject(e);
        }
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {
        if(!mIsCancelled) {
            mCancellationSignal.cancel();
            mIsCancelled = true;
        }
    }

    @Override
    public void onHostDestroy() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public class AuthenticationCallback extends FingerprintManager.AuthenticationCallback {

        Promise mPromise;

        public AuthenticationCallback(Promise promise) {
            mPromise = promise;
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                mIsCancelled = true;
            }

            Log.v(TAG, "auth error: " + errString.toString());

            if(mPromise == null) {
                throw new AssertionError("Tried to reject the auth promise, but it was already resolved / rejected. This shouldn't happen.");
            }
            mPromise.reject(Integer.toString(errorCode), errString.toString());
            mPromise = null;


        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);

            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putInt("code", helpCode);
            writableNativeMap.putString("message", helpString.toString());
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("fingerPrintAuthenticationHelp", writableNativeMap);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            if(mPromise == null) {
                throw new AssertionError("Tried to resolve the auth promise, but it was already resolved / rejected. This shouldn't happen.");
            }
            mPromise.resolve(null);
            mPromise = null;
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();

            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putInt("code", -1);
            writableNativeMap.putString("message", "Fingerprint was recognized as not valid.");
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("fingerPrintAuthenticationHelp", writableNativeMap);
        }

    }

}
