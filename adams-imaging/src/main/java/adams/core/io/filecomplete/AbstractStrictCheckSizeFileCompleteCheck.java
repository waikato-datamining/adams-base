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
 * JpegIsComplete.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

/**
 * Ancestor for checks that look for EOF markers that can limit the number of bytes to read from the end of the
 * file used to look for the EOF marker.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStrictCheckSizeFileCompleteCheck
  extends AbstractStrictFileCompleteCheck {

  private static final long serialVersionUID = -3565557772681364712L;

  /** the number of bytes to read from the end of the file to look for the EOF marker. */
  protected int m_CheckSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "check-size", "checkSize",
      getDefaultCheckSize(), getMinCheckSize(), null);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String strictTipText() {
    return "Whether to be strict or allow trailing junk data.";
  }

  /**
   * Returns the default check size to use.
   *
   * @return		the default
   */
  protected int getDefaultCheckSize() {
    return 100;
  }

  /**
   * Returns the minimally allowed check size.
   *
   * @return		the minimum
   */
  protected abstract int getMinCheckSize();

  /**
   * Sets the number of bytes to read from the back of the file (in non-strict mode) to check for EOF marker.
   *
   * @param value	the number of bytes
   */
  public void setCheckSize(int value) {
    if (getOptionManager().isValid("checkSize", value)) {
      m_CheckSize = value;
      reset();
    }
  }

  /**
   * Returns the number of bytes to read from the back of the file (in non-strict mode) to check for EOF marker.
   *
   * @return		the number of bytes
   */
  public int getCheckSize() {
    return m_CheckSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkSizeTipText() {
    return "The number of bytes to read from the back of the file (in non-strict mode) to check for EOF marker.";
  }
}
