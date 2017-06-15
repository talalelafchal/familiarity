# Android Monitor Overview
https://developer.android.com/studio/profile/android-monitor.html#logcat
Dùng để do đạc performance của 1 app: để ta có thể optimize, debug, improve nó bằng 3 cách:
. Log messages, either system- or user-defined: dùng để debug trong realtime.
. Memory, CPU, and GPU, Network usage (2)
. Network traffic (hardware device only)
-> Bạn có thể capture data khi bạn chạy app và xuất ra 1 file, chụp hình, quay phim.

Trong (2) ta có công cụ Memory Monitor:

Android Monitor Basics
# https://developer.android.com/studio/profile/am-basics.html
Các bước:
1. chạy app
2. chọn Memory Monitor

# Memory Monitor
https://developer.android.com/studio/profile/am-memory.html
https://www.youtube.com/watch?v=7ls28uGMBEs
Android Monitor provides a Memory Monitor so you can more easily monitor app performance and memory usage to find deallocated objects, locate memory leaks, and track the amount of memory the connected device is using.
Nhìn màn hình memory:
. màu xanh đậm ở dưới: memory mà app đang sử dụng.
. màu xanh nhạt ở trên: memory còn trống mà app có thể sử dụng.
Các loại group:
TH1: Nếu như app ko làm gì nhiều -> thấy 2 màu xanh là 1 đường thẳng. Đây là TH ideal performance.
TH2: đường thẳng bấp bên thì lúc lên thì app đang xin memory và lúc xuống thì app đang free memory(garbage collection events - GC xảy ra).
	+ Khi GC xảy ra thì nó ko hẳn là ảnh hưởng performance lắm, nhưng nếu GC xảy ra nhiều lần trong 1 khoảng thời gian ngắn -> performance issues.
	+ Khi GC xảy ra nhiều lần, thì thời gian làm những công việc khác ít dần như render màn hình (gây ra lag) hay stream audio.
	+ Khi GC xảy ra nhiều lần, dump the Java heap to identify candidate object types that get or stay allocated unexpectedly or unnecessarily.

-> nhìn cái này ta sẽ kết luận được độ heal và performance của systems. -> nhưng ta ko thể biết problem nào mà liên quan đến nó cả.
=> sd công cụ khác là Memory Heap tool, chổ nào trong code, objects được tạo ra mà ko có release, object nào được create mà ko xài,
hay object nào được new(tạo mới) trong khi ta hoàn toàn có thể sử dụng lại object đó.

