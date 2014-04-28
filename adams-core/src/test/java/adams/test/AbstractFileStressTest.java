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
 * AbstractFileStressTest.java
 * Copyright (C) 2009-2013 University of Waikato
 */

package adams.test;

import adams.core.io.PlaceholderFile;

/**
 * Abstract ancestor of classes for stress-testing.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileStressTest
  extends AbstractStressTest {

  /** for serialization. */
  private static final long serialVersionUID = -2535320030771462923L;

  /** the file to execute. */
  protected PlaceholderFile m_File;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "file",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the file to execute.
   *
   * @param value 	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file to execute.
   *
   * @return 		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The file to load and execute.";
  }

  /**
   * Connects to the database and checks whether we really have a file for
   * execution.
   *
   * @return		true if things can proceed
   */
  @Override
  protected boolean preExecute() {
    boolean	result;

    result = super.preExecute();

    if (result) {
      if (!m_File.isFile()) {
	getLogger().severe("'" + m_File +  "' is not pointing to a file!");
	result = false;
      }
    }

    return result;
  }
}
