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
 * IntrospectionHelper.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.core.option.UserMode;
import adams.gui.goe.Editors;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for introspection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IntrospectionHelper {

  /**
   * Simple container object for storing introspection information.
   */
  public static class IntrospectionContainer {
    /** the options, if any. */
    public AbstractOption[] options;

    /** the property descriptors. */
    public PropertyDescriptor[] properties;

    /** the method descriptors. */
    public MethodDescriptor[] methods;
  }

  /** the cache for "has property" checks. */
  protected static Map<String,Boolean> PROPERTY_CHECK_CACHE = new HashMap<>();

  /**
   * Introspects the specified object. Uses the blacklist.
   *
   * @param obj		the object to introspect
   * @param userMode	the user mode to use
   * @return		the information gathered
   * @throws Exception	if introspection fails
   */
  public static IntrospectionContainer introspect(Object obj, UserMode userMode) throws Exception {
    return introspect(obj, true, userMode);
  }

  /**
   * Introspects the specified class. Uses the blacklist.
   *
   * @param cls		the class to introspect
   * @param userMode	the user mode to use
   * @return		the information gathered
   * @throws Exception	if introspection fails
   */
  public static IntrospectionContainer introspect(Class cls, UserMode userMode) throws Exception {
    return introspect(cls, true, userMode);
  }

  /**
   * Introspects the specified object.
   *
   * @param obj			the object to introspect
   * @param useBlacklist	whether to apply the GOE blacklist
   * @param userMode		the user mode to use
   * @return			the information gathered
   * @throws Exception		if introspection fails
   */
  public static IntrospectionContainer introspect(Object obj, boolean useBlacklist, UserMode userMode) throws Exception {
    IntrospectionContainer	result;
    BeanInfo 			bi;
    List<AbstractOption> 	optionsTmp;
    List<AbstractOption> 	options;
    PropertyDescriptor[] 	properties;
    List<PropertyDescriptor> 	propdesc;
    int 			i;
    AbstractArgumentOption 	opt;

    // in case of OptionHandlers we only display the properties that are
    // accessible via commandline options!
    if (obj instanceof OptionHandler) {
      bi         = Introspector.getBeanInfo(obj.getClass());
      optionsTmp = ((OptionHandler) obj).getOptionManager().getOptionsList();
      options    = new ArrayList<>();
      propdesc   = new ArrayList<>();
      for (i = 0; i < optionsTmp.size(); i++) {
	if (optionsTmp.get(i) instanceof AbstractArgumentOption) {
	  if (useBlacklist) {
	    opt = (AbstractArgumentOption) optionsTmp.get(i);
	    if (Editors.isBlacklisted(opt.getBaseClass(), opt.isMultiple()))
	      continue;
	    if (Editors.isBlacklisted(obj.getClass(), opt.getProperty()))
	      continue;
	    if (!UserMode.isAtLeast(userMode, opt.getMinUserMode()))
	      continue;
	  }
	}
	propdesc.add(optionsTmp.get(i).getDescriptor());
	options.add(optionsTmp.get(i));
      }
      properties = propdesc.toArray(new PropertyDescriptor[0]);

      // assemble result
      result            = new IntrospectionContainer();
      result.options    = options.toArray(new AbstractOption[0]);
      result.properties = properties;
      result.methods    = bi.getMethodDescriptors();

      return result;
    }
    else {
      return introspect(obj.getClass(), userMode);
    }
  }

  /**
   * Introspects the specified class.
   *
   * @param cls			the class to introspect
   * @param useBlacklist	whether to apply the GOE blacklist
   * @return			the information gathered
   * @throws Exception		if introspection fails
   */
  public static IntrospectionContainer introspect(Class cls, boolean useBlacklist, UserMode userMode) throws Exception{
    IntrospectionContainer	result;
    BeanInfo 			bi;
    PropertyDescriptor[] 	properties;
    List<PropertyDescriptor> 	propdesc;
    Class 			cl;
    Method			method;

    bi         = Introspector.getBeanInfo(cls);
    properties = bi.getPropertyDescriptors();
    propdesc   = new ArrayList<>();
    for (PropertyDescriptor desc: properties) {
      if ((desc == null) || (desc.getReadMethod() == null) || (desc.getWriteMethod() == null))
	continue;

      // deprecated?
      method = desc.getReadMethod();
      if (method.getAnnotation(Deprecated.class) != null)
	continue;
      method = desc.getWriteMethod();
      if (method.getAnnotation(Deprecated.class) != null)
	continue;

      // blacklisted?
      cl = desc.getReadMethod().getReturnType();
      if (useBlacklist) {
	if (Editors.isBlacklisted(cl, cl.isArray()))
	  continue;
	if (Editors.isBlacklisted(cls, desc.getDisplayName()))
	  continue;
      }
      propdesc.add(desc);
    }
    properties = propdesc.toArray(new PropertyDescriptor[0]);

    // assemble result
    result            = new IntrospectionContainer();
    result.properties = properties;
    result.methods    = bi.getMethodDescriptors();

    return result;
  }

  /**
   * Checks whether the class has a property with the given name and type.
   *
   * @param cls		the class to inspect
   * @param name	the name of the property to look for
   * @param type 	the type that the property must have, can be null
   * @param userMode 	the user mode to apply
   * @return		true if the class has that property
   */
  public static boolean hasProperty(Class cls, String name, Class type, UserMode userMode) {
    return hasProperty(cls, name, type, true, userMode);
  }

  /**
   * Checks whether the class has a property with the given name and type.
   *
   * @param cls		the class to inspect
   * @param name	the name of the property to look for
   * @param type 	the type that the property must have, can be null
   * @param useBlacklist	whether to apply the blacklist
   * @param userMode 	the user mode to apply
   * @return		true if the class has that property
   */
  public static boolean hasProperty(Class cls, String name, Class type, boolean useBlacklist, UserMode userMode) {
    Boolean			result;
    String			key;
    IntrospectionContainer	cont;

    key    = cls.getName() + "-" + name + "-" + (type == null ? "*" : type.getName());
    result = PROPERTY_CHECK_CACHE.get(key);
    if (result == null) {
      try {
	cont = introspect(cls, useBlacklist, userMode);
	for (PropertyDescriptor desc: cont.properties) {
	  if (desc.getName().equals(name)) {
	    if ((type == null) || (desc.getPropertyType().equals(type)))
	      result = true;
	  }
	}
      }
      catch (Exception e) {
	System.err.println("Failed to introspect: " + cls);
      }
      if (result == null)
	result = false;
      PROPERTY_CHECK_CACHE.put(key, result);
    }

    return result;
  }
}
