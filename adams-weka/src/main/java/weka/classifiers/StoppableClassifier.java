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
 * StoppableClassifier.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers;

import adams.core.StoppableWithFeedback;

/**
 * Ancestor for classifiers that can be stopped.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class StoppableClassifier
  extends AbstractClassifier
  implements StoppableWithFeedback {

  private static final long serialVersionUID = -7417786077923225941L;

  /** whether the classifier was stopped. */
  protected boolean m_Stopped;

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}
