package com.nfc_lab.nfcreallike;

import java.nio.charset.Charset;
import java.util.Locale;

import android.net.Uri;
import android.nfc.NdefRecord;

/**
 * @see NdefRecord
 * @see Uri#normalizeScheme()
 */
public class NdefRecordUtils {

    /**
     * NFC Forum "URI Record Type Definition"
     * <p>
     * This is a mapping of "URI Identifier Codes" to URI string prefixes, per
     * section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
    private static final String[] URI_PREFIX_MAP = new String[] {
            "", // 0x00
            "http://www.", // 0x01
            "https://www.", // 0x02
            "http://", // 0x03
            "https://", // 0x04
            "tel:", // 0x05
            "mailto:", // 0x06
            "ftp://anonymous:anonymous@", // 0x07
            "ftp://ftp.", // 0x08
            "ftps://", // 0x09
            "sftp://", // 0x0A
            "smb://", // 0x0B
            "nfs://", // 0x0C
            "ftp://", // 0x0D
            "dav://", // 0x0E
            "news:", // 0x0F
            "telnet://", // 0x10
            "imap:", // 0x11
            "rtsp://", // 0x12
            "urn:", // 0x13
            "pop:", // 0x14
            "sip:", // 0x15
            "sips:", // 0x16
            "tftp:", // 0x17
            "btspp://", // 0x18
            "btl2cap://", // 0x19
            "btgoep://", // 0x1A
            "tcpobex://", // 0x1B
            "irdaobex://", // 0x1C
            "file://", // 0x1D
            "urn:epc:id:", // 0x1E
            "urn:epc:tag:", // 0x1F
            "urn:epc:pat:", // 0x20
            "urn:epc:raw:", // 0x21
            "urn:epc:", // 0x22
    };

    /**
     * Create a new NDEF Record containing a URI.
     * <p>
     * Use this method to encode a URI (or URL) into an NDEF Record.
     * <p>
     * Uses the well known URI type representation: {@link #TNF_WELL_KNOWN} and
     * {@link #RTD_URI}. This is the most efficient encoding of a URI into NDEF.
     * <p>
     * The uri parameter will be normalized with {@link Uri#normalizeScheme} to
     * set the scheme to lower case to follow Android best practices for intent
     * filtering. However the unchecked exception
     * {@link IllegalArgumentException} may be thrown if the uri parameter has
     * serious problems, for example if it is empty, so always catch this
     * exception if you are passing user-generated data into this method.
     * <p>
     * Reference specification: NFCForum-TS-RTD_URI_1.0
     * 
     * @param uri URI to encode.
     * @return an NDEF Record containing the URI
     * @throws IllegalArugmentException if the uri is empty or invalid
     */
    public static NdefRecord createUri(Uri uri) {
        if (uri == null)
            throw new NullPointerException("uri is null");

        uri = normalizeScheme(uri);
        String uriString = uri.toString();
        if (uriString.length() == 0)
            throw new IllegalArgumentException("uri is empty");

        byte prefix = 0;
        for (int i = 1; i < URI_PREFIX_MAP.length; i++) {
            if (uriString.startsWith(URI_PREFIX_MAP[i])) {
                prefix = (byte) i;
                uriString = uriString.substring(URI_PREFIX_MAP[i].length());
                break;
            }
        }
        byte[] uriBytes = uriString.getBytes(Charset.forName("UTF-8"));
        byte[] recordBytes = new byte[uriBytes.length + 1];
        recordBytes[0] = prefix;
        System.arraycopy(uriBytes, 0, recordBytes, 1, uriBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
                null, recordBytes);
    }

    /**
     * Return an equivalent URI with a lowercase scheme component. This aligns
     * the Uri with Android best practices for intent filtering.
     * <p>
     * For example, "HTTP://www.android.com" becomes "http://www.android.com"
     * <p>
     * All URIs received from outside Android (such as user input, or external
     * sources like Bluetooth, NFC, or the Internet) should be normalized before
     * they are used to create an Intent.
     * <p class="note">
     * This method does <em>not</em> validate bad URI's, or 'fix' poorly
     * formatted URI's - so do not use it for input validation. A Uri will
     * always be returned, even if the Uri is badly formatted to begin with and
     * a scheme component cannot be found.
     * 
     * @return normalized Uri (never null)
     * @see {@link android.content.Intent#setData}
     * @see {@link #setNormalizedData}
     */
    public static Uri normalizeScheme(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null)
            return uri; // give up
        String lowerScheme = scheme.toLowerCase(Locale.US);
        if (scheme.equals(lowerScheme))
            return uri; // no change

        return uri.buildUpon().scheme(lowerScheme).build();
    }
}
