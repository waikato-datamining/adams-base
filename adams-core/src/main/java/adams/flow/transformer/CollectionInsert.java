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
 * CollectionInsert.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.flow.control.StorageName;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ObjectRetriever;
import adams.flow.core.ObjectRetriever.RetrievalType;
import adams.flow.core.Token;

import java.util.Collection;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Inserts an object in the collection using the specified position.<br>
 * The object can be retrieved from a callable actor or from storage.<br>
 * If the collection does not implement the java.util.List interface and the insertion is not at the end, the insertion will fail.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Collection<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Collection<br>
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
 * &nbsp;&nbsp;&nbsp;default: CollectionInsert
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
 * <pre>-retrieval-type &lt;AUTO|SOURCE_ACTOR|STORAGE&gt; (property: retrievalType)
 * &nbsp;&nbsp;&nbsp;Determines how to retrieve the object, in case of AUTO, first the callable
 * &nbsp;&nbsp;&nbsp;actor is checked and then the storage.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 * <pre>-object-actor &lt;adams.flow.core.CallableActorReference&gt; (property: objectActor)
 * &nbsp;&nbsp;&nbsp;The callable actor (source) to retrieve the object from, ignored if not
 * &nbsp;&nbsp;&nbsp;present.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-object-storage &lt;adams.flow.control.StorageName&gt; (property: objectStorage)
 * &nbsp;&nbsp;&nbsp;The storage item to retrieve the object from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-object-type &lt;adams.core.base.BaseClassname&gt; (property: objectType)
 * &nbsp;&nbsp;&nbsp;The interface or superclass to restrict the object to.
 * &nbsp;&nbsp;&nbsp;default: java.lang.Object
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
public class CollectionInsert
  extends AbstractTransformer {

  private static final long serialVersionUID = -4381778255320714964L;

  /** the position where to insert the string. */
  protected Index m_Position;

  /** whether to insert after the position instead of at. */
  protected boolean m_After;

  /** for retrieving the object. */
  protected ObjectRetriever m_Retriever;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Inserts an object in the collection using the specified position.\n"
      + "The object can be retrieved from a callable actor or from storage.\n"
      + "If the collection does not implement the " + Utils.classToString(List.class) + " "
      + "interface and the insertion is not at the end, the insertion will fail.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "retrieval-type", "retrievalType",
      RetrievalType.AUTO);

    m_OptionManager.add(
      "object-actor", "objectActor",
      new CallableActorReference());

    m_OptionManager.add(
      "object-storage", "objectStorage",
      new StorageName());

    m_OptionManager.add(
      "object-type", "objectType",
      new BaseClassname(Object.class));

    m_OptionManager.add(
      "position", "position",
      new Index(Index.LAST));

    m_OptionManager.add(
      "after", "after",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Retriever = new ObjectRetriever();
    m_Retriever.setFlowContext(this);
  }

  /**
   * Sets the retrieval type. In case of {@link RetrievalType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setRetrievalType(RetrievalType value) {
    m_Retriever.setRetrievalType(value);
    reset();
  }

  /**
   * Returns the retrieval type. In case of {@link RetrievalType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public RetrievalType getRetrievalType() {
    return m_Retriever.getRetrievalType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String retrievalTypeTipText() {
    return m_Retriever.retrievalTypeTipText();
  }

  /**
   * Sets the callable actor to retrieve the object from.
   *
   * @param value	the actor reference
   */
  public void setObjectActor(CallableActorReference value) {
    m_Retriever.setObjectActor(value);
    reset();
  }

  /**
   * Returns the callable actor to retrieve the object from.
   *
   * @return		the actor reference
   */
  public CallableActorReference getObjectActor() {
    return m_Retriever.getObjectActor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectActorTipText() {
    return m_Retriever.objectActorTipText();
  }

  /**
   * Sets the storage item name to get the object from.
   *
   * @param value	the storage name
   */
  public void setObjectStorage(StorageName value) {
    m_Retriever.setObjectStorage(value);
    reset();
  }

  /**
   * Returns the storage item name to get the object from.
   *
   * @return		the storage name
   */
  public StorageName getObjectStorage() {
    return m_Retriever.getObjectStorage();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectStorageTipText() {
    return m_Retriever.objectStorageTipText();
  }

  /**
   * Sets the interface or superclass to restrict the objects to.
   *
   * @param value	the class
   */
  public void setObjectType(BaseClassname value) {
    m_Retriever.setObjectType(value);
    reset();
  }

  /**
   * Returns the interface or superclass to restrict the objects to.
   *
   * @return		the class
   */
  public BaseClassname getObjectType() {
    return m_Retriever.getObjectType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectTypeTipText() {
    return m_Retriever.objectTypeTipText();
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Collection.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Collection.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (QuickInfoHelper.hasVariable(this, "after"))
      result = QuickInfoHelper.getVariable(this, "after") + ": ";
    else if (m_After)
      result = "after: ";
    else
      result = "at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);
    result += QuickInfoHelper.toString(this, "retrievalType", getRetrievalType(), ", type: ");
    result += QuickInfoHelper.toString(this, "objectSource", getObjectActor(), ", source: ");
    result += QuickInfoHelper.toString(this, "objectStorage", getObjectStorage(), ", storage: ");

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
    MessageCollection	errors;
    Object		obj;
    int			pos;

    result = null;
    coll   = m_InputToken.getPayload(Collection.class);
    errors = new MessageCollection();
    obj    = m_Retriever.getObject(errors);
    if (!errors.isEmpty()) {
      result = errors.toString();
    }
    else {
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
	m_OutputToken = new Token(coll);
    }

    return result;
  }
}
