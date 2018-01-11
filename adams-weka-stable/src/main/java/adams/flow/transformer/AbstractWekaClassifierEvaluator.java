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
 * AbstractWekaClassifierEvaluator.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.option.OptionUtils;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.Null;
import adams.flow.container.WekaEvaluationContainer;

/**
 * Ancestor for transformers that evaluate classifiers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWekaClassifierEvaluator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7740799988980266316L;

  /** for generating predictions output. */
  protected AbstractOutput m_Output;

  /** the buffer for the predictions. */
  protected StringBuffer m_OutputBuffer;

  /** whether to always use a container. */
  protected boolean m_AlwaysUseContainer;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new Null());

    m_OptionManager.add(
	    "always-use-container", "alwaysUseContainer",
	    false);
  }

  /**
   * Sets the prediction output generator to use.
   *
   * @param value	the output generator
   */
  public void setOutput(AbstractOutput value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the prediction output generator in use.
   *
   * @return		the output generator
   */
  public AbstractOutput getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return
        "The class for generating prediction output; if 'Null' is used, then "
      + "an Evaluation object is forwarded instead of a String.";
  }

  /**
   * Sets whether to always use an evaluation container as output.
   *
   * @param value	true if to always use container
   */
  public void setAlwaysUseContainer(boolean value) {
    m_AlwaysUseContainer = value;
    reset();
  }

  /**
   * Returns whether to always use an evaluation container as output.
   *
   * @return		true if to always use container
   */
  public boolean getAlwaysUseContainer() {
    return m_AlwaysUseContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alwaysUseContainerTipText() {
    return
        "If enabled, always outputs an evaluation container.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  public Class[] generates() {
    if ((m_Output == null) || (m_Output instanceof Null) || m_AlwaysUseContainer)
      return new Class[]{WekaEvaluationContainer.class};
    else
      return new Class[]{String.class};
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    initOutputBuffer();
  }

  /**
   * Initializes the output buffer.
   */
  protected void initOutputBuffer() {
    m_OutputBuffer = new StringBuffer();
    if (m_Output != null) {
      try {
        m_Output = (AbstractOutput) OptionUtils.forAnyCommandLine(
          AbstractOutput.class, OptionUtils.getCommandLine(m_Output));
        m_Output.setBuffer(m_OutputBuffer);
      }
      catch (Exception e) {
        throw new IllegalStateException("Failed to create copy of output!", e);
      }
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Output.setBuffer(null);
    m_OutputBuffer = null;

    super.wrapUp();
  }
}
