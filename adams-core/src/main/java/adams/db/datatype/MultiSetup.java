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
 * MultiSetup.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db.datatype;

import java.sql.Connection;

/**
 <!-- globalinfo-start -->
 * Combines (and executes) multiple setups.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-setup &lt;adams.db.datatype.AbstractDataTypeSetup&gt; [-setup ...] (property: setups)
 * &nbsp;&nbsp;&nbsp;The data type setups to execute.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiSetup
  extends AbstractDataTypeSetup {

  /** for serialization. */
  private static final long serialVersionUID = -9152587689252144060L;

  /** the setups. */
  protected AbstractDataTypeSetup[] m_Setups;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines (and executes) multiple setups.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "setup", "setups",
	    new AbstractDataTypeSetup[0]);
  }

  /**
   * Sets the setups to execute.
   *
   * @param value	the setups
   */
  public void setSetups(AbstractDataTypeSetup[] value) {
    m_Setups = value;
    reset();
  }

  /**
   * Returns the setups to execute.
   *
   * @return 		the setups
   */
  public AbstractDataTypeSetup[] getSetups() {
    return m_Setups;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String setupsTipText() {
    return "The data type setups to execute.";
  }

  /**
   * Configures the data types.
   * 
   * @return		always null
   */
  @Override
  public String setupDataTypes(Connection conn) {
    String	result;
    int		i;
    
    result = null;
    
    for (i = 0; i < m_Setups.length; i++) {
      result = m_Setups[i].setupDataTypes(conn);
      if (result != null) {
	result = "Setup #" + (i+1) + " failed: " + result;
	break;
      }
    }
    
    return result;
  }
}
