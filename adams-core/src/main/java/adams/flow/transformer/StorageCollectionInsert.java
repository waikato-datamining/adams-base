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
 * StorageCollectionInsert.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.Collection;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Inserts the object passing through to the collection in storage at the specified position.<br>
 * After inserting the object successfully, just forwards the object.<br>
 * If the collection does not implement the java.util.List interface and the insertion is not at the end, the insertion will fail.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.CollectionInsert
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: StorageCollectionInsert
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
 * <pre>-storageName &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item that represents the collection to update.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-position &lt;adams.core.Index&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the string.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-after &lt;boolean&gt; (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the string is inserted after the position instead of at the
 * &nbsp;&nbsp;&nbsp;position.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StorageCollectionInsert
  extends AbstractTransformer
  implements StorageUpdater, ClassCrossReference {

  private static final long serialVersionUID = -4381778255320714964L;

  /** the name of the collection in storage. */
  protected StorageName m_StorageName;

  /** the position where to insert the string. */
  protected Index m_Position;

  /** whether to insert after the position instead of at. */
  protected boolean m_After;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Inserts the object passing through to the collection in storage at the specified position.\n"
	+ "After inserting the object successfully, just forwards the object.\n"
	+ "If the collection does not implement the " + Utils.classToString(List.class) + " "
	+ "interface and the insertion is not at the end, the insertion will fail.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{CollectionInsert.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storageName", "storageName",
      new StorageName());

    m_OptionManager.add(
      "position", "position",
      new Index(Index.LAST));

    m_OptionManager.add(
      "after", "after",
      false);
  }

  /**
   * Sets the storage item name that represents the collection to update.
   *
   * @param value	the storage name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the storage item name that represents the collection to update.
   *
   * @return		the storage name
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
    return "The name of the storage item that represents the collection to update.";
  }

  /**
   * Sets the position where to insert the string.
   *
   * @param value	the position
   */
  public void setPosition(Index value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the string.
   *
   * @return		the position
   */
  public Index getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return
        "The position where to insert the string.";
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the string is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Returns whether storage items are being updated.
   *
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
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
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", " + QuickInfoHelper.getVariable(this, "after") + ": ";
    else if (m_After)
      result += ", after: ";
    else
      result = ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Collection		coll;
    Object		obj;
    int			pos;

    result = null;

    obj  = m_InputToken.getPayload();
    coll = null;
    if (!getStorageHandler().getStorage().has(m_StorageName))
      result = "Collection not available from storage: " + m_StorageName;
    else
      coll = (Collection) getStorageHandler().getStorage().get(m_StorageName);

    if (result == null) {
      // determine position
      if (coll.size() == 0) {
	pos = 0;
      }
      else {
	m_Position.setMax(coll.size());
	pos = m_Position.getIntIndex();
	if (pos == -1)
	  return null;
	if (m_After)
	  pos++;
      }

      // insert
      if (pos == coll.size()) {
	coll.add(obj);
      }
      else {
        if (coll instanceof List) {
	  ((List) coll).add(pos, obj);
	}
	else {
	  result = "Collection does not implement the " + Utils.classToString(List.class) + " interface, "
	    + "can only append at the end: " + Utils.classToString(coll);
	}
      }

      if (result == null)
	m_OutputToken = new Token(obj);
    }

    return result;
  }
}
