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

/*
 * StorageQueueHandler.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.Properties;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.db.LogEntry;
import adams.flow.core.Actor;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper class around a {@link LinkedBlockingDeque} object stored in internal storage.
 * Allows to limit the queue size and the specification of a logging actor.
 * It is possible to define retrieval timestamps for queue items, i.e., only after the timestamp
 * passes is the retrieval possible ("delayed retrieval"). The underlying assumption with delayed
 * retrieveal is that items added to the queue at a later stage will have increasing retrieval
 * timestamps (they queue items don't get reordered based on that timestamp).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see LogEntry
 */
public class StorageQueueHandler
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = 5211228304482833329L;

  /**
   * Container for queueing objects.
   */
  public static class QueueItem
    implements Serializable {

    private static final long serialVersionUID = 7748334770112610824L;

    /** the payload for the queue. */
    public Object payload;

    /** the timestamp when the object can be retrieved (walltime in msec). */
    public long retrievalAt;

    /**
     * Initializes the item with no retrieval timestamp.
     *
     * @param obj	the payload
     */
    public QueueItem(Object obj) {
      this(obj, 0);
    }

    /**
     * Initializes the item with the specified retrieval timestamp.
     *
     * @param obj	the payload
     * @param timestamp the retrieval timestamp (walltime in msec)
     */
    public QueueItem(Object obj, long timestamp) {
      payload     = obj;
      retrievalAt = timestamp;
    }

    /**
     * Checks whether the item can be retrieved based on its retrieval timestamp.
     *
     * @return		true if retrieval is possible
     */
    public boolean canRetrieve() {
      return retrievalAt <= System.currentTimeMillis();
    }
  }

  /** the name of the queue. */
  protected String m_Name;
  
  /** the queue itself. */
  protected LinkedBlockingDeque<QueueItem> m_Queue;
  
  /** the limit for the queue (-1 is unlimited). */
  protected int m_Limit;
  
  /** the (optional) logging actor. */
  protected Actor m_Logging;
  
  /** the (optional) monitoring actor. */
  protected Actor m_Monitoring;
  
  /**
   * Initializes the limitless queue with no logging.
   * 
   * @param name	the name of the queue
   */
  public StorageQueueHandler(String name) {
    this(name, -1, null, null);
  }
  
  /**
   * Initializes the queue with the specified limit and logging.
   * 
   * @param name	the name of the queue
   * @param limit	the size limit, use <= 0 for unlimited
   * @param logging	the logging actor for sending the {@link LogEntry}
   * 			objects to
   * @param monitoring	the monitoring actor for sending the {@link LogEntry}
   * 			objects to
   */
  public StorageQueueHandler(String name, int limit, Actor logging, Actor monitoring) {
    if (limit <= 0)
      limit = 65535;
    m_Name       = name;
    m_Queue      = new LinkedBlockingDeque(limit);
    m_Limit      = limit;
    m_Logging    = logging;
    m_Monitoring = monitoring;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
  }

  /**
   * Returns the name of the queue.
   * 
   * @return		the name
   */
  public String getname() {
    return m_Name;
  }

  /**
   * Returns the limit of the queue.
   * 
   * @return		the limit
   */
  public int getLimit() {
    return m_Limit;
  }
  
  /**
   * Checks whether a logging actor is available.
   * 
   * @return		true if available
   */
  public boolean hasLogging() {
    return (m_Logging != null);
  }
  
  /**
   * Returns the logging actor.
   * 
   * @return		the actor, null if not available
   */
  public Actor getLogging() {
    return m_Logging;
  }
  
  /**
   * Checks whether a monitoring actor is available.
   * 
   * @return		true if available
   */
  public boolean hasMonitoring() {
    return (m_Monitoring != null);
  }
  
  /**
   * Returns the monitoring actor.
   * 
   * @return		the actor, null if not available
   */
  public Actor getMonitoring() {
    return m_Monitoring;
  }
  
  /**
   * Logs the log entry with the specified actor.
   * 
   * @param log		the log entry to send
   * @param actor	the logging actor to send the LogEntry to
   */
  protected void log(LogEntry log, Actor actor) {
    if (actor instanceof InputConsumer) {
      ((InputConsumer) actor).input(new Token(log));
      actor.execute();
    }
  }
  
  /**
   * Creates a log entry and sends it to the logging actor.
   * 
   * @param msg		the msg
   * @param obj		the object
   */
  protected void logError(String msg, Object obj) {
    LogEntry		log;
    Properties		props;

    props = new Properties();
    props.setProperty(LogEntry.KEY_ERRORS, msg);
    if (obj != null)
      props.setProperty("Object", "" + obj);

    log = new LogEntry();
    log.setType("StorageQueueError");
    log.setSource(m_Name);
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);

    log(log, m_Logging);
  }
  
  /**
   * Creates a log entry and sends it to the monitoring actor.
   * 
   * @param type	the monitoring event type (Add or Remove)
   * @param obj		the object to add, null in case of remove
   */
  protected void logMonitor(String type, Object obj) {
    LogEntry		log;
    Properties		props;

    props = new Properties();
    if (obj != null)
      props.setProperty("Object", "" + obj);

    log = new LogEntry();
    log.setType("StorageQueue" + type);
    log.setSource(m_Name);
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);

    log(log, m_Monitoring);
  }

  /**
   * Adds the object to the queue.
   *
   * @param obj		the object to add
   * @return		true if successfully added
   */
  public synchronized boolean add(Object obj) {
    return addDelayedAt(obj, 0);
  }

  /**
   * Adds the object to the queue with the specified retrieval delay.
   *
   * @param obj		the object to add
   * @param delay	the retrieval delay to use for the object
   * @return		true if successfully added
   */
  public synchronized boolean addDelayedBy(Object obj, long delay) {
    long 	retrievalAt;

    if (delay > 0)
      retrievalAt = System.currentTimeMillis() + delay;
    else
      retrievalAt = 0;

    return addDelayedAt(obj, retrievalAt);
  }

  /**
   * Adds the object to the queue with the specified timestamp from which to allow retrieval.
   * 
   * @param obj		the object to add
   * @param retrievalAt	the retrieval timestamp to associate with the object, 0 for immediate retrieval
   * @return		true if successfully added
   */
  public synchronized boolean addDelayedAt(Object obj, long retrievalAt) {
    QueueItem	item;

    if (m_Limit == -1) {
      item = new QueueItem(obj, retrievalAt);
      m_Queue.addLast(item);
      if (hasMonitoring())
	logMonitor("Add", obj);
      return true;
    }
    else {
      if (m_Queue.size() + 1 <= m_Limit) {
	item = new QueueItem(obj, retrievalAt);
	m_Queue.addLast(item);
	if (hasMonitoring())
	  logMonitor("Add", obj);
	return true;
      }
      else {
	if (hasLogging())
	  logError("Cannot add item to queue '" + m_Name + "' as it reached its limit: " + m_Limit, obj);
	return false;
      }
    }
  }

  /**
   * Checks whether it is possible to dequeue an item.
   *
   * @return		true if object can be dequeued
   */
  public synchronized boolean canRemove() {
    return !m_Queue.isEmpty() && m_Queue.peekFirst().canRetrieve();
  }

  /**
   * Removes the first element from the queue and returns it.
   * 
   * @return		the first element from the queue
   */
  public synchronized Object remove() {
    Object	result;

    result = m_Queue.poll();
    if (hasMonitoring())
      logMonitor("Remove", result);
    return result;
  }
  
  /**
   * Returns the size of the queue.
   * 
   * @return		the size
   */
  public synchronized int size() {
    return m_Queue.size();
  }

  /**
   * Polls the queue in a blocking fashion.
   *
   * @param timeout	the timeout for the poll
   * @param unit	the time unit
   * @return		the value from the queue, null if timeout or not allowed to retrieve yet
   * @throws InterruptedException	if interrupted
   */
  public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
    Object	result;
    QueueItem	item;

    result = null;
    item   = m_Queue.poll(timeout, unit);
    if (item != null) {
      if (item.canRetrieve())
	result = item.payload;
      else
	m_Queue.putFirst(item);
    }

    return result;
  }

  /**
   * Returns a short description of the queue.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "name=" + m_Name + ", size=" + m_Queue.size() + ", limit=" + m_Limit + ", logging=" + m_Logging + ", monitoring=" + m_Monitoring;
  }
}
