
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
http://www.java2s.com/Tutorial/Java/0490__Security/Createsa1024bitRSAkeypairandstoresittothefilesystemastwofiles.htm
http://www.javamex.com/tutorials/cryptography/rsa_encryption.shtml
https://stackoverflow.com/questions/1709441/generate-rsa-key-pair-and-encode-private-as-string

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ToyAuthAgentService extends Service {

    private static final String TAG = "ToyAgentService";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private SshUtil sshUtil = new SshUtil();
    Map<String, byte[]> sshEncodedPublicKeys = newHashMap();
    Map<PublicKey, PrivateKey> publicPrivateMap = newHashMap();

    @Override
    public void onCreate() {
        super.onCreate();
        for (String privateKeyFileName : new String[] {"id_rsa", "id_dsa"}) {
            KeyPair keyPair = loadKey(privateKeyFileName);
            sshEncodedPublicKeys.put(privateKeyFileName, sshUtil.sshEncode(keyPair.getPublic()));
            publicPrivateMap.put(keyPair.getPublic(),keyPair.getPrivate());
        }
    }

    private KeyPair loadKey(String fileName) {
        try {
            PEMReader r = new PEMReader(new InputStreamReader(getAssets().open(fileName)));
            return (KeyPair) r.readObject();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load key from "+fileName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called");
        return authAgentBinder;
    }

    private final AndroidAuthAgent.Stub authAgentBinder = new AndroidAuthAgent.Stub() {

        public Map getIdentities() throws RemoteException {
            Log.d(TAG, "getIdentities() called");
            return sshEncodedPublicKeys;
        }

        public byte[] sign(byte[] publicKey, byte[] data) throws RemoteException {
            Log.d(TAG, "sign() called");
            for (Map.Entry<PublicKey, PrivateKey> entry: publicPrivateMap.entrySet()) {
                if (Arrays.equals(sshUtil.sshEncode(entry.getKey()), publicKey)) {
                    return sshUtil.sign(data, entry.getValue());
                }
            }
            throw new RuntimeException("No key found matching requested public key");
        }

    };
}
