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
 * Publish.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.PublishSubscribeHandler;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Publishes the incoming data using the specified publish&#47;subscribe handler in storage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Publish
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the publish&#47;subscribe data structure in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: pubsub
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Publish
  extends AbstractSink
  implements StorageUpdater {

  private static final long serialVersionUID = 4937955990183019341L;

  /** the name of the publish/subscribe data structure in the internal storage. */
  protected StorageName m_StorageName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Publishes the incoming data using the specified publish/subscribe handler in storage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("pubsub"));
  }

  /**
   * Sets the name for the pub/sub data structure in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the pub/sub data structure in the internal storage.
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
    return "The name of the publish/subscribe data structure in the internal storage.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  @Override
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Storage 			storage;
    PublishSubscribeHandler	handler;

    result = getOptionManager().ensureVariableForPropertyExists("storageName");

    if (result == null) {
      handler = null;
      storage = getStorageHandler().getStorage();
      if (!storage.has(m_StorageName))
        result = "Storage item does not exist: " + m_StorageName;
      else if (!(storage.get(m_StorageName) instanceof PublishSubscribeHandler))
        result = "Storage item '" + m_StorageName + "' is not a " + Utils.classToString(PublishSubscribeHandler.class) + "!";
      else
        handler = (PublishSubscribeHandler) storage.get(m_StorageName);

      if (handler != null)
        handler.publish(this, m_InputToken.getPayload());
    }

    return result;
  }
}
