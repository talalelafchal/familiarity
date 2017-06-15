3 cách để làm task background:

1. asynctask: khi work on/off UI threads.
2. handler thread: thread có api callbacks.
3. threadpool: chạy nhiều cv song song.
ta co asynctask có method cho chạy song song asynctask.executeOnExecutor() nhưng cai này có nhược thay vào đó ta chọn sd threadpool.
4. intentservice: ko dính gi tời UI threads.

###########################################################################################
background và activity
1. khi ta đặt ta class chạy background trong activity thì nó implicit refer đến activity -> memory leaks xảy ra khi activity bị destroy mà asynctask vẫn hoan work.
https://www.youtube.com/watch?annotation_id=annotation_318323295&feature=iv&index=66&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&src_vid=qk5F6Bxqhr4&v=tBHPmQQNiS8

2. khi rotate activity, mà background thread lại update lên view cũ nhưng ko còn tồn tại.
https://www.youtube.com/watch?annotation_id=annotation_3296249429&feature=iv&index=73&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&src_vid=qk5F6Bxqhr4&v=s4eAtMHU5gI

###########################################################################################
Systrace để check threads, cpu
https://developer.android.com/studio/profile/systrace.html?utm_campaign=app_series_systrace_021816&utm_source=gdev&utm_medium=yt-annt

để app ko lag thì nó phải 1s vẽ dc 60 frames -> 1 work phải làm xong trong > 16 ms , sd systrace để dò cpu dang làm j lâu.
###########################################################################################
worker thread die khi làm xong 1 task(works)
-> co cách nào làm worker ko die?
+ 1. có loop (giu cho workerthread ko die, chạy lần lượt từng tasks trong nó): nằm trong hàng đợi dang thực hiện.
+ 2. MessageQueue(đẩy works cho loop thực hiện): nằm trong hàng PendingTask (works đang đợi để thực hiện).
+ 3. 1 threads khác: đẩy works vào MessageQueue đê đợi.
https://www.youtube.com/watch?v=0Z5MZ0jL2BM&index=2&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE
=> Cách hiện thực: 
+ sd class Looper: chứa 1 và 2
+ sd class Handler: chua 3, nhưng ta có thể đặt tạo đầu, cuối, giữa hàng đợi (ko phải lúc nào củng cuối).

=> Tổng 2 cái class trên ta có 1 class chứa cà 2 là HANDLERTHREAD
MessageQueue chứa các loại works sau: tùy vào cái làm work đó. 
+ Intent
+ Runnable
+ Message 

#####################################################################################
tại sao ko thể update view dc trên background thread?
Để tránh trường hợp 2 thread cùng access 1 vùng memory đồng thời sẽ gây crash -> nên android chỉ cho UI thread dc quyền access thôi.

#####################################################################################
từ worker thread mà reference đến 1 view cũng ko ổn nữa?
https://www.youtube.com/watch?v=tBHPmQQNiS8&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&index=3

Lý do khi worker thread đang làm task mà activity bị destroy và phải hủy view cũ tạo view mới, trong khi worker thread đang refer và sẽ làm việc trên view cũ -> mà view lại refer đến toàn bọ activity cũ (nếu ta đặt class async trong class activity ko có static)-> ngăn activity cũ bị gc xóa cho đén khi async làm xong.

Tệ hơn nữa là khi rotate 3 lần thì sẽ có 3 cái activity trong memory.

Trong TH trong 1 activity bị rotate, khi asynctask nó làm xong và nó update lại view cũ -> crash nếu view cũ ko còn nữa. (hay nếu nó còn trong memory thì chỉ update lại view cũ).

=> KO DC REFER ĐẾN VIEW OBJECT NÀO TRONG THREAD BACKGROUND.
nhưng làm sao để update view từ background thread? =>  BẮT BUỘC TOP ACTIVITY HAY FRAGMENT PHẢI UPDATE VIEW.
bằng callback - interface.

