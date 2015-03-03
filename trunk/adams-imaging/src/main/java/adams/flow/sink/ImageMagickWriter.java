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
 * ImageMagickWriter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import adams.core.QuickInfoHelper;
import adams.core.annotation.DeprecatedClass;
import adams.data.image.AbstractImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.data.imagemagick.ImageType;
import adams.data.io.output.ImageMagickImageWriter;

/**
 <!-- globalinfo-start -->
 * Writes an image to disk using ImageMagick (http:&#47;&#47;www.imagemagick.org&#47;).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageMagickWriter
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The filename of the image to write.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-image-type &lt;AUTO|AAI|AVS|BMP|CIN|CMYK|CMYKA|DCX|DIB|DPX|EPDF|EPI|EPS|EPS2|EPS3|EPSF|EPSI|EPT|EXR|FAX|FITS|FPX|GIF|GRAY|HDR|HRZ|HTML|INFO|JBIG|JNG|JP2|JPC|JPEG|MIFF|MONO|MNG|M2V|MPEG|MPC|MPR|MSL|MTV|MVG|OTB|P7|PALM|PAM|PBM|PCD|PCDS|PCL|PCX|PDB|PDF|PFM|PGM|PICON|PICT|PNG|PNG8|PNG24|PNG32|PNM|PPM|PS|PS2|PS3|PSB|PSD|PTIF|RGB|RGBA|SGI|SHTML|SUN|SVG|TGA|TIFF|TXT|UIL|UYVY|VICAR|VIFF|WBMP|WEBP|X|XBM|XPM|XWD|YCbCr|YCbCrA|YUV&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
@DeprecatedClass(useInstead = {ImageWriter.class, ImageMagickImageWriter.class})
public class ImageMagickWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -1264554670448330464L;

  /** the image type to create. */
  protected ImageType m_ImageType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Writes an image to disk using ImageMagick (http://www.imagemagick.org/).\n"
	+ "\n"
	+ "DEPRECATED\n"
	+ "Use " + ImageWriter.class.getName() + " in conjunction with the " 
	+ ImageMagickImageWriter.class.getName() + " instead.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "image-type", "imageType",
	    ImageType.AUTO);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return super.getQuickInfo() + QuickInfoHelper.toString(this, "imageType", m_ImageType, ", image type: ");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The filename of the image to write.";
  }

  /**
   * Sets the type of image to create.
   *
   * @param value 	the image type
   */
  public void setImageType(ImageType value) {
    m_ImageType = value;
    reset();
  }

  /**
   * Returns the type of image to create.
   *
   * @return 		the image type
   */
  public ImageType getImageType() {
    return m_ImageType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTypeTipText() {
    return "The type of image to create.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.image.AbstractImage.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!ImageMagickHelper.isConvertAvailable())
	result = ImageMagickHelper.getMissingConvertErrorMessage();
    }

    return result;
  }

  /**
   * Determines the image type for the given filename.
   *
   * @param filename	the file to determine the image type for
   * @return		the determine image type, default is PNG
   */
  protected ImageType determineImageType(String filename) {
    ImageType	result;

    result = null;

    for (ImageType type: ImageType.values()) {
      if (type.matches(filename)) {
	result = type;
	break;
      }
    }

    if (result == null)
      throw new IllegalStateException("Failed to determine image type for '" + filename + "'!");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    AbstractImageContainer	img;
    String		filename;
    ImageType		type;
    ConvertCmd		cmd;
    IMOperation		op;

    result = null;

    img      = (AbstractImageContainer) m_InputToken.getPayload();
    filename = m_OutputFile.getAbsolutePath();

    try {
      // determine image type
      if (m_ImageType == ImageType.AUTO)
        type = determineImageType(filename);
      else
        type = m_ImageType;

      op = new IMOperation();
      op.addImage();  // input
      op.addImage(type.getType() + ":" + filename);  // output
      cmd = new ConvertCmd();
      cmd.run(op, img.toBufferedImage());
    }
    catch (Exception e) {
      result = handleException("Failed to write image to: " + m_OutputFile, e);
    }

    return result;
  }
}
