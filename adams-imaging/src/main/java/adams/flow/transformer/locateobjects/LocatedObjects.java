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

/**
 * LocatedObjects.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for located objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocatedObjects
  extends ArrayList<LocatedObject> {

  /** for serialization. */
  private static final long serialVersionUID = 7784285445489902957L;
  
  /** the key for the X location. */
  public final static String KEY_X = ".x";

  /** the key for the Y location. */
  public final static String KEY_Y = ".y";

  /** the key for the width. */
  public final static String KEY_WIDTH = ".width";

  /** the key for the height. */
  public final static String KEY_HEIGHT = ".height";

  /** the key for the location. */
  public final static String KEY_LOCATION = ".location";

  /** the key for the overall count. */
  public final static String KEY_COUNT = "count";

  /** the key for the index of a group. */
  public final static String KEY_INDEX = "index";

  /**
   * Turns the located objects into a report.
   * Usinga prefix like "Object." will result in the following report entries
   * for a single object:
   * <pre>
   * Object.1.x
   * Object.1.y
   * Object.1.width
   * Object.1.height
   * </pre>
   * 
   * @param prefix	the prefix to use
   * @return		the generated report
   */
  public Report toReport(String prefix) {
    Report		result;
    int			count;
    Field		field;
    
    result = new Report();
    
    count = 0;
    for (LocatedObject obj: this) {
      count++;
      // x
      field = new Field(prefix + count + KEY_X, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getX());
      // y
      field = new Field(prefix + count + KEY_Y, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getY());
      // width
      field = new Field(prefix + count + KEY_WIDTH, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getWidth());
      // height
      field = new Field(prefix + count + KEY_HEIGHT, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getHeight());
      // location
      field = new Field(prefix + count + KEY_LOCATION, DataType.STRING);
      result.addField(field);
      result.setValue(field, obj.getLocation().getValue());
    }
    // count
    field = new Field(prefix + KEY_COUNT, DataType.NUMERIC);
    result.addField(field);
    result.setValue(field, size());

    return result;
  }

  /**
   * Retrieves all objects from the report.
   *
   * @param report	the report to process
   * @param prefix	the prefix to look for
   * @return		the objects found
   */
  public static LocatedObjects fromReport(Report report, String prefix) {
    LocatedObjects  			result;
    LocatedObject			obj;
    String				current;
    List<AbstractField> 		fields;
    Map<String,List<AbstractField>> 	groups;
    Map<String,Object>			meta;
    int					x;
    int					y;
    int					width;
    int					height;

    result = new LocatedObjects();
    fields = report.getFields();

    // group fields
    groups = new HashMap<>();
    for (AbstractField field: fields) {
      if (field.getName().startsWith(prefix)) {
	current = field.getName().substring(0, field.getName().lastIndexOf('.'));
	if (!groups.containsKey(current))
	  groups.put(current, new ArrayList<>());
	groups.get(current).add(field);
      }
    }

    // process grouped fields
    for (String group: groups.keySet()) {
      // meta-data
      meta = new HashMap<>();
      if (group.length() <= prefix.length())
	continue;
      meta.put(KEY_INDEX, group.substring(prefix.length()));
      for (AbstractField field: groups.get(group)) {
	if (field.getName().endsWith(KEY_X))
	  continue;
	if (field.getName().endsWith(KEY_Y))
	  continue;
	if (field.getName().endsWith(KEY_WIDTH))
	  continue;
	if (field.getName().endsWith(KEY_HEIGHT))
	  continue;
	meta.put(
	  field.getName().substring(field.getName().lastIndexOf('.') + 1),
	  report.getValue(field));
      }
      // location
      try {
	if ( report.hasValue(group + KEY_X)
	  && report.hasValue(group + KEY_Y)
	  && report.hasValue(group + KEY_WIDTH)
	  && report.hasValue(group + KEY_HEIGHT) ) {
	  x      = report.getDoubleValue(group + KEY_X).intValue();
	  y      = report.getDoubleValue(group + KEY_Y).intValue();
	  width  = report.getDoubleValue(group + KEY_WIDTH).intValue();
	  height = report.getDoubleValue(group + KEY_HEIGHT).intValue();
	  obj    = new LocatedObject(null, x, y, width, height, (meta.size() > 0) ? meta : null);
	  result.add(obj);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }
}
