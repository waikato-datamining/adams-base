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
 * Editors.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.env.Environment;
import adams.env.GOEBlacklistDefinition;
import adams.env.GOEEditorsDefinition;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Registers all the editors for the GenericObjectEditor/GenericArrayEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Editors {

  /** the name of the props file. */
  public final static String FILENAME = "Editors.props";

  /** the name of the props file with the blacklisted classes. */
  public final static String BLACKLIST = "Editors.blacklist";

  /** the blacklisted classes. */
  protected static Properties m_BlacklistedClasses;

  /**
   * Generates the list of editors to register, based on the classes listed in
   * the ClassLister.props file.
   *
   * @return		the generated props file
   */
  protected static Properties getAvailableEditors() {
    Properties			result;
    Properties			packages;
    List<String>		enums;
    String			goeEditor;
    String			arrEditor;
    String			enmEditor;
    Enumeration<String>		enm;
    String			classname;
    String[]			classnames;
    String			pkg;
    HashSet<String>		uniquePackages;
    Iterator<String>		iter;
    int				i;

    result  = new Properties();

    // determine the editors
    goeEditor = GenericObjectEditor.class.getName();
    arrEditor = GenericArrayEditor.class.getName();
    enmEditor = EnumEditor.class.getName();

    // general
    result.setProperty(Object.class.getName() + "[]", arrEditor);

    // superclasses
    for (String superclass: ClassLister.getSingleton().getSuperclasses()) {
      result.setProperty(superclass, goeEditor);
      result.setProperty(superclass + "[]", arrEditor);
    }

    // enums
    // 1. generate package list to search for enums
    uniquePackages = new HashSet<>();
    packages       = new Properties(ClassLister.getSingleton().getPackages());
    enm            = (Enumeration<String>) packages.propertyNames();
    while (enm.hasMoreElements()) {
      classname  = enm.nextElement();
      classnames = ClassLister.getSingleton().getPackages(classname);
      for (i = 0; i < classnames.length; i++)
        uniquePackages.add(classnames[i]);
    }

    // 2. search for enums
    iter = uniquePackages.iterator();
    while (iter.hasNext()) {
      pkg   = iter.next();
      enums = ClassLocator.getSingleton().findNamesInPackage(Enum.class, pkg);
      for (String cname: enums) {
	if (!result.hasKey(cname)) {
          result.setProperty(cname, enmEditor);
          result.setProperty(cname + "[]", arrEditor);
        }
      }
    }

    return result;
  }

  /**
   * registers all the editors.
   */
  public static void registerEditors() {
    Properties 		props;
    Enumeration 	enm;
    String 		name;
    String 		value;
    Class 		baseCls;
    Class		cls;

    // generate list of editors
    props = getAvailableEditors();

    // register all editors
    enm = props.propertyNames();
    while (enm.hasMoreElements()) {
      name  = enm.nextElement().toString();
      value = props.getProperty(name, "");
      try {
        // array class?
        if (name.endsWith("[]")) {
          baseCls = Class.forName(name.substring(0, name.indexOf("[]")));
          cls = Array.newInstance(baseCls, 1).getClass();
        }
        else {
          cls = Class.forName(name);
        }
        // register
        PropertyEditorManager.registerEditor(cls, Class.forName(value));
      }
      catch (Exception e) {
        System.err.println("Problem registering " + name + "/" + value + ": " + e);
      }
    }

    // register the custom editors
    Editors.registerCustomEditors();

    // register editors for basic Java types
    Editors.registerBasicEditors();
  }

  /**
   * Returns the properties storing the blacklisted classes.
   *
   * @return		the blacklisted class properties
   */
  protected static synchronized Properties getBlacklistedClasses() {
    if (m_BlacklistedClasses == null)
      m_BlacklistedClasses = Environment.getInstance().read(GOEBlacklistDefinition.KEY);
    return m_BlacklistedClasses;
  }

  /**
   * Checks whether the class has been blacklisted.
   * 
   * @param cls		the class to check
   * @param array	whether to look for an array definition
   * @return		true if the class is blacklisted
   */
  public static boolean isBlacklisted(Class cls, boolean array) {
    if (array)
      return getBlacklistedClasses().getBoolean(cls.getName(), false);
    else
      return getBlacklistedClasses().getBoolean(cls.getName() + "[]", false);
  }

  /**
   * Checks whether the property of the specified class has been blacklisted.
   * If the class itself is blacklisted, this will also return true.
   * 
   * @param cls		the class to check
   * @param property	the property to check
   * @return		true if the class's property is blacklisted
   */
  public static boolean isBlacklisted(Class cls, String property) {
    return 
	   getBlacklistedClasses().getBoolean(cls.getName() + "#" + property, false)
	|| isBlacklisted(cls, false);
  }
  
  /**
   * For registering editors for basic Java types.
   */
  protected static void registerBasicEditors() {
    try {
      // integer
      PropertyEditorManager.registerEditor(Byte.class, ByteEditor.class);
      PropertyEditorManager.registerEditor(Byte.TYPE, ByteEditor.class);
      PropertyEditorManager.registerEditor(Short.class, ShortEditor.class);
      PropertyEditorManager.registerEditor(Short.TYPE, ShortEditor.class);
      PropertyEditorManager.registerEditor(Integer.class, IntegerEditor.class);
      PropertyEditorManager.registerEditor(Integer.TYPE, IntegerEditor.class);

      // float
      PropertyEditorManager.registerEditor(Long.class, LongEditor.class);
      PropertyEditorManager.registerEditor(Long.TYPE, LongEditor.class);
      PropertyEditorManager.registerEditor(Float.class, FloatEditor.class);
      PropertyEditorManager.registerEditor(Float.TYPE, FloatEditor.class);
      PropertyEditorManager.registerEditor(Double.class, DoubleEditor.class);
      PropertyEditorManager.registerEditor(Double.TYPE, DoubleEditor.class);

      // others
      PropertyEditorManager.registerEditor(Boolean.class, BooleanEditor.class);
      PropertyEditorManager.registerEditor(Boolean.TYPE, BooleanEditor.class);
      PropertyEditorManager.registerEditor(String.class, StringEditor.class);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Registers a custom editor for a certain class. Automatically adds the
   * registering of the array editor.
   *
   * @param cls		the class to register the editor for
   * @param clsEditor	the custom editor
   * @return		true if successfully registered
   */
  public static boolean registerCustomEditor(Class cls, Class clsEditor) {
    boolean	result;

    try {
      PropertyEditorManager.registerEditor(cls, clsEditor);
      PropertyEditorManager.registerEditor(Array.newInstance(cls, 1).getClass(), GenericArrayEditor.class);
      result = true;
    }
    catch (Exception e) {
      result = false;
      System.err.println(
          "Registering editor " + clsEditor.getName()
          + " for class " + cls.getName() + " failed:\n" + e);
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Returns the properties file with the custom editors.
   *
   * @return		the props file
   */
  protected static Properties getCustomEditors() {
    return Environment.getInstance().read(GOEEditorsDefinition.KEY);
  }

  /**
   * Registers all custom editors and adds the hooks as well.
   */
  protected static void registerCustomEditors() {
    Properties 		props;
    Enumeration		enm;
    String		classname;
    String		classnameEditor;
    Class		cls;
    Class		clsEditor;

    props = getCustomEditors();
    enm   = props.propertyNames();
    while (enm.hasMoreElements()) {
      classname = (String) enm.nextElement();
      if (classname.indexOf("#") > -1)
        continue;
      classnameEditor = props.getProperty(classname);

      // obtain classes
      cls = null;
      try {
        if (classname.endsWith("[]")) {
          cls = Class.forName(classname.substring(0, classname.length() - 2));
          cls = Array.newInstance(cls, 0).getClass();
        }
        else {
          cls = Class.forName(classname);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        System.err.println("Cannot get class for '" + classname + "' - skipped!");
        continue;
      }

      clsEditor = null;
      try {
        clsEditor = Class.forName(classnameEditor);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.err.println("Cannot get class for editor '" + classnameEditor + "' - skipped!");
        continue;
      }

      // register editors
      registerCustomEditor(cls, clsEditor);
    }
  }
}
