// HPROF Viewer -> take a snapshot 
https://developer.android.com/studio/profile/am-hprof.html

# HPROF Viewer -> take a snapshot của heap thôi 
When you dump the Java heap, the Memory Monitor creates an Android-specific Heap/CPU Profiling (HPROF) file that you can view in the HPROF Viewer. 

The HPROF Viewer displays classes, instances of each class, and a reference tree to help you track memory usage and find memory leaks.

The HPROF Analyzer finds the following potential issues:
. All destroyed activity instances that are reachable from garbage collection roots.
. Where the target program has strings that repeat values.
A dominator is at the top of a tree. If you remove it, you also remove the branches of the tree it dominates, so it’s a potential way to free memory.

Understanding the HPROF Viewer Display
https://developer.android.com/studio/profile/am-hprof.html#display

If you click Analyzer Tasks, the HPROF Analyzer appears:
-> You can detect leaked activities and find duplicate strings with the HPROF Analyzer.

# Analyzing Heap Dump Data in the HPROF Analyzer
Bật task này ở phía bên phải, có 2 tùy chọn là 
. Duplicated string.
. Detect Leaked Activities -> nếu ko có thì ko leak 
vd nếu có leak memory:
http://tools.android.com/recent/androidstudio15preview1available

# Memory leak and use analysis
1. Another area that deserves attention is objects that the app no longer needs but continues to reference. 
You can gather heap dumps over different periods of time and compare them to determine if you have a growing memory leak, such as an object type that your code creates multiple times but doesn’t destroy. 

2. Continually growing object trees that contain root or dominator objects(obj cha dominator vẫn refer đến con mặc dù con ko sd nữa) can prevent subordinate objects (obj ở dưới cây dominator) from being garbage-collected. 
-> This issue is a common cause of memory leaks, out-of-memory errors, and crashes. 

# Diving into Heap Dump Data in the HPROF Viewer
The following steps outline the typical workflow:
. In the HPROF Viewer, select a class name.
. Select an instance of that class.
. Examine the reference tree.
. Right-click an item to Jump to source or Go to instance, as needed.