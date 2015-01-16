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
 * Injector.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
import adams.flow.core.ControlActor;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Injects a string token into the token sequence.<br/>
 * The string can be inject before or after the current token. One can also control how often the string gets injected, i.e., every n-th token.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: Injector
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-injection &lt;java.lang.String&gt; (property: injection)
 * &nbsp;&nbsp;&nbsp;The string to inject into the sequence; tab and newline can be inserted 
 * &nbsp;&nbsp;&nbsp;as escaped sequence: \t and \n
 * &nbsp;&nbsp;&nbsp;default: inject_me
 * </pre>
 * 
 * <pre>-location &lt;BEFORE|AFTER&gt; (property: location)
 * &nbsp;&nbsp;&nbsp;The location where to inject the string.
 * &nbsp;&nbsp;&nbsp;default: AFTER
 * </pre>
 * 
 * <pre>-nth &lt;int&gt; (property: everyNth)
 * &nbsp;&nbsp;&nbsp;The number of tokens after which the injection takes place.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionFromString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to the string before injecting it.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Injector
  extends AbstractTransformer
  implements ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = 5477999576142658922L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_COUNTER = "counter";

  /** the key for storing the queue in the backup. */
  public final static String BACKUP_QUEUE = "queue";

  /**
   * Enumeration for where to inject the String.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Location {
    /** before the current token. */
    BEFORE,
    /** after the current token. */
    AFTER
  }

  /** the string to inject. */
  protected String m_Injection;

  /** where to inject the string. */
  protected Location m_Location;

  /** every nth token the string will get injected. */
  protected int m_EveryNth;

  /** the conversion for turning the string into another object type. */
  protected ConversionFromString m_Conversion;

  /** the strings to output. */
  protected List m_Queue;

  /** the token counter. */
  protected int m_Counter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Injects a string token into the token sequence.\n"
      + "The string can be inject before or after the current token. One can "
      + "also control how often the string gets injected, i.e., every n-th token.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "injection", "injection",
	    "inject_me");

    m_OptionManager.add(
	    "location", "location",
	    Location.AFTER);

    m_OptionManager.add(
	    "nth", "everyNth",
	    1, 1, null);

    m_OptionManager.add(
	    "conversion", "conversion",
	    new StringToString());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "injection", (m_Injection.length() > 0 ? m_Injection : "-none-"));
    result += QuickInfoHelper.toString(this, "location", m_Location, ", location: ");
    result += QuickInfoHelper.toString(this, "everyNth", m_EveryNth, ", every: ");
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
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
   * Sets the string to inject.
   *
   * @param value	the string
   */
  public void setInjection(String value) {
    m_Injection = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to inject.
   *
   * @return		the string
   */
  public String getInjection() {
    return Utils.backQuoteChars(m_Injection);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String injectionTipText() {
    return "The string to inject into the sequence; tab and newline can be inserted as escaped sequence: \\t and \\n";
  }

  /**
   * Sets the location where to inject the string.
   *
   * @param value	the location
   */
  public void setLocation(Location value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns the location wher to inject the string.
   *
   * @return		the location
   */
  public Location getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "The location where to inject the string.";
  }

  /**
   * Sets after how many tokens the injection takes place.
   *
   * @param value	the number of tokens
   */
  public void setEveryNth(int value) {
    if (value >= 1) {
      m_EveryNth = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Value for every nth has to be at least 1, provided: " + value);
    }
  }

  /**
   * Returns after how many tokens the injection takes place.
   *
   * @return		the number of tokens
   */
  public int getEveryNth() {
    return m_EveryNth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String everyNthTipText() {
    return "The number of tokens after which the injection takes place.";
  }

  /**
   * Sets the conversion to apply to the string.
   *
   * @param value	the conversion
   */
  public void setConversion(ConversionFromString value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to apply to the string.
   *
   * @return		the conversion
   */
  public ConversionFromString getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion to apply to the string before injecting it.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Object.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Object.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Object.class};
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_COUNTER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_COUNTER, m_Counter);
    result.put(BACKUP_QUEUE, m_Queue);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_COUNTER)) {
      m_Counter = (Integer) state.get(BACKUP_COUNTER);
      state.remove(BACKUP_COUNTER);
    }

    if (state.containsKey(BACKUP_QUEUE)) {
      m_Queue = (List) state.get(BACKUP_QUEUE);
      state.remove(BACKUP_QUEUE);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Counter = 0;
    m_Queue.clear();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object      obj;

    result = null;

    m_Counter++;

    m_Queue.add(m_InputToken.getPayload());

    if (m_Counter % m_EveryNth == 0) {
      obj = m_Injection;
      if (!(m_Conversion instanceof StringToString)) {
        m_Conversion.setInput(obj);
        result = m_Conversion.convert();
        if (result == null)
          obj = m_Conversion.getOutput();
        m_Conversion.cleanUp();
      }
      if (result == null) {
        if (m_Location == Location.BEFORE)
          m_Queue.add(0, obj);
        else if (m_Location == Location.AFTER)
          m_Queue.add(obj);
        else
          result = "Unhandled location: " + m_Location;
      }
    }

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
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result        = new Token(m_Queue.get(0));
    m_Queue.remove(0);

    m_OutputToken = null;
    m_InputToken  = null;

    return result;
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
