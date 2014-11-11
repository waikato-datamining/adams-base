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
 * ImageMagickImageReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.data.imagemagick.ImageType;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.ImageMagickImageWriter;

/**
 <!-- globalinfo-start -->
 * ImageMagick image reader for: 3fr, aai, avs, bmp, cin, cmyk, cmyka, cur, cut, dcm, dcx, dds, dpx, fax, fits, fts, g3, gif, gif87, gray, group4, hdr, hrz, icb, ico, icon, inline, j2c, j2k, jbg, jbig, jng, jp2, jpc, jpeg, jpg, jpx, mac, miff, mng, mono, mpc, msl, mtv, mvg, otb, palm, pam, pbm, pcd, pcds, pct, pcx, pdb, pfm, pgm, picon, pict, png, png, png, png, pnm, ppm, psb, psd, ptif, rgb, rgba, sgi, sun, tga, tiff, txt, uyvy, vicar, viff, wbmp, x, xbm, xpm, xwd, ycbcr, ycbcra, yuv<br/>
 * <br/>
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagickImageReader
  extends AbstractImageReader<BufferedImageContainer> {
  
  /** for serialization. */
  private static final long serialVersionUID = 5347100846354068540L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"ImageMagick image reader for: " + Utils.flatten(getFormatExtensions(), ", ") + "\n"
	+ "\n"
	+ "For more information see:\n"
	+ "http://www.imagemagick.org/\n"
	+ (ImageMagickHelper.isConvertAvailable() ? "" : "\n" + ImageMagickHelper.getMissingConvertErrorMessage());
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "ImageMagick";
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
      if (it.canRead())
	result.add(it.getExtension());
    }
    Collections.sort(result);
    
    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return new ImageMagickImageWriter();
  }
  
  /**
   * Returns whether the reader is actually available.
   * 
   * @return		true if available and ready to use
   */
  @Override
  public boolean isAvailable() {
    return ImageMagickHelper.isConvertAvailable();
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  protected void check(PlaceholderFile file) {
    super.check(file);
    
    if (!ImageMagickHelper.isConvertAvailable())
      throw new IllegalStateException(ImageMagickHelper.getMissingConvertErrorMessage());
  }
  
  /**
   * Performs the actual reading of the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  @Override
  protected BufferedImageContainer doRead(PlaceholderFile file) {
    BufferedImageContainer	result;
    BufferedImage		image;
    
    result = null;
    image  = ImageMagickHelper.read(file);
    
    if (image != null) {
      result = new BufferedImageContainer();
      result.setImage(image);
    }
    
    return result;
  }
}
