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
 * Enqueue.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.stdout;

import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.control.StorageUpdater;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.QueueHelper;

/**
 * Adds the output from stdout to the specified queue.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Enqueue
  extends AbstractStdOutProcessor
  implements StorageUpdater {

  private static final long serialVersionUID = -4718336619542173528L;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the actual queue. */
  protected transient StorageQueueHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds the received data to the specified queue.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("queue"));
  }

  /**
   * Returns whether storage items are being updated.
   *
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return true;
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(String value) {
    setStorageName(new StorageName(value));
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
  }

  @Override
  public String setUp(FlowContextHandler owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (!QueueHelper.hasQueue(m_Owner.getFlowContext(), m_StorageName))
        result = "Failed to locate queue: " + m_StorageName;
      else
	m_Handler = QueueHelper.getQueue(m_Owner.getFlowContext(), m_StorageName);
    }

    return result;
  }

  /**
   * Processes the stdout output received when in async mode.
   *
   * @param output the output to process
   */
  @Override
  public void processAsync(String output) {
    if (m_Handler != null)
      m_Handler.add(output);
  }

  /**
   * Processes the stdout output received when in blocking mode.
   *
   * @param output the output to process
   */
  @Override
  public void processBlocking(String output) {
    processAsync(output);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Handler = null;
    super.cleanUp();
  }
}
