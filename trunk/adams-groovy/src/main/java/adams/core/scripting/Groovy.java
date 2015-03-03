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
 * Groovy.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingObject;
import adams.env.Environment;
import adams.flow.core.AdditionalOptionsHandler;
import adams.flow.core.AdditionalOptionsHandlerUtils;

/**
 * A helper class for <a href="http://groovy.codehaus.org/" target="_blank">Groovy</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Groovy
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2628766602043134673L;

  /** the classname of the Groovy classloader. */
  public final static String CLASS_GROOVYCLASSLOADER = "groovy.lang.GroovyClassLoader";

  /** whether the Groovy classes are in the Classpath. */
  protected boolean m_Present;

  /** the classloader. */
  protected Object m_ClassLoader;

  /** the singleton. */
  protected static Groovy m_Singleton;
  
  /**
   * default constructor, tries to instantiate a Groovy classloader.
   */
  protected Groovy() {
    try {
      Class.forName(CLASS_GROOVYCLASSLOADER);
      m_Present = true;
    }
    catch (Exception e) {
      m_Present = false;
    }
    m_ClassLoader = newClassLoader();
  }

  /**
   * returns the currently used Groovy classloader.
   *
   * @return		the classloader, can be null
   */
  public Object getClassLoader() {
    return m_ClassLoader;
  }

  /**
   * executes the specified method on the current interpreter and returns the
   * result, if any.
   *
   * @param methodName		the name of the method
   * @param paramClasses	the classes of the parameters
   * @param paramValues		the values of the parameters
   * @return			the return value of the method, if any (in that case null)
   */
  public Object invoke(String methodName, Class[] paramClasses, Object[] paramValues) {
    Object	result;

    result = null;
    if (getClassLoader() != null)
      result = invoke(getClassLoader(), methodName, paramClasses, paramValues);

    return result;
  }

  /**
   * returns whether the Groovy classes are present or not, i.e. whether the
   * classes are in the classpath or not
   *
   * @return 			whether the Groovy classes are available
   */
  public boolean isPresent() {
    return m_Present;
  }

  /**
   * initializes and returns a Groovy Interpreter.
   *
   * @return			the interpreter or null if Groovy classes not present
   */
  public Object newClassLoader() {
    Object	result;
    Class<?>	cls;
    Constructor	constr;

    result = null;

    if (isPresent()) {
      try {
	cls    = Class.forName(CLASS_GROOVYCLASSLOADER);
	constr = cls.getConstructor(new Class[]{ClassLoader.class});
	result = constr.newInstance(Groovy.class.getClassLoader());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate new classloader!", e);
	result = null;
      }
    }

    return result;
  }

  /**
   * loads the module and returns a new instance of it as instance of the
   * provided Java class template.
   *
   * @param file		the Groovy module file
   * @param template		the template for the returned Java object
   * @return			the Groovy object
   */
  public Object newInstance(File file, Class template) {
    Object 	result;
    Object	interpreter;
    Class	cls;

    result = null;

    if (!isPresent())
      return result;

    interpreter = newClassLoader();
    if (interpreter == null)
      return result;

    try {
      cls    = (Class) invoke(interpreter, "parseClass", new Class[]{File.class}, new Object[]{file.getAbsoluteFile()});
      result = cls.newInstance();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to instantiate script from '" + file + "' as '" + template.getName() + "'!", e);
    }

    return result;
  }

  /**
   * executes the specified method and returns the result, if any.
   *
   * @param o			the object the method should be called from,
   * 				e.g., a Groovy Interpreter
   * @param methodName		the name of the method
   * @param paramClasses	the classes of the parameters
   * @param paramValues		the values of the parameters
   * @return			the return value of the method, if any (in that case null)
   */
  public Object invoke(Object o, String methodName, Class[] paramClasses, Object[] paramValues) {
    Method      m;
    Object      result;

    result = null;

    try {
      m      = o.getClass().getMethod(methodName, paramClasses);
      result = m.invoke(o, paramValues);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to invoke method '" + methodName + "' (" + Utils.arrayToString(paramClasses) + " with " + Utils.arrayToString(paramValues) + ")!", e);
      result = null;
    }

    return result;
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @param cls			the class to instantiate
   * @param scriptFile		the external file to load
   * @param inlineScript	the inline script to load if external file points to a directory
   * @param scriptOptions	the options to set
   * @param vars		the variables to use for expanding
   * @return			element 0: error messsage (null if ok), element 1: script object
   */
  public Object[] loadScriptObject(Class cls, PlaceholderFile scriptFile, GroovyScript inlineScript, String scriptOptions, Variables vars) {
    Object[]		result;
    PlaceholderFile	file;

    result = new Object[2];

    file = null;
    if (scriptFile.isDirectory()) {
      if (inlineScript.getValue().trim().length() == 0) {
	result[0] = "Neither script file nor inline script provided!";
      }
      else {
	try {
	  file = new PlaceholderFile(File.createTempFile(Environment.getInstance().getProject() + "-", ".groovy"));
	  FileUtils.saveToFile(inlineScript.getValue().split("\n"), file);
	}
	catch (Exception e) {
	  result[0] = "Failed to save inline script to temporary file: " + Utils.throwableToString(e);
	  getLogger().log(Level.SEVERE, "Failed to save inline script to temporary file!", e);
	}
      }
    }
    else {
      file = scriptFile;
    }
    
    if (result[0] == null) {
      try {
	if (file.isFile()) {
	  result[1] = newInstance(file, cls);
	  if (result[1] != null) {
	    if (!AdditionalOptionsHandlerUtils.setOptions(result[1], scriptOptions, vars))
	      result[0] = "Does not implement '" + AdditionalOptionsHandler.class.getName() + "': " + file;
	  }
	  else {
	    result[0] = "Failed to instantiate script '" + file + "'!";
	  }
	}
	else {
	  result[0] = "No script provided!";
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to initialize Groovy script '" + file + "'!", e);
	result[0] = "Failed to initialize Groovy script '" + file + "': " + e.toString();
      }
    }

    return result;
  }

  /**
   * Returns the singleton instance.
   * 
   * @return		the singleton
   */
  public static synchronized Groovy getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Groovy();
    return m_Singleton;
  }
  
  /**
   * If no arguments are given, it just prints the presence of the Groovy
   * classes, otherwise it expects a Groovy filename to execute.
   *
   * @param args		commandline arguments
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    if (args.length == 0) {
      System.out.println("Groovy present: " + getSingleton().isPresent());
    }
    else {
      if (getSingleton().getClassLoader() == null) {
	System.err.println("Cannot instantiate Groovy ClassLoader!");
      }
      else {
	Object groovyObject = getSingleton().newInstance(new File(args[0]), Object.class);
	if (groovyObject == null)
	  System.err.println("Failed to instantiate script: " + args[0]);
	else
	  System.out.println("Successfully instantiated script: " + args[0]);
      }
    }
  }
}
