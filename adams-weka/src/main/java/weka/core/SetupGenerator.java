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
 * SetupGenerator.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import weka.core.PropertyPath.Path;
import weka.core.PropertyPath.PropertyContainer;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.ListParameter;
import weka.core.setupgenerator.MathParameter;
import weka.core.setupgenerator.Point;
import weka.core.setupgenerator.Space;
import weka.core.setupgenerator.SpaceDimension;
import adams.core.option.OptionUtils;

/**
 * Generates different setups of objects (e.g., classifiers or filters) based
 * on parameter settings. The parameter settings can be either based on
 * mathematical functions; therefore numeric) or chosen from lists (for
 * string values, SelectedTags or classnames (with optional parameters).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetupGenerator
  implements Serializable, OptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7683008589774888142L;

  /** type: mathematical function. */
  public static final int TYPE_FUNCTION = 0;
  /** type: explicit, comma-separated list of values. */
  public static final int TYPE_LIST = 1;
  /** type of parameter. */
  public static final Tag[] TAGS_TYPE = {
    new Tag(TYPE_FUNCTION, "func", "Mathematical function"),
    new Tag(TYPE_LIST, "list", "Comma-separated list of values")
  };

  /** base object. */
  protected Serializable m_BaseObject;

  /** the parameters. */
  protected AbstractParameter[] m_Parameters;

  /** whether everything has been initialized. */
  protected boolean m_Initialized;

  /** the parameter space to use for obtaining the setups from. */
  protected Space m_Space;

  /**
   * Default constructor.
   */
  public SetupGenerator() {
    super();

    m_BaseObject  = null;
    m_Parameters  = new AbstractParameter[0];

    reset();
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Generates different setups of objects (e.g., classifiers or filters) based "
      + "on parameter settings. The parameter settings can be either based on "
      + "mathematical functions; therefore numeric) or chosen from lists (for "
      + "string values, SelectedTags or classnames (with optional parameters).";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector       	result;
    Enumeration		en;

    result = new Vector();

    result.addElement(new Option(
	  "\tThe object to generate the setups for, e.g., a classifier.\n"
	  + "\tOptions for the object (in case it is an OptionHandler)\n"
	  + "\thave to be provided after the '--' meta-option.",
	  "W", 1, "-W \"<classname>\""));

    result.addElement(new Option(
	  "\tA parameter setup for generating the setups.\n"
	  + "\tCan be supplied multiple times.\n"
	  + "\t(default: " + AbstractParameter.class.getName() + ")",
	  "search", 1, "-search <classname options>"));

    result.addElement(new Option(
	  "",
	  "", 0, "\nOptions specific to search parameter class '"
	  + MathParameter.class.getName() + "' ('-search'):"));

    en = new MathParameter().listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    result.addElement(new Option(
	  "",
	  "", 0, "\nOptions specific to search parameter class '"
	  + ListParameter.class.getName() + "' ('-search'):"));

    en = new ListParameter().listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return		the current options
   */
  public String[] getOptions() {
    int       	i;
    Vector    	result;
    String[]  	options;
    String	tmpStr;

    result = new Vector();

    result.add("-W");
    result.add("" + getBaseObject().getClass().getName());

    for (i = 0; i < m_Parameters.length; i++) {
      result.add("-search");
      tmpStr =   m_Parameters[i].getClass().getName() + " "
               + Utils.joinOptions(m_Parameters[i].getOptions());
      result.add(tmpStr);
    }

    if (getBaseObject() instanceof OptionHandler) {
      result.add("--");
      options = ((OptionHandler) getBaseObject()).getOptions();
      for (i = 0; i < options.length; i++)
	result.add(options[i]);
    }

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  public void setOptions(String[] options) throws Exception {
    String		tmpStr;
    String[]		tmpOptions;
    Vector<String>	search;
    int			i;
    AbstractParameter[]		params;
    Serializable	base;

    tmpStr = Utils.getOption('W', options);
    if (tmpStr.length() != 0) {
      base = (Serializable) OptionUtils.forName(Serializable.class, tmpStr, Utils.partitionOptions(options));
      setBaseObject(base);
    }
    else {
      throw new IllegalArgumentException("No base object provided!");
    }

    search = new Vector<String>();
    do {
      tmpStr = Utils.getOption("search", options);
      if (tmpStr.length() > 0)
	search.add(tmpStr);
    }
    while (tmpStr.length() > 0);
    if (search.size() == 0)
      throw new IllegalArgumentException("No search parameters provided!");
    params = new AbstractParameter[search.size()];
    for (i = 0; i < search.size(); i++) {
      tmpOptions    = Utils.splitOptions(search.get(i));
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      params[i]     = (AbstractParameter) OptionUtils.forName(AbstractParameter.class, tmpStr, tmpOptions);
    }
    setParameters(params);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String baseObjectFileTipText() {
    return "The base object to set the parameters for.";
  }

  /**
   * Sets the base object, can be single object or array of objects.
   *
   * @param obj		the object
   */
  public void setBaseObject(Serializable obj) {
    m_BaseObject = obj;
    reset();
  }

  /**
   * Returns the base object.
   *
   * @return		the object
   */
  public Serializable getBaseObject() {
    return m_BaseObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String parametersFileTipText() {
    return "The parameter definitions.";
  }

  /**
   * Sets the parameters to use as basis for the setups.
   *
   * @param value	the parameters
   */
  public void setParameters(AbstractParameter[] value) {
    m_Parameters = value;
    reset();
  }

  /**
   * Returns the current parameters.
   *
   * @return		the parameters
   */
  public AbstractParameter[] getParameters() {
    return m_Parameters;
  }

  /**
   * Resets the generation.
   */
  public void reset() {
    m_Initialized = false;
    m_Space       = null;
  }

  /**
   * Updates the space to use for the setup generation.
   *
   * @param value	the space to use
   */
  public void setSpace(Space value) {
    m_Space       = value;
    m_Initialized = false;
  }

  /**
   * Returns the space currently in use.
   *
   * @return		the space
   */
  public Space getSpace() {
    initialize();
    return m_Space;
  }

  /**
   * Performs all the necessary initializations.
   */
  protected void initialize() {
    SpaceDimension[]	dims;
    int			i;

    if (m_Initialized)
      return;

    // a few sanity checks
    //if (m_Parameters.length == 0)
    //  throw new IllegalStateException("No parameters set!");
    if (m_BaseObject == null)
      throw new IllegalStateException("No base object set!");

    // setup space, if necessary
    if (m_Space == null) {
      dims = new SpaceDimension[m_Parameters.length];
      for (i = 0; i < m_Parameters.length; i++) {
	try {
	  dims[i] = new SpaceDimension(m_Parameters[i]);
	}
	catch (Exception e) {
	  e.printStackTrace();
	  throw new IllegalStateException(
	      "Error initializing space dimension #" + (i+1) + ": " + e);
	}
      }
      m_Space = new Space(dims);
    }

    m_Initialized = true;
  }

  /**
   * evalutes the expression for the current iteration.
   *
   * @param values	the current iteration values (from 'min' to 'max' with
   * 			stepsize 'step') for all dimensions
   * @return		the generated value, NaN if the evaluation fails
   */
  public Point<Object> evaluate(Point<Object> values) {
    HashMap		symbols;
    String		expr;
    double		base;
    double		min;
    double		max;
    double		step;
    Object		value;
    int			i;
    Object[]		evaluated;

    evaluated = new Object[values.dimensions()];

    for (i = 0; i < values.dimensions(); i++) {
      if (m_Parameters[i] instanceof MathParameter) {
	expr = ((MathParameter) m_Parameters[i]).getExpression();
	base = ((MathParameter) m_Parameters[i]).getBase();
	min  = ((MathParameter) m_Parameters[i]).getMin();
	max  = ((MathParameter) m_Parameters[i]).getMax();
	step = ((MathParameter) m_Parameters[i]).getStep();
	value = values.getValue(i);

	try {
	  symbols = new HashMap();
	  symbols.put("BASE", new Double(base));
	  symbols.put("FROM", new Double(min));
	  symbols.put("TO",   new Double(max));
	  symbols.put("STEP", new Double(step));
	  symbols.put("I",    new Double((Double) value));
	  evaluated[i] = MathematicalExpression.evaluate(expr, symbols);
	}
	catch (Exception e) {
	  evaluated[i] = Double.NaN;
	}
      }
      else if (m_Parameters[i] instanceof ListParameter) {
	evaluated[i] = values.getValue(i);
      }
      else {
	throw new IllegalStateException(
	    "Unhandled parameter class '" + m_Parameters[i].getClass().getName() + "'!");
      }
    }

    return new Point<Object>(evaluated);
  }

  /**
   * tries to set the value as double, integer (just casts it to int!) or
   * boolean (false if 0, otherwise true) in the object according to the
   * specified path. float, char and long are also supported.
   *
   * @param o		the object to modify
   * @param path	the property path
   * @param value	the value to set
   * @return		the modified object
   * @throws Exception	if neither double nor int could be set
   */
  public Object setValue(Object o, String path, Object value) throws Exception {
    PropertyDescriptor	desc;
    Class		c;
    double		valDouble;
    String		valString;
    Tag[]		tags;
    SelectedTag		selTag;
    int			i;
    boolean		found;
    Object		valObject;
    String[]		options;
    String		classname;

    desc = PropertyPath.getPropertyDescriptor(o, path);
    c    = desc.getPropertyType();

    if (value instanceof Double) {
      valDouble = (Double) value;

      // float
      if ((c == Float.class) || (c == Float.TYPE))
	PropertyPath.setValue(o, path, new Float((float) valDouble));
      // double
      else if ((c == Double.class) || (c == Double.TYPE))
	PropertyPath.setValue(o, path, new Double(valDouble));
      // char
      else if ((c == Character.class) || (c == Character.TYPE))
	PropertyPath.setValue(o, path, new Integer((char) valDouble));
      // int
      else if ((c == Integer.class) || (c == Integer.TYPE))
	PropertyPath.setValue(o, path, new Integer((int) valDouble));
      // long
      else if ((c == Long.class) || (c == Long.TYPE))
	PropertyPath.setValue(o, path, new Long((long) valDouble));
      // boolean
      else if ((c == Boolean.class) || (c == Boolean.TYPE))
	PropertyPath.setValue(o, path, (valDouble == 0 ? new Boolean(false) : new Boolean(true)));
      else
	throw new Exception(
	  "Could neither set double nor integer nor boolean value '"
	    + valDouble + "' for '" + path + "'!");
    }
    else {
      valString = (String) value;

      // float
      if ((c == Float.class) || (c == Float.TYPE)) {
	PropertyPath.setValue(o, path, Float.parseFloat(valString));
      }
      // double
      else if ((c == Double.class) || (c == Double.TYPE)) {
	PropertyPath.setValue(o, path, Double.parseDouble(valString));
      }
      // char
      else if ((c == Character.class) || (c == Character.TYPE)) {
	PropertyPath.setValue(o, path, valString.charAt(0));
      }
      // int
      else if ((c == Integer.class) || (c == Integer.TYPE)) {
	PropertyPath.setValue(o, path, Integer.parseInt(valString));
      }
      // long
      else if ((c == Long.class) || (c == Long.TYPE)) {
	PropertyPath.setValue(o, path, Long.parseLong(valString));
      }
      // boolean
      else if ((c == Boolean.class) || (c == Boolean.TYPE)) {
	PropertyPath.setValue(o, path, Boolean.parseBoolean(valString));
      }
      // string
      else if (c == String.class) {
	PropertyPath.setValue(o, path, valString);
      }
      // selected tag
      else if (c == SelectedTag.class) {
	selTag = (SelectedTag) PropertyPath.getValue(o, path);
	tags   = selTag.getTags();
	// check ID string
	found  = false;
	for (i = 0; i < tags.length; i++) {
	  if (tags[i].getIDStr().equals(valString)) {
	    PropertyPath.setValue(o, path, new SelectedTag(tags[i].getID(), tags));
	    found = true;
	    break;
	  }
	}
	// try "readable"
	if (!found) {
	  for (i = 0; i < tags.length; i++) {
	    if (tags[i].getReadable().equals(valString)) {
	      PropertyPath.setValue(o, path, new SelectedTag(tags[i].getID(), tags));
	      found = true;
	      break;
	    }
	  }
	}
      }
      // a classname maybe?
      else if (valString.indexOf(".") > 0) {
	try {
	  options    = Utils.splitOptions(valString);
	  classname  = options[0];
	  options[0] = "";
	  valObject  = OptionUtils.forName(Object.class, classname, options);
	  PropertyPath.setValue(o, path, valObject);
	}
	catch (Exception e) {
	  e.printStackTrace();
	  throw new Exception(
	      "Failed to instantiate object from  '" + valString + "' for '" + path + "'!");
	}
      }
      else {
	throw new Exception(
	  "Could not set list value '" + valString + "' for '" + path + "'!");
      }
    }

    return o;
  }

  /**
   * returns a fully configures object (a copy of the provided one).
   *
   * @param original	the object to create a copy from and set the parameters
   * @param values	the current iteration values in all dimensions
   * @return		the configured classifier
   * @throws Exception	if setup fails
   */
  public Serializable setup(Serializable original, Point<Object> values) throws Exception {
    Serializable	result;
    int			i;
    PropertyContainer	cnt;

    result = (Serializable) new SerializedObject(original).getObject();

    for (i = 0; i < values.dimensions(); i++) {
      cnt = PropertyPath.find(original, new Path(m_Parameters[i].getProperty()));
      if (cnt != null)
	setValue(
	    result,
	    m_Parameters[i].getProperty(),
	    values.getValue(i));
    }

    return result;
  }

  /**
   * Returns a copy of the object.
   *
   * @param obj		the object to copy
   * @return		the copy
   * @throws Exception	if copying fails
   */
  protected Serializable copy(Serializable obj) throws Exception {
    return (Serializable) new SerializedObject(obj).getObject();
  }

  /**
   * Returns an enumeration of all the setups.
   *
   * @return		the generated setups, null in case a setup could not
   * 			be generated
   */
  public Enumeration<Serializable> setups() {
    Vector<Serializable>	result;
    Enumeration<Point<Object>>	values;
    Point<Object>		value;
    Point<Object>		evaluated;
    Serializable		newObj;

    initialize();

    result = new Vector<Serializable>();
    values = m_Space.values();
    while (values.hasMoreElements()) {
      value     = values.nextElement();
      evaluated = evaluate(value);
      try {
	newObj = setup(m_BaseObject, evaluated);
	result.add(newObj);
      }
      catch (Exception e) {
	System.err.println("Failed to generate setup:");
	e.printStackTrace();
	result = null;
	break;
      }
    }

    if (result != null)
      return result.elements();
    else
      return null;
  }

  /**
   * A string representation of the generator.
   *
   * @return		a string representation
   */
  public String toString() {
    StringBuffer	result;
    int			i;

    result = new StringBuffer();

    // objects
    result.append("Base object:\n");
    result.append(m_BaseObject.getClass().getName());
    if (m_BaseObject instanceof OptionHandler)
	result.append(" " + Utils.joinOptions(((OptionHandler) m_BaseObject).getOptions()));

    // parameters
    result.append("Parameters:\n");
    for (i = 0; i < m_Parameters.length; i++) {
      result.append((i+1) + ". ");
      result.append(Utils.joinOptions(m_Parameters[i].getOptions()));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    SetupGenerator		generator;
    AbstractParameter[]		params;
    Enumeration<Serializable>	setups;
    Serializable		obj;
    int				i;
    weka.classifiers.Classifier	fc;

    // setup the generator
    generator = new SetupGenerator();
    fc = new weka.classifiers.meta.FilteredClassifier();
    ((weka.classifiers.meta.FilteredClassifier) fc).setFilter(new weka.filters.supervised.attribute.PLSFilter());
    ((weka.classifiers.meta.FilteredClassifier) fc).setClassifier(new weka.classifiers.functions.LinearRegression());
    generator.setBaseObject((Serializable) fc);

    params = new AbstractParameter[3];

    params[0] = new MathParameter();
    ((MathParameter) params[0]).setProperty("classifier.ridge");
    ((MathParameter) params[0]).setMin(-5);
    ((MathParameter) params[0]).setMax(+3);
    ((MathParameter) params[0]).setStep(1);
    ((MathParameter) params[0]).setBase(10);
    ((MathParameter) params[0]).setExpression("pow(BASE,I)");

    params[1] = new MathParameter();
    ((MathParameter) params[1]).setProperty("filter.numComponents");
    ((MathParameter) params[1]).setMin(+5);
    ((MathParameter) params[1]).setMax(+20);
    ((MathParameter) params[1]).setStep(1);
    ((MathParameter) params[1]).setBase(10);
    ((MathParameter) params[1]).setExpression("I");

    params[2] = new ListParameter();
    ((ListParameter) params[2]).setProperty("filter.algorithm");
    ((ListParameter) params[2]).setList("PLS1 SIMPLS");

    generator.setParameters(params);
    System.out.println("\nGenerator setup:\n\n" + generator);

    // output the setups
    System.out.println("\nGenerated setups:\n");
    setups = generator.setups();
    if (setups != null) {
      i      = 0;
      while (setups.hasMoreElements()) {
	i++;
	System.out.println(i + ". Setup:");
	obj = setups.nextElement();
	System.out.print("  " + obj.getClass().getName());
	if (obj instanceof OptionHandler)
	  System.out.print(" " + Utils.joinOptions(((OptionHandler) obj).getOptions()));
	System.out.println();
      }
    }
    else {
      System.err.println("Encountered error in setup generation!");
    }
  }
}
