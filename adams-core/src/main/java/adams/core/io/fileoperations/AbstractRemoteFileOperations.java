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
 * AbstractRemoteFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

/**
 * Ancestor for remote file operation classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRemoteFileOperations
  extends AbstractFileOperations
  implements RemoteFileOperations {

  private static final long serialVersionUID = -5717588876672274558L;

  /** the direction. */
  protected RemoteDirection m_Direction;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_Direction = RemoteDirection.LOCAL_TO_REMOTE;
  }

  /**
   * Sets the direction for the operations.
   *
   * @param value	the direction
   */
  public void setDirection(RemoteDirection value) {
    m_Direction = value;
  }

  /**
   * Returns the direction of the operations.
   *
   * @return		the direction
   */
  public RemoteDirection getDirection() {
    return m_Direction;
  }
}
