if(scanRecord.length > 14)
{
    if((scanRecord[0] == (byte) 0x02) &&
       (scanRecord[1] == (byte) 0x01) &&
       (scanRecord[2] == (byte) 0x06) &&
       (scanRecord[3] == (byte) 0x03) &&
       (scanRecord[4] == (byte) 0x03) &&
       (scanRecord[5] == (byte) 0xaa) &&
       (scanRecord[6] == (byte) 0xfe) &&
       (scanRecord[8] == (byte) 0x16) &&
       (scanRecord[9] == (byte) 0xaa) &&
       (scanRecord[10] == (byte) 0xfe))
    {
        switch(scanRecord[11])
        {
            case 0x0:
                Log.d(TAG,"Eddystone-UID");
                Log.d(TAG,"Ranging Data=" + IntToHex2(scanRecord[12]));

                String namespace =
                                    IntToHex2(scanRecord[13]) +
                                    IntToHex2(scanRecord[14]) +
                                    IntToHex2(scanRecord[15]) +
                                    IntToHex2(scanRecord[16]) +
                                    IntToHex2(scanRecord[17]) +
                                    IntToHex2(scanRecord[18]) +
                                    IntToHex2(scanRecord[19]) +
                                    IntToHex2(scanRecord[20]) +
                                    IntToHex2(scanRecord[21]) +
                                    IntToHex2(scanRecord[22]);

                String uid =
                                    IntToHex2(scanRecord[23]) +
                                    IntToHex2(scanRecord[24]) +
                                    IntToHex2(scanRecord[25]) +
                                    IntToHex2(scanRecord[26]) +
                                    IntToHex2(scanRecord[27]) +
                                    IntToHex2(scanRecord[28]);

                Log.d(TAG,namespace+uid);
                break;

            case 0x10:
                Log.d(TAG,"Eddystone-URL");
                Log.d(TAG,"TX Power=" + IntToHex2(scanRecord[12]));

                int len = scanRecord[7] - 6;

                String Scheme = "";

                switch(scanRecord[13])
                {
                     case 0x0:
                          Scheme = "http://www.";
                          break;

                     case 0x1:
                          Scheme = "https://www.";
                          break;

                     case 0x2:
                          Scheme = "http://";
                          break;

                     case 0x3:
                          Scheme = "https://";
                          break;
                }

                String url = "";

                for(int i = 0 ; i < len ; i++)
                {
                    url = url + decodeURL(scanRecord[14 + i]);
                }

                Log.d(TAG,Scheme + url);
                break;

           case 0x20:
                Log.d(TAG,"Eddystone-TLM");
                Log.d(TAG,"Version=" + IntToHex2(scanRecord[12]));
                Log.d(TAG,"VBATT=" + IntToHex2(scanRecord[13]) + IntToHex2(scanRecord[14]));
                Log.d(TAG,"TEMP=" + IntToHex2(scanRecord[15]) + IntToHex2(scanRecord[16]));
                Log.d(TAG,"ADVCNT=" + IntToHex2(scanRecord[17]) + IntToHex2(scanRecord[18])+ IntToHex2(scanRecord[19]) + IntToHex2(scanRecord[20]));
                Log.d(TAG,"SECCNT=" + IntToHex2(scanRecord[21]) + IntToHex2(scanRecord[22])+ IntToHex2(scanRecord[23]) + IntToHex2(scanRecord[24]));
                break;
          }
     }
}
