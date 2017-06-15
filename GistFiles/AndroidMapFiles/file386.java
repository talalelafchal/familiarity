package net.zeddev.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import android.util.Log;

/**
 * <p>
 * A wrapper for the android log, providing a more robust, useful interface.
 * As the name suggests the log is named which is used as the tag passed to the android 
 * logger.
 * </p>
 * 
 * <p>
 * Can be used like so;
 *<pre>
 * NamedLog log = NamedLog.getLog("log name"); // returns cached instance
 * 
 * // alternatively can get an objects log, like so;
 * NamedLog thisLog = NamedLog.getLog(this); // uses the classes getSimpleName()
 *
 * // uses the same log levels as android.util.Log
 * log.e("An error");
 * log.i("formatted %s", "string"); // also has string formatting
 *                                  // using same format as String.format()
 *
 *</pre>
 * </p>
 *
 * <p>
 * The logger is also extensible (unlike the built-in one) so that extra 
 * functionality can be added easily (i.e. not requiring a complete re-write).
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> This file is released as a stand-alone utility.  The original 
 * file and the associated unit test can be found on GitHub Gist - 
 * <a href="https://gist.github.com/zscott92/5696929">here</a>.
 * </p>
 * 
 * @author Zachary Scott <zscott.dev@gmail.com>
 */
public class NamedLog {

	// The cached logs.
	// Mapped by name to instance.
	private static final Map<String, NamedLog> LOG_INSTANCES = 
			new HashMap<String, NamedLog>();
	
	// The listener event dispatch thread.
	public static DispatchThread dispatchThread = new DispatchThread();
	
	// The name of the log.
	// Passed as the tag to the android Log.
	private final String name;
	
	/** 
	 * Creates a new {@code NamedLog} with the given name.
	 * Should use static factory methods ({@code getLog()} where ever possible to 
	 * instantiate logs as the instances are cached.
	 * 
	 * @param name the name of the logger to fetch (must not be {@code null}).
	 */
	protected NamedLog(String name) {
		
		assert(name != null);
		
		this.name = name;
		
	}
	
	/** 
	 * Returns the {@code NamedLog} with the given name.
	 * 
	 * @param name the name of the logger to fetch (must not be {@code null}).
	 */
	public static NamedLog getLog(String name) {
		
		assert(name != null);
		
		// instantiate if not cached instance
		if (!LOG_INSTANCES.containsKey(name))
			LOG_INSTANCES.put(name, new NamedLog(name));
		
		return LOG_INSTANCES.get(name);
		
	}
	
	/** 
	 * Returns the {@code NamedLog} for the given class.
	 * 
	 * @param clazz which classes log to fetch (must not be {@code null}).
	 */
	public static NamedLog getLog(Class<?> clazz) {
		
		assert(clazz != null);
		
		return getLog(clazz.getSimpleName());
		
	}
	
	/** 
	 * Returns the objects associated {@code NamedLog} instance.
	 * 
	 * @param clazz which classes log to fetch (must not be {@code null}).
	 */
	public static NamedLog getLog(Object object) {
		
		assert(object != null);
		
		return getLog(object.getClass());
		
	}
	
	/**
	 * Adds a listener to the list to be notified of logs.
	 * 
	 * @param listener the listener to be added (must not be {@code null}).
	 */
	public static void addListener(Listener listener) {
		
		assert(listener != null);
		
		dispatchThread.addListener(listener);
		
	}
	
	/**
	 * Removes the given listener from the list.
	 * 
	 * @param listener the listener to be removed. Must currently be in the 
	 * list of listeners and cannot be {@code null}.
	 */
	public static void removeListener(Listener listener) {
		
		assert(listener != null);
		
		dispatchThread.removeListener(listener);
		
	}
	
	/** The logs name.  Used as the tag in the log output. */
	public String getName() {
		return name;
	}
	
