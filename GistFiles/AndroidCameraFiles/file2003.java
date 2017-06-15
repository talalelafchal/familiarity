package com.ctrlsmart.bcDecode;

/**
 * Created by Administrator on 2015/4/12.
 */
public class ZBarImageScanner
{
//    static final int CACHE_HYSTERESIS = 2000;
//    static final int CACHE_PROXIMITY = 1000;
//    static final int CACHE_TIMEOUT = 4000;
//    static final int NUM_SCN_CFGS = 2;
//    static final int NUM_SYMS = 20;
//    static final int RECYCLE_BUCKETS = 5;
//    ZBarSymbol cache;
//    int config;
//    int[] configs = new int[2];
//    ZBarDecoder dcode;
//    int du;
//    int dx;
//    int dy;
//    int ean_config;
//    boolean enable_cache;
//    ZBarImageDataHandler handler;
//    zbar_image_t img;
//    QrDec.qr_reader qr = new QrDec.qr_reader();
//    RecycleBucket[] recycle = new RecycleBucket[5];
//    ZBarScanner scn;
//    int[][] sym_configs;
//    ZBarSymbolSet syms;
//    long time;
//    int umin;
//    byte[] userdata;
//    int v;
//
//    static
//    {
//        if (!ZBarImageScanner.class.desiredAssertionStatus()) {}
//        for (boolean bool = true;; bool = false)
//        {
//            $assertionsDisabled = bool;
//            return;
//        }
//    }
//
//    public ZBarImageScanner()
//    {
//        int[] arrayOfInt = { 1, 20 };
//        this.sym_configs = ((int[][]) Array.newInstance(Integer.TYPE, arrayOfInt));
//        for (int i = 0;; i++)
//        {
//            if (i >= 5) {
//                return;
//            }
//            this.recycle[i] = new RecycleBucket();
//        }
//    }
//
//    static int CFG(ZBarImageScanner paramZBarImageScanner, ZbarConfig paramZbarConfig)
//    {
//        return paramZBarImageScanner.configs[(EnumXVal.value(paramZbarConfig) - EnumXVal.value(ZbarConfig.ZBAR_CFG_X_DENSITY))];
//    }
//
//    static void CFG(ZBarImageScanner paramZBarImageScanner, ZbarConfig paramZbarConfig, int paramInt)
//    {
//        paramZBarImageScanner.configs[(EnumXVal.value(paramZbarConfig) - EnumXVal.value(ZbarConfig.ZBAR_CFG_X_DENSITY))] = paramInt;
//    }
//
//    static int TEST_CFG(ZBarImageScanner paramZBarImageScanner, ZbarConfig paramZbarConfig)
//    {
//        return 0x1 & paramZBarImageScanner.config >> EnumXVal.value(paramZbarConfig) - EnumXVal.value(ZbarConfig.ZBAR_CFG_POSITION);
//    }
//
//    public static void _zbar_image_scanner_add_sym(ZBarImageScanner paramZBarImageScanner, ZBarSymbol paramZBarSymbol)
//    {
//        cache_sym(paramZBarImageScanner, paramZBarSymbol);
//        ZBarSymbolSet localZBarSymbolSet = paramZBarImageScanner.syms;
//        if ((paramZBarSymbol.cache_count == 0) || (localZBarSymbolSet.tail == null))
//        {
//            paramZBarSymbol.next = localZBarSymbolSet.head;
//            localZBarSymbolSet.head = paramZBarSymbol;
//            if (paramZBarSymbol.cache_count != 0) {
//                break label82;
//            }
//            localZBarSymbolSet.nsyms = (1 + localZBarSymbolSet.nsyms);
//        }
//        for (;;)
//        {
//            ZBarSymbol._zbar_symbol_refcnt(paramZBarSymbol, 1);
//            return;
//            paramZBarSymbol.next = localZBarSymbolSet.tail.next;
//            localZBarSymbolSet.tail.next = paramZBarSymbol;
//            break;
//            label82:
//            if (localZBarSymbolSet.tail == null) {
//                localZBarSymbolSet.tail = paramZBarSymbol;
//            }
//        }
//    }
//
//    public static ZBarSymbol _zbar_image_scanner_alloc_sym(ZBarImageScanner paramZBarImageScanner, ZBarSymbolType paramZBarSymbolType, int paramInt)
//    {
//        int i = 0;
//        ZBarSymbol localZBarSymbol = null;
//        if (i >= 4) {}
//        for (;;)
//        {
//            label10:
//            if (i <= 0) {}
//            do
//            {
//                if (localZBarSymbol == null) {
//                    break label192;
//                }
//                paramZBarImageScanner.recycle[i].head = localZBarSymbol.next;
//                localZBarSymbol.next = null;
//                if (($assertionsDisabled) || (paramZBarImageScanner.recycle[i].nsyms != 0)) {
//                    break label109;
//                }
//                throw new AssertionError();
//                int j = 1 << i * 2;
//                localZBarSymbol = null;
//                if (paramInt <= j) {
//                    break label10;
//                }
//                i++;
//                break;
//                localZBarSymbol = paramZBarImageScanner.recycle[i].head;
//            } while (localZBarSymbol != null);
//            i--;
//        }
//        label109:
//        RecycleBucket localRecycleBucket = paramZBarImageScanner.recycle[i];
//        localRecycleBucket.nsyms = (-1 + localRecycleBucket.nsyms);
//        for (;;)
//        {
//            localZBarSymbol.type = paramZBarSymbolType;
//            localZBarSymbol.quality = 1;
//            localZBarSymbol.npts = 0;
//            localZBarSymbol.orient = ZBarOrientation.ZBAR_ORIENT_UNKNOWN;
//            localZBarSymbol.cache_count = 0;
//            localZBarSymbol.time = paramZBarImageScanner.time;
//            if (($assertionsDisabled) || (localZBarSymbol.syms == null)) {
//                break;
//            }
//            throw new AssertionError();
//            label192:
//            localZBarSymbol = new ZBarSymbol();
//        }
//        if (paramInt > 0)
//        {
//            localZBarSymbol.datalen = (paramInt - 1);
//            if (localZBarSymbol.data_alloc < paramInt)
//            {
//                localZBarSymbol.data_alloc = paramInt;
//                localZBarSymbol.data = new byte[paramInt];
//            }
//            return localZBarSymbol;
//        }
//        localZBarSymbol.data = null;
//        localZBarSymbol.data_alloc = 0;
//        localZBarSymbol.datalen = 0;
//        return localZBarSymbol;
//    }
//
//    public static void _zbar_image_scanner_recycle_syms(ZBarImageScanner paramZBarImageScanner, ZBarSymbol paramZBarSymbol)
//    {
//        if (paramZBarSymbol == null) {
//            return;
//        }
//        ZBarSymbol localZBarSymbol = paramZBarSymbol.next;
//        if ((paramZBarSymbol.refcnt.intValue() != 0) && (ZBarRefcnt._zbar_refcnt(paramZBarSymbol.refcnt, -1) != 0))
//        {
//            assert (paramZBarSymbol.data_alloc != 0);
//            paramZBarSymbol.next = null;
//        }
//        for (;;)
//        {
//            paramZBarSymbol = localZBarSymbol;
//            break;
//            if (paramZBarSymbol.data_alloc != 0)
//            {
//                paramZBarSymbol.data = null;
//                paramZBarSymbol.datalen = 0;
//            }
//            if (paramZBarSymbol.syms != null)
//            {
//                if ((ZBarRefcnt._zbar_refcnt(paramZBarSymbol.syms.refcnt, -1) != 0) && (!$assertionsDisabled)) {
//                    throw new AssertionError();
//                }
//                _zbar_image_scanner_recycle_syms(paramZBarImageScanner, paramZBarSymbol.syms.head);
//                paramZBarSymbol.syms.head = null;
//                ZBarSymbol._zbar_symbol_set_free(paramZBarSymbol.syms);
//                paramZBarSymbol.syms = null;
//            }
//            for (int i = 0;; i++)
//            {
//                if (i >= 5) {}
//                while (paramZBarSymbol.data_alloc < 1 << i * 2)
//                {
//                    if (i != 5) {
//                        break label208;
//                    }
//                    if (($assertionsDisabled) || (paramZBarSymbol.data != null)) {
//                        break;
//                    }
//                    throw new AssertionError();
//                }
//            }
//            paramZBarSymbol.data = null;
//            paramZBarSymbol.data_alloc = 0;
//            i = 0;
//            label208:
//            RecycleBucket localRecycleBucket = paramZBarImageScanner.recycle[i];
//            localRecycleBucket.nsyms = (1 + localRecycleBucket.nsyms);
//            paramZBarSymbol.next = localRecycleBucket.head;
//            localRecycleBucket.head = paramZBarSymbol;
//        }
//    }
//
//    static ZBarSymbol cache_lookup(ZBarImageScanner paramZBarImageScanner, ZBarSymbol paramZBarSymbol)
//    {
//        Object localObject = paramZBarImageScanner.cache;
//        for (;;)
//        {
//            if (localObject == null) {}
//            while ((((ZBarSymbol)localObject).type == paramZBarSymbol.type) && (((ZBarSymbol)localObject).datalen == paramZBarSymbol.datalen) && (!ZBarMemCmp.memcmp(((ZBarSymbol)localObject).data, paramZBarSymbol.data, paramZBarSymbol.datalen))) {
//                return (ZBarSymbol)localObject;
//            }
//            if (paramZBarSymbol.time - ((ZBarSymbol)localObject).time > 4000L)
//            {
//                ZBarSymbol localZBarSymbol = ((ZBarSymbol)localObject).next;
//                ((ZBarSymbol)localObject).next = null;
//                _zbar_image_scanner_recycle_syms(paramZBarImageScanner, (ZBarSymbol)localObject);
//                localObject = localZBarSymbol;
//            }
//            else
//            {
//                localObject = ((ZBarSymbol)localObject).next;
//            }
//        }
//    }
//
//    static void cache_sym(ZBarImageScanner paramZBarImageScanner, ZBarSymbol paramZBarSymbol)
//    {
//        if (paramZBarImageScanner.enable_cache)
//        {
//            ZBarSymbol localZBarSymbol = cache_lookup(paramZBarImageScanner, paramZBarSymbol);
//            if (localZBarSymbol == null)
//            {
//                localZBarSymbol = _zbar_image_scanner_alloc_sym(paramZBarImageScanner, paramZBarSymbol.type, 1 + paramZBarSymbol.datalen);
//                localZBarSymbol.configs = paramZBarSymbol.configs;
//                localZBarSymbol.modifiers = paramZBarSymbol.modifiers;
//                System.arraycopy(paramZBarSymbol.data, 0, localZBarSymbol.data, 0, paramZBarSymbol.datalen);
//                paramZBarSymbol.time -= 2000L;
//                localZBarSymbol.cache_count = 0;
//                localZBarSymbol.next = paramZBarImageScanner.cache;
//                paramZBarImageScanner.cache = localZBarSymbol;
//            }
//            int i = (int)(paramZBarSymbol.time - localZBarSymbol.time);
//            localZBarSymbol.time = paramZBarSymbol.time;
//            int j;
//            int k;
//            label134:
//            int m;
//            label144:
//            int n;
//            if (i < 1000)
//            {
//                j = 1;
//                if (i < 2000) {
//                    break label197;
//                }
//                k = 1;
//                if (localZBarSymbol.cache_count < 0) {
//                    break label203;
//                }
//                m = 1;
//                if (((m != 0) || (j != 0)) && (k == 0)) {
//                    break label209;
//                }
//                n = ZBarSymbol._zbar_get_symbol_hash(paramZBarSymbol.type);
//            }
//            for (localZBarSymbol.cache_count = (-paramZBarImageScanner.sym_configs[0][n]);; localZBarSymbol.cache_count = (1 + localZBarSymbol.cache_count)) {
//                label197:
//                label203:
//                label209:
//                do
//                {
//                    paramZBarSymbol.cache_count = localZBarSymbol.cache_count;
//                    return;
//                    j = 0;
//                    break;
//                    k = 0;
//                    break label134;
//                    m = 0;
//                    break label144;
//                } while ((m == 0) && (j == 0));
//            }
//        }
//        paramZBarSymbol.cache_count = 0;
//    }
//
//    static void qr_handler(ZBarImageScanner paramZBarImageScanner)
//    {
//        qr_finder_line localqr_finder_line = QrFinder._zbar_decoder_get_qr_finder_line(paramZBarImageScanner.dcode);
//        assert (localqr_finder_line != null);
//        int i = paramZBarImageScanner.scn.zbar_scanner_get_edge(localqr_finder_line.pos[0], 2);
//        localqr_finder_line.boffs = (i - paramZBarImageScanner.scn.zbar_scanner_get_edge(localqr_finder_line.boffs, 2));
//        localqr_finder_line.len = paramZBarImageScanner.scn.zbar_scanner_get_edge(localqr_finder_line.len, 2);
//        localqr_finder_line.eoffs = (paramZBarImageScanner.scn.zbar_scanner_get_edge(localqr_finder_line.eoffs, 2) - localqr_finder_line.len);
//        localqr_finder_line.len -= i;
//        int j = QrCode.QR_FIXED(paramZBarImageScanner.umin, 0) + i * paramZBarImageScanner.du;
//        if (paramZBarImageScanner.du < 0)
//        {
//            int n = localqr_finder_line.boffs;
//            localqr_finder_line.boffs = localqr_finder_line.eoffs;
//            localqr_finder_line.eoffs = n;
//            j -= localqr_finder_line.len;
//        }
//        if (paramZBarImageScanner.dx == 0) {}
//        for (int k = 1;; k = 0)
//        {
//            localqr_finder_line.pos[k] = j;
//            int[] arrayOfInt = localqr_finder_line.pos;
//            int m = 0;
//            if (k == 0) {
//                m = 1;
//            }
//            arrayOfInt[m] = QrCode.QR_FIXED(paramZBarImageScanner.v, 1);
//            QrDec._zbar_qr_found_line(paramZBarImageScanner.qr, k, localqr_finder_line);
//            return;
//        }
//    }
//
//    static void quiet_border(ZBarImageScanner paramZBarImageScanner)
//    {
//        ZBarScanner localZBarScanner = paramZBarImageScanner.scn;
//        localZBarScanner.zbar_scanner_flush();
//        localZBarScanner.zbar_scanner_flush();
//        localZBarScanner.zbar_scanner_new_scan();
//    }
//
//    static int recycle_syms(ZBarImageScanner paramZBarImageScanner, ZBarSymbolSet paramZBarSymbolSet)
//    {
//        if (ZBarRefcnt._zbar_refcnt(paramZBarSymbolSet.refcnt, -1) != 0) {
//            return 1;
//        }
//        _zbar_image_scanner_recycle_syms(paramZBarImageScanner, paramZBarSymbolSet.head);
//        paramZBarSymbolSet.tail = null;
//        paramZBarSymbolSet.head = null;
//        paramZBarSymbolSet.nsyms = 0;
//        return 0;
//    }
//
//    static void zbar_image_scanner_destroy(ZBarImageScanner paramZBarImageScanner)
//    {
//        if (paramZBarImageScanner.syms != null)
//        {
//            if (paramZBarImageScanner.syms.refcnt.intValue() == 0) {
//                break label78;
//            }
//            ZBarSymbol.zbar_symbol_set_ref(paramZBarImageScanner.syms, -1);
//        }
//        int i;
//        for (;;)
//        {
//            paramZBarImageScanner.syms = null;
//            paramZBarImageScanner.scn = null;
//            paramZBarImageScanner.dcode.buf = null;
//            paramZBarImageScanner.dcode = null;
//            i = 0;
//            if (i < 5) {
//                break;
//            }
//            if (paramZBarImageScanner.qr != null)
//            {
//                QrDec._zbar_qr_destroy(paramZBarImageScanner.qr);
//                paramZBarImageScanner.qr = null;
//            }
//            return;
//            label78:
//            ZBarSymbol._zbar_symbol_set_free(paramZBarImageScanner.syms);
//        }
//        ZBarSymbol localZBarSymbol;
//        for (Object localObject = paramZBarImageScanner.recycle[i].head;; localObject = localZBarSymbol)
//        {
//            if (localObject == null)
//            {
//                i++;
//                break;
//            }
//            localZBarSymbol = ((ZBarSymbol)localObject).next;
//            ZBarSymbol._zbar_symbol_free((ZBarSymbol)localObject);
//        }
//    }
//
//    static void zbar_image_scanner_recycle_image(ZBarImageScanner paramZBarImageScanner, zbar_image_t paramzbar_image_t)
//    {
//        ZBarSymbolSet localZBarSymbolSet1 = paramZBarImageScanner.syms;
//        if ((localZBarSymbolSet1 != null) && (localZBarSymbolSet1.refcnt.intValue() != 0) && (recycle_syms(paramZBarImageScanner, localZBarSymbolSet1) != 0)) {
//            paramZBarImageScanner.syms = null;
//        }
//        ZBarSymbolSet localZBarSymbolSet2 = paramzbar_image_t.syms;
//        paramzbar_image_t.syms = null;
//        if (((localZBarSymbolSet2 == null) || (recycle_syms(paramZBarImageScanner, localZBarSymbolSet2) == 0)) && (localZBarSymbolSet2 != null))
//        {
//            if (paramZBarImageScanner.syms != null) {
//                ZBarSymbol._zbar_symbol_set_free(localZBarSymbolSet2);
//            }
//        }
//        else {
//            return;
//        }
//        paramZBarImageScanner.syms = localZBarSymbolSet2;
//    }
//
//    static int zbar_scan_image(ZBarImageScanner paramZBarImageScanner, zbar_image_t paramzbar_image_t)
//    {
//        ZBarScanner localZBarScanner = paramZBarImageScanner.scn;
//        paramZBarImageScanner.time = new Date().getTime();
//        QrDec._zbar_qr_reset(paramZBarImageScanner.qr);
//        if ((paramzbar_image_t.format != ZBar.zbar_fourcc('Y', '8', '0', '0')) && (paramzbar_image_t.format != ZBar.zbar_fourcc('G', 'R', 'E', 'Y'))) {
//            return -1;
//        }
//        paramZBarImageScanner.img = paramzbar_image_t;
//        zbar_image_scanner_recycle_image(paramZBarImageScanner, paramzbar_image_t);
//        ZBarSymbolSet localZBarSymbolSet = paramZBarImageScanner.syms;
//        if (localZBarSymbolSet == null)
//        {
//            localZBarSymbolSet = ZBarSymbol._zbar_symbol_set_create();
//            paramZBarImageScanner.syms = localZBarSymbolSet;
//            ZBarSymbol.zbar_symbol_set_ref(localZBarSymbolSet, 1);
//        }
//        int i;
//        int j;
//        int k;
//        for (;;)
//        {
//            paramzbar_image_t.syms = localZBarSymbolSet;
//            i = paramzbar_image_t.width;
//            j = paramzbar_image_t.height;
//            k = paramzbar_image_t.crop_x + paramzbar_image_t.crop_w;
//            if (($assertionsDisabled) || (k <= i)) {
//                break;
//            }
//            throw new AssertionError();
//            ZBarSymbol.zbar_symbol_set_ref(localZBarSymbolSet, 2);
//        }
//        int m = paramzbar_image_t.crop_y + paramzbar_image_t.crop_h;
//        assert (m <= j);
//        byte[] arrayOfByte = paramzbar_image_t.data;
//        localZBarScanner.zbar_scanner_new_scan();
//        int n = CFG(paramZBarImageScanner, ZbarConfig.ZBAR_CFG_Y_DENSITY);
//        int i23;
//        int i24;
//        int i25;
//        if (n > 0)
//        {
//            int i20 = (1 + (-1 + paramzbar_image_t.crop_h) % n) / 2;
//            if (i20 > paramzbar_image_t.crop_h / 2) {
//                i20 = paramzbar_image_t.crop_h / 2;
//            }
//            int i21 = i20 + paramzbar_image_t.crop_y;
//            assert (i21 <= j);
//            paramZBarImageScanner.dy = 0;
//            int i22 = paramzbar_image_t.crop_x;
//            i23 = 0 + i22;
//            i24 = 0 + i21;
//            i25 = 0 + (i22 + i21 * i);
//            paramZBarImageScanner.v = i24;
//            if (i24 < m) {
//                break label408;
//            }
//        }
//        int i1;
//        int i9;
//        label408:
//        int i26;
//        label430:
//        int i28;
//        int i29;
//        int i30;
//        do
//        {
//            paramZBarImageScanner.dx = 0;
//            i1 = CFG(paramZBarImageScanner, ZbarConfig.ZBAR_CFG_X_DENSITY);
//            if (i1 <= 0) {
//                break label676;
//            }
//            int i8 = (1 + (-1 + paramzbar_image_t.crop_w) % i1) / 2;
//            if (i8 > paramzbar_image_t.crop_w / 2) {
//                i8 = paramzbar_image_t.crop_w / 2;
//            }
//            i9 = i8 + paramzbar_image_t.crop_x;
//            if (($assertionsDisabled) || (i9 <= i)) {
//                break label633;
//            }
//            throw new AssertionError();
//            i26 = paramzbar_image_t.crop_x;
//            paramZBarImageScanner.du = 1;
//            paramZBarImageScanner.dx = 1;
//            paramZBarImageScanner.umin = i26;
//            if (i23 < k) {
//                break;
//            }
//            quiet_border(paramZBarImageScanner);
//            i28 = i23 - 1;
//            i29 = i24 + n;
//            i30 = i25 + (-1 + n * i);
//            paramZBarImageScanner.v = i29;
//        } while (i29 >= m);
//        paramZBarImageScanner.du = -1;
//        paramZBarImageScanner.dx = -1;
//        paramZBarImageScanner.umin = k;
//        for (;;)
//        {
//            if (i28 < i26)
//            {
//                quiet_border(paramZBarImageScanner);
//                i23 = i28 + 1;
//                i24 = i29 + n;
//                i25 = i30 + (1 + n * i);
//                paramZBarImageScanner.v = i24;
//                break;
//                int i27 = 0xFF & arrayOfByte[i25];
//                i23++;
//                i24 += 0;
//                i25 += 1 + 0 * i;
//                localZBarScanner.zbar_scan_y(i27);
//                break label430;
//            }
//            if ((i29 >= 69) && (i28 == 33)) {
//                i28 += 0;
//            }
//            int i31 = 0xFF & arrayOfByte[i30];
//            i28--;
//            i29 += 0;
//            i30 += -1 + 0 * i;
//            localZBarScanner.zbar_scan_y(i31);
//        }
//        label633:
//        int i10 = paramzbar_image_t.crop_y;
//        int i11 = 0 + i9;
//        int i12 = 0 + i10;
//        int i13 = 0 + (i9 + i10 * i);
//        paramZBarImageScanner.v = i11;
//        label676:
//        int i2;
//        label723:
//        int i3;
//        int i4;
//        ZBarSymbol localZBarSymbol1;
//        Object localObject1;
//        Object localObject2;
//        ZBarSymbol localZBarSymbol4;
//        if (i11 >= k)
//        {
//            paramZBarImageScanner.dy = 0;
//            paramZBarImageScanner.img = null;
//            QrDec._zbar_qr_decode(paramZBarImageScanner.qr, paramZBarImageScanner, paramzbar_image_t);
//            if ((paramZBarImageScanner.enable_cache) || ((i1 != 1) && (CFG(paramZBarImageScanner, ZbarConfig.ZBAR_CFG_Y_DENSITY) != 1))) {
//                break label1010;
//            }
//            i2 = 1;
//            i3 = 0;
//            i4 = 0;
//            if (localZBarSymbolSet.nsyms == 0) {
//                break label1510;
//            }
//            localZBarSymbol1 = localZBarSymbolSet.head;
//            if (localZBarSymbol1 != null) {
//                break label1016;
//            }
//            if ((i3 != 1) || (i4 != 1) || (paramZBarImageScanner.ean_config == 0)) {
//                break label1510;
//            }
//            localObject1 = null;
//            localObject2 = null;
//            localZBarSymbol4 = localZBarSymbolSet.head;
//        }
//        for (;;)
//        {
//            if (localZBarSymbol4 == null)
//            {
//                if (($assertionsDisabled) || (localObject1 != null)) {
//                    break label1372;
//                }
//                throw new AssertionError();
//                int i14 = paramzbar_image_t.crop_y;
//                paramZBarImageScanner.du = 1;
//                paramZBarImageScanner.dy = 1;
//                paramZBarImageScanner.umin = i14;
//                label824:
//                int i16;
//                int i17;
//                int i18;
//                if (i12 >= m)
//                {
//                    quiet_border(paramZBarImageScanner);
//                    i16 = i11 + i1;
//                    i17 = i12 - 1;
//                    i18 = i13 + (i1 + -1 * i);
//                    paramZBarImageScanner.v = i16;
//                    if (i16 >= k) {
//                        break label676;
//                    }
//                    paramZBarImageScanner.du = -1;
//                    paramZBarImageScanner.dy = -1;
//                    paramZBarImageScanner.umin = m;
//                }
//                for (;;)
//                {
//                    if (i17 < i14)
//                    {
//                        quiet_border(paramZBarImageScanner);
//                        i11 = i16 + i1;
//                        i12 = i17 + 1;
//                        i13 = i18 + (i1 + 1 * i);
//                        paramZBarImageScanner.v = i11;
//                        break;
//                        int i15 = 0xFF & arrayOfByte[i13];
//                        i11 += 0;
//                        i12++;
//                        i13 += 0 + 1 * i;
//                        localZBarScanner.zbar_scan_y(i15);
//                        break label824;
//                    }
//                    int i19 = 0xFF & arrayOfByte[i18];
//                    i16 += 0;
//                    i17--;
//                    i18 += 0 + -1 * i;
//                    localZBarScanner.zbar_scan_y(i19);
//                }
//                label1010:
//                i2 = 0;
//                break label723;
//                label1016:
//                ZBarSymbol localZBarSymbol2 = localZBarSymbol1;
//                if ((localZBarSymbol2.cache_count <= 0) && (((EnumXVal.value(localZBarSymbol2.type) < EnumXVal.value(ZBarSymbolType.ZBAR_COMPOSITE)) && (EnumXVal.value(localZBarSymbol2.type) > EnumXVal.value(ZBarSymbolType.ZBAR_PARTIAL))) || (localZBarSymbol2.type == ZBarSymbolType.ZBAR_DATABAR) || (localZBarSymbol2.type == ZBarSymbolType.ZBAR_DATABAR_EXP) || (localZBarSymbol2.type == ZBarSymbolType.ZBAR_CODABAR)))
//                {
//                    if (((localZBarSymbol2.type == ZBarSymbolType.ZBAR_CODABAR) || (i2 != 0)) && (localZBarSymbol2.quality < 4))
//                    {
//                        if (paramZBarImageScanner.enable_cache)
//                        {
//                            ZBarSymbol localZBarSymbol3 = cache_lookup(paramZBarImageScanner, localZBarSymbol2);
//                            if (localZBarSymbol3 == null) {
//                                break label1188;
//                            }
//                            localZBarSymbol3.cache_count = (-1 + localZBarSymbol3.cache_count);
//                        }
//                        label1188:
//                        while ($assertionsDisabled)
//                        {
//                            localZBarSymbol1 = localZBarSymbol2.next;
//                            int i5 = -1 + localZBarSymbolSet.nsyms;
//                            localZBarSymbolSet.nsyms = i5;
//                            localZBarSymbol2.next = null;
//                            _zbar_image_scanner_recycle_syms(paramZBarImageScanner, localZBarSymbol2);
//                            break;
//                        }
//                        throw new AssertionError();
//                    }
//                    if ((EnumXVal.value(localZBarSymbol2.type) < EnumXVal.value(ZBarSymbolType.ZBAR_COMPOSITE)) && (localZBarSymbol2.type != ZBarSymbolType.ZBAR_ISBN10))
//                    {
//                        if (EnumXVal.value(localZBarSymbol2.type) <= EnumXVal.value(ZBarSymbolType.ZBAR_EAN5)) {
//                            break label1260;
//                        }
//                        i3++;
//                    }
//                }
//                for (;;)
//                {
//                    localZBarSymbol1 = localZBarSymbol2.next;
//                    break;
//                    label1260:
//                    i4++;
//                }
//            }
//            ZBarSymbol localZBarSymbol5 = localZBarSymbol4;
//            if ((EnumXVal.value(localZBarSymbol5.type) < EnumXVal.value(ZBarSymbolType.ZBAR_COMPOSITE)) && (EnumXVal.value(localZBarSymbol5.type) > EnumXVal.value(ZBarSymbolType.ZBAR_PARTIAL)))
//            {
//                localZBarSymbol4 = localZBarSymbol5.next;
//                int i6 = -1 + localZBarSymbolSet.nsyms;
//                localZBarSymbolSet.nsyms = i6;
//                localZBarSymbol5.next = null;
//                if (EnumXVal.value(localZBarSymbol5.type) <= EnumXVal.value(ZBarSymbolType.ZBAR_EAN5)) {
//                    localObject2 = localZBarSymbol5;
//                } else {
//                    localObject1 = localZBarSymbol5;
//                }
//            }
//            else
//            {
//                localZBarSymbol4 = localZBarSymbol5.next;
//            }
//        }
//        label1372:
//        assert (localObject2 != null);
//        int i7 = 1 + (((ZBarSymbol)localObject1).datalen + ((ZBarSymbol)localObject2).datalen);
//        ZBarSymbol localZBarSymbol6 = _zbar_image_scanner_alloc_sym(paramZBarImageScanner, ZBarSymbolType.ZBAR_COMPOSITE, i7);
//        localZBarSymbol6.orient = ((ZBarSymbol)localObject1).orient;
//        localZBarSymbol6.syms = ZBarSymbol._zbar_symbol_set_create();
//        ZBarMemCmp.memcpy(localZBarSymbol6.data, ((ZBarSymbol)localObject1).data, ((ZBarSymbol)localObject1).datalen);
//        ZBarMemCmp.memcpy(localZBarSymbol6.data, ((ZBarSymbol)localObject1).datalen, ((ZBarSymbol)localObject2).data, 1 + ((ZBarSymbol)localObject2).datalen);
//        localZBarSymbol6.syms.head = ((ZBarSymbol)localObject1);
//        ((ZBarSymbol)localObject1).next = ((ZBarSymbol)localObject2);
//        localZBarSymbol6.syms.nsyms = 2;
//        _zbar_image_scanner_add_sym(paramZBarImageScanner, localZBarSymbol6);
//        label1510:
//        if ((localZBarSymbolSet.nsyms != 0) && (paramZBarImageScanner.handler != null)) {
//            paramZBarImageScanner.handler.img_data_handler(paramzbar_image_t, paramZBarImageScanner.userdata);
//        }
//        return localZBarSymbolSet.nsyms;
//    }
//
//    ZBarImageScanner zbar_image_scanner_create()
//    {
//        ZBarImageScanner localZBarImageScanner = new ZBarImageScanner();
//        localZBarImageScanner.dcode = ZBarDecoder.zbar_decoder_create();
//        localZBarImageScanner.scn = ZBarScanner.zbar_scanner_create(localZBarImageScanner.dcode);
//        if ((localZBarImageScanner.dcode == null) || (localZBarImageScanner.scn == null))
//        {
//            zbar_image_scanner_destroy(localZBarImageScanner);
//            return null;
//        }
//        localZBarImageScanner.dcode.zbar_decoder_set_userdata(localZBarImageScanner);
//        SymbolHandler localSymbolHandler = new SymbolHandler();
//        localZBarImageScanner.dcode.zbar_decoder_set_handler(localSymbolHandler);
//        localZBarImageScanner.qr = QrDec._zbar_qr_create();
//        CFG(localZBarImageScanner, ZbarConfig.ZBAR_CFG_X_DENSITY, 1);
//        CFG(localZBarImageScanner, ZbarConfig.ZBAR_CFG_Y_DENSITY, 1);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_NONE, ZbarConfig.ZBAR_CFG_POSITION, 1);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_NONE, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 2);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_QRCODE, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 0);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_CODE128, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 0);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_CODE93, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 0);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_CODE39, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 0);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_CODABAR, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 1);
//        zbar_image_scanner_set_config(localZBarImageScanner, ZBarSymbolType.ZBAR_COMPOSITE, ZbarConfig.ZBAR_CFG_UNCERTAINTY, 0);
//        return localZBarImageScanner;
//    }
//
//    void zbar_image_scanner_enable_cache(ZBarImageScanner paramZBarImageScanner, int paramInt)
//    {
//        if (paramZBarImageScanner.cache != null)
//        {
//            _zbar_image_scanner_recycle_syms(paramZBarImageScanner, paramZBarImageScanner.cache);
//            paramZBarImageScanner.cache = null;
//        }
//        if (paramInt != 0) {}
//        for (boolean bool = true;; bool = false)
//        {
//            paramZBarImageScanner.enable_cache = bool;
//            return;
//        }
//    }
//
//    ZBarSymbolSet zbar_image_scanner_get_results(ZBarImageScanner paramZBarImageScanner)
//    {
//        return paramZBarImageScanner.syms;
//    }
//
//    int zbar_image_scanner_set_config(ZBarImageScanner paramZBarImageScanner, ZBarSymbolType paramZBarSymbolType, ZbarConfig paramZbarConfig, int paramInt)
//    {
//        int m;
//        if (((paramZBarSymbolType == ZBarSymbolType.ZBAR_NONE) || (paramZBarSymbolType == ZBarSymbolType.ZBAR_COMPOSITE)) && (paramZbarConfig == ZbarConfig.ZBAR_CFG_ENABLE)) {
//            if (paramInt == 0)
//            {
//                m = 0;
//                paramZBarImageScanner.ean_config = m;
//                if (paramZBarSymbolType == ZBarSymbolType.ZBAR_NONE) {
//                    break label50;
//                }
//            }
//        }
//        for (;;)
//        {
//            return 0;
//            m = 1;
//            break;
//            label50:
//            if (EnumXVal.value(paramZbarConfig) < EnumXVal.value(ZbarConfig.ZBAR_CFG_UNCERTAINTY)) {
//                return ZBarDecoder.zbar_decoder_set_config(paramZBarImageScanner.dcode, paramZBarSymbolType, paramZbarConfig, paramInt);
//            }
//            if (EnumXVal.value(paramZbarConfig) >= EnumXVal.value(ZbarConfig.ZBAR_CFG_POSITION)) {
//                break label177;
//            }
//            if (EnumXVal.value(paramZbarConfig) > EnumXVal.value(ZbarConfig.ZBAR_CFG_UNCERTAINTY)) {
//                return 1;
//            }
//            int i = EnumXVal.value(paramZbarConfig) - EnumXVal.value(ZbarConfig.ZBAR_CFG_UNCERTAINTY);
//            if (EnumXVal.value(paramZBarSymbolType) > EnumXVal.value(ZBarSymbolType.ZBAR_PARTIAL))
//            {
//                int k = ZBarSymbol._zbar_get_symbol_hash(paramZBarSymbolType);
//                paramZBarImageScanner.sym_configs[i][k] = paramInt;
//                return 0;
//            }
//            for (int j = 0; j < 20; j++) {
//                paramZBarImageScanner.sym_configs[i][j] = paramInt;
//            }
//        }
//        label177:
//        if (EnumXVal.value(paramZBarSymbolType) > EnumXVal.value(ZBarSymbolType.ZBAR_PARTIAL)) {
//            return 1;
//        }
//        if ((EnumXVal.value(paramZbarConfig) >= EnumXVal.value(ZbarConfig.ZBAR_CFG_X_DENSITY)) && (EnumXVal.value(paramZbarConfig) <= EnumXVal.value(ZbarConfig.ZBAR_CFG_Y_DENSITY)))
//        {
//            CFG(paramZBarImageScanner, paramZbarConfig, paramInt);
//            return 0;
//        }
//        if (EnumXVal.value(paramZbarConfig) > EnumXVal.value(ZbarConfig.ZBAR_CFG_POSITION)) {
//            return 1;
//        }
//        ZbarConfig localZbarConfig = EnumXVal.ConfigInt(EnumXVal.value(paramZbarConfig) - EnumXVal.value(ZbarConfig.ZBAR_CFG_POSITION));
//        if (paramInt == 0)
//        {
//            paramZBarImageScanner.config &= (0xFFFFFFFF ^ 1 << EnumXVal.value(localZbarConfig));
//            return 0;
//        }
//        if (paramInt == 1)
//        {
//            paramZBarImageScanner.config |= 1 << EnumXVal.value(localZbarConfig);
//            return 0;
//        }
//        return 1;
//    }
//
//    ZBarImageDataHandler zbar_image_scanner_set_data_handler(ZBarImageScanner paramZBarImageScanner, ZBarImageDataHandler paramZBarImageDataHandler, byte[] paramArrayOfByte)
//    {
//        ZBarImageDataHandler localZBarImageDataHandler = paramZBarImageScanner.handler;
//        paramZBarImageScanner.handler = paramZBarImageDataHandler;
//        paramZBarImageScanner.userdata = paramArrayOfByte;
//        return localZBarImageDataHandler;
//    }
//
//    public int zbar_scans_image(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, String[] paramArrayOfString1, String[] paramArrayOfString2, int[][] paramArrayOfInt1, int[][] paramArrayOfInt2)
//    {
//        zbar_image_t localzbar_image_t = zbar_image_t.zbar_image_create();
//        zbar_image_t.zbar_image_set_format(localzbar_image_t, ZBar.zbar_fourcc('Y', '8', '0', '0'));
//        zbar_image_t.zbar_image_set_size(localzbar_image_t, paramInt2, paramInt3);
//        zbar_image_t.zbar_image_set_data(localzbar_image_t, (byte[])paramArrayOfByte.clone(), paramInt1, 0);
//        ZBarImageScanner localZBarImageScanner = zbar_image_scanner_create();
//        int i = zbar_scan_image(localZBarImageScanner, localzbar_image_t);
//        int j = 0;
//        if (i == 0)
//        {
//            zbar_image_scanner_destroy(localZBarImageScanner);
//            zbar_image_t.zbar_image_destroy(localzbar_image_t);
//            return -1;
//        }
//        ZBarSymbol localZBarSymbol1 = zbar_image_t.zbar_image_first_symbol(localzbar_image_t);
//        ZBarSymbol localZBarSymbol2 = null;
//        Point[] arrayOfPoint;
//        if (localZBarSymbol1 == null)
//        {
//            if (localZBarSymbol2 == null) {
//                break label261;
//            }
//            paramArrayOfString2.length;
//            if (paramArrayOfString1.length != 0)
//            {
//                paramArrayOfString1[0] = localZBarSymbol2.sd_data;
//                j = localZBarSymbol2.sd_ecc_level;
//            }
//            if (paramArrayOfInt1 != null) {
//                arrayOfPoint = localZBarSymbol2.pts;
//            }
//        }
//        for (int k = 0;; k++)
//        {
//            if ((k >= localZBarSymbol2.npts) || (k >= 4) || (arrayOfPoint == null))
//            {
//                zbar_image_scanner_destroy(localZBarImageScanner);
//                zbar_image_t.zbar_image_destroy(localzbar_image_t);
//                return j;
//                if (localZBarSymbol1.type == ZBarSymbolType.SD4_CODE)
//                {
//                    localZBarSymbol2 = localZBarSymbol1;
//                    ZBarSymbol.zbar_symbol_get_type(localZBarSymbol1);
//                    j = ZBarSymbol.zbar_symbol_get_quality(localZBarSymbol1);
//                }
//                localZBarSymbol1 = ZBarSymbol.zbar_symbol_next(localZBarSymbol1);
//                break;
//            }
//            paramArrayOfInt1[0][(k << 1)] = (arrayOfPoint[k].x >> 2);
//            paramArrayOfInt1[0][(1 + (k << 1))] = (arrayOfPoint[k].y >> 2);
//        }
//        label261:
//        zbar_image_scanner_destroy(localZBarImageScanner);
//        zbar_image_t.zbar_image_destroy(localzbar_image_t);
//        return -1;
//    }
//
//    class SymbolHandler
//            implements ZBarDecoderHandler
//    {
//        static
//        {
//            if (!ZBarImageScanner.class.desiredAssertionStatus()) {}
//            for (boolean bool = true;; bool = false)
//            {
//                $assertionsDisabled = bool;
//                return;
//            }
//        }
//
//        SymbolHandler() {}
//
//        public void decode(ZBarDecoder paramZBarDecoder)
//        {
//            ZBarImageScanner localZBarImageScanner = (ZBarImageScanner)paramZBarDecoder.zbar_decoder_get_userdata();
//            ZBarSymbolType localZBarSymbolType = paramZBarDecoder.zbar_decoder_get_type();
//            if (localZBarSymbolType == ZBarSymbolType.ZBAR_QRCODE)
//            {
//                ZBarImageScanner.qr_handler(localZBarImageScanner);
//                return;
//            }
//            assert (localZBarSymbolType != ZBarSymbolType.ZBAR_QRCODE);
//            int i = ZBarImageScanner.TEST_CFG(localZBarImageScanner, ZbarConfig.ZBAR_CFG_POSITION);
//            int j = 0;
//            int k = 0;
//            int i3;
//            label115:
//            byte[] arrayOfByte;
//            int m;
//            if (i != 0)
//            {
//                int i2 = (int)localZBarImageScanner.scn.zbar_scanner_get_width();
//                i3 = localZBarImageScanner.umin + localZBarImageScanner.du * localZBarImageScanner.scn.zbar_scanner_get_edge(i2, 0);
//                if (localZBarImageScanner.dx != 0)
//                {
//                    j = i3;
//                    k = localZBarImageScanner.v;
//                }
//            }
//            else
//            {
//                if (EnumXVal.value(localZBarSymbolType) <= EnumXVal.value(ZBarSymbolType.ZBAR_PARTIAL)) {
//                    break label309;
//                }
//                arrayOfByte = ZBarImageScanner.this.dcode.zbar_decoder_get_data();
//                m = ZBarImageScanner.this.dcode.zbar_decoder_get_data_length();
//            }
//            for (ZBarSymbol localZBarSymbol1 = localZBarImageScanner.syms.head;; localZBarSymbol1 = localZBarSymbol1.next)
//            {
//                if (localZBarSymbol1 == null)
//                {
//                    ZBarSymbol localZBarSymbol2 = ZBarImageScanner._zbar_image_scanner_alloc_sym(localZBarImageScanner, localZBarSymbolType, m + 1);
//                    localZBarSymbol2.configs = ZBarDecoder.zbar_decoder_get_configs(ZBarImageScanner.this.dcode, localZBarSymbolType);
//                    localZBarSymbol2.modifiers = ZBarImageScanner.this.dcode.zbar_decoder_get_modifiers();
//                    System.arraycopy(arrayOfByte, 0, localZBarSymbol2.data, 0, m + 1);
//                    if (ZBarImageScanner.TEST_CFG(localZBarImageScanner, ZbarConfig.ZBAR_CFG_POSITION) != 0) {
//                        ZBarSymbol.sym_add_point(localZBarSymbol2, j, k);
//                    }
//                    int n = ZBarImageScanner.this.dcode.zbar_decoder_get_direction();
//                    if (n != 0)
//                    {
//                        int i1 = 0x2 & (n ^ localZBarImageScanner.du);
//                        if (localZBarImageScanner.dy != 0) {
//                            i1++;
//                        }
//                        localZBarSymbol2.orient = EnumXVal.OrientationInt(i1);
//                    }
//                    ZBarImageScanner._zbar_image_scanner_add_sym(localZBarImageScanner, localZBarSymbol2);
//                    return;
//                    j = localZBarImageScanner.v;
//                    k = i3;
//                    break label115;
//                    label309:
//                    break;
//                }
//                if ((localZBarSymbol1.type == localZBarSymbolType) && (localZBarSymbol1.datalen == m) && (ZBarMemCmp.memcmp(localZBarSymbol1.data, arrayOfByte, m)))
//                {
//                    localZBarSymbol1.quality = (1 + localZBarSymbol1.quality);
//                    if (ZBarImageScanner.TEST_CFG(localZBarImageScanner, ZbarConfig.ZBAR_CFG_POSITION) == 0) {
//                        break;
//                    }
//                    ZBarSymbol.sym_add_point(localZBarSymbol1, j, k);
//                    return;
//                }
//            }
//        }
//    }
}