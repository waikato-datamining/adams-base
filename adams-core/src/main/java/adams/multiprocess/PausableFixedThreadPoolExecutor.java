/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * PausableThreadPoolExecutor.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Doug Lea with assistance from members of JCP JSR-166 Expert Group
 */
package adams.multiprocess;

import adams.core.License;
import adams.core.Pausable;
import adams.core.annotation.MixedCopyright;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The thread pool executor for the {@link LocalJobRunner} class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see LocalJobRunner
 */
@MixedCopyright(
    copyright = "Doug Lea with assistance from members of JCP JSR-166 Expert Group",
    license = License.CC_PD,
    url = "http://svn.apache.org/repos/asf/harmony/standard/classlib/trunk/modules/concurrent/src/main/java/java/util/concurrent/ThreadPoolExecutor.java"
)
public class PausableFixedThreadPoolExecutor
  extends ThreadPoolExecutor
  implements Pausable {

  /** whether the executor is paused. */
  protected boolean m_IsPaused;

  /** for pausing. */
  protected ReentrantLock m_PauseLock = new ReentrantLock();

  /** for resuming. */
  protected Condition m_Unpaused = m_PauseLock.newCondition();

  /**
   * Initializes the thread pool.
   *
   * @param numThreads	the maximum number of threads to use
   */
  public PausableFixedThreadPoolExecutor(int numThreads) {
    super(numThreads, numThreads,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
  }

  protected void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
    m_PauseLock.lock();
    try {
      while (m_IsPaused)
	m_Unpaused.await();
    }
    catch (InterruptedException ie) {
      t.interrupt();
    }
    finally {
      m_PauseLock.unlock();
    }
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_IsPaused;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    m_PauseLock.lock();
    try {
      m_IsPaused = true;
    }
    finally {
      m_PauseLock.unlock();
    }
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    m_PauseLock.lock();
    try {
      m_IsPaused = false;
      m_Unpaused.signalAll();
    }
    finally {
      m_PauseLock.unlock();
    }
  }
}