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
 * Listing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import adams.core.ClassLister;
import adams.core.base.BaseClassname;
import adams.core.option.AbstractCommandLineHandler;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.Modules;
import adams.env.Modules.Module;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates class listings per module.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Listing
  extends AbstractOptionHandler  {

  private static final long serialVersionUID = 5329297063876357426L;

  /** the environment class (dummy option). */
  protected String m_Environment;

  /** the superclass to use. */
  protected BaseClassname m_Superclass;

  /** the output scheme to use. */
  protected AbstractListingOutput m_Output;

  @Override
  public String globalInfo() {
    return "Generates a listing of all the classes located for the specified "
      + "superclass and outputs it via the selected output scheme.\n"
      + "The listing is split into per-module sub-lists.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    // dummy option
    m_OptionManager.add(
	"env", "environment",
	Environment.class.getName());

    m_OptionManager.add(
      "superclass", "superclass",
      new BaseClassname(Actor.class));

    m_OptionManager.add(
      "output", "output",
      new ConsoleOutput());
  }

  /**
   * sets the classname of the environment class to use.
   *
   * @param value	the environment class name
   */
  public void setEnvironment(String value) {
    m_Environment = value;
  }

  /**
   * returns the current classname of the environment class to use.
   *
   * @return	the current classname of the environment class
   */
  public String getEnvironment() {
    return m_Environment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String environmentTipText() {
    return "The class to use for determining the environment.";
  }

  /**
   * Sets the superclass to use for the listing.
   *
   * @param value	the scheme
   */
  public void setSuperclass(BaseClassname value) {
    m_Superclass = value;
    reset();
  }

  /**
   * Returns the superclass to use for the listing.
   *
   * @return		the scheme
   */
  public BaseClassname getSuperclass() {
    return m_Superclass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String superclassTipText() {
    return "The superclass to use for the listing.";
  }

  /**
   * Sets the scheme for outputting the generated listing.
   *
   * @param value	the scheme
   */
  public void setOutput(AbstractListingOutput value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the scheme for outputting the generated listing.
   *
   * @return		the scheme
   */
  public AbstractListingOutput getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The scheme to use for outputting the generated listing.";
  }

  /**
   * Generates the listing.
   *
   * @return		null if successful, otherwise error message
   */
  public String generate() {
    String			result;
    Map<String,Set<String>> 	raw;
    Map<String,List<String>> 	listing;
    List<String>		list;
    Class[]			classes;
    Class[]			classesPerModule;

    // raw listing
    classes = ClassLister.getSingleton().getClasses(m_Superclass.classValue());
    raw     = new HashMap<>();
    for (Module module: Modules.getSingleton().getModules()) {
      raw.put(module.getName(), new HashSet<>());
      classesPerModule = ClassLister.getSingleton().filterByModule(classes, module.getName());
      for (Class cls: classesPerModule)
        raw.get(module.getName()).add(cls.getName());
    }

    // sort
    listing = new HashMap<>();
    for (String module: raw.keySet()) {
      list = new ArrayList<>(raw.get(module));
      Collections.sort(list);
      listing.put(module, list);
    }

    // output
    result = m_Output.generate(m_Superclass.classValue(), listing);

    return result;
  }

  /**
   * For generating the listing.
   *
   * @param options	the commandline options
   * @throws Exception	if generation fails
   */
  public static void runListing(String[] options) throws Exception {
    String			env;
    AbstractCommandLineHandler	handler;
    Listing			listing;
    String			msg;

    // we have to set the environment before anything else happens
    env = OptionUtils.getOption(options, "-env");
    if ((env == null) || (env.length() == 0))
      env = Environment.class.getName();
    try {
      Environment.setEnvironmentClass(Class.forName(env));
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate the environment class: " + env);
      e.printStackTrace();
      Environment.setEnvironmentClass(Environment.class);
    }

    try {
      try {
	if (OptionUtils.helpRequested(options)) {
	  System.out.println("Help requested...\n");
	  System.out.println("\n" + OptionUtils.list(new Listing()));
	  return;
	}
	else {
	  listing = new Listing();
	  handler = AbstractCommandLineHandler.getHandler(listing);
	  handler.setOptions(listing, options);
	}
      }
      catch (Exception ex) {
        String result = "\n" + ex.getMessage() + "\n\n" + OptionUtils.list(new Listing());
        throw new Exception(result);
      }

      msg = listing.generate();
      if (msg != null)
        throw new IllegalStateException(msg);
      // System.exit is necessary for some GUI related classes, due to
      // invisible frames
      System.exit(0);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   * For generating the listing from the commandline.
   *
   * @param args	the options
   * @throws Exception	if generation fails
   */
  public static void main(String[] args) throws Exception {
    runListing(args);
  }
}
