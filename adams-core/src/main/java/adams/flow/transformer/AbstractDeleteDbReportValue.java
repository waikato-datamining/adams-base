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
 * AbstractDeleteDbReportValue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.db.ReportProviderByID;
import adams.flow.core.Unknown;

/**
 * Ancestor for transformers that delete values from a report in the database
 * whenever a token passes through.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of report
 */
public abstract class AbstractDeleteDbReportValue<T extends Report>
  extends AbstractDbTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6237324482439105653L;

  /** the sample id. */
  protected String m_SampleID;

  /** the field(s) to delete from the report. */
  protected AbstractField[] m_Fields;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sample-id", "sampleID",
      "");

    m_OptionManager.add(
      "field", "fields",
      getDefaultFields());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "sampleID", m_SampleID, "ID: ");
    result += QuickInfoHelper.toString(this, "fields", m_Fields, ", fields: ");

    return result;
  }

  /**
   * Sets the sample ID.
   *
   * @param value	the ID
   */
  public void setSampleID(String value) {
    m_SampleID = value;
    reset();
  }

  /**
   * Returns the sample ID.
   *
   * @return		the ID
   */
  public String getSampleID() {
    return m_SampleID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleIDTipText() {
    return "The sample ID of the report to remove fields from.";
  }

  /**
   * Returns the default fields for the option.
   *
   * @return		the default fields
   */
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
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the report provider to use for removing the
   *
   * @return		the provider
   */
  protected abstract ReportProviderByID<T> getReportProvider();

  /**
   * Performs the actual database query.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String			result;
    ReportProviderByID<T>	provider;
    boolean			success;

    result   = null;
    provider = getReportProvider();

    try {
      for (AbstractField field: m_Fields) {
	if (isLoggingEnabled())
	  getLogger().info("Deleting field: " + field);
	success = provider.remove(m_SampleID, field);
	if (isLoggingEnabled())
	  getLogger().info("Deleted field: " + field + "? " + success);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to delete fields: " + Utils.arrayToString(m_Fields), e);
    }

    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
