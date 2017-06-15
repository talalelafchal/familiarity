# Khái niệm stack/heap trong Java
1.Stack: Stack values only exist within the scope of the function they are created in. Once it returns, they are discarded.
Stack memory holds: 
. primitives
. method invocations.
. reference to object (because reference is primitive)
2.Heap:  They are created at some point in time, and destructed at another (either by GC or manually, depending on the language/runtime).
. Heap memory is used to store objects, with all the variables that belong to it - so that it can persist after the function call returns. (sd new sẽ làm obj nằm trên heap)

# Java Heap Memory Structure:
http://www.javatutorialguide.com/core-java/tutorials/java-memory-management.php
. Young Generation: gồm 3 vùng nhỏ là: Eden and Survivor space 0 và Survivor space 1
. Old Generation: gồm Tenured Space
. Permanent Generation: gồm permGen

# Minor GC vs Major GC vs Full GC
https://plumbr.eu/blog/garbage-collection/minor-gc-vs-major-gc-vs-full-gc
. Minor GC: gọi GC là Minor khi GC xày ra trong vùng "Young Generation"
. Major GC: gọi GC là Major khi GC xảy ra trong vùng "Old Generation"
. Full GC: gọi GC là Full khi GC xảy ra trong vùng "Young  + old Generation"

. Khi new object được tạo ra, nó nằm trong vùng "young generation", khi vùng young generation bị đầy thì "minor GC" xảy ra.
=> nếu minor GC xảy ra quá thường xuyên -> tức là quá trình xin bộ nhớ trong heap xảy ra cao (trong 1 khoảng thời gian phải sd new() nhiều)
. Khi minor GC xảy ra, những obj nào còn lai (tức obj đó vẫn còn refer) thì obj đó move đến "old generation" (trong vùng này lưu những obj đã tồn tại lâu rồi) -> tại 1 điểm nào đó nó gọi Major GC (nhưng tần suất lại ít thường xuyên hơn là Minor GC).
=> đừng có nghĩ về GC dc chia thành 3 phần, hãy nghĩ về sự thường xuyên mà GC xảy ra.


# GC (garbage collection): là cái java sẽ tự động làm để lấy lại bộ nhớ mà program xin.
https://www.youtube.com/watch?v=_CruQY55HOk&t=2718s
	+ vd: obj thì nằm trong heap, và khai báo new(). Khi ta khai báo new() trong block (vd trong 1 method).
	Khi ta đi ra khỏi block (vd ra khỏi method) thì biến mà primitive đến obj bị null (là xóa luôn biến) -> lúc nào obj còn nằm trong heap, GC ko thấy nó dc refer nữa thì sẽ tự xóa obj đó trong 1 khoảng thời gian nào đó trong tương lai.
	Nhưng nếu là static thì nó tồn tại trong life của program -> nó cứ giữ obj đó thì GC sẽ ko bao giờ xóa object.
	Không thể khai báo static var dạng local trong java, chỉ dc khai báo nó cho instance variable và method thôi.

Sau android Gingerbread, thì GC sẽ chạy concurrent với app(lúc trước thì app phải stop thì GC mới chạy, giờ thì GC chạy trong thread khác), mà App bị PAUSE lúc đầu và lúc cuối khi GC bắt đầu/kết thúc chạy.(cỡ 5ms)

# cấu trúc của GC trong android:
. Lúc đầu GC Roots sẽ trỏ tới 1 obj trong vùng heap mà chắc chắn nó sẽ sống(còn refer)
. sau đó nó di chuyển hết toàn bộ graph(lý do là obj dc trỏ nó lại ref đến 1 obj nào đó trong heap, nó trỏ lẫn nhau trong đây, GC phải traverse hết) -> sau đó những obj mà ko dc GC đi qua sẽ bị xóa

# Managing Your Apps Memory
để GC to reclaim memory from your app, you need to:
. avoid introducing memory leaks (usually caused by holding onto object references in global members)
. release any Reference objects at the appropriate time (as defined by lifecycle callbacks discussed further below). 
For most apps, the Dalvik GC  takes care of the rest: the system reclaims your memory allocations when the corresponding objects leave the scope of your app active threads.

