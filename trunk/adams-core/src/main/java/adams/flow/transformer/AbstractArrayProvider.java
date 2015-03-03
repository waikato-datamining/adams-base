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
 * AbstractArrayProvider.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

import adams.core.Utils;
import adams.data.report.AbstractField;
import adams.flow.core.ArrayProvider;
import adams.flow.core.Token;

/**
 * Ancestor for transformer actors that can output items one by one or as a single
 * array.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractArrayProvider
  extends AbstractTransformer
  implements ArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3535682146675647153L;

  /** the key for storing the elements in the backup. */
  public final static String BACKUP_QUEUE = "queue";

  /** the key for storing the index in the backup. */
  public final static String BACKUP_INDEX = "index";

  /** contains the items. */
  protected ArrayList m_Queue;

  /** whether to output an array instead of single items. */
  protected boolean m_OutputArray;

  /** the current index. */
  protected int m_Index;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-array", "outputArray",
	    false);
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  protected abstract Class getItemClass();

  /**
   * Sets whether to output the items as array or as single strings.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to output the items as array or as single strings.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String outputArrayTipText();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    Class[]	result;
    Object	array;

    if (m_OutputArray) {
      array  = Array.newInstance(getItemClass(), 0);
      result = new Class[]{array.getClass()};
    }
    else {
      result = new Class[]{getItemClass()};
    }

    return result;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Queue = new ArrayList();
    m_Index = 0;
  }

  /**
   * Removes entries from the backup.
   *
   * @see		#reset()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_QUEUE);
    pruneBackup(BACKUP_INDEX);
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

    result.put(BACKUP_QUEUE, m_Queue);
    result.put(BACKUP_INDEX, m_Index);

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
      m_Queue = (ArrayList<AbstractField>) state.get(BACKUP_QUEUE);
      state.remove(BACKUP_QUEUE);
    }

    if (state.containsKey(BACKUP_INDEX)) {
      m_Index = (Integer) state.get(BACKUP_INDEX);
      state.remove(BACKUP_INDEX);
    }

    super.restoreState(state);
  }

  /**
   * Resets the index.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Index
   */
  @Override
  protected String preExecute() {
    String	result;

    result  = super.preExecute();
    m_Index = 0;
    
    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    Object	array;
    int		i;

    if (m_OutputArray) {
      array = Array.newInstance(getItemClass(), m_Queue.size());
      for (i = 0; i < m_Queue.size(); i++)
	Array.set(array, i, m_Queue.get(i));
      result = new Token(array);
      m_Queue.clear();
    }
    else {
      result = new Token(m_Queue.get(m_Index));
      m_Index++;
      if (m_Index >= m_Queue.size()) {
	m_Queue.clear();
	m_Index = 0;
      }
    }
    
    if (isLoggingEnabled()) {
      if (m_OutputArray)
	getLogger().info("Array: " + Utils.arrayToString(result.getPayload()));
      else
	getLogger().info("Element: " + result.getPayload());
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
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Queue = null;
  }
}
