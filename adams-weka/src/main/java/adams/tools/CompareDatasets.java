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
 * CompareDatasets.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.tools;

import java.util.Hashtable;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.Index;
import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Compares two datasets, either row-by-row or using a row attribute listing a unique ID for matching the rows, outputting the correlation coefficient of the numeric attributes found in the ranges defined by the user.<br/>
 * In order to trim down the number of generated rows, a threshold can be specified. Only rows are output which correlation coefficient is below that threshold.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-dataset1 &lt;adams.core.io.PlaceholderFile&gt; (property: dataset1)
 * &nbsp;&nbsp;&nbsp;The first dataset in the comparison.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-range1 &lt;java.lang.String&gt; (property: range1)
 * &nbsp;&nbsp;&nbsp;The range of attributes of the first dataset.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-row1 &lt;java.lang.String&gt; (property: rowAttribute1)
 * &nbsp;&nbsp;&nbsp;The index for the attribute used for identifying rows to compare; if not
 * &nbsp;&nbsp;&nbsp;provided, then the comparison is performed row-by-row (first dataset).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-dataset2 &lt;adams.core.io.PlaceholderFile&gt; (property: dataset2)
 * &nbsp;&nbsp;&nbsp;The second dataset in the comparison.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-range2 &lt;java.lang.String&gt; (property: range2)
 * &nbsp;&nbsp;&nbsp;The range of attributes of the second dataset.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-row2 &lt;java.lang.String&gt; (property: rowAttribute2)
 * &nbsp;&nbsp;&nbsp;The index for the attribute used for identifying rows to compare; if not
 * &nbsp;&nbsp;&nbsp;provided, then the comparison is performed row-by-row (second dataset).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to save the comparison result in (CSV format).
 * &nbsp;&nbsp;&nbsp;default: output.csv
 * </pre>
 *
 * <pre>-missing &lt;adams.core.io.PlaceholderFile&gt; (property: missing)
 * &nbsp;&nbsp;&nbsp;The file to save the information about missing rows to (CSV format).
 * &nbsp;&nbsp;&nbsp;default: missing.csv
 * </pre>
 *
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold for the correlation coefficient; only if the coefficient is
 * &nbsp;&nbsp;&nbsp;below that threshold, it will get output; 0.0 turns the threshold off.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CompareDatasets
  extends AbstractTool
  implements OutputFileGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -1399473007795695271L;

  /** the first dataset. */
  protected PlaceholderFile m_Dataset1;

  /** the first range of attributes. */
  protected Range m_Range1;

  /** the optional attribute for matching up rows (dataset 1). */
  protected Index m_RowAttribute1;

  /** the second dataset. */
  protected PlaceholderFile m_Dataset2;

  /** the second range of attributes. */
  protected Range m_Range2;

  /** the optional attribute for matching up rows (dataset 2). */
  protected Index m_RowAttribute2;

  /** the output file (CSV format). */
  protected PlaceholderFile m_OutputFile;

  /** the output file for missing tests (CSV format). */
  protected PlaceholderFile m_Missing;

  /** the current dataset 1. */
  protected Instances m_Data1;

  /** the current dataset 2. */
  protected Instances m_Data2;

  /** whether to use the row attribute or not. */
  protected Boolean m_UseRowAttribute;

  /** whether the row attribute is a string/nominal attribute or not. */
  protected boolean m_RowAttributeIsString;

  /** the indices for the first dataset. */
  protected int[] m_Indices1;

  /** the indices for the second dataset. */
  protected int[] m_Indices2;

  /** the lookup table of indices for the second dataset. */
  protected Hashtable<String,Integer> m_Lookup2;

  /** the threshold for listing correlations. */
  protected double m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Compares two datasets, either row-by-row or using a row attribute "
      + "listing a unique ID for matching the rows, outputting the correlation "
      + "coefficient of the numeric attributes found in the ranges defined by "
      + "the user.\n"
      + "In order to trim down the number of generated rows, a threshold can "
      + "be specified. Only rows are output which correlation coefficient "
      + "is below that threshold.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dataset1", "dataset1",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "range1", "range1",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "row1", "rowAttribute1",
	    "");

    m_OptionManager.add(
	    "dataset2", "dataset2",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "range2", "range2",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "row2", "rowAttribute2",
	    "");

    m_OptionManager.add(
	    "output", "outputFile",
	    new PlaceholderFile("output.csv"));

    m_OptionManager.add(
	    "missing", "missing",
	    new PlaceholderFile("missing.csv"));

    m_OptionManager.add(
	    "threshold", "threshold",
	    0.0, 0.0, 1.0);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Range1        = new Range();
    m_Range2        = new Range();
    m_RowAttribute1 = new Index();
    m_RowAttribute2 = new Index();
  }

  /**
   * Sets the first dataset for the comparison.
   *
   * @param value	the dataset
   */
  public void setDataset1(PlaceholderFile value) {
    m_Dataset1 = value;
    reset();
  }

  /**
   * Returns the first dataset for the comparison.
   *
   * @return		the dataset
   */
  public PlaceholderFile getDataset1() {
    return m_Dataset1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataset1TipText() {
    return "The first dataset in the comparison.";
  }

  /**
   * Sets the second dataset for the comparison.
   *
   * @param value	the dataset
   */
  public void setDataset2(PlaceholderFile value) {
    m_Dataset2 = value;
    reset();
  }

  /**
   * Returns the second dataset for the comparison.
   *
   * @return		the dataset
   */
  public PlaceholderFile getDataset2() {
    return m_Dataset2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataset2TipText() {
    return "The second dataset in the comparison.";
  }

  /**
   * Sets the range of attributes of the first dataset.
   *
   * @param value	the range
   */
  public void setRange1(Range value) {
    m_Range1 = value;
    reset();
  }

  /**
   * Returns the range of attributes of the first dataset.
   *
   * @return		the range
   */
  public Range getRange1() {
    return m_Range1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String range1TipText() {
    return "The range of attributes of the first dataset.";
  }

  /**
   * Sets the range of attributes of the second dataset.
   *
   * @param value	the range
   */
  public void setRange2(Range value) {
    m_Range2 = value;
    reset();
  }

  /**
   * Returns the range of attributes of the second dataset.
   *
   * @return		the range
   */
  public Range getRange2() {
    return m_Range2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String range2TipText() {
    return "The range of attributes of the second dataset.";
  }

  /**
   * Sets the index of the attribute used for identifying rows to compare
   * against each other (first dataset).
   *
   * @param value	the index
   */
  public void setRowAttribute1(String value) {
    m_RowAttribute1.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the attribute used for identifying rows to compare
   * against each other (first dataset).
   *
   * @return		the index
   */
  public String getRowAttribute1() {
    return m_RowAttribute1.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowAttribute1TipText() {
    return
        "The index for the attribute used for identifying rows to compare; if "
      + "not provided, then the comparison is performed row-by-row (first dataset).";
  }

  /**
   * Sets the index of the attribute used for identifying rows to compare
   * against each other (second dataset).
   *
   * @param value	the index
   */
  public void setRowAttribute2(String value) {
    m_RowAttribute2.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the attribute used for identifying rows to compare
   * against each other (second dataset).
   *
   * @return		the index
   */
  public String getRowAttribute2() {
    return m_RowAttribute2.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowAttribute2TipText() {
    return
        "The index for the attribute used for identifying rows to compare; if "
      + "not provided, then the comparison is performed row-by-row (second dataset).";
  }

  /**
   * Sets the first dataset for the comparison.
   *
   * @param value	the dataset
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the first dataset for the comparison.
   *
   * @return		the dataset
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The file to save the comparison result in (CSV format).";
  }

  /**
   * Sets the first dataset for the comparison.
   *
   * @param value	the dataset
   */
  public void setMissing(PlaceholderFile value) {
    m_Missing = value;
    reset();
  }

  /**
   * Returns the first dataset for the comparison.
   *
   * @return		the dataset
   */
  public PlaceholderFile getMissing() {
    return m_Missing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingTipText() {
    return "The file to save the information about missing rows to (CSV format).";
  }

  /**
   * Sets the threshold for the correlation coefficient.
   *
   * @param value	the threshold (0.0 turns it off)
   */
  public void setThreshold(double value) {
    if ((value >= 0.0) && (value <= 1.0)) {
      m_Threshold = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Threshold has to satisfy 0<=x<=1.0, provided: " + value);
    }
  }

  /**
   * Returns the threshold for the correlation coefficient.
   *
   * @return		the threshold (0.0 means it is turned off)
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return
        "The threshold for the correlation coefficient; only if the "
      + "coefficient is below that threshold, it will get output; 0.0 turns "
      + "the threshold off.";
  }

  /**
   * Before the actual run is executed.
   */
  @Override
  protected void preRun() {
    super.preRun();

    if (!m_Dataset1.exists())
      throw new IllegalArgumentException("Input file 1 '" + m_Dataset1 + "' does not exist?");
    if (!m_Dataset2.exists())
      throw new IllegalArgumentException("Input file 2 '" + m_Dataset2 + "' does not exist?");

    if (m_Dataset1.isDirectory())
      throw new IllegalArgumentException("Input 1 '" + m_Dataset1 + "' is a directory!");
    if (m_Dataset2.isDirectory())
      throw new IllegalArgumentException("Input 2 '" + m_Dataset2 + "' is a directory!");
    if (m_OutputFile.isDirectory())
      throw new IllegalArgumentException("Output '" + m_OutputFile + "' is pointing to a directory!");

    try {
      m_Data1 = DataSource.read(m_Dataset1.getAbsolutePath());
      m_Data2 = DataSource.read(m_Dataset2.getAbsolutePath());
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    m_Range1.setMax(m_Data1.numAttributes());
    m_Range2.setMax(m_Data2.numAttributes());

    if (m_Range1.getIntIndices().length != m_Range2.getIntIndices().length)
      throw new IllegalArgumentException(
	  "Different range of attributes: "
	  + m_Range1.getIntIndices().length + " != " + m_Range2.getIntIndices().length);

    m_Indices1 = m_Range1.getIntIndices();
    m_Indices2 = m_Range2.getIntIndices();

    m_RowAttribute1.setMax(m_Data1.numAttributes());
    m_RowAttribute2.setMax(m_Data2.numAttributes());

    m_UseRowAttribute = null;
    m_Lookup2         = null;

    if (getUseRowAttribute()) {
      if (m_Data1.attribute(m_RowAttribute1.getIntIndex()).type() != m_Data2.attribute(m_RowAttribute2.getIntIndex()).type())
	throw new IllegalArgumentException(
	    "The attributes types of the two row attributes differ: "
	    + Attribute.typeToString(m_Data1.attribute(m_RowAttribute1.getIntIndex()))
	    + " != "
	    + Attribute.typeToString(m_Data2.attribute(m_RowAttribute2.getIntIndex())));
      m_RowAttributeIsString = m_Data1.attribute(m_RowAttribute1.getIntIndex()).isNominal() || m_Data1.attribute(m_RowAttribute1.getIntIndex()).isString();
    }
  }

  /**
   * Returns whether to use the row attribute or the order in the datasets
   * for matching up the rows.
   *
   * @return		true if the row attribute is used for matching
   */
  protected boolean getUseRowAttribute() {
    if (m_UseRowAttribute == null)
      m_UseRowAttribute = (m_RowAttribute1.getIndex().length() > 0) && (m_RowAttribute2.getIndex().length() > 0);

    return m_UseRowAttribute;
  }

  /**
   * Returns either the ID for the row, either the row index of the actual
   * row attribute ID for that position.
   *
   * @param index	the index to get the ID for
   * @return		the ID
   */
  protected String getRowID(int index) {
    String	result;

    if (getUseRowAttribute()) {
      if (m_RowAttributeIsString)
	result = m_Data1.instance(index).stringValue(m_RowAttribute1.getIntIndex());
      else
	result = "" + m_Data1.instance(index).value(m_RowAttribute1.getIntIndex());
    }
    else {
      result = "" + (index + 1);
    }

    return result;
  }

  /**
   * Returns the next pair by simple index.
   *
   * @param index	the index of the pair to retrieve
   * @return		the row pair or null if not available
   */
  protected Instance[] nextByIndex(int index) {
    Instance[]	result;

    result = null;

    if (index < m_Data1.numInstances() && index < m_Data2.numInstances())
      result = new Instance[]{m_Data1.instance(index), m_Data2.instance(index)};

    return result;
  }

  /**
   * Initializes the lookup table of indices for the second dataset, if
   * necessary.
   */
  protected void initLookup() {
    int		i;
    int		attIndex;

    if (m_Lookup2 == null) {
      m_Lookup2 = new Hashtable<String,Integer>();
      attIndex  = m_RowAttribute2.getIntIndex();
      for (i = 0; i < m_Data2.numInstances(); i++) {
	if (m_RowAttributeIsString)
	  m_Lookup2.put(m_Data2.instance(i).stringValue(attIndex), i);
	else
	  m_Lookup2.put("" + m_Data2.instance(i).value(attIndex), i);
      }
    }
  }

  /**
   * Returns the next pair by using the value of the row attribute.
   *
   * @param index	the index of the pair to retrieve
   * @return		the row pair or null if not available
   */
  protected Instance[] nextByRowAttribute(int index) {
    Instance[]	result;
    int		attIndex;
    Integer	rowIndex;

    result = null;

    if (index < m_Data1.numInstances() && index < m_Data2.numInstances()) {
      initLookup();

      attIndex = m_RowAttribute1.getIntIndex();
      if (m_RowAttributeIsString)
	rowIndex = m_Lookup2.get(m_Data1.instance(index).stringValue(attIndex));
      else
	rowIndex = m_Lookup2.get("" + m_Data1.instance(index).value(attIndex));

      if (rowIndex != null) {
	result    = new Instance[2];
	result[0] = m_Data1.instance(index);
	result[1] = m_Data2.instance(rowIndex);
      }
    }

    return result;
  }

  /**
   * Returns the next row pair to compare.
   *
   * @param index	the index of the pair to retrieve
   * @return		the row pair or null if not available
   */
  protected Instance[] next(int index) {
    if (getUseRowAttribute())
      return nextByRowAttribute(index);
    else
      return nextByIndex(index);
  }

  /**
   * Returns the correlation between the two rows.
   *
   * @param first	the first row
   * @param second	the second row
   * @return		the correlation
   */
  protected double getCorrelation(Instance first, Instance second) {
    double[]	val1;
    double[]	val2;
    int		i;

    val1 = new double[m_Indices1.length];
    val2 = new double[m_Indices2.length];

    for (i = 0; i < val1.length; i++) {
      if (first.attribute(m_Indices1[i]).isNumeric())
	val1[i] = first.value(m_Indices1[i]);
      if (second.attribute(m_Indices2[i]).isNumeric())
	val2[i] = second.value(m_Indices2[i]);
    }

    return StatUtils.correlationCoefficient(val1, val2);
  }

  /**
   * Performs the comparison.
   */
  @Override
  protected void doRun() {
    SpreadSheet		output;
    SpreadSheet		missing;
    Row			row;
    Instance[]		pair;
    int			i;
    double		correlation;

    // spreadsheet headers
    output = new SpreadSheet();
    row    = output.getHeaderRow();
    if (getUseRowAttribute())
      row.addCell("ID").setContent("ID");
    else
      row.addCell("ID").setContent("Index");
    row.addCell("Correlation").setContent("Correlation");

    missing = null;
    if (!m_Missing.isDirectory()) {
      missing = new SpreadSheet();
      row     = missing.getHeaderRow();
      if (getUseRowAttribute())
	row.addCell("ID").setContent("ID");
      else
	row.addCell("ID").setContent("Index");
    }

    for (i = 0; i < m_Data1.numInstances(); i++) {
      pair = next(i);

      if (pair != null) {
	correlation = getCorrelation(pair[0], pair[1]);
	if ((m_Threshold == 0.0) || ((m_Threshold > 0.0) && (correlation < m_Threshold))) {
	  row = output.addRow("" + (i + 1));
	  row.addCell("ID").setContent(getRowID(i));
	  row.addCell("Correlation").setContent(correlation);
	}
      }
      else {
	if (missing != null) {
	  row = missing.addRow("" + (i + 1));
	  row.addCell("ID").setContent(getRowID(i));
	}
      }

      if (isLoggingEnabled() && (i % 100 == 0))
	getLogger().info("Processed " + i + "/" + m_Data1.numInstances());
    }

    // write file
    if (!new CsvSpreadSheetWriter().write(output, m_OutputFile.getAbsolutePath()))
      getLogger().severe("Failed to write output to '" + m_OutputFile + "'!");
    else
      getLogger().info("Output written to '" + m_OutputFile + "'!");

    if (missing != null) {
      if (!new CsvSpreadSheetWriter().write(missing, m_Missing.getAbsolutePath()))
	getLogger().severe("Failed to write missing data to '" + m_Missing + "'!");
      else
	getLogger().info("Missing data written to '" + m_Missing + "'!");
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Data1    = null;
    m_Data2    = null;
    m_Indices1 = null;
    m_Indices2 = null;
  }
}
