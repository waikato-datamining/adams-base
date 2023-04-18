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
 * AbstractStrictFileCompleteCheck.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

/**
 * Ancestor for file checks that
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStrictFileCompleteCheck
  extends AbstractFileCompleteCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /** whether to be strict. */
  protected boolean m_Strict;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "strict", "strict",
      true);
  }

  /**
   * Sets whether to be strict or allow trailing junk data.
   *
   * @param value	true if to be strict
   */
  public void setStrict(boolean value) {
    m_Strict = value;
    reset();
  }

  /**
   * Returns whether to be strict or allow trailing junk data.
   *
   * @return		true if to be strict
   */
  public boolean getStrict() {
    return m_Strict;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String strictTipText();
}
