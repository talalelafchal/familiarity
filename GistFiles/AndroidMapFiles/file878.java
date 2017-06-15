package invoke;


import jbolt.core.ioc.MKernelIOCFactory;
import jbolt.core.trace.TraceComponentManifest;
import jbolt.core.trace.TraceManager;
import jbolt.core.trace.TraceProducer;
import jbolt.core.utilities.ClassUtilities;
import jbolt.core.utilities.ObjectUtilities;
import meta.RemoteResponse;
import org.apache.commons.lang.ArrayUtils;
import utils.Base64;
import utils.SerialUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Description:
 * <p/>
 * Date: 14-2-1
 * Author: Administrator
 */
public class RemoteInvokeHandler extends HttpServlet {
    //    private static Gson gson = new GsonBuilder().create();
    private static TraceProducer tracer = (
            (TraceManager) MKernelIOCFactory.getIocContainer().getService(
                    TraceComponentManifest.TRACE_MANAGER)).createTraceProducer(RemoteInvokeHandler.class.getName());

    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        RemoteResponse response = new RemoteResponse();
        try {
            String serviceName = req.getParameter("serviceName");
            String methodName = req.getParameter("methodName");
            String[] params = req.getParameterValues("params[]");
            if (params == null) {
                params = new String[0];
            }
            Object bean = MKernelIOCFactory.getIocContainer().getService(serviceName);
            Method callMethod = ClassUtilities.getMethodByArbitraryName(bean.getClass(), methodName);
            Object[] objParams = convertToServerParams(callMethod, params);
            handleInvoker(bean, objParams, callMethod, response);
        } catch (Exception e) {
            String error = ObjectUtilities.printExceptionStack(e);
            tracer.logError(error);
            response.setError(error);
        } finally {
            resp.getWriter().write(Base64.encodeBytes(SerialUtils.getObjectByteArray(response)));
            resp.getWriter().flush();
        }
    }

    protected Object[] convertToServerParams(Method callMethod, String[] params)
            throws Exception {
        Object[] objParams;
        if (!ArrayUtils.isEmpty(params)) {
            Class[] parameterTypes = callMethod.getParameterTypes();
            objParams = new Object[parameterTypes.length];
            for (int j = 0; j < params.length; j++) {
                String param = params[j];
                Object paramObj = SerialUtils.readObject(Base64.decode(param));
                objParams[j] = paramObj;
            }
            return objParams;
        }
        return new Object[0];
    }

    protected void handleInvoker(Object bean, Object[] params, Method callMethod, RemoteResponse response)
            throws Exception {
        Object res = callMethod.invoke(bean, params);
        response.setRes((Serializable) res);
    }
}
