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
 * ObjectsInPolygon.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.data.geometry.PolygonUtils;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Polygon;

/**
 <!-- globalinfo-start -->
 * Locates the objects that fall into the defined polygon.<br>
 * Partial hits can be considered as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-reset-indices-if-necessary &lt;boolean&gt; (property: resetIndicesIfNecessary)
 * &nbsp;&nbsp;&nbsp;If enabled, automatically resets the indices if some are missing or duplicates
 * &nbsp;&nbsp;&nbsp;are located.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, then no error is generated if -1 indices are returned.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-x &lt;java.lang.String&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X coordinates of the polygon (1-based).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-y &lt;java.lang.String&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y coordinates of the polygon (1-based).
 * &nbsp;&nbsp;&nbsp;default:
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectsInPolygon
  extends AbstractObjectFinder {

  private static final long serialVersionUID = -6804833277425617270L;

  /** the x coordinates. */
  protected int[] m_X;

  /** the y coordinates. */
  protected int[] m_Y;

  /** whether to include partial hits. */
  protected boolean m_Partial;

  /** whether report contains one-based coordinates. */
  protected boolean m_OneBased;

  /** whether to check the type (if a suffix provided). */
  protected boolean m_CheckType;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression to apply to the type. */
  protected BaseRegExp m_TypeFind;

  /** the polygon. */
  protected transient Polygon m_Polygon;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Locates the objects that fall into the defined polygon.\n"
	+ "Partial hits can be considered as well.";
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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Polygon = null;
  }

  /**
   * Sets the X coordinates (1-based) as comma-separated list.
   *
   * @param value 	the comma-separated list
   */
  public void setX(String value) {
    TIntList 	coords;

    coords = new TIntArrayList();
    if (!value.isEmpty()) {
      for (String part : value.split(","))
	coords.add(Integer.parseInt(part));
    }

    m_X = coords.toArray();
    reset();
  }

  /**
   * Returns the comma-separated list of X coordinates (1-based).
   *
   * @return 		the comma-separated list
   */
  public String getX() {
    return Utils.flatten(StatUtils.toNumberArray(m_X), ",");
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
    TIntList 	coords;

    coords = new TIntArrayList();
    if (!value.isEmpty()) {
      for (String part : value.split(","))
	coords.add(Integer.parseInt(part));
    }

    m_Y = coords.toArray();
    reset();
  }

  /**
   * Returns the comma-separated list of Y coordinates (1-based).
   *
   * @return 		the comma-separated list
   */
  public String getY() {
    return Utils.flatten(StatUtils.toNumberArray(m_Y), ",");
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
    result += QuickInfoHelper.toString(this, "partialCounts", m_Partial, "partial", ", ");
    if (m_CheckType) {
      result += QuickInfoHelper.toString(this, "typeSuffix", m_TypeSuffix.isEmpty() ? "-missing-" : m_TypeSuffix, ", type suffix: ");
      result += QuickInfoHelper.toString(this, "typeFind", m_TypeFind.isEmpty() ? "-missing-" : m_TypeFind, ", type find: ");
    }

    return result;
  }

  /**
   * Hook method for performing checks.
   *
   * @param objects  	the list of objects to check
   * @return		null if successful check, otherwise error message
   */
  @Override
  protected String check(LocatedObjects objects) {
    String	result;

    result = super.check(objects);

    if (m_Polygon == null) {
      if (m_X.length != m_Y.length)
	result = "Number of X and Y coordinates do not match: " + m_X.length + " != " + m_Y.length;
      else if (m_X.length < 3)
	result = "Polygon must consist of at least 3 points, provided #points: " + m_X.length;
      else
	m_Polygon = new Polygon(m_X, m_Y, m_X.length);
    }

    return result;
  }

  /**
   * Performs the actual finding of the objects in the list.
   *
   * @param objects  	the list of objects to process
   * @return		the indices
   */
  @Override
  protected int[] doFind(LocatedObjects objects) {
    TIntList		result;
    boolean		add;
    String		typeKey;
    int			inside;
    Polygon		inner;

    result = new TIntArrayList();

    if (m_TypeSuffix.startsWith("."))
      typeKey = m_TypeSuffix.substring(1);
    else
      typeKey = m_TypeSuffix;

    for (LocatedObject obj : objects) {
      if (isLoggingEnabled())
	getLogger().info("Object: " + obj);
      add = true;
      if (m_CheckType) {
	add = obj.getMetaData().containsKey(m_TypeSuffix) && m_TypeFind.isMatch(obj.getMetaData().get(typeKey).toString());
	if (isLoggingEnabled())
	  getLogger().info("Type check: " + add);
      }
      if (add) {
	if (obj.hasPolygon())
	  inner = obj.getPolygon();
	else
	  inner = PolygonUtils.toPolygon(obj.getRectangle());
	if (!m_OneBased)
	  inner = PolygonUtils.inc(inner, 1, 1);
	inside = PolygonUtils.inside(m_Polygon, inner);
	if (inside == 0)
	  add = false;
	if ((inside < inner.npoints) && !m_Partial)
	  add = false;
	if (isLoggingEnabled())
	  getLogger().info("Overlap check: " + add);
      }
      if (add)
	result.add(obj.getIndex());
    }

    return result.toArray();
  }
}
