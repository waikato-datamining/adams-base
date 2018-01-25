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
 * AbstractDeleteDataContainer.java
 * Copyright (C) 2012-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.container.DataContainer;
import adams.db.DataProvider;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that delete containers from the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to remove from the database
 */
public abstract class AbstractDeleteDataContainer<T extends DataContainer>
  extends AbstractDbTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -4736058667429890220L;

  /** whether to keep any existing report. */
  protected boolean m_KeepReport;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "keep-report", "keepReport",
      false);
  }

  /**
   * Sets whether to keep an existing report in the database.
   *
   * @param value	true if to keep
   */
  public void setKeepReport(boolean value) {
    m_KeepReport = value;
    reset();
  }

  /**
   * Returns whether to keep an existing report in the database.
   *
   * @return		true if keep
   */
  public boolean getKeepReport() {
    return m_KeepReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepReportTipText() {
    return "If enabled, any existing report is kept in the database.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		java.lang.Integer.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Integer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		java.lang.Integer.class
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
  }

  /**
   * Returns the data provider to use for storing the container in the database.
   *
   * @return		the data provider
   */
  protected abstract DataProvider<T> getDataProvider();

  /**
   * Removes the container from the database.
   *
   * @param input	the ID of the container
   * @return		true if successfully removed
   */
  protected boolean remove(Token input) {
    Integer		id;
    DataProvider<T>	provider;

    provider = getDataProvider();
    id       = (Integer) m_InputToken.getPayload();

    return provider.remove(id, m_KeepReport);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String	result;
    boolean	success;

    result = null;

    success = remove(m_InputToken);
    if (!success)
      result = "Failed to delete container with ID: " + m_InputToken;
    else
      m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}
