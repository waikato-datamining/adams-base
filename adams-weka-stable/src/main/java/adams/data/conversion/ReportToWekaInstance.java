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
 * ReportToWekaInstance.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Converts a report into a weka.core.Instance objects.
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
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields from the report to turn into attributes of the generated instance.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-data-type &lt;S|N|B|U&gt; [-data-type ...] (property: dataTypes)
 * &nbsp;&nbsp;&nbsp;The data types of the fields to extract.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportToWekaInstance
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 124581970397295630L;

  /** the fields to turn into an instance. */
  protected Field[] m_Fields;

  /** the header to use. */
  protected Instances m_Header;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Converts a report into a weka.core.Instance objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new Field[0]);
  }

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_Header = null;
  }

  /**
   * Sets the fields to use.
   *
   * @param value	the fields
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the fields in use.
   *
   * @return		the fields
   */
  public Field[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The fields from the report to turn into attributes of the generated instance.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts() {
    return Report.class;
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
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    Report	report;
    Instance	result;
    ArrayList	atts;
    ArrayList	attValues;
    int		i;
    double[]	values;

    report = (Report) m_Input;

    // generate header
    if (m_Header == null) {
      atts = new ArrayList();
      for (i = 0; i < m_Fields.length; i++) {
	switch (m_Fields[i].getDataType()) {
	  case NUMERIC:
	    atts.add(new Attribute(m_Fields[i].getName()));
	    break;
	  case BOOLEAN:
	    attValues = new ArrayList();
	    attValues.add("false");
	    attValues.add("true");
	    atts.add(new Attribute(m_Fields[i].getName(), attValues));
	    break;
	  default:
	    atts.add(new Attribute(m_Fields[i].getName(), (List) null));
	    break;
	}
      }
      m_Header = new Instances(getClass().getName(), atts, 0);
    }

    // generate instance
    values = new double[m_Header.numAttributes()];
    for (i = 0; i < m_Fields.length; i++) {
      if (report.hasValue(m_Fields[i])) {
	switch (m_Fields[i].getDataType()) {
	  case NUMERIC:
	    values[i] = report.getDoubleValue(m_Fields[i]);
	    break;
	  case BOOLEAN:
	    if (report.getBooleanValue(m_Fields[i]))
	      values[i] = 1;
	    else
	      values[i] = 0;
	    break;
	  default:
	    values[i] = m_Header.attribute(i).addStringValue("" + report.getValue(m_Fields[i]));
	    break;
	}
      }
      else {
	values[i] = weka.core.Utils.missingValue();
      }
    }
    result = new DenseInstance(1.0, values);
    result.setDataset(m_Header);

    return result;
  }
}
