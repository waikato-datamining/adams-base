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
 * ArrayFolds.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a subset of the array, using folds similar to cross-validation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: ArrayFolds
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.random.RandomIntegerRangeGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The random number generator to use for selecting the elements.
 * &nbsp;&nbsp;&nbsp;default: adams.data.random.JavaRandomInt
 * </pre>
 * 
 * <pre>-split-result &lt;SPLIT|INVERSE|BOTH&gt; (property: splitResult)
 * &nbsp;&nbsp;&nbsp;The type of data to return: e.g., the sample, the inverse of the sample 
 * &nbsp;&nbsp;&nbsp;or both (split and inverse).
 * &nbsp;&nbsp;&nbsp;default: SPLIT
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to generate.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7732 $
 */
public class ArrayFolds
  extends AbstractArraySplitter {

  /** for serialization. */
  private static final long serialVersionUID = 8536100625511019961L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_QUEUE = "queue";

  /** the number of folds. */
  protected int m_Folds;
  
  /** the queue of folds. */
  protected List<TIntArrayList> m_Queue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a subset of the array, using folds similar to "
	+ "cross-validation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "folds", "folds",
	    10, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Queue = new ArrayList<TIntArrayList>();
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Queue.clear();
  }
  
  /**
   * Sets the number of folds.
   *
   * @param value 	the folds
   */
  public void setFolds(int value) {
    if (value > 0) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().warning("Sample size must be >0, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return 		the folds
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
    return "The number of folds to generate.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "folds", m_Folds, "folds: ");
    result += ", " + super.getQuickInfo();
    
    return result;
  }

  /* (non-Javadoc)
   * @see adams.flow.core.AbstractActor#pruneBackup()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_QUEUE);
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

    if (m_Queue != null)
      result.put(BACKUP_QUEUE, m_Queue);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_QUEUE)) {
      m_Queue = (List<TIntArrayList>) state.get(BACKUP_QUEUE);
      state.remove(BACKUP_QUEUE);
    }

    super.restoreState(state);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		arrayOld;
    int			i;
    int			n;
    int			from;
    int			to;
    TIntArrayList	available;
    TIntArrayList	indices;
    
    result   = null;
    
    // randomize indices
    arrayOld = m_InputToken.getPayload();
    available = new TIntArrayList();
    for (i = 0; i < Array.getLength(arrayOld); i++)
      available.add(i);
    indices = new TIntArrayList();
    m_Generator.setMinValue(0);
    while (available.size() > 0) {
      if (available.size() == 1) {
	i = 0;
      }
      else {
	m_Generator.setMaxValue(available.size() - 1);
	i = m_Generator.next().intValue();
      }
      indices.add(available.get(i));
      available.removeAt(i);
    }
    
    // create folds
    m_Queue.clear();
    for (n = 0; n < m_Folds; n++) {
      from = (int) Math.round(n * ((double) indices.size() / (double) m_Folds));
      to   = (int) Math.round((n + 1) * ((double) indices.size() / (double) m_Folds));
      available = new TIntArrayList();
      for (i = from; i < to; i++)
	available.add(indices.get(i));
      available.sort();
      m_Queue.add(available);
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
    return (m_Queue.size() > 0);
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    TIntArrayList	indices;
    TIntArrayList	available;
    Object		arrayOld;
    Object		arrayNew;
    int			i;
    String		suffix;
    
    indices   = m_Queue.remove(0);
    arrayOld  = m_InputToken.getPayload();
    available = new TIntArrayList();
    for (i = 0; i < Array.getLength(arrayOld); i++) {
      if (!indices.contains(i))
	available.add(i);
    }

    suffix = " " + (m_Folds - m_Queue.size()) + "/" + m_Folds;
    switch (m_SplitResult) {
      case SPLIT:
	arrayNew = newArray(arrayOld, indices, "split" + suffix);
	m_OutputToken = new Token(arrayNew);
	break;
      case INVERSE:
	arrayNew = newArray(arrayOld, available, "inverse" + suffix);
	m_OutputToken = new Token(arrayNew);
	break;
      case BOTH:
	arrayNew = Array.newInstance(arrayOld.getClass(), 2);
	Array.set(arrayNew, 0, newArray(arrayOld, indices, "split" + suffix));
	Array.set(arrayNew, 1, newArray(arrayOld, available, "inverse" + suffix));
	m_OutputToken = new Token(arrayNew);
	break;
      default:
	throw new IllegalStateException("Unhandled split result: " + m_SplitResult);
    }
    
    updateProvenance(m_OutputToken);
    
    return m_OutputToken;
  }
}
