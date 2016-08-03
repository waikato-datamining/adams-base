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
 * Flip.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import adams.data.FlipDirection;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Flips an image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-direction &lt;HORIZONTAL|VERTICAL&gt; (property: direction)
 * &nbsp;&nbsp;&nbsp;The flip direction.
 * &nbsp;&nbsp;&nbsp;default: HORIZONTAL
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Flip
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the flip direction. */
  protected FlipDirection m_Direction;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flips an image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "direction", "direction",
	    FlipDirection.HORIZONTAL);
  }

  /**
   * Sets the flip direction.
   *
   * @param value	the direction
   */
  public void setDirection(FlipDirection value) {
    m_Direction = value;
    reset();
  }

  /**
   * Returns the flip direction.
   *
   * @return		the direction
   */
  public FlipDirection getDirection() {
    return m_Direction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String directionTipText() {
    return "The flip direction.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   * <br><br>
   * Original code for rotation taken from <a href="http://asserttrue.blogspot.com/2010/01/image-rotation-in-8-lines-using-java.html" target="_blank">here</a>,
   * which was placed in public domain by Kas Thomas.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage 		inverted;
    TransposeType		desc;
    
    if (m_Direction == FlipDirection.HORIZONTAL)
      desc = TransposeDescriptor.FLIP_HORIZONTAL;
    else
      desc = TransposeDescriptor.FLIP_VERTICAL;
    inverted  = JAI.create("transpose", img.toBufferedImage(), desc).getAsBufferedImage();
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(inverted);

    return result;
  }
}
