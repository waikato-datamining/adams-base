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
 * Image.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfstamp;

import adams.core.io.PlaceholderFile;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfStamper;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Places the image at the specified location.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-pages &lt;adams.core.Range&gt; (property: pages)
 * &nbsp;&nbsp;&nbsp;The pages to stamp.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-image &lt;adams.core.io.PlaceholderFile&gt; (property: image)
 * &nbsp;&nbsp;&nbsp;The image to use as stamp.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-x &lt;float&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-y &lt;float&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-rotation &lt;float&gt; (property: rotation)
 * &nbsp;&nbsp;&nbsp;The rotation in degrees, counterclockwise.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 360.0
 * </pre>
 * 
 * <pre>-scale &lt;float&gt; (property: scale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the image, ie, scaling it to the page dimensions; 
 * &nbsp;&nbsp;&nbsp;use 0 to turn scaling off.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Image
  extends AbstractPageRangeStamper {

  private static final long serialVersionUID = -2687932798037862212L;

  /** the image. */
  protected PlaceholderFile m_Image;

  /** the x position. */
  protected float m_X;

  /** the y position. */
  protected float m_Y;

  /** the rotation. */
  protected float m_Rotation;

  /** the percentage (0-1) to scale the images to. */
  protected float m_Scale;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Places the image at the specified location.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image", "image",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "x", "X",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y", "Y",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "rotation", "rotation",
      0.0f, 0.0f, 360.0f);

    m_OptionManager.add(
      "scale", "scale",
      1.0f, 0.0f, 1.0f);
  }

  /**
   * Sets the image to insert.
   *
   * @param value	the image filename
   */
  public void setImage(PlaceholderFile value) {
    m_Image = value;
    reset();
  }

  /**
   * Returns the image to insert.
   *
   * @return 		the image filename
   */
  public PlaceholderFile getImage() {
    return m_Image;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String imageTipText() {
    return "The image to use as stamp.";
  }

  /**
   * Sets the X position for the text.
   *
   * @param value	the x position
   */
  public void setX(float value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the X position for the text.
   *
   * @return 		the x position
   */
  public float getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position.";
  }

  /**
   * Sets the Y position for the text.
   *
   * @param value	the y position
   */
  public void setY(float value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the Y position for the text.
   *
   * @return 		the y position
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position.";
  }

  /**
   * Sets the rotation for the text.
   *
   * @param value	the rotation (degrees counterclockwise)
   */
  public void setRotation(float value) {
    m_Rotation = value;
    reset();
  }

  /**
   * Returns the rotation for the text.
   *
   * @return 		the rotation (degrees counterclockwise)
   */
  public float getRotation() {
    return m_Rotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String rotationTipText() {
    return "The rotation in degrees, counterclockwise.";
  }

  /**
   * Sets the scale factor (0-1) for images based on the page size.
   *
   * @param value	the scale factor, 0 to turn off scaling
   */
  public void setScale(float value) {
    if ((value >= 0) && (value <= 1)) {
      m_Scale = value;
      reset();
    }
    else {
      getLogger().warning("Scale must satisfy 0 <= x <= 1!");
    }
  }

  /**
   * Returns the scale factor (0-1) for images based on the page size.
   *
   * @return 		the scale factor
   */
  public float getScale() {
    return m_Scale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleTipText() {
    return
        "The scaling factor for the image, ie, scaling it to the page "
      + "dimensions; use 0 to turn scaling off.";
  }

  /**
   * Performs the actual stamping.
   *
   * @param stamper	the stamper to use
   * @param page	the page to apply the stamp to
   */
  protected void doStamp(PdfStamper stamper, int page) {
    PdfContentByte 		canvas;
    com.itextpdf.text.Image 	image;

    canvas = stamper.getOverContent(page + 1);
    try {
      image = com.itextpdf.text.Image.getInstance(m_Image.getAbsolutePath());
      image.setAbsolutePosition(m_X, m_Y);
      if (m_Rotation != 0) {
	image.setRotationDegrees(m_Rotation);
	image.rotate();
      }
      if (m_Scale > 0) {
	image.scaleToFit(
	  stamper.getReader().getPageSize(page + 1).getWidth()*m_Scale,
	  stamper.getReader().getPageSize(page + 1).getHeight()*m_Scale);
      }
      canvas.addImage(image);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to insert image: " + m_Image, e);
    }
  }
}
