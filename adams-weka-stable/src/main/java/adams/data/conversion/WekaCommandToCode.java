
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
 * WekaCommandToCode.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import weka.core.code.Converter;
import weka.core.code.JavaString;

/**
 <!-- globalinfo-start -->
 * Applies a commandline converter to the incoming commandline to generate code.<br>
 * Uses the following project:<br>
 * https:&#47;&#47;github.com&#47;fracpete&#47;command-to-code-weka-package
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;weka.core.code.Converter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The convert to use.
 * &nbsp;&nbsp;&nbsp;default: weka.core.code.JavaString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCommandToCode
  extends AbstractStringConversion {

  private static final long serialVersionUID = -8251955083601155771L;

  /** the converter to use. */
  protected Converter m_Converter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies a commandline converter to the incoming commandline to "
	+ "generate code.\n"
	+ "Uses the following project:\n"
	+ "https://github.com/fracpete/command-to-code-weka-package";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "converter", "converter",
      new JavaString());
  }

  /**
   * Sets the converter to use.
   *
   * @param value 	the converter
   */
  public void setConverter(Converter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the converter to use.
   *
   * @return		the converter
   */
  public Converter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String converterTipText() {
    return "The convert to use.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	input;

    input = (String) m_Input;
    if (!m_Converter.handles(input))
      throw new IllegalStateException("Converter '" + m_Converter.getClass().getName() + "' does not handle command: " + input);

    return m_Converter.convert(input);
  }
}
