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
 * WekaCrossValidationClustererEvaluator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Random;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.flow.container.WekaClusterEvaluationContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Cross-validates a clusterer on an incoming dataset. The clusterer setup being used in the evaluation is a callable 'Clusterer' actor.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaClusterEvaluationContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaClusterEvaluationContainer: Evaluation, Model, Log-likelohood
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaCrossValidationClustererEvaluator
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-clusterer &lt;adams.flow.core.CallableActorReference&gt; (property: clusterer)
 * &nbsp;&nbsp;&nbsp;The callable clusterer actor to cross-validate on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaClustererSetup
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the cross-validation (used for randomization).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; use -1 for leave-one-out 
 * &nbsp;&nbsp;&nbsp;cross-validation (LOOCV).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCrossValidationClustererEvaluator
  extends AbstractGlobalWekaClustererEvaluator
  implements Randomizable, ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the number of folds. */
  protected int m_Folds;

  /** the seed value. */
  protected long m_Seed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cross-validates a clusterer on an incoming dataset. The clusterer "
      + "setup being used in the evaluation is a callable 'Clusterer' actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "seed", "seed",
	    1L);

    m_OptionManager.add(
	    "folds", "folds",
	    10, -1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", folds: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String clustererTipText() {
    return "The callable clusterer actor to cross-validate on the input data.";
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds, -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value == -1) || (value >= 2)) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of folds must be >=2 or -1 for LOOCV, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in the cross-validation; use -1 for leave-one-out cross-validation (LOOCV).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the cross-validation (used for randomization).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaClusterEvaluationContainer.class};
  }

  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Instances				data;
    weka.clusterers.Clusterer		cls;
    int					folds;
    MakeDensityBasedClusterer		make;
    double				log;

    result = null;

    try {
      // evaluate classifier
      cls = getClustererInstance();
      if (cls == null)
	throw new IllegalStateException("Clusterer '" + getClusterer() + "' not found!");

      data = (Instances) m_InputToken.getPayload();
      folds = m_Folds;
      if (folds == -1)
	folds = data.numInstances();
      if (!(cls instanceof DensityBasedClusterer)) {
	make = new MakeDensityBasedClusterer();
	make.setClusterer(cls);
	cls = make;
      }
      log = ClusterEvaluation.crossValidateModel((DensityBasedClusterer) cls, data, folds, new Random(m_Seed));
      m_OutputToken = new Token(new WekaClusterEvaluationContainer(log));
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to cross-validate clusterer: ", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
