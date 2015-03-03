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
 * PdfFont.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adams.core.CloneHandler;
import adams.core.Utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;

/**
 * A helper class for PDF fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PdfFont
  implements Serializable, CloneHandler<PdfFont> {

  /** for serialization. */
  private static final long serialVersionUID = -8327183921477550848L;

  /** the separator. */
  public final static char SEPARATOR = '-';

  /** the string for NORMAL. */
  public final static String NORMAL = "Normal";

  /** the string for BOLD. */
  public final static String BOLD = "Bold";

  /** the string for ITALIC. */
  public final static String ITALIC = "Italic";

  /** the string for STRIKETHRU. */
  public final static String STRIKETHRU = "StrikeThru";

  /** the string for UNDERLINE. */
  public final static String UNDERLINE = "Underline";

  /** the string for Courier. */
  public final static String COURIER = "Courier";

  /** the string for Helvetica. */
  public final static String HELVETICA = "Helvetica";

  /** the string for Symbol. */
  public final static String SYMBOL = "Symbol";

  /** the string for Times Roman. */
  public final static String TIMES_ROMAN = "Times Roman";

  /** the string for ZapfDingBats. */
  public final static String ZAPFDINGBATS = "ZapfDingBats";

  /** the font family. */
  protected int m_FontFamily;

  /** the font face. */
  protected int m_FontFace;

  /** the font size. */
  protected float m_Size;

  /** the actual PDF font. */
  protected transient Font m_Font;

  /**
   * Initializes a default font.
   */
  public PdfFont() {
    this(HELVETICA, NORMAL, 12.0f);
  }

  /**
   * Initializes the font with the given values.
   *
   * @param family	the font family (helvetica, etc)
   * @param face	the font face (bold, etc)
   * @param size	the size of the font
   */
  public PdfFont(String family, String face, float size) {
    this(getFontFamily(family), getFontFace(face), size);
  }

  /**
   * Initializes the font with the given values.
   *
   * @param family	the font family (helvetica, etc)
   * @param face	the font face (bold, etc)
   * @param size	the size of the font
   */
  public PdfFont(String family, int face, float size) {
    this(getFontFamily(family), face, size);
  }

  /**
   * Initializes the font with the given values.
   *
   * @param family	the font family (helvetica, etc)
   * @param face	the font face (bold, etc)
   * @param size	the size of the font
   */
  public PdfFont(int family, int face, float size) {
    super();

    m_FontFamily = family;
    m_FontFace   = face;
    m_Size       = size;
    m_Font       = null;
  }

  /**
   * Parses the given string.
   *
   * @param fontStr	the string to parse
   */
  public PdfFont(String fontStr) {
    String	attsStr;
    String	str;
    String[]	split;
    int		i;

    str = fontStr;

    // size
    m_Size = Integer.parseInt(str.substring(str.lastIndexOf(SEPARATOR) + 1));
    str    = str.substring(0, str.lastIndexOf(SEPARATOR));

    // face
    attsStr    = str.substring(str.lastIndexOf(SEPARATOR) + 1);
    split      = attsStr.split(",");
    str        = str.substring(0, str.lastIndexOf(SEPARATOR));
    m_FontFace = Font.NORMAL;
    for (i = 0; i < split.length; i++)
      m_FontFace |= getFontFace(split[i]);

    // family
    m_FontFamily = getFontFamily(str);
  }

  /**
   * Turns the font family into the String constant of the font family.
   *
   * @return		the font family
   */
  public int getFontFamily() {
    return m_FontFamily;
  }

  /**
   * Turns the font family into the String constant of the font family.
   *
   * @return		the font family name
   */
  public String getFontFamilyName() {
    if (m_FontFamily == Font.FontFamily.COURIER.ordinal())
      return COURIER;
    if (m_FontFamily == Font.FontFamily.HELVETICA.ordinal())
      return HELVETICA;
    if (m_FontFamily == Font.FontFamily.SYMBOL.ordinal())
      return SYMBOL;
    if (m_FontFamily == Font.FontFamily.TIMES_ROMAN.ordinal())
      return TIMES_ROMAN;
    if (m_FontFamily == Font.FontFamily.ZAPFDINGBATS.ordinal())
      return ZAPFDINGBATS;

    return HELVETICA;
  }

  /**
   * Returns the font face.
   *
   * @return		the font face
   */
  public int getFontFace() {
    return m_FontFace;
  }

  /**
   * Generates a list of font faces.
   *
   * @return		the list of font faces
   */
  public String[] getFontFaces() {
    List<String>	result;

    result = new ArrayList<String>();
    if ((m_FontFace & Font.BOLD) == Font.BOLD)
      result.add(BOLD);
    if ((m_FontFace & Font.ITALIC) == Font.ITALIC)
      result.add(ITALIC);
    if ((m_FontFace & Font.STRIKETHRU) == Font.STRIKETHRU)
      result.add(STRIKETHRU);
    if ((m_FontFace & Font.UNDERLINE) == Font.UNDERLINE)
      result.add(UNDERLINE);
    if (result.size() == 0)
      result.add(NORMAL);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the font size.
   *
   * @return		the font size
   */
  public float getSize() {
    return m_Size;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public PdfFont getClone() {
    return new PdfFont(m_FontFamily, m_FontFace, m_Size);
  }

  /**
   * Returns the font as string.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;
    Font	font;

    font   = toFont();
    result = font.getFamilyname();
    result += "" + SEPARATOR + Utils.flatten(getFontFaces(font), ",");
    result += "" + SEPARATOR + new Float(font.getSize()).intValue();

    return result;
  }

  /**
   * Turns the font object into the PDF font.
   *
   * @return		the font
   */
  public synchronized Font toFont() {
    return toFont(Color.BLACK);
  }

  /**
   * Turns the font object into the PDF font.
   *
   * @param color	the color to use
   * @return		the font
   */
  public synchronized Font toFont(Color color) {
    if (m_Font == null)
      m_Font = new Font(getFontFamilyEnum(m_FontFamily), m_Size, m_FontFace, new BaseColor(color.getRGB()));
    return m_Font;
  }

  /**
   * Returns the closest Java font.
   *
   * @return		the closest java font
   */
  public java.awt.Font toJavaFont() {
    java.awt.Font	result;
    String		name;
    int			atts;

    // name
    if (m_FontFamily == Font.FontFamily.HELVETICA.ordinal())
      name = java.awt.Font.SANS_SERIF;
    else if (m_FontFamily == Font.FontFamily.TIMES_ROMAN.ordinal())
      name = java.awt.Font.SERIF;
    else if (m_FontFamily == Font.FontFamily.COURIER.ordinal())
      name = java.awt.Font.MONOSPACED;
    else
      name = java.awt.Font.DIALOG;

    // attributes
    atts = java.awt.Font.PLAIN;
    if ((m_FontFace & Font.BOLD) == Font.BOLD)
      atts |= java.awt.Font.BOLD;
    if ((m_FontFace & Font.ITALIC) == Font.ITALIC)
      atts |= java.awt.Font.ITALIC;

    result = new java.awt.Font(name, atts, (int) m_Size);

    return result;
  }

  /**
   * Turns the font family name into the constant of the font family.
   *
   * @param name	the font family name
   * @return		the font family identifier
   */
  public static int getFontFamily(String name) {
    int		result;

    if (name.equals(COURIER))
      result = Font.FontFamily.COURIER.ordinal();
    else if (name.equals(HELVETICA))
      result = Font.FontFamily.HELVETICA.ordinal();
    else if (name.equals(SYMBOL))
      result = Font.FontFamily.SYMBOL.ordinal();
    else if (name.equals(TIMES_ROMAN))
      result = Font.FontFamily.TIMES_ROMAN.ordinal();
    else if (name.equals(ZAPFDINGBATS))
      result = Font.FontFamily.ZAPFDINGBATS.ordinal();
    else
      result = 0;

    return result;
  }

  /**
   * Turns the font family into the String constant of the font family.
   *
   * @param font	the font to analyze
   * @return		the font family name
   */
  public static String getFontFamily(Font font) {
    return getFontFamily(font.getFamily().ordinal());
  }

  /**
   * Turns the font family into the enum of the font family.
   *
   * @param family	the family to analyze
   * @return		the font family enum
   */
  public static FontFamily getFontFamilyEnum(int family) {
    if (family == Font.FontFamily.COURIER.ordinal())
      return FontFamily.COURIER;
    if (family == Font.FontFamily.HELVETICA.ordinal())
      return FontFamily.HELVETICA;
    if (family == Font.FontFamily.SYMBOL.ordinal())
      return FontFamily.SYMBOL;
    if (family == Font.FontFamily.TIMES_ROMAN.ordinal())
      return FontFamily.TIMES_ROMAN;
    if (family == Font.FontFamily.ZAPFDINGBATS.ordinal())
      return FontFamily.ZAPFDINGBATS;

    return FontFamily.HELVETICA;
  }

  /**
   * Turns the font family into the String constant of the font family.
   *
   * @param family	the family to analyze
   * @return		the font family name
   */
  public static String getFontFamily(int family) {
    if (family == Font.FontFamily.COURIER.ordinal())
      return COURIER;
    if (family == Font.FontFamily.HELVETICA.ordinal())
      return HELVETICA;
    if (family == Font.FontFamily.SYMBOL.ordinal())
      return SYMBOL;
    if (family == Font.FontFamily.TIMES_ROMAN.ordinal())
      return TIMES_ROMAN;
    if (family == Font.FontFamily.ZAPFDINGBATS.ordinal())
      return ZAPFDINGBATS;

    return HELVETICA;
  }

  /**
   * Turns the font style name into the constant of the font style.
   *
   * @param name	the font style name
   * @return		the font style identifier
   */
  public static int getFontFace(String name) {
    int		result;

    if (name.equals(BOLD))
      result = Font.BOLD;
    else if (name.equals(ITALIC))
      result = Font.ITALIC;
    else if (name.equals(STRIKETHRU))
      result = Font.STRIKETHRU;
    else if (name.equals(UNDERLINE))
      result = Font.UNDERLINE;
    else
      result = 0;

    return result;
  }

  /**
   * Generates a list of font style names from the given font setup.
   *
   * @param font	the font to analyze
   * @return		the list of font names
   */
  public static String[] getFontFaces(Font font) {
    List<String>	result;

    result = new ArrayList<String>();
    if (font.isBold())
      result.add(BOLD);
    if (font.isItalic())
      result.add(ITALIC);
    if (font.isStrikethru())
      result.add(STRIKETHRU);
    if (font.isUnderlined())
      result.add(UNDERLINE);
    if (result.size() == 0)
      result.add(NORMAL);

    return result.toArray(new String[result.size()]);
  }
}
