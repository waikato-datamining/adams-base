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

/**
 * AbstractFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.ArrayList;
import java.util.List;

import adams.core.option.AbstractOptionHandler;
import adams.data.report.DataType;

/**
 * Ancestor for generic feature converter schemes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of dataset that is generated
 * @param <R> the type of row that is generated
 */
public abstract class AbstractFeatureConverter<D,R>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4745159188031576718L;

  /** the header. */
  protected D m_Header;

  /** the data types. */
  protected List<DataType> m_Types;
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Header = null;
    m_Types  = null;
  }
  
  /**
   * Performs the actual generation of the header data structure using the 
   * names and data types.
   * 
   * @param names	the attribute names
   * @param types	the attribute types
   * @return		the dataset structure
   */
  protected abstract D doGenerateHeader(List<String> names, List<DataType> types);
  
  /**
   * Generates the header data structure using the names and data types.
   * 
   * @param names	the attribute names
   * @param types	the attribute types
   * @return		the dataset structure
   */
  public D generateHeader(List<String> names, List<DataType> types) {
    if (names.size() != types.size())
      throw new IllegalArgumentException("Number of names and types vary: " + names.size() + " != " + types.size());
    
    m_Header = doGenerateHeader(names, types);
    m_Types  = new ArrayList<DataType>(types);
    
    return m_Header;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  protected abstract R doGenerateRow(List<Object> data);

  /**
   * Generates a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   * @see		#generateHeader(List, List)
   */
  public R generateRow(List<Object> data) {
    if (m_Header == null)
      throw new IllegalStateException("No header available! generatedHeader called?");
    
    return doGenerateRow(data);
  }
}
