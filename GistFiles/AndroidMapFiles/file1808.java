package invoke;

import jbolt.core.ioc.MKernelIOCFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class InitContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        MKernelIOCFactory.initialize(new String[]{
                "conf/services/Server_Service.xml",
                "conf/services/Log4j_Service.xml"
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
