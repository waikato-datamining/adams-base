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
 * WekaOptionHandlerHelpGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.classhelp;

import adams.core.ClassLocator;
import adams.gui.core.ConsolePanel;
import weka.core.CapabilitiesHandler;
import weka.core.MultiInstanceCapabilitiesHandler;
import weka.core.OptionMetadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Help generator for {@link weka.core.OptionHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaOptionHandlerHelpGenerator
  extends AbstractHelpGenerator {

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(weka.core.OptionHandler.class, cls);
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
    StringBuilder 	result;
    Object		obj;
    BeanInfo 		bi;
    MethodDescriptor[] 	methods;
    Object[] 		args;
    boolean 		firstTip;
    StringBuilder 	options;
    String 		name;
    Method 		meth;
    OptionMetadata 	meta;
    String 		tempTip;
    String 		globalInfo;

    result = null;

    try {
      obj = cls.newInstance();
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to instantiate class: " + cls.getName(), ex);
      return null;
    }

    // get methods
    try {
      bi      = Introspector.getBeanInfo(cls);
      methods = bi.getMethodDescriptors();
    }
    catch (IntrospectionException ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Couldn't introspect class: " + cls.getName(), ex);
      return null;
    }

    // Look for a globalInfo method that returns a string
    // describing the target
    args     = new Object[]{};
    firstTip = true;
    options  = new StringBuilder();
    for (MethodDescriptor method : methods) {
      name = method.getDisplayName();
      meth = method.getMethod();
      meta = meth.getAnnotation(OptionMetadata.class);

      if (name.endsWith("TipText") || meta != null) {
	if (meth.getReturnType().equals(String.class) || meta != null) {
	  try {
	    tempTip = meta != null ? meta.description() : (String) (meth.invoke(obj, args));
	    name = meta != null ? meta.displayName() : name;

	    if (firstTip) {
	      options.append("OPTIONS\n");
	      firstTip = false;
	    }
	    tempTip = tempTip.replace("<html>", "").replace("</html>", "").replace("<br>", "\n").replace("<p>", "\n\n");
	    options.append(name.replace("TipText", "")).append(" -- ");
	    options.append(tempTip).append("\n\n");

	  }
	  catch (Exception ex) {
	    // ignored
	  }
	}
      }

      if (name.equals("globalInfo")) {
	if (meth.getReturnType().equals(String.class)) {
	  try {
	    globalInfo = (String) (meth.invoke(obj, args));
	    result = new StringBuilder("NAME\n");
	    result.append(cls.getName()).append("\n\n");
	    result.append("SYNOPSIS\n").append(globalInfo).append("\n\n");

	    if (obj instanceof CapabilitiesHandler) {
	      result.append(weka.gui.PropertySheetPanel.addCapabilities(
		"CAPABILITIES",
		((CapabilitiesHandler) obj).getCapabilities()));
	      if (obj instanceof MultiInstanceCapabilitiesHandler) {
		result.append(weka.gui.PropertySheetPanel.addCapabilities(
		  "MI CAPABILITIES",
		  ((MultiInstanceCapabilitiesHandler) obj).getMultiInstanceCapabilities()));
	      }
	    }
	  }
	  catch (Exception ex) {
	    // ignored
	  }
	}
      }
    }

    if (result != null)
      result.append(options.toString());

    if (result != null)
      return result.toString();
    else
      return null;
  }
}
