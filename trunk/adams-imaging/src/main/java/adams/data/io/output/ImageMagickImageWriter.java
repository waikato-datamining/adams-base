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
 * ImageMagickImageWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.data.imagemagick.ImageType;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.ImageMagickImageReader;

/**
 <!-- globalinfo-start -->
 * ImageMagick image writer for: aai, avs, bmp, cin, cip, cmyk, cmyka, cur, dcx, dpx, eps, eps, fax, fits, fts, g3, gif, gif87, gray, group4, hdr, hrz, htm, html, icb, ico, icon, j2c, j2k, jbg, jbig, jng, jp2, jpc, jpeg, jpg, jpx, matte, miff, mng, mono, mpc, msl, mtv, mvg, otb, palm, pam, pbm, pcd, pcds, pct, pcx, pdb, pfm, pgm, picon, pict, png, png, png, png, pnm, ppm, ps, ps, psb, psd, ptif, rgb, rgba, sgi, shtml, sun, tga, tiff, txt, uil, uyvy, vicar, viff, wbmp, x, xbm, xpm, xwd, ycbcr, ycbcra, yuv<br/>
 * For more information see:<br/>
 * http:&#47;&#47;www.imagemagick.org&#47;<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-image-type &lt;AUTO|THREEFR|AAI|AVS|BMP|CIN|CIP|CMYK|CMYKA|CUR|CUT|DCM|DCX|DDS|DPX|EPS2|EPS3|FAX|FITS|FTS|G3|GIF|GIF87|GRAY|GROUP4|HDR|HRZ|HTM|HTML|ICB|ICO|ICON|INLINE|J2C|J2K|JBG|JBIG|JNG|JP2|JPC|JPEG|JPG|JPX|MAC|MATTE|MIFF|MNG|MONO|MPC|MSL|MTV|MVG|OTB|PALM|PAM|PBM|PCD|PCDS|PCT|PCX|PDB|PFM|PGM|PICON|PICT|PNG|PNG8|PNG24|PNG32|PNM|PPM|PS2|PS3|PSB|PSD|PTIF|RGB|RGBA|SGI|SHTML|SUN|TGA|TIFF|TXT|UIL|UYVY|VICAR|VIFF|WBMP|X|XBM|XPM|XWD|YCbCr|YCbCrA|YUV&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagickImageWriter
  extends AbstractImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

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
	"ImageMagick image writer for: " + Utils.flatten(getFormatExtensions(), ", ")
	+ "\n"
	+ "For more information see:\n"
	+ "http://www.imagemagick.org/\n"
	+ (ImageMagickHelper.isConvertAvailable() ? "" : "\n" + ImageMagickHelper.getMissingConvertErrorMessage());
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "ImageMagick image writer";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    List<String>	result;
    
    result = new ArrayList<String>();
    
    for (ImageType it: ImageType.values()) {
      if (it == ImageType.AUTO)
	continue;
      if (it.canWrite())
	result.add(it.getExtension());
    }
    Collections.sort(result);
    
    return result.toArray(new String[result.size()]);
  }
  
  /**
   * Returns whether the writer is actually available.
   * 
   * @return		true if available and ready to use
   */
  @Override
  public boolean isAvailable() {
    return ImageMagickHelper.isConvertAvailable();
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
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new ImageMagickImageReader();
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  protected void check(BufferedImageContainer cont) {
    super.check(cont);
    
    if (!ImageMagickHelper.isConvertAvailable())
      throw new IllegalStateException(ImageMagickHelper.getMissingConvertErrorMessage());
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
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    String		result;
    ImageType		type;
    ConvertCmd		cmd;
    IMOperation		op;

    result = null;
    
    try {
      // determine image type
      if (m_ImageType == ImageType.AUTO)
        type = determineImageType(file.getAbsolutePath());
      else
        type = m_ImageType;

      op = new IMOperation();
      op.addImage();  // input
      op.addImage(type.getType() + ":" + file.getAbsolutePath());  // output
      cmd = new ConvertCmd();
      cmd.run(op, cont.toBufferedImage());
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to write image to: " + file, e);
    }
    
    return result;
  }
}
