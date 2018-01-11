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
 * AbstractClustererPostProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.wekaclusterer;

import adams.core.AdditionalInformationHandler;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.WekaModelContainer;

/**
 * Ancestor for post-processors for output that the WekaClusterer transformer
 * produces.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClustererPostProcessor
  extends AbstractOptionHandler 
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6272798304877142955L;
  
  /**
   * Returns the keys that the processor adds/modifies.
   * 
   * @return		the keys, null of zero-length array for none
   */
  protected abstract String[] getContainerKeys();
  
  /**
   * Returns the additional information.
   * 
   * @return		the additional information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;
    String[]		keys;
    
    result = new StringBuilder();

    keys = getContainerKeys();
    if ((keys != null) && (keys.length > 0)) {
      result.append("Container keys: ");
      result.append(Utils.flatten(keys, ", "));
    }
    
    return result.toString();
  }

  /**
   * Checks the model container.
   * <br><br>
   * Default implementation only ensures that it is not null.
   * 
   * @param cont	the container to check
   */
  protected void check(WekaModelContainer cont) {
    if (cont == null)
      throw new IllegalStateException(
	  "No model container (" + WekaModelContainer.class.getName() + ") provided!");
  }
  
  /**
   * Performs the actual post-processing.
   * 
   * @param cont	the container to post-process
   * @return		the post-processed container
   */
  protected abstract WekaModelContainer doPostProcess(WekaModelContainer cont);
  
  /**
   * Post-processes the model container.
   * 
   * @param cont	the container to post-process
   * @return		the (potentially) post-processed container
   */
  public WekaModelContainer postProcess(WekaModelContainer cont) {
    check(cont);
    return doPostProcess(cont);
  }
}
