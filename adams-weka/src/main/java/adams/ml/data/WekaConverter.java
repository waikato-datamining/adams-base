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
 * WekaConverter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Helper class for converting data to and fro Weka.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaConverter {

  /**
   * Converts an ADAMS Dataset to Weka Instances.
   * Only sets the class attribute if the dataset has exactly one defined.
   *
   * @param data	the data to convert
   * @return		the generated data
   * @throws Exception	if conversion fails
   */
  public static Instances toInstances(Dataset data) throws Exception {
    Instances			result;
    SpreadSheetToWekaInstances	conv;
    String			msg;
    int[]			classes;

    conv = new SpreadSheetToWekaInstances();
    conv.setInput(data);
    msg = conv.convert();
    if (msg != null)
      throw new Exception("Failed to convert Dataset to Instances: " + msg);

    result = (Instances) conv.getOutput();
    conv.cleanUp();

    classes = data.getClassAttributeIndices();
    if (classes.length == 1)
      result.setClassIndex(classes[0]);

    return result;
  }

  /**
   * Converts a Weka Instances object into an ADAMS Dataset.
   *
   * @param data	the data to convert
   * @return		the generated data
   * @throws Exception	if conversion fail
   */
  public static Dataset toDataset(Instances data) throws Exception {
    Dataset			result;
    WekaInstancesToSpreadSheet	conv;
    String			msg;

    // TODO just use a view?

    conv = new WekaInstancesToSpreadSheet();
    conv.setSpreadSheetType(new DefaultDataset());
    msg = conv.convert();
    if (msg != null)
      throw new Exception("Failed to convert Instances to Dataset: " + msg);

    result = (Dataset) conv.getOutput();
    if (data.classIndex() > -1)
      result.setClassAttribute(data.classIndex(), true);

    return result;
  }

  /**
   * Turns an ADAMS dataset row into a Weka Instance.
   *
   * @param data	the dataset to use as template
   * @param row		the row to convert
   * @return		the generated instance
   * @throws Exception	if conversion fails
   */
  public static Instance toInstance(Instances data, Row row) throws Exception {
    Instance	result;
    double[]	values;
    int		i;
    Cell 	cell;
    Attribute	att;

    values = new double[data.numAttributes()];
    for (i = 0; i < data.numAttributes(); i++) {
      values[i] = Utils.missingValue();

      if (!row.hasCell(i))
	continue;
      cell = row.getCell(i);
      if (cell.isMissing())
	continue;

      att = data.attribute(i);
      switch (att.type()) {
	case Attribute.NUMERIC:
	  values[i] = cell.toDouble();
	  break;
	case Attribute.DATE:
	  values[i] = cell.toAnyDateType().getTime();
	  break;
	case Attribute.NOMINAL:
	  values[i] = att.indexOfValue(cell.getContent());
	  break;
	case Attribute.STRING:
	  values[i] = att.addStringValue(cell.getContent());
	  break;
	default:
	  throw new Exception("Unhandled Weka attribute type: " + Attribute.typeToString(att));
      }
    }

    result = new DenseInstance(1.0, values);
    result.setDataset(data);

    return result;
  }
}
