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
 * ArchiveHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;

import java.io.File;

/**
 * Interface of archive handlers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ArchiveHandler
  extends OptionHandler {

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  public String[] getExtensions();

  /**
   * Sets the archive to get the files from.
   *
   * @param value	the archive
   */
  public void setArchive(PlaceholderFile value);

  /**
   * Returns the current archive.
   *
   * @return		the archive
   */
  public PlaceholderFile getArchive();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String archiveTipText();

  /**
   * Returns the files stored in the archive.
   *
   * @return		the files
   * @see		#extract(String, File)
   */
  public String[] getFiles();

  /**
   * Extracts the specified file and saves it locally.
   *
   * @param archiveFile	the file in the archive to extract
   * @param outFile	the local file to store the content in
   * @return		true if successfully extracted
   */
  public boolean extract(String archiveFile, File outFile);
}
