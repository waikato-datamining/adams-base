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

import adams.core.Range;
import adams.core.Utils;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaAttributeRange;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PartitionedMultiFilter;
import weka.filters.unsupervised.attribute.PublicPrincipalComponents;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;

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

  /** the supported attributes. */
  protected TIntList m_Supported;

  /** the unsupported attributes. */
  protected TIntList m_Unsupported;

  /** the indices of the kept attributes. */
  protected ArrayList<Integer> m_Kept;

  /** the number of attributes in the data (excl class). */
  protected int m_NumAttributes;

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

    m_Loadings      = null;
    m_Scores        = null;
    m_Supported     = null;
    m_Unsupported   = null;
    m_Kept          = null;
    m_NumAttributes = 0;
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
   * Create a spreadsheet to output from the coefficients 2D array
   *
   * @param data	the underlying dataset
   * @param coeff 	The coefficients from the principal components analysis
   * @return		A spreadsheet containing the components
   */
  protected SpreadSheet createSpreadSheet(Instances data, ArrayList<ArrayList<Double>> coeff) {
    SpreadSheet result;
    Row row;
    int		i;
    int		n;

    result = new DefaultSpreadSheet();
    row = result.getHeaderRow();
    row.addCell("I").setContent("Index");
    row.addCell("A").setContent("Attribute");

    for (i = 0; i < coeff.size(); i++)
      row.addCell("L" + (i+1)).setContent("Loading-" + (i+1));

    //add the first column, which will be just the number of the attribute
    for (n = 0; n < m_NumAttributes; n++) {
      row = result.addRow();
      row.addCell("I").setContent(n+1);
      row.addCell("A").setContent(data.attribute(n).name());
    }

    //each arraylist is a single column
    for (i = 0; i< coeff.size() ; i++) {
      for (n = 0; n < m_NumAttributes; n++) {
	row = result.getRow(n);

	//attribute was kept earlier
	if (m_Kept.contains(n)) {
	  int index = m_Kept.indexOf(n);
	  if (index < coeff.get(i).size()) {
	    double value = coeff.get(i).get(index);
	    row.addCell("L" + (i + 1)).setContent(value);
	  }
	  else {
	    row.addCell("L" + (i+1)).setContent(0);
	  }
	}
	//attribute wasn't kept, coefficient is 0
	else {
	  row.addCell("L" + (i+1)).setContent(0);
	}
      }
    }

    return result;
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
    String				result;
    Remove 				remove;
    PublicPrincipalComponents 		pca;
    int					i;
    Capabilities 			caps;
    PartitionedMultiFilter 		part;
    Range 				rangeUnsupported;
    Range 				rangeSupported;
    ArrayList<ArrayList<Double>> 	coeff;
    Instances				filtered;
    SpreadSheet				transformed;
    WekaInstancesToSpreadSheet 		conv;
    String				colName;

    result     = null;
    m_Loadings = null;
    m_Scores   = null;

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

    // check for unsupported attributes
    caps        = new PublicPrincipalComponents().getCapabilities();
    m_Supported = new TIntArrayList();
    m_Unsupported = new TIntArrayList();
    for (i = 0; i < data.numAttributes(); i++) {
      if (!caps.test(data.attribute(i)) || (i == data.classIndex()))
	m_Unsupported.add(i);
      else
	m_Supported.add(i);
    }
    data.setClassIndex(-1);

    m_NumAttributes = m_Supported.size();

    // the principal components will delete the attributes without any distinct values.
    // this checks which instances will be kept.
    m_Kept = new ArrayList<>();
    for (i = 0; i < m_Supported.size(); i++) {
      if (data.numDistinctValues(m_Supported.get(i)) > 1)
	m_Kept.add(m_Supported.get(i));
    }

    // build a model using the PublicPrincipalComponents
    pca = new PublicPrincipalComponents();
    pca.setMaximumAttributes(m_MaxAttributes);
    pca.setVarianceCovered(m_Variance);
    pca.setMaximumAttributeNames(m_MaxAttributeNames);
    part = null;
    if (m_Unsupported.size() > 0) {
      rangeUnsupported = new Range();
      rangeUnsupported.setMax(data.numAttributes());
      rangeUnsupported.setIndices(m_Unsupported.toArray());
      rangeSupported = new Range();
      rangeSupported.setMax(data.numAttributes());
      rangeSupported.setIndices(m_Supported.toArray());
      part = new PartitionedMultiFilter();
      part.setFilters(new Filter[]{
	pca,
	new AllFilter(),
      });
      part.setRanges(new weka.core.Range[]{
	new weka.core.Range(rangeSupported.getRange()),
	new weka.core.Range(rangeUnsupported.getRange()),
      });
    }
    try {
      if (part != null)
	part.setInputFormat(data);
      else
	pca.setInputFormat(data);
    }
    catch(Exception e) {
      result = Utils.handleException(this, "Failed to set data format", e);
    }

    transformed = null;
    if (result == null) {
      try {
	if (part != null)
	  filtered = weka.filters.Filter.useFilter(data, part);
	else
	  filtered = weka.filters.Filter.useFilter(data, pca);
      }
      catch (Exception e) {
	result   = Utils.handleException(this, "Failed to apply filter", e);
	filtered = null;
      }
      if (filtered != null) {
	conv = new WekaInstancesToSpreadSheet();
	conv.setInput(filtered);
	result = conv.convert();
	if (result == null) {
	  transformed = (SpreadSheet) conv.getOutput();
	  // shorten column names again
	  if (part != null) {
	    for (i = 0; i < transformed.getColumnCount(); i++) {
	      colName = transformed.getColumnName(i);
	      colName = colName.replaceFirst("filtered-[0-9]*-", "");
	      transformed.getHeaderRow().getCell(i).setContentAsString(colName);
	    }
	  }
	}
      }
    }

    if (result == null) {
      // get the coefficients from the filter
      m_Scores   = transformed;
      coeff      = pca.getCoefficients();
      m_Loadings = createSpreadSheet(data, coeff);
      m_Loadings.setName("Loadings for " + data.relationName());
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
