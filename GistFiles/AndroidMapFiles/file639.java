https://blog.siliconstraits.com/4-thư-viện-android-hữu-%C3%ADch-bạn-nên-biết-bd9c2bd76913#.h13q6q4lx
RxJava- Understanding observeOn() and subscribeOn()
http://tomstechnicalblog.blogspot.com/2016/02/rxjava-understanding-observeon-and.html?utm_source=Android+Weekly&utm_campaign=20d30e2dd2-Android_Weekly_192&utm_medium=email&utm_term=0_4eb677ad19-20d30e2dd2-338009597
#####
RxJava - Maximizing Parallelization
http://tomstechnicalblog.blogspot.com/2016/02/rxjava-maximizing-parallelization.html?utm_source=Android+Weekly&utm_campaign=39574cfef4-Android_Weekly_194&utm_medium=email&utm_term=0_4eb677ad19-39574cfef4-338009597
#####
sqlbrite, rxbinding
https://gist.github.com/PhongHuynh93/1a5b3e6e3d3e7fd6f22b0ec5b30da736
#####
#####
RxAndroid Basics: Part 1
LINK NÀY RẤT LÀ ĐỄ HIỄU
https://medium.com/@kurtisnusbaum/rxandroid-basics-part-1-c0d5edcf6850#.d0r96si9d
Getting Started With ReactiveX on Android
http://code.tutsplus.com/tutorials/getting-started-with-reactivex-on-android--cms-24387
https://www.youtube.com/watch?v=vfjgQabgjOg&feature=youtu.be&utm_source=Android+Weekly&utm_campaign=20d30e2dd2-Android_Weekly_192&utm_medium=email&utm_term=0_4eb677ad19-20d30e2dd2-338009597
.skip(2) // Skip the first two items that emits
.filter ignore some items that emitting
. Handling Events: there is a class called ViewObservable that can handled the click event of a Button http://code.tutsplus.com/tutorials/getting-started-with-reactivex-on-android--cms-24387
#####
RxJava blogs
http://futurice.com/blog/top-7-tips-for-rxjava-on-android
https://gist.github.com/staltz/868e7e9bc2a7b8c1f754
#####
seri rxjava trên android
https://medium.com/crunching-rxandroid
https://github.com/PhongHuynh93/RxAndroidCrunch
#####
THAY ASYNCTASK BANG RXJAVA
https://www.youtube.com/watch?v=7IEPrihz1-E&list=PLecKJETdtE5KCF_PS7V_0Q-aGONzW5Iz-&index=7
#####
Why should we use RxJava on Android
https://medium.com/@lpereira/why-should-we-use-rxjava-on-android-c9066087c56c#.fz40hv3u8
#####
MVVM + Rxjava
https://upday.github.io/blog/mvvm_rx_common_mistakes/?utm_source=Android+Weekly&utm_campaign=644e634e1a-Android_Weekly_215&utm_medium=email&utm_term=0_4eb677ad19-644e634e1a-338009597
https://github.com/ivacf/archi

https://developer.android.com/topic/libraries/data-binding/index.html

#####
RxJava - Just vs From
http://stackoverflow.com/questions/30819349/rxjava-just-vs-from
The difference should be clearer when you look at the behaviour of each when you pass it an Iterable (for example a List):

Observable.just(someList) will give you 1 emission - a List.
Observable.from(someList) will give you N emissions - each item in the list.
#####
Hướng dẫn rxjava rất dễ hiểu về căn bản Rxjava vd subcriber, observer, map, flatmap, from , just 
https://medium.com/fuzz/howdy-rxjava-8f40fef88181#.sd9u2y910
#####
Alphabetical List of Observable Operators
https://github.com/ReactiveX/RxJava/wiki/Alphabetical-List-of-Observable-Operators
#####
Transforming Observables
https://github.com/ReactiveX/RxJava/wiki/Transforming-Observables#flatmap-concatmap-and-flatmapiterable
#####
Creating Observables
https://github.com/ReactiveX/RxJava/wiki/Creating-Observables
#####
Lazy Injection with Dagger 2 on Android
http://stackoverflow.com/questions/34049807/lazy-injection-with-dagger-2-on-android
#####
If you never call get, Dagger never creates the object in question.
The first call to get creates and stores the object instance.
The second call to get returns the same instance, and so on forever, regardless of whether the object was marked as Singleton.
Async Injection in Dagger 2 with RxJava
https://medium.com/@froger_mcs/async-injection-in-dagger-2-with-rxjava-e7df503343c0#.f1kmruie2
có 2 cách inject 1 cách async:
c1: làm async trong @Provide

    . dạng singleton (wrap nó bằng Lazy)
    . dạng instance, muốn nó tạo instance mới mỗi lần gọi
