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
 * AbstractDataContainerDbReader.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.container.DataContainer;
import adams.db.DataProvider;
import adams.db.FilteredDataProvider;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.transformer.datacontainer.AbstractDataContainerPostProcessor;
import adams.flow.transformer.datacontainer.NoPostProcessing;

/**
 * Ancestor for transformers that read containers from the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to load from the database
 */
public abstract class AbstractDataContainerDbReader<T extends DataContainer>
  extends AbstractDbTransformer
  implements ProvenanceSupporter, DataContainerDbReader<T> {

  /** for serialization. */
  private static final long serialVersionUID = -4736058667429890220L;

  /** whether to return the raw data or not. */
  protected boolean m_Raw;

  /** the post-processor to apply to the data. */
  protected AbstractDataContainerPostProcessor m_PostProcessor;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"raw", "raw",
	false);
    
    m_OptionManager.add(
	"post-processor", "postProcessor",
	new NoPostProcessing());
  }

  /**
   * Sets whether to return the raw data or not (only FilteredDataProviders).
   *
   * @param value 	true if transformation is to be skipped
   * @see		FilteredDataProvider
   */
  public void setRaw(boolean value) {
    m_Raw = value;
    reset();
  }

  /**
   * Returns whether to return the raw data or not (only FilteredDataProviders).
   *
   * @return 		true if transformation is skipped
   * @see		FilteredDataProvider
   */
  public boolean getRaw() {
    return m_Raw;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rawTipText() {
    return
        "If set to true, then the raw data is returned instead of being "
      + "filtered through the global data container filter.";
  }

  /**
   * Sets the post-processor to apply to the data.
   *
   * @param value 	the post-processor
   */
  public void setPostProcessor(AbstractDataContainerPostProcessor value) {
    m_PostProcessor = value;
    m_PostProcessor.setOwner(this);
    reset();
  }

  /**
   * Returns the post-processor in use.
   *
   * @return 		the post-processor
   */
  public AbstractDataContainerPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to apply to the data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Integer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Integer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data to retrieve from the database
   */
  @Override
  public abstract Class[] generates();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    T			cont;
    Integer		id;
    DataProvider<T>	provider;

    result = null;

    provider = getDataProvider();
    id       = (Integer) m_InputToken.getPayload();
    if (m_Raw)
      cont = ((FilteredDataProvider<T>) provider).loadRaw(id);
    else
      cont = provider.load(id);
    if (cont == null)
      result = "No container loaded for ID: " + m_InputToken;
    else
      m_OutputToken = new Token(m_PostProcessor.postProcess(cont));

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Returns the data provider to use for storing the container in the database.
   *
   * @return		the data provider
   */
  public abstract DataProvider<T> getDataProvider();

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, m_OutputToken.getPayload().getClass()));
  }
}
