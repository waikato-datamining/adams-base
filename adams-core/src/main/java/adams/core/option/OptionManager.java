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
 * OptionManager.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.CleanUpHandler;
import adams.core.EnumWithCustomDisplay;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.VariablesHandler;
import adams.core.base.BaseObject;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.constraint.AbstractOptionConstraint;
import adams.env.Environment;
import adams.env.OptionManagerDefinition;
import adams.flow.core.Actor;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class for managing option definitions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class OptionManager
  implements Serializable, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2383307592894383257L;

  public final static String FILENAME = "OptionManager.props";

  /** the properties in use. */
  protected static Properties m_Properties;

  /** default instance (gets replaced at runtime). */
  protected static Variables m_DefaultVariablesInstance;

  /** the blacklisted classes. */
  protected static Set<String> m_BlacklistedClasses;

  /** the owner. */
  protected OptionHandler m_Owner;

  /** the options. */
  protected List<AbstractOption> m_Options;

  /** the commandline flag &lt;-&gt; index relationship. */
  protected Map<String,Integer> m_CommandlineIndex;

  /** the (bean) property &lt;-&gt; index relationship. */
  protected Map<String,Integer> m_PropertyIndex;

  /** the properties that cannot have variables attached. */
  protected Set<String> m_NoVariablesProperties;

  /** the removed flags. */
  protected Set<String> m_RemovedFlags;

  /** the removed properties. */
  protected Set<String> m_RemovedProperties;

  /** whether to throw exceptions or just ignore errors. */
  protected boolean m_ThrowExceptions;

  /** the Variables instance to use for resolving variables. */
  protected transient Variables m_Variables;

  /** whether to suppress error messages. */
  protected boolean m_Quiet;

  /**
   * Initializes the manager.
   *
   * @param owner	the owner of this manager
   */
  public OptionManager(OptionHandler owner) {
    super();

    m_Owner                 = owner;
    m_Options               = new ArrayList<>();
    m_CommandlineIndex      = new HashMap<>();
    m_PropertyIndex         = new HashMap<>();
    m_NoVariablesProperties = null;
    m_RemovedFlags          = null;
    m_RemovedProperties     = null;
    m_ThrowExceptions       = false;
    m_Variables             = null;
    m_Quiet                 = false;
  }

  /**
   * Returns the owning OptionHandler.
   *
   * @return		the owner
   */
  public OptionHandler getOwner() {
    return m_Owner;
  }

  /**
   * Sets the Variables instance to use (not recursively!).
   *
   * @param value	the instance to use
   * @see		#updateVariablesInstance(Variables)
   */
  public void setVariables(Variables value) {
    m_Variables = value;
  }

  /**
   * Returns the variables instance in use.
   *
   * @return		the instance in use
   */
  public synchronized Variables getVariables() {
    if (m_Variables == null) {
      if (m_DefaultVariablesInstance == null)
	m_DefaultVariablesInstance = new Variables();
      m_Variables = m_DefaultVariablesInstance;
    }
    return m_Variables;
  }

  /**
   * Sets whether to throw exceptions or simply ignore errors (recursively).
   *
   * @param value	if true then exceptions are thrown on errors
   */
  public void setThrowExceptions(boolean value) {
    int		i;
    Object	current;

    m_ThrowExceptions = value;

    for (i = 0; i < m_Options.size(); i++) {
      if (m_Options.get(i) instanceof ClassOption) {
	current = ((ClassOption) m_Options.get(i)).getCurrentValue();
	if (current instanceof OptionHandler)
	  ((OptionHandler) current).getOptionManager().setThrowExceptions(value);
      }
    }
  }

  /**
   * Returns whether to throw exceptions on errors or to ignore them.
   *
   * @return		true if exceptions are thrown in case of errors
   */
  public boolean getThrowExceptions() {
    return m_ThrowExceptions;
  }

  /**
   * Sets whether to suppress error messages.
   *
   * @param value	true if to suppress error messages
   */
  public void setQuiet(boolean value) {
    m_Quiet = value;
  }

  /**
   * Returns whether to suppress error messages.
   *
   * @return		true if to suppress error messages
   */
  public boolean isQuiet() {
    return m_Quiet;
  }

  /**
   * Handles the given throwable/exception. Either throws the exception or
   *
   * @param t		the error to throw/print
   * @see		#getThrowExceptions()
   */
  protected void handleError(Throwable t) {
    if (m_ThrowExceptions)
      throw new OptionManagerException(t);
    else
      LoggingHelper.global().log(Level.SEVERE, "Option handling error occurred: " + Utils.classToString(getOwner()), t);
  }

  /**
   * Determines the appropriate concrete option class, sets it up and returns it.
   * This call will always output the default value.
   * Adds the option at the end.
   *
   * @param commandline	the commandline string (without the leading dash "-")
   * @param property	the Java Beans property name
   * @param defValue	the default value
   * @return		the generated option object
   */
  public AbstractOption add(String commandline, String property, Object defValue) {
    return insert(-1, commandline, property, defValue, null, null);
  }

  /**
   * Determines the appropriate concrete option class, sets it up and returns it.
   * This call will always output the default value.
   *
   * @param index	the position for the option, use -1 to add at the end
   * @param commandline	the commandline string (without the leading dash "-")
   * @param property	the Java Beans property name
   * @param defValue	the default value
   * @return		the generated option object
   */
  public AbstractOption insert(int index, String commandline, String property, Object defValue) {
    return insert(index, commandline, property, defValue, null, null);
  }

  /**
   * Determines the appropriate concrete option class, sets it up and returns it.
   * This call will always output the default value.
   * Adds the option at the end.
   *
   * @param commandline	the commandline string (without the leading dash "-")
   * @param property	the Java Beans property name
   * @param defValue	the default value
   * @param lower	the lower bound
   * @param upper	the upper bound
   * @return		the generated option object
   */
  public AbstractOption add(String commandline, String property, Object defValue, Number lower, Number upper) {
    return insert(-1, commandline, property, defValue, lower, upper);
  }

  /**
   * Determines the appropriate concrete option class, sets it up and returns it.
   *
   * @param index		the position for the option, use -1 to add at the end
   * @param commandline		the commandline string (without the leading dash "-")
   * @param property		the Java Beans property name
   * @param defValue		the default value
   * @param lower		the lower bound
   * @param upper		the upper bound
   * @return			the generated option object
   */
  public AbstractOption insert(int index, String commandline, String property, Object defValue, Number lower, Number upper) {
    AbstractOption	result;
    PropertyDescriptor	descriptor;
    Class		baseclass;

    OptionUtils.registerCustomHooks();

    // flag/property already defined?
    if (m_CommandlineIndex.containsKey(commandline))
      handleError(new IllegalArgumentException("Command-line flag '" + commandline + "' is already in use (" + getOwner().getClass().getName() + ")!"));
    if (m_PropertyIndex.containsKey(property))
      handleError(new IllegalArgumentException("Property '" + property + "' is already in use (" + getOwner().getClass().getName() + ")!"));

    // update index
    if (index == -1) {
      index = m_CommandlineIndex.size();
      m_CommandlineIndex.put(commandline, m_CommandlineIndex.size());
      m_PropertyIndex.put(property, m_PropertyIndex.size());
    }
    else if (index > -1) {
      for (String key: m_CommandlineIndex.keySet()) {
	if (m_CommandlineIndex.get(key) >= index)
	  m_CommandlineIndex.put(key, m_CommandlineIndex.get(key) + 1);
      }
      for (String key: m_PropertyIndex.keySet()) {
	if (m_PropertyIndex.get(key) >= index)
	  m_PropertyIndex.put(key, m_PropertyIndex.get(key) + 1);
      }
      m_CommandlineIndex.put(commandline, index);
      m_PropertyIndex.put(property, index);
    }
    else {
      handleError(new IllegalArgumentException("Invalid index for option '" + property + "' (" + getOwner().getClass().getName() + "): " + index));
    }

    baseclass = defValue.getClass();
    if (baseclass.isArray())
      baseclass = baseclass.getComponentType();

    // boolean option
    if ((baseclass == Boolean.TYPE) || (baseclass == Boolean.class)) {
      result = new BooleanOption(this, commandline, property, defValue);
      m_Options.add(index, result);
      return result;
    }

    // string option
    if (baseclass == String.class) {
      result = new StringOption(this, commandline, property, defValue);
      m_Options.add(index, result);
      return result;
    }

    // numeric options
    if ((baseclass == Integer.TYPE) || (baseclass == Integer.class)) {
      result = new IntegerOption(this, commandline, property, defValue, (Integer) lower, (Integer) upper);
      m_Options.add(index, result);
      return result;
    }
    if ((baseclass == Double.TYPE) || (baseclass == Double.class)) {
      result = new DoubleOption(this, commandline, property, defValue, (Double) lower, (Double) upper);
      m_Options.add(index, result);
      return result;
    }
    if ((baseclass == Long.TYPE) || (baseclass == Long.class)) {
      result = new LongOption(this, commandline, property, defValue, (Long) lower, (Long) upper);
      m_Options.add(index, result);
      return result;
    }
    if ((baseclass == Float.TYPE) || (baseclass == Float.class)) {
      result = new FloatOption(this, commandline, property, defValue, (Float) lower, (Float) upper);
      m_Options.add(index, result);
      return result;
    }
    if ((baseclass == Byte.TYPE) || (baseclass == Byte.class)) {
      result = new ByteOption(this, commandline, property, defValue, (Byte) lower, (Byte) upper);
      m_Options.add(index, result);
      return result;
    }
    if ((baseclass == Short.TYPE) || (baseclass == Short.class)) {
      result = new ShortOption(this, commandline, property, defValue, (Short) lower, (Short) upper);
      m_Options.add(index, result);
      return result;
    }

    try {
      descriptor = OptionUtils.getDescriptor(getOwner(), property);
      baseclass  = descriptor.getReadMethod().getReturnType();
      if (baseclass.isArray())
	baseclass = baseclass.getComponentType();

      // base object
      if (ClassLocator.isSubclass(BaseObject.class, baseclass)) {
	result = new BaseObjectOption(this, commandline, property, defValue);
	m_Options.add(index, result);
	return result;
      }

      // enums
      if (   ClassLocator.hasInterface(EnumWithCustomDisplay.class, baseclass)
	|| ClassLocator.isSubclass(Enum.class, baseclass)) {
	result = new EnumOption(this, commandline, property, defValue);
	m_Options.add(index, result);
	return result;
      }

      // custom hook
      if ((OptionUtils.getValueOfHook(baseclass) != null) || (OptionUtils.getToStringHook(baseclass) != null)) {
	result = new CustomHooksOption(this, commandline, property, defValue);
	m_Options.add(index, result);
	return result;
      }

      // class option (default)
      result = new ClassOption(this, commandline, property, defValue);
      m_Options.add(index, result);
      return result;
    }
    catch (Exception e) {
      handleError(new Exception("Failed to add " + commandline + "/" + property + " (" + getOwner() + ")!", e));
      return null;
    }
  }

  /**
   * Disables variable support for this bean property.
   *
   * @param property	the property to disable
   */
  public void disableVariables(String property) {
    if (m_NoVariablesProperties == null)
      m_NoVariablesProperties = new HashSet<>();
    m_NoVariablesProperties.add(property);
  }

  /**
   * Enables variable support for this bean property again.
   *
   * @param property	the property to enable
   */
  public void enabledVariables(String property) {
    if (m_NoVariablesProperties != null) {
      m_NoVariablesProperties.remove(property);
      if (m_NoVariablesProperties.isEmpty())
	m_NoVariablesProperties = null;
    }
  }

  /**
   * Checks whether this property allows variables.
   *
   * @param property	the bean property to check
   * @return		true if variables allowed
   */
  public boolean allowsVariables(String property) {
    return (m_NoVariablesProperties == null)
      || !m_NoVariablesProperties.contains(property);
  }

  /**
   * Returns the list of options.
   *
   * @return		the options
   */
  public List<AbstractOption> getOptionsList() {
    if (getOwner() instanceof PreGetOptionslistHook)
      ((PreGetOptionslistHook) getOwner()).preGetOptionsList();

    return m_Options;
  }

  /**
   * Returns the option for the commandline flag/property.
   *
   * @param flagOrProperty	the commandline flag/property string
   * @param flag		if true then "flagOrProperty" is interpreted
   * 				as flag instead of property
   * @return			the option or null if not found
   */
  protected AbstractOption findOption(String flagOrProperty, boolean flag) {
    AbstractOption	result;
    Integer		index;

    result = null;

    if (flag)
      index = m_CommandlineIndex.get(flagOrProperty);
    else
      index = m_PropertyIndex.get(flagOrProperty);

    if (index != null)
      result = m_Options.get(index);

    return result;
  }

  /**
   * Tries to locate the corresponding option for the given commandline string
   * (without the leading dash).
   *
   * @param flag	the commandline to look for (no leading dash)
   * @return		the option or null if not found
   * @see		AbstractOption#getCommandline()
   */
  public AbstractOption findByFlag(String flag) {
    return findOption(flag, true);
  }

  /**
   * Tries to locate the corresponding option for the given property name.
   *
   * @param property	the property name to look for
   * @return		the option or null if not found
   * @see		AbstractOption#getProperty(
   */
  public AbstractOption findByProperty(String property) {
    return findOption(property, false);
  }

  /**
   * Tries to locate the option for the given property name and then set the constraint.
   *
   * @param property	the property name to look for
   * @param constraint 	the constraint to set, null to remove any constraint
   * @return		true if successfully updated
   */
  public boolean setConstraintForProperty(String property, AbstractOptionConstraint constraint) {
    AbstractOption	opt;

    opt = findByProperty(property);

    if (opt != null) {
      opt.setConstraint(constraint);
      return true;
    }

    return false;
  }

  /**
   * Tries to locate the option for the given property name and then checks for any constraint.
   *
   * @param property	the property name to look for
   * @return		true if property located and constraint available
   */
  public boolean hasConstraintForProperty(String property) {
    AbstractOption	opt;

    opt = findByProperty(property);
    return (opt != null) && (opt.getConstraint() != null);
  }

  /**
   * Tries to locate the option for the given property name and then return any constraint.
   *
   * @param property	the property name to look for
   * @return		the constraint, null if none set or property not found
   */
  public AbstractOptionConstraint getConstraintForProperty(String property) {
    AbstractOption	opt;

    opt = findByProperty(property);

    if (opt != null)
      return opt.getConstraint();

    return null;
  }

  /**
   * Tries to locate the option for the given property name and then return any constraint.
   *
   * @param property	the property name to look for
   * @param type	the constraint type to cast to
   * @return		the constraint, null if none set or property not found
   */
  public <T extends AbstractOptionConstraint> T getConstraintForProperty(String property, Class<T> type) {
    AbstractOption	opt;

    opt = findByProperty(property);

    if (opt != null)
      return (T) opt.getConstraint();

    return null;
  }

  /**
   * Tries to locate the corresponding option for the given class.
   *
   * @param cls		the class to look for
   * @return		the option or null if not found
   * @see		AbstractOption#getReadMethod()
   */
  public AbstractOption findByClass(Class cls) {
    AbstractOption	result;

    result = null;

    for (AbstractOption option: m_Options) {
      if (option.getReadMethod().getReturnType() == cls) {
	result = option;
	break;
      }
    }

    return result;
  }

  /**
   * Removes the corresponding option associated with the given commandline string
   * (without the leading dash).
   *
   * @param flag	the commandline to look for (no leading dash)
   * @return		the option or null if not found
   * @see		AbstractOption#getCommandline()
   */
  public AbstractOption removeByFlag(String flag) {
    return removeOption(flag, true);
  }

  /**
   * Removes the corresponding option associated with the given property name.
   *
   * @param property	the property name to look for
   * @return		the option or null if not found
   * @see		AbstractOption#getProperty(
   */
  public AbstractOption removeByProperty(String property) {
    return removeOption(property, false);
  }

  /**
   * Removes the option associated with the commandline flag/property.
   *
   * @param flagOrProperty	the commandline flag/property string
   * @param isFlag		if true then "flagOrProperty" is interpreted
   * 				as flag instead of property
   * @return			the option or null if not found
   */
  protected AbstractOption removeOption(String flagOrProperty, boolean isFlag) {
    AbstractOption	result;
    Integer		index;
    String		removeKey;
    String 		flag;
    String		property;

    result = null;

    if (isFlag) {
      index    = m_CommandlineIndex.get(flagOrProperty);
      flag     = flagOrProperty;
      property = null;
    }
    else {
      index    = m_PropertyIndex.get(flagOrProperty);
      flag     = null;
      property = flagOrProperty;
    }

    if (index != null) {
      // record removed flags/properties
      if (flag == null)
	flag = m_Options.get(index).getCommandline();
      if (m_RemovedFlags == null)
	m_RemovedFlags = new HashSet<>();
      m_RemovedFlags.add(flag);
      if (property == null)
	property = m_Options.get(index).getProperty();
      if (m_RemovedProperties == null)
	m_RemovedProperties = new HashSet<>();
      m_RemovedProperties.add(property);

      result = m_Options.get(index);

      // remove option
      m_Options.remove(index.intValue());

      removeKey = null;
      for (String key: m_CommandlineIndex.keySet()) {
	if (m_CommandlineIndex.get(key).intValue() == index) {
	  removeKey = key;
	  break;
	}
      }
      if (removeKey != null)
	m_CommandlineIndex.remove(removeKey);

      removeKey = null;
      for (String key: m_PropertyIndex.keySet()) {
	if (m_PropertyIndex.get(key).intValue() == index) {
	  removeKey = key;
	  break;
	}
      }
      if (removeKey != null)
	m_PropertyIndex.remove(removeKey);

      // update indices
      for (String key: m_CommandlineIndex.keySet()) {
	if (m_CommandlineIndex.get(key) > index)
	  m_CommandlineIndex.put(key, m_CommandlineIndex.get(key) - 1);
      }
      for (String key: m_PropertyIndex.keySet()) {
	if (m_PropertyIndex.get(key) > index)
	  m_PropertyIndex.put(key, m_PropertyIndex.get(key) - 1);
      }
    }

    return result;
  }

  /**
   * Checks whether the flag has been removed intentionally.
   *
   * @param flag	the command-line flag to check
   * @return		true if removed
   */
  public boolean isRemovedFlag(String flag) {
    return (m_RemovedFlags != null) && m_RemovedFlags.contains(flag);
  }

  /**
   * Checks whether the property has been removed intentionally.
   *
   * @param property	the property to check
   * @return		true if removed
   */
  public boolean isRemovedProperty(String property) {
    return (m_RemovedProperties != null) && m_RemovedProperties.contains(property);
  }

  /**
   * Sets the variable in the option handler for the given property.
   *
   * @param property	the property to set the variable for
   * @param variable	the variable name, null removes the variable
   * @return		the option that the variable was set for, null if
   * 			property not found
   */
  public AbstractArgumentOption setVariableForProperty(String property, String variable) {
    AbstractArgumentOption	result;
    AbstractOption		option;
    AbstractArgumentOption	argOption;

    result = null;
    option = findOption(property, false);
    if (option instanceof AbstractArgumentOption) {
      argOption = (AbstractArgumentOption) option;
      if (argOption.getProperty().equals(property)) {
	result = argOption;
	argOption.setVariable(variable);
      }
    }

    return result;
  }

  /**
   * Returns the variable that is attached to a property.
   *
   * @param property	the property to check for a variable
   * @return		null if no variable attached, otherwise the variable name
   */
  public String getVariableForProperty(String property) {
    String			result;
    AbstractOption		option;
    AbstractArgumentOption	argOption;

    result = null;
    option = findOption(property, false);
    if (option instanceof AbstractArgumentOption) {
      argOption = (AbstractArgumentOption) option;
      if (argOption.getProperty().equals(property))
	result = argOption.getVariable();
    }

    return result;
  }

  /**
   * Returns whether there is a variable attached to the property.
   *
   * @param property	the property to check for a variable
   * @return		true if variable attached
   */
  public boolean hasVariableForProperty(String property) {
    return (getVariableForProperty(property) != null);
  }

  /**
   * If a variable is attached to the variable, it checks whether the variable
   * really exists in the context. If that shouldn't be the case, an error
   * message gets returned, otherwise null. If no variable is attached, then
   * null is being returned as well.
   *
   * @param property	the property to check for a variable
   * @return		null if no variable attached or variable exists, otherwise error message
   */
  public String ensureVariableForPropertyExists(String property) {
    String	result;
    String	var;

    result = null;
    var    = getVariableForProperty(property);
    if (var != null) {
      if (!getVariables().has(var))
	result = "Variable " + Variables.padName(var) + " attached to property '" + property + "' does not exist!";
    }

    return result;
  }

  /**
   * Checks whether the current value associated with the flag is the
   * default value.
   *
   * @param flag	the commandline to look for (no leading dash)
   * @return		true if option located, has an argument and is the default value
   */
  public boolean isDefaultValueByFlag(String flag) {
    AbstractOption 	option;

    option = findOption(flag, true);
    if (option != null) {
      if (option instanceof AbstractArgumentOption)
	return ((AbstractArgumentOption) option).isDefaultValue();
    }

    return false;
  }

  /**
   * Checks whether the current value associated with the property is the
   * default value.
   *
   * @param property	the property name to look for
   * @return		true if option located, has an argument and is the default value
   */
  public boolean isDefaultValueByProperty(String property) {
    AbstractOption 	option;

    option = findOption(property, false);
    if (option != null) {
      if (option instanceof AbstractArgumentOption)
	return ((AbstractArgumentOption) option).isDefaultValue();
    }

    return false;
  }

  /**
   * Sets the default values.
   */
  public void setDefaults() {
    int			i;
    AbstractOption	option;
    Method		method;

    for (i = 0; i < m_Options.size(); i++) {
      option = m_Options.get(i);
      method = option.getWriteMethod();
      try {
	method.invoke(option.getOptionHandler(), option.getDefaultValue());
      }
      catch (Exception e) {
	if (!m_Quiet)
	  System.err.println("Error setting default value for '" + m_Owner.getClass().getName() + "/" + option.getProperty() + "':");
	handleError(e);
      }
    }
  }

  /**
   * Updates the Variables instance recursively on all options.
   *
   * @param variables	the variables instance to use
   */
  public void updateVariablesInstance(final Variables variables) {
    traverse(new OptionTraverser() {
      private void update(AbstractOption option, OptionTraversalPath path) {
	if (option.getOwner().getVariables() != variables)
	  option.getOwner().setVariables(variables);
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	update(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	update(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	update(option, path);
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return !ClassLocator.hasInterface(VariablesHandler.class, cls) && !ClassLocator.hasInterface(Actor.class, cls);
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
    }, true);
  }

  /**
   * Updates the variables, i.e., in case an option uses a variable and this
   * variable's value has changed, the updated value will be set.
   *
   * @return		null if all variables were successfully updated
   */
  public String updateVariableValues() {
    return updateVariableValues(false);
  }

  /**
   * Updates the variables, i.e., in case an option uses a variable and this
   * variable's value has changed (or update is enforced), the updated value
   * will be set.
   *
   * @param forceUpdate	whether to force the update
   * @return		null if all variables were successfully updated
   */
  public String updateVariableValues(final boolean forceUpdate) {
    return updateVariableValues(forceUpdate, null);
  }

  /**
   * Updates the variables, i.e., in case an option uses a variable and this
   * variable's value has changed (or update is enforced), the updated value 
   * will be set.
   *
   * @param forceUpdate	whether to force the update
   * @param log		optional logger for logging traversal, can be null
   * @return		null if all variables were successfully updated
   */
  public String updateVariableValues(final boolean forceUpdate, final Logger log) {
    OptionTraverserWithResult<StringBuilder>	traverser;
    StringBuilder				result;

    traverser = new OptionTraverserWithResult<>() {
      private StringBuilder m_Result;
      public void resetResult() {
	m_Result = new StringBuilder();
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (    option.isVariableModified()
	  || (option.isVariableAttached() && forceUpdate)
	  || option.isVariableReferencingObject() ) {
	  String error = option.updateVariable(true, log);
	  if (error != null) {
	    if (m_Result.length() > 0)
	      m_Result.append("\n");
	    m_Result.append(option.getOptionHandler().getClass().getName()).append("/").append(option.getProperty()).append(": ").append(error);
	    if (log != null)
	      log.severe(path + "/" + option.getOptionHandler().getClass().getName() + "/" + option.getProperty() + "/" + getVariables().hashCode() + ":" + error);
	  }
	  else {
	    if (log != null)
	      log.info(path + "/" + option.getOptionHandler().getClass().getName() + "/" + option.getProperty() + "/" + getVariables().hashCode() + ": updated");
	  }
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return !ClassLocator.hasInterface(VariablesHandler.class, cls) && !isBlacklistedClass(cls);
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
      public StringBuilder getResult() {
	return m_Result;
      }
    };
    traverser.resetResult();
    traverse(traverser, true);

    result = traverser.getResult();
    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Registers all the variables recursively.
   */
  public void registerVariables() {
    traverse(new OptionTraverser() {
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (option.isVariableAttached())
	  getVariables().addVariableChangeListener(option);
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return !ClassLocator.hasInterface(VariablesHandler.class, cls) && !isBlacklistedClass(cls);
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
    }, true);
  }

  /**
   * Deregisters all the variables recursively.
   */
  public void deregisterVariables() {
    if (m_Variables == null)
      return;
    traverse(new OptionTraverser() {
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (option.isVariableAttached())
	  getVariables().removeVariableChangeListener(option);
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return !ClassLocator.hasInterface(VariablesHandler.class, cls) && !isBlacklistedClass(cls);
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
    }, true);
  }

  /**
   * Adds or removes the specified prefix from all variables.
   *
   * @param prefix		the prefix for the variables
   * @param add			whether to add or remove the prefixes
   */
  public void updateVariablePrefix(final String prefix, final boolean add) {
    traverse(new OptionTraverser() {
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	String vname;
	if (option.isVariableAttached()) {
	  vname = option.getVariableName();
	  if (add) {
	    if (!vname.startsWith(prefix))
	      vname = prefix + vname;
	  }
	  else {
	    if (!vname.startsWith(prefix))
	      vname = vname.substring(prefix.length());
	  }
	  option.setVariable(vname);
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return !ClassLocator.hasInterface(VariablesHandler.class, cls) && !isBlacklistedClass(cls);
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass()) && !isBlacklistedClass(obj.getClass());
      }
    }, true);
  }

  /**
   * Traverses all the options and lets the various options get handled by the
   * supplied object.
   *
   * @param traverser	the object to handled the traversed options
   */
  public void traverse(OptionTraverser traverser) {
    traverse(traverser, false);
  }

  /**
   * Traverses all the options and lets the various options get handled by the
   * supplied object.
   *
   * @param traverser	the object to handled the traversed options
   * @param nonAdams	whether to traverse non-ADAMS objects as well
   */
  public void traverse(OptionTraverser traverser, boolean nonAdams) {
    traverse(traverser, new OptionTraversalPath(), nonAdams);
  }

  /**
   * Traverses all the options and lets the various options get handled by the
   * supplied object.
   *
   * @param traverser	the object to handle the traversed options
   * @param path	the path so far
   * @param nonAdams	whether to traverse non-ADAMS objects as well
   */
  protected void traverse(OptionTraverser traverser, OptionTraversalPath path, boolean nonAdams) {
    List<AbstractOption>	options;
    Class[]			cls;
    ClassOption			cloption;
    int				n;
    Object			element;
    Object			current;
    boolean			isOptionHandler;

    options = getOptionsList();
    for (AbstractOption opt: options) {
      cloption = null;

      if (opt instanceof BooleanOption) {
	if (traverser.canHandle(opt))
	  traverser.handleBooleanOption((BooleanOption) opt, path);
      }
      else if (opt instanceof ClassOption) {
	cloption = (ClassOption) opt;
	if (traverser.canHandle(opt))
	  traverser.handleClassOption(cloption, path);
      }
      else {
	if (traverser.canHandle(opt))
	  traverser.handleArgumentOption((AbstractArgumentOption) opt, path);
      }

      if ((cloption != null) && traverser.canRecurse(cloption.getBaseClass()) && !isBlacklistedClass(cloption.getBaseClass())) {
	current = cloption.getCurrentValue();
	if (current == null)
	  continue;
	isOptionHandler = ClassLocator.hasInterface(OptionHandler.class, cloption.getBaseClass());
	if (!isOptionHandler) {
	  if (current.getClass().isArray()) {
	    cls = new Class[Array.getLength(current)];
	    for (n = 0; n < cls.length; n++)
	      cls[n] = Array.get(current, n).getClass();
	  }
	  else {
	    cls = new Class[]{current.getClass()};
	  }
	  for (n = 0; n < cls.length; n++) {
	    isOptionHandler = ClassLocator.hasInterface(OptionHandler.class, cls[n]);
	    if (isOptionHandler)
	      break;
	  }
	}
	if (isOptionHandler) {
	  if (cloption.isMultiple()) {
	    for (n = 0; n < Array.getLength(current); n++) {
	      element = Array.get(current, n);
	      if (traverser.canRecurse(element) && !isBlacklistedClass(element)) {
		path.push(cloption.getProperty() + "[" + n + "]", element);
		((OptionHandler) element).getOptionManager().traverse(traverser, path, nonAdams);
		path.pop();
	      }
	    }
	  }
	  else {
	    if (traverser.canRecurse(current) && !isBlacklistedClass(current)) {
	      path.push(cloption.getProperty(), current);
	      ((OptionHandler) current).getOptionManager().traverse(traverser, path, nonAdams);
	      path.pop();
	    }
	  }
	}
	else if (nonAdams) {
	  if (!isBlacklistedClass(current))
	    traverse(traverser, path, current);
	}
      }
    }
  }

  /**
   * Traverses a non-ADAMS OptionHandler.
   *
   * @param traverser	the object to handle the traversed options
   * @param path	the path so far
   * @param obj	the non-ADAMS object to traverse
   */
  protected void traverse(OptionTraverser traverser, OptionTraversalPath path, Object obj) {
    BeanInfo 			bi;
    PropertyDescriptor[]	props;
    Object			current;

    try {
      bi    = Introspector.getBeanInfo(obj.getClass());
      props = bi.getPropertyDescriptors();
      for (PropertyDescriptor prop: props) {
	if ((prop.getReadMethod() != null) && (prop.getWriteMethod() != null)) {
	  current = prop.getReadMethod().invoke(obj);
	  if (current != null) {
	    path.push(prop.getName(), current);
	    if (current instanceof OptionHandler)
	      ((OptionHandler) current).getOptionManager().traverse(traverser, path, true);
	    else if (!isBlacklistedClass(current))
	      traverse(traverser, path, current);
	    path.pop();
	  }
	}
      }
    }
    catch (IllegalAccessException iae) {
      // we notify about these starting with Java 17
      // https://www.infoq.com/news/2021/06/internals-encapsulated-jdk17/
      System.err.println("Attempted illegal access to " + obj.getClass().getName() + ", please add to blacklisted classes in " + FILENAME + "!");
    }
    catch (Exception e) {
      if (!m_Quiet)
	System.err.println("Failed to traverse non-ADAMS object: path=" + path + ", class=" + obj.getClass() + ", object=" + obj + ", exeption=" + e);
    }
  }

  /**
   * Returns the number of managed options.
   *
   * @return		the number of options
   */
  public int size() {
    return m_Options.size();
  }

  /**
   * Checks whether the value is valid, in case there is a constraint in place for the property.
   *
   * @param property	the property to check
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isValid(String property, Object value) {
    boolean			result;
    AbstractOption 		opt;

    result = true;

    opt = findByProperty(property);
    if (opt != null)
      result = opt.isValid(value);

    return result;
  }

  /**
   * Cleans up the options vector.
   */
  public void cleanUp() {
    int		i;

    for (i = 0; i < m_Options.size(); i++)
      m_Options.get(i).cleanUp();

    m_Options.clear();
    m_CommandlineIndex.clear();
    m_PropertyIndex.clear();
    if (m_NoVariablesProperties != null)
      m_NoVariablesProperties.clear();
    m_Variables = null;
  }

  /**
   * Returns the properties file with the custom editors.
   *
   * @return		the props file
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(OptionManagerDefinition.KEY);

    return m_Properties;
  }

  /**
   * Checks whether the object's class is blacklisted.
   *
   * @param obj		the object to check
   * @return		true if blacklisted
   */
  protected static boolean isBlacklistedClass(Object obj) {
    return (obj != null) && isBlacklistedClass(obj.getClass().getName());
  }

  /**
   * Checks whether the class is blacklisted.
   *
   * @param cls		the class to check
   * @return		true if blacklisted
   */
  protected static boolean isBlacklistedClass(Class cls) {
    return (cls != null) && isBlacklistedClass(cls.getName());
  }

  /**
   * Checks whether the class is blacklisted.
   *
   * @param cls		the class to check
   * @return		true if blacklisted
   */
  protected static synchronized boolean isBlacklistedClass(String cls) {
    String[]		items;

    if ((cls == null) || cls.isEmpty())
      return false;

    if (m_BlacklistedClasses == null) {
      items = getProperties().getProperty("BlacklistedClasses", "").replace(" ", "").split(",");
      m_BlacklistedClasses = new HashSet<>(Arrays.asList(items));
    }

    return m_BlacklistedClasses.contains(cls);
  }
}
