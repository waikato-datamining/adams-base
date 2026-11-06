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
 * WekaClassDescriptionGenerator.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.description;

import adams.core.EnumHelper;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.data.map.MapToPlainText;
import adams.gui.goe.CustomStringRepresentationHandler;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates descriptions for WEKA option handlers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassDescriptionGenerator
  extends AbstractClassDescriptionGenerator {

  private static final long serialVersionUID = 3280375826101888180L;

  /**
   * Checks whether this class is handled by this generator.
   *
   * @param cls the class to check
   * @return true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }

  /**
   * Turns the object into a string.
   *
   * @param descriptor	the current property descriptor
   * @param baseClass	the base class
   * @param obj		the object itself
   * @return		the string representation
   */
  protected String toString(PropertyDescriptor descriptor, Class baseClass, Object obj) {
    Class			pec;
    PropertyEditor		pe;

    // OptionHandler -> get commandline
    if (obj instanceof OptionHandler)
      return OptionUtils.getCommandLine(obj);

    // BaseObject -> toString()
    if (obj instanceof BaseObject)
      return obj.toString();

    // property editor with custom representation handling?
    pec = descriptor.getPropertyEditorClass();
    if (pec != null) {
      try {
	pe = (PropertyEditor) pec.getDeclaredConstructor().newInstance();
	if (pe instanceof CustomStringRepresentationHandler)
	  return ((CustomStringRepresentationHandler) pe).toCustomStringRepresentation(obj);
      }
      catch (Exception e) {
	// ignored
      }
    }

    // anything else -> toString()
    return "" + obj;
  }

  /**
   * Generates the description for the object.
   *
   * @param obj the object to process
   * @return the description, null if failed to generate
   */
  @Override
  public Map<String, Object> generate(Object obj) {
    Map<String, Object>		result;
    IntrospectionContainer	cont;
    Map<String,Object> 		optionInfo;
    Method			method;
    boolean			multiple;
    Object			defValue;
    Class			baseClass;
    boolean			isEnum;
    List<String> 		values;
    Object			selTag;
    int				i;

    result = new HashMap<>();
    result.put(KEY_CLASS, obj.getClass().getName());
    result.put(KEY_OPTIONS, new ArrayList<>());

    try {
      cont = IntrospectionHelper.introspect(obj, UserMode.HIGHEST);
      for (PropertyDescriptor descriptor: cont.properties) {
	// skip get/setOptions
	if (descriptor.getName().equals("options"))
	  continue;
	optionInfo = new HashMap<>();
	optionInfo.put("property", descriptor.getName());
	if (descriptor.getReadMethod().getReturnType().isArray())
	  baseClass = descriptor.getReadMethod().getReturnType().getComponentType();
	else
	  baseClass = descriptor.getReadMethod().getReturnType();
	optionInfo.put("type", baseClass);
	multiple = descriptor.getReadMethod().getReturnType().isArray();
	optionInfo.put("multiple", multiple);

	// enum
	isEnum = baseClass.isEnum();
	if (isEnum) {
	  values = new ArrayList<>();
	  for (Object val: EnumHelper.getValues(baseClass))
	    values.add(toString(descriptor, baseClass, val));
	  optionInfo.put("enum", values);
	}

	// default value
	defValue = descriptor.getReadMethod().invoke(obj);
	if (defValue == null) {
	  optionInfo.put("default", null);
	}
	else {
	  if (multiple) {
	    values = new ArrayList<>();
	    for (i = 0; i < Array.getLength(defValue); i++)
	      values.add(toString(descriptor, baseClass, Array.get(defValue, i)));
	    optionInfo.put("default", values);
	  }
	  else {
	    if ((defValue instanceof Double) && (Double.isNaN((Double) defValue)))
	      optionInfo.put("default", "NaN");
	    else
	      optionInfo.put("default", toString(descriptor, baseClass, defValue));
	  }
	}

	// SelectedTag
	if (ClassLocator.matches(SelectedTag.class, baseClass)) {
	  if (multiple)
	    selTag = Array.get(defValue, 0);
	  else
	    selTag = defValue;
	  if (selTag instanceof SelectedTag) {
	    values = new ArrayList<>();
	    for (Tag tag : ((SelectedTag) selTag).getTags())
	      values.add(tag.getIDStr());
	    optionInfo.put("enum", values);
	  }
	}

	// help
	try {
	  method = obj.getClass().getMethod(descriptor.getName() + "TipText");
	  optionInfo.put("help", method.invoke(obj));
	}
	catch (Exception ex) {
	  // ignored
	}

	// add option
	((List) result.get(KEY_OPTIONS)).add(optionInfo);
      }
    }
    catch (Exception e) {
      LoggingHelper.global().severe("Failed to compile class description for: " + Utils.classToString(obj), e);
      return null;
    }

    return result;
  }

  public static void main(String[] args) throws Exception {
    WekaClassDescriptionGenerator generator = new WekaClassDescriptionGenerator();
    Map<String,Object> map = generator.generate(new FilteredClassifier());
    System.out.println(new MapToPlainText().mapToString(map));
  }
}
