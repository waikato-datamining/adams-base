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
 * ArchiveManager.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;

import java.io.InputStream;

/**
 * Interface for archive managers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ArchiveManager
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Initializes the archive.
   *
   * @param output	the file name for the archive
   * @return		null if successful, otherwise error message
   */
  public String initialize(PlaceholderFile output);

  /**
   * Adds the file to the archive.
   *
   * @param data	the file to add
   * @param name 	the name for the file in the archive
   * @return		null if successful, otherwise error message
   */
  public String add(PlaceholderFile data, String name);

  /**
   * Adds the data from the input stream to the archive.
   * Caller needs to close input stream.
   *
   * @param data	the data to add
   * @param name 	the name for the data in the archive
   * @return		null if successful, otherwise error message
   */
  public String add(InputStream data, String name);

  /**
   * Adds the data to the archive.
   *
   * @param data	the data to add
   * @param name 	the name for the data in the archive
   * @return		null if successful, otherwise error message
   */
  public String add(byte[] data, String name);

  /**
   * Finalizes the archive.
   *
   * @return		null if successful, otherwise error message
   */
  public String close();
}
