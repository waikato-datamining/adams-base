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
 * PolygonVertexLengths.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Calculates the lengths of the polygon's vertices and stores them in
 * the object's meta-data as comma-separated list.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PolygonVertexLengths
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -3865626476883847402L;

  /** the meta-data key to store the lengths as comma-separated list. */
  protected String m_Key;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the lengths of the polygon's vertices and stores them in "
	     + "the object's meta-data as comma-separated list.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "vertices");
  }

  /**
   * Sets the meta-data key for the vertices.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the meta-data key for the vertices.
   *
   * @return		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The meta-data to store the lengths of the vertices in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "key", m_Key, "key: ");
  }

  /**
   * Calculates the distance between the two points.
   *
   * @param x0		the x of the 1st point
   * @param y0		the y of the 1st point
   * @param x1		the x of the 2nd point
   * @param y1		the y of the 2nd point
   * @return		the distance
   */
  protected double dist(int x0, int y0, int x1, int y1) {
    int		x;
    int		y;

    x = (x0 < x1) ? (x1 - x0) : (x0 - x1);
    y = (y0 < y1) ? (y1 - y0) : (y0 - y1);
    if (x == 0)
      return y;
    if (y == 0)
      return x;

    return Math.sqrt(x*x + y*y);
  }

  /**
   * Filters the image objects.
   *
   * @param objects the objects to filter
   * @return the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    int			i;
    int[]		x;
    int[]		y;
    TDoubleList 	lengths;

    result  = new LocatedObjects();
    lengths = new TDoubleArrayList();

    for (LocatedObject obj: objects) {
      obj = obj.getClone();
      if (obj.hasPolygon()) {
	lengths.clear();
	x = obj.getPolygonX();
	y = obj.getPolygonY();
	for (i = 0; i < x.length - 1; i++)
	  lengths.add(dist(x[i], y[i], x[i+1], y[i+1]));
	lengths.add(dist(x[x.length-1], y[y.length-1], x[0], y[0]));
	// add lengths
	obj.getMetaData().put(m_Key, Utils.flatten(StatUtils.toNumberArray(lengths.toArray()), ","));
      }
      result.add(obj);
    }

    return result;
  }
}
