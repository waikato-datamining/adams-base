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
 * ImageContainerToSpreadSheet.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns the image of an image container into a spreadsheet for viewing the pixel values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-remove-alpha-channel &lt;boolean&gt; (property: removeAlphaChannel)
 * &nbsp;&nbsp;&nbsp;If enabled, the alpha channel gets removed from the pixel value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageContainerToSpreadSheet
  extends AbstractConversion {

  private static final long serialVersionUID = -2365914378679923351L;

  /** whether to remove the alpha channel. */
  protected boolean m_RemoveAlphaChannel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the image of an image container into a spreadsheet for viewing the pixel values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remove-alpha-channel", "removeAlphaChannel",
      false);
  }

  /**
   * Sets whether to remove the alpha channel.
   *
   * @param value	true if to remove
   */
  public void setRemoveAlphaChannel(boolean value) {
    m_RemoveAlphaChannel = value;
    reset();
  }

  /**
   * Returns whether to remove the alpha channel.
   *
   * @return 		true if to remove
   */
  public boolean getRemoveAlphaChannel() {
    return m_RemoveAlphaChannel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeAlphaChannelTipText() {
    return "If enabled, the alpha channel gets removed from the pixel value.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractImageContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet			result;
    Row				row;
    AbstractImageContainer	cont;
    int[][]			matrix;
    int				i;
    int				n;

    cont   = (AbstractImageContainer) m_Input;
    matrix = BufferedImageHelper.getPixelRaster(cont.toBufferedImage());
    result = new DefaultSpreadSheet();

    // header
    row    = result.getHeaderRow();
    for (i = 0; i < cont.getWidth(); i++)
      row.addCell("" + i).setContentAsString("" + (i+1));

    // data
    for (n = 0; n < matrix.length; n++) {
      row = result.addRow();
      for (i = 0; i < matrix[n].length; i++) {
        if (m_RemoveAlphaChannel)
          row.addCell("" + i).setContent(matrix[n][i] & 0x00FFFFFF);
        else
          row.addCell("" + i).setContent(matrix[n][i]);
      }
    }

    return result;
  }
}
