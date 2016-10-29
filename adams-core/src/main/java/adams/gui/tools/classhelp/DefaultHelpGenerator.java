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
 * DefaultHelpGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.classhelp;

import adams.gui.core.ConsolePanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Just looks for the globalInfo method.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultHelpGenerator
  extends AbstractHelpGenerator {

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param cls		the class to generate the help for
   * @return		true if HTML
   */
  @Override
  public boolean isHtml(Class cls) {
    return false;
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generateHelp(Class cls) {
    StringBuilder	result;
    Object		obj;
    Method 		meth;
    String		info;
    Constructor<?>[]	constructors;
    Field[]		fields;
    Method[]		methods;
    Class<?>[]		classes;

    result = new StringBuilder();

    try {
      obj = cls.newInstance();
      try {
        meth = cls.getMethod("globalInfo");
        info = (String) meth.invoke(obj);
	result.append("DESCRIPTION\n");
	result.append(info);
	result.append("\n");
      }
      catch (Exception ex2) {
	// ignored
      }

      constructors = cls.getConstructors();
      if (constructors.length > 0) {
	result.append("\n");
	result.append("CONSTRUCTORS\n");
	for (Constructor cons : constructors) {
	  result.append(cons.toGenericString());
	  result.append("\n");
	}
      }

      methods = cls.getDeclaredMethods();
      if (methods.length > 0) {
	result.append("\n");
	result.append("METHODS\n");
	for (Method method : methods) {
	  result.append(method.toGenericString());
	  result.append("\n");
	}
      }

      fields = cls.getDeclaredFields();
      if (fields.length > 0) {
	result.append("\n");
	result.append("FIELDS\n");
	for (Field field : fields) {
	  result.append(field.toGenericString());
	  result.append("\n");
	}
      }

      classes = cls.getDeclaredClasses();
      if (classes.length > 0) {
	result.append("\n");
	result.append("CLASSES\n");
	for (Class c : classes) {
	  result.append(c.toGenericString());
	  result.append("\n");
	}
      }
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to instantiate class: " + cls.getName(), ex);
      return null;
    }

    return result.toString().trim();
  }
}
