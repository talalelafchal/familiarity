/**
 * sdk 6.0以后取消了HttpClient，设置Android SDK的编译版本为23时，且使用了httpClient相关类的库项目，会出现有一些类找不到的错误。
 *
 * 解决方法有两种：
 * 1、在相应的module下的build.gradle中加入：useLibrary 'org.apache.http.legacy'
 *
 * 这条语句一定要加在 android{ } 当中，参见以下源码文件。
 * android {
 *    useLibrary 'org.apache.http.legacy'
 * }
 *
 * 2、将在相应的module下的build.gradle中修改compileSdkVersion的值，设置为更小的sdk版本
 */