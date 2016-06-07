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
 * PdfProcletWithOptionalAbsolutePosition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

/**
 * Interface for PDF proclets that allow absolute positioning.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13481 $
 */
public interface PdfProcletWithOptionalAbsolutePosition
  extends PdfProcletWithAbsolutePosition {

  /**
   * Sets whether to use absolute positioning.
   *
   * @param value	true if absolute
   */
  public void setUseAbsolutePosition(boolean value);

  /**
   * Returns whether absolute positioning is used.
   *
   * @return		true if absolute
   */
  public boolean getUseAbsolutePosition();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsolutePositionTipText();

  /**
   * Sets the absolute X position.
   *
   * @param value	the X position
   */
  public void setX(float value);

  /**
   * Returns the absolute X position.
   *
   * @return		the X position
   */
  public float getX();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText();

  /**
   * Sets the absolute Y position.
   *
   * @param value	the Y position
   */
  public void setY(float value);

  /**
   * Returns the absolute Y position.
   *
   * @return		the Y position
   */
  public float getY();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText();
}
