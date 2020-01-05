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
 * WekaRandomSplit.java
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.Stoppable;
import adams.core.option.OptionUtils;
import adams.data.weka.InstancesViewCreator;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.classifiers.DefaultRandomSplitGenerator;
import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Splits a dataset into a training and test set according to a specified split percentage. Randomization can be suppressed using the 'preserve order' option.<br>
 * The training set can be accessed in the container with 'Train' and the test set with 'Test'.<br>
 * Depending on the split generator in use, more than one container may be output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaTrainTestSetContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaTrainTestSetContainer: Train, Test, Seed, FoldNumber, FoldCount, Train original indices, Test original indices
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaRandomSplit
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the splits are output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-preserve-order &lt;boolean&gt; (property: preserveOrder)
 * &nbsp;&nbsp;&nbsp;If set to true, then the order is preserved by suppressing randomization;
 * &nbsp;&nbsp;&nbsp; overrides the value defined by the split generator scheme.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the randomization; overrides the value defined by the
 * &nbsp;&nbsp;&nbsp;split generator scheme.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-percentage &lt;double&gt; (property: percentage)
 * &nbsp;&nbsp;&nbsp;The percentage for the split (between 0 and 1); overrides the value defined
 * &nbsp;&nbsp;&nbsp;by the split generator scheme.
 * &nbsp;&nbsp;&nbsp;default: 0.66
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * &nbsp;&nbsp;&nbsp;maximum: 0.9999
 * </pre>
 *
 * <pre>-create-view &lt;boolean&gt; (property: createView)
 * &nbsp;&nbsp;&nbsp;If enabled, views of the dataset are created instead of actual copies; overrides
 * &nbsp;&nbsp;&nbsp;the value defined by the split generator scheme.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-generator &lt;weka.classifiers.RandomSplitGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The scheme to use for generating the split; the actor options take precedence
 * &nbsp;&nbsp;&nbsp;over the scheme's ones.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.DefaultRandomSplitGenerator
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaRandomSplit
  extends AbstractArrayProvider
  implements Randomizable, InstancesViewCreator {

  /** for serialization. */
  private static final long serialVersionUID = -6447945986570354931L;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** the seed value. */
  protected long m_Seed;

  /** the percentage for the split (0-1). */
  protected double m_Percentage;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /** the split generator to use. */
  protected RandomSplitGenerator m_Generator;

  /** the currently active generator. */
  protected transient RandomSplitGenerator m_ActualGenerator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Splits a dataset into a training and test set according to a "
        + "specified split percentage. Randomization can be suppressed using "
        + "the 'preserve order' option.\n"
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
      "preserve-order", "preserveOrder",
      false);

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0001, 0.9999);

    m_OptionManager.add(
      "create-view", "createView",
      false);

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
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "percentage", m_Percentage);
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "preserveOrder", m_PreserveOrder, "order preserved", ", ");
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
    if (value != null)
      result += value;
    value  = QuickInfoHelper.toString(this, "outputArray", m_OutputArray, ", as array");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
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
   * Sets whether to preserve order and suppress randomization.
   *
   * @param value	if true then no randomization will happen
   */
  public void setPreserveOrder(boolean value) {
    m_PreserveOrder = value;
    reset();
  }

  /**
   * Returns whether to preserve order and suppress randomization.
   *
   * @return		true if to preserve order and suppress randomization
   */
  public boolean getPreserveOrder() {
    return m_PreserveOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preserveOrderTipText() {
    return "If set to true, then the order is preserved by suppressing randomization; overrides the value defined by the split generator scheme.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the randomization; overrides the value defined by the split generator scheme.";
  }

  /**
   * Sets the percentage (0-1).
   *
   * @param value	the percentage
   */
  public void setPercentage(double value) {
    if ((value > 0) && (value < 1)) {
      m_Percentage = value;
      reset();
    }
    else {
      getLogger().severe(
        "Percentage must be between 0 and 1 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the percentage (0-1).
   *
   * @return  		the percentage
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The percentage for the split (between 0 and 1); overrides the value defined by the split generator scheme.";
  }

  /**
   * Sets whether to create a view only.
   *
   * @param value	true if to create a view only
   */
  public void setCreateView(boolean value) {
    m_CreateView = value;
    reset();
  }

  /**
   * Returns whether to create only a view.
   *
   * @return		true if to create view only
   */
  public boolean getCreateView() {
    return m_CreateView;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createViewTipText() {
    return "If enabled, views of the dataset are created instead of actual copies; overrides the value defined by the split generator scheme.";
  }

  /**
   * Sets the scheme for generating the split.
   *
   * @param value	the generator
   */
  public void setGenerator(RandomSplitGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the scheme for generating the split.
   *
   * @return		the generator
   */
  public RandomSplitGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The scheme to use for generating the split; the actor options take precedence over the scheme's ones.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instances			inst;

    result = null;
    inst   = new Instances((Instances) m_InputToken.getPayload());
    m_Queue.clear();

    try {
      m_ActualGenerator = (RandomSplitGenerator) OptionUtils.shallowCopy(m_Generator);
      m_ActualGenerator.setData(inst);
      m_ActualGenerator.setSeed(m_Seed);
      m_ActualGenerator.setPercentage(m_Percentage);
      m_ActualGenerator.setPreserveOrder(m_PreserveOrder);
      m_ActualGenerator.setUseViews(m_CreateView);
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
