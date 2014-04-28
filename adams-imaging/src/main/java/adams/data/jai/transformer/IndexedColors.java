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
 * IndexedColors.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.image.BufferedImage;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ColorQuantizerDescriptor;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Turns an RGB image into one with an indexed color palette.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-colors &lt;int&gt; (property: numColors)
 * &nbsp;&nbsp;&nbsp;The maximum number of colors in the palette.
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-color-quantizer &lt;MEDIANCUT|NEUQUANT|OCTTREE&gt; (property: colorQuantizer)
 * &nbsp;&nbsp;&nbsp;The type of color quantizer to use.
 * &nbsp;&nbsp;&nbsp;default: MEDIANCUT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IndexedColors
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * The types of color quantizers.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ColorQuantizer {
    MEDIANCUT,
    NEUQUANT,
    OCTTREE
  }

  /** the number of colors. */
  protected int m_NumColors;

  /** the color quantizer. */
  protected ColorQuantizer m_ColorQuantizer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an RGB image into one with an indexed color palette.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-colors", "numColors",
	    256, 1, null);

    m_OptionManager.add(
	    "color-quantizer", "colorQuantizer",
	    ColorQuantizer.MEDIANCUT);
  }

  /**
   * Sets the maximum number of colors.
   *
   * @param value	the maximum
   */
  public void setNumColors(int value) {
    if (value > 0) {
      m_NumColors = value;
      reset();
    }
    else {
      getLogger().severe("Number of colors must be >0, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of colors.
   *
   * @return		the maximum
   */
  public int getNumColors() {
    return m_NumColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numColorsTipText() {
    return "The maximum number of colors in the palette.";
  }

  /**
   * Sets the type of color quantizer to use.
   *
   * @param value	the type
   */
  public void setColorQuantizer(ColorQuantizer value) {
    m_ColorQuantizer = value;
    reset();
  }

  /**
   * Returns the type of color quantizer in use.
   *
   * @return		the type
   */
  public ColorQuantizer getColorQuantizer() {
    return m_ColorQuantizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorQuantizerTipText() {
    return "The type of color quantizer to use.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage		image;
    RenderedOp			renderedOp;

    result = new BufferedImageContainer[1];

    image      = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    renderedOp = ColorQuantizerDescriptor.create(
	image, 
	ColorQuantizerDescriptor.MEDIANCUT, 
	m_NumColors, 
	null, 
	null, 
	null, 
	null, 
	null);

    result[0]  = (BufferedImageContainer) img.getHeader();
    result[0].setImage(renderedOp.getRendering().getAsBufferedImage());

    return result;
  }
}
