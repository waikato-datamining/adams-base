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
 * ObjectsInRegion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 <!-- globalinfo-start -->
 * Locates the objects that fall into the defined region.<br>
 * Partial hits can be considered as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
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
 * <pre>-partial &lt;boolean&gt; (property: partial)
 * &nbsp;&nbsp;&nbsp;If enabled, partial hits are included as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-one-based &lt;boolean&gt; (property: oneBased)
 * &nbsp;&nbsp;&nbsp;If enabled, coordinates in the report are assumed to be 1-based instead
 * &nbsp;&nbsp;&nbsp;of 0-based.
 * &nbsp;&nbsp;&nbsp;default: false
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
public class ObjectsInRegion
  extends AbstractObjectFinder {

  private static final long serialVersionUID = -6804833277425617270L;

  /** the y of the top-left corner. */
  protected int m_Top;

  /** the x of the top-left corner. */
  protected int m_Left;

  /** the height of the window. */
  protected int m_Height;

  /** the width of the window. */
  protected int m_Width;

  /** whether to include partial counts. */
  protected boolean m_Partial;

  /** whether report contains one-based coordinates. */
  protected boolean m_OneBased;

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
      "Locates the objects that fall into the defined region.\n"
	+ "Partial hits can be considered as well.";
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
      "partial", "partial",
      false);

    m_OptionManager.add(
      "one-based", "oneBased",
      false);

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
  public void setPartial(boolean value) {
    m_Partial = value;
    reset();
  }

  /**
   * Returns whether to include partial hits.
   *
   * @return 		true if to count partial hits
   */
  public boolean getPartial() {
    return m_Partial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String partialTipText() {
    return "If enabled, partial hits are included as well.";
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
    result += QuickInfoHelper.toString(this, "partialCounts", m_Partial, "partial", ", ");
    if (m_CheckType) {
      result += QuickInfoHelper.toString(this, "typeSuffix", m_TypeSuffix.isEmpty() ? "-missing-" : m_TypeSuffix, ", type suffix: ");
      result += QuickInfoHelper.toString(this, "typeFind", m_TypeFind.isEmpty() ? "-missing-" : m_TypeFind, ", type find: ");
    }

    return result;
  }

  /**
   * Performs the actual finding of the objects in the report.
   *
   * @param report  	the report to process
   * @return		the indices
   */
  @Override
  protected int[] doFind(Report report) {
    TIntList		result;
    LocatedObjects 	objs;
    LocatedObject 	region;
    boolean		add;
    double		overlap;
    String		typeKey;

    result = new TIntArrayList();

    if (m_TypeSuffix.startsWith("."))
      typeKey = m_TypeSuffix.substring(1);
    else
      typeKey = m_TypeSuffix;
    region = new LocatedObject(null, m_Left - (m_OneBased ? 0 : 1), m_Top - (m_OneBased ? 0 : 1), m_Width, m_Height);
    if (isLoggingEnabled())
      getLogger().info("Region: " + region);
    objs = LocatedObjects.fromReport(report, m_Prefix);

    for (LocatedObject obj : objs) {
      if (isLoggingEnabled())
	getLogger().info("Object: " + obj);
      add = true;
      if (m_CheckType) {
	add = obj.getMetaData().containsKey(m_TypeSuffix) && m_TypeFind.isMatch(obj.getMetaData().get(typeKey).toString());
	if (isLoggingEnabled())
	  getLogger().info("Type check: " + add);
      }
      if (add) {
	overlap = obj.overlapRatio(region);
	if (isLoggingEnabled())
	  getLogger().info("Overlap: " + overlap);
	if (overlap == 1)
	  result.add(obj.getIndex());
	else if ((overlap < 1.0) && m_Partial)
	  result.add(obj.getIndex());
      }
    }

    return result.toArray();
  }
}
