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
 * MultiClustererPostProcessor.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaclusterer;

import adams.flow.container.WekaModelContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Applies the specified post-processors sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiClustererPostProcessor
  extends AbstractClustererPostProcessor {

  private static final long serialVersionUID = 3138007350708781436L;

  /** the post-processors to apply. */
  protected AbstractClustererPostProcessor[] m_PostProcessors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified post-processors sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "post-processor", "postProcessors",
      new AbstractClustererPostProcessor[0]);
  }

  /**
   * Sets the distance function to use.
   *
   * @param value	the function
   */
  public void setPostProcessors(AbstractClustererPostProcessor[] value){
    m_PostProcessors = value;
    reset();
  }

  /**
   * Returns the distance function to use.
   *
   * @return		the function
   */
  public AbstractClustererPostProcessor[] getPostProcessors(){
    return m_PostProcessors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorsTipText() {
    return "The post-processors to apply sequentially.";
  }

  /**
   * Returns the keys that the processor adds/modifies.
   *
   * @return the keys, null of zero-length array for none
   */
  @Override
  protected String[] getContainerKeys() {
    List<String> 	result;

    result = new ArrayList<>();
    for (AbstractClustererPostProcessor p: m_PostProcessors)
      result.addAll(Arrays.asList(p.getContainerKeys()));

    return result.toArray(new String[0]);
  }

  /**
   * Performs the actual post-processing.
   *
   * @param cont the container to post-process
   * @return the post-processed container
   */
  @Override
  protected WekaModelContainer doPostProcess(WekaModelContainer cont) {
    for (AbstractClustererPostProcessor p: m_PostProcessors)
      cont = p.postProcess(cont);

    return cont;
  }
}
