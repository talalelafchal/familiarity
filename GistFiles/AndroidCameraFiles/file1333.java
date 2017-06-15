package com.ctrlsmart.bcDecode;

/**
 * Created by Administrator on 2015/4/12.
 */
public class ZBarSymbol
{
//    static final byte[] hash;
//    public int cache_count;
//    public int configs = 0;
//    public byte[] data = null;
//    public int data_alloc = 0;
//    public int datalen = 0;
//    public int modifiers = 0;
//    public ZBarSymbol next = null;
//    public int npts = 0;
//    public ZBarOrientation orient;
//    public Point[] pts;
//    public int pts_alloc = 0;
//    public int quality;
//    public AtomicInteger refcnt = new AtomicInteger();
//    public String sd_data = null;
//    public int sd_data_alloc = 0;
//    public int sd_datalen = 0;
//    public int sd_ecc_level = 0;
//    public int sd_version = 0;
//    public ZBarSymbolSet syms;
//    public long time;
//    public ZBarSymbolType type = ZBarSymbolType.ZBAR_NONE;
//
//    static
//    {
//        if (!ZBarSymbol.class.desiredAssertionStatus()) {}
//        for (boolean bool = true;; bool = false)
//        {
//            $assertionsDisabled = bool;
//            byte[] arrayOfByte = new byte[32];
//            arrayOfByte[1] = 1;
//            arrayOfByte[2] = 16;
//            arrayOfByte[3] = 17;
//            arrayOfByte[4] = -1;
//            arrayOfByte[5] = 17;
//            arrayOfByte[6] = 22;
//            arrayOfByte[7] = 12;
//            arrayOfByte[8] = 5;
//            arrayOfByte[9] = 6;
//            arrayOfByte[10] = 8;
//            arrayOfByte[11] = -1;
//            arrayOfByte[12] = 4;
//            arrayOfByte[13] = 3;
//            arrayOfByte[14] = 7;
//            arrayOfByte[15] = 18;
//            arrayOfByte[16] = -1;
//            arrayOfByte[17] = -1;
//            arrayOfByte[18] = -1;
//            arrayOfByte[19] = -1;
//            arrayOfByte[20] = -1;
//            arrayOfByte[21] = -1;
//            arrayOfByte[22] = -1;
//            arrayOfByte[23] = 2;
//            arrayOfByte[24] = -1;
//            arrayOfByte[26] = 18;
//            arrayOfByte[27] = 12;
//            arrayOfByte[28] = 11;
//            arrayOfByte[29] = 29;
//            arrayOfByte[30] = 10;
//            hash = arrayOfByte;
//            return;
//        }
//    }
//
//    public static int _zbar_get_symbol_hash(ZBarSymbolType paramZBarSymbolType)
//    {
//        int i = hash[(0x1F & cn.com.sdax.jlibSD4Bar.EnumXVal.value(paramZBarSymbolType))];
//        int j = hash[(0x1F & (0xFFFFFFFF ^ cn.com.sdax.jlibSD4Bar.EnumXVal.value(paramZBarSymbolType) >> 4))];
//        assert ((i >= 0) && (j >= 0));
//        if ((i < 0) || (j < 0)) {
//            return 0;
//        }
//        return 0x1F & i + j;
//    }
//
//    public static void _zbar_symbol_free(ZBarSymbol paramZBarSymbol)
//    {
//        if (paramZBarSymbol.syms != null)
//        {
//            zbar_symbol_set_ref(paramZBarSymbol.syms, -1);
//            paramZBarSymbol.syms = null;
//        }
//        paramZBarSymbol.pts = null;
//        paramZBarSymbol.data = null;
//        paramZBarSymbol.sd_data = null;
//    }
//
//    public static void _zbar_symbol_refcnt(ZBarSymbol paramZBarSymbol, int paramInt)
//    {
//        if ((ZBarRefcnt._zbar_refcnt(paramZBarSymbol.refcnt, paramInt) == 0) && (paramInt <= 0)) {
//            _zbar_symbol_free(paramZBarSymbol);
//        }
//    }
//
//    public static ZBarSymbolSet _zbar_symbol_set_create()
//    {
//        ZBarSymbolSet localZBarSymbolSet = new ZBarSymbolSet();
//        ZBarRefcnt._zbar_refcnt(localZBarSymbolSet.refcnt, 1);
//        return localZBarSymbolSet;
//    }
//
//    public static void _zbar_symbol_set_free(ZBarSymbolSet paramZBarSymbolSet)
//    {
//        ZBarSymbol localZBarSymbol;
//        for (Object localObject = paramZBarSymbolSet.head;; localObject = localZBarSymbol)
//        {
//            if (localObject == null)
//            {
//                paramZBarSymbolSet.head = null;
//                return;
//            }
//            localZBarSymbol = ((ZBarSymbol)localObject).next;
//            ((ZBarSymbol)localObject).next = null;
//            _zbar_symbol_refcnt((ZBarSymbol)localObject, -1);
//        }
//    }
//
//    public static void sym_add_point(ZBarSymbol paramZBarSymbol, int paramInt1, int paramInt2)
//    {
//        int i = paramZBarSymbol.npts;
//        int j = 1 + paramZBarSymbol.npts;
//        paramZBarSymbol.npts = j;
//        Point[] arrayOfPoint;
//        if (j >= paramZBarSymbol.pts_alloc)
//        {
//            paramZBarSymbol.pts_alloc <<= 1;
//            paramZBarSymbol.pts_alloc = (0x1 | paramZBarSymbol.pts_alloc);
//            arrayOfPoint = new Point[paramZBarSymbol.pts_alloc];
//        }
//        for (int k = 0;; k++)
//        {
//            if (k >= arrayOfPoint.length)
//            {
//                if (i > 0) {
//                    System.arraycopy(paramZBarSymbol.pts, 0, arrayOfPoint, 0, i);
//                }
//                paramZBarSymbol.pts = arrayOfPoint;
//                paramZBarSymbol.pts[i].x = paramInt1;
//                paramZBarSymbol.pts[i].y = paramInt2;
//                return;
//            }
//            arrayOfPoint[k] = new Point();
//        }
//    }
//
//    public static byte[] zbar_symbol_get_data(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.data;
//    }
//
//    public static int zbar_symbol_get_quality(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.quality;
//    }
//
//    public static ZBarSymbolType zbar_symbol_get_type(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.type;
//    }
//
//    public static ZBarSymbol zbar_symbol_next(ZBarSymbol paramZBarSymbol)
//    {
//        if (paramZBarSymbol != null) {
//            return paramZBarSymbol.next;
//        }
//        return null;
//    }
//
//    public static void zbar_symbol_set_ref(ZBarSymbolSet paramZBarSymbolSet, int paramInt)
//    {
//        if ((ZBarRefcnt._zbar_refcnt(paramZBarSymbolSet.refcnt, paramInt) == 0) && (paramInt <= 0)) {
//            _zbar_symbol_set_free(paramZBarSymbolSet);
//        }
//    }
//
//    void _zbar_symbol_set_add(ZBarSymbolSet paramZBarSymbolSet, ZBarSymbol paramZBarSymbol)
//    {
//        paramZBarSymbol.next = paramZBarSymbolSet.head;
//        paramZBarSymbolSet.head = paramZBarSymbol;
//        paramZBarSymbolSet.nsyms = (1 + paramZBarSymbolSet.nsyms);
//        _zbar_symbol_refcnt(paramZBarSymbol, 1);
//    }
//
//    int base64_encode(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
//    {
//        byte[] arrayOfByte = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
//        int i = 19;
//        int j = 0;
//        int k = 0;
//        int i10;
//        if (paramInt == 0)
//        {
//            i10 = k;
//            int i11 = i10 + 1;
//            paramArrayOfByte1[i10] = 10;
//            int i12 = i11 + 1;
//            paramArrayOfByte1[i11] = 0;
//            return i12 - 1;
//        }
//        int m = j + 1;
//        int n = paramArrayOfByte2[j] << 16;
//        int i1;
//        if (paramInt > 1)
//        {
//            i1 = m + 1;
//            n |= paramArrayOfByte2[m] << 8;
//        }
//        for (;;)
//        {
//            int i2;
//            if (paramInt > 2)
//            {
//                i2 = i1 + 1;
//                n |= paramArrayOfByte2[i1];
//            }
//            for (;;)
//            {
//                int i3 = k + 1;
//                paramArrayOfByte1[k] = arrayOfByte[(0x3F & n >>> 18)];
//                int i4 = i3 + 1;
//                paramArrayOfByte1[i3] = arrayOfByte[(0x3F & n >>> 12)];
//                int i5 = i4 + 1;
//                int i6;
//                label181:
//                int i7;
//                if (paramInt > 1)
//                {
//                    i6 = arrayOfByte[(0x3F & n >>> 6)];
//                    paramArrayOfByte1[i4] = i6;
//                    i7 = i5 + 1;
//                    if (paramInt <= 2) {
//                        break label233;
//                    }
//                }
//                label233:
//                for (int i8 = arrayOfByte[(n & 0x3F)];; i8 = 61)
//                {
//                    paramArrayOfByte1[i5] = i8;
//                    if (paramInt >= 3) {
//                        break label240;
//                    }
//                    i10 = i7;
//                    break;
//                    i6 = 61;
//                    break label181;
//                }
//                label240:
//                i--;
//                int i9;
//                if (i == 0)
//                {
//                    i9 = i7 + 1;
//                    paramArrayOfByte1[i7] = 10;
//                    i = 19;
//                }
//                for (;;)
//                {
//                    paramInt -= 3;
//                    j = i2;
//                    k = i9;
//                    break;
//                    i9 = i7;
//                }
//                i2 = i1;
//            }
//            i1 = m;
//        }
//    }
//
//    String zbar_get_addon_name(ZBarSymbolType paramZBarSymbolType)
//    {
//        return "";
//    }
//
//    String zbar_get_config_name(ZbarConfig paramZbarConfig)
//    {
//        switch (paramZbarConfig)
//        {
//            case ZBAR_CFG_MAX_LEN:
//            default:
//                return "";
//            case ZBAR_CFG_ADD_CHECK:
//                return "ENABLE";
//            case ZBAR_CFG_ASCII:
//                return "ADD_CHECK";
//            case ZBAR_CFG_EMIT_CHECK:
//                return "EMIT_CHECK";
//            case ZBAR_CFG_ENABLE:
//                return "ASCII";
//            case ZBAR_CFG_MIN_LEN:
//                return "MIN_LEN";
//            case ZBAR_CFG_NUM:
//                return "MAX_LEN";
//            case ZBAR_CFG_POSITION:
//                return "UNCERTAINTY";
//            case ZBAR_CFG_UNCERTAINTY:
//                return "POSITION";
//            case ZBAR_CFG_X_DENSITY:
//                return "X_DENSITY";
//        }
//        return "Y_DENSITY";
//    }
//
//    String zbar_get_modifier_name(ZBarModifier paramZBarModifier)
//    {
//        switch (paramZBarModifier)
//        {
//            default:
//                return "";
//            case ZBAR_MOD_AIM:
//                return "GS1";
//        }
//        return "AIM";
//    }
//
//    String zbar_get_orientation_name(ZBarOrientation paramZBarOrientation)
//    {
//        switch (paramZBarOrientation)
//        {
//            default:
//                return "UNKNOWN";
//            case ZBAR_ORIENT_LEFT:
//                return "UP";
//            case ZBAR_ORIENT_RIGHT:
//                return "RIGHT";
//            case ZBAR_ORIENT_UNKNOWN:
//                return "DOWN";
//        }
//        return "LEFT";
//    }
//
//    String zbar_get_symbol_name(ZBarSymbolType paramZBarSymbolType)
//    {
//        switch (paramZBarSymbolType)
//        {
//            default:
//                return "UNKNOWN";
//            case ZBAR_ADDON2:
//                return "EAN-2";
//            case ZBAR_ADDON5:
//                return "EAN-5";
//            case ZBAR_CODABAR:
//                return "EAN-8";
//            case ZBAR_CODE128:
//                return "UPC-E";
//            case ZBAR_CODE39:
//                return "ISBN-10";
//            case ZBAR_CODE93:
//                return "UPC-A";
//            case ZBAR_COMPOSITE:
//                return "EAN-13";
//            case ZBAR_DATABAR:
//                return "ISBN-13";
//            case ZBAR_DATABAR_EXP:
//                return "COMPOSITE";
//            case ZBAR_EAN13:
//                return "I2/5";
//            case ZBAR_EAN2:
//                return "DataBar";
//            case ZBAR_EAN5:
//                return "DataBar-Exp";
//            case ZBAR_EAN8:
//                return "Codabar";
//            case ZBAR_I25:
//                return "CODE-39";
//            case ZBAR_NONE:
//                return "CODE-93";
//            case ZBAR_PARTIAL:
//                return "CODE-128";
//            case ZBAR_ISBN10:
//                return "PDF417";
//        }
//        return "QR-Code";
//    }
//
//    ZBarSymbol zbar_symbol_first_component(ZBarSymbol paramZBarSymbol)
//    {
//        if ((paramZBarSymbol != null) && (paramZBarSymbol.syms != null)) {
//            return paramZBarSymbol.syms.head;
//        }
//        return null;
//    }
//
//    ZBarSymbolSet zbar_symbol_get_components(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.syms;
//    }
//
//    int zbar_symbol_get_configs(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.configs;
//    }
//
//    int zbar_symbol_get_count(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.cache_count;
//    }
//
//    int zbar_symbol_get_data_length(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.datalen;
//    }
//
//    int zbar_symbol_get_loc_size(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.npts;
//    }
//
//    int zbar_symbol_get_loc_x(ZBarSymbol paramZBarSymbol, int paramInt)
//    {
//        if (paramInt < paramZBarSymbol.npts) {
//            return paramZBarSymbol.pts[paramInt].x;
//        }
//        return -1;
//    }
//
//    int zbar_symbol_get_loc_y(ZBarSymbol paramZBarSymbol, int paramInt)
//    {
//        if (paramInt < paramZBarSymbol.npts) {
//            return paramZBarSymbol.pts[paramInt].y;
//        }
//        return -1;
//    }
//
//    int zbar_symbol_get_modifiers(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.modifiers;
//    }
//
//    ZBarOrientation zbar_symbol_get_orientation(ZBarSymbol paramZBarSymbol)
//    {
//        return paramZBarSymbol.orient;
//    }
//
//    void zbar_symbol_ref(ZBarSymbol paramZBarSymbol, int paramInt)
//    {
//        _zbar_symbol_refcnt(paramZBarSymbol, paramInt);
//    }
//
//    ZBarSymbol zbar_symbol_set_first_symbol(ZBarSymbolSet paramZBarSymbolSet)
//    {
//        ZBarSymbol localZBarSymbol = paramZBarSymbolSet.tail;
//        if (localZBarSymbol != null) {
//            return localZBarSymbol.next;
//        }
//        return paramZBarSymbolSet.head;
//    }
//
//    ZBarSymbol zbar_symbol_set_first_unfiltered(ZBarSymbolSet paramZBarSymbolSet)
//    {
//        return paramZBarSymbolSet.head;
//    }
//
//    int zbar_symbol_set_get_size(ZBarSymbolSet paramZBarSymbolSet)
//    {
//        return paramZBarSymbolSet.nsyms;
//    }
}
