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
 * AbstractIMAPFolderOperation.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.imaptransformer;

import adams.core.QuickInfoHelper;
import adams.flow.standalone.IMAPConnection;

/**
 * Ancestor of operations that work on a specific folder.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> the type of output that is being received
 * @param <O> the type of output that is being generated
 */
public abstract class AbstractIMAPFolderOperation<I, O>
  extends AbstractIMAPOperation<I, O> {

  private static final long serialVersionUID = -4905051738206018923L;

  /** the folder to query. */
  protected String m_Folder;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "folder", "folder",
      getDefaultFolder());
  }

  /**
   * Returns the default folder.
   *
   * @return		the default
   */
  protected String getDefaultFolder() {
    return "INBOX";
  }

  /**
   * Sets the folder to query.
   *
   * @param value	the folder
   */
  public void setFolder(String value) {
    m_Folder = value;
    reset();
  }

  /**
   * Returns the folder to query.
   *
   * @return 		the folder
   */
  public String getFolder() {
    return m_Folder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String folderTipText() {
    return "The folder to query.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "folder", (m_Folder.isEmpty() ? "-not set-" : m_Folder), "folder: ");
  }

  /**
   * Hook method for checks before executing the operation.
   *
   * @param conn	the connection to use
   * @param input 	the input data
   * @return		the generated output, can be null
   */
  @Override
  protected String check(IMAPConnection conn, I input) {
    String	result;

    result = super.check(conn, input);

    if (result == null) {
      if (m_Folder.isEmpty())
	result = "No folder specified!";
    }

    return result;
  }
}
