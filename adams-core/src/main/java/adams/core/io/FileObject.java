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
 * FileObject.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Interface for wrappers around files to avoid costly API calls by caching
 * values also abstracting file representation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FileObject
  extends Serializable {

  /**
   * Returns the wrapped file.
   *
   * @return		the file
   */
  public File getFile();

  /**
   * Returns the actual target (if possible) in case of a link.
   *
   * @return		the actual file
   */
  public File getActualFile();

  /**
   * Returns the file name.
   *
   * @return		the name
   */
  public String getName();

  /**
   * Returns the size of the file.
   *
   * @return		the size
   */
  public long getLength();

  /**
   * Returns whether the file represents a directory.
   *
   * @return		true if directory
   */
  public boolean isDirectory();

  /**
   * Returns the date when the file was last modified.
   *
   * @return		date when last modified
   */
  public Date getLastModified();

  /**
   * Returns whether the file is hidden.
   *
   * @return		true if hidden
   */
  public boolean isHidden();

  /**
   * Returns whether the file represents a link.
   *
   * @return		true if link
   */
  public boolean isLink();

  /**
   * Returns whether the file is a local file.
   *
   * @return		true if local
   */
  public boolean isLocal();

  /**
   * Returns just the file's string representation.
   *
   * @return		the string representation
   */
  @Override
  public String toString();
}
