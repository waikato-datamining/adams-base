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
 * FixedNumFeatures.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Meta-feature-converter that ensures that the generated output has a fixed number of data points.<br>
 * In case of filler type FIRST, the data gets inserted at the start, as opposed to at the end when using LAST.<br>
 * NUMERIC&#47;STRING&#47;BOOLEAN use the appropriate filler value that the user specified.<br>
 * The MISSING_* types just add a missing value of the appropriate data type.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The base feature converter to use and trim to size or extend.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.TextualFeatureConverter
 * </pre>
 * 
 * <pre>-num-features &lt;int&gt; (property: numFeatures)
 * &nbsp;&nbsp;&nbsp;The number of features to ensure, either trim or fill to satisfy.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-header-template &lt;java.lang.String&gt; (property: headerTemplate)
 * &nbsp;&nbsp;&nbsp;The template for filling in the header; '#' is 1-based index for filled 
 * &nbsp;&nbsp;&nbsp;in value, '$' is 1-based, absolute column index; Using a header definition 
 * &nbsp;&nbsp;&nbsp;of 'Att-1,Att-2,Att-3' with a size of 5 will give you: 'Filler-#' -&gt; 'Att
 * &nbsp;&nbsp;&nbsp;-1,Att-2,Att-3,Filler-1,Fillter-2', 'Filler-$' -&gt; 'Att-1,Att-2,Att-3,Filler
 * &nbsp;&nbsp;&nbsp;-4,Filler-5'
 * &nbsp;&nbsp;&nbsp;default: Filler-#
 * </pre>
 * 
 * <pre>-filler-type &lt;MISSING_NUMERIC|MISSING_STRING|MISSING_BOOLEAN|NUMERIC|STRING|BOOLEAN|FIRST|LAST&gt; (property: fillerType)
 * &nbsp;&nbsp;&nbsp;The type of filler to use.
 * &nbsp;&nbsp;&nbsp;default: MISSING_NUMERIC
 * </pre>
 * 
 * <pre>-filler-numeric &lt;double&gt; (property: fillerNumeric)
 * &nbsp;&nbsp;&nbsp;The value for a numeric filler.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-filler-string &lt;java.lang.String&gt; (property: fillerString)
 * &nbsp;&nbsp;&nbsp;The value for a string filler.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-filler-boolean &lt;boolean&gt; (property: fillerBoolean)
 * &nbsp;&nbsp;&nbsp;The value for a boolean filler.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedNumFeatures
  extends AbstractMetaFeatureConverter {

  /** for serialization. */
  private static final long serialVersionUID = -5349388859224578387L;

  /**
   * Enumeration on how to fill in values if base converter generates too 
   * little data points.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum FillerType {
    /** missing numeric value. */
    MISSING_NUMERIC,
    /** missing string value. */
    MISSING_STRING,
    /** missing boolean value. */
    MISSING_BOOLEAN,
    /** use a numeric value. */
    FILLER_NUMERIC,
    /** use a string value. */
    FILLER_STRING,
    /** use a boolean value. */
    FILLER_BOOLEAN,
    /** uses the first value as template. */
    FIRST_VALUE,
    /** uses the last value as template. */
    LAST_VALUE
  }
  
  /** the number of features to guarantee. */
  protected int m_NumFeatures;
  
  /** the template to use in the header for missing values. */
  protected String m_HeaderTemplate;
  
  /** the filler type. */
  protected FillerType m_FillerType;
  
  /** the numeric filler. */
  protected double m_FillerNumeric;
  
  /** the string filler. */
  protected String m_FillerString;
  
  /** the boolean filler. */
  protected boolean m_FillerBoolean;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Meta-feature-converter that ensures that the generated output has "
	+ "a fixed number of data points.\n"
	+ "In case of filler type " + FillerType.FIRST_VALUE + ", the data gets "
	+ "inserted at the start, as opposed to at the end when using " 
	+ FillerType.LAST_VALUE + ".\n"
	+ FillerType.FILLER_NUMERIC + "/" + FillerType.FILLER_STRING + "/" + FillerType.FILLER_BOOLEAN + " "
	+ "use the appropriate filler value that the user specified.\n"
	+ "The MISSING_* types just add a missing value of the appropriate data type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-features", "numFeatures",
	    1, 1, null);

    m_OptionManager.add(
	    "header-template", "headerTemplate",
	    "Filler-#");

    m_OptionManager.add(
	    "filler-type", "fillerType",
	    FillerType.MISSING_NUMERIC);

    m_OptionManager.add(
	    "filler-numeric", "fillerNumeric",
	    0.0);

    m_OptionManager.add(
	    "filler-string", "fillerString",
	    "");

    m_OptionManager.add(
	    "filler-boolean", "fillerBoolean",
	    false);
  }

  /**
   * Returns the default converter to use.
   * 
   * @return		the converter
   */
  @Override
  protected AbstractFeatureConverter getDefaultConverter() {
    return new Text();
  }
  
  /**
   * Sets the number of features to guarantee.
   *
   * @param value	the number of features
   */
  public void setNumFeatures(int value) {
    if (value >= 1) {
      m_NumFeatures = value;
      reset();
    }
    else {
      getLogger().warning("At least 1 feature must be present, provided: " + value);
    }
  }

  /**
   * Returns the number of features to guarantee.
   *
   * @return		the number of features
   */
  public int getNumFeatures() {
    return m_NumFeatures;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFeaturesTipText() {
    return "The number of features to ensure, either trim or fill to satisfy.";
  }

  /**
   * Sets the template to use for filling in the header.
   *
   * @param value	the template
   */
  public void setHeaderTemplate(String value) {
    m_HeaderTemplate = value;
    reset();
  }

  /**
   * Returns the template to use for filling in the header.
   *
   * @return		the template
   */
  public String getHeaderTemplate() {
    return m_HeaderTemplate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTemplateTipText() {
    return 
	"The template for filling in the header; '#' is 1-based index for "
	+ "filled in value, '$' is 1-based, absolute column index; "
	+ "Using a header definition of 'Att-1,Att-2,Att-3' with a size of 5 "
	+ "will give you: 'Filler-#' -> 'Att-1,Att-2,Att-3,Filler-1,Fillter-2', "
	+ "'Filler-$' -> 'Att-1,Att-2,Att-3,Filler-4,Filler-5'";
  }

  /**
   * Sets the type of filler to use.
   *
   * @param value	the type
   */
  public void setFillerType(FillerType value) {
    m_FillerType = value;
    reset();
  }

  /**
   * Returns the type of filler to use.
   *
   * @return		the type
   */
  public FillerType getFillerType() {
    return m_FillerType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillerTypeTipText() {
    return "The type of filler to use.";
  }
  
  /**
   * Sets the value to use for a numeric filler.
   *
   * @param value	the value
   */
  public void setFillerNumeric(double value) {
    m_FillerNumeric = value;
    reset();
  }

  /**
   * Returns the value to use for a numeric filler.
   *
   * @return		the value
   */
  public double getFillerNumeric() {
    return m_FillerNumeric;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillerNumericTipText() {
    return "The value for a numeric filler.";
  }
  
  /**
   * Sets the value to use for a string filler.
   *
   * @param value	the value
   */
  public void setFillerString(String value) {
    m_FillerString = value;
    reset();
  }

  /**
   * Returns the value to use for a string filler.
   *
   * @return		the value
   */
  public String getFillerString() {
    return m_FillerString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillerStringTipText() {
    return "The value for a string filler.";
  }
  
  /**
   * Sets the value to use for a boolean filler.
   *
   * @param value	the value
   */
  public void setFillerBoolean(boolean value) {
    m_FillerBoolean = value;
    reset();
  }

  /**
   * Returns the value to use for a boolean filler.
   *
   * @return		the value
   */
  public boolean getFillerBoolean() {
    return m_FillerBoolean;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillerBooleanTipText() {
    return "The value for a boolean filler.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String converterTipText() {
    return "The base feature converter to use and trim to size or extend.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "numFeatures", m_NumFeatures, "# features: ");
    result += QuickInfoHelper.toString(this, "fillerType", m_FillerType, ", filler: ");
    result += ", " + super.getQuickInfo();
    
    return result;
  }

  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return m_Converter.getDatasetFormat();
  }

  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return m_Converter.getRowFormat();
  }

  /**
   * Performs the actual generation of the header data structure using the 
   * supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  @Override
  protected Object doGenerateHeader(HeaderDefinition header) {
    HeaderDefinition	fixed;
    int			count;
    String		name;
    
    fixed = header.getClone();
    
    // trim to size
    while (fixed.size() > m_NumFeatures) {
      if (m_FillerType == FillerType.FIRST_VALUE)
	fixed.remove(0);
      else
	fixed.remove(fixed.size() - 1);
    }
    
    // fill to size
    count = 0;
    while (fixed.size() < m_NumFeatures) {
      count++;
      name = m_HeaderTemplate.replace("#", "" + count).replace("$", "" + (fixed.size() + 1));
      switch (m_FillerType) {
	case MISSING_BOOLEAN:
	case FILLER_BOOLEAN:
	  fixed.add(name, DataType.BOOLEAN);
	  break;
	case MISSING_STRING:
	case FILLER_STRING:
	  fixed.add(name, DataType.STRING);
	  break;
	case MISSING_NUMERIC:
	case FILLER_NUMERIC:
	  fixed.add(name, DataType.NUMERIC);
	  break;
	case FIRST_VALUE:
	  fixed.add(0, name, fixed.getTypes().get(0));
	  break;
	case LAST_VALUE:
	  fixed.add(name, fixed.getTypes().get(fixed.size() - 1));
	  break;
	default:
	  throw new IllegalStateException("Unhandled filler type: " + m_FillerType);
      }
    }
    
    return m_Converter.generateHeader(fixed);
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected Object doGenerateRow(List data) {
    List	fixed;
    
    fixed = new ArrayList(data);
    
    // trim to size
    while (fixed.size() > m_NumFeatures) {
      if (m_FillerType == FillerType.FIRST_VALUE)
	fixed.remove(0);
      else
	fixed.remove(fixed.size() - 1);
    }
    
    // fill to size
    while (fixed.size() < m_NumFeatures) {
      switch (m_FillerType) {
	case MISSING_BOOLEAN:
	case MISSING_STRING:
	case MISSING_NUMERIC:
	  fixed.add(null);
	  break;
	case FILLER_BOOLEAN:
	  fixed.add(m_FillerBoolean);
	  break;
	case FILLER_STRING:
	  fixed.add(m_FillerString);
	  break;
	case FILLER_NUMERIC:
	  fixed.add(m_FillerNumeric);
	  break;
	case FIRST_VALUE:
	  fixed.add(0, fixed.get(0));
	  break;
	case LAST_VALUE:
	  fixed.add(fixed.get(fixed.size() - 1));
	  break;
	default:
	  throw new IllegalStateException("Unhandled filler type: " + m_FillerType);
      }
    }
    
    return m_Converter.generateRow(fixed);
  }

  /**
   * Generates a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   * @see		#generateHeader(List, List)
   */
  @Override
  public Object generateRow(List data) {
    if (m_Header == null)
      throw new IllegalStateException("No header available! generatedHeader called?");
    
    return doGenerateRow(data);
  }
}
