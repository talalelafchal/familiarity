package com.ogaclejapan;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 送信された Runnable タスクを実行するオブジェクトです。
 * <p>id毎にタスクを管理しているため、同一idのタスクがまだ動作中の場合は実行をキャンセルしてくれます</p>  
 */
public interface ManagedExecutor extends Executor {
	
	/**
	 * すべてのタスクが実行を完了していたか、タイムアウトが発生するか、現在のスレッドで割り込みが発生するか、そのいずれかが最初に発生するまでブロックします。
	 * @param timeout 待機する最長時間
	 * @param unit 引数の時間単位
	 * @return この executor が終了した場合は true、終了前にタイムアウトが経過した場合は false
	 * @throws InterruptedException 待機中に割り込みが発生した場合
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
	 * 指定した{@link Runnable}を非同期で実行します。
	 * @param r 実行するタスク
	 * @param id タスクを識別するためのID
	 * @throws RejectedExecutionException タスクの実行をスケジュールできない場合
	 */
	void execute(Runnable r, int id);
	
	/**
	 * 実行中のアクティブなすべてのタスクを停止します。
	 * <p>このメソッドはActivity#onPauseで呼び出すことを想定しています</p>
	 */
	void cancel();
	
	/**
	 * すべてのタスクを停止し、リソースを解放します。
	 * <p>このメソッドはActivity#onDestoryで呼び出すことを想定しています</p>
	 */
	void dispose();

}
