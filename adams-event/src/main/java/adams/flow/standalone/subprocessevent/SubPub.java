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
 * SubPub.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.subprocessevent;

import adams.core.MessageCollection;
import adams.core.PublishSubscribeHandler;
import adams.core.QuickInfoHelper;
import adams.event.PublicationEvent;
import adams.event.PublicationListener;
import adams.flow.control.StorageName;
import adams.flow.standalone.SubProcessEvent;

/**
 * Subscribes to the specified 'subscribe' queue to receive data from and
 * publishes the processed data to the 'publish' queue.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SubPub
  extends AbstractSubProcessEventTrigger<Object, Object>
  implements PublicationListener {

  private static final long serialVersionUID = 9132835385548701230L;

  /** the name of the queue in the internal storage to subscribe to. */
  protected StorageName m_SubscribeStorageName;

  /** the pub/sub handler for subscribing. */
  protected transient PublishSubscribeHandler m_SubscribeHandler;

  /** the name of the queue in the internal storage to publish to. */
  protected StorageName m_PublishStorageName;

  /** the pub/sub handler for publishing. */
  protected transient PublishSubscribeHandler m_PublishHandler;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Subscribes to the specified 'subscribe' queue to receive data from and "
	     + "publishes the processed data to the 'publish' queue.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "subscribe-storage-name", "subscribeStorageName",
      new StorageName("sub"));

    m_OptionManager.add(
      "publish-storage-name", "publishStorageName",
      new StorageName("pub"));
  }

  /**
   * Sets the name for the queue in the internal storage to subscribe to.
   *
   * @param value	the name
   */
  public void setSubscribeStorageName(StorageName value) {
    m_SubscribeStorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage to subscribe to.
   *
   * @return		the name
   */
  public StorageName getSubscribeStorageName() {
    return m_SubscribeStorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subscribeStorageNameTipText() {
    return "The name of the queue in the internal storage to subscribe to.";
  }

  /**
   * Sets the name for the queue in the internal storage to publish to.
   *
   * @param value	the name
   */
  public void setPublishStorageName(StorageName value) {
    m_PublishStorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage to publish to.
   *
   * @return		the name
   */
  public StorageName getPublishStorageName() {
    return m_PublishStorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String publishStorageNameTipText() {
    return "The name of the queue in the internal storage to publish to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "subscribeStoragename", m_SubscribeStorageName, "sub: ");
    result += QuickInfoHelper.toString(this, "publishStoragename", m_PublishStorageName, ", pub: ");

    return result;
  }

  /**
   * Configures the trigger.
   *
   * @param owner 	the owning event
   * @return		null if successfully configured, otherwise error message
   */
  @Override
  public String setUp(SubProcessEvent owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_Owner.getStorageHandler() == null)
	result = "No storage handler available!";
      else if (!m_Owner.getStorageHandler().getStorage().has(m_SubscribeStorageName))
	result = "PubSub Handler for subscribing not available: " + m_SubscribeStorageName;
      else if (!m_Owner.getStorageHandler().getStorage().has(m_PublishStorageName))
	result = "PubSub Handler for publishing not available: " + m_PublishStorageName;

      if (result == null) {
	m_SubscribeHandler = (PublishSubscribeHandler) m_Owner.getStorageHandler().getStorage().get(m_SubscribeStorageName);
	m_SubscribeHandler.addSubscriber(this);
	m_PublishHandler = (PublishSubscribeHandler) m_Owner.getStorageHandler().getStorage().get(m_PublishStorageName);
      }
    }

    return result;
  }

  /**
   * Gets called when data is being published.
   *
   * @param e		the data event
   */
  public void dataPublished(PublicationEvent e) {
    Object		input;
    Object		output;
    MessageCollection	errors;

    if (!m_Owner.isStopped() && !m_Owner.isPaused()) {
      if (isLoggingEnabled())
	getLogger().info("Data published by " + e.getDataSource() + ": " + e.getPublishedData());

      input  = e.getPublishedData();
      errors = new MessageCollection();
      output = process(input, errors);
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
      if (output != null)
	m_PublishHandler.publish(this, output);
    }
  }

  /**
   * Wraps up the trigger.
   */
  @Override
  public void wrapUp() {
    if (m_SubscribeHandler != null) {
      m_SubscribeHandler.removeSubscriber(this);
      m_SubscribeHandler = null;
    }
    m_PublishHandler = null;
    super.wrapUp();
  }
}