c2: làm async trong onCreate của Actiivty bao quanh Rxjava
#####
Retrofit with Rxjava Schedulers.newThread() vs Schedulers.io()
http://stackoverflow.com/questions/33415881/retrofit-with-rxjava-schedulers-newthread-vs-schedulers-io
#####
If you wish to change it so that multiple subscribers are attached before executing the request, otherwise known as converting to a hot observable, you need to convert the Observable to anConnectableObservable. 
https://github.com/codepath/android_guides/wiki/RxJava#hot-vs-cold-observables
#####
Avoiding Memory Leaks
để bị leak khi observers giữ ref đến activity chừng nó nào xong, avoid bằng cách sd library  RxLifecycle https://github.com/trello/RxLifecycle
RxLifecycle requires subclassing all activities with RxActivity or RxAppCompatActivity.
Hiều rõ hơn về cách hoạt động Rxjava https://vimeo.com/144812843
The Hidden Pitfalls of AsyncTask
http://blog.danlew.net/2014/06/21/the-hidden-pitfalls-of-asynctask/
#####
Party tricks with RxJava, RxAndroid & Retrolambda
https://medium.com/swlh/party-tricks-with-rxjava-rxandroid-retrolambda-1b06ed7cd29c#.qwyan1bgv
############################################################################
Cách sd rxjava để lưu dữ liệu từ retrofit về db qua từng stream 
https://github.com/PhongHuynh93/MovieNanoDegree/blob/b27301ea4f8a989360269d4d8ce467de3320ded5/app/src/main/java/dhbk/android/movienanodegree/io/MovieInteractor.java
############################################################################
use Rxjava instead of Event Bus libraries
https://medium.com/mobiwise-blog/use-rxjava-instead-of-event-bus-libraries-aa78b5023097#.vfybbsdqi

tại rxjava nó đã loại bỏ callback cho ta, và nhờ Observable ta cố thể filter, save ... tùy thích 
############################################################################
RxJava- Understanding observeOn() and subscribeOn()
hiểu vè cách loại schedule trong rxjava
http://tomstechnicalblog.blogspot.com/2016/02/rxjava-understanding-observeon-and.html?utm_source=Android+Weekly&utm_campaign=20d30e2dd2-Android_Weekly_192&utm_medium=email&utm_term=0_4eb677ad19-20d30e2dd2-338009597

