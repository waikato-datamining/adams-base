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
 * WekaInstancesStatistic.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Instances;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.ArrayMean;
import adams.data.statistics.StatUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates statistics from a weka.core.Instances object.<br>
 * The actor just uses the internal format (double array) and does not check whether the attributes are actually numeric.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: InstancesStatistic
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-type &lt;ROW_BY_INDEX|COLUMN_BY_INDEX|COLUMN_BY_REGEXP&gt; (property: dataType)
 * &nbsp;&nbsp;&nbsp;Whether to retrieve rows or columns from the Instances object.
 * &nbsp;&nbsp;&nbsp;default: COLUMN_BY_INDEX
 * </pre>
 *
 * <pre>-location &lt;adams.core.base.BaseString&gt; [-location ...] (property: locations)
 * &nbsp;&nbsp;&nbsp;The locations of the data, depending on the chosen data type that can be
 * &nbsp;&nbsp;&nbsp;either indices or regular expressions on the attribute names.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-statistic &lt;adams.data.statistics.AbstractArrayStatistic [options]&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The statistic to generate from the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.statistics.ArrayMean
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesStatistic
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8493694755948450901L;

  /**
   * Defines what data to retrieve from an Instances object.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DataType {
    /** obtains rows. */
    ROW_BY_INDEX,
    /** obtains columns (by index). */
    COLUMN_BY_INDEX,
    /** obtains columns (by reg exp). */
    COLUMN_BY_REGEXP
  }

  /** the type of data to get from the Instances object (rows or columns). */
  protected DataType m_DataType;

  /** the array of indices/regular expressions. */
  protected BaseString[] m_Locations;

  /** the statistic to generate. */
  protected AbstractArrayStatistic m_Statistic;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates statistics from a weka.core.Instances object.\n"
      + "The actor just uses the internal format (double array) and does "
      + "not check whether the attributes are actually numeric.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "dataType",
	    DataType.COLUMN_BY_INDEX);

    m_OptionManager.add(
	    "location", "locations",
	    new BaseString[0]);

    m_OptionManager.add(
	    "statistic", "statistic",
	    new ArrayMean());
  }

  /**
   * Sets what type of data to retrieve from the Instances object.
   *
   * @param value	the type of conversion
   */
  public void setDataType(DataType value) {
    m_DataType = value;
    reset();
  }

  /**
   * Returns what type of data to retrieve from the Instances object.
   *
   * @return		the type of conversion
   */
  public DataType getDataType() {
    return m_DataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataTypeTipText() {
    return "Whether to retrieve rows or columns from the Instances object.";
  }

  /**
   * Sets the locations of the data (indices/regular expressions on attribute name).
   *
   * @param value	the locations of the data
   */
  public void setLocations(BaseString[] value) {
    m_Locations = value;
    reset();
  }

  /**
   * Returns the locations of the data (indices/regular expressions on attribute name).
   *
   * @return		the locations of the data
   */
  public BaseString[] getLocations() {
    return m_Locations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationsTipText() {
    return
        "The locations of the data, depending on the chosen data type that "
      + "can be either indices or regular expressions on the attribute names.";
  }

  /**
   * Sets the statistic to use.
   *
   * @param value	the statistic
   */
  public void setStatistic(AbstractArrayStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the statistic in use.
   *
   * @return		the statistic
   */
  public AbstractArrayStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to generate from the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "statistic", m_Statistic);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.core.io.SpreadSheet.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    Instances			data;
    int				i;
    int				n;
    Index			index;
    AbstractArrayStatistic 	stat;

    result = null;

    try {
      sheet = null;
      data  = (Instances) m_InputToken.getPayload();
      stat  = m_Statistic.shallowCopy(true);

      for (i = 0; i < m_Locations.length; i++) {
	switch (m_DataType) {
	  case ROW_BY_INDEX:
	    index = new Index(m_Locations[i].stringValue());
	    index.setMax(data.numInstances());
	    stat.add(StatUtils.toNumberArray(data.instance(index.getIntIndex()).toDoubleArray()));
	    break;

	  case COLUMN_BY_INDEX:
	    index = new Index(m_Locations[i].stringValue());
	    index.setMax(data.numAttributes());
	    stat.add(StatUtils.toNumberArray(data.attributeToDoubleArray(index.getIntIndex())));
	    break;

	  case COLUMN_BY_REGEXP:
	    for (n = 0; n < data.numAttributes(); n++) {
	      if (data.attribute(n).name().matches(m_Locations[i].stringValue())) {
		stat.add(StatUtils.toNumberArray(data.attributeToDoubleArray(n)));
		break;
	      }
	    }
	    break;

	  default:
	    throw new IllegalStateException("Unhandlded data type: " + m_DataType);
	}
      }

      sheet = stat.calculate().toSpreadSheet();
    }
    catch (Exception e) {
      result = handleException("Error generating the statistic: ", e);
      sheet = null;
    }

    if (sheet != null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
