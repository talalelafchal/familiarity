0. sd memory monitor: view state of memory over time 
Nhìn tổng quan xem GC có xảy ra liên tục trong 1 khoảng thời gian ngắn hay ko

1. sd heap viewer: what on the heap
# Heap view trong DDMS: xem real time độ tăng giảm memory -> thích hợp dò xem class activity nào bị leak
ta sẽ chuyển từ activity dang chạy 2 -> activity blank 1 (chuyền bằng nút Back)
trong qua trình chuyển -> force GC

# Heap dump: thích hợp xem cái nào refer đến activity đó
Activity 2 sd heap tool để do heap dump activity lúc này, sau đó khi chuyển sang 1 đo lại heap dump.
Khi chuyển qua thì activity cũ phải dc clean up -> remove toàn bộ activity
Khi sd Heap dump thì có khoảng 2000 objs lận, ta chỉ dò Activity xem có instance ko 
-> thường thì chỉ có 1 instance của activity thôi
-> nếu ta xoay màn hình, thì trong 1 lúc ngắn thì có 2 instance của activity.
=> thây 2 cái thì nghi là bị leak rồi.
-> khi thấy leak, click vào activity xem cái nào refer đến activity đó (cái refer nghi ngờ nhất là có icon có 3 mũi tên chọt 3 hướng)

2. sd allocation tracker để xem leak cho rõ hơn: where did it come from

tạo 1 blank ac (start allocation) -> activity chính -> (force GC) -> (bấm nút back đi) blank ac (stop allocation)
Nó sẽ liệt kê:
. list các obj đã create.
. thứ tự create.
. create ở đâu.

Mở tab Memory ra -> chọn start Allocation Tracking -> sử dụng activity chính (lướt lướt nó để obj dc tạo ra)
Sd cái này đẻ xem tại sao object ko dc remove khi bấm back
Xác định memory churn: là qua trình tao ra rất nhiều object trong 1 khoang thời gian ngắn, vd:
. tạo ra nhiều object ở trong vòng lặp for
. hay tạo ra nhiều object on hàm ondraw() function -> vd khi animation xẽ goi ondraw trong mỗi frame (khi nhấn button thì có
hiệu ưng trong button) -> cùng 1 thời gian ngắn mà xin heap 1 lg lớn -> heap ko đủ cc 1 lg lớn đó thì nó goi GC 
=> giai pháp: remove khỏi inner loop (tránh loop nhiều lần), remove khỏi hàm nào dc gọi liên tục nhiều lần.

=> ta sẽ sd cong cụ trace view để tìm memory churn.

3. sd traceview
https://developer.android.com/studio/profile/traceview-walkthru.html



