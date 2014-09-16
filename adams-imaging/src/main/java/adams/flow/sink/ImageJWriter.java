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
 * ImageJWriter.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import ij.io.FileSaver;
import adams.core.ImageJHelper;
import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.imagej.ImagePlusContainer;

/**
 <!-- globalinfo-start -->
 * Actor for saving an ImagePlus object as image file.
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
 * &nbsp;&nbsp;&nbsp;default: ImageJWriter
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
 * <pre>-image-type &lt;AUTO|BMP|FITS|GIF|JPEG|LUT|PGM|PNG|RAW|TEXT|TIFF|ZIP&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageJWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 7509908838736709270L;

  /**
   * The type of the image to create.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ImageType {
    /** Automatic, based on extension. */
    AUTO,
    /** Bitmap. */
    BMP,
    /** fits. */
    FITS,
    /** GIF. */
    GIF,
    /** JPEG. */
    JPEG,
    /** look up table. */
    LUT,
    /** PGM. */
    PGM,
    /** PNG. */
    PNG,
    /** raw. */
    RAW,
    /** text. */
    TEXT,
    /** TIFF. */
    TIFF,
    /** zipped TIFF. */
    ZIP
  }

  /** the image type to create. */
  protected ImageType m_ImageType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for saving an ImagePlus object as image file.";
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
   * Determines the image type for the given filename.
   *
   * @param filename	the file to determine the image type for
   * @return		the determine image type, default is PNG
   */
  protected ImageType determineImageType(String filename) {
    ImageType	result;
    String	filenameLower;

    result        = ImageType.PNG;
    filenameLower = filename.toLowerCase();

    if (filenameLower.endsWith(".bmp"))
      result = ImageType.BMP;
    else if (filenameLower.endsWith(".fits"))
      result = ImageType.FITS;
    else if (filenameLower.endsWith(".gif"))
      result = ImageType.GIF;
    else if (filenameLower.endsWith(".jpeg") || filenameLower.endsWith(".jpg"))
      result = ImageType.JPEG;
    else if (filenameLower.endsWith(".lut"))
      result = ImageType.LUT;
    else if (filenameLower.endsWith(".pgm") || filenameLower.endsWith(".ppm"))
      result = ImageType.PGM;
    else if (filenameLower.endsWith(".png"))
      result = ImageType.PNG;
    else if (filenameLower.endsWith(".raw"))
      result = ImageType.RAW;
    else if (filenameLower.endsWith(".txt"))
      result = ImageType.TEXT;
    else if (filenameLower.endsWith(".tiff") || filenameLower.endsWith(".tif"))
      result = ImageType.TIFF;
    else
      getLogger().info("Cannot determine image type for: " + filenameLower);

    return result;
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
    
    if (result == null)
      ImageJHelper.setPluginsDirectory();
    
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
    ImagePlusContainer	img;
    String		filename;
    FileSaver		saver;
    ImageType		type;

    result = null;

    img = ImageJHelper.toImagePlusContainer((AbstractImageContainer) m_InputToken.getPayload());
    filename = m_OutputFile.getAbsolutePath();
    if (m_ImageType == ImageType.AUTO)
      type = determineImageType(filename);
    else
      type = m_ImageType;

    try {
      saver = new FileSaver(img.getImage());
      switch (type) {
	case BMP:
	  saver.saveAsBmp(filename);
	  break;
	case FITS:
	  saver.saveAsFits(filename);
	  break;
	case GIF:
	  saver.saveAsGif(filename);
	  break;
	case JPEG:
	  FileSaver.setJpegQuality(100);
	  saver.saveAsJpeg(filename);
	  break;
	case LUT:
	  saver.saveAsLut(filename);
	  break;
	case PGM:
	  saver.saveAsPgm(filename);
	  break;
	case PNG:
	  saver.saveAsPng(filename);
	  break;
	case RAW:
	  saver.saveAsRaw(filename);
	  break;
	case TEXT:
	  saver.saveAsText(filename);
	  break;
	case TIFF:
	  saver.saveAsTiff(filename);
	  break;
	case ZIP:
	  saver.saveAsZip(filename);
	  break;
	default:
	  result = "Unhandled image type: " + m_ImageType;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to write image to: " + m_OutputFile, e);
    }

    return result;
  }
}
