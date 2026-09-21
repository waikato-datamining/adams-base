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
 * SelfIntersectingPolygons.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfilter;

import adams.data.geometry.GeometryUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Looks for self-intersection polygons and applies the selected action.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelfIntersectingPolygons
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -372897277406310067L;

  /**
   * The action to perform.
   */
  public enum Action {
    FLAG,
    FLAG_ALL,
    REMOVE,
  }

  /** the action to perform. */
  protected Action m_Action;

  /** the boolean label to use in the object's meta-data when flagging them. */
  protected String m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Looks for self-intersection polygons and applies the selected action.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "action",
      Action.FLAG);

    m_OptionManager.add(
      "field", "field",
      "self-intersects");
  }

  /**
   * Sets the action to perform.
   *
   * @param value	the action
   */
  public void setAction(Action value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to perform.
   *
   * @return		the action
   */
  public Action getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The action to perform; in case of " + Action.FLAG_ALL + ", a boolean field is added to all objects with polygons, using " + Action.FLAG + " only when self-intersection detected.";
  }

  /**
   * Sets the field to store the self-intersect state in.
   *
   * @param value	the field
   */
  public void setField(String value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to store the self-intersect state in.
   *
   * @return		the field
   */
  public String getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The boolean field in the object's meta-data to store the self-intersection state (action: " + Action.FLAG + ").";
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
    boolean		intersects;

    result = new LocatedObjects();

    for (LocatedObject obj: objects) {
      obj = obj.getClone();

      if (!obj.hasPolygon()) {
	result.add(obj);
	continue;
      }

      intersects = GeometryUtils.selfIntersects(obj.getPolygon());
      switch (m_Action) {
	case FLAG:
	  if (intersects)
	    obj.getMetaData().put(m_Field, true);
	  else
	    obj.getMetaData().remove(m_Field);
	  result.add(obj);
	  break;
	case FLAG_ALL:
	  obj.getMetaData().put(m_Field, intersects);
	  result.add(obj);
	  break;
	case REMOVE:
	  if (!intersects)
	    result.add(obj);
	  break;
	default:
	  throw new IllegalStateException("Unhandled action: " + m_Action);
      }
    }

    return result;
  }
}
