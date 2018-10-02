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
 * PLS.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing.supervised;

import adams.core.ObjectCopyHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.Capability;
import adams.ml.data.Dataset;
import adams.ml.data.DatasetUtils;
import adams.ml.data.DefaultDataset;
import adams.ml.preprocessing.AbstractColumnSubsetBatchFilter;
import com.github.waikatodatamining.matrix.algorithm.pls.AbstractPLS;
import com.github.waikatodatamining.matrix.algorithm.pls.SIMPLS;
import com.github.waikatodatamining.matrix.core.Matrix;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PLS
  extends AbstractColumnSubsetBatchFilter {

  private static final long serialVersionUID = 8479195394918205567L;

  /** the PLS algorithm to use. */
  protected AbstractPLS m_Algorithm;

  /** the actual PLS algorithm to use. */
  protected AbstractPLS m_ActualAlgorithm;

  /** the loadings. */
  protected SpreadSheet m_Loadings;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the selected partial least squares (PLS) algorithm to the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new SIMPLS());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Loadings = null;
  }

  /**
   * Sets the algorithm to  use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractPLS value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use.
   *
   * @return		the algorithm
   */
  public AbstractPLS getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use.";
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
    result.enable(Capability.NUMERIC_CLASS);

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
    Matrix	predictors;
    Matrix	response;
    String	msg;

    predictors = DatasetUtils.numericToMatrixAlgo(data, m_DataColumns.toArray());
    response   = DatasetUtils.numericToMatrixAlgo(data, m_ClassColumns.toArray());

    m_ActualAlgorithm = ObjectCopyHelper.copyObject(m_Algorithm);
    msg = m_ActualAlgorithm.initialize(predictors, response);
    if (msg != null)
      throw new Exception(msg);

    if (m_ActualAlgorithm.hasLoadings())
      m_Loadings = DatasetUtils.matrixAlgoToSpreadSheet(m_ActualAlgorithm.getLoadings(), "Loadings-");
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
    prefix = m_Algorithm.getClass().getSimpleName().toUpperCase() + "_";
    for (i = 0; i < m_Algorithm.getNumComponents(); i++)
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
    Matrix	predictors;
    Matrix	transformed;
    String	prefix;
    Row 	rowOut;
    Row		rowIn;
    int		y;
    int		x;

    predictors  = DatasetUtils.numericToMatrixAlgo(data, m_DataColumns.toArray());
    transformed = m_ActualAlgorithm.transform(predictors);
    result      = getOutputFormat().getClone();
    prefix      = m_Algorithm.getClass().getSimpleName().toUpperCase() + "_";

    for (y = 0; y < transformed.numRows(); y++) {
      rowIn  = data.getRow(y);
      rowOut = result.addRow();
      for (x = 0; x < transformed.numColumns(); x++)
        rowOut.addCell(prefix + (x+1)).setContent(transformed.get(y, x));
      appendData(rowIn, rowOut, m_OtherColumns);
      appendData(rowIn, rowOut, m_ClassColumns);
    }

    return result;
  }
}
