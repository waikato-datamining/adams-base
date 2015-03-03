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
 * TitleGenerator.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.io.File;
import java.io.Serializable;

/**
 * A simple helper class for generating titles for frames and dialogs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TitleGenerator
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 6278140781437652946L;

  /** the base title. */
  protected String m_Title;

  /** whether to split the full filename into path and filename. */
  protected boolean m_Split;

  /** whether the generator is enabled. */
  protected boolean m_Enabled;
  
  /**
   * Initializes the generator.
   *
   * @param title	the base title.
   * @param split	whether to split the filename into path/name
   */
  public TitleGenerator(String title, boolean split) {
    super();

    m_Title   = title;
    m_Split   = split;
    m_Enabled = true;
  }

  /**
   * Sets the base title.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
  }

  /**
   * Returns the base title.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns whether the filename is split into path and name part.
   *
   * @return		true if split
   */
  public boolean getSplit() {
    return m_Split;
  }

  /**
   * Sets whether the generator is enabled.
   * 
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
  }
  
  /**
   * Returns whether the generator is enabled.
   * 
   * @return		true if enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }
  
  /**
   * Generates the default title.
   *
   * @return		the generated title
   */
  public String generate() {
    return generate(false);
  }

  /**
   * Generates the default title.
   *
   * @param modified	whether the data is modified
   * @return		the generated title
   */
  public String generate(boolean modified) {
    return generate((File) null, modified);
  }

  /**
   * Generates a title for the given file.
   *
   * @param filename	the file to generate the title for, can be null
   * @return		the generated title
   */
  public String generate(String filename) {
    return generate(filename, false);
  }

  /**
   * Generates a title for the given file.
   *
   * @param filename	the file to generate the title for, can be null
   * @param modified	whether the file is modified
   * @return		the generated title
   */
  public String generate(String filename, boolean modified) {
    if (filename == null)
      return generate((File) null, modified);
    else
      return generate(new File(filename), modified);
  }

  /**
   * Generates a title for the given file.
   *
   * @param file	the file to generate the title for, can be null
   * @return		the generated title
   */
  public String generate(File file) {
    return generate(file, false);
  }

  /**
   * Generates a title for the given file.
   *
   * @param file	the file to generate the title for, can be null
   * @param modified	whether the file is modified
   * @return		the generated title
   */
  public String generate(File file, boolean modified) {
    String	result;

    result = m_Title;

    if (file != null) {
      file = new File(file.getAbsolutePath());
      if (m_Split)
	result += " [" + file.getName() + " -- " + file.getParent() + "]";
      else
	result += " [" + file.getAbsolutePath() + "]";
    }

    if (modified)
      result = "*" + result;

    return result;
  }
}
