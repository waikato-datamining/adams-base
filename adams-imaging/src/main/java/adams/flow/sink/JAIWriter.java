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
 * JAIWriter.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;

import javax.media.jai.JAI;

import adams.core.EnumWithCustomDisplay;
import adams.core.QuickInfoHelper;
import adams.core.option.AbstractOption;
import adams.data.image.AbstractImage;

/**
 <!-- globalinfo-start -->
 * Writes an image to disk using Java Advanced Imaging (JAI).
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
 * &nbsp;&nbsp;&nbsp;default: JAIWriter
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
 * <pre>-image-type &lt;AUTO|BMP|JPEG|PNG|PNM|TIFF&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JAIWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 1824012225640852716L;

  /**
   * The type of the image to create.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ImageType
    implements EnumWithCustomDisplay<ImageType> {
    
    AUTO("AUTO", "", "Automatic"),
    BMP("BMP", "bmp", "Microsoft Windows bitmap"),
    JPEG("JPEG", "jpg", "Joint Photographic Experts Group JFIF format"),
    PNG("PNG", "png", "Portable Network Graphics"),
    PNM("PNM", "pnm", "Portable anymap"),
    TIFF("TIFF", "tiff", "Tagged Image File Format");

    /** the raw string. */
    private String m_Raw;

    /** the type. */
    private String m_Type;

    /** the extension. */
    private String m_Extension;

    /** the description. */
    private String m_Description;

    /**
     * Initializes the image type.
     *
     * @param ext	the extension
     * @param desc	the description
     */
    private ImageType(String type, String ext, String desc) {
      m_Raw         = super.toString();
      m_Type        = type;
      m_Extension   = ext;
      m_Description = desc;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Type + " - " + m_Description;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return toDisplay();
    }

    /**
     * Returns the ImageMagick type.
     *
     * @return		the type
     */
    public String getType() {
      return m_Type;
    }

    /**
     * Returns the associated extension.
     *
     * @return		the extension
     */
    public String getExtension() {
      return m_Extension;
    }

    /**
     * Checks whether the file matches the extension of this item.
     *
     * @param file	the file to check
     * @return		true if the extensions match
     */
    public boolean matches(File file) {
      return matches(file.getPath());
    }

    /**
     * Checks whether the file matches the extension of this item.
     *
     * @param filename	the file to check
     * @return		true if the extensions match
     */
    public boolean matches(String filename) {
      return filename.toLowerCase().endsWith("." + m_Extension);
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public ImageType parse(String s) {
      return (ImageType) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((ImageType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static ImageType valueOf(AbstractOption option, String str) {
      ImageType	result;

      result = null;

      // default parsing
      try {
        result = valueOf(str);
      }
      catch (Exception e) {
        // ignored
      }

      // try display
      if (result == null) {
        for (ImageType dt: values()) {
  	if (dt.toDisplay().equals(str)) {
  	  result = dt;
  	  break;
  	}
        }
      }

      return result;
    }
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
    return "Writes an image to disk using Java Advanced Imaging (JAI).";
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
    return new Class[]{AbstractImage.class};
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
    AbstractImage	img;
    String		filename;
    ImageType		type;

    result = null;

    img      = (AbstractImage) m_InputToken.getPayload();
    filename = m_OutputFile.getAbsolutePath();

    try {
      // determine image type
      if (m_ImageType == ImageType.AUTO)
        type = determineImageType(filename);
      else
        type = m_ImageType;

      JAI.create("filestore", img.toBufferedImage(), filename, type.toRaw());
    }
    catch (Exception e) {
      result = handleException("Failed to write image to: " + m_OutputFile, e);
    }

    return result;
  }
}
