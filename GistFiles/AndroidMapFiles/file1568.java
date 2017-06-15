package service;

import service.meta.TestMeta;

import java.util.List;

/**
 * Description:
 * <p/>
 * Date: 14-2-1
 * Author: Administrator
 */
public interface HelloWorld {

    public List<TestMeta> sayHello(List<String> msg1, TestMeta testMeta);
}
