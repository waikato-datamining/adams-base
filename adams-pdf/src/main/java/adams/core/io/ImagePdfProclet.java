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
 * ImagePdfProclet.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.io.File;

import adams.core.base.BaseString;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;

/**
 <!-- globalinfo-start -->
 * Adds GIF, JPEG and PNG image files.
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
 * <pre>-page-break-before (property: pageBreakBefore)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added before the content of the file is inserted.
 * </pre>
 *
 * <pre>-page-break-after (property: pageBreakAfter)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added after the content of the file is inserted.
 * </pre>
 *
 * <pre>-num-files &lt;int&gt; (property: numFilesPerPage)
 * &nbsp;&nbsp;&nbsp;The number of files to put on a page before adding an automatic page break;
 * &nbsp;&nbsp;&nbsp; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-add-filename (property: addFilename)
 * &nbsp;&nbsp;&nbsp;Whether to add the file name before the actual file content as separate
 * &nbsp;&nbsp;&nbsp;paragraph.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePdfProclet
  extends AbstractPdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the degrees to rotate images. */
  protected int m_Rotation;

  /** the percentage (0-1) to scale the images to. */
  protected double m_Scale;

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
  }

  /**
   * Sets the degrees to rotate the image by.
   *
   * @param value	the degrees
   */
  public void setRotation(int value) {
    if ((value >= 0) && (value <= 360)) {
      m_Rotation = value;
      reset();
    }
    else {
      System.err.println("Degrees must be from 0 to 360!");
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
    if ((value >= 0) && (value <= 1)) {
      m_Scale = value;
      reset();
    }
    else {
      System.err.println("Scale must satisfy 0 <= x <= 1!");
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
   * @param doc		the PDF document to add the file content to
   * @param state	the current document state
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(Document doc, DocumentState state, File file) throws Exception {
    boolean	result;
    Image	image;
    float	scale;

    result = addFilename(doc, state, file);
    if (!result)
      return result;

    image = Image.getInstance(file.getAbsolutePath());
    if (m_Rotation != 0) {
      image.setRotationDegrees(m_Rotation);
      image.rotate();
    }
    if (m_Scale > 0) {
      scale = (float) m_Scale;
      image.scaleToFit(
	  doc.getPageSize().getWidth()*scale,
	  doc.getPageSize().getHeight()*scale);
    }
    result = doc.add(image);
    if (result)
      state.contentAdded();

    return result;
  }
}
