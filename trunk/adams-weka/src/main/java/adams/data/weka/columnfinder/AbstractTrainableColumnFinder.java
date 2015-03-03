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
 * AbstractTrainableColumnFinder.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import weka.core.Instances;

/**
 * Ancestor for {@link ColumnFinder} algorithms that can be trained.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrainableColumnFinder
  extends AbstractColumnFinder
  implements TrainableColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 3800769979430280486L;

  /** whether the column finder was trained already. */
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
   * Performs the actual training of the column finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  protected abstract boolean doTrainColumnFinder(Instances data);

  /**
   * Trains the column finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  public boolean trainColumnFinder(Instances data) {
    m_Trained = doTrainColumnFinder(data);
    
    if (isLoggingEnabled())
      getLogger().info("Training success: " + m_Trained);
    
    return m_Trained;
  }
  
  /**
   * Checks whether the column finder has been trained.
   * 
   * @return		true if the column finder has been trained already
   */
  @Override
  public boolean isColumnFinderTrained() {
    return m_Trained;
  }

  /**
   * Checks the data.
   * <p/>
   * Trains the column finder with the given data if not yet trained.
   * 
   * @param data	the data to check
   */
  @Override
  protected void check(Instances data) {
    super.check(data);
    
    if (!isColumnFinderTrained()) {
      if (!trainColumnFinder(data))
	throw new IllegalStateException("Unable to train column finder!");
    }
  }
}
