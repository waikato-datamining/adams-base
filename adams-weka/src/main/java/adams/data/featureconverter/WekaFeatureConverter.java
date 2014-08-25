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
 * WekaFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Generates features in spreadsheet format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaFeatureConverter
  extends AbstractFeatureConverter<Instances,Instance> {

  /** for serialization. */
  private static final long serialVersionUID = 2019318091828718405L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the features into Weka format.";
  }
  
  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return Instances.class;
  }
  
  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return Instance.class;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected Instances doGenerateHeader(HeaderDefinition header) {
    Instances			result;
    ArrayList<Attribute>	atts;
    ArrayList<String>		values;
    int				i;
    
    atts = new ArrayList<Attribute>();
    for (i = 0; i < header.size(); i++) {
      switch (header.getType(i)) {
	case BOOLEAN:
	  values = new ArrayList<String>();
	  values.add("yes");
	  values.add("no");
	  atts.add(new Attribute(header.getName(i), values));
	  break;
	case NUMERIC:
	  atts.add(new Attribute(header.getName(i)));
	  break;
	case STRING:
	case UNKNOWN:
	  atts.add(new Attribute(header.getName(i), (List<String>) null));
	  break;
      }
    }
    
    result = new Instances(getClass().getName(), atts, 0);
    
    return result;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected Instance doGenerateRow(List<Object> data) {
    Instance	result;
    int		i;
    Object	obj;
    double[]	values;
    
    values = new double[m_Header.numAttributes()];
    
    for (i = 0; i < data.size(); i++) {
      obj = data.get(i);
      if (obj == null) {
	values[i] = Utils.missingValue();
	continue;
      }
      switch (m_HeaderDefinition.getType(i)) {
	case BOOLEAN:
	  values[i] = ((Boolean) obj) ? 0.0 : 1.0;
	  break;
	case NUMERIC:
	  values[i] = ((Number) obj).doubleValue();
	  break;
	case STRING:
	case UNKNOWN:
	  values[i] = m_Header.attribute(i).addStringValue(obj.toString());
	  break;
      }
    }
    
    result = new DenseInstance(1.0, values);
    result.setDataset(m_Header);
    
    return result;
  }
}
