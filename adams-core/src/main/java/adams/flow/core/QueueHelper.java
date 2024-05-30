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
 * QueueHelper.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;

/**
 * Helper class for queue handling.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class QueueHelper {

  /**
   * Checks whether the specified queue is available.
   * 
   * @param actor	the actor to check for
   * @param queue	the name of the queue
   * @return		true if available
   */
  public static boolean hasQueue(Actor actor, StorageName queue) {
    return 
	   actor.getStorageHandler().getStorage().has(queue)
	&& (actor.getStorageHandler().getStorage().get(queue) instanceof StorageQueueHandler);
  }

  /**
   * Returns the specified queue.
   * 
   * @param actor	the actor to obtain the queue for
   * @param queue	the name of the queue
   * @return		the queue handler, null if not available
   */
  public static StorageQueueHandler getQueue(Actor actor, StorageName queue) {
    if (!hasQueue(actor, queue))
      return null;
    else
      return (StorageQueueHandler) actor.getStorageHandler().getStorage().get(queue);
  }

  /**
   * Queues the payload in the specified queue.
   * 
   * @param actor	the actor to obtain the queue for
   * @param queue	the name of the queue
   * @param payload	the data to queue
   * @return		true if successfully queued
   */
  public static boolean enqueue(Actor actor, StorageName queue, Object payload) {
    StorageQueueHandler		handler;

    handler = getQueue(actor, queue);
    return (handler != null) && handler.add(payload);
  }

  /**
   * Queues the payload in the specified queue, applying the specified retrieval delay.
   *
   * @param actor		the actor to obtain the queue for
   * @param queue		the name of the queue
   * @param payload		the data to queue
   * @param retrievalDelay 	the delay to enforce on this object
   * @return			true if successfully queued
   */
  public static boolean enqueueDelayedBy(Actor actor, StorageName queue, Object payload, long retrievalDelay) {
    StorageQueueHandler		handler;

    handler = getQueue(actor, queue);
    return (handler != null) && handler.addDelayedBy(payload, retrievalDelay);
  }

  /**
   * Queues the payload in the specified queue, applying the specified retrieval timestamp.
   *
   * @param actor		the actor to obtain the queue for
   * @param queue		the name of the queue
   * @param payload		the data to queue
   * @param retrievalAt 	the delay to enforce on this object
   * @return			true if successfully queued
   */
  public static boolean enqueueDelayedAt(Actor actor, StorageName queue, Object payload, long retrievalAt) {
    StorageQueueHandler		handler;

    handler = getQueue(actor, queue);
    return (handler != null) && handler.addDelayedAt(payload, retrievalAt);
  }
}
