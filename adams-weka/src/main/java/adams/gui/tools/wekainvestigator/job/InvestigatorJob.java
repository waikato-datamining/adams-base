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
 * InvestigatorJob.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.job;

import adams.gui.tools.wekainvestigator.InvestigatorPanel;

/**
 * For running jobs in the {@link InvestigatorPanel}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class InvestigatorJob
  implements Runnable {

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /** the title of the job. */
  protected String m_Title;

  /**
   * Initializes the job.
   *
   * @param owner	the owning panel
   */
  public InvestigatorJob(InvestigatorPanel owner, String title) {
    m_Owner = owner;
    m_Title = title;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the title of the job.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Gets called before the execution.
   */
  protected void preRun() {
    m_Owner.logAndShowMessage(m_Title);
  }

  /**
   * Performs the actual execution.
   */
  protected abstract void doRun();

  /**
   * Gets called after execution.
   */
  protected void postRun() {
    m_Owner.executionFinished();
  }

  /**
   * Executes the job.
   */
  @Override
  public void run() {
    preRun();
    try {
      doRun();
    }
    catch (ThreadDeath d) {
      // ignored, since user initiated
    }
    catch (Throwable t) {
      m_Owner.logError("Failed to execute job:\n" + m_Title, t, "Job error");
    }
    postRun();
  }
}
