package stub.impl;

import service.HelloWorld;
import stub.BaseStub;

public class HelloWorldImpl extends BaseStub implements HelloWorld {
    @Override
    public java.util.List sayHello(java.util.List p0, service.meta.TestMeta p1) {
        return (java.util.List) remoteInvoke("helloWorld", "sayHello",
                new Object[]{p0, p1});
    }
}