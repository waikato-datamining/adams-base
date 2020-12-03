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
 * LocatedObjects.java
 * Copyright (C) 2014-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.CloneHandler;
import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for located objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocatedObjects
  extends ArrayList<LocatedObject>
  implements CloneHandler<LocatedObjects> {

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

  /** the key for the Xs of the polygon. */
  public final static String KEY_POLY_X = "." + LocatedObject.KEY_POLY_X;

  /** the key for the Ys of the polygon. */
  public final static String KEY_POLY_Y = "." + LocatedObject.KEY_POLY_Y;

  /** the key for the overall count. */
  public final static String KEY_COUNT = "count";

  /** the key for the index of a group. */
  public final static String KEY_INDEX = "index";

  /** the logger in use. */
  protected Logger m_Logger;

  /**
   * Initializes the list.
   */
  public LocatedObjects() {
    super();
  }

  /**
   * Initializes the list.
   *
   * @param objects	the list to initialize with
   */
  public LocatedObjects(LocatedObjects objects) {
    super(objects);
  }

  /**
   * Initializes the list.
   *
   * @param objects	the list to initialize with
   */
  public LocatedObjects(List<LocatedObject> objects) {
    super(objects);
  }

  /**
   * Initializes the list.
   *
   * @param objects	the array to initialize with
   */
  public LocatedObjects(LocatedObject[] objects) {
    super(Arrays.asList(objects));
  }

  /**
   * Initializes the list.
   *
   * @param object	the object to initialize with
   */
  public LocatedObjects(LocatedObject object) {
    super();
    add(object);
  }

  /**
   * Returns the logger instance.
   *
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      m_Logger = LoggingHelper.getLogger(getClass());
    return m_Logger;
  }

  /**
   * Returns a new instance using the specified object indices.
   *
   * @param indices	the indices for the subset
   * @return		the subset
   */
  public LocatedObjects subset(int[] indices) {
    return subset(indices, false);
  }

  /**
   * Returns a new instance using the specified object indices.
   *
   * @param indices	the indices for the subset
   * @param invert 	whether to invert the matching of the indices
   * @return		the subset
   */
  public LocatedObjects subset(int[] indices, boolean invert) {
    LocatedObjects 	result;
    int			index;
    TIntSet 		hash;

    result = new LocatedObjects();
    hash    = new TIntHashSet(indices);
    for (LocatedObject obj: this) {
      if (obj.getMetaData() != null) {
	index = obj.getIndex();
	if (invert) {
	  if (!hash.contains(index))
	    result.add(obj);
	}
	else {
	  if (hash.contains(index))
	    result.add(obj);
	}
      }
      else {
	getLogger().warning("Object has no meta-data: " + obj);
      }
    }

    return result;
  }

  /**
   * Returns a new instance using the specified list indices.
   *
   * @param indices	the list indices for the subset
   * @return		the subset
   */
  public LocatedObjects subList(int[] indices) {
    return subList(indices, false);
  }

  /**
   * Returns a new instance using the specified list indices.
   *
   * @param indices	the list indices for the subset
   * @param invert 	whether to invert the matching of the indices
   * @return		the subset
   */
  public LocatedObjects subList(int[] indices, boolean invert) {
    LocatedObjects	result;
    TIntSet		set;
    int			i;

    result = new LocatedObjects();
    set    = new TIntHashSet(indices);
    for (i = 0; i < size(); i++) {
      if (invert) {
        if (!set.contains(i))
          result.add(get(i));
      }
      else {
        if (set.contains(i))
          result.add(get(i));
      }
    }

    return result;
  }

  /**
   * Removes the objects with the specified indices.
   *
   * @param indices	the indices to remove (ie the indices stored with the object!)
   */
  public void remove(int[] indices) {
    int		i;
    TIntSet	set;

    set = new TIntHashSet(indices);
    i   = 0;
    while (i < size()) {
      if (set.contains(get(i).getIndex()))
        remove(i);
      else
        i++;
    }
  }

  /**
   * Returns the object with the specified index.
   *
   * @param index	the index to look for
   * @return		the object, null if none found
   */
  public LocatedObject find(int index) {
    return find("" + index);
  }

  /**
   * Returns the object with the specified index.
   *
   * @param index	the index to look for
   * @return		the object, null if none found
   */
  public LocatedObject find(String index) {
    LocatedObject	result;
    Integer		intIndex;
    String 		objIndexStr;
    int 		objIndex;

    result = null;

    try {
      intIndex = Integer.parseInt(index);
    }
    catch (Exception e) {
      intIndex = null;
    }

    for (LocatedObject obj: this) {
      // exact match
      objIndexStr = obj.getIndexString();
      if (objIndexStr != null) {
	if (objIndexStr.equals(index)) {
	  result = obj;
	  break;
	}
      }
      // numeric match
      if (intIndex != null) {
	objIndex = obj.getIndex();
	if (objIndex != -1) {
	  if (objIndex == intIndex) {
	    result = obj;
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Scales all objects with the provided scale factor.
   *
   * @param scale	the scale factor
   */
  public void scale(double scale) {
    int		i;

    for (i = 0; i < size(); i++)
      get(i).scale(scale);
  }

  /**
   * Renames the meta-data key in all objects.
   *
   * @param oldKey	the old key
   * @param newKey	the new key
   * @return		how many keys were updated
   */
  public int renameMetaDataKey(String oldKey, String newKey) {
    int		result;
    int		i;

    result = 0;

    for (i = 0; i < size(); i++) {
      if (get(i).renameMetaDataKey(oldKey, newKey))
        result++;
    }

    return result;
  }

  /**
   * Resets the index value of all the objects (starts at 1) in the meta-data
   * using the current order in the list.
   */
  public void resetIndex() {
    resetIndex(0);
  }

  /**
   * Resets the index value of all the objects (starts at 1) in the meta-data
   * using the current order in the list.
   *
   * @param offset 	the offset to add to the index
   */
  public void resetIndex(int offset) {
    int		i;

    for (i = 0; i < size(); i++)
      get(i).getMetaData().put(LocatedObjects.KEY_INDEX, (i+1+offset));
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public LocatedObjects getClone() {
    LocatedObjects	result;

    result = new LocatedObjects();
    for (LocatedObject obj: this)
      result.add(obj.getClone());

    return result;
  }

  /**
   * Turns the located objects into a report.
   * Using a prefix like "Object." will result in the following report entries
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
    return toReport(prefix, 0);
  }

  /**
   * Turns the located objects into a report. Does not update the index in the meta-data.
   * Using a prefix like "Object." will result in the following report entries
   * for a single object:
   * <pre>
   * Object.1.x
   * Object.1.y
   * Object.1.width
   * Object.1.height
   * Object.1.poly_x -- if polygon data present
   * Object.1.poly_y -- if polygon data present
   * </pre>
   *
   * @param prefix	the prefix to use
   * @param offset	the offset for the index to use
   * @return		the generated report
   */
  public Report toReport(String prefix, int offset) {
    return toReport(prefix, offset, false);
  }

  /**
   * Turns the located objects into a report.
   * Using a prefix like "Object." will result in the following report entries
   * for a single object:
   * <pre>
   * Object.1.x
   * Object.1.y
   * Object.1.width
   * Object.1.height
   * Object.1.poly_x -- if polygon data present
   * Object.1.poly_y -- if polygon data present
   * </pre>
   *
   * @param prefix	the prefix to use
   * @param offset	the offset for the index to use
   * @param updateIndex	whether to update the index in the meta-data
   * @return		the generated report
   */
  public Report toReport(String prefix, int offset, boolean updateIndex) {
    Report		result;
    int			count;
    String		countStr;
    Field		field;
    int			width;
    DataType		type;
    Object		value;

    result = new Report();

    count = 0;
    width = ("" + size()).length();
    for (LocatedObject obj: this) {
      count++;
      countStr = Utils.padLeft("" + (count + offset), '0', width);
      // meta-data
      for (String key: obj.getMetaData().keySet()) {
	value = obj.getMetaData().get(key);
	if (value instanceof Double)
	  type = DataType.NUMERIC;
	else if (value instanceof Boolean)
	  type = DataType.BOOLEAN;
	else if (value instanceof String)
	  type = DataType.STRING;
	else
	  type = DataType.UNKNOWN;
	field = new Field(prefix + countStr + "." + key, type);
	result.addField(field);
	result.setValue(field, value);
      }
      // index
      if (updateIndex) {
	field = new Field(prefix + countStr + "." + KEY_INDEX, DataType.STRING);
	result.addField(field);
	result.setValue(field, countStr);
      }
      // x
      field = new Field(prefix + countStr + KEY_X, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getX());
      // y
      field = new Field(prefix + countStr + KEY_Y, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getY());
      // width
      field = new Field(prefix + countStr + KEY_WIDTH, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getWidth());
      // height
      field = new Field(prefix + countStr + KEY_HEIGHT, DataType.NUMERIC);
      result.addField(field);
      result.setValue(field, obj.getHeight());
      // location
      field = new Field(prefix + countStr + KEY_LOCATION, DataType.STRING);
      result.addField(field);
      result.setValue(field, obj.getLocation().getValue());
      // polygon
      if (obj.hasPolygon()) {
        // poly_x
	field = new Field(prefix + countStr + KEY_POLY_X, DataType.STRING);
	result.addField(field);
	result.setValue(field, Utils.flatten(StatUtils.toNumberArray(obj.getPolygonX()), ","));
	// poly_y
	field = new Field(prefix + countStr + KEY_POLY_Y, DataType.STRING);
	result.addField(field);
	result.setValue(field, Utils.flatten(StatUtils.toNumberArray(obj.getPolygonY()), ","));
      }
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
    return fromReport(report, new String[]{prefix});
  }

  /**
   * Retrieves all objects from the report.
   *
   * @param report	the report to process
   * @param prefixes	the prefixes to look for
   * @return		the objects found
   */
  public static LocatedObjects fromReport(Report report, String[] prefixes) {
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
    boolean				match;
    String				actPrefix;

    result = new LocatedObjects();
    fields = report.getFields();

    // group fields
    groups = new HashMap<>();
    for (AbstractField field: fields) {
      match = false;
      for (String prefix: prefixes) {
        if (field.getName().startsWith(prefix)) {
          match = true;
          break;
	}
      }
      if (match) {
	current = field.getName().substring(0, field.getName().lastIndexOf('.'));
	if (!groups.containsKey(current))
	  groups.put(current, new ArrayList<>());
	groups.get(current).add(field);
      }
    }

    // process grouped fields
    for (String group: groups.keySet()) {
      actPrefix = null;
      for (String prefix: prefixes) {
        if (group.startsWith(prefix)) {
          actPrefix = prefix;
          break;
	}
      }
      if (actPrefix == null)
        continue;
      // meta-data
      meta = new HashMap<>();
      if (group.length() <= actPrefix.length())
	continue;
      meta.put(KEY_INDEX, group.substring(actPrefix.length()));
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
	  // polygon
	  if ( report.hasValue(group + KEY_POLY_X)
	    && report.hasValue(group + KEY_POLY_Y)) {
	    obj.getMetaData().put(LocatedObject.KEY_POLY_X, report.getStringValue(group + KEY_POLY_X));
	    obj.getMetaData().put(LocatedObject.KEY_POLY_Y, report.getStringValue(group + KEY_POLY_Y));
	  }
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }
}
