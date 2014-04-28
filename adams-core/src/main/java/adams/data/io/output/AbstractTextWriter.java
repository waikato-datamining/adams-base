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
 * AbstractTextWriter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that can write text documents.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextWriter
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -432793583505607999L;

  /** whether the writer is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "enabled", "enabled",
	    true);
  }

  /**
   * Sets whether the writer is enabled.
   *
   * @param value 	true if to enable writer
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the writer is enabled.
   *
   * @return 		true if writer is enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Whether the writer is enabled.";
  }

  /**
   * Writes the given content under the specified name.
   *
   * @param content	the content to write
   * @param name	the name under which to save the content
   * @return		if a file was generated, the filename the content was written
   * 			as, otherwise null
   */
  protected abstract String doWrite(String content, String name);

  /**
   * Writes the given content under the specified name.
   *
   * @param content	the content to write
   * @param name	the name under which to save the content
   * @return		if a file was generated, the filename the content was written
   * 			as, otherwise null
   */
  public String write(String content, String name) {
    if (!m_Enabled)
      return null;
    else
      return doWrite(content, name);
  }
}
