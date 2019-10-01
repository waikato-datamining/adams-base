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
 * AbstractBufferingSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for queues that need to buffer output from eg another process.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBufferingSource
  extends AbstractSource {

  private static final long serialVersionUID = 3817717770718728821L;

  /** the queue to buffer the output in. */
  protected List<Object> m_Queue;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList<>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    if (m_Queue != null)
      m_Queue.clear();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return isExecuted() && (m_Queue != null) && (m_Queue.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = null;

    if ((m_Queue != null) && (m_Queue.size() > 0))
      result = new Token(m_Queue.remove(0));

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Queue != null)
      m_Queue.clear();

    super.wrapUp();
  }
}
