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
 * MergeReport.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.report.Report;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Allows the report passing through to to be merged with another one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
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
 * &nbsp;&nbsp;&nbsp;default: MergeReport
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
 * <pre>-type &lt;SOURCE|STORAGE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;Determines how to obtain the other report for merging.
 * &nbsp;&nbsp;&nbsp;default: SOURCE
 * </pre>
 * 
 * <pre>-source &lt;adams.flow.core.CallableActorReference&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The source actor to obtain the report from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: storage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the report from.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-merge &lt;REPLACE|MERGE_CURRENT_WITH_OTHER|MERGE_OTHER_WITH_CURRENT&gt; (property: merge)
 * &nbsp;&nbsp;&nbsp;Determines how to perform the merge.
 * &nbsp;&nbsp;&nbsp;default: MERGE_CURRENT_WITH_OTHER
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeReport
  extends AbstractTransformer
  implements StorageUser {

  private static final long serialVersionUID = 8198756617336896352L;

  /** the source of the other report. */
  public enum SourceType {
    SOURCE,
    STORAGE
  }

  /** the type of merge operation to perform. */
  public enum MergeType {
    REPLACE,
    MERGE_CURRENT_WITH_OTHER,
    MERGE_OTHER_WITH_CURRENT
  }

  /** the source type. */
  protected SourceType m_Type;

  /** the source actor. */
  protected CallableActorReference m_Source;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the merge type. */
  protected MergeType m_Merge;
  
  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the report passing through to to be merged with another one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      SourceType.SOURCE);

    m_OptionManager.add(
      "source", "source",
      new CallableActorReference());

    m_OptionManager.add(
      "storage", "storage",
      new StorageName());

    m_OptionManager.add(
      "merge", "merge",
      MergeType.MERGE_CURRENT_WITH_OTHER);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "merge", m_Merge, ", merge: ");
    
    return result;
  }

  /**
   * Sets the type of source.
   *
   * @param value	the type
   */
  public void setType(SourceType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of source.
   *
   * @return		the type
   */
  public SourceType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "Determines how to obtain the other report for merging.";
  }

  /**
   * Sets the report source actor.
   *
   * @param value	the source
   */
  public void setSource(CallableActorReference value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the report source actor.
   *
   * @return		the source
   */
  public CallableActorReference getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The source actor to obtain the report from.";
  }

  /**
   * Sets the report storage item.
   *
   * @param value	the storage item
   */
  public void setStorage(StorageName value) {
    m_Storage = value;
    reset();
  }

  /**
   * Returns the report storage item.
   *
   * @return		the storage item
   */
  public StorageName getStorage() {
    return m_Storage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageTipText() {
    return "The storage item to obtain the report from.";
  }

  /**
   * Sets the merge type.
   *
   * @param value	the merge
   */
  public void setMerge(MergeType value) {
    m_Merge = value;
    reset();
  }

  /**
   * Returns the merge type.
   *
   * @return		the merge
   */
  public MergeType getMerge() {
    return m_Merge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mergeTipText() {
    return "Determines how to perform the merge.";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return (m_Type == SourceType.STORAGE);
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getSource());
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Report.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Compatibility		comp;
    Actor			source;
    Token 			token;
    Report			current;
    Report			merged;
    Report			other;
    Storage			storage;

    result = null;

    current = (Report) m_InputToken.getPayload();
    other   = null;
    switch (m_Type) {
      case SOURCE:
	source  = findCallableActor();
	if (source instanceof OutputProducer) {
	  comp = new Compatibility();
	  if (!comp.isCompatible(new Class[]{Report.class}, ((OutputProducer) source).generates()))
	    result = "Callable actor '" + m_Source + "' does not produce output that is compatible with '" + Report.class.getName() + "'!";
	}
	else {
	  result = "Callable actor '" + m_Source + "' does not produce any output!";
	}
	token = null;
	if (result == null) {
	  result = source.execute();
	  if (result != null) {
	    result = "Callable actor '" + m_Source + "' execution failed:\n" + result;
	  }
	  else {
	    if (((OutputProducer) source).hasPendingOutput())
	      token = ((OutputProducer) source).output();
	    else
	      result = "Callable actor '" + m_Source + "' did not generate any output!";
	  }
	}
	if (token != null)
	  other = (Report) token.getPayload();
	break;

      case STORAGE:
	storage = getStorageHandler().getStorage();
	if (!storage.has(m_Storage)) {
	  result = "Storage item not available: " + m_Storage;
	}
	else {
	  other = (Report) storage.get(m_Storage);
	}
	break;

      default:
	result = "Unhandled source type: " + m_Type;
    }
    
    // merge
    if (other != null) {
      merged = null;
      switch (m_Merge) {
	case REPLACE:
	  merged = other.getClone();
	  break;

	case MERGE_CURRENT_WITH_OTHER:
	  merged = current.getClone();
	  merged.mergeWith(other);
	  break;

	case MERGE_OTHER_WITH_CURRENT:
	  merged = other.getClone();
	  merged.mergeWith(current);
	  break;

	default:
	  result = "Unhandled merge type: " + m_Merge;
      }
      if (merged != null)
	m_OutputToken = new Token(merged);
    }

    return result;
  }
}
