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
 * Invert.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;

import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Inverts the image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class Invert
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inverts the image.";
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
    ParameterBlock		pb;
    BufferedImage		inverted;

    result    = new BufferedImageContainer[1];
    pb        = new ParameterBlock().addSource(img.toBufferedImage())
	.add(null)
	.add(null)
	.add(null)
	.add(null)
	.add(null);
    inverted  = JAI.create("Invert", pb, null).getAsBufferedImage();
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(inverted);

    return result;
  }
}
