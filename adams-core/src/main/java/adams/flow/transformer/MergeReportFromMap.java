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
 * MergeReportFromMap.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.conversion.ReportArrayToMap;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Merges the passing through spectrum&#47;sample data objects with the referenced map of sample data objects in storage (the map uses the sample ID as key).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: MergeReportFromMap
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
 * &nbsp;&nbsp;&nbsp;The name of the stored map containing the sample ID &lt;-&gt; sample data object
 * &nbsp;&nbsp;&nbsp;mapping.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-key &lt;adams.data.report.Field&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The field to use as key in the map.
 * &nbsp;&nbsp;&nbsp;default:
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
 */
public class MergeReportFromMap
  extends AbstractTransformer
  implements StorageUser, ClassCrossReference {

  private static final long serialVersionUID = 2746984385469969356L;

  /** the type of merge operation to perform. */
  public enum MergeType {
    REPLACE,
    MERGE_CURRENT_WITH_OTHER,
    MERGE_OTHER_WITH_CURRENT
  }

  /** the name of the stored value. */
  protected StorageName m_StorageName;

  /** the field to acts as key in the map. */
  protected Field m_Key;

  /** the merge type. */
  protected MergeReport.MergeType m_Merge;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges the passing through report handler/report objects with the "
      + "referenced map of report objects in storage. The specified key field "
      + "is used to determine the key (string) in the map.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{ReportArrayToMap.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "key", "key",
      new Field());

    m_OptionManager.add(
      "merge", "merge",
      MergeReport.MergeType.MERGE_CURRENT_WITH_OTHER);
  }

  /**
   * Sets the name of the stored map with the sample data objects.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored map with the sample data objects.
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
    return "The name of the stored map containing the sample ID <-> sample data object mapping.";
  }

  /**
   * Sets the field to use as key in the map.
   *
   * @param value	the field
   */
  public void setKey(Field value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the field in use as key in the map.
   *
   * @return 		the field
   */
  public Field getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The field to use as key in the map.";
  }

  /**
   * Sets the merge type.
   *
   * @param value	the merge
   */
  public void setMerge(MergeReport.MergeType value) {
    m_Merge = value;
    reset();
  }

  /**
   * Returns the merge type.
   *
   * @return		the merge
   */
  public MergeReport.MergeType getMerge() {
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
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{MutableReportHandler.class, Report.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{MutableReportHandler.class, Report.class};
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return true if storage items are used
   */
  @Override
  public boolean isUsingStorage() {
    return !getSkip();
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
    result += QuickInfoHelper.toString(this, "key", m_Key, ", key: ");
    result += QuickInfoHelper.toString(this, "merge", m_Merge, ", merge: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Map<String,Report> 		map;
    MutableReportHandler 	handler;
    Report			current;
    Report			other;
    Report			merged;
    String			id;

    result = null;
    map    = null;

    // get map
    if (getStorageHandler().getStorage().has(m_StorageName)) {
      if (getStorageHandler().getStorage().get(m_StorageName) instanceof Map)
	map = (Map<String,Report>) getStorageHandler().getStorage().get(m_StorageName);
      else
        result = "Expected Report/ReportHandler in storage, but found: " + Utils.classToString(getStorageHandler().getStorage().get(m_StorageName));
    }
    else {
      result = "Storage item with bays not available: " + m_StorageName;
    }

    // input data
    handler = null;
    current = new Report();
    merged  = new Report();
    id      = null;
    if (m_InputToken.hasPayload(MutableReportHandler.class)) {
      handler = m_InputToken.getPayload(MutableReportHandler.class);
      current = handler.getReport();
      id      = "" + current.getValue(m_Key);
    }
    else if (m_InputToken.hasPayload(Report.class)) {
      current = m_InputToken.getPayload(Report.class);
      id      = "" + current.getValue(m_Key);
    }
    else {
      result = m_InputToken.unhandledData();
    }

    // get from map
    other = new Report();
    if (result == null) {
      if (map != null) {
        if (map.containsKey(id))
	  other = map.get(id);
	else
	  result = "Failed to retrieve report from map using ID: " + id;
      }
    }

    if (result == null) {
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
    }

    if (result == null) {
      if (handler != null) {
	handler.setReport(merged);
	m_OutputToken = new Token(handler);
      }
      else {
        m_OutputToken = new Token(merged);
      }
    }

    return result;
  }
}
