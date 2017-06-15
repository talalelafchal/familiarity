Is it possible to run multiple AsyncTask in same time?
http://stackoverflow.com/questions/18357641/is-it-possible-to-run-multiple-asynctask-in-same-time
http://android-er.blogspot.com/2014/04/run-multi-asynctask-as-same-time.html
http://stackoverflow.com/questions/24459145/how-to-run-multiple-background-tasks-in-parallel-using-asynctask-in-android
ko dc
 By default, all of the AsyncTasks happen on a single thread. To use multiple threads, you need to use a different executor. AsyncTask has a thread pool executor you can use:
task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
###########################################################################
Run Asynctask với Service đồng thời dc ko? 
cũng ko dc
You can create your own Threads for this type of work. Since IntentService has its own queue same as AsyncTask you cannot run multiple background tasks at the same time.

