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
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
import adams.flow.core.ControlActor;
import adams.flow.core.Token;
import adams.flow.transformer.AbstractTransformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Injects a string token into the token sequence.<br>
 * The string can be inject before or after the current token. One can also control how often the string gets injected, i.e., every n-th token.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;STRING|STORAGE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of data to inject.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 * 
 * <pre>-location &lt;BEFORE|AFTER|INPLACE&gt; (property: location)
 * &nbsp;&nbsp;&nbsp;The location where to inject the data.
 * &nbsp;&nbsp;&nbsp;default: AFTER
 * </pre>
 * 
 * <pre>-injection &lt;java.lang.String&gt; (property: injection)
 * &nbsp;&nbsp;&nbsp;The string to inject into the sequence; tab and newline can be inserted 
 * &nbsp;&nbsp;&nbsp;as escaped sequence: \t and \n
 * &nbsp;&nbsp;&nbsp;default: inject_me
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionFromString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to the string before injecting it.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item to inject.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-nth &lt;int&gt; (property: everyNth)
 * &nbsp;&nbsp;&nbsp;The number of tokens after which the injection takes place.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
   * Enumeration of what type of data to inject.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DataType {
    /** the specified string. */
    STRING,
    /** the specified storage item. */
    STORAGE
  }

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
    AFTER,
    /** replace the current token. */
    INPLACE
  }

  /** the data to inject. */
  protected DataType m_Type;

  /** where to inject the string. */
  protected Location m_Location;

  /** the string to inject. */
  protected String m_Injection;

  /** every nth token the string will get injected. */
  protected int m_EveryNth;

  /** the conversion for turning the string into another object type. */
  protected ConversionFromString m_Conversion;

  /** the storage item. */
  protected StorageName m_StorageName;

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
      "type", "type",
      DataType.STRING);

    m_OptionManager.add(
      "location", "location",
      Location.AFTER);

    m_OptionManager.add(
      "injection", "injection",
      "inject_me");

    m_OptionManager.add(
      "conversion", "conversion",
      new StringToString());

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "nth", "everyNth",
      1, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "location", m_Location, ", location: ");
    if (!getOptionManager().hasVariableForProperty("type")) {
      switch (m_Type) {
	case STRING:
	  result += QuickInfoHelper.toString(this, "injection", (m_Injection.length() > 0 ? m_Injection : "-none-"), ", inject: ");
	  result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");
	  break;
	case STORAGE:
	  result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", storage: ");
	  break;
	default:
	  throw new IllegalStateException("Unhandled data type: " + m_Type);
      }
    }
    result += QuickInfoHelper.toString(this, "everyNth", m_EveryNth, ", every: ");

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
   * Sets the type of data to inject.
   *
   * @param value	the type
   */
  public void setType(DataType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of data to inject.
   *
   * @return		the type
   */
  public DataType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of data to inject.";
  }

  /**
   * Sets the location where to inject the data.
   *
   * @param value	the location
   */
  public void setLocation(Location value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns the location where to inject the data.
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
    return "The location where to inject the data.";
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
   * Sets the name of the storage item to inject.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to inject.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item to inject.";
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
    return "The conversion to apply to the string before injecting it (does not apply to STORAGE injection).";
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

    m_Queue.clear();
    m_Counter++;

    if (m_Counter % m_EveryNth == 0) {
      switch (m_Type) {
	case STRING:
	  obj = m_Injection;
	  if (!(m_Conversion instanceof StringToString)) {
	    m_Conversion.setInput(obj);
	    result = m_Conversion.convert();
	    if (result == null)
	      obj = m_Conversion.getOutput();
	    m_Conversion.cleanUp();
	  }
	  break;
	case STORAGE:
	  obj = getStorageHandler().getStorage().get(m_StorageName);
	  if (obj == null)
	    result = "Failed to obtain storage item: " + m_StorageName;
	  break;
	default:
	  throw new IllegalStateException("Unhandled data type: " + m_Type);
      }

      if (result == null) {
	switch (m_Location) {
	  case BEFORE:
	    m_Queue.add(obj);
	    m_Queue.add(m_InputToken.getPayload());
	    break;
	  case AFTER:
	    m_Queue.add(m_InputToken.getPayload());
	    m_Queue.add(obj);
	    break;
	  case INPLACE:
	    m_Queue.add(obj);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled location: " + m_Location);
	}
      }
    }
    else {
      m_Queue.add(m_InputToken.getPayload());
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
