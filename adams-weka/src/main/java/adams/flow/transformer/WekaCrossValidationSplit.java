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
 * WekaCrossValidationSplit.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.data.weka.InstancesViewCreator;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.core.Instances;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Generates train&#47;test pairs like during a cross-validation run. It is possible to generate pairs for leave-one-out cross-validation (LOOCV) as well.<br>
 * It is essential that a class attribute is set. In case of a nominal class attribute, the data gets stratified automatically.<br>
 * Each of the pairs gets forwarded as a container. The training set can be accessed in the container with 'Train' and the test set with 'Test'.
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
 * &nbsp;&nbsp;&nbsp;default: WekaCrossValidationSplit
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
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the randomization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The folds to use; using '-1' will generate folds for leave-one-out cross-validation 
 * &nbsp;&nbsp;&nbsp;(LOOCV).
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 * 
 * <pre>-relation &lt;java.lang.String&gt; (property: relationName)
 * &nbsp;&nbsp;&nbsp;The placeholders for creating the relation name: &#64; = original relation name,
 * &nbsp;&nbsp;&nbsp; $T = type (train&#47;test), $N = current fold number.
 * &nbsp;&nbsp;&nbsp;default: &#64;
 * </pre>
 * 
 * <pre>-create-view &lt;boolean&gt; (property: createView)
 * &nbsp;&nbsp;&nbsp;If enabled, views of the dataset are created instead of actual copies.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCrossValidationSplit
  extends AbstractTransformer
  implements Randomizable, ProvenanceSupporter, InstancesViewCreator {

  /** for serialization. */
  private static final long serialVersionUID = 4026105903223741240L;

  /** the key for storing the current fold in the backup. */
  public final static String BACKUP_GENERATOR = "generator";

  /** the seed value. */
  protected long m_Seed;

  /** the number of folds to generate. */
  protected int m_Folds;

  /** the format of the relation names of the generated datasets. */
  protected String m_RelationName;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /** the fold generator. */
  protected CrossValidationFoldGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates train/test pairs like during a cross-validation run. It is "
	+ "possible to generate pairs for leave-one-out cross-validation (LOOCV) "
	+ "as well.\n"
	+ "It is essential that a class attribute is set. In case of a nominal "
	+ "class attribute, the data gets stratified automatically.\n"
	+ "Each of the pairs gets forwarded as a container. The training set can "
	+ "be accessed in the container with '" + WekaTrainTestSetContainer.VALUE_TRAIN + "' "
	+ "and the test set with '" + WekaTrainTestSetContainer.VALUE_TEST + "'.";
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
      10);

    m_OptionManager.add(
      "relation", "relationName",
      "@");

    m_OptionManager.add(
      "create-view", "createView",
      false);
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

    result  = QuickInfoHelper.toString(this, "folds", m_Folds, "folds: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    result += QuickInfoHelper.toString(this, "relationName", m_RelationName, ", relation: ");
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaTrainTestSetContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaTrainTestSetContainer.class};
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
   * Sets the number of folds to use.
   *
   * @param value	the folds, use -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value >= 2) || (value == -1)) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	"Folds must be >=2 or -1 for LOOCV (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of folds to generate.
   *
   * @return  		the folds, 1 in case of LOOCV
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
    return "The folds to use; using '-1' will generate folds for leave-one-out cross-validation (LOOCV).";
  }

  /**
   * Sets the pattern to use for renaming the relation.
   *
   * @param value	the pattern
   */
  public void setRelationName(String value) {
    m_RelationName = value;
  }

  /**
   * Returns the pattern used for renaming the relation.
   *
   * @return  		the pattern
   */
  public String getRelationName() {
    return m_RelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String relationNameTipText() {
    return "The placeholders for creating the relation name: @ = original relation name, $T = type (train/test), $N = current fold number.";
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
    return "If enabled, views of the dataset are created instead of actual copies.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_GENERATOR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Generator != null)
      result.put(BACKUP_GENERATOR, m_Generator);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_GENERATOR)) {
      m_Generator = (CrossValidationFoldGenerator) state.get(BACKUP_GENERATOR);
      state.remove(BACKUP_GENERATOR);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Generator = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;
    try {
      m_Generator = new CrossValidationFoldGenerator((Instances) m_InputToken.getPayload(), m_Folds, m_Seed, true, true, m_RelationName);
      m_Generator.setUseViews(m_CreateView);
    }
    catch (Exception e) {
      result = handleException("Failed to initalize fold generator!", e);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Generator != null) && m_Generator.hasNext();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Generator.next());

    updateProvenance(result);

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
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Generator = null;

    super.wrapUp();
  }
}
