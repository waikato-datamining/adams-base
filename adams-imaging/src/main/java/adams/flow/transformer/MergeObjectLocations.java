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
 * MergeObjectLocations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.data.image.AbstractImageContainer;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Merges the object locations in the report of the container passing through with the one obtained from storage.<br>
 * The 'overlap action' determines what to do if objects overlap.<br>
 * With the 'check type' you can still trigger a 'skip' if the type values of the two overlapping objects differ.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: MergeObjectLocations
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
 * &nbsp;&nbsp;&nbsp;The name of the storage item to merge with (Report or ReportHandler).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 * <pre>-overlap-action &lt;SKIP|KEEP&gt; (property: overlapAction)
 * &nbsp;&nbsp;&nbsp;The action to take when an object from this and the other report overlap.
 * &nbsp;&nbsp;&nbsp;default: SKIP
 * </pre>
 * 
 * <pre>-no-overlap-action &lt;SKIP|KEEP&gt; (property: noOverlapAction)
 * &nbsp;&nbsp;&nbsp;The action to take when an object has no overlaps at all.
 * &nbsp;&nbsp;&nbsp;default: KEEP
 * </pre>
 * 
 * <pre>-check-type &lt;boolean&gt; (property: checkType)
 * &nbsp;&nbsp;&nbsp;If enabled, the type of the objects gets checked as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The report field suffix for the type used in the report (ignored if empty
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-type-find &lt;adams.core.base.BaseRegExp&gt; (property: typeFind)
 * &nbsp;&nbsp;&nbsp;The regular expression to apply to the type, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-type-replace &lt;java.lang.String&gt; (property: typeReplace)
 * &nbsp;&nbsp;&nbsp;The replacement string to use with the replacement regular expression.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-min-overlap-ratio &lt;double&gt; (property: minOverlapRatio)
 * &nbsp;&nbsp;&nbsp;The minimum ratio that an overlap must have before being considered an actual 
 * &nbsp;&nbsp;&nbsp;overlap.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeObjectLocations
  extends AbstractTransformer {

  private static final long serialVersionUID = 8175397929496972306L;

  /**
   * Determines what to do when two objects overlap.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OverlapAction {
    SKIP,
    KEEP,
  }

  /**
   * Determines what to do when an object has no overlaps.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum NoOverlapAction {
    SKIP,
    KEEP,
  }

  /** the storage item. */
  protected StorageName m_StorageName;

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** what to do when two objects overlap. */
  protected OverlapAction m_OverlapAction;

  /** what to do when an object has no overlaps. */
  protected NoOverlapAction m_NoOverlapAction;

  /** whether to check the type (if a suffix provided). */
  protected boolean m_CheckType;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression to apply to the type. */
  protected BaseRegExp m_TypeFind;

  /** the replacement for the type. */
  protected String m_TypeReplace;

  /** the minimum overlap ratio to use. */
  protected double m_MinOverlapRatio;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Merges the object locations in the report of the container passing "
	+ "through with the one obtained from storage.\n"
	+ "The 'overlap action' determines what to do if objects overlap.\n"
	+ "With the 'check type' you can still trigger a 'skip' if the type "
	+ "values of the two overlapping objects differ.";
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
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "overlap-action", "overlapAction",
      OverlapAction.SKIP);

    m_OptionManager.add(
      "no-overlap-action", "noOverlapAction",
      NoOverlapAction.KEEP);

    m_OptionManager.add(
      "check-type", "checkType",
      false);

    m_OptionManager.add(
      "type-suffix", "typeSuffix",
      "");

    m_OptionManager.add(
      "type-find", "typeFind",
      new BaseRegExp(""));

    m_OptionManager.add(
      "type-replace", "typeReplace",
      "");

    m_OptionManager.add(
      "min-overlap-ratio", "minOverlapRatio",
      0.0, 0.0, 1.0);
  }

  /**
   * Sets the name of the storage item to merge with (Report or ReportHandler).
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to merge with (Report or ReportHandler).
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
    return "The name of the storage item to merge with (Report or ReportHandler).";
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the action to take when an object from this and the other report overlap.
   *
   * @param value 	the action
   */
  public void setOverlapAction(OverlapAction value) {
    m_OverlapAction = value;
    reset();
  }

  /**
   * Returns the action to take when an object from this and the other report overlap.
   *
   * @return 		the action
   */
  public OverlapAction getOverlapAction() {
    return m_OverlapAction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapActionTipText() {
    return "The action to take when an object from this and the other report overlap.";
  }

  /**
   * Sets the action to take when an object has no overlaps.
   *
   * @param value 	the action
   */
  public void setNoOverlapAction(NoOverlapAction value) {
    m_NoOverlapAction = value;
    reset();
  }

  /**
   * Returns the action to take when an object has no overlaps.
   *
   * @return 		the action
   */
  public NoOverlapAction getNoOverlapAction() {
    return m_NoOverlapAction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noOverlapActionTipText() {
    return "The action to take when an object has no overlaps at all.";
  }

  /**
   * Sets whether to check the type as well.
   *
   * @param value 	true if to check
   */
  public void setCheckType(boolean value) {
    m_CheckType = value;
    reset();
  }

  /**
   * Returns the field suffix for the type used in the report (ignored if empty).
   *
   * @return 		true if to check
   */
  public boolean getCheckType() {
    return m_CheckType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkTypeTipText() {
    return "If enabled, the type of the objects gets checked as well.";
  }

  /**
   * Sets the field suffix for the type used in the report (ignored if empty).
   *
   * @param value 	the field suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the field suffix for the type used in the report (ignored if empty).
   *
   * @return 		the field suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The report field suffix for the type used in the report (ignored if empty).";
  }

  /**
   * Sets the regular expression to apply to the type, ignored if empty.
   *
   * @param value 	the expression
   */
  public void setTypeFind(BaseRegExp value) {
    m_TypeFind = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the type, ignored if empty.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeFind() {
    return m_TypeFind;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeFindTipText() {
    return "The regular expression to apply to the type, ignored if empty.";
  }

  /**
   * Sets the replacement string to use with the replacement regular expression.
   *
   * @param value 	the replacement
   */
  public void setTypeReplace(String value) {
    m_TypeReplace = value;
    reset();
  }

  /**
   * Returns the replacement string to use with the replacement regular expression.
   *
   * @return 		the replacement
   */
  public String getTypeReplace() {
    return m_TypeReplace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeReplaceTipText() {
    return "The replacement string to use with the replacement regular expression.";
  }

  /**
   * Sets the minimum overlap ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinOverlapRatio(double value) {
    if (getOptionManager().isValid("minOverlapRatio", value)) {
      m_MinOverlapRatio = value;
      reset();
    }
  }

  /**
   * Returns the minimum overlap ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinOverlapRatio() {
    return m_MinOverlapRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minOverlapRatioTipText() {
    return "The minimum ratio that an overlap must have before being considered an actual overlap.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "prefix", m_Prefix, ", prefix: ");
    result += QuickInfoHelper.toString(this, "overlapAction", m_OverlapAction, ", overlap: ");
    result += QuickInfoHelper.toString(this, "noOverlapAction", m_NoOverlapAction, ", no-overlap: ");
    result += QuickInfoHelper.toString(this, "typeSuffix", m_TypeSuffix.isEmpty() ? "-ignored-" : m_TypeSuffix, ", type suffix: ");
    result += QuickInfoHelper.toString(this, "minOverlapRatio", m_MinOverlapRatio, ", overlap ratio: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			obj;
    Report			newReport;
    Report			thisReport;
    Report 			otherReport;
    LocatedObjects		thisObjs;
    LocatedObjects		otherObjs;
    LocatedObjects		mergedObjs;
    boolean			add;
    int				overlaps;
    Object			output;
    String 			typeKey;
    String			typeNew;

    result = null;

    output      = null;
    thisReport  = null;
    otherReport = null;
    if (m_TypeSuffix.startsWith("."))
      typeKey = m_TypeSuffix.substring(1);
    else
      typeKey = m_TypeSuffix;

    if (m_InputToken.getPayload() instanceof AbstractImageContainer)
      thisReport = ((AbstractImageContainer) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      thisReport = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      thisReport = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unsupported input class: " + Utils.classToString(m_InputToken.getPayload().getClass());

    if (thisReport != null) {
      obj = getStorageHandler().getStorage().get(m_StorageName);
      if (obj == null)
	result = "Failed to retrieve storage item: " + m_StorageName;
      else {
	if (obj instanceof Report)
	  otherReport = (Report) obj;
	else if (obj instanceof ReportHandler)
	  otherReport = ((ReportHandler) obj).getReport();
	else
	  result = "Unhandled type of storage item '" + m_StorageName + "': " + Utils.classToString(obj.getClass());
      }
    }

    if (otherReport != null) {
      thisObjs   = LocatedObjects.fromReport(thisReport,  m_Prefix);
      otherObjs  = LocatedObjects.fromReport(otherReport, m_Prefix);
      mergedObjs = new LocatedObjects();
      for (LocatedObject thisObj: thisObjs) {
	add      = true;
	overlaps = 0;
	for (LocatedObject otherObj: otherObjs) {
	  if (thisObj.overlapRatio(otherObj) >= m_MinOverlapRatio) {
	    overlaps++;
	    switch (m_OverlapAction) {
	      case SKIP:
		add = false;
		break;
	      case KEEP:
		if (m_CheckType) {
		  if (thisObj.getMetaData().containsKey(m_TypeSuffix) && otherObj.getMetaData().containsKey(m_TypeSuffix))
		    add = thisObj.getMetaData().get(m_TypeSuffix).equals(otherObj.getMetaData().get(m_TypeSuffix));
		}
		if (add) {
		  for (String key: otherObj.getMetaData().keySet())
		    thisObj.getMetaData().putIfAbsent(key, otherObj.getMetaData().get(key));
		}
		break;
	      default:
		throw new IllegalStateException("Unhandled overlap action: " + m_OverlapAction);
	    }
	  }
	  if (!add)
	    break;
	}
	// check for no overlaps
	if (overlaps == 0) {
	  switch (m_NoOverlapAction) {
	    case SKIP:
	      add = false;
	      break;
	    case KEEP:
	      // nothing to do
	      break;
	    default:
	      throw new IllegalStateException("Unhandled no-overlap action: " + m_NoOverlapAction);
	  }
	}
	if (add) {
	  // update type?
	  if (!typeKey.isEmpty() && !m_TypeFind.isEmpty()) {
	    if (thisObj.getMetaData().containsKey(typeKey)) {
	      typeNew = thisObj.getMetaData().get(typeKey).toString();
	      typeNew = typeNew.replaceFirst(m_TypeFind.getValue(), m_TypeReplace);
	      thisObj.getMetaData().put(typeKey, typeNew);
	    }
	  }
	  mergedObjs.add(thisObj);
	}
      }

      // assemble new report
      try {
	newReport = thisReport.getClass().newInstance();
	// transfer non-object fields
	for (AbstractField field: thisReport.getFields()) {
	  if (!field.getName().startsWith(m_Prefix)) {
	    newReport.addField(field);
	    newReport.setValue(field, thisReport.getValue(field));
	  }
	}
	// store objects
	newReport.mergeWith(mergedObjs.toReport(m_Prefix));
	// update report
	if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
	  output = m_InputToken.getPayload();
	  ((AbstractImageContainer) output).setReport(newReport);
	}
	else if (m_InputToken.getPayload() instanceof MutableReportHandler) {
	  output = m_InputToken.getPayload();
	  ((MutableReportHandler) output).setReport(newReport);
	}
	else {
	  output = newReport;
	}
      }
      catch (Exception e) {
	result = handleException("Failed to create new report with merged objects!", e);
	output = null;
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
