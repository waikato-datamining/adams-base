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
 * PropertyHelper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.gui.goe.Editors;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for introspection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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

  /**
   * Introspects the specified object. Uses the blacklist.
   *
   * @param obj	the object to introspect
   * @return		the information gathered
   * @throws Exception	if introspection fails
   */
  public static IntrospectionContainer introspect(Object obj) throws Exception {
    return introspect(obj, true);
  }

  /**
   * Introspects the specified class. Uses the blacklist.
   *
   * @param cls		the class to introspect
   * @return		the information gathered
   * @throws Exception	if introspection fails
   */
  public static IntrospectionContainer introspect(Class cls) throws Exception {
    return introspect(cls, true);
  }

  /**
   * Introspects the specified object.
   *
   * @param obj			the object to introspect
   * @param useBlacklist	whether to apply the GOE blacklist
   * @return			the information gathered
   * @throws Exception		if introspection fails
   */
  public static IntrospectionContainer introspect(Object obj, boolean useBlacklist) throws Exception {
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
      options    = new ArrayList<AbstractOption>();
      propdesc   = new ArrayList<PropertyDescriptor>();
      for (i = 0; i < optionsTmp.size(); i++) {
	if (optionsTmp.get(i) instanceof AbstractArgumentOption) {
          if (useBlacklist) {
            opt = (AbstractArgumentOption) optionsTmp.get(i);
            if (Editors.isBlacklisted(opt.getBaseClass(), opt.isMultiple()))
              continue;
            if (Editors.isBlacklisted(obj.getClass(), opt.getProperty()))
              continue;
          }
	}
	propdesc.add(optionsTmp.get(i).getDescriptor());
	options.add(optionsTmp.get(i));
      }
      properties = propdesc.toArray(new PropertyDescriptor[propdesc.size()]);

      // assemble result
      result            = new IntrospectionContainer();
      result.options    = options.toArray(new AbstractOption[options.size()]);
      result.properties = properties;
      result.methods    = bi.getMethodDescriptors();

      return result;
    }
    else {
      return introspect(obj.getClass());
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
  public static IntrospectionContainer introspect(Class cls, boolean useBlacklist) throws Exception{
    IntrospectionContainer	result;
    BeanInfo 			bi;
    PropertyDescriptor[] 	properties;
    List<PropertyDescriptor> 	propdesc;
    Class 			cl;

    bi         = Introspector.getBeanInfo(cls);
    properties = bi.getPropertyDescriptors();
    propdesc   = new ArrayList<PropertyDescriptor>();
    for (PropertyDescriptor desc: properties) {
      if ((desc == null) || (desc.getReadMethod() == null) || (desc.getWriteMethod() == null))
        continue;
      cl = desc.getReadMethod().getReturnType();
      if (Editors.isBlacklisted(cl, cl.isArray()))
        continue;
      if (Editors.isBlacklisted(cls, desc.getDisplayName()))
        continue;
      propdesc.add(desc);
    }
    properties = propdesc.toArray(new PropertyDescriptor[propdesc.size()]);

    // assemble result
    result            = new IntrospectionContainer();
    result.properties = properties;
    result.methods    = bi.getMethodDescriptors();

    return result;
  }
}
