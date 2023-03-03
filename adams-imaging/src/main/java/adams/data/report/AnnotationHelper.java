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
 * AnnotationHelper.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.report;

import adams.core.base.BaseRegExp;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.SelectionRectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods for dealing with annotations in reports.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AnnotationHelper {

  /** the key for the X location. */
  public final static String KEY_X = LocatedObjects.KEY_X;

  /** the key for the Y location. */
  public final static String KEY_Y = LocatedObjects.KEY_Y;

  /** the key for the width. */
  public final static String KEY_WIDTH = LocatedObjects.KEY_WIDTH;

  /** the key for the height. */
  public final static String KEY_HEIGHT = LocatedObjects.KEY_HEIGHT;

  /**
   * Removes the specified index from the report.
   *
   * @param report 	the report to process
   * @param prefix 	the object prefix to use
   * @return		true if successfully removed
   */
  public static boolean removeIndex(Report report, String prefix, int index) {
    boolean		result;
    BaseRegExp regexp;
    List<AbstractField> remove;

    result = false;
    regexp = new BaseRegExp(prefix + "[0]*" + index + "\\..*");
    remove = new ArrayList<>();
    for (AbstractField field: report.getFields()) {
      if (regexp.isMatch(field.getName()))
	remove.add(field);
    }
    if (remove.size() > 0) {
      result = true;
      for (AbstractField field: remove)
	report.removeValue(field);
    }

    return result;
  }

  /**
   * Determines the last index used with the given prefix.
   *
   * @param report 	the report to process
   * @param prefix 	the object prefix to use
   * @return		the last index
   */
  public static int findLastIndex(Report report, String prefix) {
    int			result;
    List<AbstractField>	fields;
    String		name;
    int			current;

    result = 0;
    fields = report.getFields();

    for (AbstractField field: fields) {
      if (field.getName().startsWith(prefix)) {
	name = field.getName().substring(prefix.length());
	if (name.indexOf('.') > -1)
	  name = name.substring(0, name.indexOf('.'));
	try {
	  current = Integer.parseInt(name);
	  if (current > result)
	    result = current;
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Returns all the values stored in the report under this index.
   *
   * @param report	the report to look up the index in
   * @param prefix 	the object prefix to use
   * @param index	the index to retrieve the values for
   * @return		the values
   */
  public static Map<String,Object> valuesForIndex(Report report, String prefix, int index) {
    Map<String,Object>  result;
    BaseRegExp		regexp;

    result = new HashMap<>();
    regexp = new BaseRegExp(prefix + "[0]*" + index + "\\..*");
    for (AbstractField field: report.getFields()) {
      if (regexp.isMatch(field.getName()))
	result.put(field.getName().replaceAll(regexp.getValue(), ""), report.getValue(field));
    }

    return result;
  }

  /**
   * Returns all currently stored locations.
   *
   * @param report	the report to get the locations from
   * @param prefix 	the object prefix to use
   * @return		the locations
   */
  public static List<SelectionRectangle> getLocations(Report report, String prefix) {
    List<SelectionRectangle>	result;
    List<AbstractField>		fields;
    String			name;
    SelectionRectangle		rect;

    result = new ArrayList<>();
    fields = report.getFields();

    for (AbstractField field: fields) {
      if (field.getName().startsWith(prefix)) {
	name = field.getName().substring(prefix.length());
	if (name.indexOf('.') > -1)
	  name = name.substring(0, name.indexOf('.'));
	try {
	  rect = new SelectionRectangle(
	    report.getDoubleValue(prefix + name + KEY_X).intValue(),
	    report.getDoubleValue(prefix + name + KEY_Y).intValue(),
	    report.getDoubleValue(prefix + name + KEY_WIDTH).intValue(),
	    report.getDoubleValue(prefix + name + KEY_HEIGHT).intValue(),
	    Integer.parseInt(name));
	  if (!result.contains(rect))
	    result.add(rect);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
}
