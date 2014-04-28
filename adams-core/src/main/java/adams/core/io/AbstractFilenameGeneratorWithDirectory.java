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
 * AbstractFilenameGeneratorWithDirectory.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

/**
 * Ancestor for filename generators that use a directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilenameGeneratorWithDirectory
  extends AbstractFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -9019484464686478277L;

  /** the parent directory of the file. */
  protected PlaceholderDirectory m_Directory;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dir", "directory",
	    new PlaceholderDirectory("."));
  }

  /**
   * Sets the parent directory for the generated filename.
   *
   * @param value	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Directory = value;
    reset();
  }

  /**
   * Returns the parent directory for the generated filename.
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
    return "The parent directory of the generated filename.";
  }
}
