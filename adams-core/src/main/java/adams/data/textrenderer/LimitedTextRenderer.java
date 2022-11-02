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
 * LimitedTextRenderer.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

/**
 * Ancestor for renderers that can limit the output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LimitedTextRenderer
  extends TextRenderer {

  public static final String DOTS = "...";

  /**
   * Returns the default limit.
   *
   * @return		the default
   */
  public int getDefaultLimit();

  /**
   * Returns the minimum limit.
   *
   * @return		the minimum
   */
  public Integer getMinLimit();

  /**
   * Returns the maximum limit.
   *
   * @return		the maximum
   */
  public Integer getMaxLimit();

  /**
   * Sets the maximum of rows to render.
   *
   * @param value	the maximum
   */
  public void setLimit(int value);

  /**
   * Returns the maximum of rows to render.
   *
   * @return		the maximum
   */
  public int getLimit();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String limitTipText();

  /**
   * Renders the object as text with no limits.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  public String renderUnlimited(Object obj);
}
