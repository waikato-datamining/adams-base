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
 * AbstractClusterMembershipPostProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.wekaclusterer;

import weka.clusterers.Clusterer;
import weka.core.Instances;
import adams.flow.container.WekaModelContainer;

/**
 * Ancestor for post-processors that require a built clusterer and the dataset 
 * that was used to build the clusterer to be present in the model container.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClusterMembershipPostProcessor
  extends AbstractClustererPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 1028684763791331327L;

  /** the clustered full dataset. */
  public final static String VALUE_CLUSTERED_DATASET = "Clustered dataset";
  
  /**
   * Returns the keys that the processor adds/modifies.
   * 
   * @return		the keys, null of zero-length array for none
   */
  protected String[] getContainerKeys() {
    return new String[]{VALUE_CLUSTERED_DATASET};
  }
  
  /**
   * Checks the model container.
   * <br><br>
   * Checks for Clusterer and Instances.
   * 
   * @param cont	the container to check
   * @see		WekaModelContainer#VALUE_MODEL
   * @see		WekaModelContainer#VALUE_DATASET
   */
  protected void check(WekaModelContainer cont) {
    super.check(cont);
    
    if (!cont.hasValue(WekaModelContainer.VALUE_MODEL))
      throw new IllegalStateException("No clusterer model in container!");
    if (!cont.hasValue(WekaModelContainer.VALUE_DATASET))
      throw new IllegalStateException("No full dataset in container!");
  }
  
  /**
   * Performs some form of processing on the full dataset.
   */
  protected abstract Instances processDatasetWithClusterer(Instances data, Clusterer clusterer);
  
  /**
   * Performs the actual post-processing. Adds a new dataset to the container,
   * using the key VALUE_CLUSTERED_DATASET
   * 
   * @param cont	the container to post-process
   * @return		the post-processed container
   * @see		#VALUE_CLUSTERED_DATASET
   */
  protected WekaModelContainer doPostProcess(WekaModelContainer cont) {
    Clusterer	clusterer;
    Instances	data;
    Instances	processed;
    
    clusterer = (Clusterer) cont.getValue(WekaModelContainer.VALUE_MODEL);
    data      = (Instances) cont.getValue(WekaModelContainer.VALUE_DATASET);

    processed = processDatasetWithClusterer(data, clusterer);
    
    cont.addAdditionalName(VALUE_CLUSTERED_DATASET);
    cont.setValue(VALUE_CLUSTERED_DATASET, processed);
    
    return cont;
  }
}
