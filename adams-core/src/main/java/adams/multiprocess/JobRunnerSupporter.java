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
 * JobRunnerSupporter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

/**
 * Interface for classes that can offload their processing into a jobs
 * executed by a JobRunner instance.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface JobRunnerSupporter {

  /**
   * Sets whether to offload processing to a JobRunner instance if available.
   *
   * @param value	if true try to find/use a JobRunner instance
   */
  public void setPreferJobRunner(boolean value);

  /**
   * Returns whether to offload processing to a JobRunner instance if available.
   *
   * @return		if true try to find/use a JobRunner instance
   */
  public boolean getPreferJobRunner();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preferJobRunnerTipText();
}
