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
 * ArrayToChunks.java
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.classmanager.ClassManager;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;
import java.util.Hashtable;

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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayToChunks
  extends AbstractTransformer {

  private static final long serialVersionUID = 1548815476164777688L;

  /** the key for storing the current array in the backup. */
  public final static String BACKUP_ARRAY = "array";

  /** the key for storing the current index in the backup. */
  public final static String BACKUP_INDEX = "index";

  /** the chunk size to split the array into. */
  protected int m_ChunkSize;

  /** the current array to work on. */
  protected Object m_CurrentArray;

  /** the current index in the array. */
  protected int m_CurrentIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits an array into chunks of a specified size. The last chunk will contain the remainder of the array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      1, 1, null);
  }

  /**
   * Sets the size of the chunks to generate.
   *
   * @param value	the size
   */
  public void setChunkSize(int value) {
    if (getOptionManager().isValid("chunkSize", value)) {
      m_ChunkSize = value;
      reset();
    }
  }

  /**
   * Returns the size of the chunks to generate.
   *
   * @return		the size
   */
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chunkSizeTipText() {
    return "The size of the chunks to generate.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_ARRAY);
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

    if (m_InputToken != null)
      result.put(BACKUP_ARRAY, m_CurrentArray);
    if (m_OutputToken != null)
      result.put(BACKUP_INDEX, m_CurrentIndex);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_ARRAY)) {
      m_CurrentArray = state.get(BACKUP_ARRAY);
      state.remove(BACKUP_ARRAY);
    }

    if (state.containsKey(BACKUP_INDEX)) {
      m_CurrentIndex = (Integer) state.get(BACKUP_INDEX);
      state.remove(BACKUP_INDEX);
    }

    super.restoreState(state);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "chunkSize", m_ChunkSize, "size: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result         = null;
    m_CurrentIndex = 0;
    m_CurrentArray = null;

    if (m_InputToken.getPayload().getClass().isArray())
      m_CurrentArray = m_InputToken.getPayload();
    else
      result = "Input is not an array!";

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
    return (m_CurrentArray != null) && (m_CurrentIndex < Array.getLength(m_CurrentArray) - 1);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    int		size;
    Object	chunk;
    int		i;

    if (m_CurrentArray == null)
      return null;

    size  = Math.min(m_ChunkSize, (Array.getLength(m_CurrentArray) - m_CurrentIndex));
    chunk = Array.newInstance(m_CurrentArray.getClass().getComponentType(), size);
    for (i = 0; i < size; i++)
      Array.set(chunk, i, ClassManager.getSingleton().deepCopy(Array.get(m_CurrentArray, m_CurrentIndex + i)));
    m_CurrentIndex += size;
    result = new Token(chunk);

    return result;
  }
}
