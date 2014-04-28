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
 * TimeseriesInfo.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.data.DateFormatString;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs information for a timeseries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TimeseriesInfo
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-type &lt;ID|DB_ID|NUM_POINTS|MIN_TIMESTAMP|MAX_TIMESTAMP|MIN_VALUE|MAX_VALUE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: ID
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format for turning the timestamps into a string.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesInfo
  extends AbstractTransformer
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;
  
  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** the spectrum ID. */
    ID,
    /** the database ID. */
    DB_ID,
    /** the number of data points. */
    NUM_POINTS,
    /** the smallest timestamp. */
    MIN_TIMESTAMP,
    /** the largest timestamp. */
    MAX_TIMESTAMP,
    /** the smallest value. */
    MIN_VALUE,
    /** the largest value. */
    MAX_VALUE,
  }

  /** the tokens to output. */
  protected ArrayList m_Queue;

  /** the type of information to generate. */
  protected InfoType m_Type;
  
  /** the format string to use for the timestamps. */
  protected DateFormatString m_Format;

  /** the formatter. */
  protected transient DateFormat m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information for a timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    InfoType.ID);

    m_OptionManager.add(
	    "format", "format",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets the format to use for the timestamps.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format in use for the timestamps.
   *
   * @return		the format
   */
  public DateFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format for turning the timestamps into a string.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.timeseries.Timeseries.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case ID:
      case MIN_TIMESTAMP:
      case MAX_TIMESTAMP:
	return new Class[]{String.class};

      case NUM_POINTS:
      case DB_ID:
	return new Class[]{Integer.class};

      case MIN_VALUE:
      case MAX_VALUE:
	return new Class[]{Double.class};

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
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
    Timeseries		series;

    result = null;

    series = (Timeseries) m_InputToken.getPayload();
    if (m_Formatter == null)
      m_Formatter = m_Format.toDateFormat();

    m_Queue.clear();

    switch (m_Type) {
      case ID:
	m_Queue.add(series.getID());
	break;

      case DB_ID:
	m_Queue.add(series.getDatabaseID());
	break;

      case NUM_POINTS:
	m_Queue.add(series.size());
	break;

      case MIN_TIMESTAMP:
	if (series.size() > 0)
	  m_Queue.add(m_Formatter.format(((TimeseriesPoint) series.toList().get(0)).getTimestamp()));
	break;

      case MAX_TIMESTAMP:
	if (series.size() > 0)
	  m_Queue.add(m_Formatter.format(((TimeseriesPoint) series.toList().get(series.size() - 1)).getTimestamp()));
	break;

      case MIN_VALUE:
	if (series.getMinValue() != null)
	  m_Queue.add(new Double(series.getMinValue().getValue()));
	break;

      case MAX_VALUE:
	if (series.getMaxValue() != null)
	  m_Queue.add(new Double(series.getMaxValue().getValue()));
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Queue.get(0));
    m_Queue.remove(0);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Queue.size() > 0);
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Queue.clear();

    super.wrapUp();
  }
}
