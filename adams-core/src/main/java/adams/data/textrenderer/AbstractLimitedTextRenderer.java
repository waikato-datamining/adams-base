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
 * SpreadSheetTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

/**
 * Ancestor for renderers that can limit the output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLimitedTextRenderer
  extends AbstractTextRenderer {

  private static final long serialVersionUID = 2413293721997389467L;

  public static final String DOTS = "...";

  /** the maximum to render. */
  protected int m_Limit;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "limit", "limit",
      getDefaultLimit(), getMinLimit(), getMaxLimit());
  }

  /**
   * Returns the default limit.
   *
   * @return		the default
   */
  protected abstract int getDefaultLimit();

  /**
   * Returns the minimum limit.
   *
   * @return		the minimum
   */
  protected abstract Integer getMinLimit();

  /**
   * Returns the maximum limit.
   *
   * @return		the maximum
   */
  protected abstract Integer getMaxLimit();

  /**
   * Sets the maximum of rows to render.
   *
   * @param value	the maximum
   */
  public void setLimit(int value) {
    m_Limit = value;
    reset();
  }

  /**
   * Returns the maximum of rows to render.
   *
   * @return		the maximum
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public abstract String limitTipText();
}
