package com.ctrlsmart.bcDecode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2015/4/12.
 */
public class ZBarSymbolSet
{
    public ZBarSymbol head = null;
    public int nsyms = 0;
    public AtomicInteger refcnt = new AtomicInteger();
    public ZBarSymbol tail = null;
}

