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
 * TesseractHelper.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.tesseract;

import java.util.ArrayList;
import java.util.List;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOption;
import adams.env.Environment;
import adams.env.TesseractDefinition;
import adams.flow.core.TesseractLanguage;
import adams.flow.core.TesseractPageSegmentation;

/**
 * Helper class for locale setup (see <a href="http://en.wikipedia.org/wiki/ISO_639" target="_blank">ISO 639</a>).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TesseractHelper {

  /** the props file. */
  public final static String FILENAME = "Tesseract.props";

  /** the executable to use. */
  public final static String EXECUTABLE = "Executable";

  /** the language to use. */
  public final static String LANGUAGE = "Language";

  /** the singleton. */
  protected static TesseractHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;
  
  /**
   * Initializes the helper.
   */
  private TesseractHelper() {
    super();
    reload();
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
   * Returns the executable.
   *
   * @return		the executable
   */
  public String getExecutable() {
    return m_Properties.getProperty(EXECUTABLE, "");
  }

  /**
   * Updates the executable.
   *
   * @param value	the executable
   */
  public void setExecutable(String value) {
    m_Modified = true;
    m_Properties.setProperty(EXECUTABLE, value);
  }

  /**
   * Returns the language.
   *
   * @return		the language
   */
  public TesseractLanguage getLanguage() {
    return TesseractLanguage.valueOf((AbstractOption) null, m_Properties.getProperty(LANGUAGE, TesseractLanguage.ENGLISH.toRaw()));
  }

  /**
   * Updates the language.
   *
   * @param value	the language
   */
  public void setLanguage(TesseractLanguage value) {
    m_Modified = true;
    m_Properties.setProperty(LANGUAGE, value.toRaw());
  }
  
  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(TesseractDefinition.KEY);
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

    result = Environment.getInstance().write(TesseractDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }
  
  /**
   * Assembles the tesseract command for the given input/output. Uses default
   * values for executable, language and page segmentation, output in ASCII.
   * 
   * @param input	the input file to process
   * @param outputbase	the output base to use
   * @param config	the config file, null or directory to ignore
   * @return		the command
   */
  public String[] getCommand(String input, String outputbase, PlaceholderFile config) {
    return getCommand(null, input, outputbase, null, null, config, false);
  }
  
  /**
   * Assembles the tesseract command for the given input/output.
   * 
   * @param exe		the executable, null to use default
   * @param input	the input file to process
   * @param outputbase	the output base to use
   * @param lang	the language to use, null to use default
   * @param seg		the page segemention, null to use default
   * @param config	the config file, null or directory to ignore
   * @param hocr	whether to output in hOCR format instead of ASCII
   * @return		the command
   */
  public String[] getCommand(String exe, String input, String outputbase, TesseractLanguage lang, TesseractPageSegmentation seg, PlaceholderFile config, boolean hocr) {
    List<String>	result;
    
    if (exe == null)
      exe = getExecutable();
    if (lang == null)
      lang = getLanguage();
    if (seg == null)
      seg = TesseractPageSegmentation.FULL_AUTO_NO_OSD;

    result = new ArrayList<String>();
    result.add(exe);
    result.add(input);
    result.add(outputbase);
    result.add("-l");
    result.add(lang.toCode());
    result.add("-psm");
    result.add("" + seg.toCode());
    if ((config != null) && config.exists() && !config.isDirectory())
      result.add(config.getAbsolutePath());
    if (hocr)
      result.add("hocr");
    
    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static TesseractHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new TesseractHelper();

    return m_Singleton;
  }
}
