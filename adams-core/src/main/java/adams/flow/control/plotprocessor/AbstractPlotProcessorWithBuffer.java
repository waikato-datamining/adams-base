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
 * AbstractPlotProcessorWithBuffer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.plotprocessor;

import adams.flow.container.SequencePlotterContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for post-processors that work on a buffer of plot containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data used by the buffer
 */
public abstract class AbstractPlotProcessorWithBuffer<T>
  extends AbstractPlotProcessor {

  private static final long serialVersionUID = -3966475656998629015L;

  /** for storing the plot data. */
  protected List<T> m_Data;

  /** the x index. */
  protected int m_XIndex;

  /** the last plot name. */
  protected String m_LastPlot;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data     = new ArrayList<>();
    m_XIndex   = 0;
    m_LastPlot = null;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Data.clear();
    m_LastPlot = null;
  }

  /**
   * Hook method before processing the plot container.
   *
   * @param cont	the container to process
   */
  protected void preProcess(SequencePlotterContainer cont) {
    String	name;

    super.preProcess(cont);

    name = cont.getValue(SequencePlotterContainer.VALUE_PLOTNAME, String.class);
    if ((m_LastPlot == null) || !m_LastPlot.equals(name)) {
      m_LastPlot = name;
      m_XIndex   = 0;
      m_Data.clear();
    }
  }

  /**
   * Post-processes the provided containers.
   * </p>
   * Default implementation drops containers that have invalid values for X or Y.
   *
   * @param conts	the containers to post-process
   * @return		null if no new containers were produced
   * @see		#isValid(Comparable)
   */
  protected List<SequencePlotterContainer> postProcess(List<SequencePlotterContainer> conts) {
    List<SequencePlotterContainer>	result;

    result = super.postProcess(conts);

    m_XIndex++;

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_Data.clear();
  }
}
