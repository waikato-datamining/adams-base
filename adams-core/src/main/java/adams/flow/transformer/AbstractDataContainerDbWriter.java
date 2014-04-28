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
 * AbstractDataContainerDbWriter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Constants;
import adams.core.QuickInfoHelper;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.db.DataProvider;
import adams.flow.core.Token;
import adams.flow.transformer.datacontainer.AbstractDataContainerPreProcessor;
import adams.flow.transformer.datacontainer.NoPreProcessing;

/**
 * Abstract ancestor for actors that import data containers into the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to write to the database
 */
public abstract class AbstractDataContainerDbWriter<T extends DataContainer & DatabaseIDHandler>
  extends AbstractDbTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7382952208593440101L;

  /** the pre-processor to apply to the data. */
  protected AbstractDataContainerPreProcessor m_PreProcessor;
  
  /** whether to replace existing containers with the new one (otherwise, nothing happens). */
  protected boolean m_OverwriteExisting;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"pre-processor", "preProcessor",
	new NoPreProcessing());
    
    m_OptionManager.add(
	"overwrite-existing", "overwriteExisting",
	false);
  }

  /**
   * Sets the pre-processor to apply to the data.
   *
   * @param value 	the pre-processor
   */
  public void setPreProcessor(AbstractDataContainerPreProcessor value) {
    m_PreProcessor = value;
    reset();
  }

  /**
   * Returns the pre-processor in use.
   *
   * @return 		the pre-processor
   */
  public AbstractDataContainerPreProcessor getPreProcessor() {
    return m_PreProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preProcessorTipText() {
    return "The pre-processor to apply to the data.";
  }

  /**
   * Sets whether to remove existing containers.
   *
   * @param value 	true if to remove existing containers
   */
  public void setOverwriteExisting(boolean value) {
    m_OverwriteExisting = value;
    reset();
  }

  /**
   * Returns whether to remove existing containers.
   *
   * @return 		true if to remove existing containers
   */
  public boolean getOverwriteExisting() {
    return m_OverwriteExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String overwriteExistingTipText();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the type of data to store
   */
  @Override
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
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
    
    result = QuickInfoHelper.toString(this, "preProcessor", m_PreProcessor);
    value  = QuickInfoHelper.toString(this, "overwriteExisting", m_OverwriteExisting, "overwrite", ", ");
    if (value != null)    
      result += value;
    
    return result;
  }

  /**
   * Returns the data provider to use for storing the container in the database.
   *
   * @param cont	the current container
   * @return		the data provider
   */
  protected abstract DataProvider<T> getDataProvider(T cont);

  /**
   * Returns whether the container already exists in the database.
   * 
   * @param provider	the provider to use for checking
   * @param cont	the container to look for
   * @return		true if already stored in database
   */
  protected boolean exists(DataProvider provider, T cont) {
    return provider.exists(cont.getID());
  }

  /**
   * Removes the container from the database.
   * 
   * @param provider	the provider to use for removing
   * @param cont	the container to remove
   * @return		true if successfully removed
   */
  protected boolean remove(DataProvider provider, T cont) {
    return provider.remove(cont.getID());
  }

  /**
   * Adds the container to the database.
   * 
   * @param provider	the provider to use
   * @param cont	the container to store
   * @return		the database ID, {@link Constants#NO_ID} if failed
   */
  protected Integer add(DataProvider provider, T cont) {
    return provider.add(cont);
  }
  
  /**
   * Stores the data container.
   *
   * @param cont	the container
   * @return		the database ID, null in case of error
   */
  protected Integer store(T cont) {
    Integer		result;
    DataProvider<T>	provider;
    boolean		exists;
    boolean		ok;

    result   = Constants.NO_ID;
    provider = getDataProvider(cont);
    exists   = exists(provider, cont);
    if (isLoggingEnabled())
      getLogger().info("Container '" + cont + "' exists in database: " + exists);
    if (exists) {
      if (m_OverwriteExisting) {
	ok = remove(provider, cont);
	if (isLoggingEnabled())
	  getLogger().info("Existing container '" + cont + "' removed from database: " + ok);
	if (ok)
	  result = add(provider, cont);
	else
	  getLogger().severe("Failed to remove container from database: " + cont);
      }
      else {
	getLogger().severe("Container '" + cont + "' already exists in database, but no overwrite allowed, skipping!");
      }
    }
    else {
      result = add(provider, cont);
    }

    return result;
  }

  /**
   * Performs preprocessing on the container.
   * 
   * @param cont	the container to preprocess
   * @return		the processed container
   */
  protected T preProcess(T cont) {
    return (T) m_PreProcessor.preProcess(cont);
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String	result;
    T		cont;
    Integer	id;

    result = null;

    cont = (T) m_InputToken.getPayload();
    id   = store(preProcess(cont));
    if (id == null)
      result = "Error saving container: " + m_InputToken;
    else
      m_OutputToken = new Token(id);

    return result;
  }
}
