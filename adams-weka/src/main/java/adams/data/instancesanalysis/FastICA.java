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
 * FastICA.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis;

import adams.data.instancesanalysis.pls.MatrixHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaAttributeRange;
import com.github.waikatodatamining.matrix.core.Matrix;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Performs independent components analysis and allows access to components and sources.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FastICA
  extends AbstractInstancesAnalysis {

  private static final long serialVersionUID = 7150143741822676345L;

  /** the range of attributes to work. */
  protected WekaAttributeRange m_AttributeRange;

  /** the Fast ICA analysis to use. */
  protected com.github.waikatodatamining.matrix.algorithm.ica.FastICA m_ICA;

  /** the components. */
  protected SpreadSheet m_Components;

  /** the sources. */
  protected SpreadSheet m_Sources;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs independent components analysis (Fast ICA) and allows access to components and sources.";
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
      "ica", "ICA",
      new com.github.waikatodatamining.matrix.algorithm.ica.FastICA());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Components = null;
    m_Sources    = null;
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
   * Sets the ICA analysis.
   *
   * @param value	the analysis
   */
  public void setICA(com.github.waikatodatamining.matrix.algorithm.ica.FastICA value) {
    m_ICA = value;
    reset();
  }

  /**
   * Returns the ICA analysis.
   *
   * @return		the analysis
   */
  public com.github.waikatodatamining.matrix.algorithm.ica.FastICA getICA() {
    return m_ICA;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ICATipText() {
    return "The ICA analysis setup to use.";
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
  protected String doAnalyze(Instances data) throws Exception {
    String	result;
    Matrix 	matrix;
    Remove 	remove;

    result       = null;
    m_Components = null;
    m_Sources    = null;

    if (!m_AttributeRange.isAllRange()) {
      if (isLoggingEnabled())
	getLogger().info("Filtering attribute range: " + m_AttributeRange.getRange());
      remove = new Remove();
      remove.setAttributeIndicesArray(m_AttributeRange.getIntIndices());
      remove.setInvertSelection(true);
      remove.setInputFormat(data);
      data = Filter.useFilter(data, remove);
    }

    if (isLoggingEnabled())
      getLogger().info("Performing ICA...");
    matrix     = m_ICA.transform(MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getAll(data)));
    if (matrix != null) {
      m_Components = MatrixHelper.matrixToSpreadSheet(MatrixHelper.matrixAlgoToWeka(m_ICA.getComponents()), "Component-");
      m_Sources    = MatrixHelper.matrixToSpreadSheet(MatrixHelper.matrixAlgoToWeka(m_ICA.getSources()), "Source-");
    }
    else {
      result = "Failed to transform data!";
    }

    return result;
  }

  /**
   * Returns the components.
   *
   * @return		the components, null if not available
   */
  public SpreadSheet getComponents() {
    return m_Components;
  }

  /**
   * Returns the sources.
   *
   * @return		the sources, null if not available
   */
  public SpreadSheet getSources() {
    return m_Sources;
  }
}
