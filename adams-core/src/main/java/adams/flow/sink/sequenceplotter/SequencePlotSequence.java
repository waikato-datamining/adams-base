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
 * SequencePlotterSequence.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.container.DataPointComparator;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Extended {@link XYSequence}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotSequence
  extends XYSequence {

  /** for serialization. */
  private static final long serialVersionUID = 331392414841660594L;

  /** the meta-data key to include in the comparison of data points. */
  protected String m_MetaDataKey;

  /**
   * Initializes the sequence.
   */
  public SequencePlotSequence() {
    super();
    m_MetaDataKey = null;
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator to use
   */
  @Override
  public DataPointComparator<XYSequencePoint> newComparator() {
    return new SequencePlotPointComparator(m_Comparison, true, m_MetaDataKey);
  }

  /**
   * Sets the meta-data key to use.
   *
   * @param value	the key, null if not to use
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    m_Comparator  = newComparator();
  }

  /**
   * Returns the meta-data key in use.
   *
   * @return		the key, null if not used
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    Iterator<XYSequencePoint>	iter;
    SpreadSheet			result;
    Row				row;
    XYSequencePoint		point;
    SequencePlotPoint		spoint;
    Double[]			error;
    boolean			errorXLow;
    boolean			errorXHigh;
    boolean			errorYLow;
    boolean			errorYHigh;
    HashMap<String,Object>	meta;

    errorXLow  = false;
    errorXHigh = false;
    errorYLow  = false;
    errorYHigh = false;
    iter = iterator();
    while (iter.hasNext()) {
      point = iter.next();
      if (point instanceof SequencePlotPoint) {
	spoint = (SequencePlotPoint) point;
	if (!errorXLow && spoint.hasErrorX()) {
	  errorXLow = true;
	  error     = spoint.getErrorX();
	  if (error.length > 1)
	    errorXHigh = true;
	}
	if (!errorYLow && spoint.hasErrorY()) {
	  errorYLow = true;
	  error     = spoint.getErrorY();
	  if (error.length > 1)
	    errorYHigh = true;
	}
      }
    }
    
    result = new SpreadSheet();
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
    iter = iterator();
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
	if (point instanceof SequencePlotPoint) {
	  spoint = (SequencePlotPoint) point;
	  if (errorXLow || errorXHigh) {
	    if (spoint.hasErrorX()) {
	      error = spoint.getErrorX();
	      row.addCell("EX1").setContent(error[0]);
	      if (error.length > 1)
		row.addCell("EX2").setContent(error[1]);
	    }
	  }
	  if (errorYLow || errorYHigh) {
	    if (spoint.hasErrorY()) {
	      error = spoint.getErrorY();
	      row.addCell("EY1").setContent(error[0]);
	      if (error.length > 1)
		row.addCell("EY2").setContent(error[1]);
	    }
	  }
	}
      }
      if (point instanceof SequencePlotPoint) {
	spoint = (SequencePlotPoint) point;
	if (spoint.hasMetaData()) {
	  meta = spoint.getMetaData();
	  for (String key: meta.keySet()) {
	    // extend header?
	    if (result.getHeaderRow().getCell("MetaData-" + key) == null)
	      result.getHeaderRow().addCell("MetaData-" + key).setContent("MetaData-" + key);
	    row.addCell("MetaData-" + key).setNative(meta.get(key));
	  }
	}
      }
    }
    
    return result;
  }
}
