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
 * Instance.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instance;

import adams.core.Range;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.weka.ArffUtils;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores values from weka.core.Instance objects, with X being the
 * attribute index (integer) and Y being the internal value (double).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Instance
  extends AbstractDataContainer<InstancePoint>
  implements MutableReportHandler<Report>, NotesHandler, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 8553741559715144356L;

  /** the key in the report for the dataset name. */
  public static final String REPORT_DATASET = "Dataset-Name";

  /** the key in the report for the database ID. */
  public static final String REPORT_DB_ID = "DB-ID";

  /** the key in the report for the ID. */
  public static final String REPORT_ID = "ID";

  /** the key in the report for the display ID. */
  public static final String REPORT_DISPLAY_ID = "Display-ID";

  /** the key in the report for the class. */
  public static final String REPORT_CLASS = "Class";

  /** the key in the report for the row in the dataset. */
  public static final String REPORT_ROW = "Dataset-Row";

  /** the key prefix in the report for the additional attributes. */
  public static final String REPORT_ADDITIONAL_PREFIX = "Attribute-";

  /** the default comparator. */
  protected static DataPointComparator m_Comparator;

  /** a reference to the dataset the data was obtained from. */
  protected Instances m_DatasetHeader;

  /** the automatically generated report. */
  protected Report m_Report;

  /** the notes for the chromatogram. */
  protected Notes m_Notes;

  /**
   * Initializes the sequence.
   */
  public Instance() {
    super();

    m_Report = newReport();
    m_Notes  = new Notes();

    if (m_Comparator == null)
      m_Comparator = newComparator();

    setID("");
  }

  /**
   * Sets the ID of the sequence.
   *
   * @param value	the new ID
   */
  @Override
  public void setID(String value) {
    super.setID(value);
    m_Report.addParameter(REPORT_DISPLAY_ID, value);
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator to use
   */
  public DataPointComparator<InstancePoint> newComparator() {
    return new InstancePointComparator();
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  public DataPointComparator<InstancePoint> getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a sequence point.
   *
   * @return		the new sequence point
   */
  public InstancePoint newPoint() {
    return new InstancePoint();
  }

  /**
   * Creates an empty report.
   *
   * @return		the empty report
   */
  protected Report newReport() {
    Report	result;

    result = new Report();
    result.addField(new Field(REPORT_DATASET,    DataType.STRING));
    result.addField(new Field(REPORT_DB_ID,      DataType.NUMERIC));
    result.addField(new Field(REPORT_ID,         DataType.STRING));
    result.addField(new Field(REPORT_DISPLAY_ID, DataType.STRING));
    result.addField(new Field(REPORT_CLASS,      DataType.STRING));  // gets updated if necessary when setting the weka.core.Instance object

    return result;
  }

  /**
   * Removes all the points and report and nulls the header reference.
   *
   * @see	#m_DatasetHeader
   */
  @Override
  public void clear() {
    m_DatasetHeader = null;
    m_Report        = newReport();

    super.clear();
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<InstancePoint> other) {
    super.assign(other);

    m_DatasetHeader = ((Instance) other).getDatasetHeader();
    m_Report        = ((Instance) other).getReport().getClone();
    m_Notes         = ((Instance) other).getNotes().getClone();
  }

  /**
   * Returns whether a header of a dataset is available.
   *
   * @return		true if a header is available
   */
  public boolean hasDatasetHeader() {
    return (m_DatasetHeader != null);
  }

  /**
   * Returns the header of the underlying dataset.
   *
   * @return		the header, null if none currently set
   */
  public weka.core.Instances getDatasetHeader() {
    return m_DatasetHeader;
  }

  /**
   * Clears the container and adds the data from the weka.core.Instance
   * (internal values).
   *
   * @param inst	the instance to use
   */
  public void set(weka.core.Instance inst) {
    set(inst, -1, new int[0], new Range("first-last"), null);
  }

  /**
   * Clears the container and adds the data from the weka.core.Instance
   * (internal values). Uses only the attributes specified in the range.
   *
   * @param inst	the instance to use
   * @param index	the row index in the original dataset, use -1 to ignore
   * @param additional	the indices of the additional attribute values to
   * 			store in the report
   * @param range	the range of attributes to limit the instance to
   * @param attTypes	whether to restrict to attributes types, null or zero-length array means no restriction
   * @see		Attribute
   */
  public void set(weka.core.Instance inst, int index, int[] additional, Range range, HashSet<Integer> attTypes) {
    ArrayList<InstancePoint>	list;
    int				i;
    Attribute			att;
    String			fieldStr;

    clear();

    // keep reference to header
    m_DatasetHeader = new Instances(inst.dataset(), 0);

    range.setMax(inst.numAttributes());
    list = new ArrayList<InstancePoint>();
    for (i = 0; i < inst.numAttributes(); i++) {
      if (i == inst.classIndex())
	continue;
      if (!range.isInRange(i))
	continue;
      if ((attTypes != null) && (!attTypes.contains(inst.attribute(i).type())))
	continue;
      list.add(new InstancePoint(i, inst.value(i)));
    }

    addAll(list);

    // create artificial report
    m_Report.addParameter(REPORT_DATASET, m_DatasetHeader.relationName());
    att = m_DatasetHeader.attribute(ArffUtils.getDBIDName());
    if (att != null) {
      m_Report.addParameter(REPORT_DB_ID, new Double(inst.value(att)));
      m_Report.setDatabaseID((int) inst.value(att));
    }
    att = m_DatasetHeader.attribute(ArffUtils.getIDName());
    if (att != null)
      m_Report.addParameter(REPORT_ID, new Double(inst.value(att)));
    // class
    if (inst.classIndex() > -1) {
      if (inst.classAttribute().isNumeric()) {
	m_Report.addField(new Field(REPORT_CLASS, DataType.NUMERIC));
	if (inst.classIsMissing()) {
	  m_Report.addField(new Field(REPORT_CLASS, DataType.STRING));
	  m_Report.addParameter(REPORT_CLASS, "?");
	}
	else {
	  m_Report.addField(new Field(REPORT_CLASS, DataType.NUMERIC));
	  m_Report.addParameter(REPORT_CLASS, Double.toString(inst.classValue()));
	}
      }
      else {
	m_Report.addField(new Field(REPORT_CLASS, DataType.STRING));
	if (inst.classIsMissing())
	  m_Report.addParameter(REPORT_CLASS, "?");
	else
	  m_Report.addParameter(REPORT_CLASS, inst.stringValue(inst.classIndex()));
      }
    }
    // row
    if (index != -1) {
      m_Report.addField(new Field(REPORT_ROW, DataType.NUMERIC));
      m_Report.addParameter(REPORT_ROW, new Double(index + 1));
    }
    // additional attributes
    for (i = 0; i < additional.length; i++) {
      att      = inst.attribute(additional[i]);
      fieldStr = REPORT_ADDITIONAL_PREFIX + (additional[i]+1) + "-" + att.name();
      if (att.isNumeric()) {
	m_Report.addField(new Field(fieldStr, DataType.NUMERIC));
	m_Report.addParameter(fieldStr, inst.value(additional[i]));
      }
      else {
	m_Report.addField(new Field(fieldStr, DataType.STRING));
	m_Report.addParameter(fieldStr, inst.stringValue(additional[i]));
      }
    }

    // display ID (hashcode of string representation of Instance)
    if (getID().length() == 0)
      setID("" + inst.toString().hashCode());
  }

  /**
   * Checks whether a report is present.
   *
   * @return		always true
   */
  public boolean hasReport() {
    return true;
  }

  /**
   * Sets a new report.
   *
   * @param value	the new report
   */
  public void setReport(Report value) {
    m_Report = value;
  }

  /**
   * Returns the report.
   *
   * @return		the report, can be null if none available
   */
  public Report getReport() {
    return m_Report;
  }

  /**
   * Generates a weka instance, if a dataset header is available.
   *
   * @return		the generated Instance, null if no header available
   */
  public weka.core.Instance toInstance() {
    weka.core.Instance	result;
    Field		field;

    result = null;

    if (hasDatasetHeader()) {
      result = new weka.core.DenseInstance(m_DatasetHeader.numAttributes());
      result.setDataset(m_DatasetHeader);
      for (InstancePoint point: this) {
	if (point.getX() < m_DatasetHeader.numAttributes())
	  result.setValue(point.getX(), point.getY());
      }
      if (m_DatasetHeader.classIndex() > -1) {
	field = new Field(REPORT_CLASS, DataType.UNKNOWN);
	if (m_DatasetHeader.classAttribute().isNumeric())
	  result.setClassValue(getReport().getDoubleValue(field));
	else
	  result.setClassValue(m_DatasetHeader.classAttribute().indexOfValue(getReport().getStringValue(field)));
      }
    }

    return result;
  }

  /**
   * Returns the currently stored notes.
   *
   * @return		the current notes
   */
  public Notes getNotes() {
    return m_Notes;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    Iterator<InstancePoint>	iter;
    SpreadSheet			result;
    Row				row;
    InstancePoint		point;

    result = new DefaultSpreadSheet();
    result.setName(getID());
    row    = result.getHeaderRow();
    row.addCell("A").setContent("Attribute");
    row.addCell("V").setContent("Value");
    iter = iterator();
    while (iter.hasNext()) {
      point = iter.next();
      row = result.addRow();
      row.addCell("A").setContent(point.getX());
      row.addCell("V").setContent(point.getY());
    }
    
    return result;
  }
}
