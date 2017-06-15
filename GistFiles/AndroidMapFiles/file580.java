package net.zeddev.android.util;

import net.zeddev.android.util.NamedLog;
import net.zeddev.android.util.NamedLog.Priority;
import junit.framework.TestCase;

/**
 * Unit test for {@code net.zeddev.android.util.NamedLog}.
 * 
 * <p>
 * <b>NOTE:</b> The {@code NamedLog} class is released as a stand-alone utility and
 * the original file can be found on GitHub Gist - 
 * <a href="https://gist.github.com/zscott92/5696929">here</a>.
 * </p>
 * 
 * @author Zachary Scott <zscott.dev@gmail.com>
 */
public class NamedLogTest extends TestCase {

	/** Tests the setting of the logs name. */
	public void testLogName() {
		
		String TEST_NAME = "TestLog";
		
		NamedLog log = NamedLog.getLog(TEST_NAME);
		assertEquals(log.getName(), TEST_NAME);
		
	}
	
	/** Tests the caching of log instances. */
	public void testLogCaching() {
		
		NamedLog log1 = NamedLog.getLog("CacheMe");
		
		assertEquals(log1, NamedLog.getLog(log1.getName()));
		assertTrue(log1 != NamedLog.getLog("AnotherLog"));
		
	}
	
	/** Tests log methods for each log priority. */
	public void testLogMethods() {
		
		NamedLog log = NamedLog.getLog(this);
		
		// listen to the logs output to check that it is all correct
		NamedLog.Listener listener = new NamedLog.Listener() {
			public void notifyLog(Priority priority, String msg, Throwable cause) {
				
				switch (priority) {
					case DEBUG:
						assertEquals(msg, "debug log");
						break;
					case VERBOSE:
						assertEquals(msg, "verbose log");
						break;
					case INFO:
						assertEquals(msg, "info log");
						break;
					case WARN:
						assertEquals(msg, "warn log");
						break;
					case ERROR:
						assertEquals(msg, "error log");
						break;
					default:
						break;
				}
				
			}			
		};
		
		NamedLog.addListener(listener);
		
		// now send the test log data
		log.d("debug %s", "log");
		log.v("verbose %s", "log");
		log.i("info %s", "log");
		log.w("warn %s", "log");
		log.e("error %s", "log");
		
		NamedLog.removeListener(listener);
		
	}
	
	/** Test log with the causing exception. */
	public void testLogWithException() {
		
		final Exception testex = new Exception("Test exception.");
		
		NamedLog log = NamedLog.getLog(this);
		
		// check the causing exception is reported
		NamedLog.Listener listener = new NamedLog.Listener() {
			public void notifyLog(Priority priority, String msg, Throwable cause) {
				assertEquals(cause, testex);
			}
		};
		
		NamedLog.addListener(listener);
		
		// send the test log
		log.e(testex, "Test log with exception");
	
		NamedLog.removeListener(listener);
		
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