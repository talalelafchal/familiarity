Replace com.example with your package.

/**
 * Extracts minimal essential exception data and reports it as a Google Analytics event 
 * (Not reported as an App exception :).<br/>
 * <br/>
 * Reported action: Exception type & message<br/>
 * Reported label: File & line no
 * <code><pre>
 * } catch (Exception e) {
 *    Analytics.reportCaughtException(getActivity(), e);
 * }
 * </pre></code>  
 * @param context
 * @param exception 
 */
public static void reportCaughtException(Context context, Exception exception) {
    String message=exception.getClass().getSimpleName();
    if(exception.getMessage() != null) {
        message += ":"+exception.getMessage();
    }
    StackTraceElement[] stackTraceElements = exception.getStackTrace();
    String location="";
    for(int i=0;i<stackTraceElements.length; i++) {
        StackTraceElement stackTraceElement = stackTraceElements[i];
        if(stackTraceElement.getClassName().contains("com.example")) {
            location += stackTraceElement.getFileName().replace(".java","")+":"+stackTraceElement.getLineNumber();
            break;
        }
    }
    EasyTracker easyTracker = EasyTracker.getInstance(context);
    easyTracker.send(MapBuilder.createEvent("caught_exception", message, location, 0L).build());
}
