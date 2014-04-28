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
 * MathParameter.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package weka.core.setupgenerator;

import java.util.Enumeration;
import java.util.Vector;

import weka.core.MathematicalExpression;
import weka.core.Option;
import weka.core.Utils;
import weka.filters.unsupervised.attribute.MathExpression;

/**
 * Container class for search parameters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathParameter
  extends AbstractParameter {
  
  /** for serialization. */
  private static final long serialVersionUID = 6430400142112674316L;

  /** the minimum. */
  protected double m_Min = 1.0;
  
  /** the maximum. */
  protected double m_Max = 5.0;
  
  /** the step. */
  protected double m_Step = 1.0;
  
  /** the base. */
  protected double m_Base = 10.0;

  /**
   * The expression for the property. Available parameters for the
   * expression:
   * <ul>
   *   <li>BASE</li>
   *   <li>FROM (= min)</li>
   *   <li>TO (= max)</li>
   *   <li>STEP</li>
   *   <li>I - the current value (from 'from' to 'to' with stepsize 'step')</li>
   * </ul>
   * 
   * @see MathematicalExpression
   * @see MathExpression
   */
  protected String m_Expression = "pow(BASE,I)";
  
  /**
   * Returns a string describing the object.
   * 
   * @return 		a description suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String globalInfo() {
    return 
        "Container class defining the search parameters for a particular "
      + "property.\n"
      + "The expression is a mathematical expression that uses the "
      + "following variables:\n"
      + "  BASE\n"
      + "  FROM (= min)\n"
      + "  TO (= max)\n"
      + "  STEP\n"
      + "  I - the current value (from 'from' to 'to' with stepsize 'step')\n"
      + "For more information on the allowed mathematical operations, see "
      + "Javadoc of class " + MathematicalExpression.class.getName() + ".";
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector	result;
    Enumeration	enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option(
        "\tThe minimum.\n"
        + "\t(default: -10)",
        "min", 1, "-min <num>"));

    result.addElement(new Option(
        "\tThe maximum.\n"
        + "\t(default: +5)",
        "max", 1, "-max <num>"));

    result.addElement(new Option(
        "\tThe step size.\n"
        + "\t(default: 1)",
        "step", 1, "-step <num>"));

    result.addElement(new Option(
        "\tThe base.\n"
        + "\t(default: 10)",
        "base", 1, "-base <num>"));

    result.addElement(new Option(
        "\tThe expression.\n"
        + "\tAvailable parameters:\n"
        + "\t\tBASE\n"
        + "\t\tFROM\n"
        + "\t\tTO\n"
        + "\t\tSTEP\n"
        + "\t\tI - the current iteration value\n"
        + "\t\t(from 'FROM' to 'TO' with stepsize 'STEP')\n"
        + "\t(default: 'pow(BASE,I)')",
        "expression", 1, "-expression <expr>"));
    
    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return		the current options
   */
  public String[] getOptions() {
    Vector<String>	result;
    String[]		options;
    int			i;

    result = new Vector<String>();

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);
    
    result.add("-min");
    result.add("" + getMin());

    result.add("-max");
    result.add("" + getMax());

    result.add("-step");
    result.add("" + getStep());

    result.add("-base");
    result.add("" + getBase());

    result.add("-expression");
    result.add("" + getExpression());

    return result.toArray(new String[result.size()]);	  
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("min", options);
    if (tmpStr.length() != 0)
      setMin(Double.parseDouble(tmpStr));
    else
      setMin(-10);

    tmpStr = Utils.getOption("max", options);
    if (tmpStr.length() != 0)
      setMax(Double.parseDouble(tmpStr));
    else
      setMax(10);

    tmpStr = Utils.getOption("step", options);
    if (tmpStr.length() != 0)
      setStep(Double.parseDouble(tmpStr));
    else
      setStep(1);

    tmpStr = Utils.getOption("base", options);
    if (tmpStr.length() != 0)
      setBase(Double.parseDouble(tmpStr));
    else
      setBase(10);

    tmpStr = Utils.getOption("expression", options);
    if (tmpStr.length() != 0)
      setExpression(tmpStr);
    else
      setExpression("pow(BASE,I)");

    super.setOptions(options);
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minTipText() {
    return "The minimum.";
  }

  /**
   * Get the value of the minimum.
   *
   * @return Value of the minimum.
   */
  public double getMin() {
    return m_Min;
  }
  
  /**
   * Set the value of the minimum.
   *
   * @param value Value to use as minimum.
   */
  public void setMin(double value) {
    m_Min = value;
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxTipText() {
    return "The maximum.";
  }

  /**
   * Get the value of the Maximum.
   *
   * @return Value of the Maximum.
   */
  public double getMax() {
    return m_Max;
  }
  
  /**
   * Set the value of the Maximum.
   *
   * @param value Value to use as Maximum.
   */
  public void setMax(double value) {
    m_Max = value;
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String stepTipText() {
    return "The step size.";
  }

  /**
   * Get the value of the step size.
   *
   * @return Value of the step size.
   */
  public double getStep() {
    return m_Step;
  }
  
  /**
   * Set the value of the step size.
   *
   * @param value Value to use as the step size.
   */
  public void setStep(double value) {
    m_Step = value;
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String baseTipText() {
    return "The base of X.";
  }

  /**
   * Get the value of the base.
   *
   * @return Value of the base.
   */
  public double getBase() {
    return m_Base;
  }
  
  /**
   * Set the value of the base.
   *
   * @param value Value to use as the base.
   */
  public void setBase(double value) {
    m_Base = value;
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String expressionTipText() {
    return "The expression for the value (parameters: BASE, FROM, TO, STEP, I).";
  }

  /**
   * Get the expression.
   *
   * @return Expression.
   */
  public String getExpression() {
    return m_Expression;
  }
  
  /**
   * Set the expression.
   *
   * @param value Expression.
   */
  public void setExpression(String value) {
    m_Expression = value;
  }
  
  /**
   * Returns a string representation of the search parameter.
   * 
   * @return		a string representation
   */
  public String toString() {
    String	result;

    result = super.toString();
    result += 
        ", min: " + getMin() 
      + ", max: " + getMax() 
      + ", step: " + getStep()
      + ", base: " + getBase()
      + ", expr: " + getExpression();
    
    return result;
  }
}