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
 * CompareObjectTypes.java
 * Copyright (C) 2016-2023 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

/**
 <!-- globalinfo-start -->
 * Compares the object types between objects from the two reports.<br>
 * The first report is considered 'ground truth'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: CompareObjectTypes
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
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
 */
public class CompareObjectTypes
  extends AbstractTransformer
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -2856574104135118360L;

  /** the object prefix to use. */
  protected String m_Prefix;

  /** the object type suffix to use. */
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
      "Compares the object types between objects from the two reports.\n"
	+ "The first report is considered 'ground truth'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

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

    result  = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
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
    return new Class[]{Report[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Updates the type if necessary.
   *
   * @param obj		the object to update
   * @param typeKey	the key of the type in the meta-data
   */
  protected void updateType(LocatedObject obj, String typeKey) {
    String	typeNew;

    if (!typeKey.isEmpty() && !m_TypeFind.isEmpty()) {
      if (obj.getMetaData().containsKey(typeKey)) {
	typeNew = obj.getMetaData().get(typeKey).toString();
	typeNew = typeNew.replaceFirst(m_TypeFind.getValue(), m_TypeReplace);
	obj.getMetaData().put(typeKey, typeNew);
      }
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Report[]		reports;
    Report		truth;
    Report		other;
    LocatedObjects	truthObjs;
    LocatedObjects	otherObjs;
    LocatedObject	largestOverlapObj;
    double		largestOverlap;
    double		overlap;
    SpreadSheet		sheet;
    Row			row;
    String		suffix;
    String 		typeKey;
    String		typeNew;

    result = null;

    sheet   = null;
    truth   = null;
    other   = null;
    reports = (Report[]) m_InputToken.getPayload();
    if (reports.length == 2) {
      truth = reports[0];
      other = reports[1];
    }
    else {
      result = "Expected two reports, found: " + reports.length;
    }

    if (result == null) {
      truthObjs = LocatedObjects.fromReport(truth, m_Prefix);
      otherObjs = LocatedObjects.fromReport(other, m_Prefix);
      if (m_TypeSuffix.startsWith("."))
	suffix = m_TypeSuffix.substring(1);
      else
	suffix = m_TypeSuffix;
      if (m_TypeSuffix.startsWith("."))
	typeKey = m_TypeSuffix.substring(1);
      else
	typeKey = m_TypeSuffix;

      sheet = new DefaultSpreadSheet();

      // header
      row       = sheet.getHeaderRow();
      row.addCell("TX").setContentAsString("Truth X");
      row.addCell("TY").setContentAsString("Truth Y");
      row.addCell("TW").setContentAsString("Truth Width");
      row.addCell("TH").setContentAsString("Truth Height");
      row.addCell("TT").setContentAsString("Truth Type");
      row.addCell("OX").setContentAsString("Other X");
      row.addCell("OY").setContentAsString("Other Y");
      row.addCell("OW").setContentAsString("Other Width");
      row.addCell("OH").setContentAsString("Other Height");
      row.addCell("OT").setContentAsString("Other Type");
      row.addCell("M").setContentAsString("Match");

      // data
      for (LocatedObject truthObj: truthObjs) {
	// update type?
	updateType(truthObj, typeKey);

	row = sheet.addRow();
	row.addCell("TX").setContent(truthObj.getX());
	row.addCell("TY").setContent(truthObj.getY());
	row.addCell("TW").setContent(truthObj.getWidth());
	row.addCell("TH").setContent(truthObj.getHeight());
	row.addCell("TT").setNative(truthObj.getMetaData().containsKey(suffix) ? truthObj.getMetaData().get(suffix) : SpreadSheet.MISSING_VALUE);
	row.addCell("OX").setMissing();
	row.addCell("OY").setMissing();
	row.addCell("OW").setMissing();
	row.addCell("OH").setMissing();
	row.addCell("OT").setMissing();
	row.addCell("M").setMissing();

	largestOverlapObj = null;
	largestOverlap    = 0.0;
	for (LocatedObject otherObj: otherObjs) {
	  overlap = truthObj.overlapRatio(otherObj);
	  if ((overlap > largestOverlap) && (overlap >= m_MinOverlapRatio)) {
	    largestOverlap    = overlap;
	    largestOverlapObj = otherObj;
	  }
	}
	if (largestOverlapObj != null) {
	  // update type?
	  updateType(largestOverlapObj, typeKey);
	  
	  row.getCell("OX").setContent(largestOverlapObj.getX());
	  row.getCell("OY").setContent(largestOverlapObj.getY());
	  row.getCell("OW").setContent(largestOverlapObj.getWidth());
	  row.getCell("OH").setContent(largestOverlapObj.getHeight());
	  row.addCell("OT").setNative(largestOverlapObj.getMetaData().containsKey(suffix) ? largestOverlapObj.getMetaData().get(suffix) : SpreadSheet.MISSING_VALUE);
	  row.getCell("M").setContent(
	    !row.getCell("TT").isMissing()
	      && (row.getCell("TT").getContent().equals(row.getCell("OT").getContent())));
	}
      }
    }

    if (sheet != null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
