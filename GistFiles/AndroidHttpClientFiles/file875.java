/**
 * Copyright 2016 Henric Andersson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.util.Log;

import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 */
public class VerifiedHttpClient extends DefaultHttpClient {
    private final static String TAG = "VerifiedHttpClient";
    private final static String[] KEY_PATHS = {"/etc/security/cacerts/"};

    private final static int CONNECT_TIMEOUT = 30000;
    private final static int RESPONSE_TIMEOUT = 30000;

    // This variable is initialized ONCE
    private static KeyStore mSafeKeyStore = null;

    private boolean mAllowHttp = false;

    public VerifiedHttpClient(boolean allowHTTP, String forceHost, int forcePort) {
        mAllowHttp = allowHTTP;

        if (mSafeKeyStore == null) {
            mSafeKeyStore = loadTrustedCertificates();
        }

        // Set timeout for our communications
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, RESPONSE_TIMEOUT);
        setParams(params);

        // If set, allows us to override default proxy in the system by pointing out where
        // we are going, this also overrides URL provided in execute() !!
        if (forceHost != null) {
            HttpRoute hr = new HttpRoute(new HttpHost(forceHost, forcePort, "https"), null, true);
            getParams().setParameter(ConnRoutePNames.FORCED_ROUTE, hr);
        }
    }

    /**
     * Create a KeyStore based on the certificates installed by the platform, not the user.
     *
     * @return Secure KeyStore or NULL on failure
     */
    private KeyStore loadTrustedCertificates() {

        CertificateFactory cf;
        KeyStore ret;
        try {
            ret = KeyStore.getInstance(KeyStore.getDefaultType());
            ret.load(null, null);
            cf = CertificateFactory.getInstance("X.509");
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for (String key : KEY_PATHS) {
            try {
                File path = new File(key);
                File[] files = null;

                if (!path.exists()) {
                    Log.d(TAG, key + " does not exist");
                } else if (path.isDirectory()) {
                    files = path.listFiles();
                } else if (path.isFile()) {
                    files = new File[1];
                    files[0] = path;
                } else {
                    Log.w(TAG, key + " is neither file nor directory?");
                }

                InputStream caInput = null;

                for (File f : files) {
                    // Skip non-file items
                    if (!f.isFile())
                        continue;

                    try {
                        caInput = new BufferedInputStream(new FileInputStream(f));
                        Certificate ca;
                        ca = cf.generateCertificate(caInput);
                        ret.setCertificateEntry(f.getName(), ca);
                        //Log.d(TAG, "Added " + f.getAbsolutePath() + f.getName());
                    } catch (KeyStoreException e) {
                        // Not adding this one then...
                    } catch (FileNotFoundException e) {
                        // Not adding this one then...
                    } finally {
                        try {
                            if (caInput != null)
                                caInput.close();
                        } catch (IOException e) {
                            // Can't do much about this
                        }
                    }
                }
            } catch (CertificateException e) {
                // Woha! This would be REALLY BAD, abort!
                Log.w(TAG, "WARNING! Unable to load " + key + " into keystore");
            }
        }

        return ret;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();

        registry.register(new Scheme("http", new HTTPSocketFactory(), 80));
        registry.register(new Scheme("https", strictSSLSocketFactory(), 443));

        return new SingleClientConnManager(getParams(), registry);
    }

    private class HTTPSocketFactory implements SocketFactory {
        private SocketFactory mRealFactory = PlainSocketFactory.getSocketFactory();

        @Override
        public Socket createSocket() throws IOException {
            if (!mAllowHttp) {
                Log.d(TAG, "WARNING! Using non-HTTPS protocol");
            }
            return mRealFactory.createSocket();
        }

        @Override
        public Socket connectSocket(Socket socket, String s, int i, InetAddress inetAddress, int i2, HttpParams httpParams) throws IOException, UnknownHostException, ConnectTimeoutException {
            if (!mAllowHttp) {
                Log.d(TAG, "WARNING! Using non-HTTPS protocol");
            }
            return mRealFactory.connectSocket(socket, s, i, inetAddress, i2, httpParams);
        }

        @Override
        public boolean isSecure(Socket socket) throws IllegalArgumentException {
            return mRealFactory.isSecure(socket);
        }
    }

    /**
     * Creates a somewhat augmented SSLSocketFactory which only trusts the built-in certificates
     * in your device.
     *
     * @return SSLSocketFactory
     */
    private SSLSocketFactory strictSSLSocketFactory() {
        try {
            // Load the system certificates ONLY
            KeyStore trusted = mSafeKeyStore;

            // Setup SSL with the new and shiny keystore
            SSLSocketFactory sf = new SSLSocketFactory(trusted);

            // Verify hostnames too!
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
