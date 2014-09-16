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

import java.io.File;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import adams.core.EnumWithCustomDisplay;
import adams.core.ImageMagickHelper;
import adams.core.QuickInfoHelper;
import adams.core.option.AbstractOption;
import adams.data.image.AbstractImageContainer;

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
public class ImageMagickWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -1264554670448330464L;

  /**
   * The type of the image to create.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ImageType
    implements EnumWithCustomDisplay<ImageType> {
    AUTO("AUTO", "", "Automatic"),
    AAI("AAI", "aai", "AAI Dune image"),
    AVS("AVS", "avs", "AVS X image"),
    BMP("BMP", "bmp", "Microsoft Windows bitmap"),
    CIN("CIN", "cin", "Kodak Cineon Image Format"),
    CMYK("CMYK", "cmyk", "Raw cyan, magenta, yellow, and black samples"),
    CMYKA("CMYKA", "cmyka", "Raw cyan, magenta, yellow, black, and alpha samples"),
    DCX("DCX", "dcx", "ZSoft IBM PC multi-page Paintbrush image"),
    DIB("DIB", "dib", "Microsoft Windows Device Independent Bitmap"),
    DPX("DPX", "dpx", "SMPTE Digital Moving Picture Exchange 2.0 (SMPTE 268M-2003)"),
    EPDF("EPDF", "epdf", "Encapsulated Portable Document Format"),
    EPI("EPI", "epi", "Adobe Encapsulated PostScript Interchange format"),
    EPS("EPS", "eps", "Adobe Encapsulated PostScript"),
    EPS2("EPS2", "eps", "Adobe Level II Encapsulated PostScript"),
    EPS3("EPS3", "eps", "Adobe Level III Encapsulated PostScript"),
    EPSF("EPSF", "epsf", "Adobe Encapsulated PostScript"),
    EPSI("EPSI", "epsi", "Adobe Encapsulated PostScript Interchange format"),
    EPT("EPT", "ept", "Adobe Encapsulated PostScript Interchange format with TIFF preview"),
    EXR("EXR", "exr", "High dynamic-range (HDR) file format developed by Industrial Light & Magic"),
    FAX("FAX", "fax", "Group 3 TIFF"),
    FITS("FITS", "fits", "Flexible Image Transport System"),
    FPX("FPX", "fpx", "FlashPix Format"),
    GIF("GIF", "gif", "CompuServe Graphics Interchange Format"),
    GRAY("GRAY", "gray", "Raw gray samples"),
    HDR("HDR", "hdr", "Radiance RGBE image format"),
    HRZ("HRZ", "hrz", "Slow Scane TeleVision"),
    HTML("HTML", "html", "Hypertext Markup Language with a client-side image map"),
    INFO("INFO", "info", "Format and characteristics of the image"),
    JBIG("JBIG", "jbig", "Joint Bi-level Image experts Group file interchange format"),
    JNG("JNG", "jng", "Multiple-image Network Graphics"),
    JP2("JP2", "jp2", "JPEG-2000 JP2 File Format Syntax"),
    JPC("JPC", "jpc", "JPEG-2000 Code Stream Syntax"),
    JPEG("JPEG", "jpg", "Joint Photographic Experts Group JFIF format"),
    MIFF("MIFF", "miff", "Magick image file format"),
    MONO("MONO", "mono", "Bi-level bitmap in least-significant-byte first order"),
    MNG("MNG", "mng", "Multiple-image Network Graphics"),
    M2V("M2V", "", "Motion Picture Experts Group file interchange format (version 2)"),
    MPEG("MPEG", "mpg", "Motion Picture Experts Group file interchange format (version 1)"),
    MPC("MPC", "mpc", "Magick Persistent Cache image file format"),
    MPR("MPR", "mpr", "Magick Persistent Registry"),
    MSL("MSL", "msl", "Magick Scripting Language"),
    MTV("MTV", "mtv", "MTV Raytracing image format"),
    MVG("MVG", "mvg", "Magick Vector Graphics."),
    OTB("OTB", "otb", "On-the-air Bitmap"),
    P7("P7", "p7", "Xv's Visual Schnauzer thumbnail format"),
    PALM("PALM", "palm", "Palm pixmap"),
    PAM("PAM", "pam", "Common 2-dimensional bitmap format"),
    PBM("PBM", "pbm", "Portable bitmap format (black and white)"),
    PCD("PCD", "pcd", "Photo CD"),
    PCDS("PCDS", "pcds", "Photo CD"),
    PCL("PCL", "pcl", "HP Page Control Language"),
    PCX("PCX", "pcx", "ZSoft IBM PC Paintbrush file"),
    PDB("PDB", "pdb", "Palm Database ImageViewer Format"),
    PDF("PDF", "pdf", "Portable Document Format"),
    PFM("PFM", "pfm", "Portable float map format"),
    PGM("PGM", "pgm", "Portable graymap format (gray scale)"),
    PICON("PICON", "picon", "Personal Icon"),
    PICT("PICT", "pict", "Apple Macintosh QuickDraw/PICT file"),
    PNG("PNG", "png", "Portable Network Graphics"),
    PNG8("PNG8", "png", "Portable Network Graphics"),
    PNG24("PNG24", "png", "Portable Network Graphics"),
    PNG32("PNG32", "png", "Portable Network Graphics"),
    PNM("PNM", "pnm", "Portable anymap"),
    PPM("PPM", "ppm", "Portable pixmap format (color)"),
    PS("PS", "ps", "Adobe PostScript file"),
    PS2("PS2", "ps", "Adobe Level II PostScript file"),
    PS3("PS3", "ps", "Adobe Level III PostScript file"),
    PSB("PSB", "psb", "Adobe Large Document Format"),
    PSD("PSD", "psd", "Adobe Photoshop bitmap file"),
    PTIF("PTIF", "ptif", "Pyramid encoded TIFF"),
    RGB("RGB", "rgb", "Raw red, green, and blue samples"),
    RGBA("RGBA", "rgba", "Raw red, green, blue, and alpha samples"),
    SGI("SGI", "sgi", "Irix RGB image"),
    SHTML("SHTML", "shtml", "Hypertext Markup Language client-side image map"),
    SUN("SUN", "sun", "SUN Rasterfile"),
    SVG("SVG", "svg", "Scalable Vector Graphics"),
    TGA("TGA", "tga", "Truevision Targa image"),
    TIFF("TIFF", "tiff", "Tagged Image File Format"),
    TXT("TXT", "txt", "Raw text file"),
    UIL("UIL", "uil", "X-Motif UIL table"),
    UYVY("UYVY", "uyvy", "Interleaved YUV raw image"),
    VICAR("VICAR", "vicar", "VICAR rasterfile format"),
    VIFF("VIFF", "viff", "Khoros Visualization Image File Format"),
    WBMP("WBMP", "wbmp", "Wireless bitmap"),
    WEBP("WEBP", "webp", "Weppy image format"),
    X("X", "x", "display or import an image to or from an X11 server"),
    XBM("XBM", "xbm", "X Windows system bitmap, black and white only"),
    XPM("XPM", "xpm", "X Windows system pixmap"),
    XWD("XWD", "xwd", "X Windows system window dump"),
    YCbCr("YCbCr", "ycbcr", "Raw Y, Cb, and Cr samples"),
    YCbCrA("YCbCrA", "ycbcra", "Raw Y, Cb, Cr, and alpha samples"),
    YUV("YUV", "yuv", "CCIR 601 4:1:1");

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
    return "Writes an image to disk using ImageMagick (http://www.imagemagick.org/).";
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
