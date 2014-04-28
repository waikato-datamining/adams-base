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
 * TesseractPageSegmentation.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The types of page segementation that Tesseract supports.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum TesseractPageSegmentation
  implements EnumWithCustomDisplay<TesseractPageSegmentation> {
  
  OSD_ONLY(0, "Orientation and script detection (OSD) only"),
  AUTO_WITH_OSD(1, "Automatic page segmentation with OSD"),
  AUTO_NO_OSD(2, "Automatic page segmentation, but no OSD, or OCR"),
  FULL_AUTO_NO_OSD(3, "Fully automatic page segmentation, but no OSD"),
  SINGLE_COLUMN(4, "Assume a single column of text of variable sizes"),
  SINGLE_VERTICAL_BLOCK(5, "Assume a single uniform block of vertically aligned text"),
  SINGLE_BLOCK(6, "Assume a single uniform block of text"),
  SINGLE_LINE(7, "Treat the image as a single text line"),
  SINGLE_WORD(8, "Treat the image as a single word"),
  SINGLE_WORD_CIRCLE(9, "Treat the image as a single word in a circle"),
  SINGLE_CHARACTER(10, "Treat the image as a single character");

  /** the display string. */
  private String m_Display;

  /** the integer code. */
  private Integer m_Code;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   * @param code	the ISO 639-2 code
   */
  private TesseractPageSegmentation(Integer code, String display) {
    m_Code    = code;
    m_Display = display;
    m_Raw     = super.toString();
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the integer code.
   *
   * @return		the integer code
   */
  public Integer toCode() {
    return m_Code;
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
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public TesseractPageSegmentation parse(String s) {
    return (TesseractPageSegmentation) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((TesseractPageSegmentation) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static TesseractPageSegmentation valueOf(AbstractOption option, String str) {
    TesseractPageSegmentation	result;

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
      for (TesseractPageSegmentation seg: values()) {
        if (seg.toDisplay().equals(str)) {
          result = seg;
          break;
        }
      }
    }

    // try code
    if (result == null) {
      for (TesseractPageSegmentation seg: values()) {
        if (seg.toCode().toString().equals(str)) {
          result = seg;
          break;
        }
      }
    }

    return result;
  }
}
