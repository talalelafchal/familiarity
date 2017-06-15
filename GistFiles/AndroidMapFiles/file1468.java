// Get stacks for every thread
import java.util.*;

Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
for (Thread key : map.keySet()) {
  for (StackTraceElement ste : map.get(key)) {
    Log.e(TAG, "TRACE: (" + key.toString() + ":" + key.getId() + ") " + ste);
  }
}
