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
 * OptionLister.java
 * Copyright (C) 2024-2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.option.description.AbstractClassDescriptionGenerator;
import adams.core.option.description.ClassDescriptionGenerator;
import adams.data.map.MapToJson;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Outputs the options (commandline, property, type, enum (optional), multiple, default, minUserMode,
 * lowerBound (only numeric/optional), upperBound (only numeric/optional), help (optional)) for
 * the classes in the ClassLister output props file it receives as input.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OptionLister {

  /** the classlister props file to process. */
  protected File m_ClassListerInput;
  
  /** the output JSON file to generate. */
  protected File m_Output;

  /**
   * Initializes the lister.
   */
  public OptionLister() {
    m_ClassListerInput = null;
    m_Output           = null;
  }

  /**
   * Sets the classlister props file to use as input.
   * 
   * @param value	the file to use
   */
  public void setClassListerInput(File value) {
    m_ClassListerInput = value;
  }

  /**
   * Returns the currently set classlister props file to use as input.
   * 
   * @return		the file to use, null if not set
   */
  public File getClassListerInput() {
    return m_ClassListerInput;
  }

  /**
   * Sets the file to store the JSON output in.
   *
   * @param value	the file to use
   */
  public void setOutput(File value) {
    m_Output = value;
  }

  /**
   * Returns the currently set file to store the JSON output in.
   *
   * @return		the file to use, null if not set
   */
  public File getOutput() {
    return m_Output;
  }

  /**
   * Collects the data for the specified class.
   *
   * @param cls			the class to process
   * @param baseClassesColl	option traverser instance to use for collecting concrete base classes, ignored if None
   * @param baseClasses		for collecting the concrete base classes
   * @param output		for collecting the data
   */
  protected void collect(Class cls, ConcreteBaseClassesOptionTraverser baseClassesColl, Set<Class> baseClasses, Map<String,Object> output) {
    Object				obj;
    AbstractClassDescriptionGenerator	generator;
    Map<String,Object>			desc;

    try {
      obj = cls.getDeclaredConstructor().newInstance();
      if (baseClassesColl != null) {
	if (obj instanceof OptionHandler) {
	  ((OptionHandler) obj).getOptionManager().traverse(baseClassesColl);
	  baseClasses.addAll(baseClassesColl.getResult());
	}
      }
    }
    catch (Exception e) {
      // no default constructor, so we'll ignore it
      return;
    }
    generator = AbstractClassDescriptionGenerator.getHandler(obj);
    if (generator != null) {
      desc = generator.generate(obj);
      if (desc != null)
	output.put(cls.getName(), desc.get(ClassDescriptionGenerator.KEY_OPTIONS));
    }

  }

  /**
   * Generates the JSON.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    Properties				props;
    Map<String,Object> 			output;
    String				outputStr;
    String				msg;
    String[]				classes;
    Class				cls;
    MapToJson 				mapToString;
    Set<Class>				managedClasses;
    Set<Class>				baseClasses;
    ConcreteBaseClassesOptionTraverser 	baseClassesTrav;

    if (m_ClassListerInput == null)
      return "No classlister props file provided as input!";
    if (!m_ClassListerInput.exists())
      return "Classlister props file does not exist: " + m_ClassListerInput;
    if (m_ClassListerInput.isDirectory())
      return "Classlister props file points to directory: " + m_ClassListerInput;

    // read properties
    props = new Properties();
    try {
      props.load(m_ClassListerInput.getAbsolutePath());
    }
    catch (Exception e) {
      return "Failed to read classlister props file: " + e;
    }

    // collect data
    output          = new HashMap<>();
    managedClasses  = new HashSet<>();
    baseClasses     = new HashSet<>();
    baseClassesTrav = new ConcreteBaseClassesOptionTraverser();
    for (String superclass: props.keySetAll()) {
      classes = props.getProperty(superclass).split(",");
      for (String class_ : classes) {
	if (class_.trim().isEmpty())
	  continue;
	try {
	  cls = ClassManager.getSingleton().forName(class_);
	  managedClasses.add(cls);
	  collect(cls, baseClassesTrav, baseClasses, output);
	}
	catch (Exception e) {
	  LoggingHelper.global().severe("Failed to process: " + class_, e);
	}
      }
    }

    // remove managed classes
    baseClasses.removeAll(managedClasses);
    if (!baseClasses.isEmpty()) {
      LoggingHelper.global().warning("Adding unmanaged classes: " + baseClasses);
      for (Class base: baseClasses)
	collect(base, null, null, output);
    }

    // generate output
    mapToString = new MapToJson();
    outputStr   = mapToString.mapToString(output);

    if (m_Output == null) {
      System.out.println(outputStr);
    }
    else {
      msg = FileUtils.writeToFileMsg(m_Output.getAbsolutePath(), outputStr, false, null);
      if (msg != null)
	return null;
    }

    return null;
  }

  /**
   * For generating the option JSON output from the command-line.
   *
   * @param args	the options to use
   * @throws Exception	if anything goes wrong
   */
  public static void main(String[] args) throws Exception {
    OptionLister	lister;
    String		msg;

    if (OptionUtils.helpRequested(args)) {
      System.out.println();
      System.out.println("Usage: " + OptionLister.class.getName() + " -input <classlister props file> [-output <JSON file>]");
      System.out.println();
      System.out.println("-input <classlister props file>");
      System.out.println("    the properties file generated by the " + ClassLister.class.getName() + " tool");
      System.out.println("-output <JSON file>");
      System.out.println("    the JSON file to write the generated data to; outputs to stdout if omitted");
      System.out.println();
      return;
    }

    lister = new OptionLister();
    if (!OptionUtils.hasFlag(args, "-input"))
      throw new IllegalArgumentException("Missing option: -input");
    lister.setClassListerInput(new File(OptionUtils.getOption(args, "-input")));
    if (OptionUtils.hasFlag(args, "-output"))
      lister.setOutput(new File(OptionUtils.getOption(args, "-output")));
    msg = lister.execute();
    if (msg != null)
      throw new IllegalStateException(msg);
    System.exit(0);
  }
}