############################################################################
Getting Started with RxJava and Android
http://www.captechconsulting.com/blogs/getting-started-with-rxjava-and-android
############################################################################
A MVP Approach to Lifecycle Safe Requests with Retrofit 2.0 and RxJava
http://www.captechconsulting.com/blogs/a-mvp-approach-to-lifecycle-safe-requests-with-retrofit-20-and-rxjava?utm_source=Android+Weekly&utm_campaign=20d30e2dd2-Android_Weekly_192&utm_medium=email&utm_term=0_4eb677ad19-20d30e2dd2-338009597
############################################################################
RxJava - Maximizing Parallelization
http://tomstechnicalblog.blogspot.com/2016/02/rxjava-maximizing-parallelization.html?utm_source=Android+Weekly&utm_campaign=39574cfef4-Android_Weekly_194&utm_medium=email&utm_term=0_4eb677ad19-39574cfef4-338009597
############################################################################
Grokking RxJava
http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
ko cần subscribe rồi hiện thực 3 hàm onNext, onSuccess và onError, chỉ cần hiện thực onNext thì tạo 1 hàm action thôi
############################################################################
RxJava: thread safety of the Operators and Subjects
https://artemzin.com/blog/rxjava-thread-safety-of-operators-and-subjects/?utm_source=Android+Weekly&utm_campaign=a4662f244d-Android_Weekly_217&utm_medium=email&utm_term=0_4eb677ad19-a4662f244d-338009597
############################################################################
Reactive Fit API Library for Android
https://github.com/patloew/RxFit?utm_source=Android+Weekly&utm_campaign=36def426b1-Android_Weekly_195&utm_medium=email&utm_term=0_4eb677ad19-36def426b1-338009597
############################################################################
Backpressure
TH khi phát nhanh còn consume chậm hơn.
https://github.com/ReactiveX/RxJava/wiki/Backpressure
############################################################################
RxJava design retrospect
1 bài blog cảm nghĩ cách design của rxjava
http://akarnokd.blogspot.it/2016/03/rxjava-design-retrospect.html?utm_source=Android+Weekly&utm_campaign=1172c35594-Android_Weekly_196&utm_medium=email&utm_term=0_4eb677ad19-1172c35594-338009597
############################################################################
RxJava — One Observable, Multiple Subscribers, Same Data
vd khi ta muốn kết nối mạng và trả về giá trị cho nhiều subcriber, ta chỉ muốn giá tri truyền thôi chứ ko cần phải mỗi
lần subcriber subscribe thì nó lại kết nối mạng.
https://medium.com/@p.tournaris/rxjava-one-observable-multiple-subscribers-7bf497646675#.xuzwamsvo
sd library
https://medium.com/@p.tournaris/rxjava-rxreplayingshare-emit-only-once-b19acd61b469#.i9lkstxw3
############################################################################
Reactive Android UI Programming with RxBinding
https://realm.io/news/donn-felker-reactive-android-ui-programming-with-rxbinding/?utm_source=Android+Weekly&utm_campaign=cc76a98ffc-Android_Weekly_197&utm_medium=email&utm_term=0_4eb677ad19-cc76a98ffc-338009597
############################################################################
Droidcon SF - Common RxJava Mistakes
https://www.youtube.com/watch?v=QdmkXL7XikQ
https://speakerdeck.com/dlew/common-rxjava-mistakes?utm_source=Android+Weekly&utm_campaign=cc76a98ffc-Android_Weekly_197&utm_medium=email&utm_term=0_4eb677ad19-cc76a98ffc-338009597
############################################################################
RxRelay
https://github.com/JakeWharton/RxRelay
Replay
http://reactivex.io/documentation/operators/replay.html
############################################################################
RxEither
https://github.com/eleventigers/rxeither?utm_source=Android+Weekly&utm_campaign=cc76a98ffc-Android_Weekly_197&utm_medium=email&utm_term=0_4eb677ad19-cc76a98ffc-338009597
############################################################################
Tagged Unions for the RxJava aesthete
https://github.com/pakoito/RxSealedUnions?utm_source=Android+Weekly&utm_campaign=cc76a98ffc-Android_Weekly_197&utm_medium=email&utm_term=0_4eb677ad19-cc76a98ffc-338009597
############################################################################
Loading data from multiple sources with RxJava
http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
############################################################################
Reactive Android UI Programming with RxBinding
https://realm.io/news/donn-felker-reactive-android-ui-programming-with-rxbinding/?utm_source=Android+Weekly&utm_campaign=cc76a98ffc-Android_Weekly_197&utm_medium=email&utm_term=0_4eb677ad19-cc76a98ffc-338009597
############################################################################
RxJava — One Observable, Multiple Subscribers, Same Data
https://medium.com/@p.tournaris/rxjava-one-observable-multiple-subscribers-7bf497646675#.xjom23prw
############################################################################
Loading data from multiple sources with RxJava
http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
############################################################################
RxJava design retrospect
http://akarnokd.blogspot.it/2016/03/rxjava-design-retrospect.html?utm_source=Android+Weekly&utm_campaign=1172c35594-Android_Weekly_196&utm_medium=email&utm_term=0_4eb677ad19-1172c35594-338009597
############################################################################
RxJava —RxReplayingShare, Emit only Once
https://medium.com/@p.tournaris/rxjava-rxreplayingshare-emit-only-once-b19acd61b469#.bprvzl4uh
############################################################################
Adopting RxJava on Airbnb Android
https://realm.io/news/kau-felipe-lima-adopting-rxjava-airbnb-android/?utm_source=Android+Weekly&utm_campaign=c1ffc42de6-Android_Weekly_198&utm_medium=email&utm_term=0_4eb677ad19-c1ffc42de6-338009597
############################################################################
RxJava - The Problem with Subjects
http://tomstechnicalblog.blogspot.com/2016/03/rxjava-problem-with-subjects.html?utm_source=Android+Weekly&utm_campaign=c1ffc42de6-Android_Weekly_198&utm_medium=email&utm_term=0_4eb677ad19-c1ffc42de6-338009597
############################################################################
Writing a custom reactive base type
http://akarnokd.blogspot.com/2016/03/writing-custom-reactive-base-type.html?utm_source=Android+Weekly&utm_campaign=c1ffc42de6-Android_Weekly_198&utm_medium=email&utm_term=0_4eb677ad19-c1ffc42de6-338009597
############################################################################
rxlint: an Android lint rule for RxJava code
http://www.littlerobots.nl/blog/RxLint-a-lint-rule-for-RxJava/?utm_source=Android+Weekly&utm_campaign=2542ddef98-Android_Weekly_200&utm_medium=email&utm_term=0_4eb677ad19-2542ddef98-338009597
############################################################################
pass object từ trên xuống cùng với object dưới và phát cùng lúc 2 cái
https://github.com/ReactiveX/RxJava/issues/2931
############################################################################
Error Handling Operators rxjava
https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators
#################
In a perfect mobile world users would never lose connectivity, servers will never return errors and bacon would have been low fat.
https://lorentzos.com/improving-ux-with-rxjava-4440a13b157f#.58sguta5n
                                                                               
