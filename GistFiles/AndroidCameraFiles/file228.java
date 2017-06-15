import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CameraDumper {
    private static final String TAG = CameraDumper.class.getSimpleName();

    private static final String ANDROID_FLASHLIGHT_PACKAGE_NAME = "com.android.systemui";
    private static final String CMD_DUMPSYS_MEDIA_CAMERA = "dumpsys media.camera";
    private static final int INVALID_PID = -1;
    public static final String NOT_FOUND = "NOT_FOUND";
    private static final String REGEX_CLIENT_PID = "^Client.*\\s(\\d+)$";
    private static final int TIMEOUT = 800;

    private final Context mContext;

    public CameraDumper(Context context) {
        mContext = context;
    }

    /**
     * Detects whether camera device is currently opened by any application or not.
     *
     * @return The application name, if detected, otherwise {@link CameraDumper#NOT_FOUND}.
     */
    public String dump() {
        if (mContext == null) {
            return NOT_FOUND;
        }

        if (mContext.checkCallingOrSelfPermission("android.permission.DUMP")
                != PackageManager.PERMISSION_GRANTED) {
            return NOT_FOUND;
        }

        String pkg = NOT_FOUND;
        int pid = getCameraLockPid();
        if (pid != INVALID_PID) {
            pkg = getPackageName(pid);
        }

        if (pkg == NOT_FOUND) {
            return NOT_FOUND;
        }

        return getApplicationName(pkg);
    }

    private static int getCameraLockPid() {
        Process process = null;
        Worker worker = null;
        try {
            process = Runtime.getRuntime().exec(CMD_DUMPSYS_MEDIA_CAMERA);
            worker = new Worker(process);
            worker.start();
            worker.join(TIMEOUT);

            String line = null;
            Pattern pattern = Pattern.compile(REGEX_CLIENT_PID);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            while (bufferedReader.ready() && (line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    line = matcher.group(1);
                    break;
                }
            }
            return (line != null ? Integer.valueOf(line).intValue() : INVALID_PID);
        } catch (IOException ignored) {
            // NOP.
        } catch (NumberFormatException ignored) {
            // NOP.
        } catch (InterruptedException ignored) {
            try {
                if (worker != null) {
                    worker.interrupt();
                }
            } catch (Throwable t) {
                // NOP.
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return INVALID_PID;
    }

    private String getApplicationName(final String pkg) {
        if (ANDROID_FLASHLIGHT_PACKAGE_NAME.equals(pkg)) {
            return "Flashlight";
        }

        ApplicationInfo ai = null;
        PackageManager pm = mContext.getPackageManager();
        try {
            ai = pm.getApplicationInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // NOP.
        }

        if (ai == null) {
            return NOT_FOUND;
        }

        CharSequence label = pm.getApplicationLabel(ai);
        if (label == null) {
            return NOT_FOUND;
        }

        return label.toString();
    }

    private String getPackageName(final int pid) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = processes.iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo rapi = iterator.next();
            if (rapi.pid == pid) {
                return rapi.processName;
            }
        }

        return NOT_FOUND;
    }

    private final static class Worker extends Thread {
        private final Process mProcess;

        public Worker(Process process) {
            mProcess = process;
        }

        @Override
        public void run() {
            try {
                mProcess.waitFor();
            } catch (InterruptedException ignored) {
                // NOP.
            }
        }
    }
}