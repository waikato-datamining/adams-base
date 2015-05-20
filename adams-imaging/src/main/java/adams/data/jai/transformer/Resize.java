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
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Resizes the image to predefined width and height.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;double&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width to resize the image to; use -1 to use original width; use (0-1
 * &nbsp;&nbsp;&nbsp;) for percentage.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 * 
 * <pre>-height &lt;double&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height to resize the image to; use -1 to use original height; use (0
 * &nbsp;&nbsp;&nbsp;-1) for percentage.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 * 
 * <pre>-interpolation-type &lt;NEAREST|BILINEAR|BICUBIC|BICUBIC2&gt; (property: interpolationType)
 * &nbsp;&nbsp;&nbsp;The type of interpolation to perform.
 * &nbsp;&nbsp;&nbsp;default: BICUBIC
 * </pre>
 * 
 * <pre>-num-subsample-bits &lt;int&gt; (property: numSubsampleBits)
 * &nbsp;&nbsp;&nbsp;The number of bits to use for precision when subsampling.
 * &nbsp;&nbsp;&nbsp;default: 8
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Resize
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7139209460998569352L;

  /**
   * Type of interpolaction.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InterpolationType {
    /** nearest. */
    NEAREST,
    /** bilinear. */
    BILINEAR,
    /** bicubic. */
    BICUBIC,
    /** bicubic2. */
    BICUBIC2
  }
  
  /** the new width. */
  protected double m_Width;

  /** the new height. */
  protected double m_Height;
  
  /** the type of interpolation to perform. */
  protected InterpolationType m_InterpolationType;

  /** the number of subsample bits. */
  protected int m_NumSubsampleBits;

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
	    -1.0, -1.0, null);

    m_OptionManager.add(
	    "height", "height",
	    -1.0, -1.0, null);

    m_OptionManager.add(
	    "interpolation-type", "interpolationType",
	    InterpolationType.BICUBIC);

    m_OptionManager.add(
	    "num-subsample-bits", "numSubsampleBits",
	    8, 1, null);
  }

  /**
   * Sets the width to resize to.
   *
   * @param value 	the width, -1 uses original width
   */
  public void setWidth(double value) {
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
  public double getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width to resize the image to; use -1 to use original width; use (0-1) for percentage.";
  }

  /**
   * Sets the height to resize to.
   *
   * @param value 	the height, -1 uses original height
   */
  public void setHeight(double value) {
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
  public double getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height to resize the image to; use -1 to use original height; use (0-1) for percentage.";
  }

  /**
   * Sets the type of interpolation to use.
   *
   * @param value 	the type
   */
  public void setInterpolationType(InterpolationType value) {
    m_InterpolationType = value;
    reset();
  }

  /**
   * Returns the type of interpolation in use.
   *
   * @return 		the type
   */
  public InterpolationType getInterpolationType() {
    return m_InterpolationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String interpolationTypeTipText() {
    return "The type of interpolation to perform.";
  }


  /**
   * Sets the precision for subsampling in bits.
   *
   * @param value 	the number of bits
   */
  public void setNumSubsampleBits(int value) {
    if (value > 0) {
      m_NumSubsampleBits = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of subsample bits must be >0, provided: " + value);
    }
  }

  /**
   * Returns the precision for subsampling in bits.
   *
   * @return 		the number of bits
   */
  public int getNumSubsampleBits() {
    return m_NumSubsampleBits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSubsampleBitsTipText() {
    return "The number of bits to use for precision when subsampling.";
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
    BufferedImage		im;
    ParameterBlock 		params;
    RenderedOp 			imNew;

    im = img.getImage();

    params = new ParameterBlock();
    params.addSource(im);
    if (m_Width == -1)
      params.add((float) 1.0F);
    else if ((m_Width >= 0) && (m_Width <= 1))
      params.add((float) m_Width);  // x percentage
    else
      params.add((float) ((double) m_Width  / (double) img.getWidth()));    // x scale factor
    if (m_Height == -1)
      params.add((float) 1.0F);
    else if ((m_Height >= 0) && (m_Height <= 1))
      params.add((float) m_Height);  // x percentage
    else
      params.add((float) ((double) m_Height / (double) img.getHeight()));   // y scale factor
    params.add(0.0F);  // x translate
    params.add(0.0F);  // y translate
    switch (m_InterpolationType) {
      case NEAREST:
	params.add(new InterpolationNearest());
	break;
      case BILINEAR:
	params.add(new InterpolationBilinear(m_NumSubsampleBits));
	break;
      case BICUBIC:
	params.add(new InterpolationBicubic(m_NumSubsampleBits));
	break;
      case BICUBIC2:
	params.add(new InterpolationBicubic2(m_NumSubsampleBits));
	break;
      default:
	throw new IllegalStateException("Unhandled interpolation type: " + m_InterpolationType);
    }
    
    imNew = JAI.create("scale", params);
    
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(imNew.getAsBufferedImage());
    
    return result;
  }
}
