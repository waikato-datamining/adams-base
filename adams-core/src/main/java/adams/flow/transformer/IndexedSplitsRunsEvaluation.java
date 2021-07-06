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
 * IndexedSplitsRunsEvaluation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.transformer.indexedsplitsrunsevaluation.NullEvaluation;

/**
 <!-- globalinfo-start -->
 * Uses the specified evaluation for evaluating the incoming data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: IndexedSplitsRunsEvaluation
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
 * <pre>-type &lt;SOURCE|STORAGE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;Determines how to obtain the indexed splits runs.
 * &nbsp;&nbsp;&nbsp;default: SOURCE
 * </pre>
 *
 * <pre>-source &lt;adams.flow.core.CallableActorReference&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The source actor to obtain the indexed splits runs from.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage &lt;adams.flow.control.StorageName&gt; (property: storage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the indexed splits runs from.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-evaluation &lt;adams.flow.transformer.indexedsplitsrunsevaluation.IndexedSplitsRunsEvaluation&gt; (property: evaluation)
 * &nbsp;&nbsp;&nbsp;The evaluation to use for generating the indexed splits.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.indexedsplitsrunsevaluation.NullEvaluation
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRunsEvaluation
  extends AbstractTransformer
  implements StorageUser {

  private static final long serialVersionUID = 7448032116260228656L;

  /** the source of the indexed splits. */
  public enum SourceType {
    SOURCE,
    STORAGE
  }

  /** the source type. */
  protected SourceType m_Type;

  /** the source actor. */
  protected CallableActorReference m_Source;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the evaluation to use. */
  protected adams.flow.transformer.indexedsplitsrunsevaluation.IndexedSplitsRunsEvaluation m_Evaluation;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified evaluation for evaluating the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      SourceType.SOURCE);

    m_OptionManager.add(
      "source", "source",
      new CallableActorReference());

    m_OptionManager.add(
      "storage", "storage",
      new StorageName());

    m_OptionManager.add(
      "evaluation", "evaluation",
      new NullEvaluation());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the type of source.
   *
   * @param value	the type
   */
  public void setType(SourceType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of source.
   *
   * @return		the type
   */
  public SourceType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "Determines how to obtain the indexed splits runs.";
  }

  /**
   * Sets the report source actor.
   *
   * @param value	the source
   */
  public void setSource(CallableActorReference value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the report source actor.
   *
   * @return		the source
   */
  public CallableActorReference getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The source actor to obtain the indexed splits runs from.";
  }

  /**
   * Sets the report storage item.
   *
   * @param value	the storage item
   */
  public void setStorage(StorageName value) {
    m_Storage = value;
    reset();
  }

  /**
   * Returns the report storage item.
   *
   * @return		the storage item
   */
  public StorageName getStorage() {
    return m_Storage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageTipText() {
    return "The storage item to obtain the indexed splits runs from.";
  }

  /**
   * Sets the evaluation to use.
   *
   * @param value	the evaluation
   */
  public void setEvaluation(adams.flow.transformer.indexedsplitsrunsevaluation.IndexedSplitsRunsEvaluation value) {
    m_Evaluation = value;
    reset();
  }

  /**
   * Returns the evaluation to use.
   *
   * @return		the evaluation
   */
  public adams.flow.transformer.indexedsplitsrunsevaluation.IndexedSplitsRunsEvaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evaluationTipText() {
    return "The evaluation to use for generating the indexed splits.";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return (m_Type == SourceType.STORAGE);
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getSource());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "evaluation", m_Evaluation, ", evaluation: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{m_Evaluation.accepts()};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_Evaluation.generates()};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MessageCollection	errors;
    IndexedSplitsRuns	runs;
    Actor		source;
    Compatibility	comp;
    Token		token;
    Storage 		storage;
    Object		eval;

    result = null;
    runs   = null;

    // get indexed splits runs
    switch (m_Type) {
      case SOURCE:
	source  = findCallableActor();
	if (source instanceof OutputProducer) {
	  comp = new Compatibility();
	  if (!comp.isCompatible(new Class[]{IndexedSplitsRun.class}, ((OutputProducer) source).generates()))
	    result = "Callable actor '" + m_Source + "' does not produce output that is compatible with '" + IndexedSplitsRun.class.getName() + "'!";
	}
	else {
	  result = "Callable actor '" + m_Source + "' does not produce any output!";
	}
	token = null;
	if (result == null) {
	  result = source.execute();
	  if (result != null) {
	    result = "Callable actor '" + m_Source + "' execution failed:\n" + result;
	  }
	  else {
	    if (((OutputProducer) source).hasPendingOutput())
	      token = ((OutputProducer) source).output();
	    else if (!m_Silent)
	      result = "Callable actor '" + m_Source + "' did not generate any output!";
	  }
	}
	if (token != null)
	  runs = (IndexedSplitsRuns) token.getPayload();
	break;

      case STORAGE:
	storage = getStorageHandler().getStorage();
	if (storage.has(m_Storage))
	  runs = (IndexedSplitsRuns) storage.get(m_Storage);
	else if (!m_Silent)
	  result = "Storage item not available: " + m_Storage;
	break;

      default:
	result = "Unhandled source type: " + m_Type;
    }

    // evaluate
    if (result == null) {
      errors = new MessageCollection();
      try {
	m_Evaluation.setFlowContext(this);
	eval = m_Evaluation.evaluate(m_InputToken.getPayload(), runs, errors);
	if (eval == null) {
	  if (errors.isEmpty())
	    result = "Failed to evaluate!";
	  else
	    result = "Failed to evaluate:\n" + errors;
	}
	else {
	  m_OutputToken = new Token(eval);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to evaluate!", e);
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    m_Evaluation.stopExecution();
  }
}
