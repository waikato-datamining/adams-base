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
 * PLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis;

import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaAttributeRange;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLSFilterWithLoadings;

/**
 * Performs partial least squares analysis and allows access to loadings and scores.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PLS
  extends AbstractInstancesAnalysis {

  private static final long serialVersionUID = 7150143741822676345L;

  /**
   * The available algorithms.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Algorithm {
    SIMPLS,
    PLS1
  }

  /** the range of attributes to work. */
  protected WekaAttributeRange m_AttributeRange;

  /** the algorithm to use. */
  protected Algorithm m_Algorithm;

  /** the number of components. */
  protected int m_NumComponents;

  /** the loadings. */
  protected SpreadSheet m_Loadings;

  /** the scores. */
  protected SpreadSheet m_Scores;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs principal components analysis and allows access to loadings and scores.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "attribute-range", "attributeRange",
      new WekaAttributeRange(WekaAttributeRange.ALL));

    m_OptionManager.add(
      "algorithm", "algorithm",
      Algorithm.SIMPLS);

    m_OptionManager.add(
      "num-components", "numComponents",
      20, 1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Loadings = null;
    m_Scores   = null;
  }

  /**
   * Sets the attribute range parameter.
   *
   * @param value	the range
   */
  public void setAttributeRange(WekaAttributeRange value) {
    m_AttributeRange = value;
    reset();
  }

  /**
   * Returns the attribute range parameter.
   *
   * @return		the range
   */
  public WekaAttributeRange getAttributeRange() {
    return m_AttributeRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeRangeTipText() {
    return "The range of attributes to process.";
  }

  /**
   * Sets the algorithm to  use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(Algorithm value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use.
   *
   * @return		the algorithm
   */
  public Algorithm getAlgorithm() {
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
   * Sets the number of components.
   *
   * @param value	the components
   */
  public void setNumComponents(int value) {
    if (getOptionManager().isValid("numComponents", value)) {
      m_NumComponents = value;
      reset();
    }
  }

  /**
   * Returns the number of components.
   *
   * @return		the components
   */
  public int getNumComponents() {
    return m_NumComponents;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numComponentsTipText() {
    return "The number of components.";
  }

  /**
   * Hook method for checks.
   *
   * @param data	the data to check
   */
  @Override
  protected void check(Instances data) {
    super.check(data);

    m_AttributeRange.setData(data);
    if (m_AttributeRange.getIntIndices().length == 0)
      throw new IllegalStateException("No attributes selected with range: " + m_AttributeRange.getRange());
  }

  /**
   * Performs the actual analysis.
   *
   * @param data	the data to analyze
   * @return		null if successful, otherwise error message
   * @throws Exception	if analysis fails
   */
  @Override
  protected String doAnalyze(Instances data) throws  Exception {
    String 			result;
    PLSFilterWithLoadings 	pls;
    WekaInstancesToSpreadSheet 	conv;
    SpreadSheet 		transformed;
    Matrix 			matrix;
    SpreadSheet 		loadings;
    Row 			row;
    int				i;
    int				n;

    m_Loadings = null;
    m_Scores   = null;

    pls = new PLSFilterWithLoadings();
    switch (m_Algorithm) {
      case SIMPLS:
	pls.setAlgorithm(new SelectedTag(PLSFilterWithLoadings.ALGORITHM_SIMPLS, PLSFilterWithLoadings.TAGS_ALGORITHM));
	break;
      case PLS1:
	pls.setAlgorithm(new SelectedTag(PLSFilterWithLoadings.ALGORITHM_PLS1, PLSFilterWithLoadings.TAGS_ALGORITHM));
	break;
      default:
	throw new IllegalStateException("Unhandled algorithm: " + m_Algorithm);
    }
    pls.setNumComponents(m_NumComponents);
    pls.setInputFormat(data);
    data = Filter.useFilter(data, pls);
    conv = new WekaInstancesToSpreadSheet();
    conv.setInput(data);
    result = conv.convert();
    if (result == null) {
      transformed = (SpreadSheet) conv.getOutput();
      switch (m_Algorithm) {
	case SIMPLS:
	  matrix = pls.getSimplsW();
	  break;
	case PLS1:
	  matrix = pls.getPLS1P();
	  break;
	default:
	  throw new IllegalStateException("Unhandled algorithm: " + m_Algorithm);
      }
      loadings = new DefaultSpreadSheet();
      for (i = 0; i < matrix.getColumnDimension(); i++)
	loadings.getHeaderRow().addCell("L-" + (i + 1)).setContentAsString("Loading-" + (i + 1));
      for (n = 0; n < matrix.getRowDimension(); n++) {
	row = loadings.addRow();
	for (i = 0; i < matrix.getColumnDimension(); i++)
	  row.addCell("L-" + (i + 1)).setContent(matrix.get(n, i));
      }
      m_Loadings = loadings;
      m_Scores   = transformed;
    }

    return result;
  }

  /**
   * Returns the loadings.
   *
   * @return		the loadings, null if not available
   */
  public SpreadSheet getLoadings() {
    return m_Loadings;
  }

  /**
   * Returns the scores.
   *
   * @return		the scores, null if not available
   */
  public SpreadSheet getScores() {
    return m_Scores;
  }
}
