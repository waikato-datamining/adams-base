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
 * PdfProcletWithAbsolutePosition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.io.PdfFont;

import java.awt.Color;

/**
 * Interface for PDF proclets that allow absolute positioning.
 * From bottom-left corner.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PdfProcletWithFilenameOutput
  extends PdfProclet {

  /**
   * Sets whether to output the filename as well.
   *
   * @param value	if true then the filename gets added as well
   */
  public void setAddFilename(boolean value);

  /**
   * Returns whether to output the filename as well.
   *
   * @return 		true if the filename gets added as well
   */
  public boolean getAddFilename();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addFilenameTipText();

  /**
   * Sets the font to use for adding the filename header.
   *
   * @param value	the font
   */
  public void setFontFilename(PdfFont value);

  /**
   * Returns the font to use for adding the filename header.
   *
   * @return 		the font
   */
  public PdfFont getFontFilename();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontFilenameTipText();

  /**
   * Sets the color to use for adding the filename header.
   *
   * @param value	the color
   */
  public void setColorFilename(Color value);

  /**
   * Returns the color to use for adding the filename header.
   *
   * @return 		the color
   */
  public Color getColorFilename();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorFilenameTipText();
}
