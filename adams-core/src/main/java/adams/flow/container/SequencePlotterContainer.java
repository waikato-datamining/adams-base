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
 * SequencePlotterContainer.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A container for a single plot item. Can be used to name plots.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotterContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 7610036927945350224L;

  /**
   * The type of content in the container.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ContentType {
    /** plot data. */
    PLOT,
    /** marker. */
    MARKER,
    /** overlay. */
    OVERLAY,
    /** whether to force an update. */
    UPDATE
  }
  
  /** the identifier for the PlotName. */
  public final static String VALUE_PLOTNAME = "PlotName";

  /** the identifier for the X value. */
  public final static String VALUE_X = "X";

  /** the identifier for the Y value. */
  public final static String VALUE_Y = "Y";

  /** the identifier for the "content type" value. */
  public final static String VALUE_CONTENTTYPE = "Content type";

  /** the default plot name. */
  public static final String DEFAULT_PLOTNAME = "Plot";

  /** the identifier for the X error value. */
  public final static String VALUE_ERROR_X = "Error X";

  /** the identifier for the Y error value. */
  public final static String VALUE_ERROR_Y = "Error Y";

  /** the identifier for the meta-data. */
  public final static String VALUE_METADATA = "MetaData";

  /**
   * Initializes the container with the default plot name and no X value and Y
   * value of 0.0.
   * <br><br>
   * Only used for generating help information.
   */
  public SequencePlotterContainer() {
    this(DEFAULT_PLOTNAME, 0.0);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param plotName	the name of the plot
   * @param y		the y value of the plot
   */
  public SequencePlotterContainer(String plotName, Comparable y) {
    this(plotName, y, ContentType.PLOT);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param plotName	the name of the plot
   * @param y		the y value of the plot
   * @param type	what this container represents
   */
  public SequencePlotterContainer(String plotName, Comparable y, ContentType type) {
    this(plotName, null, y, type);
  }

  /**
   * Initializes the container with the default plot name.
   *
   * @param x		the x value of the plot
   * @param y		the y value of the plot
   * @see		#DEFAULT_PLOTNAME
   */
  public SequencePlotterContainer(Comparable x, Comparable y) {
    this(DEFAULT_PLOTNAME, x, y);
  }

  /**
   * Initializes the container with the default plot name.
   *
   * @param plotName	the name of the plot
   * @param x		the x value of the plot
   * @param y		the y value of the plot
   */
  public SequencePlotterContainer(String plotName, Comparable x, Comparable y) {
    this(plotName, x, y, ContentType.PLOT);
  }

  /**
   * Initializes the container with the default plot name.
   *
   * @param plotName	the name of the plot
   * @param x		the x value of the plot
   * @param y		the y value of the plot
   * @param type	what this container represents
   */
  public SequencePlotterContainer(String plotName, Comparable x, Comparable y, ContentType type) {
    this(plotName, x, y, null, null, type);
  }

  /**
   * Initializes the container with the default plot name.
   *
   * @param plotName	the name of the plot
   * @param x		the x value of the plot
   * @param y		the y value of the plot
   * @param errorX	the error range for X
   * @param errorY	the error range for Y
   * @param type	what this container represents
   */
  public SequencePlotterContainer(String plotName, Comparable x, Comparable y, Double[] errorX, Double[] errorY, ContentType type) {
    super();

    store(VALUE_PLOTNAME, plotName);
    store(VALUE_X, x);
    store(VALUE_Y, y);
    store(VALUE_CONTENTTYPE, type);
    store(VALUE_ERROR_X, errorX);
    store(VALUE_ERROR_Y, errorY);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_PLOTNAME);
    result.add(VALUE_X);
    result.add(VALUE_Y);
    result.add(VALUE_CONTENTTYPE);
    result.add(VALUE_ERROR_X);
    result.add(VALUE_ERROR_Y);
    result.add(VALUE_METADATA);

    return result.iterator();
  }

  /**
   * Adds the meta-data key-value relation. Ignores null values.
   * 
   * @param key		the key of the meta-data value
   * @param value	the associated value
   * @return		any previously stored value, null if none replaced
   */
  public Object addMetaData(String key, Object value) {
    HashMap<String,Object>	meta;
    
    if (value == null)
      return null;
    
    if (!hasValue(VALUE_METADATA))
      setValue(VALUE_METADATA, new HashMap<String,Object>());
    
    meta = (HashMap<String,Object>) getValue(VALUE_METADATA);
    return meta.put(key, value);
  }
  
  /**
   * Checks whether any meta-data is stored.
   * 
   * @return		true if any meta-data stored
   */
  public boolean hasMetaData() {
    return hasValue(VALUE_METADATA) && (((HashMap<String,Object>) getValue(VALUE_METADATA)).size() > 0);
  }
  
  /**
   * Returns the meta-data, if any.
   * 
   * @return		the meta-data, null if none stored
   */
  public HashMap<String,Object> getMetaData() {
    return (HashMap<String,Object>) getValue(VALUE_METADATA);
  }
  
  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return   (hasValue(VALUE_PLOTNAME) && hasValue(VALUE_Y) && !hasValue(VALUE_Y) && hasValue(VALUE_CONTENTTYPE))
           | (hasValue(VALUE_PLOTNAME) && hasValue(VALUE_X) &&  hasValue(VALUE_Y) && hasValue(VALUE_CONTENTTYPE));
  }
}
