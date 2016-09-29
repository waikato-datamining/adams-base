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
 * WekaPrincipalComponents.java
 * Copyright (C) 2014-2016 Dutch Sprouts, Wageningen, NL
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */
package adams.flow.transformer;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.annotation.MixedCopyright;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PartitionedMultiFilter;
import weka.filters.unsupervised.attribute.PublicPrincipalComponents;

import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Performs principal components analysis on the incoming data and outputs the loadings and the transformed data as spreadsheet array.<br>
 * Automatically filters out attributes that cannot be handled by PCA.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaPrincipalComponents
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-variance-covered &lt;double&gt; (property: varianceCovered)
 * &nbsp;&nbsp;&nbsp;Retain enough PC attributes to account for this proportion of variance.
 * &nbsp;&nbsp;&nbsp;default: 0.95
 * </pre>
 * 
 * <pre>-max-attributes &lt;int&gt; (property: maximumAttributes)
 * &nbsp;&nbsp;&nbsp;The maximum number of PC attributes to retain.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max-attribute-names &lt;int&gt; (property: maximumAttributeNames)
 * &nbsp;&nbsp;&nbsp;The maximum number of attribute names to use.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * Actor that takes in an instances object containing TGA-MS data and outputs the coefficients from a principal components analysis
 *
 * @author michael.fowke
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Michael Fowke",
  license = License.GPL3,
  copyright = "2014 Dutch Sprouts, Wageningen, NL"
)
public class WekaPrincipalComponents
  extends AbstractTransformer{

  /** for serialization */
  private static final long serialVersionUID = -3079556702775500196L;

  /** the variance to cover. */
  protected double m_CoverVariance;

  /** the maximum number of attributes to keep. */
  protected int m_MaxAttributes;

  /** the maximum number of attribute names to use. */
  protected int m_MaxAttributeNames;

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
    return
      "Performs principal components analysis on the incoming data and outputs "
	+ "the loadings and the transformed data as spreadsheet array.\n"
	+ "Automatically filters out attributes that cannot be handled by PCA.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "variance-covered", "varianceCovered",
      0.95);

    m_OptionManager.add(
      "max-attributes", "maximumAttributes",
      -1, -1, null);

    m_OptionManager.add(
      "max-attribute-names", "maximumAttributeNames",
      5, -1, null);
  }

  /**
   * Sets the amount of variance to account for when retaining
   * principal components.
   *
   * @param value 	the proportion of total variance to account for
   */
  public void setVarianceCovered(double value) {
    m_CoverVariance = value;
    reset();
  }

  /**
   * Gets the proportion of total variance to account for when
   * retaining principal components.
   *
   * @return 		the proportion of variance to account for
   */
  public double getVarianceCovered() {
    return m_CoverVariance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String varianceCoveredTipText() {
    return "Retain enough PC attributes to account for this proportion of variance.";
  }

  /**
   * Sets maximum number of PC attributes to retain.
   *
   * @param value 	the maximum number of attributes
   */
  public void setMaximumAttributes(int value) {
    m_MaxAttributes = value;
    reset();
  }

  /**
   * Gets maximum number of PC attributes to retain.
   *
   * @return 		the maximum number of attributes
   */
  public int getMaximumAttributes() {
    return m_MaxAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maximumAttributesTipText() {
    return "The maximum number of PC attributes to retain.";
  }

  /**
   * Sets maximum number of attribute names.
   *
   * @param value 	the maximum number of attribute names
   */
  public void setMaximumAttributeNames(int value) {
    m_MaxAttributeNames = value;
    reset();
  }

  /**
   * Gets maximum number of attribute names to use.
   *
   * @return 		the maximum number of attribute names
   */
  public int getMaximumAttributeNames() {
    return m_MaxAttributeNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maximumAttributeNamesTipText() {
    return "The maximum number of attribute names to use.";
  }

  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "varianceCovered", m_CoverVariance, "var: ");
    result += QuickInfoHelper.toString(this, "maxAttributes", m_MaxAttributes, ", max attr: ");
    result += QuickInfoHelper.toString(this, "maxAttributeNames", m_MaxAttributeNames, ", max names: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Create a spreadsheet to output from the coefficients 2D array
   *
   * @param input	the underlying dataset
   * @param coeff 	The coefficients from the principal components analysis
   * @return		A spreadsheet containing the components
   */
  protected SpreadSheet createSpreadSheet(Instances input, ArrayList<ArrayList<Double>> coeff) {
    SpreadSheet result;
    Row 	row;
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
      row.addCell("A").setContent(input.attribute(n).name());
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute()  {
    String 				result;
    Instances 				input;
    int					i;
    Capabilities 			caps;
    PublicPrincipalComponents 		pca;
    PartitionedMultiFilter		part;
    Range 				rangeUnsupported;
    Range 				rangeSupported;
    ArrayList<ArrayList<Double>> 	coeff;
    SpreadSheet 			loadings;
    Instances				filtered;
    SpreadSheet				transformed;
    WekaInstancesToSpreadSheet		conv;
    String				colName;

    result = null;
    input = (Instances) m_InputToken.getPayload();

    // check for unsupported attributes
    caps        = new PublicPrincipalComponents().getCapabilities();
    m_Supported = new TIntArrayList();
    m_Unsupported = new TIntArrayList();
    for (i = 0; i < input.numAttributes(); i++) {
      if (caps.test(input.attribute(i)))
	m_Supported.add(i);
      else
	m_Unsupported.add(i);
    }

    m_NumAttributes = m_Supported.size();

    // the principal components will delete the attributes without any distinct values.
    // this checks which instances will be kept.
    m_Kept = new ArrayList<>();
    for (i = 0; i < m_Supported.size(); i++) {
      if (input.numDistinctValues(m_Supported.get(i)) > 1)
	m_Kept.add(m_Supported.get(i));
    }

    // build a model using the PublicPrincipalComponents
    pca = new PublicPrincipalComponents();
    pca.setMaximumAttributes(m_MaxAttributes);
    pca.setVarianceCovered(m_CoverVariance);
    pca.setMaximumAttributeNames(m_MaxAttributeNames);
    part = null;
    if (m_Unsupported.size() > 0) {
      rangeUnsupported = new Range();
      rangeUnsupported.setMax(input.numAttributes());
      rangeUnsupported.setIndices(m_Unsupported.toArray());
      rangeSupported = new Range();
      rangeSupported.setMax(input.numAttributes());
      rangeSupported.setIndices(m_Supported.toArray());
      part = new PartitionedMultiFilter();
      part.setFilters(new Filter[]{
	new AllFilter(),
	pca
      });
      part.setRanges(new weka.core.Range[]{
	new weka.core.Range(rangeUnsupported.getRange()),
	new weka.core.Range(rangeSupported.getRange()),
      });
    }
    try {
      if (part != null)
	part.setInputFormat(input);
      else
	pca.setInputFormat(input);
    }
    catch(Exception e) {
      result = handleException("Failed to set input format", e);
    }

    transformed = null;
    if (result == null) {
      try {
	if (part != null)
	  filtered = weka.filters.Filter.useFilter(input, part);
	else
	  filtered = weka.filters.Filter.useFilter(input, pca);
      }
      catch (Exception e) {
	result   = handleException("Failed to apply filter", e);
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
      coeff    = pca.getCoefficients();
      loadings = createSpreadSheet(input, coeff);
      loadings.setName("Loadings for " + input.relationName());

      // output spreadsheets with loadings/transformed data
      m_OutputToken = new Token(new SpreadSheet[]{loadings, transformed});
    }

    return result;
  }
}
