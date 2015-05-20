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
 * ClassSelector.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.ml.data.Dataset;

/**
 <!-- globalinfo-start -->
 * Sets (or unsets) the class index&#47;indices. Can either honour already existing ones or override them.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.ml.data.Dataset<br>
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
 * &nbsp;&nbsp;&nbsp;default: ClassSelector
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-class &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The attribute(s) to act as class attribute(s).
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-override &lt;boolean&gt; (property: override)
 * &nbsp;&nbsp;&nbsp;If set to true, then any existing class index will get removed first; otherwise 
 * &nbsp;&nbsp;&nbsp;the class index&#47;indices will get added.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-unset &lt;boolean&gt; (property: unset)
 * &nbsp;&nbsp;&nbsp;Unsets any class attribute.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassSelector
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the class indices. */
  protected SpreadSheetColumnRange m_ClassIndex;

  /** whether to override any set class attribute.  */
  protected boolean m_Override;
  
  /** whether to unset the class index. */
  protected boolean m_Unset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Sets (or unsets) the class index/indices. Can either honour already "
	+ "existing ones or override them.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ClassIndex = new SpreadSheetColumnRange();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "class", "classIndex",
	    new SpreadSheetColumnRange(SpreadSheetColumnRange.LAST));

    m_OptionManager.add(
	    "override", "override",
	    false);

    m_OptionManager.add(
	    "unset", "unset",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (m_Unset || QuickInfoHelper.hasVariable(this, "unset")) {
      result  = QuickInfoHelper.toString(this, "unset", m_Unset, "unset");
    }
    else {
      result  = QuickInfoHelper.toString(this, "classIndex", m_ClassIndex);
      result += QuickInfoHelper.toString(this, "override", m_Override, "override", ", ");
    }

    return result;
  }

  /**
   * Sets the range of class attributes.
   *
   * @param value	the range
   */
  public void setClassIndex(SpreadSheetColumnRange value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the class attribute range.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The attribute(s) to act as class attribute(s).";
  }

  /**
   * Sets whether to override any existing class index or nor.
   *
   * @param value	if true then any class index will be override
   */
  public void setOverride(boolean value) {
    m_Override = value;
    reset();
  }

  /**
   * Returns whether any existing class index will be overriden or not.
   *
   * @return		true if any class index will be overriden
   */
  public boolean getOverride() {
    return m_Override;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overrideTipText() {
    return
        "If set to true, then any existing class index will get removed first; "
      + "otherwise the class index/indices will get added.";
  }

  /**
   * Sets whether to unset the class attribute.
   *
   * @param value	true if to unset
   */
  public void setUnset(boolean value) {
    m_Unset = value;
    reset();
  }

  /**
   * Returns whether to unset the class attribute.
   *
   * @return		true if to unset
   */
  public boolean getUnset() {
    return m_Unset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unsetTipText() {
    return "Unsets any class attribute.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Dataset.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    Dataset	data;
    int[]	indices;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    if (sheet instanceof Dataset)
      data = (Dataset) sheet;
    else
      data = new Dataset(sheet);
    
    indices = data.getClassAttributeIndices();
    if (m_Unset) {
      data.removeClassAttributes();
    }
    else {
      if (m_Override)
	data.removeClassAttributes();
      if ((indices.length == 0) || ((indices.length == 0) && m_Override)) {
	m_ClassIndex.setData(data);
	indices = m_ClassIndex.getIntIndices();
	for (int index: indices)
	  data.setClassAttribute(index, true);
      }
    }

    m_OutputToken = new Token(data);
    updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