	/** 
	 * Logs a message of the given priority.
	 * Low-level log call, should use {@code v(), d(), i(), w() and e()} for day-to-day stuff.
	 * 
	 * @param priority the priority level of the log message (must not be {@code null}).
	 * @param fmt the formatted log message (must not be {@code null}).
	 */
	public void log(Priority priority, String fmt, Object... args) {
		
		assert(priority != null);
		assert(fmt != null);
		
		// log the message, to the android logger
		if (Log.isLoggable(name, priority.value)) {
			
			String msg = String.format(fmt, args);
			Log.println(priority.value, name, msg);
			
			dispatchThread.notifyLogListeners(priority, msg, null);
			
		}
			
	}
	
	/** 
	 * Logs a message of the given priority, with the causing exception.
	 * Low-level log call, should use {@code v(), d(), i(), w() and e()} for day-to-day stuff.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param priority the priority level of the log message (must not be {@code null}).
	 * @param fmt the formatted log message (must not be {@code null}).
	 */
	public void log(Priority priority, Throwable cause, String fmt, Object... args) {
		
		assert(cause != null);
		
		// log the message, followed by the stack trace
		if (Log.isLoggable(name, priority.value)) {
			
			String msg = String.format(fmt, args);
			Log.println(priority.value, name, msg);
			
			// log the stack trace
			Log.println(priority.value, name, Log.getStackTraceString(cause));
			
			dispatchThread.notifyLogListeners(priority, msg, cause);
			
		}
		
	}
	
	/** 
	 * Reports a {@code Priority.VERBOSE} log message.
	 * i.e. Equivalent to {@code android.util.Log.v()} call.
	 * 
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).
	 * Must not be {@code null}.
	 */
	public void v(String fmt, Object... args) {
		log(Priority.VERBOSE, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.VERBOSE} log with the causing exception.
	 * i.e. Equivalent to {@code android.util.Log.v()} call.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).  
	 * Must not be {@code null}.
	 */
	public void v(Throwable cause, String fmt, Object... args) {
		log(Priority.VERBOSE, cause, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.DEBUG} log message.
	 * i.e. Equivalent to {@code android.util.Log.d()} call.
	 * 
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).
	 * Must not be {@code null}.
	 */
	public void d(String fmt, Object... args) {
		log(Priority.DEBUG, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.DEBUG} log with the causing exception.
	 * i.e. Equivalent to {@code android.util.Log.d()} call.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).  
	 * Must not be {@code null}.
	 */
	public void d(Throwable cause, String fmt, Object... args) {
		log(Priority.DEBUG, cause, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.INFO} log message.
	 * i.e. Equivalent to {@code android.util.Log.i()} call.
	 * 
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).
	 * Must not be {@code null}.
	 */
	public void i(String fmt, Object... args) {
		log(Priority.INFO, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.INFO} log with the causing exception.
	 * i.e. Equivalent to {@code android.util.Log.i()} call.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).  
	 * Must not be {@code null}.
	 */
	public void i(Throwable cause, String fmt, Object... args) {
		log(Priority.INFO, cause, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.WARN} log message.
	 * i.e. Equivalent to {@code android.util.Log.w()} call.
	 * 
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).
	 * Must not be {@code null}.
	 */
	public void w(String fmt, Object... args) {
		log(Priority.WARN, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.WARN} log with the causing exception.
	 * i.e. Equivalent to {@code android.util.Log.w()} call.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).  
	 * Must not be {@code null}.
	 */
	public void w(Throwable cause, String fmt, Object... args) {
		log(Priority.WARN, cause, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.ERROR} log message.
	 * i.e. Equivalent to {@code android.util.Log.e()} call.
	 * 
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).
	 * Must not be {@code null}.
	 */
	public void e(String fmt, Object... args) {
		log(Priority.ERROR, fmt, args);
	}
	
