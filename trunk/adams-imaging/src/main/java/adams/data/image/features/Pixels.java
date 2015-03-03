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
 * Pixels.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Gets all the pixels of the image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-pixel-type &lt;RGB_SINGLE|RGB_SEPARATE|HSB_SEPARATE|LUMINANCE_STANDARD|LUMINANCE_PERCEIVED1|LUMINANCE_PERCEIVED2&gt; (property: pixelType)
 * &nbsp;&nbsp;&nbsp;The pixel type to use.
 * &nbsp;&nbsp;&nbsp;default: RGB_SINGLE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Pixels
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * The type of pixel to use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PixelType {
    /** single RGB int. */
    RGB_SINGLE,
    /** RGB values separate. */
    RGB_SEPARATE,
    /** HSB values separate. */
    HSB_SEPARATE,
    /** luminance standard. */
    LUMINANCE_STANDARD,
    /** luminance perceived 1. */
    LUMINANCE_PERCEIVED1,
    /** luminance perceived 2. */
    LUMINANCE_PERCEIVED2,
  }
  
  /** how to output the pixels. */
  protected PixelType m_PixelType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Gets all the pixels of the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "pixel-type", "pixelType",
	    PixelType.RGB_SINGLE);
  }

  /**
   * Sets the type of pixel to output.
   *
   * @param value	the type
   */
  public void setPixelType(PixelType value) {
    m_PixelType = value;
    reset();
  }

  /**
   * Returns the type of pixel to output.
   *
   * @return		the type
   */
  public PixelType getPixelType() {
    return m_PixelType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String pixelTypeTipText() {
    return "The pixel type to use.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition		result;
    int				i;
    int				numPixels;

    numPixels = img.getWidth() * img.getHeight();
    result    = new HeaderDefinition();
    
    for (i = 0; i < numPixels; i++) {
      switch (m_PixelType) {
	case RGB_SINGLE:
	case LUMINANCE_STANDARD:
	case LUMINANCE_PERCEIVED1:
	case LUMINANCE_PERCEIVED2:
	  result.add("att_" + (i+1), DataType.NUMERIC);
	  break;
	  
	case RGB_SEPARATE:
	  result.add("att_r_" + (i+1), DataType.NUMERIC);
	  result.add("att_g_" + (i+1), DataType.NUMERIC);
	  result.add("att_b_" + (i+1), DataType.NUMERIC);
	  break;
	  
	case HSB_SEPARATE:
	  result.add("att_h_" + (i+1), DataType.NUMERIC);
	  result.add("att_s_" + (i+1), DataType.NUMERIC);
	  result.add("att_b_" + (i+1), DataType.NUMERIC);
	  break;
	  
	default:
	  throw new IllegalStateException("Unhandled pixel type: " + m_PixelType);
      }
    }

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]	result;
    int[]		pixels;
    int[][]		rgbas;
    int			i;
    float[]		hsb;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    
    switch (m_PixelType) {
      case RGB_SINGLE:
	if (img.getImage().getType() == BufferedImage.TYPE_BYTE_GRAY) {
	  rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	  for (i = 0; i < rgbas.length; i++)
	    result[0].add(rgbas[i][0]);   // R = G = B
	}
	else {
	  pixels = BufferedImageHelper.getPixels(img.getImage());
	  for (i = 0; i < pixels.length; i++)
	    result[0].add(pixels[i]);
	}
	break;
	
      case RGB_SEPARATE:
	rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	for (i = 0; i < rgbas.length; i++) {
	  result[0].add(rgbas[i][0]);
	  result[0].add(rgbas[i][1]);
	  result[0].add(rgbas[i][2]);
	}
	break;
	
      case HSB_SEPARATE:
	hsb   = new float[3];
	rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	for (i = 0; i < rgbas.length; i++) {
	  hsb = Color.RGBtoHSB(rgbas[i][0], rgbas[i][1], rgbas[i][2], hsb);
	  result[0].add(hsb[0]);
	  result[0].add(hsb[1]);
	  result[0].add(hsb[2]);
	}
	break;
	
      case LUMINANCE_STANDARD:
	rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	for (i = 0; i < rgbas.length; i++)
	  result[0].add(rgbas[i][0] * 0.2126 + rgbas[i][1] * 0.7152 + rgbas[i][2] * 0.0722);
	break;
	
      case LUMINANCE_PERCEIVED1:
	rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	for (i = 0; i < rgbas.length; i++)
	  result[0].add(rgbas[i][0] * 0.299 + rgbas[i][1] * 0.587 + rgbas[i][2] * 0.114);
	break;
	
      case LUMINANCE_PERCEIVED2:
	rgbas = BufferedImageHelper.getRGBPixels(img.getImage());
	for (i = 0; i < rgbas.length; i++)
	  result[0].add(Math.sqrt(rgbas[i][0] * rgbas[i][0] * 0.241 + rgbas[i][1] * rgbas[i][1] * 0.691 + rgbas[i][2] * rgbas[i][2] * 0.068));
	break;
	
      default:
	throw new IllegalStateException("Unhandled pixel type: " + m_PixelType);
    }

    return result;
  }
}
