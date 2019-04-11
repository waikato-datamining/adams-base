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
 * AbstractReportDbWriter.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.db.ReportProviderByID;
import adams.flow.core.Token;

/**
 * Abstract ancestor for actors that write report values to the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of report to handle
 */
public abstract class AbstractReportValueDbWriter<T extends Report>
  extends AbstractDbTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5253006932367969870L;

  /** the fields to write to DB. */
  protected Field[] m_Fields;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "fields",
      getDefaultFields());
  }

  protected abstract AbstractField[] getDefaultFields();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String fieldsTipText();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the report class
   */
  @Override
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the report class
   */
  @Override
  public abstract Class[] generates();

  /**
   * Returns the report provider to use for writing the report values to the database.
   *
   * @return		the provider to use
   */
  protected abstract ReportProviderByID<T> getReportProvider();

  /**
   * Extracts the ID from the report.
   *
   * @param report	the report to extract the ID from
   * @return		the ID
   */
  protected abstract String extractID(T report);

  /**
   * Generates a subset of the report, which only contains the specified fields.
   *
   * @param report	the report to process
   * @return		the subset report
   */
  protected abstract T extractSubset(T report);

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String			result;
    ReportProviderByID<T> 	provider;
    boolean			stored;
    T 				report;
    T				subset;
    String			id;

    result = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = (T) ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (T) m_InputToken.getPayload();

    if (report == null) {
      result = "No report attached: " + m_InputToken.getPayload();
    }
    else {
      id       = extractID(report);
      subset   = extractSubset(report);
      provider = getReportProvider();
      stored   = provider.store(id, subset, false, true, m_Fields);

      if (stored)
	m_OutputToken = new Token(id);
    }

    return result;
  }
}
