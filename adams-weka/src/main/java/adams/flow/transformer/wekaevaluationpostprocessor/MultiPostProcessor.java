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
 * MultiPostProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.flow.container.WekaEvaluationContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies the specified post-processors sequentially to the input data and combines their output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-post-processor &lt;adams.flow.transformer.wekaevaluationpostprocessor.AbstractWekaEvaluationPostProcessor&gt; [-post-processor ...] (property: postProcessors)
 * &nbsp;&nbsp;&nbsp;The post-processors to apply to the input data.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiPostProcessor
  extends AbstractWekaEvaluationPostProcessor {

  private static final long serialVersionUID = 7981725475588023689L;

  /** the post-processors to combine. */
  protected AbstractWekaEvaluationPostProcessor[] m_PostProcessors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the specified post-processors sequentially to the input "
	+ "data and combines their output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "post-processor", "postProcessors",
      new AbstractWekaEvaluationPostProcessor[0]);
  }

  /**
   * Sets the post-processors to use.
   *
   * @param value	the post-processors
   */
  public void setPostProcessors(AbstractWekaEvaluationPostProcessor[] value) {
    m_PostProcessors = value;
    reset();
  }

  /**
   * Returns the post-processors in use.
   *
   * @return		the post-processors
   */
  public AbstractWekaEvaluationPostProcessor[] getPostProcessors() {
    return m_PostProcessors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorsTipText() {
    return "The post-processors to apply to the input data.";
  }

  /**
   * Post-processes the evaluation container.
   *
   * @param cont	the container to post-process
   * @return		the generated evaluation containers
   */
  @Override
  protected List<WekaEvaluationContainer> doPostProcess(WekaEvaluationContainer cont) {
    List<WekaEvaluationContainer>	result;
    int					i;

    result = new ArrayList<>();

    for (i = 0; i < m_PostProcessors.length; i++) {
      if (isLoggingEnabled())
	getLogger().info("Applying post-processor #" + (i+1));
      result.addAll(m_PostProcessors[i].postProcess(cont));
    }

    return result;
  }
}
