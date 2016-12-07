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
 * WekaEditorsRegistration.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;


import adams.core.ClassLister;
import weka.core.PluginManager;
import weka.gui.GenericPropertiesCreator;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

/**
 * Registers first the WEKA GenericObjectEditor editors and the ADAMS ones.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEditorsRegistration
  extends AbstractEditorRegistration {

  /** for serialization. */
  private static final long serialVersionUID = -2908979337117222215L;

  /** property indicating whether to use the Weka editors instead of the Adams ones. */
  public final static String PROPERTY_WEKAEDITORS = "adams.gui.wekaeditors";

  /** the additional class hierarchies. */
  public final static String ADDITIONAL_HIERARCHIES = "weka/gui/GenericPropertiesCreator.additional";

  /** the additional editors. */
  public final static String ADDITIONAL_EDITORS = "weka/gui/GUIEditors.additional";

  /** whether to use the Weka editors. */
  protected static boolean m_UseWekaEditors = Boolean.getBoolean(PROPERTY_WEKAEDITORS);

  /** whether registration already occurred. */
  protected static boolean m_Registered;

  /**
   * Subclass of {@link weka.gui.GenericObjectEditor} to get access to the
   * class hierarchies.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class AccessibleGenericObjectEditor
    extends weka.gui.GenericObjectEditor {

    /**
     * Returns the editor properties.
     *
     * @return		the properties.
     */
    public static Properties getProperties() {
      return EDITOR_PROPERTIES;
    }

    /**
     * registers the editors from the props file.
     */
    public static void registerEditors(Properties props) {
      Enumeration		enm;
      Object		key;
      String		name;
      String		value;

      enm = props.propertyNames();
      while (enm.hasMoreElements()) {
	name = enm.nextElement().toString();
	value = props.getProperty(name, "");

	registerEditor(name, value);
      }
    }
  }

  /**
   * Returns whether registration already occurred.
   *
   * @return		true if registration already occurred
   */
  protected boolean hasRegistered() {
    return m_Registered;
  }

  /**
   * Reregisters class hierarchies with ADAMS object editors.
   *
   * @param props	the Weka class hierarchies
   */
  protected void registerEditors(Properties props) {
    Class		cls;
    PropertyEditor	editor;
    Class		newEditor;

    for (Object key: props.keySet()) {
      try {
	// skip arrays
	if (key.toString().endsWith("[]"))
	  continue;

	cls       = Class.forName("" + key);
	editor    = PropertyEditorManager.findEditor(cls);
	newEditor = null;

	// find replacement
	if (editor instanceof weka.gui.GenericObjectEditor)
	  newEditor = GenericObjectEditor.class;
	else if (editor instanceof weka.gui.FileEditor)
	  newEditor = FileEditor.class;
	else if (editor instanceof weka.gui.ColorEditor)
	  newEditor = ColorEditor.class;

	// register new editor
	if (newEditor != null) {
	  Editors.registerCustomEditor(cls, newEditor);
	  getLogger().info(
	    "Registering " + cls.getName() + ": "
	      + editor.getClass().getName() + " -> " + newEditor.getName());
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to register editors: " + key, e);
      }
    }
  }

  /**
   * Registers the class hierarchies with ADAMS.
   *
   * @param props	the Weka class hierarchies
   */
  protected void registerHierarchies(Properties props) {
    String	superclass;
    String[]	classes;
    Class	cls;
    Set<String> packages;

    for (Object key: props.keySet()) {
      superclass = "" + key;
      classes    = props.getProperty(superclass).replaceAll(" ", "").split(",");
      packages   = new HashSet<>();
      for (String clsname: classes) {
	if (clsname.trim().isEmpty())
	  continue;
	try {
	  cls = Class.forName(clsname);
	  packages.add(cls.getPackage().getName());
	}
	catch (ClassNotFoundException e) {
	  getLogger().warning("Class not found: " + clsname);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to register class hierarchy: " + key, e);
	}
      }
      if (packages.size() > 0) {
	ClassLister.getSingleton().addHierarchy(superclass, packages.toArray(new String[packages.size()]));
	getLogger().info("Registering class hierarchy: " + key);
      }
    }
  }

  /**
   * Returns whether Weka editors should be used.
   *
   * @return		true if to use Weka editors
   */
  public static boolean useWekaEditors() {
    return m_UseWekaEditors;
  }

  /**
   * Performs the registration of the editors.
   *
   * @return		true if registration successful
   */
  protected boolean doRegister() {
    adams.core.Properties  	props;
    GenericPropertiesCreator 	creator;

    weka.gui.GenericObjectEditor.determineClasses();
    weka.gui.GenericObjectEditor.registerEditors();

    // additional class hierarchies
    try {
      creator = new GenericPropertiesCreator(ADDITIONAL_HIERARCHIES);
      creator.execute(false, true);
      PluginManager.addFromProperties(creator.getOutputProperties());
    }
    catch (Exception e) {
      System.err.println();
      e.printStackTrace();
      creator = null;
    }

    // additional editors
    try {
      props = adams.core.Properties.read(ADDITIONAL_EDITORS);
    }
    catch (Exception e) {
      props = new adams.core.Properties();
    }
    registerEditors(props);

    if (!useWekaEditors())
      registerEditors(AccessibleGenericObjectEditor.getProperties());
    registerHierarchies(AccessibleGenericObjectEditor.getProperties());
    if (creator != null)
      registerHierarchies(creator.getOutputProperties());
    m_Registered = true;
    return true;
  }
}
