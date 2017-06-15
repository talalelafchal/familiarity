 package com.ogaclejapan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.support.v4.util.SparseArrayCompat;

public class ManagedExecutors implements ManagedExecutor {
  
	/**
	 * 必要に応じ、新規スレッドを作成するスレッドプールを作成しますが、利用可能な場合には以前に構築されたスレッドを再利用します。
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ManagedExecutors newCachedThreadPool(int corePoolSize, int maximumPoolSize) {
		return new ManagedExecutors(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue()));		
	}
	
	/**
	 * 必要に応じ、新規スレッドを作成するスレッドプールを作成しますが、利用可能な場合には以前に構築されたスレッドを再利用します。
	 * @return
	 */
	public static  ManagedExecutors newCachedThreadPool() {
		return new ManagedExecutors(Executors.newCachedThreadPool());
	}
	
	/**
	 * 固定数のスレッドを再利用するスレッドプールを作成します。
	 * @param nThreads
	 * @return
	 */
	public static ManagedExecutors newFixedThreadPool(int nThreads) {
		return new ManagedExecutors(Executors.newFixedThreadPool(nThreads));
	}
	
	/**
	 * 単一のワーカースレッドを使用する executor を作成します。
	 * @return
	 */
	public static  ManagedExecutors newSingleThreadExecutor() {
		return new ManagedExecutors(Executors.newSingleThreadExecutor());
	}
	
	private final ExecutorService es;
	private final SparseArrayCompat<ManagedTask> managedMap = new SparseArrayCompat<ManagedTask>(); 
	private final AtomicInteger serialId = new AtomicInteger(Integer.MAX_VALUE);
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final AtomicBoolean cancelling = new AtomicBoolean(false);
	
	private ManagedExecutors(ExecutorService es) {
		this.es = es;
	}
	
	@Override
	public void execute(Runnable r) throws IllegalStateException {
		assertDisposed();
		if (cancelling.get()) return;
		submit(r, serialId.getAndDecrement());
	}

	@Override
	public void execute(Runnable r, int id) throws IllegalStateException {
		assertDisposed();
		if (cancelling.get()) return;
		submit(r, id);
	}

	@Override
	public void cancel() {
		if (cancelling.compareAndSet(false, true)) {
			try {
				final int size = managedMap.size();
				for (int i = 0; i < size; i++) {
					final ManagedTask storedTask = managedMap.get(i);
					if (storedTask != null && !storedTask.future.isDone()) {
						storedTask.future.cancel(true);
					}										
				}
			} finally {
				cancelling.set(false);
			}
		}
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			es.shutdownNow();
			managedMap.clear();
		}
	}
	
	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		if (disposed.compareAndSet(false, true)) {
			if(!es.isShutdown()) es.shutdown(); 
		}
		return es.awaitTermination(timeout, unit);
	}

	private void submit(Runnable r, int id) {
		final ManagedTask storedTask = managedMap.get(id);
		if (storedTask != null && !storedTask.future.isDone()) {
			storedTask.future.cancel(true);
		}
		managedMap.put(id, new ManagedTask(es.submit(r)));		
	}
	
	private void assertDisposed() throws IllegalStateException {
		if (disposed.get()) throw new IllegalStateException("already disposed.");
	}
	
	@SuppressWarnings("rawtypes")
	private static class ManagedTask {		
		final Future future;
		private ManagedTask(Future future) {
			this.future = future;
		}
	}
	
}
