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
 * CountObjectsInRegion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.objectfinder.ObjectsInRegion;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
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
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
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
 * &nbsp;&nbsp;&nbsp;default: CountObjectsInRegion
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
 * <pre>-top &lt;int&gt; (property: top)
 * &nbsp;&nbsp;&nbsp;The y position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-left &lt;int&gt; (property: left)
 * &nbsp;&nbsp;&nbsp;The x position of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the region.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the region.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-partial-counts &lt;boolean&gt; (property: partialCounts)
 * &nbsp;&nbsp;&nbsp;If enabled, partial hits are counted as well (using their fraction in overlap
 * &nbsp;&nbsp;&nbsp;as count).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-one-based &lt;boolean&gt; (property: oneBased)
 * &nbsp;&nbsp;&nbsp;If enabled, coordinates in the report are assumed to be 1-based instead
 * &nbsp;&nbsp;&nbsp;of 0-based.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-check-type &lt;boolean&gt; (property: checkType)
 * &nbsp;&nbsp;&nbsp;If enabled, the type of the objects gets checked as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The report field suffix for the type used in the report (ignored if no check
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type-find &lt;adams.core.base.BaseRegExp&gt; (property: typeFind)
 * &nbsp;&nbsp;&nbsp;The regular expression to apply to the type, ignored if no check.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CountObjectsInRegion
  extends AbstractTransformer {

  private static final long serialVersionUID = 9063650105550850888L;

  /** the y of the top-left corner. */
  protected int m_Top;

  /** the x of the top-left corner. */
  protected int m_Left;

  /** the height of the window. */
  protected int m_Height;

  /** the width of the window. */
  protected int m_Width;

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
      "top", "top",
      1, 1, null);

    m_OptionManager.add(
      "left", "left",
      1, 1, null);

    m_OptionManager.add(
      "height", "height",
      1, 1, null);

    m_OptionManager.add(
      "width", "width",
      1, 1, null);

    m_OptionManager.add(
      "partial-counts", "partialCounts",
      false);

    m_OptionManager.add(
      "one-based", "oneBased",
      false);

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

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
   * Sets the y of the top-left corner.
   *
   * @param value 	the y
   */
  public void setTop(int value) {
    if (getOptionManager().isValid("top", value)) {
      m_Top = value;
      reset();
    }
  }

  /**
   * Returns the y of the top-left corner.
   *
   * @return 		the y
   */
  public int getTop() {
    return m_Top;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topTipText() {
    return "The y position of the top-left corner.";
  }

  /**
   * Sets the x of the top-left corner.
   *
   * @param value 	the x
   */
  public void setLeft(int value) {
    if (getOptionManager().isValid("left", value)) {
      m_Left = value;
      reset();
    }
  }

  /**
   * Returns the x of the top-left corner.
   *
   * @return 		the x
   */
  public int getLeft() {
    return m_Left;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftTipText() {
    return "The x position of the top-left corner.";
  }

  /**
   * Sets the height of the crop rectangle.
   *
   * @param value 	the height, ignored if less than 1
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the region.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the region.";
  }

  /**
   * Sets the width of the crop rectangle.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the region.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the region.";
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
    return "If enabled, partial hits are counted as well (using their fraction in overlap as count).";
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
    return new Class[]{Double.class};
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
    result += QuickInfoHelper.toString(this, "left", m_Left, ", l: ");
    result += QuickInfoHelper.toString(this, "top", m_Top, ", t: ");
    result += QuickInfoHelper.toString(this, "width", m_Width, ", w: ");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", h: ");
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
    LocatedObjects 	objs;
    LocatedObject 	region;
    double		overlap;
    double		count;
    ObjectsInRegion	finder;
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
      finder = new ObjectsInRegion();
      finder.setTop(m_Top);
      finder.setLeft(m_Left);
      finder.setHeight(m_Height);
      finder.setWidth(m_Width);
      finder.setPartial(m_PartialCounts);
      finder.setOneBased(m_OneBased);
      finder.setPrefix(m_Prefix);
      finder.setCheckType(m_CheckType);
      finder.setTypeSuffix(m_TypeSuffix);
      finder.setTypeFind(m_TypeFind);
      indices = new TIntHashSet(finder.find(report));

      // generate count
      count  = 0;
      objs   = LocatedObjects.fromReport(report, m_Prefix);
      region = new LocatedObject(null, m_Left - (m_OneBased ? 0 : 1), m_Top - (m_OneBased ? 0 : 1), m_Width, m_Height);
      if (isLoggingEnabled())
	getLogger().info("Region: " + region);
      for (LocatedObject obj : objs) {
        if (!indices.contains(obj.getIndex()))
          continue;

	if (isLoggingEnabled())
	  getLogger().info("Object: " + obj);

	overlap = obj.overlapRatio(region);
	if (isLoggingEnabled())
	  getLogger().info("Overlap: " + overlap);
	if (overlap == 1)
	  count++;
	else if ((overlap < 1.0) && m_PartialCounts)
	  count += overlap;
      }

      if (isLoggingEnabled())
	getLogger().info("Count: " + count);

      m_OutputToken = new Token(count);
    }

    return result;
  }
}
