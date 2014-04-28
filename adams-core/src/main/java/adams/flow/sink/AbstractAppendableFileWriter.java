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
 * AbstractAppendableFileWriter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for file writers that allow appending to the file instead of just
 * replacing any existing files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAppendableFileWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 1779528876252782006L;

  /** whether to append or not. */
  protected boolean m_Append;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "append", "append",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    if (result != null)
      result += QuickInfoHelper.toString(this, "append", (m_Append ? " (append)" : " (overwrite)"));

    return result;
  }

  /**
   * Sets whether to append to the file or not.
   *
   * @param value 	true if appending to file instead of rewriting it
   */
  public void setAppend(boolean value) {
    m_Append = value;
    reset();
  }

  /**
   * Returns whether files gets only appended or not.
   *
   * @return 		true if appending is turned on
   */
  public boolean getAppend() {
    return m_Append;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String appendTipText();
}
