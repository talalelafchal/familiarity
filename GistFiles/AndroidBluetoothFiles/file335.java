if(scanRecord.length > 30)
{
    if((scanRecord[5] == (byte)0x4c) && (scanRecord[6] == (byte)0x00) &&
       (scanRecord[7] == (byte)0x02) && (scanRecord[8] == (byte)0x15))
    {
            String uuid = IntToHex2(scanRecord[9] & 0xff) 
            + IntToHex2(scanRecord[10] & 0xff)
            + IntToHex2(scanRecord[11] & 0xff)
            + IntToHex2(scanRecord[12] & 0xff)
            + "-"
            + IntToHex2(scanRecord[13] & 0xff)
            + IntToHex2(scanRecord[14] & 0xff)
            + "-"
            + IntToHex2(scanRecord[15] & 0xff)
            + IntToHex2(scanRecord[16] & 0xff)
            + "-"
            + IntToHex2(scanRecord[17] & 0xff)
            + IntToHex2(scanRecord[18] & 0xff)
            + "-"
            + IntToHex2(scanRecord[19] & 0xff)
            + IntToHex2(scanRecord[20] & 0xff)
            + IntToHex2(scanRecord[21] & 0xff)
            + IntToHex2(scanRecord[22] & 0xff)
            + IntToHex2(scanRecord[23] & 0xff)
            + IntToHex2(scanRecord[24] & 0xff);

            String major = IntToHex2(scanRecord[25] & 0xff) + IntToHex2(scanRecord[26] & 0xff);
            String minor = IntToHex2(scanRecord[27] & 0xff) + IntToHex2(scanRecord[28] & 0xff);
        }
}