# Allocating and Reclaiming App Memory
Cách mà Android cung cấp memory cho app của ta:
. The Dalvik heap cho từng process(là 1 app), và mỗi app chỉ có giới hạn ram. (nó sẽ từ từ tăng)
. Cung cấp ram cho từng app trong Android ko giống nhau, và Android phải tính con so PSS để xác định xem app của ta cần bn ram.
. Tuy là logic heap chỉ có tăng ko có giảm(do giống như stack, chỉ giảm ở phần top), còn mấy phần giữa tuy có lây dc memory nhưng android vẫn ko nén heap lại, nhưng mà physical memory vẫn biết là vùng nhớ đó còn trống và ta đã lấy lại physical memory. (biết vùng nhớ còn trống khi GC xảy ra).


# Restricting App Memory
-> đây là con số hard, tức là con số này ko đổi, và Android sẽ cung cấp số này cho mỗi app, ko nhất thiết là giống nhau giữa các app, va số này còn tùy vào bộ nhớ Ram mà device có.
Và khi vung nhớ này đến giới hạn nhưng ta cứ xin (new) thì bị lỗi "OutOfMemoryError" (crash đây).
. Lý do mỗi app đều có 1 hard heap size, do Android là multitask, cùng 1 lúc nhiều app chạy,ta ko thể để cho 1 app nó cứ phình memory ra mà app khác ko sd được.
. Vd về heap size khác nhau trong từng device:
	+ G1: 16MB
	+ Droid: 24MB
	+ Nexus one: 32MB
	+ Xoom: 48MB
	Do mỗi device thì lượng heap size khác nhau nên nếu ta muốn lấy cái số này thì ActivityManager.getMemoryClass()
. Nhưng lại có 1 số app nó thực sự tốn vùng heap hơn bất cứ app nào khac, vd như app editor bitmap, thì ta sd "largeHeap" trong android manifest 
Lưu ý: app phải thực sự cần mới dc lý do, heap lớn thì GC làm lâu, với lại nó chiếm vùng nhớ lớn thì sẽ làm mấy app kia bị destroy, user se biết là app này gây hao tài nguyên nên có nguy cơ xóa.

# Switching Apps
khi process được tạo và ta rồi nó đi (bấm home), thì Android giữ process trong cache LRU (memory mà app chiểm vân giữ lại) -> do ko biến mất nên lúc quay lại thì mở lên nhanh
Nhưng việc giư lại ảnh hưởng performance của toan bộ system -> khi mà bị đầy thì Android sẽ kill process, nhưng ko phải kill theo dạng stack, mà củng cân nhắc giữ lại app mà nó nặng. (vd kill rồi mở lại thì lâu)


# How Your App Should Manage Memory
Để làm 1 app hiệu quả thì phải cân nhắc từ khi design (trước khi code).
1. Use services sparingly
lý do là process có service chạy nó nằm tại rank thứ 3/5 thứ tự mà Android sẽ kill, yêu tiên hơn process bị onStop()
vì Android ít kill nên nó tốn Ram và làm cho các process khác ít bị cache hơn.

2. Release memory when your user interface becomes hidden
Khi user navigates đến app khác -> app của bạn nên release resource -> giúp tăng dung lượng cache -> tăng performance toàn hệ thống.
sd  onTrimMemory() callbacks chừng nào UI của mình bị hidden, thay cho onStop() lý do là onStop() dc gọi khi ta nav qua activity khác luôn.

3. Release memory as memory becomes tight
onTrimMemory() callbacks đê biết được dung lượng memory của hệ thống đang bị low -> mặc dù ta vẫn đang trong app nhưng nên release memory

4. Check how much memory you should use

5. Avoid wasting memory with bitmaps
Do là bitmaps nằm trong heap, cho nên độ phân giải của nó càng cao thì chiếm vùng heap càng nhiều
-> phải scale nó sao cho hợp với device.

6. Use optimized data containers
Thay container như "HashMap" bằng 3 cái sau:
Sử dụng khi key là integer
. SparseArray: keys: int values: object
. SparseBooleanArray.
. LongSparseArray
http://stackoverflow.com/questions/25560629/sparsearray-vs-hashmap
7. Be aware of memory overhead
8. Be careful with code abstractions
9. Use nano protobufs for serialized data
10. Avoid dependency injection frameworks
11. Be careful about using external libraries
cái cách sd chỉ 1 vài tính năng của library nhưng sd quyên 1 library

12. Optimize overall performance
https://developer.android.com/training/best-performance.html

13. Use ProGuard to strip out any unneeded code
The ProGuard tool shrinks, optimizes, and obfuscates your code by removing unused code and renaming classes, fields, and methods with semantically obscure names. Using ProGuard can make your code more compact, requiring fewer RAM pages to be mapped

14. Use zipalign on your final APK

15. Analyze your RAM usage

16. Use multiple processes

