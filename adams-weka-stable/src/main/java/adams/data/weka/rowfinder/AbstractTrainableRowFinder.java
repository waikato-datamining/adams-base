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
 * AbstractTrainableRowFinder.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import weka.core.Instances;

/**
 * Ancestor for {@link RowFinder} algorithms that can be trained.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrainableRowFinder
  extends AbstractRowFinder
  implements TrainableRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 3800769979430280486L;

  /** whether the row finder was trained already. */
  protected boolean m_Trained;
  
  /**
   * Resets the object, including the trained state.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Trained = false;
  }

  /**
   * Performs the actual training of the row finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  protected abstract boolean doTrainRowFinder(Instances data);

  /**
   * Trains the row finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  public boolean trainRowFinder(Instances data) {
    m_Trained = doTrainRowFinder(data);
    
    if (isLoggingEnabled())
      getLogger().info("Training success: " + m_Trained);
    
    return m_Trained;
  }
  
  /**
   * Checks whether the row finder has been trained.
   * 
   * @return		true if the row finder has been trained already
   */
  @Override
  public boolean isRowFinderTrained() {
    return m_Trained;
  }

  /**
   * Checks the data.
   * <br><br>
   * Trains the row finder with the given data if not yet trained.
   * 
   * @param data	the data to check
   */
  @Override
  protected void check(Instances data) {
    super.check(data);
    
    if (!isRowFinderTrained()) {
      if (!trainRowFinder(data))
	throw new IllegalStateException("Unable to train row finder!");
    }
  }
}
