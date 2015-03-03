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
  
  AUTO("AUTO", "", "Automatic", true, true),
  THREEFR("3FR", "3fr", "Hasselblad CFV/H3D39II", true, false),
  AAI("AAI", "aai", "AAI Dune image", true, true),
  AVS("AVS", "avs", "AVS X image", true, true),
  BMP("BMP", "bmp", "Microsoft Windows bitmap", true, true),
  CIN("CIN", "cin", "Kodak Cineon Image Format", true, true),
  CIP("CIP", "cip", "Cisco IP phone image format", false, true),
  CMYK("CMYK", "cmyk", "Raw cyan, magenta, yellow, and black samples", true, true),
  CMYKA("CMYKA", "cmyka", "Raw cyan, magenta, yellow, black, and alpha samples", true, true),
  CUR("CUR", "cur", "Microsoft icon", true, true),
  CUT("CUT", "cut", "DR Halo", true, false),
  DCM("DCM", "dcm", "Digital Imaging and Communications in Medicine image", true, false),
  DCX("DCX", "dcx", "ZSoft IBM PC multi-page Paintbrush image", true, true),
  DDS("DDS", "dds", "Microsoft DirectDraw Surface", true, false),
  DPX("DPX", "dpx", "SMPTE Digital Moving Picture Exchange 2.0 (SMPTE 268M-2003)", true, true),
  EPS2("EPS2", "eps", "Adobe Level II Encapsulated PostScript", false, true),
  EPS3("EPS3", "eps", "Adobe Level III Encapsulated PostScript", false, true),
  FAX("FAX", "fax", "Group 3 TIFF", true, true),
  FITS("FITS", "fits", "Flexible Image Transport System", true, true),
  FTS("FTS", "fts", "Flexible Image Transport System", true, true),
  G3("G3", "g3", "Group 3 FAX", true, true),
  GIF("GIF", "gif", "CompuServe Graphics Interchange Format", true, true),
  GIF87("GIF87", "gif87", "CompuServe Graphics Interchange Format (version 87a)", true, true),
  GRAY("GRAY", "gray", "Raw gray samples", true, true),
  GROUP4("GROUP4", "group4", "Raw CCITT Group4", true, true),
  HDR("HDR", "hdr", "Radiance RGBE image format", true, true),
  HRZ("HRZ", "hrz", "Slow Scane TeleVision", true, true),
  HTM("HTM", "htm", "Hypertext Markup Language with a client-side image map", false, true),
  HTML("HTML", "html", "Hypertext Markup Language with a client-side image map", false, true),
  ICB("ICB", "icb", "Truevision Targa image", true, true),
  ICO("ICO", "ico", "Microsoft icon", true, true),
  ICON("ICON", "icon", "Microsoft icon", true, true),
  INLINE("INLINE", "inline", "Base64-encoded inline images", true, false),
  J2C("J2C", "j2k", "JPEG-2000 Code Stream Syntax", true, true),
  J2K("J2K", "j2c", "JPEG-2000 Code Stream Syntax", true, true),
  JBG("JBG", "jbg", "Joint Bi-level Image experts Group file interchange format", true, true),
  JBIG("JBIG", "jbig", "Joint Bi-level Image experts Group file interchange format", true, true),
  JNG("JNG", "jng", "Multiple-image Network Graphics", true, true),
  JP2("JP2", "jp2", "JPEG-2000 JP2 File Format Syntax", true, true),
  JPC("JPC", "jpc", "JPEG-2000 Code Stream Syntax", true, true),
  JPEG("JPEG", "jpeg", "Joint Photographic Experts Group JFIF format", true, true),
  JPG("JPG", "jpg", "Joint Photographic Experts Group JFIF format", true, true),
  JPX("JPX", "jpx", "JPEG-2000 File Format Syntax", true, true),
  MAC("MAC", "mac", "MAC Paint", true, false),
  MATTE("MATTE", "matte", "Matte format", false, true),
  MIFF("MIFF", "miff", "Magick image file format", true, true),
  MNG("MNG", "mng", "Multiple-image Network Graphics", true, true),
  MONO("MONO", "mono", "Bi-level bitmap in least-significant-byte first order", true, true),
  MPC("MPC", "mpc", "Magick Persistent Cache image file format", true, true),
  MSL("MSL", "msl", "Magick Scripting Language", true, true),
  MTV("MTV", "mtv", "MTV Raytracing image format", true, true),
  MVG("MVG", "mvg", "Magick Vector Graphics.", true, true),
  OTB("OTB", "otb", "On-the-air Bitmap", true, true),
  PALM("PALM", "palm", "Palm pixmap", true, true),
  PAM("PAM", "pam", "Common 2-dimensional bitmap format", true, true),
  PBM("PBM", "pbm", "Portable bitmap format (black and white)", true, true),
  PCD("PCD", "pcd", "Photo CD", true, true),
  PCDS("PCDS", "pcds", "Photo CD", true, true),
  PCT("PCT", "pct", "Apple Macintosh QuickDraw/PICT", true, true),
  PCX("PCX", "pcx", "ZSoft IBM PC Paintbrush file", true, true),
  PDB("PDB", "pdb", "Palm Database ImageViewer Format", true, true),
  PFM("PFM", "pfm", "Portable float map format", true, true),
  PGM("PGM", "pgm", "Portable graymap format (gray scale)", true, true),
  PICON("PICON", "picon", "Personal Icon", true, true),
  PICT("PICT", "pict", "Apple Macintosh QuickDraw/PICT file", true, true),
  PNG("PNG", "png", "Portable Network Graphics", true, true),
  PNG8("PNG8", "png", "Portable Network Graphics", true, true),
  PNG24("PNG24", "png", "Portable Network Graphics", true, true),
  PNG32("PNG32", "png", "Portable Network Graphics", true, true),
  PNM("PNM", "pnm", "Portable anymap", true, true),
  PPM("PPM", "ppm", "Portable pixmap format (color)", true, true),
  PS2("PS2", "ps", "Adobe Level II PostScript file", false, true),
  PS3("PS3", "ps", "Adobe Level III PostScript file", false, true),
  PSB("PSB", "psb", "Adobe Large Document Format", true, true),
  PSD("PSD", "psd", "Adobe Photoshop bitmap file", true, true),
  PTIF("PTIF", "ptif", "Pyramid encoded TIFF", true, true),
  RGB("RGB", "rgb", "Raw red, green, and blue samples", true, true),
  RGBA("RGBA", "rgba", "Raw red, green, blue, and alpha samples", true, true),
  SGI("SGI", "sgi", "Irix RGB image", true, true),
  SHTML("SHTML", "shtml", "Hypertext Markup Language client-side image map", false, true),
  SUN("SUN", "sun", "SUN Rasterfile", true, true),
  TGA("TGA", "tga", "Truevision Targa image", true, true),
  TIFF("TIFF", "tiff", "Tagged Image File Format", true, true),
  TXT("TXT", "txt", "Raw text file", true, true),
  UIL("UIL", "uil", "X-Motif UIL table", false, true),
  UYVY("UYVY", "uyvy", "Interleaved YUV raw image", true, true),
  VICAR("VICAR", "vicar", "VICAR rasterfile format", true, true),
  VIFF("VIFF", "viff", "Khoros Visualization Image File Format", true, true),
  WBMP("WBMP", "wbmp", "Wireless bitmap", true, true),
  X("X", "x", "display or import an image to or from an X11 server", true, true),
  XBM("XBM", "xbm", "X Windows system bitmap, black and white only", true, true),
  XPM("XPM", "xpm", "X Windows system pixmap", true, true),
  XWD("XWD", "xwd", "X Windows system window dump", true, true),
  YCbCr("YCbCr", "ycbcr", "Raw Y, Cb, and Cr samples", true, true),
  YCbCrA("YCbCrA", "ycbcra", "Raw Y, Cb, Cr, and alpha samples", true, true),
  YUV("YUV", "yuv", "CCIR 601 4:1:1", true, true);

  /** the raw string. */
  private String m_Raw;

  /** the type. */
  private String m_Type;

  /** the extension. */
  private String m_Extension;

  /** the description. */
  private String m_Description;
  
  /** whether reader is supported. */
  private boolean m_Read;
  
  /** whether write is supported. */
  private boolean m_Write;

  /**
   * Initializes the image type.
   *
   * @param ext		the extension
   * @param desc	the description
   * @param read	true if read supported
   * @param write	true if write supported
   */
  private ImageType(String type, String ext, String desc, boolean read, boolean write) {
    m_Raw         = super.toString();
    m_Type        = type;
    m_Extension   = ext;
    m_Description = desc;
    m_Read        = read;
    m_Write       = write;
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
   * Returns whether format can be read.
   *
   * @return		true if can be read
   */
  public boolean canRead() {
    return m_Read;
  }

  /**
   * Returns whether format can be written.
   *
   * @return		true if can be written
   */
  public boolean canWrite() {
    return m_Write;
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