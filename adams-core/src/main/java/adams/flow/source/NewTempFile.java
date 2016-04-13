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
 * NewTempFile.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.TempUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewTempFile
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = 7272049518765623563L;

  /** the directory for the temp file. */
  protected PlaceholderDirectory m_Directory;

  /** the prefix. */
  protected String m_Prefix;

  /** the suffix (incl dot). */
  protected String m_Suffix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a unique, temporary filename without creating the file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "directory", "directory",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "suffix", "suffix",
      "");
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "directory", m_Directory, "dir: ");
    result += QuickInfoHelper.toString(this, "prefix", (m_Prefix.isEmpty() ? "-none-" : m_Prefix), ", prefix: ");
    result += QuickInfoHelper.toString(this, "suffix", (m_Suffix.isEmpty() ? "-none-" : m_Suffix), ", suffix: ");

    return result;
  }

  /**
   * Sets the directory to use.
   *
   * @param value	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Directory = value;
    reset();
  }

  /**
   * Returns the directory to use.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getDirectory() {
    return m_Directory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The directory to use for the temp file.";
  }

  /**
   * Sets the prefix to use.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use.";
  }

  /**
   * Sets the suffix to use.
   *
   * @param value	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix to use.
   *
   * @return		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix to use.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_OutputToken = new Token(TempUtils.createTempFile(m_Directory, m_Prefix, m_Suffix));
    return null;
  }
}
