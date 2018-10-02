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
 * PCA.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing.unsupervised;

import adams.data.spreadsheet.Row;
import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.Capability;
import adams.ml.data.Dataset;
import adams.ml.data.DatasetUtils;
import adams.ml.data.DefaultDataset;
import adams.ml.preprocessing.AbstractColumnSubsetBatchFilter;
import com.github.waikatodatamining.matrix.core.Matrix;

/**
 <!-- globalinfo-start -->
 * Performs principal components analysis.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-column-subset &lt;RANGE|REGEXP&gt; (property: columnSubset)
 * &nbsp;&nbsp;&nbsp;Defines how to determine the columns to use for filtering.
 * &nbsp;&nbsp;&nbsp;default: RANGE
 * </pre>
 *
 * <pre>-col-range &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: colRange)
 * &nbsp;&nbsp;&nbsp;The range of columns to use in the filtering process.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-regexp &lt;adams.core.base.BaseRegExp&gt; (property: colRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to use on the column names to determine whether to
 * &nbsp;&nbsp;&nbsp;use a column for filtering.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-drop-other-columns &lt;boolean&gt; (property: dropOtherColumns)
 * &nbsp;&nbsp;&nbsp;If enabled, other columns that aren't used for filtering get removed from
 * &nbsp;&nbsp;&nbsp;the output; does not affect any class columns.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variance &lt;double&gt; (property: variance)
 * &nbsp;&nbsp;&nbsp;The variance to cover.
 * &nbsp;&nbsp;&nbsp;default: 0.95
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-max-columns &lt;int&gt; (property: maxColumns)
 * &nbsp;&nbsp;&nbsp;The maximum number of columns to generate.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-center &lt;boolean&gt; (property: center)
 * &nbsp;&nbsp;&nbsp;If enabled, the data gets centered rather than standardized, computing PCA
 * &nbsp;&nbsp;&nbsp;from covariance matrix rather than correlation matrix.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PCA
  extends AbstractColumnSubsetBatchFilter {

  private static final long serialVersionUID = 722136418091907244L;

  /** the variance to cover. */
  protected double m_Variance;

  /** the maximum number of attributes. */
  protected int m_MaxColumns;

  /** whether to center (rather than standardize) the data and compute PCA from
   * covariance (rather than correlation) matrix. */
  protected boolean m_Center;

  /** the actual algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.PCA m_Algorithm;

  /** the number of columns that got determined. */
  protected int m_NumColumns;

  /** temp matrix to avoid duplicate transformation. */
  protected transient Matrix m_Transformed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs principal components analysis.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "variance", "variance",
      0.95, 0.0, 1.0);

    m_OptionManager.add(
      "max-columns", "maxColumns",
      -1, -1, null);

    m_OptionManager.add(
      "center", "center",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Algorithm  = null;
    m_NumColumns = -1;
  }

  /**
   * Sets the variance.
   *
   * @param value	the variance
   */
  public void setVariance(double value) {
    if (getOptionManager().isValid("variance", value)) {
      m_Variance = value;
      reset();
    }
  }

  /**
   * Returns the variance.
   *
   * @return		the variance
   */
  public double getVariance() {
    return m_Variance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varianceTipText() {
    return "The variance to cover.";
  }

  /**
   * Sets the maximum attributes.
   *
   * @param value	the maximum
   */
  public void setMaxColumns(int value) {
    if (getOptionManager().isValid("maxColumns", value)) {
      m_MaxColumns = value;
      reset();
    }
  }

  /**
   * Returns the maximum attributes.
   *
   * @return		the maximum
   */
  public int getMaxColumns() {
    return m_MaxColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxColumnsTipText() {
    return "The maximum number of columns to generate.";
  }

  /**
   * Set whether to center (rather than standardize) the data. If set to true
   * then PCA is computed from the covariance rather than correlation matrix.
   *
   * @param center true if the data is to be centered rather than standardized
   */
  public void setCenter(boolean center) {
    m_Center = center;
    reset();
  }

  /**
   * Get whether to center (rather than standardize) the data. If true then PCA
   * is computed from the covariance rather than correlation matrix.
   *
   * @return true if the data is to be centered rather than standardized.
   */
  public boolean getCenter() {
    return m_Center;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String centerTipText() {
    return "If enabled, the data gets centered rather than standardized, "
      + "computing PCA from covariance matrix rather than correlation matrix.";
  }

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enable(Capability.NUMERIC_ATTRIBUTE);
    result.enableAllClass();

    return result;
  }

  /**
   * Filter-specific initialization.
   *
   * @param data 	the data to initialize with
   * @throws Exception	if initialization fails
   */
  @Override
  protected void doInitFilter(Dataset data) throws Exception {
    m_Algorithm = new com.github.waikatodatamining.matrix.algorithm.PCA();
    m_Algorithm.setVariance(m_Variance);
    m_Algorithm.setMaxColumns(m_MaxColumns);
    m_Algorithm.setCenter(m_Center);
    m_Transformed = m_Algorithm.transform(DatasetUtils.numericToMatrixAlgo(data, m_DataColumns.toArray()));
    m_NumColumns  = m_Transformed.numColumns();
  }

  /**
   * Initializes the output format.
   *
   * @param data	the output format
   * @throws Exception	if initialization fails
   */
  @Override
  protected Dataset initOutputFormat(Dataset data) throws Exception {
    Dataset	result;
    int		i;
    String	prefix;
    Row 	row;

    result = new DefaultDataset();

    row    = result.getHeaderRow();
    prefix = "PCA_";
    for (i = 0; i < m_NumColumns; i++)
      row.addCell(prefix + (i+1)).setContentAsString(prefix + (i+1));

    appendHeader(data, row, m_OtherColumns);
    appendHeader(data, row, m_ClassColumns);

    return result;
  }

  /**
   * Filters the dataset coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  @Override
  protected Dataset doFilter(Dataset data) throws Exception {
    Dataset	result;
    Row		rowIn;
    Row		rowOut;
    String	prefix;
    int		y;
    int		x;

    result = getOutputFormat().getClone();
    prefix = "PCA_";

    for (y = 0; y < m_Transformed.numRows(); y++) {
      rowIn  = data.getRow(y);
      rowOut = result.addRow();
      for (x = 0; x < m_Transformed.numColumns(); x++)
        rowOut.addCell(prefix + (x+1)).setContent(m_Transformed.get(y, x));
      appendData(rowIn, rowOut, m_OtherColumns);
      appendData(rowIn, rowOut, m_ClassColumns);
    }

    return result;
  }
}
