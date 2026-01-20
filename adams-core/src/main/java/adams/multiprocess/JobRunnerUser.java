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
 * JobRunnerUser.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

/**
 * Interface for classes that can use a JobRunner for execution.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface JobRunnerUser {

  /**
   * Sets whether to use a JobRunner to limit resource use.
   *
   * @param value	true if to use
   */
  public void setUseJobRunner(boolean value);

  /**
   * Returns whether to use a JobRunner to limit resource use.
   *
   * @return		true if to use
   */
  public boolean getUseJobRunner();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useJobRunnerTipText();

  /**
   * Sets the job runner to use.
   *
   * @param value	the job runner to use
   */
  public void setJobRunner(JobRunner value);

  /**
   * Returns the job runner to use.
   *
   * @return		the job runner to use
   */
  public JobRunner getJobRunner();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String jobRunnerTipText();
}
