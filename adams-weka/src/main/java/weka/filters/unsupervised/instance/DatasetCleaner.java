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
package weka.filters.unsupervised.instance;

import java.util.HashSet;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import adams.data.weka.rowfinder.AbstractRowFinder;

/**
 <!-- globalinfo-start -->
 * Removes all rows from the data data that have been indentified.
 * <br><br>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -W &lt;row finder specification&gt;
 *  Full class name of row finder to use, followed
 *  by scheme options. eg:
 *   "adams.data.weka.rowfinder.NullFinder -D 1"
 *  (default: adams.data.weka.rowfinder.NullFinder)</pre>
 * 
 * <pre> -invert
 *  Whether to invert the found row indices.
 *  (default: off)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetCleaner
  extends AbstractRowFinderApplier {

  /** for serialization. */
  private static final long serialVersionUID = -111639385529662833L;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Removes all rows from the data data that have been indentified.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String rowFinderTipText() {
    return "The algorithm for locating rows to be removed from the dataset.";
  }

  /**
   * Determines the output format based on the input format and returns 
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat);
  }
  
  /**
   * Method that returns whether the filter may remove instances after
   * the first batch has been done.
   * 
   * @return		always true
   */
  @Override
  protected boolean mayRemoveInstances() {
    return true;
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
    HashSet<Integer>	set;
    int			i;
    
    result = new Instances(data, data.numInstances() - indices.length);
    set    = AbstractRowFinder.arrayToHashSet(indices);
    for (i = 0; i < data.numInstances(); i++) {
      if (!set.contains(i))
	result.add((Instance) data.instance(i).copy());
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
