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
 * WekaSplitGenerator.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Stoppable;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.SplitGenerator;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaSplitGenerator
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6447945986570354931L;

  /** the split generator to use. */
  protected SplitGenerator m_Generator;

  /** the currently active generator. */
  protected transient SplitGenerator m_ActualGenerator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Splits a dataset into a training and test sets using the specified splitter.\n"
        + "The training set can be accessed in the container with '" + WekaTrainTestSetContainer.VALUE_TRAIN + "' "
        + "and the test set with '" + WekaTrainTestSetContainer.VALUE_TEST + "'.\n"
	+ "Depending on the split generator in use, more than one container may be output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new DefaultRandomSplitGenerator());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualGenerator = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start --><!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return WekaTrainTestSetContainer.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the splits are output as array rather than one-by-one.";
  }

  /**
   * Sets the scheme for generating the split.
   *
   * @param value	the generator
   */
  public void setGenerator(SplitGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the scheme for generating the split.
   *
   * @return		the generator
   */
  public SplitGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The scheme to use for generating the split.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Instances		inst;

    result = null;
    inst   = new Instances((Instances) m_InputToken.getPayload());
    m_Queue.clear();

    try {
      m_ActualGenerator = (SplitGenerator) OptionUtils.shallowCopy(m_Generator);
      m_ActualGenerator.setData(inst);
    }
    catch (Exception e) {
      m_ActualGenerator = null;
      result    = handleException("Failed to generate split!", e);
    }

    if (result == null) {
      while (m_ActualGenerator.hasNext())
        m_Queue.add(m_ActualGenerator.next());
    }

    m_ActualGenerator = null;

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ActualGenerator != null) {
      if (m_ActualGenerator instanceof Stoppable)
        ((Stoppable) m_ActualGenerator).stopExecution();
    }
    super.stopExecution();
  }
}
