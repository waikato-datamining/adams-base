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
 * TesseractConfiguration.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/** 
 * Enumeration of languages that tesseract supports. 
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum TesseractLanguage
  implements EnumWithCustomDisplay<TesseractLanguage> {
  
  ALBANIAN("Albanian","sqi"),
  ARABIC("Arabic","ara"),
  AZERBAUIJANI("Azerbauijani","aze"),
  BULGARIAN("Bulgarian","bul"),
  CATALAN("Catalan","cat"),
  CHEROKEE("Cherokee","chr"),
  CROATION("Croation","hrv"),
  CZECH("Czech","ces"),
  DANISH("Danish","dan"),
  DANISH_FRAKTUR("Danish Fraktur","dan-frak"),
  DUTCH("Dutch","nld"),
  ENGLISH("English","eng"),
  ESPERANTO("Esperanto","epo"),
  ESTONIAN("Estonian","est"),
  FINNISH("Finnish","fin"),
  FRENCH("French","fra"),
  GALICIAN("Galician","glg"),
  GERMAN("German","deu"),
  GREEK("Greek","ell"),
  HEBREW("Hebrew","heb"),
  HINDI("Hindi","hin"),
  HUNGARIAN("Hungarian","hun"),
  INDONESIAN("Indonesian","ind"),
  ITALIAN("Italian","ita"),
  JAPANESE("Japanese","jpn"),
  KOREAN("Korean","kor"),
  LATVIAN("Latvian","lav"),
  LITHUANIAN("Lithuanian","lit"),
  NORWEGIAN("Norwegian","nor"),
  OLD_ENGLISH("Old English","enm"),
  OLD_FRENCH("Old French","frm"),
  POLISH("Polish","pol"),
  PORTUGUESE("Portuguese","por"),
  ROMANIAN("Romanian","ron"),
  RUSSIAN("Russian","rus"),
  SERBIAN("Serbian","srp"),
  SIMPLIFIED_CHINESE("Simplified Chinese","chi_sim"),
  SLOVAKIAN("Slovakian","slk"),
  SLOVENIAN("Slovenian","slv"),
  SPANISH("Spanish","spa"),
  SWEDISH("Swedish","swe"),
  TAGALOG("Tagalog","tgl"),
  TAMIL("Tamil","tam"),
  TELUGU("Telugu","tel"),
  THAI("Thai","tha"),
  TRADITIONAL_CHINESE("Traditional Chinese","chi_tra"),
  TURKISH("Turkish","tur"),
  UKRAINIAN("Ukrainian","ukr"),
  VIETNAMESE("Vietnamese","vie");

  /** the display string. */
  private String m_Display;

  /** the ISO 639-2 code. */
  private String m_Code;

  /** the commandline string. */
  private String m_Raw;

  /**
   * The constructor.
   *
   * @param display	the string to use as display
   * @param code	the ISO 639-2 code
   */
  private TesseractLanguage(String display, String code) {
    m_Display = display;
    m_Code    = code;
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
   * Returns the ISO 639-2 string.
   *
   * @return		the ISO 639-2 string
   */
  public String toCode() {
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
  public TesseractLanguage parse(String s) {
    return (TesseractLanguage) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((TesseractLanguage) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str	the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static TesseractLanguage valueOf(AbstractOption option, String str) {
    TesseractLanguage	result;

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
      for (TesseractLanguage lang: values()) {
        if (lang.toDisplay().equals(str)) {
          result = lang;
          break;
        }
      }
    }

    // try code
    if (result == null) {
      for (TesseractLanguage lang: values()) {
        if (lang.toCode().equals(str)) {
          result = lang;
          break;
        }
      }
    }

    return result;
  }
}