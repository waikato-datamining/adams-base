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
 * JobResult.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.multiprocess;

import java.io.Serializable;

/**
 * The result of Job Execution.
 * TODO: should it have parameters?
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobResult
  implements Serializable  {

  /** serial uid. */
  private static final long serialVersionUID = -6567653422299525533L;

  /** info string. */
  private String m_info;

  /** job execution successful? */

  private boolean m_success;

  /** Should job be retried? */
  private boolean m_should_retry;

  /**
   * New jobresult.
   *
   * @param i		info string
   */
  public JobResult(String i) {
    this(i, false);
  }

  /**
   * New jobresult.
   *
   * @param i		info string
   * @param success	success?
   */
  public JobResult(String i, boolean success) {
    super();

    m_should_retry = false;
    m_info         = i;
    m_success      = success;
  }

  /**
   * Get info string.
   *
   * @return 		info string
   */
  public String toString() {
    return m_info;
  }

  /**
   * Execution successful?
   *
   * @return		success?
   */
  public boolean getSuccess() {
    return m_success;
  }

  /**
   * Set execution successful.
   *
   * @param value	was successful?
   */
  public void setSuccess(boolean value) {
    m_success = value;
  }

  /**
   * Should this job be retried?
   *
   * @return	retry job?
   */
  public boolean getRetry() {
    return m_should_retry;
  }

  /**
   * Set should job be retried on fail.
   *
   * @param value	retry?
   */
  public void setRetry(boolean value) {
    m_should_retry = value;
  }
}
