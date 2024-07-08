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
 * XYSequence.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sequence;

import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainerWithSpreadSheetSupport;
import adams.data.container.DataPointComparator;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TDoubleObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A sequence storing 2-dimensional points.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class XYSequence
  extends AbstractDataContainer<XYSequencePoint> 
  implements DataContainerWithSpreadSheetSupport<XYSequencePoint> {

  /** for serialization. */
  private static final long serialVersionUID = -3742507986126404535L;

  /** the file extension. */
  public final static String FILE_EXTENSION = ".csv";

  /** the default comparator. */
  protected DataPointComparator m_Comparator;

  /** the comparison to use. */
  protected Comparison m_Comparison;

  /** the minimum X point. */
  protected XYSequencePoint m_MinX;

  /** the maximum X point. */
  protected XYSequencePoint m_MaxX;

  /** the minimum Y point. */
  protected XYSequencePoint m_MinY;

  /** the maximum Y point. */
  protected XYSequencePoint m_MaxY;
  
  /** string representations for the X values. */
  protected TDoubleObjectHashMap<String> m_MappingX;
  
  /** string representations for the Y values. */
  protected TDoubleObjectHashMap<String> m_MappingY;
  
  /**
   * Initializes the sequence.
   */
  public XYSequence() {
    super();

    m_MappingX = null;
    m_MappingY = null;
    invalidateMinMax();
    setComparison(Comparison.X);
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  public void assign(XYSequence other) {
    super.assign(other);

    if (other.m_MappingX != null)
      m_MappingX = new TDoubleObjectHashMap<>(other.m_MappingX);
    if (other.m_MappingY != null)
      m_MappingY = new TDoubleObjectHashMap<>(other.m_MappingY);
  }

  /**
   * Checks whether there are string mappings for the X values available.
   * 
   * @return		true if X mappings available
   */
  public boolean hasMappingX() {
    return (m_MappingX != null);
  }

  /**
   * Adds a X mapping for the given string.
   * 
   * @param s		the string to add the mapping for
   * @return		the generated mapping
   */
  public double putMappingX(String s) {
    double	result;
    
    if (m_MappingX == null)
      m_MappingX = new TDoubleObjectHashMap<>();
    
    result = m_MappingX.size() + 1;
    m_MappingX.put(result, s);
    
    return result;
  }

  /**
   * Removes a X mapping for the given value.
   * 
   * @param x		the mapping to remove
   * @return		the previous mapping string, if any
   */
  public String removeMappingX(double x) {
    if (m_MappingX == null)
      return null;
    else
      return m_MappingX.remove(x);
  }
  
  /**
   * Returns the X mapping associated with the key.
   * 
   * @param key		the mapping to look up
   * @return		the mapping
   */
  public String getMappingX(double key) {
    return m_MappingX.get(key);
  }

  /**
   * Returns all the labels for X.
   * 
   * @return		the labels
   */
  public List<String> getLabelsX() {
    List<String>	result;
    TDoubleArrayList 	index;
    int			i;
    
    result = new ArrayList<>();
    if (m_MappingX == null)
      return result;
    
    index  = new TDoubleArrayList(m_MappingX.keys());
    index.sort();
    
    for (i = 0; i < index.size(); i++)
      result.add(m_MappingX.get(index.get(i)));
    
    return result;
  }

  /**
   * Returns all the mappings for X.
   * 
   * @return		the mappings
   */
  public TDoubleObjectHashMap<String> getMappingsX() {
    return m_MappingX;
  }
  
  /**
   * Checks whether there are string mappings for the Y values available.
   * 
   * @return		true if Y mappings available
   */
  public boolean hasMappingY() {
    return (m_MappingY != null);
  }

  /**
   * Adds a Y mapping for the given string.
   * 
   * @param s		the string to add the mapping for
   * @return		the generated mapping
   */
  public double putMappingY(String s) {
    double	result;
    
    if (m_MappingY == null)
      m_MappingY = new TDoubleObjectHashMap<>();

    result = m_MappingY.size() + 1;
    m_MappingY.put(result, s);
    
    return result;
  }

  /**
   * Removes a Y mapping for the given value.
   * 
   * @param y		the mapping to remove
   * @return		the previous mapping string, if any
   */
  public String removeMappingY(double y) {
    if (m_MappingY == null)
      return null;
    else
      return m_MappingY.remove(y);
  }

  /**
   * Returns the Y mapping associated with the key.
   * 
   * @param key		the mapping to look up
   * @return		the mapping
   */
  public String getMappingY(double key) {
    return m_MappingY.get(key);
  }

  /**
   * Returns all the labels for Y.
   * 
   * @return		the labels
   */
  public List<String> getLabelsY() {
    List<String>	result;
    TDoubleArrayList 	index;
    int			i;
    
    result = new ArrayList<String>();
    if (m_MappingY == null)
      return result;
    
    index  = new TDoubleArrayList(m_MappingY.keys());
    index.sort();
    
    for (i = 0; i < index.size(); i++)
      result.add(m_MappingY.get(index.get(i)));
    
    return result;
  }

  /**
   * Returns all the mappings for Y.
   * 
   * @return		the mappings
   */
  public TDoubleObjectHashMap<String> getMappingsY() {
    return m_MappingY;
  }

  /**
   * Sets the type of comparison to use.
   *
   * @param value	the type of comparison to use
   */
  public void setComparison(Comparison value) {
    m_Comparison = value;
    m_Comparator = newComparator();
  }

  /**
   * Returns the type of comparison currently in use.
   *
   * @return		the type of comparison
   */
  public Comparison getComparison() {
    return m_Comparison;
  }
  
  /**
   * Returns the comparator in use.
   *
   * @return		the comparator to use
   */
  public DataPointComparator<XYSequencePoint> newComparator() {
    return new XYSequencePointComparator(m_Comparison, true);
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  public DataPointComparator<XYSequencePoint> getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a sequence point.
   *
   * @return		the new sequence point
   */
  public XYSequencePoint newPoint() {
    return new XYSequencePoint();
  }

  /**
   * Method that gets notified about changes in the collection of data points.
   * Invalidates the min/max and passes the modified state through.
   *
   * @param modified	whether the action modified the collection
   * @return		the same as the input
   */
  @Override
  protected boolean modifiedListener(boolean modified) {
    invalidateMinMax();
    return modified;
  }

  /**
   * Invalidates the min/max points.
   */
  protected void invalidateMinMax() {
    m_MinX = null;
    m_MaxX = null;
    m_MinY = null;
    m_MaxY = null;
  }
  
  /**
   * Determines the min/max points.
   */
  protected synchronized void validateMinMax() {
    if (m_MinX != null)
      return;
    
    for (XYSequencePoint point: this) {
      if ((m_MinX == null) || (point.getMinX() < m_MinX.getMinX()))
	m_MinX = point;
      if ((m_MaxX == null) || (point.getMaxX() > m_MaxX.getMaxX()))
	m_MaxX = point;
      if ((m_MinY == null) || (point.getMinY() < m_MinY.getMinY()))
	m_MinY = point;
      if ((m_MaxY == null) || (point.getMaxY() > m_MaxY.getMaxY()))
	m_MaxY = point;
    }
  }

  /**
   * Returns point with the minimum X.
   * 
   * @return		the point
   */
  public XYSequencePoint getMinX() {
    validateMinMax();
    return m_MinX;
  }

  /**
   * Returns point with the maximum X.
   * 
   * @return		the point
   */
  public XYSequencePoint getMaxX() {
    validateMinMax();
    return m_MaxX;
  }

  /**
   * Returns point with the minimum Y.
   * 
   * @return		the point
   */
  public XYSequencePoint getMinY() {
    validateMinMax();
    return m_MinY;
  }

  /**
   * Returns point with the maximum Y.
   * 
   * @return		the point
   */
  public XYSequencePoint getMaxY() {
    validateMinMax();
    return m_MaxY;
  }
  
  /**
   * Returns the SequencePoint with the exact number, null if not found.
   *
   * @param x		the number to look for
   * @return		the SequencePoint or null if not found
   */
  public XYSequencePoint find(double x) {
    XYSequencePoint	result;
    int				index;
    ArrayList<XYSequencePoint>	points;

    result = null;

    points = new ArrayList<>(m_Points);  // TODO: inefficient
    index  = XYSequenceUtils.findX(points, x);
    if (index > -1)
      result = m_Points.get(index);

    return result;
  }

  /**
   * Returns the SequencePoint with a number closest to the one provided.
   *
   * @param x		the number to look for
   * @return		the SpectrumPoint
   */
  public XYSequencePoint findClosest(double x) {
    XYSequencePoint	result;
    int				index;
    ArrayList<XYSequencePoint>	points;

    result = null;

    points = new ArrayList<>(m_Points);  // TODO: inefficient
    index  = XYSequenceUtils.findClosestX(points, x);
    if (index > -1)
      result = m_Points.get(index);

    return result;
  }

  /**
   * Returns the list of points as spreadsheet.
   *
   * @param points 	the points to convert
   * @return		the content
   */
  public SpreadSheet toSpreadSheet(List<XYSequencePoint> points) {
    Iterator<XYSequencePoint>	iter;
    SpreadSheet			result;
    Row				row;
    XYSequencePoint		point;
    XYSequencePointWithErrors 	epoint;
    Double[]			error;
    boolean			errorXLow;
    boolean			errorXHigh;
    boolean			errorYLow;
    boolean			errorYHigh;
    HashMap<String,Object> 	meta;

    errorXLow  = false;
    errorXHigh = false;
    errorYLow  = false;
    errorYHigh = false;
    iter = points.iterator();
    while (iter.hasNext()) {
      point = iter.next();
      if (point instanceof XYSequencePointWithErrors) {
	epoint = (XYSequencePointWithErrors) point;
	if (!errorXLow && epoint.hasErrorX()) {
	  errorXLow = true;
	  error     = epoint.getErrorX();
	  if (error.length > 1)
	    errorXHigh = true;
	}
	if (!errorYLow && epoint.hasErrorY()) {
	  errorYLow = true;
	  error     = epoint.getErrorY();
	  if (error.length > 1)
	    errorYHigh = true;
	}
      }
    }

    result = new DefaultSpreadSheet();
    result.setDataRowClass(SparseDataRow.class);
    result.setName(getID());
    row    = result.getHeaderRow();
    row.addCell("ID").setContent("ID");
    row.addCell("X").setContent("X");
    row.addCell("Y").setContent("Y");
    if (errorXLow)
      row.addCell("EX1").setContent("Error X (low/delta)");
    if (errorXHigh)
      row.addCell("EX2").setContent("Error X (high)");
    if (errorYLow)
      row.addCell("EY1").setContent("Error Y (low/delta)");
    if (errorYHigh)
      row.addCell("EY2").setContent("Error Y (high)");
    iter = points.iterator();
    while (iter.hasNext()) {
      point = iter.next();
      row   = result.addRow();
      row.addCell("ID").setContent(point.getID());
      if (hasMappingX())
	row.addCell("X").setContent(getMappingX(point.getX()));
      else
	row.addCell("X").setContent(point.getX());
      if (hasMappingY())
	row.addCell("Y").setContent(getMappingY(point.getY()));
      else
	row.addCell("Y").setContent(point.getY());
      if (errorXLow || errorXHigh || errorYLow || errorYHigh) {
	if (point instanceof XYSequencePointWithErrors) {
	  epoint = (XYSequencePointWithErrors) point;
	  if (errorXLow || errorXHigh) {
	    if (epoint.hasErrorX()) {
	      error = epoint.getErrorX();
	      row.addCell("EX1").setContent(error[0]);
	      if (error.length > 1)
		row.addCell("EX2").setContent(error[1]);
	    }
	  }
	  if (errorYLow || errorYHigh) {
	    if (epoint.hasErrorY()) {
	      error = epoint.getErrorY();
	      row.addCell("EY1").setContent(error[0]);
	      if (error.length > 1)
		row.addCell("EY2").setContent(error[1]);
	    }
	  }
	}
      }
      if (point.hasMetaData()) {
	meta = point.getMetaData();
	for (String key: meta.keySet()) {
	  // extend header?
	  if (result.getHeaderRow().getCell("MetaData-" + key) == null)
	    result.getHeaderRow().addCell("MetaData-" + key).setContent("MetaData-" + key);
	  row.addCell("MetaData-" + key).setNative(meta.get(key));
	}
      }
    }

    return result;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    return toSpreadSheet(m_Points);
  }
}
