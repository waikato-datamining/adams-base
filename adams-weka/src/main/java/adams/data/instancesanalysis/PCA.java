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
 * PCA.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.instancesanalysis;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaAttributeRange;
import adams.flow.core.Token;
import adams.flow.transformer.WekaPrincipalComponents;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Performs principal components analysis and allows access to loadings and scores.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PCA
  extends AbstractInstancesAnalysis {

  private static final long serialVersionUID = 7150143741822676345L;

  /** the range of attributes to work. */
  protected WekaAttributeRange m_AttributeRange;

  /** the variance to cover. */
  protected double m_Variance;

  /** the maximum number of attributes. */
  protected int m_MaxAttributes;

  /** the maximum number of attribute names. */
  protected int m_MaxAttributeNames;

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
      "variance", "variance",
      0.95, 0.0, null);

    m_OptionManager.add(
      "max-attributes", "maxAttributes",
      -1, -1, null);

    m_OptionManager.add(
      "max-attribute-names", "maxAttributeNames",
      5, -1, null);
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
   * @return		tip text for this property suitable for
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
  public void setMaxAttributes(int value) {
    if (getOptionManager().isValid("maxAttributes", value)) {
      m_MaxAttributes = value;
      reset();
    }
  }

  /**
   * Returns the maximum attributes.
   *
   * @return		the maximum
   */
  public int getMaxAttributes() {
    return m_MaxAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxAttributesTipText() {
    return "The maximum attributes.";
  }

  /**
   * Sets the maximum number of attribute names.
   *
   * @param value	the maximum
   */
  public void setMaxAttributeNames(int value) {
    if (getOptionManager().isValid("maxAttributeNames", value)) {
      m_MaxAttributeNames = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of attribute names.
   *
   * @return		the maximum
   */
  public int getMaxAttributeNames() {
    return m_MaxAttributeNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxAttributeNamesTipText() {
    return "The maximum number of attribute names.";
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
    String			result;
    Remove 			remove;
    WekaPrincipalComponents 	pca;
    String 			msg;
    SpreadSheet[] 		sheets;

    result     = null;
    m_Loadings = null;
    m_Scores   = null;

    data.setClassIndex(-1);
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
      getLogger().info("Performing PCA...");
    pca = new WekaPrincipalComponents();
    pca.setVarianceCovered(m_Variance);
    pca.setMaximumAttributes(m_MaxAttributes);
    pca.setMaximumAttributeNames(m_MaxAttributeNames);
    pca.input(new Token(data));
    msg = pca.execute();
    if (msg != null) {
      result = "PCA error: " + msg;
    }
    else {
      sheets     = (SpreadSheet[]) pca.output().getPayload();
      m_Loadings = sheets[0];
      m_Scores   = sheets[1];
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
