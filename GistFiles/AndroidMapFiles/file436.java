package tools;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jbolt.core.ioc.MKernelIOCFactory;
import jbolt.core.ioc.impl.MKernelServiceRepository;
import jbolt.core.ioc.meta.MKernelServiceDefinition;
import jbolt.core.trace.TraceComponentManifest;
import jbolt.core.trace.TraceManager;
import jbolt.core.trace.TraceProducer;
import jbolt.core.utilities.FileUtilities;
import jbolt.core.utilities.ObjectUtilities;
import org.apache.commons.lang.StringUtils;
import tools.annotation.RemoteService;
import tools.meta.MethodMeta;
import tools.meta.ParamMeta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class ServiceStubGen {
    public final static String TARGET_DIR = "D:/smhwork/androidTest/client/src/stub/impl/";
    private static TraceProducer tracer;

    static {
        MKernelIOCFactory.initialize(new String[]{
                "conf/services/Log4j_Service.xml",
                "conf/services/Server_Service.xml"
        });
        tracer = ((TraceManager) MKernelIOCFactory.getIocContainer().getService(
                TraceComponentManifest.TRACE_MANAGER)).createTraceProducer(ServiceStubGen.class.getName());
    }

    public void generate() {
        Map<String, MKernelServiceRepository> serviceRepository = MKernelIOCFactory.getIocContainer().getServiceRepository();
        Set<String> repoKeys = serviceRepository.keySet();
        Set<String> serviceSet = new HashSet<String>();
        for (String key : repoKeys) {
            MKernelServiceRepository repo = serviceRepository.get(key);
            Map<String, MKernelServiceDefinition> serviceDefMap = repo.getServiceMapping();
            Set<String> defKeys = serviceDefMap.keySet();
            for (String defKey : defKeys) {
                MKernelServiceDefinition serviceDef = serviceDefMap.get(defKey);
                serviceSet.add(serviceDef.getClazzName());
            }
        }
        for (String serviceClazz : serviceSet) {
            try {
                Class clazz = Class.forName(serviceClazz);
                if (clazz.isAnnotationPresent(RemoteService.class)) {
                    genService(clazz);
                }
            } catch (Exception e) {
                tracer.logError(ObjectUtilities.printExceptionStack(e));
            }
        }
    }

    private void genService(Class clazz) throws Exception {
        Class interfaceClazz = clazz.getInterfaces()[0];
        Method[] methods = clazz.getDeclaredMethods();
        List<MethodMeta> methodMetaList = new ArrayList<MethodMeta>();
        for (Method method : methods) {
            MethodMeta methodMeta = new MethodMeta();
            methodMeta.setName(method.getName());
            methodMeta.setRtnType(method.getReturnType().getCanonicalName());
            Class[] paramTypes = method.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Class paramType = paramTypes[i];
                ParamMeta paramMeta = new ParamMeta();
                paramMeta.setNext(i < (paramTypes.length - 1));
                paramMeta.setType(paramType.getCanonicalName());
                paramMeta.setName("p" + i);
                methodMeta.getParams().add(paramMeta);
            }
            methodMetaList.add(methodMeta);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("interfaceName", interfaceClazz.getSimpleName());
        params.put("serviceName", StringUtils.uncapitalize(interfaceClazz.getSimpleName()));
        params.put("methods", methodMetaList);
        byte[] bytes = buildWithTemplate(params, "StubService.ftl");
        FileUtilities.writeFile(TARGET_DIR + interfaceClazz.getSimpleName() + "Impl.java", bytes);
    }

    private byte[] buildWithTemplate(Map<String, Object> params, String path) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(
                new URLTemplateLoader() {
                    protected URL getURL(String name) {
                        Locale locale = Locale.getDefault();
                        String urlName = "tools/template/"
                                + StringUtils.replace(name, "_" + locale.toString(), "");
                        return Thread.currentThread().getContextClassLoader().getResource(urlName);
                    }
                });
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(bos, "utf-8");
        Template temp = cfg.getTemplate(path);
        temp.setEncoding("utf-8");
        temp.process(params, out);
        out.flush();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        ServiceStubGen serviceStubGen = new ServiceStubGen();
        serviceStubGen.generate();
    }
}
