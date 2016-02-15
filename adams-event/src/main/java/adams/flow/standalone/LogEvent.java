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
 * LogEvent.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.logging.AbstractLogHandler;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingListener;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.core.DaemonEvent;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.standalone.logevent.AbstractLogRecordFilter;
import adams.flow.standalone.logevent.AbstractLogRecordProcessor;
import adams.flow.standalone.logevent.AcceptAllFilter;
import adams.flow.standalone.logevent.SimpleProcessor;

import java.util.logging.LogRecord;

/**
 <!-- globalinfo-start -->
 * Listens to the global log record handler and processes records that passed the specified filter(s).<br>
 * This allows, for instance, the output of log messages into a log file.<br>
 * By default, log records are only processed if the sub-actors are not currently being executed. In other words, log records will get dropped if the record processing takes longer than the record generation. Enable the 'noDiscard' property to process all log events - NB: this can slow down the system significantly.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LogEvent
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
 * <pre>-filter &lt;adams.flow.standalone.logevent.AbstractLogRecordFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter used for selecting log records for processing.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.logevent.AcceptAllFilter
 * </pre>
 * 
 * <pre>-processor &lt;adams.flow.standalone.logevent.AbstractLogRecordProcessor&gt; (property: processor)
 * &nbsp;&nbsp;&nbsp;The processor used for turning the log record into a different format.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.logevent.SimpleProcessor
 * </pre>
 * 
 * <pre>-no-discard &lt;boolean&gt; (property: noDiscard)
 * &nbsp;&nbsp;&nbsp;If enabled, no log event gets discarded; CAUTION: enabling this option can 
 * &nbsp;&nbsp;&nbsp;slow down the system significantly.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to process the output generated from the log record.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * For more information on the schedule format, see
 * <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html" target="_blank">CronTrigger Tutorial</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEvent
  extends AbstractMutableActorDaemonEvent<LogRecord, Object>
  implements MutableActorHandler, DaemonEvent, LoggingListener {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the log record filter. */
  protected AbstractLogRecordFilter m_Filter;

  /** the log record processor. */
  protected AbstractLogRecordProcessor m_Processor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Listens to the global log record handler and processes records that "
	+ "passed the specified filter(s).\n"
        + "This allows, for instance, the output of log messages into a log file.\n"
	+ "By default, log records are only processed if the sub-actors are not "
        + "currently being executed. In other words, log records will get "
	+ "dropped if the record processing takes longer than the record generation. "
        + "Enable the 'noDiscard' property to process all log events - NB: this "
	+ "can slow down the system significantly.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new AcceptAllFilter());

    m_OptionManager.add(
	    "processor", "processor",
	    new SimpleProcessor());
  }

  /**
   * Checks the actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkActors(Actor[] actors) {
    int			i;
    Compatibility	comp;

    comp = new Compatibility();
    for (i = 0; i < actors.length; i++) {
      if (actors[i].getSkip())
	continue;
      if (!(actors[i] instanceof InputConsumer))
	return "Actor #" + (i+1) + " does not accept input!";
      if (!comp.isCompatible(new Class[]{m_Processor.generates()}, ((InputConsumer) actors[i]).accepts()))
	return "Actor #" + (i+1) + " does not accept the processor's output: " + Utils.classToString(m_Processor.generates());
      break;
    }

    return null;
  }

  /**
   * Sets the record filter to use.
   *
   * @param value 	the filter
   */
  public void setFilter(AbstractLogRecordFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the record filter to use.
   *
   * @return 		the filter
   */
  public AbstractLogRecordFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter used for selecting log records for processing.";
  }

  /**
   * Sets the record processor to use.
   *
   * @param value 	the processor
   */
  public void setProcessor(AbstractLogRecordProcessor value) {
    m_Processor = value;
    reset();
  }

  /**
   * Returns the record processor to use.
   *
   * @return 		the processor
   */
  public AbstractLogRecordProcessor getProcessor() {
    return m_Processor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorTipText() {
    return "The processor used for turning the log record into a different format.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
    result += QuickInfoHelper.toString(this, "processor", m_Processor, ", processor: ");
    value   = QuickInfoHelper.toString(this, "noDiscard", m_NoDiscard, "no discard", ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Checks whether the event is being handled.
   *
   * @param e		the event to check
   * @return		true if being handled
   */
  @Override
  protected boolean handlesEvent(LogRecord e) {
    return m_Filter.acceptRecord(e);
  }

  /**
   * Preprocesses the event.
   *
   * @param e		the event to preprocess
   * @return		the output of the preprocessing
   */
  @Override
  protected Object preProcessEvent(LogRecord e) {
    return m_Processor.processRecord(e);
  }

  /**
   * Returns whether the preprocessed event is used as input token.
   *
   * @return		true if used as input token
   */
  @Override
  protected boolean usePreProcessedAsInput() {
    return true;
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      result = m_Actors.setUp();
      if (result == null) {
	if (!(LoggingHelper.getDefaultHandler() instanceof AbstractLogHandler)) {
	  result = 
	        "Default logging handler (" + LoggingHelper.getDefaultHandler().getClass().getName() + ") "
	      + "is not derived from " + AbstractLogHandler.class.getName() + "!";
	}
	else {
	  ((AbstractLogHandler) LoggingHelper.getDefaultHandler()).addLoggingListener(this);
	}
      }
    }

    return result;
  }

  /**
   * Gets called in case of a log event.
   * 
   * @param source	the handler that sent out the notification
   * @param record	the record associated with the log event
   */
  public void logEventOccurred(AbstractLogHandler source, LogRecord record) {
    if (!isStopped())
      processEvent(record);
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (LoggingHelper.getDefaultHandler() instanceof AbstractLogHandler)
      ((AbstractLogHandler) LoggingHelper.getDefaultHandler()).removeLoggingListener(this);

    super.wrapUp();
  }
}