#####################################################################################
DO NOT LEAK VIEWS 
https://www.youtube.com/watch?annotation_id=annotation_309995137&feature=iv&index=23&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&src_vid=tBHPmQQNiS8&v=BkbHeFHn8JY
1. ko pass view cho asynctask, do async thực hien trong tương lai mà tương lai ta ko biết activity giữ view có bị mất ko?
2. ko cho biến static access đến view

#####################################################################################
Khi nào sd background task là asynctask ko thích hợp?
vd có task nó quá dài như Camera.PreviewCallback sẽ nhận từng bức hình, mỗi bức có 8MB mà nếu đẩy vao asynctask thì nó làm stall cac cv khác mà cung cần async (do async chỉ chạy dc 1 task) -> đẩy nó vao Handler thread, vì thread này chuyên xử lý thread dài nhận callback và ko stall asynctask,
Muốn back lại UI thread khi xng ta sd runOnUiTHread()
https://developer.android.com/reference/android/hardware/Camera.PreviewCallback.html?utm_campaign=app_series_previewcallback_021816&utm_source=gdev&utm_medium=yt-annt

=> Handler thread tốt khi chạy task dài, ko UI, vd như task nén metrics trước khi up lên server.(thêm priority nữa). 
#####################################################################################
Swimming in Threadpools. 
https://www.youtube.com/watch?v=uCmHoEY1iTM&index=6&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE
chạy nhiều tác vụ trên nhiều thread vd:
+ audio thread:
+ IO thread:
+ networking thread:
+ database thread: 
=> ThreadPoolExcecutor: handle tất cả problem khi ta bật nhiều thread song song, vd cách balance giữa các thread đó. Tự động xóa thread khi nó idle....
=> Vấn dê: nên đặt ra bn threads? ko phải nhiều thread quá cũng tốt.
Ly do là CPU la nhiều thread cùng chạy động thời, nếu như app ta nhiều thread thì cpu se bắt đợi dựa vào sự ưu tiện -> hóa ra là lâu hơn ít thread thực hiện.

=> chỉ khi nào thread làm công việc má lớn mới sử dung cái thread pools này.
https://developer.android.com/training/multiple-threads/index.html
#####################################################################################
Intent service
https://www.youtube.com/watch?v=9FweabuBi1U&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&index=7
Nó có 1 cái lợi là sd service chứ ko phải background làm xong thì mất-> vd app càn cập nhật trong 1 mốc nào đó thì đặt trong đây hay tạo 1 alarm cứ đến time nào đó thì nó kiu lên...

Nhược: nó stall các background khác như asynctask do ko chạy đồng thời asynctask với intentservice, sd broadcast receiver và intent để giao tiếp với activity khi nó xong cái gì đó => do intent là liên quan tới process communication (chtr quản lý global các process) cho nên perfrom châm hơn runOnUiThread (thread communication chtr quán lý local)
Ưu: activity có service có độ ưu tien cao hơn activity đang ẩn khi system phải kill ap -> ít dc kill hơn

=> cần phải xem asynctask có làm dc ko mới đầy qua service. 
#####################################################################################
The Importance of Thread Priority
Fun fact : Modern CPUs can only handle a certain number of threads at one time. 
Once you get above that limit, they have to start scheduling which thread gets the next open block of CPU processing time. 
And even better : This can influence the performance of your Android app.

https://www.youtube.com/watch?v=NwFXVsM15Co&index=9&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE

# Chú ý: nếu sd AsyncTask thì ta khỏi cân gán do system đã gan cho class này rồi

#####################################################################################
view.post(): 

      View#post will post the runnable on the queue (e.g. call the Handler#post)
Activity#runOnUiThread will call the run method directly
Another difference between Activity.runOnUiThread and view.post() is that the runnable in view.post() is called after the view is attached to a window.
http://stackoverflow.com/questions/10558208/android-whats-the-difference-between-activity-runonuithread-and-view-post
#####################################################################################
#####################################################################################
#####################################################################################
#####################################################################################