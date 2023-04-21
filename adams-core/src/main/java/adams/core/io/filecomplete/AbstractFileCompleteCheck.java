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
 * AbstractFileCompleteCheck.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filecomplete;

import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.logging.Level;

/**
 * Ancestor for classes that check whether a file is complete.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileCompleteCheck
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -8384228126040647401L;

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer	the buffer to check
   * @return		true if complete
   */
  public abstract boolean isComplete(byte[] buffer);

  /**
   * Checks whether the file is complete.
   *
   * @param file	the file to check
   * @param bufLen 	the buffer length to use
   * @return		true if complete
   */
  protected boolean isComplete(File file, int bufLen) {
    boolean		result;
    RandomAccessFile 	raf;
    byte[]		buffer;
    long		fileLen;

    raf = null;
    try {
      fileLen = file.length();
      if (bufLen > fileLen)
	bufLen = (int) fileLen;
      buffer = new byte[bufLen];
      raf    = new RandomAccessFile(file.getAbsolutePath(), "r");
      if (file.length() > bufLen) {
	raf.seek(file.length() - bufLen);
	raf.read(buffer, 0, bufLen);
	result = isComplete(buffer);
      }
      else {
	// too small
	result = false;
      }
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to extract bytes from: " + file, e);
      result = true;
    }
    finally {
      FileUtils.closeQuietly(raf);
    }

    return result;
  }

  /**
   * Checks whether the file is complete.
   *
   * @param file	the file to check
   * @return		true if complete
   */
  public abstract boolean isComplete(File file);
}
