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
 * InvestigatorTabRunnableJob.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.job;

import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;

/**
 * For executing Runnable's in a {@link AbstractInvestigatorTab}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InvestigatorTabRunnableJob
  extends InvestigatorTabJob {

  /** the runnable to execute. */
  protected Runnable m_Run;

  /**
   * Initializes the job.
   *
   * @param owner	the owning tab
   */
  public InvestigatorTabRunnableJob(AbstractInvestigatorTab owner, Runnable run) {
    super(owner, "Job");
    m_Run = run;
  }

  /**
   * Performs the actual execution.
   */
  @Override
  protected void doRun() {
    m_Run.run();
  }
}
