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
 * DatasetCleaner.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.unsupervised.attribute;

import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.filters.Filter;

/**
 <!-- globalinfo-start -->
 * Removes all columns from the data data that have been indentified.
 * <p/>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -W &lt;column finder specification&gt;
 *  Full class name of column finder to use, followed
 *  by scheme options. eg:
 *   "adams.data.weka.columnfinder.NullFinder -D 1"
 *  (default: adams.data.weka.columnfinder.NullFinder)</pre>
 * 
 * <pre> -invert
 *  Whether to invert the found column indices.
 *  (default: off)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetCleaner
  extends AbstractColumnFinderApplier {

  /** for serialization. */
  private static final long serialVersionUID = -111639385529662833L;

  /** the remove filter to use. */
  protected Remove m_Remove;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Removes all columns from the data data that have been indentified.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String columnFinderTipText() {
    return "The algorithm for locating columns to be removed from the dataset.";
  }

  /**
   * Determines the output format based on the input format and returns 
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @thcolumns Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat);
  }

  /**
   * Applies the indices to the data. In case inverting is enabled, the indices
   * have already been inverted.
   * 
   * @param data	the data to process
   * @param indices	the indices to use
   * @return		the processed data
   */
  @Override
  protected Instances apply(Instances data, int[] indices) {
    Instances		result;

    try {
      if (!isFirstBatchDone()) {
	m_Remove = new Remove();
	m_Remove.setAttributeIndicesArray(indices);
	m_Remove.setInputFormat(data);
      }

      result = Filter.useFilter(data, m_Remove);
      
      if (!isFirstBatchDone())
	setOutputFormat(result);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to remove attributes!", e);
    }
    
    return result;
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
}
