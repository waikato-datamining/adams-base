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
 * DefaultHelpGenerator.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.help;

import adams.core.net.HtmlUtils;
import adams.gui.core.ConsolePanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Just looks for the globalInfo method.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
    return true;
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generate(Class cls) {
    StringBuilder	result;
    Object		obj;
    Method 		meth;
    String		info;
    Constructor<?>[]	constructors;
    Field[]		fields;
    Method[]		methods;
    Class<?>[]		classes;

    result = new StringBuilder();
    result.append("<html>\n");
    result.append("<body>\n");

    try {
      try {
	obj  = cls.newInstance();
        meth = cls.getMethod("globalInfo");
        info = (String) meth.invoke(obj);
	result.append("<h3>DESCRIPTION</h3>\n");
	info = HtmlUtils.markUpURLs(info, true);
	info = HtmlUtils.convertLines(info, true);
	result.append(info);
	result.append("\n");
      }
      catch (Exception ex2) {
	// ignored
      }

      constructors = cls.getConstructors();
      if (constructors.length > 0) {
	result.append("\n");
	result.append("<h3>CONSTRUCTORS</h3>\n");
	result.append("<ul>\n");
	for (Constructor cons : constructors) {
	  result.append("<li>");
	  result.append(cons.toGenericString());
	  result.append("</li>\n");
	}
	result.append("</ul>\n");
      }

      methods = cls.getDeclaredMethods();
      if (methods.length > 0) {
	result.append("\n");
	result.append("<h3>METHODS</h3>\n");
	result.append("<ul>\n");
	for (Method method : methods) {
	  result.append("<li>");
	  result.append(method.toGenericString());
	  result.append("</li>\n");
	}
	result.append("</ul>\n");
      }

      fields = cls.getDeclaredFields();
      if (fields.length > 0) {
	result.append("\n");
	result.append("<h3>FIELDS</h3>\n");
	result.append("<ul>\n");
	for (Field field : fields) {
	  result.append("<li>");
	  result.append(field.toGenericString());
	  result.append("</li>\n");
	}
	result.append("</ul>\n");
      }

      classes = cls.getDeclaredClasses();
      if (classes.length > 0) {
	result.append("\n");
	result.append("<h3>CLASSES</h3>\n");
	result.append("<ul>\n");
	for (Class c : classes) {
	  result.append("<li>");
	  result.append(c.toGenericString());
	  result.append("</li>\n");
	}
	result.append("</ul>\n");
      }
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to generate help: " + cls.getName(), ex);
      return null;
    }

    result.append("</body>\n");
    result.append("</html>\n");

    return result.toString().trim();
  }

  /**
   * Generates and returns the help for the specified object.
   *
   * @param obj		the object to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generate(Object obj) {
    return generate(obj.getClass());
  }
}
