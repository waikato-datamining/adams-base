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
 * CountObjectsInPolygon.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.objectfinder.ObjectsInPolygon;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 <!-- globalinfo-start -->
 * Counts the objects in the report passing through that fall into the defined region.<br>
 * Partial hits can be counted as well, using their percentage in overlap as count.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CountObjectsInPolygon
  extends AbstractTransformer
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = 9063650105550850888L;

  /** the x coordinates. */
  protected String m_X;

  /** the y coordinates. */
  protected String m_Y;

  /** whether to include partial counts. */
  protected boolean m_PartialCounts;

  /** whether report contains one-based coordinates. */
  protected boolean m_OneBased;

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** whether to check the type (if a suffix provided). */
  protected boolean m_CheckType;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression to apply to the type. */
  protected BaseRegExp m_TypeFind;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Counts the objects in the report passing through that fall into the "
	+ "defined region.\n"
	+ "Partial hits can be counted as well, using their percentage in "
	+ "overlap as count.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      "");

    m_OptionManager.add(
      "y", "Y",
      "");

    m_OptionManager.add(
      "partial-counts", "partialCounts",
      false);

    m_OptionManager.add(
      "one-based", "oneBased",
      false);

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "check-type", "checkType",
      false);

    m_OptionManager.add(
      "type-suffix", "typeSuffix",
      "");

    m_OptionManager.add(
      "type-find", "typeFind",
      new BaseRegExp(""));
  }

  /**
   * Sets the X coordinates (1-based) as comma-separated list.
   *
   * @param value 	the comma-separated list
   */
  public void setX(String value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the comma-separated list of X coordinates (1-based).
   *
   * @return 		the comma-separated list
   */
  public String getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X coordinates of the polygon (1-based).";
  }

  /**
   * Sets the X coordinates (1-based) as comma-separated list.
   *
   * @param value 	the comma-separated list
   */
  public void setY(String value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the comma-separated list of Y coordinates (1-based).
   *
   * @return 		the comma-separated list
   */
  public String getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y coordinates of the polygon (1-based).";
  }

  /**
   * Sets whether to include partial hits.
   *
   * @param value 	true if to include partial hits
   */
  public void setPartialCounts(boolean value) {
    m_PartialCounts = value;
    reset();
  }

  /**
   * Returns whether to include partial hits.
   *
   * @return 		true if to count partial hits
   */
  public boolean getPartialCounts() {
    return m_PartialCounts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String partialCountsTipText() {
    return "If enabled, partial hits are counted as well.";
  }

  /**
   * Sets whether to assume 1-based coordinates in report.
   *
   * @param value 	true if to use 1-based coordinates
   */
  public void setOneBased(boolean value) {
    m_OneBased = value;
    reset();
  }

  /**
   * Returns whether to assume 1-based coordinates in report.
   *
   * @return 		true if to use 1-based coordinates
   */
  public boolean getOneBased() {
    return m_OneBased;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String oneBasedTipText() {
    return "If enabled, coordinates in the report are assumed to be 1-based instead of 0-based.";
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
   * Sets the field suffix for the type used in the report (ignored if no check).
   *
   * @param value 	the field suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the field suffix for the type used in the report (ignored if no check).
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
    return "The report field suffix for the type used in the report (ignored if no check).";
  }

  /**
   * Sets the regular expression to apply to the type, ignored if no check.
   *
   * @param value 	the expression
   */
  public void setTypeFind(BaseRegExp value) {
    m_TypeFind = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the type, ignored if no check.
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
    return "The regular expression to apply to the type, ignored if no check.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
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
    result += QuickInfoHelper.toString(this, "partialCounts", m_PartialCounts, "partial", ", ");
    if (m_CheckType) {
      result += QuickInfoHelper.toString(this, "typeSuffix", m_TypeSuffix.isEmpty() ? "-missing-" : m_TypeSuffix, ", type suffix: ");
      result += QuickInfoHelper.toString(this, "typeFind", m_TypeFind.isEmpty() ? "-missing-" : m_TypeFind, ", type find: ");
    }

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
    Report		report;
    ObjectsInPolygon	finder;
    TIntSet 		indices;

    result = null;

    report = null;
    if (m_InputToken.hasPayload(Report.class))
      report = m_InputToken.getPayload(Report.class);
    else if (m_InputToken.hasPayload(ReportHandler.class))
      report = m_InputToken.getPayload(ReportHandler.class).getReport();
    else
      result = m_InputToken.unhandledData();

    if (report != null) {
      // locate objects
      finder = new ObjectsInPolygon();
      finder.setX(m_X);
      finder.setY(m_Y);
      finder.setPartial(m_PartialCounts);
      finder.setOneBased(m_OneBased);
      finder.setPrefix(m_Prefix);
      finder.setCheckType(m_CheckType);
      finder.setTypeSuffix(m_TypeSuffix);
      finder.setTypeFind(m_TypeFind);
      indices = new TIntHashSet(finder.find(report));

      if (isLoggingEnabled())
	getLogger().info("Count: " + indices.size());

      m_OutputToken = new Token(indices.size());
    }

    return result;
  }
}
