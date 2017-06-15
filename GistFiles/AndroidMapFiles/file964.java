# Leak memory:
 là khi 1 ta ko cần 1 obj mà nó vẫn tồn tại ở đó. 
 Đây hoàn toàn là phụ thuộc vào cảm nghĩ của ta, vd là ta nghĩ rằng biến này ko dùng nữa thì nó phải dươc free nhưng thực tế nó ko dc free thì gọi là leak
 Còn nếu như ta nghĩ rằng biến này sẽ tốn tại hết toàn chtr và nó sẽ ko free thì nó ko dc gõi là memory leak.

 Khi ta bị leak memory quá nhiều làm bộ nhớ cho app nhỏ lại -> JVM phải chạy GC liên tục để lấy memory -> gây giảm performance(lag)
. 4 TH bị memory leak in java:
	+  Static field holding object reference
	class MemorableClass {
    	static final ArrayList list = new ArrayList(100);
	}
	Lý do là static sẽ tồn tại trong vòng đời của chương trình (tức chừng nào chtr còn chạy thì biến static vẫn còn). 
	Cho nên nếu nó refer đến 1 obj thì obj đó ko bị GC lấy đi. (cách là set nó đến null)
	=> như vậy nếu như static var refer đến obj, và làm xong obj rồi mà quên set biến static to null thì nó là leak.
	=> nhưng nếu TH ta muốn obj tồn tại suốt program thì cứ cho static refer đến nó -> ko bị leak.
	Mọi static (instance var + method) nằm trên vùng permgem.

	+ Chú ý kiểu khai bao static HashMap và static ArrayList, do mấy cái list thường refer đến obj mà static thì lại tồn tại theo class suốt đời.
	
	+ (Unclosed) open streams ( file , network etc... )
	vd sau ta thấy nó đóng InputStream, nếu ko đóng thì bị leak resource (vd như file đang bi đọc thì app khác ko thể đồng thời đọc được file đó, do có thể gặp vấn đề 2 cái cùng access 1 thứ.)
	https://gist.github.com/PhongHuynh93/42be08bd07bd06926723ca1f3b0b4484

	+ Unclosed connections

	+ onclose event listener and callbacks: nếu 1 listener dc register nhưng khi class ko dc sd nữa thì ko unregister nó -> tốn ram
vd: 
   private void init() {
        ListenerCollector collector = new ListenerCollector();
        collector.setListener(this, mListener);
    }
    	+ runnable: vd runnable chạy trong vòng 5 phút, nhưng chưa đầy 5 phút ta lại xoay màn hình -> runnable nó vẫn hold đến activity cũ.
    
    Dong nay lam leak: collector.setListener(this, mListener);
when a new activity is created due to the device orientation changing, an associated Listener is created by the view, but when the activity is destroyed, that listener is never released. This means that no listeners can ever be reclaimed by Java's garbage collector, which creates a memory leak.
When the device is rotated and the current activity’s onStop() method is invoked, make sure to clean up any unnecessary references to view listeners.

# Bitmap (ảnh tốn heap rất lớn)
. Trước Honeycomb: nếu bitmap lưu trong heap, nếu những hình đó dc lưu trong tablet thì hình cang to sẽ càng tốn nhiều bộ nhớ nữa.
Cho nên cách lưu bitmap: trong heap chỉ lưu bitmap obj thoi -> mọi obj bitmap đều co same size tùy resolution. obj bitmap này trỏ đến vùng native memory, lưu ảnh bitmap thực sự.
Nhược: phải đợi GC xảy ra mới free được, nếu app nặng về xử lứ image thì ko lẽ đợi GC xảy ra mới chạy được. Nhược thứ 2 là ta ko truy cập dược vùng native memory, nên bitmap lưu trong đây ko debug dc.

. Sau Honeycomb: ảnh bitmap được lưu trong vùng heap luôn, có 1 bitmap obj trỏ tới vung này -> debug được lượng chiếm memory của bitmap.

# Cách xem xét memory: 
Xem mục 8
Nhìn trong log (khi GC xảy ra mới dump được memory còn trống bn) hay ta có thể gọi hàm dump ra. Nhưng nó lại ko nói dc obj sd bn memory.
Để thấy dc 1 obj sd heap bn -> sd DDMS, nếu xuất ra 1 file dạng HPROF format thì có thể sd các công cụ Memory Analyze khác (MAT) để phân tích ngoài Android Studio.

# Memory leaks:
GC nó ko đủ thông minh, nó chỉ biết 1 điều: khi dc ref -> nó còn sống, khi ko ref -> dc quyền xóa.
Là khi obj ref đến vùng heap ko sd -> ko cho GC lấy lại memory.
Nhưng khi obj ref đến obj trong vùng heap, và nó lại ref đến một dống obj khác -> lam cho GC ko thể nào lấy lại dc memory. 
vd ta có cây ref sau:
activity -> viewgroup -> nhiều view nhỏ => nếu 1 obj giữ 1 activity ko sd thì ko biết bộ nhớ leak bn.
=> Khi config xoay màn hình, ko dc ref đến activity cũ.

=> muốn xem dc mình có vô tình lảm leak ko => sd công cụ "Memory Analyze".

. Inner class: là class được dn trong 1 class khác
. Non static inner class:  dc quyền access member của class ngoài ->luôn phải giữ implicit với class ngoài -> nếu 1 obj ref đến class trong, mặc dù class ngoài ko cần nữa nhưng do class trong ref đến
class ngoài nên obj class ngoài ko bị GC xóa. 
. Static inner class: ko cần tạo instance của class ngoài vẫn access dc class trong, class trong có thể ko dc quyền access member của class ngoài, ko giữ refer đến class ngoài.
=> chỉ cần 1 chữ static đã đỡ tốn memory biết bao nhiêu.

# Activity và View:
Activity luôn có implicit ref đến các Views obj con.
View cũng có ref đến Activity nữa -> lý do khi tạo View nó phải biết tạo ở đâu, nó có chứa những view con ko (2 hàm getContext(), và getChildAt()).
View chứa bitmap thì nó ref đến Bitmap đó.

=> Nếu bạn ref đến 1 Activity (hay Activity context), bạn đã có thể tham chiếu tới toàn bộ cây gồm các view nữa -> leaks thì tốn rất nhiều đây.

-> Cách tránh:
. Tránh tạo static ref đến View hay Activity (Activity Context)
. Nếu mà ref đến context thì nên short lived (đặt trong 1 hàm)
. Nếu như cần long live context, sd Application Context (getBaseContext() or getApplicationContext()). These do not keep references implicitly.

# runnables leak memory: http://stackoverflow.com/questions/10864853/when-exactly-is-it-leak-safe-to-use-anonymous-inner-classes

# Memory Analyze (MAT trong eclipse)
. Shallow heap: chỉ size của từng object trong heap 
. Retained heap: chỉ size khi ta free 1 obj thì ta lấy lại dc bn memory (lý do là obj nếu trỏ tới obj khác thì các obj đó củng được free).
Trong công cụ này có một thuật ngữ là "Dominator Tree": A là dominator của B khi path qua B phải qua A. -> nhờ cái cây này mà ta biết được retained heap.

# Debug memory 
Khi memory đi lên nhưng ko đi xuống -> bị memory leaks (ta force GC xảy ra xem nó có làm xuống ko).