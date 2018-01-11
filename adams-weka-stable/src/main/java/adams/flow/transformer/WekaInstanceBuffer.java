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
 * WekaInstanceBuffer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import weka.core.BinarySparseInstance;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Can act in two different ways:<br>
 * 1. Instance -&gt; Instances (row -&gt; dataset)<br>
 * Buffers weka.core.Instance objects and outputs a weka.core.Instances object, whenever the interval condition has been met.<br>
 * 2. Instances -&gt; Instance (dataset -&gt; row)<br>
 * Outputs all the weka.core.Instance objects that the incoming weka.core.Instances object contains.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaInstanceBuffer
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-operation &lt;INSTANCES_TO_INSTANCE|INSTANCE_TO_INSTANCES&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The way the buffer operates, 'dataset -&gt; row' or 'row -&gt; dataset'.
 * &nbsp;&nbsp;&nbsp;default: INSTANCE_TO_INSTANCES
 * </pre>
 * 
 * <pre>-check (property: checkHeader)
 * &nbsp;&nbsp;&nbsp;Whether to check the headers - if the headers change, the Instance object 
 * &nbsp;&nbsp;&nbsp;gets dumped into a new file (in case of INSTANCE_TO_INSTANCES).
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval at which to output the Instances object (in case of INSTANCE_TO_INSTANCES
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-clear-buffer (property: clearBuffer)
 * &nbsp;&nbsp;&nbsp;Whether to clear the buffer once the dataset has been forwarded (in case 
 * &nbsp;&nbsp;&nbsp;of INSTANCE_TO_INSTANCES).
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceBuffer
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6774529845778672623L;

  /** the key for storing the current buffer in the backup. */
  public final static String BACKUP_BUFFER = "buffer";

  /** the key for storing the current iterator in the backup. */
  public final static String BACKUP_ITERATOR = "iterator";

  /**
   * Defines how the buffer actor operates.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Operation {
    /** Instances -&gt; Instance. */
    INSTANCES_TO_INSTANCE,
    /** Instance -&gt; Instances. */
    INSTANCE_TO_INSTANCES,
  }

  /** the currently buffered data. */
  protected Instances m_Buffer;

  /** the iterator for broadcasting Instance objects. */
  protected Iterator<Instance> m_Iterator;

  /** the way the buffer operates. */
  protected Operation m_Operation;

  /** whether to check the header. */
  protected boolean m_CheckHeader;

  /** the interval of when to output the Instances object. */
  protected int m_Interval;
  
  /** whether to clear the buffer once it has been forwarded. */
  protected boolean m_ClearBuffer;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Can act in two different ways:\n"
      + "1. Instance -> Instances (row -> dataset)\n"
      + "Buffers weka.core.Instance objects and outputs a weka.core.Instances "
      + "object, whenever the interval condition has been met.\n"
      + "2. Instances -> Instance (dataset -> row)\n"
      + "Outputs all the weka.core.Instance objects that the incoming "
      + "weka.core.Instances object contains.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "operation", "operation",
	    Operation.INSTANCE_TO_INSTANCES);

    m_OptionManager.add(
	    "check", "checkHeader",
	    false);

    m_OptionManager.add(
	    "interval", "interval",
	    1, 1, null);

    m_OptionManager.add(
	    "clear-buffer", "clearBuffer",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "operation", m_Operation);
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");
    
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "checkHeader", m_CheckHeader, "check header"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "clearBuffer", m_ClearBuffer, "clear"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the way the buffer operates.
   *
   * @param value	the operation
   */
  public void setOperation(Operation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the way the buffer operates.
   *
   * @return 		the operation
   */
  public Operation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The way the buffer operates, 'dataset -> row' or 'row -> dataset'.";
  }

  /**
   * Sets whether to check the header or not.
   *
   * @param value	if true then the headers get checked
   */
  public void setCheckHeader(boolean value) {
    m_CheckHeader = value;
    reset();
  }

  /**
   * Returns whether the header gets checked or not.
   *
   * @return		true if the header gets checked
   */
  public boolean getCheckHeader() {
    return m_CheckHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkHeaderTipText() {
    return
        "Whether to check the headers - if the headers change, the Instance "
      + "object gets dumped into a new file (in case of " + Operation.INSTANCE_TO_INSTANCES + ").";
  }

  /**
   * Sets the interval for outputting the Instances objects.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval for outputting the Instances objects.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return
        "The interval at which to output the Instances object (in case of " 
	+ Operation.INSTANCE_TO_INSTANCES + ").";
  }

  /**
   * Sets whether to clear the buffer once the dataset has been forwarded.
   *
   * @param value	true if to clear buffer
   */
  public void setClearBuffer(boolean value) {
    m_ClearBuffer = value;
    reset();
  }

  /**
   * Returns whether to clear the buffer once the dataset has been forwarded.
   *
   * @return		true if to clear buffer
   */
  public boolean getClearBuffer() {
    return m_ClearBuffer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clearBufferTipText() {
    return
        "Whether to clear the buffer once the dataset has been forwarded "
	+ "(in case of " + Operation.INSTANCE_TO_INSTANCES + ").";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, weka.core.Instance[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if (m_Operation == Operation.INSTANCE_TO_INSTANCES)
      return new Class[]{Instance.class, Instance[].class};
    else if (m_Operation == Operation.INSTANCES_TO_INSTANCE)
      return new Class[]{Instances.class};
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_Operation == Operation.INSTANCE_TO_INSTANCES)
      return new Class[]{Instances.class};
    else if (m_Operation == Operation.INSTANCES_TO_INSTANCE)
      return new Class[]{Instance.class};
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_BUFFER);
    pruneBackup(BACKUP_ITERATOR);
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

    if (m_Buffer != null)
      result.put(BACKUP_BUFFER, m_Buffer);
    if (m_Iterator != null)
      result.put(BACKUP_ITERATOR, m_Iterator);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_BUFFER)) {
      m_Buffer = (Instances) state.get(BACKUP_BUFFER);
      state.remove(BACKUP_BUFFER);
    }
    if (state.containsKey(BACKUP_ITERATOR)) {
      m_Iterator = (Iterator<Instance>) state.get(BACKUP_ITERATOR);
      state.remove(BACKUP_ITERATOR);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Buffer   = null;
    m_Iterator = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instance[]	insts;
    Instance	inst;
    double[]	values;
    int		i;
    int		n;
    boolean	updated;

    result = null;

    if (m_Operation == Operation.INSTANCE_TO_INSTANCES) {
      if (m_InputToken.getPayload() instanceof Instance) {
	insts = new Instance[]{(Instance) m_InputToken.getPayload()};
      }
      else {
	insts = (Instance[]) m_InputToken.getPayload();
      }

      for (n = 0; n < insts.length; n++) {
	inst = insts[n];

	if ((m_Buffer != null) && m_CheckHeader) {
	  if (!m_Buffer.equalHeaders(inst.dataset())) {
	    getLogger().info("Header changed, resetting buffer");
	    m_Buffer = null;
	  }
	}

	// buffer instance
	if (m_Buffer == null)
	  m_Buffer = new Instances(inst.dataset(), 0);

	// we need to make sure that string and relational values are in our
	// buffer header and update the current Instance accordingly before
	// buffering it
	values  = inst.toDoubleArray();
	updated = false;
	for (i = 0; i < values.length; i++) {
	  if (inst.isMissing(i))
	    continue;
	  if (inst.attribute(i).isString()) {
	    values[i] = m_Buffer.attribute(i).addStringValue(inst.stringValue(i));
	    updated   = true;
	  }
	  else if (inst.attribute(i).isRelationValued()) {
	    values[i] = m_Buffer.attribute(i).addRelation(inst.relationalValue(i));
	    updated   = true;
	  }
	}

	if (updated) {
	  if (inst instanceof SparseInstance) {
	    inst = new SparseInstance(inst.weight(), values);
	  }
	  else if (inst instanceof BinarySparseInstance) {
	    inst = new BinarySparseInstance(inst.weight(), values);
	  }
	  else {
	    if (!(inst instanceof DenseInstance)) {
	      getLogger().severe(
		  "Unhandled instance class (" + inst.getClass().getName() + "), "
		  + "defaulting to " + DenseInstance.class.getName());
	    }
	    inst = new DenseInstance(inst.weight(), values);
	  }
	}
	else {
	  inst = (Instance) inst.copy();
	}

	m_Buffer.add(inst);
      }

      if (m_Buffer.numInstances() % m_Interval == 0) {
	m_OutputToken = new Token(m_Buffer);
	if (m_ClearBuffer)
	  m_Buffer = null;
      }
    }
    else if (m_Operation == Operation.INSTANCES_TO_INSTANCE) {
      m_Buffer   = (Instances) m_InputToken.getPayload();
      m_Iterator = m_Buffer.iterator();
    }
    else {
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
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
    if (m_Operation == Operation.INSTANCE_TO_INSTANCES)
      return super.hasPendingOutput();
    else if (m_Operation == Operation.INSTANCES_TO_INSTANCE)
      return ((m_Iterator != null) && m_Iterator.hasNext());
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_Operation == Operation.INSTANCE_TO_INSTANCES) {
      result        = m_OutputToken;
      m_OutputToken = null;
    }
    else if (m_Operation == Operation.INSTANCES_TO_INSTANCE) {
      result = new Token(m_Iterator.next());
    }
    else {
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
    }

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
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Iterator = null;
    m_Buffer   = null;

    super.wrapUp();
  }
}
