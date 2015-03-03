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
 * AbstractDataContainerAppend.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.container.DataContainer;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that append the incoming data container to 
 * one available from storage (or if not available, put the current one in
 * storage) and forward the combined data container.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data container to process
 */
public abstract class AbstractDataContainerAppend<T extends DataContainer>
  extends AbstractTransformer
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the name of the stored value. */
  protected StorageName m_StorageName;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName);
    
    return result;
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String storageNameTipText();

  /**
   * Returns the data container class that the transformer handles.
   * 
   * @return		the container class
   */
  protected abstract Class getDataContainerClass();
  
  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   * @see		#getDataContainerClass()
   */
  @Override
  public Class[] accepts() {
    return new Class[]{getDataContainerClass()};
  }
  
  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   * @see		#getDataContainerClass()
   */
  @Override
  public Class[] generates() {
    return new Class[]{getDataContainerClass()};
  }
  
  /**
   * Appends the current data container to the stored one.
   * 
   * @param stored	the stored data container
   * @param current	the data to add
   */
  protected abstract void append(T stored, T current);
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    T		current;
    T		stored;
    boolean	appended;

    result = null;

    synchronized(getStorageHandler().getStorage()) {
      stored = null;
      if (getStorageHandler().getStorage().has(m_StorageName))
	stored = (T) getStorageHandler().getStorage().get(m_StorageName);
      if (isLoggingEnabled())
	getLogger().info("Data container '" + m_StorageName + "' available from storage: " + (stored != null));

      current = (T) m_InputToken.getPayload();
      if (stored != null) {
	append(stored, current);
	appended = false;
      }
      else {
	stored   = current;
	appended = false;
      }
      getStorageHandler().getStorage().put(m_StorageName, stored);
      m_OutputToken = new Token(stored);
      if (isLoggingEnabled())
	getLogger().info("Data container " + (appended ? "appended" : "added") + " to storage: " + m_StorageName);
    }

    return result;
  }
}
