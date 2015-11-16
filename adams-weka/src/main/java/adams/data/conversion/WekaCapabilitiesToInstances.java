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
 * WekaCapabilitiesToSpreadSheet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.TestInstances;

/**
 <!-- globalinfo-start -->
 * Turns a weka.core.Capabilities object into a Weka dataset filled with random data that is compatible with these capabilities.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of data rows to generate.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCapabilitiesToInstances
  extends AbstractConversion {

  private static final long serialVersionUID = -2595383708535316457L;

  /** the number of data rows to generate. */
  protected int m_NumRows;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a " + Capabilities.class.getName() + " object into a "
      + "Weka dataset filled with random data that is compatible with these "
      + "capabilities.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-rows", "numRows",
	    100, 0, null);
  }

  /**
   * Sets the number of data rows to generate.
   *
   * @param value 	the number of rows
   */
  public void setNumRows(int value) {
    m_NumRows = value;
    reset();
  }

  /**
   * Returns the number of data rows to generate.
   *
   * @return 		the number of rows
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The number of data rows to generate.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Capabilities.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Instances.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instances		result;
    TestInstances	test;
    Capabilities	caps;

    caps   = (Capabilities) m_Input;
    test   = TestInstances.forCapabilities(caps);
    test.setNumInstances(m_NumRows);
    result = test.generate();

    return result;
  }
}
