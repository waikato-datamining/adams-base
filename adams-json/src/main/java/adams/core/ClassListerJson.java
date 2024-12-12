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
 * ClassListerJson.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.goe.AbstractEditorRegistration;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;

/**
 * Same as {@link ClassLister}, but with support for outputting JSON when calling the main method.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ClassListerJson
  extends ClassLister {

  private static final long serialVersionUID = -1468292265379375783L;

  /** for statically listed classes (superclass -> comma-separated classnames). */
  public static final String CLASSLISTERJSON_CLASSES = "ClassListerJson.classes";

  /** for statically listed packages (superclass -> comma-separated packages). */
  public static final String CLASSLISTERJSON_PACKAGES = "ClassListerJson.packages";

  /**
   * Extracts the format from the command-line options.
   *
   * @param args	the options to parse
   * @return		the format
   * @throws Exception	if unsupported format encountered
   */
  protected static String getFormat(String[] args) throws Exception {
    String 	result;

    result = OptionUtils.getOption(args, "-format");
    if (result == null)
      result = "props";
    result = result.toLowerCase();
    if (!(result.equals("props") || result.equals("json")))
      throw new IllegalArgumentException("Unsupported format: " + result);

    return result;
  }

  /**
   * Outputs a list of available conversions.
   *
   * @param args	the commandline options: [-env classname] [-super classname] [-match regexp]
   * @throws Exception	if invalid environment class or invalid regular expression
   */
  public static void main(String[] args) throws Exception {
    if (OptionUtils.helpRequested(args)) {
      System.out.println();
      System.out.println("Usage: " + ClassLister.class.getName() + " [-env <classname>] [-action <search|classes|packages>]");
      System.out.println();
      System.out.println("'search' action (default):");
      System.out.println("allows searching for classes");
      System.out.println("[-super <classname>] [-match <regexp>] [-allow-empty] [-filter-by-module <module>]");
      System.out.println();
      System.out.println("'classes' action:");
      System.out.println("for outputting the class hierarchies as file");
      System.out.println("each key is a superclass, the corresponding value a comma-separated list of class names");
      System.out.println("-output <file>");
      System.out.println("-format <props|json> (default: props)");
      System.out.println();
      System.out.println("'packages' action:");
      System.out.println("for outputting the packages of the class hierarchies as file");
      System.out.println("each key is a superclass, the corresponding value a comma-separated list of package names");
      System.out.println("-output <file>");
      System.out.println("-format <props|json> (default: props)");
      System.out.println();
      return;
    }

    // environment
    String env = OptionUtils.getOption(args, "-env");
    if (env == null)
      env = Environment.class.getName();
    Class cls = Class.forName(env);
    Environment.setEnvironmentClass(cls);

    // register editors
    AbstractEditorRegistration.registerEditors();

    // action
    String action = OptionUtils.getOption(args, "-action");
    if (action == null)
      action = "search";
    String format;
    switch (action) {
      case "search":
	// match
	String match = OptionUtils.getOption(args, "-match");
	if (match == null)
	  match = BaseRegExp.MATCH_ALL;
	BaseRegExp regexp = new BaseRegExp(match);

	// allow empty class hierarchies?
	boolean allowEmpty = OptionUtils.hasFlag(args, "-allow-empty");

	// superclass
	String[] superclasses;
	String sclass = OptionUtils.getOption(args, "-super");
	if (sclass == null)
	  superclasses = getSingleton().getSuperclasses();
	else
	  superclasses = new String[]{sclass};

	// filter-by-module
	String module = OptionUtils.getOption(args, "-filter-by-module");

	// list them
	for (String superclass : superclasses) {
	  cls = Class.forName(superclass);
	  Class[] classes = getSingleton().getClasses(cls);
	  if (module != null)
	    classes = getSingleton().filterByModule(classes, module);
	  if ((classes.length > 0) || allowEmpty) {
	    System.out.println("--> " + superclass);
	    for (Class c : classes) {
	      if (regexp.isMatch(c.getName()))
		System.out.println(c.getName());
	    }
	    System.out.println();
	  }
	}
	break;

      case "classes":
	format = getFormat(args);
	PlaceholderFile class_file = new PlaceholderFile(OptionUtils.getOption(args, "-output"));
	File class_dir = class_file.getParentFile();
	if (!class_dir.exists()) {
	  if (!class_dir.mkdirs()) {
	    System.err.println("Failed to create directory for classes file: " + class_dir);
	    System.exit(2);
	  }
	}
	Properties class_props = new Properties(getSingleton().toProperties());
	if (format.equals("props")) {
	  if (!class_props.save(class_file.getAbsolutePath())) {
	    System.err.println("Failed to write properties with classes to: " + class_file);
	    System.exit(1);
	  }
	}
	else if (format.equals("json")) {
	  JSONObject class_json = new JSONObject();
	  for (String key: class_props.keySetAll()) {
	    JSONArray class_array = new JSONArray();
	    class_json.put(key, class_array);
	    for (String cname: class_props.getProperty(key, "").split(",")) {
	      if (cname.trim().isEmpty())
		continue;
	      class_array.add(cname);
	    }
	  }
	  if (!FileUtils.writeToFile(class_file.getAbsolutePath(), class_json, false, null)) {
	    System.err.println("Failed to write json with classes to: " + class_file);
	    System.exit(1);
	  }
	}
	else {
	  System.err.println("Unhandled format: " + format);
	  System.exit(2);
	}
	break;

      case "packages":
	format = getFormat(args);
	PlaceholderFile pkgs_file = new PlaceholderFile(OptionUtils.getOption(args, "-output"));
	File pkgs_dir = pkgs_file.getParentFile();
	if (!pkgs_dir.exists()) {
	  if (!pkgs_dir.mkdirs()) {
	    System.err.println("Failed to create directory for packages file: " + pkgs_dir);
	    System.exit(2);
	  }
	}
	Properties pkgs_props = new Properties(getSingleton().toPackages());
	if (format.equals("props")) {
	  if (!pkgs_props.save(pkgs_file.getAbsolutePath())) {
	    System.err.println("Failed to write properties with packages to: " + pkgs_file);
	    System.exit(1);
	  }
	}
	else {
	  JSONObject pkgs_json = new JSONObject();
	  for (String key: pkgs_props.keySetAll()) {
	    JSONArray pkgs_array = new JSONArray();
	    pkgs_json.put(key, pkgs_array);
	    for (String pname : pkgs_props.getProperty(key, "").split(",")) {
	      if (pname.trim().isEmpty())
		continue;
	      pkgs_array.add(pname);
	    }
	  }
	  if (!FileUtils.writeToFile(pkgs_file.getAbsolutePath(), pkgs_json, false, null)) {
	    System.err.println("Failed to write json with packages to: " + pkgs_file);
	    System.exit(1);
	  }
	}
	break;

      default:
	throw new IllegalArgumentException("Unknown action: " + action);
    }
  }
}
