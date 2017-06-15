package service.impl;

import jbolt.core.utilities.ObjectUtilities;
import service.HelloWorld;
import service.meta.TestMeta;
import tools.annotation.RemoteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p/>
 * Date: 14-2-1
 * Author: Administrator
 */
@RemoteService
public class HelloWorldImpl implements HelloWorld {

    @Override
    public List<TestMeta> sayHello(List<String> strings, TestMeta testMeta) {
        List<TestMeta> resList = new ArrayList<TestMeta>();
        for (int i = 0; i < testMeta.getI(); i++) {
            TestMeta testMeta1 = new TestMeta();
            ObjectUtilities.cloneFlattenProperties(testMeta, testMeta1);
            testMeta1.setMsg(strings.get(0));
            resList.add(testMeta1);
        }
        return resList;
    }
}
