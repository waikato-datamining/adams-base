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
  *    PDFWriter.java
  *    Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import adams.core.io.FileUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

/**
 <!-- globalinfo-start -->
 * Outputs PDF documents.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to save the image to.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-scaling (property: scalingEnabled)
 * &nbsp;&nbsp;&nbsp;If set to true, then scaling will be used.
 * </pre>
 *
 * <pre>-scale-x &lt;double&gt; (property: XScale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the X-axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-scale-y &lt;double&gt; (property: YScale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-custom-dimensions (property: useCustomDimensions)
 * &nbsp;&nbsp;&nbsp;Whether to use custom dimensions or use the component's ones.
 * </pre>
 *
 * <pre>-custom-width &lt;int&gt; (property: customWidth)
 * &nbsp;&nbsp;&nbsp;The custom width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-custom-height &lt;int&gt; (property: customHeight)
 * &nbsp;&nbsp;&nbsp;The custom height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The background color.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 *
 * <pre>-type &lt;RGB|GRAY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 *
 * <pre>-rotation &lt;int&gt; (property: imageRotation)
 * &nbsp;&nbsp;&nbsp;The degrees to rotate the images by (0-360).
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-scale &lt;double&gt; (property: imageScale)
 * &nbsp;&nbsp;&nbsp;The scale factor (0-1) for images based on the page size.
 * &nbsp;&nbsp;&nbsp;default: 0.9
 * </pre>
 *
 <!-- options-end -->
 * <p/>
 * Based on weka.gui.visualize.PDFWriter
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFWriter
  extends BufferedImageBasedWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3177842835940277934L;

  /** the degrees to rotate images. */
  protected int m_ImageRotation;

  /** the percentage (0-1) to scale the images to. */
  protected double m_ImageScale;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs PDF documents.";
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   *
   * @return 		the name of the writer
   */
  @Override
  public String getDescription() {
    return "PDF document";
  }

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser.
   *
   * @return 		the file extensions
   */
  @Override
  public String[] getExtensions() {
    return new String[]{".pdf"};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "rotation", "imageRotation",
	    0);

    m_OptionManager.add(
	    "scale", "imageScale",
	    0.9);
  }

  /**
   * Sets the degrees to rotate the image by.
   *
   * @param value	the degrees
   */
  public void setImageRotation(int value) {
    if ((value >= 0) && (value <= 360)) {
      m_ImageRotation = value;
      reset();
    }
    else {
      getLogger().severe("Degrees must be from 0 to 360!");
    }
  }

  /**
   * Returns the degrees by which to rotate the images.
   *
   * @return 		the degrees
   */
  public int getImageRotation() {
    return m_ImageRotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String imageRotationTipText() {
    return "The degrees to rotate the images by (0-360).";
  }

  /**
   * Sets the scale factor (0-1) for images based on the page size.
   *
   * @param value	the scale factor
   */
  public void setImageScale(double value) {
    if ((value > 0) && (value <= 1)) {
      m_ImageScale = value;
      reset();
    }
    else {
      getLogger().severe("Scale must satisfy 0<x<1!");
    }
  }

  /**
   * Returns the scale factor (0-1) for images based on the page size.
   *
   * @return 		the scale factor
   */
  public double getImageScale() {
    return m_ImageScale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String imageScaleTipText() {
    return "The scale factor (0-1) for images based on the page size.";
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  @Override
  public void generateOutput() throws Exception {
    BufferedImage	bi;
    Document		doc;
    Image 		image;
    float		scale;
    FileOutputStream    fos;

    // render image
    bi = createBufferedImage();

    // generate PDF
    scale = (float) m_ImageScale;
    doc   = new Document();
    fos   = new FileOutputStream(getFile().getAbsoluteFile());
    PdfWriter.getInstance(doc, fos);
    doc.open();
    image = Image.getInstance(Toolkit.getDefaultToolkit().createImage(bi.getSource()), null);
    if (m_ImageRotation != 0) {
      image.setRotationDegrees(m_ImageRotation);
      image.rotate();
    }
    image.scaleToFit(
	doc.getPageSize().getWidth()*scale,
	doc.getPageSize().getHeight()*scale);
    doc.add(image);
    doc.close();
    FileUtils.closeQuietly(fos);
  }
}
