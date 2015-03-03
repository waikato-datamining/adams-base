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
 * AbstractLimitedTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core.axis;

/**
 * Ancestor for tick generators that only generate a limited number of ticks.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLimitedTickGenerator
  extends AbstractTickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 7324989157055747347L;
  
  /** the number of ticks to display. */
  protected int m_NumTicks;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"num-ticks", "numTicks",
        getDefaultNumTicks(), getMinNumTicks(), null);
  }

  /**
   * Return the default number of ticks to generate.
   * 
   * @return		the default number
   */
  protected int getDefaultNumTicks() {
    return 10;
  }

  /**
   * Return the minimum number of ticks to generate.
   * 
   * @return		the minimum
   */
  protected int getMinNumTicks() {
    return 1;
  }
  
  /**
   * Sets the number of ticks to display along the axis.
   *
   * @param value	the number of ticks
   */
  public void setNumTicks(int value) {
    if (value >= getMinNumTicks()) {
      m_NumTicks = value;
      reset();
    }
    else {
      getLogger().warning("Number of ticks must be >=" + getMinNumTicks() + ", provided: " + value);
    }
  }

  /**
   * Returns the number of ticks currently displayed.
   *
   * @return		the number of ticks
   */
  public int getNumTicks() {
    return m_NumTicks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numTicksTipText() {
    return "The number of ticks to display along the axis.";
  }
}
