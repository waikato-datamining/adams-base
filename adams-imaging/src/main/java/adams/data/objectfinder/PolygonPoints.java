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
 * PolygonPoints.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Returns the indices of objects which polygons satisfy specified min/max for number of points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonPoints
  extends AbstractObjectFinder {

  /** for serialization. */
  private static final long serialVersionUID = 235661615457187608L;

  /** the placeholder for NaN. */
  public final static String NAN = "NaN";

  /** the minimum value. */
  protected int m_Minimum;

  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;

  /** the maximum value. */
  protected int m_Maximum;

  /** whether the maximum value is included. */
  protected boolean m_MaximumIncluded;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of objects which polygons satisfy specified min/max for number of points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "minimum", "minimum",
      0, 0, null);

    m_OptionManager.add(
      "minimum-included", "minimumIncluded",
      false);

    m_OptionManager.add(
      "maximum", "maximum",
      Integer.MAX_VALUE, 0, null);

    m_OptionManager.add(
      "maximum-included", "maximumIncluded",
      false);
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(int value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public int getMinimum() {
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
      "The minimum value that the #points must satisfy; use " + NAN + " (not a "
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
    return "If enabled, then the minimum gets included (testing '<=' rather than '<').";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(int value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public int getMaximum() {
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
      "The maximum value that the #points must satisfy; use " + NAN + " (not a "
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
    return "If enabled, then the maximum gets included (testing '>=' rather than '>').";
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
    TIntList 		result;
    int 		points;
    boolean		add;

    result = new TIntArrayList();
    for (LocatedObject obj: objects) {
      if (obj.getMetaData() != null) {
        if (obj.hasPolygon()) {
          points = obj.getPolygonX().length;
          add   = true;
	  if (!Double.isNaN(m_Minimum)) {
	    if (m_MinimumIncluded) {
	      if (points < m_Minimum)
		add = false;
	    }
	    else {
	      if (points <= m_Minimum)
		add = false;
	    }
	  }
	  if (!Double.isNaN(m_Maximum)) {
	    if (m_MaximumIncluded) {
	      if (points > m_Maximum)
		add = false;
	    }
	    else {
	      if (points >= m_Maximum)
		add = false;
	    }
	  }
          if (isLoggingEnabled())
            getLogger().info(obj.getIndex() + ".  #points (" + points + ") fits: " + add);
          if (add)
            result.add(obj.getIndex());
        }
      }
    }

    return result.toArray();
  }
}
