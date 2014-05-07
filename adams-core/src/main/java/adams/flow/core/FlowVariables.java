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
 * FlowVariables.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.Utils;
import adams.core.Variables;
import adams.flow.control.StorageName;

/**
 * Enhanced variable management, which allows referencing callable actors in 
 * variable names ("@{callable:actorname}") in order to obtain output value of
 * actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowVariables
  extends Variables {

  /** for serialization. */
  private static final long serialVersionUID = 1085438226194687237L;

  /** the callable actor reference prefix. */
  public final static String PREFIX_CALLABLEACTOR = "callable:";

  /** the default value if callable reference could not be expanded. */
  public final static String CALLABLEREF_NOT_FOUND = "CallableRefNotFound";

  /** the storage reference prefix. */
  public final static String PREFIX_STORAGE = "storage:";

  /** the default value if storage reference could not be expanded. */
  public final static String STORAGEREF_NOT_FOUND = "StorageRefNotFound";
  
  /** the flow reference. */
  protected AbstractActor m_Flow;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Initializes the container.
   */
  public FlowVariables() {
    super();
    
    m_Flow   = null;
    m_Helper = new CallableActorHelper();
  }
  
  /**
   * Sets the flow to obtain callable actors from.
   * 
   * @param value	the flow reference
   */
  public void setFlow(AbstractActor value) {
    m_Flow = value;
  }
  
  /**
   * Returns the flow to obtain callable actors from.
   * 
   * @return		the flow reference
   */
  public AbstractActor getFlow() {
    return m_Flow;
  }
  
  /**
   * Checks whether a flow reference is available.
   * 
   * @return		true if reference available
   */
  public boolean hasFlow() {
    return (m_Flow != null);
  }
  
  /**
   * Adds all the variables from the other Variables object (overwrites
   * any existing ones).
   * 
   * @param other	the Variables to copy
   */
  @Override
  public void assign(Variables other) {
    super.assign(other);

    if (other instanceof FlowVariables)
      setFlow(((FlowVariables) other).getFlow());
  }

  /**
   * Checks whether a variable is stored or not.
   *
   * @param name	the name (or placeholder string) of the variable
   * @return		true if the variable is stored or a callable reference (check is too expensive)
   */
  @Override
  public boolean has(String name) {
    if (isObject(name))
      return true;
    else
      return super.has(name);
  }

  /**
   * Returns the value obtain from the callable actor.
   * 
   * @param name	the name of the callable actor
   * @param defValue	the default value
   * @return		the value obtained from the callable actor, defValue in case
   * 			of error
   */
  protected Object getCallableActorValue(String name, Object defValue) {
    Object		result;
    String		msg;
    AbstractActor	callable;
    OutputProducer	prod;
    Token		output;
    
    result = defValue;

    callable = m_Helper.findCallableActor(m_Flow, new CallableActorReference(name));
    msg    = null;
    if (callable != null) {
      if (ActorUtils.isSource(callable)) {
	try {
	  msg = callable.execute();
	  if (msg == null) {
	    prod = (OutputProducer) callable;
	    if (prod.hasPendingOutput()) {
	      output = prod.output();
	      if (output.getPayload() != null)
		result = output.getPayload();
	      else
		msg = "Null token produced!";
	    }
	    else {
	      msg = "No output produced!";
	    }
	  }
	}
	catch (Exception e) {
	  msg = Utils.throwableToString(e);
	}
      }
      else {
	msg = "Not a source actor!";
      }
    }
    else {
      msg = "Not found - invalid reference?";
    }
    
    if (msg != null) {
      System.err.println("Failed to obtain variable value from callable actor '" + name + "':");
      System.err.println(msg);
    }
    
    return result;
  }

  /**
   * Returns the value obtain from storage.
   * 
   * @param name	the name of the storage value
   * @param defValue	the default value
   * @return		the value obtained from storage, defValue in case
   * 			of value not present in storage
   */
  protected Object getStorageValue(String name, Object defValue) {
    Object	result;
    StorageName	sName;
    
    result = defValue;
    
    sName = new StorageName(name);
    if (m_Flow.getStorageHandler().getStorage().has(sName))
      result = m_Flow.getStorageHandler().getStorage().get(sName);
    
    return result;
  }
  
  /**
   * Returns the stored value if present, otherwise the default value.
   *
   * @param name	the name (or placeholder string) of the variable
   * @param defValue	the default value, in case the variable is not stored
   * @return		the associated value
   */
  @Override
  public String get(String name, String defValue) {
    String	result;
    Object	obj;
    
    if (isObject(name)) {
      obj = getObject(name, defValue);
      if (obj == null)
	result = null;
      else
	result = obj.toString();
    }
    else {
      result = super.get(name, defValue);
    }
    
    return result;
  }

  /**
   * Returns whether the stored value is present as non-string object.
   * 
   * @return		true if the value is stored as non-string
   * @see		#getObject(String)
   */
  @Override
  public boolean isObject(String name) {
    boolean	result;
    
    result = false;
    
    if (name == null)
      return result;
    
    if (hasFlow()) {
      name   = extractName(name);
      result = name.startsWith(PREFIX_CALLABLEACTOR) || name.startsWith(PREFIX_STORAGE);
    }
    
    return result;
  }
  
  /**
   * Returns the stored value.
   * 
   * @param name	the name of the value
   * @param defValue	the default value to use if value not present
   * @return		the value referenced by the name, defValue if not available
   */
  @Override
  public Object getObject(String name, Object defValue) {
    Object	result;

    name = extractName(name);
    
    if (name.startsWith(PREFIX_CALLABLEACTOR))
      result = getCallableActorValue(name.substring(PREFIX_CALLABLEACTOR.length()), defValue);
    else if (name.startsWith(PREFIX_STORAGE))
      result = getStorageValue(name.substring(PREFIX_STORAGE.length()), defValue);
    else
      result = defValue;
    
    return result;
  }

  /**
   * Expands regular variables.
   * 
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  protected String doExpandGlobalRefs(String s) {
    String		result;
    String		part;
    String		fullname;
    String		name;
    int			pos;
    
    result = s;
    
    // callable actor refs
    part = Variables.START + PREFIX_CALLABLEACTOR;
    if (result.indexOf(part) > -1) {
      pos = -1;
      do {
	pos      = result.indexOf(part, pos + 1);
	fullname = result.substring(pos, result.indexOf(Variables.END, pos) + 1);
	name     = extractName(fullname).substring(PREFIX_CALLABLEACTOR.length());
	result   = result.replace(fullname, "" + getCallableActorValue(name, CALLABLEREF_NOT_FOUND));
      }
      while (result.indexOf(part, pos) > -1);
    }
    
    // storage refs
    part = Variables.START + PREFIX_STORAGE;
    if (result.indexOf(part) > -1) {
      pos = -1;
      do {
	pos      = result.indexOf(part, pos + 1);
	fullname = result.substring(pos, result.indexOf(Variables.END, pos) + 1);
	name     = extractName(fullname).substring(PREFIX_STORAGE.length());
	result   = result.replace(fullname, "" + getStorageValue(name, STORAGEREF_NOT_FOUND));
      }
      while (result.indexOf(part, pos) > -1);
    }
    
    return result;
  }
  
  /**
   * Performs all expansions.
   * 
   * @param s		the string to expand
   * @return		the potentially expanded string
   */
  @Override
  protected String doExpand(String s) {
    String		result;

    result = doExpandGlobalRefs(s);
    result = super.doExpand(result);
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Flow = null;
    super.cleanUp();
  }
}
