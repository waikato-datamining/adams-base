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
 * AbstractNegativeRegionsGenerator.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Ancestor for algorithms for generating negative regions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNegativeRegionsGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter, StoppableWithFeedback {

  private static final long serialVersionUID = 3551924872679626711L;

  /** whether the generator was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation just returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook methods for checks before processing.
   *
   * @param cont	the image container to check
   * @return		null if successful, otherwise error message
   */
  protected String check(AbstractImageContainer cont) {
    if (cont == null)
      return "No image container provided!";
    return null;
  }

  /**
   * Generates the negative regions.
   *
   * @param cont	the image container to generate the regions for
   * @return		the generated regions
   */
  protected abstract LocatedObjects doGenerateRegions(AbstractImageContainer cont);

  /**
   * Generates the negative regions.
   *
   * @param cont	the image container to generate the regions for
   * @return		the generated regions
   */
  public LocatedObjects generateRegions(AbstractImageContainer cont) {
    String	msg;

    m_Stopped = false;
    msg = check(cont);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerateRegions(cont);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }
}
