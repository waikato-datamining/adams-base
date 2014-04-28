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
 * Jython.java
 * Copyright (C) 2007-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
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
 * A helper class for <a href="http://www.jython.org/" target="_blank">Jython</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Jython
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -6972298704460209252L;

  /** the classname of the Python interpreter. */
  public final static String CLASS_PYTHONINERPRETER = "org.python.util.PythonInterpreter";

  /** the classname of the Python ObjectInputStream. */
  public final static String CLASS_PYTHONOBJECTINPUTSTREAM = "org.python.util.PythonObjectInputStream";

  /** whether the Jython classes are in the Classpath. */
  protected boolean m_Present;

  /** the interpreter. */
  protected Object m_Interpreter;

  /** the singleton. */
  protected static Jython m_Singleton;
  
  /**
   * default constructor, tries to instantiate a Python Interpreter.
   */
  protected Jython() {
    try {
      Class.forName(CLASS_PYTHONINERPRETER);
      m_Present = true;
    }
    catch (Exception e) {
      m_Present = false;
    }
    m_Interpreter = newInterpreter();
  }

  /**
   * returns the currently used Python Interpreter.
   *
   * @return		the interpreter, can be null
   */
  public Object getInterpreter() {
    return m_Interpreter;
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
    if (getInterpreter() != null)
      result = invoke(getInterpreter(), methodName, paramClasses, paramValues);

    return result;
  }

  /**
   * returns whether the Jython classes are present or not, i.e. whether the
   * classes are in the classpath or not
   *
   * @return 			whether the Jython classes are available
   */
  public boolean isPresent() {
    return m_Present;
  }

  /**
   * initializes and returns a Python Interpreter.
   *
   * @return			the interpreter or null if Jython classes not present
   */
  public Object newInterpreter() {
    Object	result;

    result = null;

    if (isPresent()) {
      try {
	result = Class.forName(CLASS_PYTHONINERPRETER).newInstance();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate new interpreter!", e);
	result = null;
      }
    }

    return result;
  }

  /**
   * loads the module and returns a new instance of it as instance of the
   * provided Java class template.
   *
   * @param file		the Jython module file
   * @param template		the template for the returned Java object
   * @return			the Jython object
   */
  public Object newInstance(File file, Class template) {
    return newInstance(file, template, new File[0]);
  }

  /**
   * loads the module and returns a new instance of it as instance of the
   * provided Java class template. The paths are added to 'sys.path' - can
   * be used if the module depends on other Jython modules.
   *
   * @param file		the Jython module file
   * @param template		the template for the returned Java object
   * @param paths		additional paths to add to "sys.path"
   * @return			the Jython object
   */
  public Object newInstance(File file, Class template, File[] paths) {
    Object 		result;
    String 		tempName;
    String 		instanceName;
    String 		javaClassName;
    String 		objectDef;
    int			i;
    String[]		tmpPaths;
    HashSet<String>	currentPaths;
    String		filename;
    Object		interpreter;

    result = null;

    if (!isPresent())
      return result;

    interpreter = newInterpreter();
    if (interpreter == null)
      return result;

    // add paths to sys.path
    if (paths.length > 0) {
      invoke(interpreter, "exec", new Class[]{String.class}, new Object[]{"import sys"});

      // determine currently set paths
      instanceName = "syspath";
      invoke(interpreter, "exec", new Class[]{String.class}, new Object[]{instanceName + " = sys.path"});
      currentPaths = new HashSet<String>();
      try {
	tmpPaths = (String[]) invoke(interpreter, "get", new Class[]{String.class, Class.class}, new Object[]{instanceName, String[].class});
	for (i = 0; i < tmpPaths.length; i++)
	  currentPaths.add(tmpPaths[i]);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to add additional paths!", e);
      }

      // add only new paths
      for (i = 0; i < paths.length; i++) {
	if (!currentPaths.contains(paths[i].getAbsolutePath()))
	  invoke(interpreter, "exec", new Class[]{String.class}, new Object[]{"sys.path.append('" + paths[i].getAbsolutePath() + "')"});
      }
    }

    // get object
    filename      = file.getAbsolutePath();
    invoke(interpreter, "execfile", new Class[]{String.class}, new Object[]{filename});
    tempName      = filename.substring(filename.lastIndexOf("/") + 1);
    tempName      = tempName.substring(0, tempName.indexOf("."));
    instanceName  = tempName.toLowerCase();
    javaClassName = tempName.substring(0,1).toUpperCase() + tempName.substring(1);
    objectDef     = "=" + javaClassName + "()";
    invoke(interpreter, "exec", new Class[]{String.class}, new Object[]{instanceName + objectDef});
    try {
      result = invoke(interpreter, "get", new Class[]{String.class, Class.class}, new Object[]{instanceName, template});
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to instantiate script '" + file + "' as '" + template.getName() + "'!", e);
      e.printStackTrace();
    }

    return result;
  }

  /**
   * executes the specified method and returns the result, if any.
   *
   * @param o			the object the method should be called from,
   * 				e.g., a Python Interpreter
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
   * deserializes the Python Object from the stream.
   *
   * @param in			the stream to use
   * @return			the deserialized object
   */
  public Object deserialize(InputStream in) {
    Class<?> 		cls;
    Class[] 		paramTypes;
    Constructor 	constr;
    Object[] 		arglist;
    Object 		obj;
    Object 		result;

    result = null;

    try {
      cls        = Class.forName(CLASS_PYTHONOBJECTINPUTSTREAM);
      paramTypes = new Class[]{InputStream.class};
      constr     = cls.getConstructor(paramTypes);
      arglist    = new Object[]{in};
      obj        = constr.newInstance(arglist);
      result     = invoke(obj, "readObject", new Class[]{}, new Object[]{});
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to deserialize stream!", e);
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
  public Object[] loadScriptObject(Class cls, PlaceholderFile scriptFile, JythonScript inlineScript, String scriptOptions, Variables vars) {
    Object[]		result;
    PlaceholderFile	file;
    String[]		lines;
    String		name;

    result = new Object[2];

    file = null;
    if (scriptFile.isDirectory()) {
      if (inlineScript.getValue().trim().length() == 0) {
	result[0] = "Neither script file nor inline script provided!";
      }
      else {
	try {
	  lines = inlineScript.getValue().split("\n");
	  name  = null;
	  for (String line: lines) {
	    if (line.matches("class [ ]*.*\\(.*")) {
	      name = line.replaceAll("class [ ]*", "").replaceAll("\\(.*", "");
	      break;
	    }
	  }
	  if (name == null) {
	    result[0] = "Failed to locate class name!";
	  }
	  else {
	    file = new PlaceholderFile("${TMP}/" + name + ".py");
	    FileUtils.saveToFile(lines, file);
	  }
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
	result[0] = "Failed to initialize Jython script '" + file + "': " + e.toString();
	getLogger().log(Level.SEVERE, "Failed to initialize Jython script '" + file + "'!", e);
      }
    }

    return result;
  }

  /**
   * Returns the singleton.
   * 
   * @return		the singleton
   */
  public static synchronized Jython getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Jython();
    return m_Singleton;
  }
  
  /**
   * If no arguments are given, it just prints the presence of the Jython
   * classes, otherwise it expects a Jython filename to execute.
   *
   * @param args		commandline arguments
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    if (args.length == 0) {
      System.out.println("Jython present: " + getSingleton().isPresent());
    }
    else {
      if (getSingleton().getInterpreter() == null) {
	System.err.println("Cannot instantiate Python Interpreter!");
      }
      else {
	Object jythonObject = getSingleton().newInstance(new File(args[0]), Object.class);
	if (jythonObject == null)
	  System.err.println("Failed to instantiate script: " + args[0]);
	else
	  System.out.println("Successfully instantiated script: " + args[0]);
      }
    }
  }
}
