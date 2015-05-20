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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.RandomSplitGenerator;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Splits a dataset into a training and test set according to a specified split percentage. Randomization can be suppressed using with 'preserve order'.<br>
 * The training set can be accessed in the container with 'Train' and the test set with 'Test'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.TrainTestSetContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RandomSplit
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-preserve-order (property: preserveOrder)
 * &nbsp;&nbsp;&nbsp;If set to true, then the order is preserved by suppressing randomization.
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the randomization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-percentage &lt;double&gt; (property: percentage)
 * &nbsp;&nbsp;&nbsp;The percentage for the split (between 0 and 1).
 * &nbsp;&nbsp;&nbsp;default: 0.66
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * &nbsp;&nbsp;&nbsp;maximum: 0.9999
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaRandomSplit
  extends AbstractTransformer
  implements Randomizable, ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -6447945986570354931L;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /** the seed value. */
  protected long m_Seed;

  /** the percentage for the split (0-1). */
  protected double m_Percentage;

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
      + "with 'preserve order'.\n"
      + "The training set can be accessed in the container with '" + WekaTrainTestSetContainer.VALUE_TRAIN + "' "
      + "and the test set with '" + WekaTrainTestSetContainer.VALUE_TEST + "'.";
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
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "percentage", m_Percentage);
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "preserveOrder", m_PreserveOrder, "order preserved", ", ");

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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.TrainTestSetContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaTrainTestSetContainer.class};
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
    return "If set to true, then the order is preserved by suppressing randomization.";
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
    return "The seed value for the randomization.";
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
    return "The percentage for the split (between 0 and 1).";
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
    RandomSplitGenerator	generator;

    result = null;
    inst   = new Instances((Instances) m_InputToken.getPayload());

    try {
      if (m_PreserveOrder)
	generator = new RandomSplitGenerator(inst, m_Percentage);
      else
	generator = new RandomSplitGenerator(inst, m_Seed, m_Percentage);
    }
    catch (Exception e) {
      generator = null;
      result    = handleException("Failed to generate split!", e);
    }

    if (result == null)
      m_OutputToken = new Token(generator.next());

    updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
