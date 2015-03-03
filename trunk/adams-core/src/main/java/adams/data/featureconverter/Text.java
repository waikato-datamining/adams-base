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
 * Text.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Simple feature converter that generates a CSV-like textual output.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use between cells.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 * 
 * <pre>-missing-value &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The value to use for missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Text
  extends AbstractFeatureConverter<String,String> {

  /** for serialization. */
  private static final long serialVersionUID = 2245576408802564218L;

  /** the separator to use. */
  protected String m_Separator;

  /** the string to use for missing values. */
  protected String m_MissingValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple feature converter that generates a CSV-like textual output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "separator", "separator",
	    ",");

    m_OptionManager.add(
	    "missing-value", "missingValue",
	    "");
  }
  
  /**
   * Sets the separator to use.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the separator in use.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use between cells.";
  }
  
  /**
   * Sets the string to use for missing values.
   *
   * @param value	the missing value string
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the string to use for missing values.
   *
   * @return		the missing value string
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The value to use for missing values.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "separator", m_Separator, "separator: ");
  }

  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return String.class;
  }

  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return String.class;
  }

  /**
   * Performs the actual generation of the header data structure using the 
   * supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  @Override
  protected String doGenerateHeader(HeaderDefinition header) {
    StringBuilder	result;
    
    result = new StringBuilder();
    for (String name: header.getNames()) {
      if (result.length() > 0)
	result.append(m_Separator);
      result.append(Utils.doubleQuote(name));
    }
    
    return result.toString();
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected String doGenerateRow(List data) {
    StringBuilder	result;
    
    result = new StringBuilder();
    for (Object value: data) {
      if (result.length() > 0)
	result.append(m_Separator);
      if (value == null)
	result.append(Utils.doubleQuote(m_MissingValue));
      else
	result.append(Utils.doubleQuote(value.toString()));
    }

    return result.toString();
  }
}
