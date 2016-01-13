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
 * AbstractReportDbReader.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.report.Report;
import adams.db.ReportProviderByDBID;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.transformer.report.AbstractReportPostProcessor;
import adams.flow.transformer.report.NoPostProcessing;

/**
 * Abstract ancestor for actors that load reports from the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to handle
 */
public abstract class AbstractReportDbReader<T extends Report>
  extends AbstractDbTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7352720726300796621L;

  /** the post-processor to apply to the data. */
  protected AbstractReportPostProcessor m_PostProcessor;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"post-processor", "postProcessor",
	new NoPostProcessing());
  }

  /**
   * Sets the post-processor to apply to the data.
   *
   * @param value 	the post-processor
   */
  public void setPostProcessor(AbstractReportPostProcessor value) {
    m_PostProcessor = value;
    m_PostProcessor.setOwner(this);
    reset();
  }

  /**
   * Returns the post-processor in use.
   *
   * @return 		the post-processor
   */
  public AbstractReportPostProcessor getPostProcessor() {
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
   * @return		the type of report
   */
  @Override
  public abstract Class[] generates();

  /**
   * Returns the report provider to use.
   *
   * @return		the report provider
   */
  protected abstract ReportProviderByDBID<T> getReportProvider();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    ReportProviderByDBID<T> provider;
    Report		report;
    int			id;

    result = null;

    id       = (Integer) m_InputToken.getPayload();
    provider = getReportProvider();
    report   = provider.load(id);
    if (report == null)
      result = "No report loaded for ID: " + m_InputToken;
    else
      m_OutputToken = new Token(m_PostProcessor.postProcess(report));

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
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, m_OutputToken.getPayload().getClass()));
  }
}
