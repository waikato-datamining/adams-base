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
 * Brightness.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.dcraw;

import org.im4java.core.DCRAWOperation;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Divides the white level by the provided level.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-level &lt;double&gt; (property: level)
 * &nbsp;&nbsp;&nbsp;The level to divide the white level by.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Brightness
  extends AbstractDcrawSimpleOperation {

  /** for serialization. */
  private static final long serialVersionUID = 3529048936510645338L;

  /** the factor to divide the white level by. */
  protected double m_Level;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Divides the white level by the provided level.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "level", "level",
	    1.0, 0.0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "level", m_Level, "level: ");
  }

  /**
   * Sets the level to divide the white level by.
   *
   * @param value	the level
   */
  public void setLevel(double value) {
    if (value > 0) {
      m_Level = value;
      reset();
    }
    else {
      getLogger().warning("Level must be > 0, provided: " + value);
    }
  }

  /**
   * Returns the level to divide the white level by.
   *
   * @return		the level
   */
  public double getLevel() {
    return m_Level;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String levelTipText() {
    return "The level to divide the white level by.";
  }

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  @Override
  public void addOperation(DCRAWOperation op) {
    op.brightness(m_Level);
  }
}
