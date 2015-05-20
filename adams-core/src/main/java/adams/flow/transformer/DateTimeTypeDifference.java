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
 * DateTimeTypeDifference.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.DateTime;
import adams.core.DateTimeType;
import adams.core.QuickInfoHelper;
import adams.core.Time;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.data.conversion.ConvertDateTimeType;
import adams.flow.core.Token;

import java.lang.reflect.Array;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Computes the difference between the two date&#47;time types of the incoming array (of length 2) by subtracting the second element from the first one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Date[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DateTimeTypeDifference
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-input-datetime-type &lt;MSECS|SECONDS|DATE|DATETIME|TIME|BASEDATE|BASEDATETIME|BASETIME|JULIANDATE&gt; (property: inputDateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type of the input data.
 * &nbsp;&nbsp;&nbsp;default: DATE
 * </pre>
 * 
 * <pre>-output-datetime-type &lt;MSECS|SECONDS|DATE|DATETIME|TIME|BASEDATE|BASEDATETIME|BASETIME|JULIANDATE&gt; (property: outputDateTimeType)
 * &nbsp;&nbsp;&nbsp;The date&#47;time type of the output data.
 * &nbsp;&nbsp;&nbsp;default: MSECS
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateTimeTypeDifference
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1393364512048214265L;

  /** the datetime type of the input. */
  protected DateTimeType m_InputDateTimeType;

  /** the datetime type of the output. */
  protected DateTimeType m_OutputDateTimeType;
  
  /** the conversion in use for the input. */
  protected ConvertDateTimeType	m_ConversionInput;

  /** the conversion in use for the output. */
  protected ConvertDateTimeType m_ConversionOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Computes the difference between the two date/time types of the "
	+ "incoming array (of length 2) by subtracting the second element "
	+ "from the first one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "input-datetime-type", "inputDateTimeType",
	    DateTimeType.DATE);

    m_OptionManager.add(
	    "output-datetime-type", "outputDateTimeType",
	    DateTimeType.MSECS);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_ConversionInput  = null;
    m_ConversionOutput = null;
  }
  
  /**
   * Sets the input date/time type.
   *
   * @param value	the type
   */
  public void setInputDateTimeType(DateTimeType value) {
    m_InputDateTimeType = value;
    reset();
  }

  /**
   * Returns the input date/time type.
   *
   * @return		the type
   */
  public DateTimeType getInputDateTimeType() {
    return m_InputDateTimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputDateTimeTypeTipText() {
    return "The date/time type of the input data.";
  }
  
  /**
   * Sets the output date/time type.
   *
   * @param value	the type
   */
  public void setOutputDateTimeType(DateTimeType value) {
    m_OutputDateTimeType = value;
    reset();
  }

  /**
   * Returns the output date/time type.
   *
   * @return		the type
   */
  public DateTimeType getOutputDateTimeType() {
    return m_OutputDateTimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDateTimeTypeTipText() {
    return "The date/time type of the output data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "inputDateType", m_InputDateTimeType);
    result += "[] -> ";
    result += QuickInfoHelper.toString(this, "outputDateType", m_OutputDateTimeType);
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    switch (m_InputDateTimeType) {
      case MSECS:
	return new Class[]{Double[].class};
      case SECONDS:
	return new Class[]{Double[].class};
      case DATE:
	return new Class[]{Date[].class};
      case DATETIME:
	return new Class[]{DateTime[].class};
      case TIME:
	return new Class[]{Time[].class};
      case BASEDATE:
	return new Class[]{BaseDate[].class};
      case BASEDATETIME:
	return new Class[]{BaseDateTime[].class};
      case BASETIME:
	return new Class[]{BaseTime[].class};
      case JULIANDATE:
	return new Class[]{Double[].class};
      case SERIAL_DATETIME:
	return new Class[]{Double[].class};
      default:
	throw new IllegalStateException("Unhandled input data/time type: " + m_InputDateTimeType);
    }
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_OutputDateTimeType) {
      case MSECS:
	return new Class[]{Double.class};
      case SECONDS:
	return new Class[]{Double.class};
      case DATE:
	return new Class[]{Date.class};
      case DATETIME:
	return new Class[]{DateTime.class};
      case TIME:
	return new Class[]{Time.class};
      case BASEDATE:
	return new Class[]{BaseDate.class};
      case BASEDATETIME:
	return new Class[]{BaseDateTime.class};
      case BASETIME:
	return new Class[]{BaseTime.class};
      case JULIANDATE:
	return new Class[]{Double.class};
      case SERIAL_DATETIME:
	return new Class[]{Double.class};
      default:
	throw new IllegalStateException("Unhandled output data/time type: " + m_OutputDateTimeType);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		array;
    double[]		in;
    int			i;
    double		diff;
    
    result = null;
    
    if (m_ConversionInput == null) {
      m_ConversionInput = new ConvertDateTimeType();
      m_ConversionInput.setInputDateTimeType(m_InputDateTimeType);
      m_ConversionInput.setOutputDateTimeType(DateTimeType.MSECS);
    }
    
    diff  = Double.NaN;
    array = m_InputToken.getPayload();
    if (Array.getLength(array) != 2)
      result = "Input array must have length 2, received: " + Array.getLength(array);
    
    if (result == null) {
      in = new double[2];
      for (i = 0; i < 2; i++) {
	m_ConversionInput.setInput(Array.get(array, i));
	result = m_ConversionInput.convert();
	if (result == null)
	  in[i] = (Double) m_ConversionInput.getOutput();
	else
	  break;
      }
      if (result == null)
	diff = in[0] - in[1];
      m_ConversionInput.cleanUp();
    }
    
    if (result == null) {
      if (m_ConversionOutput == null) {
	m_ConversionOutput = new ConvertDateTimeType();
	m_ConversionOutput.setInputDateTimeType(DateTimeType.MSECS);
	m_ConversionOutput.setOutputDateTimeType(m_OutputDateTimeType);
      }
      m_ConversionOutput.setInput(diff);
      result = m_ConversionOutput.convert();
      if (result == null)
	m_OutputToken = new Token(m_ConversionOutput.getOutput());
      m_ConversionOutput.cleanUp();
    }

    return result;
  }
}
