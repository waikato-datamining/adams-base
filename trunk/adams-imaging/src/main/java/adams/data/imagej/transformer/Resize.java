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
 * Resize.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.transformer;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import adams.data.imagej.ImagePlusContainer;

/**
 <!-- globalinfo-start -->
 * Resizes the image to predefined width and height.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width to resize the image to; use -1 to use original width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height to resize the image to; use -1 to use original height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Resize
  extends AbstractImageJTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7139209460998569352L;

  /** the new width. */
  protected int m_Width;

  /** the new height. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Resizes the image to predefined width and height.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    -1, -1, null);

    m_OptionManager.add(
	    "height", "height",
	    -1, -1, null);
  }

  /**
   * Sets the width to resize to.
   *
   * @param value 	the width, -1 uses original width
   */
  public void setWidth(int value) {
    if (value >= -1) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Width must be -1 (current width) or greater, provided: " + value);
    }
  }

  /**
   * Returns the width to resize to.
   *
   * @return 		the width, -1 if original width is used
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width to resize the image to; use -1 to use original width.";
  }

  /**
   * Sets the height to resize to.
   *
   * @param value 	the height, -1 uses original height
   */
  public void setHeight(int value) {
    if (value >= -1) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Height must be -1 (current height) or greater, provided: " + value);
    }
  }

  /**
   * Returns the height to resize to.
   *
   * @return 		the height, -1 if original height is used
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height to resize the image to; use -1 to use original height.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected ImagePlusContainer[] doTransform(ImagePlusContainer img) {
    ImagePlusContainer[]	result;
    ImagePlus			im;
    ImagePlus			imNew;
    ImageProcessor		ip;
    ImageProcessor		ipNew;

    im = img.getImage();
    ip = im.getProcessor();
    ip.setInterpolate(true);
    ipNew = ip.resize(((m_Width == -1) ? im.getWidth() : m_Width), ((m_Height == -1) ? im.getHeight() : m_Height));
    imNew = new ImagePlus(im.getTitle() + "-resized[w=" + m_Width + ",h=" + m_Height + "]", ipNew);

    result    = new ImagePlusContainer[1];
    result[0] = (ImagePlusContainer) img.getHeader();
    result[0].setImage(imNew);
    
    return result;
  }
}
