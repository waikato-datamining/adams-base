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
package adams.data.jai;

import java.io.File;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The type of the image to create.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9700 $
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