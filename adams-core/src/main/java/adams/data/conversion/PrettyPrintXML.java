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
 * PrettyPrintXML.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.ClassCrossReference;

/**
 <!-- globalinfo-start -->
 * Turns the XML string into a pretty-printed XML string.<br>
 * <br>
 * See also:<br>
 * adams.data.conversion.XMLToDOM<br>
 * adams.data.conversion.DOMToString
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-spaces &lt;int&gt; (property: numSpaces)
 * &nbsp;&nbsp;&nbsp;The number of spaces to use for indentation.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrettyPrintXML
  extends AbstractStringConversion
  implements ClassCrossReference {

  private static final long serialVersionUID = -3082296139248705157L;

  /** the number of spaces to use for indentation. */
  protected int m_NumSpaces;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the XML string into a pretty-printed XML string.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{XMLToDOM.class, DOMToString.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-spaces", "numSpaces",
      2, 0, null);
  }

  /**
   * Sets the number of spaces to use for pretty printing.
   *
   * @param value	the number of spaces
   */
  public void setNumSpaces(int value) {
    if (getOptionManager().isValid("numSpaces", value)) {
      m_NumSpaces = value;
      reset();
    }
  }

  /**
   * Returns the number of spaces to use for pretty printing.
   *
   * @return		the number of spaces
   */
  public int getNumSpaces() {
    return m_NumSpaces;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSpacesTipText() {
    return "The number of spaces to use for indentation.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    MultiConversion	multi;
    XMLToDOM		xml2dom;
    DOMToString		dom2str;

    xml2dom = new XMLToDOM();
    dom2str = new DOMToString();
    dom2str.setPrettyPrinting(true);
    dom2str.setNumSpaces(m_NumSpaces);
    multi = new MultiConversion();
    multi.setSubConversions(new Conversion[]{xml2dom, dom2str});
    multi.setInput(m_Input);
    return multi.doConvert();
  }
}