############################################################################
agera
Reactive Programming for Android
https://github.com/google/agera?utm_source=Android+Weekly&utm_campaign=5d9d6b9604-Android_Weekly_203&utm_medium=email&utm_term=0_4eb677ad19-5d9d6b9604-338009597
#################
OAuth RxJava extension for Android. iOS version is located at this repository.
RxSocialConnect simplifies the process of retrieving authorizations tokens from multiple social networks to a minimalist observable call, from any Fragment or Activity.
https://github.com/VictorAlbertos/RxSocialConnect-Android?utm_source=Android+Weekly&utm_campaign=c6e59dd48c-Android_Weekly_206&utm_medium=email&utm_term=0_4eb677ad19-c6e59dd48c-338009597
#################
Nice AssertJ assertions for RxJava
https://github.com/ubiratansoares/rxassertions?utm_source=Android+Weekly&utm_campaign=f7b91895ba-Android_Weekly_205&utm_medium=email&utm_term=0_4eb677ad19-f7b91895ba-338009597
#################
############################################################################
ReactiveCache
https://github.com/VictorAlbertos/ReactiveCache?utm_source=Android+Weekly&utm_campaign=4c2c611c0a-Android_Weekly_216&utm_medium=email&utm_term=0_4eb677ad19-4c2c611c0a-338009597
#################
Loading data from multiple sources with RxJava
http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
############################################################################
Using RxJava in SearchView
https://medium.com/@matdziu/using-rxjava-in-searchview-f1d1d5dcb8b7#.h7b7pa5eq
############################################################################
Learning Rx by example
https://vimeo.com/190922794
############################################################################
`Rxify` : The Anti Cache-then-Network OR Network-then-Cache Problem
http://www.andevcon.com/news/rxify-the-anti-cache-then-network-or-network-then-cache-problem
############################################################################
RxRecipes: Wrap your way to Rx
https://hackernoon.com/rxrecipes-wrap-your-way-to-rx-fd40eb5254b6#.g9ux80c44
############################################################################
Reactive Views: retrying errors
https://medium.com/xing-engineering/reactive-views-retrying-errors-a59fffbd827f#.dveoq8t0f
RxJava's repeatWhen and retryWhen, explained
http://blog.danlew.net/2016/01/25/rxjavas-repeatwhen-and-retrywhen-explained/
############################################################################
Roaring RxJava https://github.com/ReactiveX/RxJava
https://github.com/Commit451/Reptar?utm_source=Android+Weekly&utm_campaign=8ab4301908-AndroidWeekly_241&utm_medium=email&utm_term=0_4eb677ad19-8ab4301908-338009597
############################################################################
FunctionalRx2 is a collection of constructs to simplify a functional programming approach to Java and Android
https://github.com/pakoito/FunctionalRx2?utm_source=Android+Weekly&utm_campaign=4355a5dcbf-AndroidWeekly_242&utm_medium=email&utm_term=0_4eb677ad19-4355a5dcbf-338009597
############################################################################
Showcase of RxJava used with MVP and some other popular android libraries
https://github.com/emmaguy/rxjava-mvp-giphy/projects
############################################################################
RxJava as event bus, the right way
https://lorentzos.com/rxjava-as-event-bus-the-right-way-10a36bdd49ba#.k9v7rtjru
############################################################################
RxJava and Android: error handling
http://andraskindler.com/blog/2013/rxjava-and-android-error-handling/
############################################################################
`Rxify` — Maintaining Order in Auto-Complete Search
https://medium.com/fueled-android/rxify-a-simple-spell-for-complex-rxjava-operators-part-1-4c31921583c4#.78t060p98
############################################################################
Pull vs Push & Imperative vs Reactive – Reactive Programming [Android RxJava2] ( What the hell is this ) Part2
http://www.uwanttolearn.com/android/pull-vs-push-imperative-vs-reactive-reactive-programming-android-rxjava2-hell-part2/
############################################################################
Observer Pattern – Reactive Programming [Android RxJava2] ( What the hell is this ) Part1
https://android.jlelse.eu/android-dev-tip-3-99da754151ad#.ch6kwuqlt
############################################################################
`Rxify`— a simple spell for complex RxJava operators (Part -1)
https://medium.com/fueled-android/rxify-a-simple-spell-for-complex-rxjava-operators-part-1-4c31921583c4#.5bcyec1ql
############################################################################
DebouncedBuffer With RxJava
http://blog.kaush.co/2015/01/05/debouncedbuffer-with-rxjava/
############################################################################
Server polling and retrying failed operations. With Retrofit and RxJava.
https://medium.com/@v.danylo/server-polling-and-retrying-failed-operations-with-retrofit-and-rxjava-8bcc7e641a5a#.rc5ec9rx8
http://blog.danlew.net/2016/01/25/rxjavas-repeatwhen-and-retrywhen-explained/
############################################################################
rxjava 2
    https://github.com/kaushikgopal/RxJava-Android-Samples
https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#observable-and-flowable
https://github.com/amitshekhariitbhu/RxJava2-Android-Samples
https://realm.io/news/gotocph-jake-wharton-exploring-rxjava2-android/
http://www.vogella.com/tutorials/RxJava/article.html
https://blog.mindorks.com/migrating-from-rxjava1-to-rxjava2-5dac0a94b4aa#.ngvym7wls
https://medium.com/@manuelvicnt/rxjava2-android-mvvm-lifecycle-app-structure-with-retrofit-2-cf903849f49e#.ccpw6k2hv
############################################################################
Clearer RxJava intentions with Single and Completable
https://android.jlelse.eu/making-your-rxjava-intentions-clearer-with-single-and-completable-f064d98d53a8
############################################################################
      Making RxJava code tidier with doOnSubscribe and doFinally
https://medium.com/@ValCanBuild/making-rxjava-code-tidier-with-doonsubscribe-and-dofinally-3748f223d32d                                                            
############################################################################
Moving away from the Event Bus with RxJava and Dagger 2

############################################################################
############################################################################