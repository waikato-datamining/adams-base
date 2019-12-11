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

/*
 * AbstractLineNumberedLimitedTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

/**
 * Ancestor for text renderers that can output line numbers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLineNumberedLimitedTextRenderer
  extends AbstractLimitedTextRenderer
  implements LineNumberTextRenderer {

  private static final long serialVersionUID = 5957297452898236485L;

  /** whether to output line numbers. */
  protected boolean m_OutputLineNumbers;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-line-numbers", "outputLineNumbers",
      getDefaultOutputLineNumbers());
  }

  /**
   * Returns the default value for outputting line numbers.
   *
   * @return		the default
   */
  protected boolean getDefaultOutputLineNumbers() {
    return true;
  }

  /**
   * Sets whether to output line numbers.
   *
   * @param value	true if to output
   */
  public void setOutputLineNumbers(boolean value) {
    m_OutputLineNumbers = value;
    reset();
  }

  /**
   * Returns whether to output line numbers.
   *
   * @return		true if to output
   */
  public boolean getOutputLineNumbers() {
    return m_OutputLineNumbers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String outputLineNumbersTipText() {
    return "If enabled, line numbers are output as prefix to each line.";
  }
}
