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
 * AbstractMatchWekaInstances.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 * Ancestor for classes that match Instance objects against Instances headers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMatchWekaInstanceAgainstHeader
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -7728745365733721265L;

  /** the header to match against. */
  protected Instances m_Dataset;

  /**
   * Resets the converter.
   */
  protected void reset() {
    super.reset();

    m_Dataset = null;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return Instance.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  public Class generates() {
    return Instance.class;
  }

  /**
   * Acquires the dataset header.
   *
   * @return		the dataset header to match against
   */
  protected abstract Instances getDatasetHeader();

  /**
   * Checks the instance against the header, whether they are compatible.
   *
   * @param input	the input instance
   * @return		null if compatible, otherwise error message
   */
  protected String isCompatible(Instance input) {
    String	result;
    int		i;
    int		typeInput;
    int		typeHeader;

    result = null;
    if (input.numAttributes() != m_Dataset.numAttributes())
      result = "Number of attributes differ";

    if (result == null) {
      for (i = 0; i < m_Dataset.numAttributes(); i++) {
	typeInput  = input.attribute(i).type();
	typeHeader = m_Dataset.attribute(i).type();
	if (typeInput == typeHeader)
	  continue;
	if ((typeInput == Attribute.NOMINAL) && (typeHeader == Attribute.STRING))
	  continue;
	if ((typeInput == Attribute.STRING) && (typeHeader == Attribute.NOMINAL))
	  continue;
	result = "Attribute types at #" + (i+1) + "  are not ";
	break;
      }
    }

    return result;
  }

  /**
   * Matches the input instance against the header.
   *
   * @param input	the Instance to align to the header
   * @return		the aligned Instance
   */
  protected Instance match(Instance input) {
    Instance	result;
    double[]	values;
    int		i;

    values = new double[m_Dataset.numAttributes()];
    for (i = 0; i < m_Dataset.numAttributes(); i++) {
      values[i] = Utils.missingValue();
      switch (m_Dataset.attribute(i).type()) {
	case Attribute.NUMERIC:
	case Attribute.DATE:
	  values[i] = input.value(i);
	  break;
	case Attribute.NOMINAL:
	  if (m_Dataset.attribute(i).indexOfValue(input.stringValue(i)) != -1)
	    values[i] = m_Dataset.attribute(i).indexOfValue(input.stringValue(i));
	  break;
	case Attribute.STRING:
	  values[i] = m_Dataset.attribute(i).addStringValue(input.stringValue(i));
	  break;
	case Attribute.RELATIONAL:
	  values[i] = m_Dataset.attribute(i).addRelation(input.relationalValue(i));
	  break;
	default:
	  throw new IllegalStateException("Unhandled attribute type: " + Attribute.typeToString(m_Dataset.attribute(i).type()));
      }
    }

    if (input instanceof SparseInstance)
      result = new SparseInstance(input.weight(), values);
    else
      result = new DenseInstance(input.weight(), values);
    result.setDataset(m_Dataset);

    // fix class index, if necessary
    if ((input.classIndex() != m_Dataset.classIndex()) && (m_Dataset.classIndex() < 0))
      m_Dataset.setClassIndex(input.classIndex());

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    Instance	input;
    Instance	result;
    String	error;

    result = null;

    // get header
    if (m_Dataset == null) {
      m_Dataset = new Instances(getDatasetHeader(), 0);
      if (m_Dataset == null)
	throw new IllegalStateException("Failed to obtain header!");
    }

    input = (Instance) m_Input;

    // check compatibility
    error = isCompatible(input);
    if (error != null)
      throw new IllegalArgumentException("Input is not compatible: " + error);

    // convert (if necessary)
    result = match(input);

    return result;
  }
}
