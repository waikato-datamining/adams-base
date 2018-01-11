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
 * WekaMultiLabelSplitter.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Reorder;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Splits a dataset containing multiple class attributes ('multi-label') into separate datasets with only a single class attribute.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MultiLabelSplitter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-regexp &lt;java.lang.String&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the strings.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 *
 * <pre>-update (property: updateRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the name of the class attribute is used as new relation
 * &nbsp;&nbsp;&nbsp;name.
 * </pre>
 *
 * <pre>-make-class-last (property: makeClassLast)
 * &nbsp;&nbsp;&nbsp;If set to true, then the new class attribute will be moved to the end.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaMultiLabelSplitter
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4034797930137217038L;

  /** the key for storing the current class attributes in the backup. */
  public final static String BACKUP_CLASSATTRIBUTES = "class attributes";

  /** the key for storing the current attributes to process in the backup. */
  public final static String BACKUP_ATTRIBUTESTOPROCESS = "attributes to process";

  /** the regular expression that the class attribute names have to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /** whether to use the class attribute name as new relation name. */
  protected boolean m_UpdateRelationName;

  /** whether to move the class attribute to the end. */
  protected boolean m_MakeClassLast;

  /** the indices of the class attributes. */
  protected List<Integer> m_ClassAttributes;

  /** the indices of the class attributes still to process. */
  protected List<Integer> m_AttributesToProcess;

  /** the dataset to process. */
  protected Instances m_Dataset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Splits a dataset containing multiple class attributes ('multi-label') "
      + "into separate datasets with only a single class attribute.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);

    m_OptionManager.add(
	    "update", "updateRelationName",
	    false);

    m_OptionManager.add(
	    "make-class-last", "makeClassLast",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp, (m_Invert ? "! " : ""));
  }

  /**
   * Sets the regular expression to match the strings against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the strings against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the strings.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Sets whether to update the relation name with the new class attribute.
   *
   * @param value	true if to update the relation name
   */
  public void setUpdateRelationName(boolean value) {
    m_UpdateRelationName = value;
    reset();
  }

  /**
   * Returns whether to update the relation name with the new class attribute.
   *
   * @return		true if name is updated
   */
  public boolean getUpdateRelationName() {
    return m_UpdateRelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateRelationNameTipText() {
    return
        "If set to true, then the name of the class attribute is used as new "
      + "relation name.";
  }

  /**
   * Sets whether to make the class attribute the last attribute.
   *
   * @param value	if true then the class attribute will be last
   */
  public void setMakeClassLast(boolean value) {
    m_MakeClassLast = value;
    reset();
  }

  /**
   * Returns whether to make the class attribute the last attribute.
   *
   * @return		true if the class attribute is moved to the end
   */
  public boolean getMakeClassLast() {
    return m_MakeClassLast;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String makeClassLastTipText() {
    return "If set to true, then the new class attribute will be moved to the end.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_CLASSATTRIBUTES);
    pruneBackup(BACKUP_ATTRIBUTESTOPROCESS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_ClassAttributes != null)
      result.put(BACKUP_CLASSATTRIBUTES, m_ClassAttributes);
    if (m_AttributesToProcess != null)
      result.put(BACKUP_ATTRIBUTESTOPROCESS, m_AttributesToProcess);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CLASSATTRIBUTES)) {
      m_ClassAttributes = (List<Integer>) state.get(BACKUP_CLASSATTRIBUTES);
      state.remove(BACKUP_CLASSATTRIBUTES);
    }
    if (state.containsKey(BACKUP_ATTRIBUTESTOPROCESS)) {
      m_AttributesToProcess = (List<Integer>) state.get(BACKUP_ATTRIBUTESTOPROCESS);
      state.remove(BACKUP_ATTRIBUTESTOPROCESS);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ClassAttributes     = new ArrayList<Integer>();
    m_AttributesToProcess = new ArrayList<Integer>();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int		i;

    result = null;

    m_Dataset = (Instances) m_InputToken.getPayload();
    for (i = 0; i < m_Dataset.numAttributes(); i++) {
      if (m_Invert) {
	if (!m_RegExp.isMatch(m_Dataset.attribute(i).name()))
	  m_ClassAttributes.add(i);
      }
      else {
	if (m_RegExp.isMatch(m_Dataset.attribute(i).name()))
	  m_ClassAttributes.add(i);
      }
    }

    if (m_ClassAttributes.size() == 0) {
      result =   "No attribute names matched the " + (m_Invert ? "inverted" : "")
               + " regular expression: " + m_RegExp;
    }
    else {
      m_AttributesToProcess.addAll(m_ClassAttributes);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_AttributesToProcess.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token		result;
    int			index;
    Remove		remove;
    Reorder		reorder;
    StringBuilder	indices;
    int			i;
    int			newIndex;
    Instances		processed;

    result = null;

    index   = m_AttributesToProcess.remove(0);
    remove  = new Remove();
    indices = new StringBuilder();
    for (i = 0; i < m_ClassAttributes.size(); i++) {
      if (m_ClassAttributes.get(i) == index)
	continue;
      if (indices.length() > 0)
	indices.append(",");
      indices.append("" + (m_ClassAttributes.get(i) + 1));
    }
    remove.setAttributeIndices(indices.toString());

    try {
      remove.setInputFormat(m_Dataset);
      processed = weka.filters.Filter.useFilter(m_Dataset, remove);
      if (m_UpdateRelationName)
	processed.setRelationName(m_Dataset.attribute(index).name());
      result = new Token(processed);
    }
    catch (Exception e) {
      processed = null;
      handleException(
	  "Failed to process dataset with following filter setup:\n"
	  + OptionUtils.getCommandLine(remove), e);
    }

    if (m_MakeClassLast && (processed != null)) {
      newIndex = processed.attribute(m_Dataset.attribute(index).name()).index();
      indices  = new StringBuilder();
      for (i = 0; i < processed.numAttributes(); i++) {
	if (i == newIndex)
	  continue;
	if (indices.length() > 0)
	  indices.append(",");
	indices.append("" + (i+1));
      }
      if (indices.length() > 0)
	indices.append(",");
      indices.append("" + (newIndex+1));
      reorder = new Reorder();
      try {
	reorder.setAttributeIndices(indices.toString());
	reorder.setInputFormat(processed);
	processed = weka.filters.Filter.useFilter(processed, reorder);
	if (m_UpdateRelationName)
	  processed.setRelationName(m_Dataset.attribute(index).name());
	result = new Token(processed);
      }
      catch (Exception e) {
	handleException(
	    "Failed to process dataset with following filter setup:\n"
	    + OptionUtils.getCommandLine(reorder), e);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_AttributesToProcess != null)
      m_AttributesToProcess.clear();
    if (m_ClassAttributes != null)
      m_ClassAttributes.clear();
    m_Dataset = null;

    super.wrapUp();
  }
}
