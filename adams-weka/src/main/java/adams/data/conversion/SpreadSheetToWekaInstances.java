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
 * SpreadSheetToWekaInstances.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import adams.core.Constants;
import adams.core.Utils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.ml.data.Dataset;

/**
 <!-- globalinfo-start -->
 * Generates a weka.core.Instances object from a SpreadSheet object.<br>
 * If there are too many unique lables for a NOMINAL attribute, it gets turned into a STRING attribute (see 'maxLabels' property).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max-labels &lt;int&gt; (property: maxLabels)
 * &nbsp;&nbsp;&nbsp;The maximum number of labels that a NOMINAL attribute can have before it 
 * &nbsp;&nbsp;&nbsp;is switched to a STRING attribute.
 * &nbsp;&nbsp;&nbsp;default: 25
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToWekaInstances
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 867886761713927179L;

  /** the threshold for number of labels before an attribute gets switched 
   * to {@link Attribute#STRING}. */
  protected int m_MaxLabels;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a " + Instances.class.getName() + " object from a "
	+ "SpreadSheet object.\n"
	+ "If there are too many unique lables for a NOMINAL attribute, it gets "
	+ "turned into a STRING attribute (see 'maxLabels' property).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max-labels", "maxLabels",
	    25, 1, null);
  }

  /**
   * Sets the maximum number of labels a nominal attribute can have.
   *
   * @param value 	the maximum
   */
  public void setMaxLabels(int value) {
    m_MaxLabels = value;
    reset();
  }

  /**
   * Returns the name of the global actor in use.
   *
   * @return 		the global name
   */
  public int getMaxLabels() {
    return m_MaxLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLabelsTipText() {
    return 
	"The maximum number of labels that a NOMINAL attribute can have "
	+ "before it is switched to a STRING attribute.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return adams.data.spreadsheet.SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return weka.core.Instances.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instances			result;
    SpreadSheet			sheet;
    DenseInstance		inst;
    ArrayList<Attribute>	atts;
    HashSet<String>		unique;
    ArrayList<String>		labels;
    Row				row;
    Cell			cell;
    int				i;
    int				n;
    double[]			values;
    Collection<ContentType>	types;
    ContentType			type;
    boolean			added;
    int[]			classIndices;

    sheet = (SpreadSheet) m_Input;

    // create header
    atts = new ArrayList<Attribute>();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      added = false;
      types = sheet.getContentTypes(i);
      if (types.contains(ContentType.DOUBLE))
	types.remove(ContentType.LONG);
      if (types.contains(ContentType.LONG)) {
	types.add(ContentType.DOUBLE);
	types.remove(ContentType.LONG);
      }
      
      if (types.size() == 1) {
	type = (ContentType) types.toArray()[0];
	if (type == ContentType.DOUBLE) {
	  atts.add(new Attribute(sheet.getHeaderRow().getCell(i).getContent()));
	  added = true;
	}
	else if (type == ContentType.DATE) {
	  atts.add(new Attribute(sheet.getHeaderRow().getCell(i).getContent(), Constants.TIMESTAMP_FORMAT));
	  added = true;
	}
	else if (type == ContentType.TIME) {
	  atts.add(new Attribute(sheet.getHeaderRow().getCell(i).getContent(), Constants.TIME_FORMAT));
	  added = true;
	}
      }
      
      if (!added) {
	unique = new HashSet<String>();
	for (n = 0; n < sheet.getRowCount(); n++) {
	  row  = sheet.getRow(n);
	  cell = row.getCell(i);
	  if ((cell != null) && !cell.isMissing())
	    unique.add(cell.getContent());
	}
	if (unique.size() > m_MaxLabels) {
	  atts.add(new Attribute(sheet.getHeaderRow().getCell(i).getContent(), (FastVector) null));
	}
	else {
	  labels = new ArrayList<String>(unique);
	  Collections.sort(labels);
	  atts.add(new Attribute(sheet.getHeaderRow().getCell(i).getContent(), labels));
	}
      }
    }
    result = new Instances(Environment.getInstance().getProject(), atts, sheet.getRowCount());
    if (sheet.hasName())
      result.setRelationName(sheet.getName());

    // add data
    for (n = 0; n < sheet.getRowCount(); n++) {
      row    = sheet.getRow(n);
      values = new double[result.numAttributes()];
      for (i = 0; i < result.numAttributes(); i++) {
	cell      = row.getCell(i);
	values[i] = weka.core.Utils.missingValue();
	if ((cell != null) && !cell.isMissing()) {
	  if (result.attribute(i).type() == Attribute.DATE) {
	    if (cell.isTime())
	      values[i] = cell.toTime().getTime();
	    else
	      values[i] = cell.toDate().getTime();
	  }
	  else if (result.attribute(i).isNumeric()) {
	    values[i] = Utils.toDouble(cell.getContent());
	  }
	  else if (result.attribute(i).isString()) {
	    values[i] = result.attribute(i).addStringValue(cell.getContent());
	  }
	  else {
	    values[i] = result.attribute(i).indexOfValue(cell.getContent());
	  }
	}
      }
      inst = new DenseInstance(1.0, values);
      result.add(inst);
    }

    if (sheet instanceof Dataset) {
      classIndices = ((Dataset) sheet).getClassAttributeIndices();
      if (classIndices.length > 0)
	result.setClassIndex(classIndices[0]);
    }
    
    return result;
  }
}
