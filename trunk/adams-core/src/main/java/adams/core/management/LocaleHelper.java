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
 * LocaleHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.LocaleDefinition;

/**
 * Helper class for locale setup (see <a href="http://en.wikipedia.org/wiki/ISO_639" target="_blank">ISO 639</a>).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocaleHelper {

  /** the props file. */
  public final static String FILENAME = "Locale.props";

  /** the locale to use. */
  public final static String LOCALE = "Locale";

  /** the constant for the system's default locale. */
  public final static String LOCALE_DEFAULT = "Default";

  /** the constant for the en_US locale. */
  public final static String LOCALE_EN_US = "en_US";

  /** the singleton. */
  protected static LocaleHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;
  
  /** the character for the decimal point. */
  protected char m_DecimalSeparator;
  
  /** the character for the grouping. */
  protected char m_GroupingSeparator;

  /** en_US locale instance. */
  protected Locale m_LocaleEnUS;
  
  /**
   * Initializes the helper.
   */
  private LocaleHelper() {
    super();
    initialize();
    reload();
  }
  
  /**
   * Initializes the helper.
   */
  protected void initialize() {
    m_LocaleEnUS = new Locale(LOCALE_EN_US);
  }

  /**
   * Initializes the locale with the current settings.
   */
  public void initializeLocale() {
    Locale	locale;
    
    locale = valueOf(getLocale());
    Locale.setDefault(locale);
    m_DecimalSeparator  = DecimalFormatSymbols.getInstance(locale).getDecimalSeparator();
    m_GroupingSeparator = DecimalFormatSymbols.getInstance(locale).getGroupingSeparator();
  }

  /**
   * Whether the settings got modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the locale.
   *
   * @return		the locale
   */
  public String getLocale() {
    return m_Properties.getProperty(LOCALE, Locale.getDefault().toString());
  }

  /**
   * Updates the locale.
   *
   * @param value	the locale
   */
  public void setLocale(String value) {
    m_Modified = true;
    m_Properties.setProperty(LOCALE, value);
  }
  
  /**
   * Returns the default locale.
   * 
   * @return		the default
   */
  public Locale getDefault() {
    return Locale.getDefault();
  }
  
  /**
   * Sets the new default locale.
   * 
   * @param value	the new default
   */
  public void setDefault(String value) {
    Locale.setDefault(valueOf(value));
  }
  
  /**
   * Sets the new default locale.
   * 
   * @param value	the new default
   */
  public void setDefault(Locale value) {
    Locale.setDefault(value);
  }
  
  /**
   * Returns the en_US locale.
   * 
   * @return		the en_US locale
   */
  public Locale getEnUS() {
    return m_LocaleEnUS;
  }
  
  /**
   * Returns the character representing the separator for the decimals, 
   * i.e., "." in english locales.
   * 
   * @return		the character
   */
  public char getDecimalSeparator() {
    return m_DecimalSeparator;
  }
  
  /**
   * Returns the character representing the separator for the decimals for the
   * specified locale, i.e., "." in english locales.
   * 
   * @param locale	the locale to get the separator for
   * @return		the character
   */
  public char getDecimalSeparator(Locale locale) {
    return DecimalFormatSymbols.getInstance(locale).getDecimalSeparator();
  }
  
  /**
   * Returns the character representing the separator for the grouping, 
   * i.e., "," in english locales.
   * 
   * @return		the character
   */
  public char getGroupingSeparator() {
    return m_GroupingSeparator;
  }
  
  /**
   * Returns the character representing the separator for the grouping for the
   * specified locale, i.e., "," in english locales.
   * 
   * @param locale	the locale to get the separator for
   * @return		the character
   */
  public char getGroupingSeparator(Locale locale) {
    return DecimalFormatSymbols.getInstance(locale).getGroupingSeparator();
  }

  /**
   * Returns the number format for the default locale.
   * Grouping is always disabled.
   * 
   * @return		the format
   * @see		#getNumberFormat(Locale)
   */
  public NumberFormat getNumberFormat() {
    return getNumberFormat(Locale.getDefault());
  }

  /**
   * Returns the number format for the specified locale. 
   * Grouping is always disabled.
   * 
   * @param locale	the locale to get the format for
   * @return		the format
   */
  public NumberFormat getNumberFormat(Locale locale) {
    NumberFormat	result;
    
    result = NumberFormat.getNumberInstance(locale);
    // let's simulate Double.toString(double), apart from scientific notation
    result.setGroupingUsed(false);
    result.setMaximumFractionDigits(14);
    result.setMinimumFractionDigits(1);
    
    return result;
  }

  /**
   * Returns the number format for the en_US locale.
   * Grouping is always disabled.
   * 
   * @return		the format
   * @see		#getNumberFormat(Locale)
   */
  public NumberFormat getNumberFormatEnUS() {
    return getNumberFormat(m_LocaleEnUS);
  }
  
  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(LocaleDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(LocaleDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static LocaleHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new LocaleHelper();

    return m_Singleton;
  }
  
  /**
   * Returns the locale as string.
   *
   * @param l		the locale object to convert
   * @return		the generated string
   */
  public static String toString(Locale l) {
    String	result;
    
    result = l.toString();
    if (result.equals(Locale.getDefault().toString()))
      result = LOCALE_DEFAULT;
    
    return result;
  }

  /**
   * Returns a locale generated from the string (eg en, en_US).
   *
   * @param str		the string to convert to a locale
   * @return		the generated locale
   */
  public static Locale valueOf(String str) {
    String[]	parts;
    
    if (str.equals(LOCALE_DEFAULT)) {
      return Locale.getDefault();
    }
    else {
      if (str.indexOf('_') == -1) {
	return new Locale(str);
      }
      else {
	parts = str.split("_");
	if (parts.length == 2) {
	  return new Locale(parts[0], parts[1]);
	}
	else if (parts.length == 3) {
	  return new Locale(parts[0], parts[1], parts[2]);
	}
	else {
	  System.err.println("Failed to parse locale '" + str + "', using default!");
	  return Locale.getDefault();
	}
      }
    }
  }
  
  /**
   * Returns all the locale IDs, including {@link #LOCALE_DEFAULT}.
   * 
   * @return		the IDs
   */
  public static String[] getIDs() {
    Locale[]		locales;
    ArrayList<String>	result;
    
    result  = new ArrayList<String>();
    locales = Locale.getAvailableLocales();
    for (Locale l: locales)
      result.add(l.toString());
    Collections.sort(result);
    result.add(0, LocaleHelper.LOCALE_DEFAULT);
    
    return result.toArray(new String[result.size()]);
  }
}
