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
 * Image.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseString;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.BufferedImageSupporter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Adds GIF, JPEG and PNG image files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-regexp-filename &lt;adams.core.base.BaseRegExp&gt; (property: regExpFilename)
 * &nbsp;&nbsp;&nbsp;The regular expression that the filename must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-add-filename &lt;boolean&gt; (property: addFilename)
 * &nbsp;&nbsp;&nbsp;Whether to add the file name before the actual file content as separate
 * &nbsp;&nbsp;&nbsp;paragraph.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-font-filename &lt;adams.core.io.PdfFont&gt; (property: fontFilename)
 * &nbsp;&nbsp;&nbsp;The font to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Bold-12
 * </pre>
 *
 * <pre>-color-filename &lt;java.awt.Color&gt; (property: colorFilename)
 * &nbsp;&nbsp;&nbsp;The color to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-page-break-before &lt;boolean&gt; (property: pageBreakBefore)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added before the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-page-break-after &lt;boolean&gt; (property: pageBreakAfter)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added after the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-files &lt;int&gt; (property: numFilesPerPage)
 * &nbsp;&nbsp;&nbsp;The number of files to put on a page before adding an automatic page break;
 * &nbsp;&nbsp;&nbsp; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-rotation &lt;int&gt; (property: rotation)
 * &nbsp;&nbsp;&nbsp;The rotation in degrees.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 360
 * </pre>
 *
 * <pre>-scale &lt;double&gt; (property: scale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the image, ie, scaling it to the page dimensions;
 * &nbsp;&nbsp;&nbsp;use 0 to turn scaling off.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-use-absolute-position &lt;boolean&gt; (property: useAbsolutePosition)
 * &nbsp;&nbsp;&nbsp;If enabled, the absolute position is used (from bottom-left corner).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-x &lt;float&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The absolute X position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-y &lt;float&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The absolute Y position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-scaling-type &lt;SCALE_TO_FIT|SCALE_PERCENT&gt; (property: scalingType)
 * &nbsp;&nbsp;&nbsp;The type of scaling to use.
 * &nbsp;&nbsp;&nbsp;default: SCALE_TO_FIT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Image
  extends AbstractPdfProcletWithPageBreaks
  implements PdfProcletWithOptionalAbsolutePosition {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /**
   * The type of scaling to use.
   */
  public enum ScalingType {
    SCALE_TO_FIT,
    SCALE_PERCENT,
  }

  /** the degrees to rotate images. */
  protected int m_Rotation;

  /** the percentage (0-1) to scale the images to. */
  protected double m_Scale;

  /** whether to use absolute position. */
  protected boolean m_UseAbsolutePosition;

  /** the absolute X position. */
  protected float m_X;

  /** the absolute Y position. */
  protected float m_Y;

  /** the type of scaling to use. */
  protected ScalingType m_ScalingType;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  public String globalInfo() {
    return "Adds GIF, JPEG and PNG image files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "rotation", "rotation",
      0, 0, 360);

    m_OptionManager.add(
      "scale", "scale",
      1.0, 0.0, 1.0);

    m_OptionManager.add(
      "use-absolute-position", "useAbsolutePosition",
      false);

    m_OptionManager.add(
      "x", "X",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y", "Y",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "scaling-type", "scalingType",
      ScalingType.SCALE_TO_FIT);
  }

  /**
   * Sets the degrees to rotate the image by.
   *
   * @param value	the degrees
   */
  public void setRotation(int value) {
    if (getOptionManager().isValid("rotation", value)) {
      m_Rotation = value;
      reset();
    }
  }

  /**
   * Returns the degrees by which to rotate the images.
   *
   * @return 		the degrees
   */
  public int getRotation() {
    return m_Rotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rotationTipText() {
    return "The rotation in degrees.";
  }

  /**
   * Sets the scale factor (0-1) for images based on the page size.
   *
   * @param value	the scale factor, 0 to turn off scaling
   */
  public void setScale(double value) {
    if (getOptionManager().isValid("scale", value)) {
      m_Scale = value;
      reset();
    }
  }

  /**
   * Returns the scale factor (0-1) for images based on the page size.
   *
   * @return 		the scale factor
   */
  public double getScale() {
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
   * Sets whether to use absolute positioning (from bottom-left corner).
   *
   * @param value	true if absolute
   */
  public void setUseAbsolutePosition(boolean value) {
    m_UseAbsolutePosition = value;
    reset();
  }

  /**
   * Returns whether absolute positioning is used (from bottom-left corner).
   *
   * @return		true if absolute
   */
  public boolean getUseAbsolutePosition() {
    return m_UseAbsolutePosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsolutePositionTipText() {
    return "If enabled, the absolute position is used (from bottom-left corner).";
  }

  /**
   * Sets the absolute X position.
   *
   * @param value	the X position
   */
  public void setX(float value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the absolute X position.
   *
   * @return		the X position
   */
  public float getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The absolute X position.";
  }

  /**
   * Sets the absolute Y position.
   *
   * @param value	the Y position
   */
  public void setY(float value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the absolute Y position.
   *
   * @return		the Y position
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The absolute Y position.";
  }

  /**
   * Sets the type of scaling to use.
   *
   * @param value	the type
   */
  public void setScalingType(ScalingType value) {
    m_ScalingType = value;
    reset();
  }

  /**
   * Returns the type of scaling to use.
   *
   * @return 		the type
   */
  public ScalingType getScalingType() {
    return m_ScalingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scalingTypeTipText() {
    return "The type of scaling to use.";
  }

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  public BaseString[] getExtensions() {
    return new BaseString[]{
      new BaseString("gif"),
      new BaseString("jpg"),
      new BaseString("jpeg"),
      new BaseString("png")
    };
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param img		the image to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, BufferedImage img) throws Exception {
    boolean			result;
    float			scale;
    com.itextpdf.text.Image	image;

    image = com.itextpdf.text.Image.getInstance(img, Color.WHITE);
    if (m_Rotation != 0) {
      image.setRotationDegrees(m_Rotation);
      image.rotate();
    }
    if (m_Scale > 0) {
      scale = (float) m_Scale;
      switch (m_ScalingType) {
	case SCALE_TO_FIT:
	  image.scaleToFit(
	    generator.getDocument().getPageSize().getWidth() * scale,
	    generator.getDocument().getPageSize().getHeight() * scale);
	  break;
	case SCALE_PERCENT:
	  image.scalePercent(scale * 100.0f);
	  break;
	default:
	  throw new IllegalStateException("Unhandled scaling type: " + m_ScalingType);
      }
    }
    if (m_UseAbsolutePosition)
      image.setAbsolutePosition(m_X, m_Y);
    result = addElement(generator, image);

    return result;
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, File file) throws Exception {
    boolean			result;
    BufferedImageContainer 	cont;

    result = addFilename(generator, file);
    if (result) {
      cont = BufferedImageHelper.read(file);
      if (cont == null)
	return false;
      result = doProcess(generator, cont.toBufferedImage());
    }

    return result;
  }

  /**
   * Whether the processor can handle this particular object.
   *
   * @param generator	the context
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean canProcess(PDFGenerator generator, Object obj) {
    return (obj instanceof BufferedImage) || (obj instanceof BufferedImageSupporter);
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, Object obj) throws Exception {
    if (obj instanceof BufferedImage)
      return doProcess(generator, (BufferedImage) obj);
    else if (obj instanceof BufferedImageSupporter)
      return doProcess(generator, ((BufferedImageSupporter) obj).toBufferedImage());
    else
      return false;
  }
}