	/** 
	 * Reports a {@code Priority.ERROR} log with the causing exception.
	 * i.e. Equivalent to {@code android.util.Log.e()} call.
	 * 
	 * @param cause the exception which caused the message to be logged (must not 
	 * be {@code null}).
	 * @param fmt the formatted log message (uses same format as {@code String.format()}).  
	 * Must not be {@code null}.
	 */
	public void e(Throwable cause, String fmt, Object... args) {
		log(Priority.ERROR, cause, fmt, args);
	}
	
	/** 
	 * An enumeration of the available log message priorities.
	 * Map directly to those defined in {@code android.util.Log}.
	 * 
	 */
	public enum Priority {
		
		ASSERT(Log.ASSERT), 
		VERBOSE(Log.VERBOSE), 
		DEBUG(Log.DEBUG),
		INFO(Log.INFO),
		WARN(Log.WARN),
		ERROR(Log.ERROR);
		
		public final int value;
		
		private Priority(int value) {
			this.value = value;
		}
		
	}
	
	/** 
 	 * Interface to listen to incoming log messages.
 	 * Should be registered with the {@code NamedLog} class by calling {@code addListener()}. 
 	 *  
 	 */
	public static interface Listener {
		
		/**
		 * Reports an incoming log message.
		 * Called by the {@code NamedLog} class when registered as listener.
		 * 
		 * @param priority the priority of the log.
		 * @param msg the logged message.
		 * @param cause the exception which caused the message to be logged (may be 
		 * {@code null} if there is no causing exception).
		 */
		public void notifyLog(Priority priority, String msg, Throwable cause);
		
	}
	
	// The thread which is responsible for notifying listeners
	private static class DispatchThread extends Thread {
		
		// the registered listeners
		private final List<Listener> listeners = new ArrayList<Listener>();
		
		// the log entries waiting to be dispatched
		private final BlockingQueue<LogEntry> queue = new LinkedBlockingDeque<LogEntry>();
		
		public DispatchThread() {
			
			super("named log dispatch");
			
			setDaemon(true);
			setPriority(Thread.MIN_PRIORITY);
			
		}
		
		// adds a listener to the list to be notified
		public void addListener(Listener listener) {
			
			assert(listener != null);
			
			synchronized (listeners) {
				listeners.add(listener);
			}
			
		}
		
		// removes the given listener
		public void removeListener(Listener listener) {
			
			synchronized (listeners) {
			
				assert(listener != null);
				assert(listeners.contains(listener));
				
				listeners.remove(listener);
			
			}
			
		}
		
		// notifies the registered listeners
		private void notifyLogListeners(Priority priority, String msg, Throwable cause) {
			queue.add(new LogEntry(priority, msg, cause));
		}
		
		@Override
		public void run() {
			
			while (true) {
				/* NOTE:
				 * Does not stop until execution is terminated.  This is not a
				 * problem as;
				 *     a. There is only a single app-wide dispatch thread, and thus there 
				 *         wont be dead threads hanging about. 
				 *     b. The dispatch thread is marked (in constructor above) as
				 *        daemon and thus is forcefully terminated upon shutdown
				 *        of the app.
				 */
				
				// wait for the next log entry to process
				LogEntry log;
				try {
					log = queue.take();
				} catch (InterruptedException ex) {
					continue; // try again if interrupted
				}
				
				synchronized (listeners) {
				
					// notify each listener
					for (Listener listener : listeners)
						listener.notifyLog(log.priority, log.msg, log.cause);
					
				}
				
			}
			
		}
		
		// A simple container for logged data.
		private class LogEntry {
			
			public final Priority priority;
			public final String msg;
			public final Throwable cause;
			
			public LogEntry(Priority priority, String msg, Throwable cause) {
				
				assert(priority != null);
				assert(msg != null);
				
				this.priority = priority;
				this.msg = msg;
				this.cause = cause;
				
			}
			
		}
		
	}
	
}

/* Copyright (C) 2013  Zachary Scott <zscott.dev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION  OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */