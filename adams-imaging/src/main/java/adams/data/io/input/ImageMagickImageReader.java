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

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.data.io.output.AbstractImageWriter;

/**
 <!-- globalinfo-start -->
 * ImageMagick image reader for: a, aai, art, avs, b, bgr, bgra, bie, bmp, bmp2, bmp3, brf, c, cal, cals, canvas, caption, cin, cip, clip, cmyk, cmyka, cur, cut, dcm, dcx, dds, dfont, dpx, eps2, eps3, fax, fits, fractal, fts, g, g3, gif, gif87, gradient, gray, group4, hald, hdr, histogram, hrz, htm, html, icb, ico, icon, inline, ipl, isobrl, j2c, j2k, jbg, jbig, jng, jp2, jpc, jpeg, jpg, jpx, k, label, m, mac, map, matte, miff, mng, mono, mpc, msl, mtv, mvg, null, o, otb, otf, pal, palm, pam, pattern, pbm, pcd, pcds, pct, pcx, pdb, pes, pfa, pfb, pfm, pgm, pgx, picon, pict, pix, pjpeg, plasma, png, png24, png32, png8, pnm, ppm, preview, ps2, ps3, psb, psd, ptif, pwp, r, radial-gradient, ras, rgb, rgba, rgbo, rla, rle, scr, sct, sfw, sgi, shtml, stegano, sun, text, tga, thumbnail, tiff, tiff64, tile, tim, ttc, ttf, txt, ubrl, uil, uyvy, vda, vicar, vid, viff, vst, wbmp, wpg, x, xbm, xc, xcf, xpm, xv, xwd, y, ycbcr, ycbcra, yuv
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
    return "ImageMagick image reader for: " + Utils.flatten(getFormatExtensions(), ", ");
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
    return new String[]{
	"a", "aai", "art", "avs", "b", "bgr", "bgra", "bie", "bmp", "bmp2", 
	"bmp3", "brf", "c", "cal", "cals", "canvas", "caption", "cin", "cip", 
	"clip", "cmyk", "cmyka", "cur", "cut", "dcm", "dcx", "dds", "dfont", 
	"dpx", "eps2", "eps3", "fax", "fits", "fractal", "fts", "g", "g3", 
	"gif", "gif87", "gradient", "gray", "group4", "hald", "hdr", 
	"histogram", "hrz", "htm", "html", "icb", "ico", "icon", "inline", 
	"ipl", "isobrl", "j2c", "j2k", "jbg", "jbig", "jng", "jp2", "jpc", 
	"jpeg", "jpg", "jpx", "k", "label", "m", "mac", "map", "matte", 
	"miff", "mng", "mono", "mpc", "msl", "mtv", "mvg", "null", "o", 
	"otb", "otf", "pal", "palm", "pam", "pattern", "pbm", "pcd", "pcds", 
	"pct", "pcx", "pdb", "pes", "pfa", "pfb", "pfm", "pgm", "pgx", "picon", 
	"pict", "pix", "pjpeg", "plasma", "png", "png24", "png32", "png8", 
	"pnm", "ppm", "preview", "ps2", "ps3", "psb", "psd", "ptif", "pwp", 
	"r", "radial-gradient", "ras", "rgb", "rgba", "rgbo", "rla", "rle", 
	"scr", "sct", "sfw", "sgi", "shtml", "stegano", "sun", "text", "tga", 
	"thumbnail", "tiff", "tiff64", "tile", "tim", "ttc", "ttf", "txt", 
	"ubrl", "uil", "uyvy", "vda", "vicar", "vid", "viff", "vst", "wbmp", 
	"wpg", "x", "xbm", "xc", "xcf", "xpm", "xv", "xwd", "y", "ycbcr", 
	"ycbcra", "yuv"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return null;
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
