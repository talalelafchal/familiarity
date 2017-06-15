https://github.com/konmik/konmik.github.io/wiki/Introduction-to-Model-View-Presenter-on-Android

MODEL VIEW PRESENTER (MVP) IN ANDROID
http://www.tinmegali.com/en/model-view-presenter-android-part-1/?utm_source=Android+Weekly&utm_campaign=36def426b1-Android_Weekly_195&utm_medium=email&utm_term=0_4eb677ad19-36def426b1-338009597

Presenter surviving orientation changes with Loaders
https://medium.com/@czyrux/presenter-surviving-orientation-changes-with-loaders-6da6d86ffbbf#.fs26bq3y5

MVP for Android: how to organize the presentation layer
http://antonioleiva.com/mvp-android/

Android Code That Scales, With MVP
http://engineering.remind.com/android-code-that-scales/

Model-View-Presenter (MVP)
http://hannesdorfmann.com/android/mosby

MVP With Better Naming of implementation classes & DRY Principle
https://medium.com/@kailash09dabhi/mvp-with-better-naming-of-implementation-classes-dry-principle-e8b6130bbd02#.rave1ng3k
https://www.novoda.com/blog/better-class-naming/

###################################
Modeling my presentation layer
http://panavtec.me/modeling-presentation-layer?utm_source=Android+Weekly&utm_campaign=7407d72e14-Android_Weekly_193&utm_medium=email&utm_term=0_4eb677ad19-7407d72e14-338009597
Nêu lên ý tưởng phần nào đặt trong presenter, phần nào đặt trong view.
1 screen có thể có nhiều view (view là 1 cai phần nhỏ vd như list bài hát, cái menu chứa nút play là 2 view), mỗi view sẽ có 1 presenter, làm như vậy ta có thể reuse dể dàng hơn do mỗi presenter sẽ chuyên làm  view nhỏ.

nhiều screen có thể chỉ cần 1 presenter, vd ta suy nghĩ rằng 1 viewpager có 2 tab là 2 view, nhưng lúc sau khi ta ko thích nữa, ta bỏ viewpager 
và muốn là bấm tab 1 sẽ nav sang tab 2, lúc này 1 presenter có lợi cho ta, do ta ko cần thay đổi presenter chỉ thay đổi view.

làm sao để presenter sống sót khi config change, vd ta có cách cache rxjava với retrofit
A MVP Approach to Lifecycle Safe Requests with Retrofit 2.0 and RxJava
http://www.captechconsulting.com/blogs/a-mvp-approach-to-lifecycle-safe-requests-with-retrofit-20-and-rxjava?utm_source=Android+Weekly&utm_campaign=20d30e2dd2-Android_Weekly_192&utm_medium=email&utm_term=0_4eb677ad19-20d30e2dd2-338009597

callback hell: vd presenter ko cần biết flag thì ta đừng đặt nó vào presenter do nếu đặt ta phải thêm nhìu code và nhìu hàm,
flag liên quan đến domain thì ta đặt trong đó (khi chức năng thay đổi là ko cần flag nữa thì ta chỉ cần change domain lại)

###################################
Modeling my Android domain layer
http://panavtec.me/modeling-my-android-domain-layer

###################################
Mô hình MVP và chức năng từng layer:
BaseView: creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive 
across configuration changes.(sd HashMap)
BasePresenter: control attach, or detach view, check view presenter
https://github.com/PhongHuynh93/StockHawkPhong/commit/185e17f8cf642e04086ecb1eb0262fe43befe957?diff=split

###################################
Android Studio MVP Template
https://github.com/PhongHuynh93/TestMVPBoilerplate/commit/ca4396da5e0ca83166b2f860752de1528519fb03
https://github.com/benoitletondor/Android-Studio-MVP-template

V: chứa activity hay fragment
P: chứa code java thuần, sống sót khi config change 
M: làm trong background nhờ rxjava subscribeOn và observaOn, hay nếu sd SQLBrite thì code access db nó sẽ tự động background cho mình.

project mẫu sd template đó:
https://github.com/PhongHuynh93/XYZReaderPhong
###################################
######################################################################
Repository Design Pattern
https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006#.gcbfr57v4
######################################################################
PRESENTERS DON'T NEED LIFECYCLE EVENTS
http://hannesdorfmann.com/android/presenters-dont-need-lifecycle?utm_source=Android+Weekly&utm_campaign=329a06d39e-Android_Weekly_199&utm_medium=email&utm_term=0_4eb677ad19-329a06d39e-338009597
######################################################################
DroidMVP
https://github.com/andrzejchm/DroidMVP?utm_source=Android+Weekly&utm_campaign=db6d50d3e8-Android_Weekly_211&utm_medium=email&utm_term=0_4eb677ad19-db6d50d3e8-338009597
######################################################################
Small Android library to help you incorporate MVP, Passive View and Presentation Model patterns in your app https://medium.com/@andrzejchm/presentation-model-and-passive-view-in-mvp-the-android-way-fdba56a35b1e#.o8rb6bwji
https://github.com/andrzejchm/DroidMVP?utm_source=Android+Weekly&utm_campaign=db6d50d3e8-Android_Weekly_211&utm_medium=email&utm_term=0_4eb677ad19-db6d50d3e8-338009597
#################
Simple and powerful MVP library for Android
https://github.com/MaksTuev/ferro?utm_source=Android+Weekly&utm_campaign=644e634e1a-Android_Weekly_215&utm_medium=email&utm_term=0_4eb677ad19-644e634e1a-338009597
#################
a MVP library for Android
https://github.com/grandcentrix/ThirtyInch/?utm_source=Android+Weekly&utm_campaign=c0a2159802-Android_Weekly_222&utm_medium=email&utm_term=0_4eb677ad19-c0a2159802-338009597
#################
Shades of MVVM
https://www.bignerdranch.com/blog/shades-of-mvvm/?utm_source=Android+Weekly&utm_campaign=5f639dab6b-Android_Weekly_228&utm_medium=email&utm_term=0_4eb677ad19-5f639dab6b-338009597
######################################################################
EasyMVP
https://github.com/6thsolution/EasyMVP/blob/master/README.md
######################################################################
a MVP library for Android favoring a stateful Presenter
https://github.com/grandcentrix/ThirtyInch
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
######################################################################
