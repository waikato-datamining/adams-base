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
 * Size.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Applies the minimum/maximum constraints to width/height (according to the chosen type).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Size
  extends AbstractObjectFinder {

  private static final long serialVersionUID = -2324944380405209657L;

  /** the placeholder for NaN. */
  public final static String NAN = "NaN";

  /** how to apply the checks. */
  public enum CheckType {
    WIDTH,
    HEIGHT,
    WIDTH_AND_HEIGHT,
  }

  /** the minimum value. */
  protected double m_Minimum;

  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;

  /** the maximum value. */
  protected double m_Maximum;

  /** whether the maximum value is included. */
  protected boolean m_MaximumIncluded;

  /** what to apply the constraints to. */
  protected CheckType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the minimum/maximum constraints to width/height (according to the chosen type).\n"
	+ "Constraints get ignored if " + NAN + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"minimum", "minimum",
	Double.NaN);

    m_OptionManager.add(
	"minimum-included", "minimumIncluded",
	false);

    m_OptionManager.add(
	"maximum", "maximum",
	Double.NaN);

    m_OptionManager.add(
	"maximum-included", "maximumIncluded",
	false);

    m_OptionManager.add(
	"type", "type",
	CheckType.WIDTH_AND_HEIGHT);
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumTipText() {
    return
	"The minimum value that the values must satisfy; use " + NAN + " (not a "
	    + "number) to ignore minimum.";
  }

  /**
   * Sets whether to exclude the minimum.
   *
   * @param value	true to exclude minimum
   */
  public void setMinimumIncluded(boolean value) {
    m_MinimumIncluded = value;
    reset();
  }

  /**
   * Returns whether the minimum is included.
   *
   * @return		true if minimum included
   */
  public boolean getMinimumIncluded() {
    return m_MinimumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumIncludedTipText() {
    return "If enabled, then the minimum value gets included (testing '<=' rather than '<').";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumTipText() {
    return
	"The maximum value that the values must satisfy; use " + NAN + " (not a "
	    + "number) to ignore maximum.";
  }

  /**
   * Sets whether to exclude the maximum.
   *
   * @param value	true to exclude maximum
   */
  public void setMaximumIncluded(boolean value) {
    m_MaximumIncluded = value;
    reset();
  }

  /**
   * Returns whether the maximum is included.
   *
   * @return		true if maximum included
   */
  public boolean getMaximumIncluded() {
    return m_MaximumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumIncludedTipText() {
    return "If enabled, then the maximum value gets included (testing '>=' rather than '>').";
  }

  /**
   * Sets what to apply the constraints to.
   *
   * @param value	the type
   */
  public void setType(CheckType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns what to apply the constraints to.
   *
   * @return		the type
   */
  public CheckType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return"Determines what to apply the constraints to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "minimum", m_Minimum, ", min: ");
    result += " [" + QuickInfoHelper.toString(this, "minimumIncluded", (m_MinimumIncluded ? "incl" : "excl")) + "]";
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum, ", max: ");
    result += " [" + QuickInfoHelper.toString(this, "maximumIncluded", (m_MaximumIncluded ? "incl" : "excl")) + "]";
    result += QuickInfoHelper.toString(this, "type", m_Type, ", apply to: ");

    return result;
  }

  /**
   * Checks whether the value fits the constraints.
   *
   * @param value	the value to check
   * @return		falls within constraints
   */
  protected boolean keep(int value) {
    boolean result;

    result = true;

    if (!Double.isNaN(m_Minimum)) {
      if (m_MinimumIncluded) {
	if (value < m_Minimum)
	  result = false;
      }
      else {
	if (value <= m_Minimum)
	  result = false;
      }
    }
    if (!Double.isNaN(m_Maximum)) {
      if (m_MaximumIncluded) {
	if (value > m_Maximum)
	  result = false;
      }
      else {
	if (value >= m_Maximum)
	  result = false;
      }
    }

    return result;
  }

  /**
   * Performs the actual finding of the objects in the list.
   *
   * @param objects the list of objects to process
   * @return the indices
   */
  @Override
  protected int[] doFind(LocatedObjects objects) {
    TIntList 	result;
    boolean	add;

    result = new TIntArrayList();

    for (LocatedObject obj: objects) {
      switch (m_Type) {
	case WIDTH:
	  add = keep(obj.getWidth());
	  break;
	case HEIGHT:
	  add = keep(obj.getHeight());
	  break;
	case WIDTH_AND_HEIGHT:
	  add = keep(obj.getWidth()) && keep(obj.getHeight());
	  break;
	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }
      if (isLoggingEnabled())
	getLogger().info(obj.getIndex() + ". width=" + obj.getWidth() + ", height=" + obj.getHeight() + " fits: " + add);
      if (add)
	result.add(obj.getIndex());
    }

    return result.toArray();
  }
}
