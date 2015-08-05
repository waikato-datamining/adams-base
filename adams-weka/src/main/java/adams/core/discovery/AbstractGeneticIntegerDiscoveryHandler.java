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
 * AbstractGeneticIntegerDiscoveryHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

/**
 * Ancestor for genetic discovery handlers that handle integer properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticIntegerDiscoveryHandler
  extends AbstractGeneticDiscoveryHandler {

  private static final long serialVersionUID = -5442076178374142588L;

  /** the minimum. */
  protected int m_Minimum;

  /** the maximum. */
  protected int m_Maximum;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "minimum", "minimum",
      getDefaultMinimum());

    m_OptionManager.add(
      "maximum", "maximum",
      getDefaultMaximum());
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  protected abstract int getDefaultMinimum();

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(int value) {
    if (getOptionManager().isValid("minimum", value)) {
      m_Minimum = value;
      reset();
    }
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public int getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum to use.";
  }

  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  protected abstract int getDefaultMaximum();

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(int value) {
    if (getOptionManager().isValid("maximum", value)) {
      m_Maximum = value;
      reset();
    }
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public int getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum to use.";
  }
}
