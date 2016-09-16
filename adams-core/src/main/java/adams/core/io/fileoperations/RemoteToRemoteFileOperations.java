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
 * RemoteToRemoteFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.io.TempUtils;

import java.io.File;

/**
 * File operations between two remote locations, using intermediate local
 * files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteToRemoteFileOperations
  extends AbstractFileOperations {

  private static final long serialVersionUID = -669774831741034540L;

  /** the source file operations. */
  protected RemoteFileOperations m_Source;

  /** the target file operations. */
  protected RemoteFileOperations m_Target;

  /**
   * Sets the source file operations.
   * 
   * @param value	the source
   */
  public void setSource(RemoteFileOperations value) {
    m_Source = value;
    m_Source.setDirection(RemoteDirection.REMOTE_TO_LOCAL);
  }

  /**
   * Returns the source file operations.
   * 
   * @return		the source
   */
  public RemoteFileOperations getSource() {
    return m_Source;
  }

  /**
   * Sets the target file operations.
   * 
   * @param value	the target
   */
  public void setTarget(RemoteFileOperations value) {
    m_Target = value;
    m_Target.setDirection(RemoteDirection.LOCAL_TO_REMOTE);
  }

  /**
   * Returns the target file operations.
   * 
   * @return		the target
   */
  public RemoteFileOperations getTarget() {
    return m_Target;
  }
  
  /**
   * Checks whether the given operation is supported.
   *
   * @param op		the operation to check
   * @return		true if supported
   */
  public boolean isSupported(Operation op) {
    switch (op) {
      case COPY:
      case MOVE:
	return m_Source.isSupported(op) && m_Target.isSupported(op);
      case RENAME:
      case DELETE:
	return m_Source.isSupported(op);
      default:
	throw new IllegalStateException("Unhandled operation: " + op);
    }
  }

  /**
   * Copies a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String copy(String source, String target) {
    String	result;
    File	tmp;

    tmp    = TempUtils.createTempFile("rrfo", ".tmp");
    result = m_Source.copy(source, tmp.getAbsolutePath());
    if (result == null)
      result = m_Target.copy(tmp.getAbsolutePath(), target);
    if (tmp.exists())
      tmp.delete();

    return result;
  }

  /**
   * Moves a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String move(String source, String target) {
    String	result;

    result = copy(source, target);
    if (result == null)
      m_Source.delete(source);

    return result;
  }

  /**
   * Renames a file. Uses {@link #getSource()}.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  public String rename(String source, String target) {
    return m_Source.rename(source, target);
  }

  /**
   * Deletes a file. Uses {@link #getSource()}.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String file) {
    return m_Source.delete(file);
  }
}
