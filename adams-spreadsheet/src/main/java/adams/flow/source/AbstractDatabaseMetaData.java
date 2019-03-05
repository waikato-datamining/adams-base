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
 * AbstractDatabaseMetaData.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.db.MetaDataType;
import adams.db.MetaDataUtils;
import adams.flow.core.Token;

/**
 * Ancestor for sources that output the database meta-data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDatabaseMetaData
  extends AbstractSimpleDbSource {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /** the type mapper to use. */
  protected AbstractTypeMapper m_TypeMapper;

  /** the type of meta-data to return. */
  protected MetaDataType m_MetaDataType;

  /** the table to retrieve the information for. */
  protected String m_Table;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the meta-data of the current database connection.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type-mapper", "typeMapper",
      new DefaultTypeMapper());

    m_OptionManager.add(
      "meta-data-type", "metaDataType",
      MetaDataType.BASIC);

    m_OptionManager.add(
      "table", "table",
      "");
  }

  /**
   * Sets the type mapper to use.
   *
   * @param value	the mapper
   */
  public void setTypeMapper(AbstractTypeMapper value) {
    m_TypeMapper = value;
    reset();
  }

  /**
   * Returns the type mapper in use.
   *
   * @return		the mapper
   */
  public AbstractTypeMapper getTypeMapper() {
    return m_TypeMapper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeMapperTipText() {
    return "The type mapper to use for mapping spreadsheet and SQL types.";
  }

  /**
   * Sets the type of meta-data to retrieve.
   *
   * @param value	the type
   */
  public void setMetaDataType(MetaDataType value) {
    m_MetaDataType = value;
    reset();
  }

  /**
   * Returns the type of meta-data to retrieve.
   *
   * @return		the type
   */
  public MetaDataType getMetaDataType() {
    return m_MetaDataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataTypeTipText() {
    return "The type of meta-data to retrieve.";
  }

  /**
   * Sets the table to retrieve the information for.
   *
   * @param value	the table
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table to retrieve the information for.
   *
   * @return		the table
   */
  public String getTable() {
    return m_Table;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableTipText() {
    return "The table to retrieve the information for (" + Utils.flatten(MetaDataUtils.typesRequireTable(), ", ") + ").";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "metaDataType", m_MetaDataType, "type: ");
    result += QuickInfoHelper.toString(this, "table", (m_Table.isEmpty() ? "-none-" : m_Table), ", table: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Performs the actual database query.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    SpreadSheet		sheet;
    MessageCollection	errors;

    result = null;

    errors = new MessageCollection();
    sheet  = MetaDataUtils.getMetaData(m_DatabaseConnection, m_TypeMapper, m_MetaDataType, m_Table, errors);
    if (!errors.isEmpty())
      result = errors.toString();
    else if (sheet != null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
