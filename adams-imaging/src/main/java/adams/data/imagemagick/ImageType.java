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
 * ImageType.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick;

import java.io.File;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The type of the image to create.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9987 $
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