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
 * MathExpressionClassifier.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.functions;

import adams.core.base.BaseString;
import adams.env.Environment;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Simple classifier that uses a pre-defined formula that can make use of attribute values using their names.<br>
 * Grammar:<br>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br>
 * expr_part ::=  expr ;<br>
 * <br>
 * expr      ::=   ( expr )<br>
 * <br>
 * # data types<br>
 *               | number<br>
 *               | string<br>
 *               | boolean<br>
 *               | date<br>
 * <br>
 * # constants<br>
 *               | true<br>
 *               | false<br>
 *               | pi<br>
 *               | e<br>
 *               | now()<br>
 *               | today()<br>
 * <br>
 * # negating numeric value<br>
 *               | -expr<br>
 * <br>
 * # comparisons<br>
 *               | expr &lt; expr<br>
 *               | expr &lt;= expr<br>
 *               | expr &gt; expr<br>
 *               | expr &gt;= expr<br>
 *               | expr = expr<br>
 *               | expr != expr (or: expr &lt;&gt; expr)<br>
 * <br>
 * # boolean operations<br>
 *               | ! expr (or: not expr)<br>
 *               | expr &amp; expr (or: expr and expr)<br>
 *               | expr | expr (or: expr or expr)<br>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br>
 *               | isNaN ( expr )<br>
 * <br>
 * # arithmetics<br>
 *               | expr + expr<br>
 *               | expr - expr<br>
 *               | expr * expr<br>
 *               | expr / expr<br>
 *               | expr ^ expr (power of)<br>
 *               | expr % expr (modulo)<br>
 *               ;<br>
 * <br>
 * # numeric functions<br>
 *               | abs ( expr )<br>
 *               | sqrt ( expr )<br>
 *               | cbrt ( expr )<br>
 *               | log ( expr )<br>
 *               | log10 ( expr )<br>
 *               | exp ( expr )<br>
 *               | sin ( expr )<br>
 *               | sinh ( expr )<br>
 *               | cos ( expr )<br>
 *               | cosh ( expr )<br>
 *               | tan ( expr )<br>
 *               | tanh ( expr )<br>
 *               | atan ( expr )<br>
 *               | atan2 ( exprY , exprX )<br>
 *               | hypot ( exprX , exprY )<br>
 *               | signum ( expr )<br>
 *               | rint ( expr )<br>
 *               | floor ( expr )<br>
 *               | pow[er] ( expr , expr )<br>
 *               | ceil ( expr )<br>
 *               | min ( expr1 , expr2 )<br>
 *               | max ( expr1 , expr2 )<br>
 *               | year ( expr )<br>
 *               | month ( expr )<br>
 *               | day ( expr )<br>
 *               | hour ( expr )<br>
 *               | minute ( expr )<br>
 *               | second ( expr )<br>
 *               | weekday ( expr )<br>
 *               | weeknum ( expr )<br>
 * <br>
 * # string functions<br>
 *               | substr ( expr , start [, end] )<br>
 *               | left ( expr , len )<br>
 *               | mid ( expr , start , len )<br>
 *               | right ( expr , len )<br>
 *               | rept ( expr , count )<br>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br>
 *               | lower[case] ( expr )<br>
 *               | upper[case] ( expr )<br>
 *               | trim ( expr )<br>
 *               | matches ( expr , regexp )<br>
 *               | trim ( expr )<br>
 *               | len[gth] ( str )<br>
 *               | find ( search , expr [, pos] )<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'.<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * <br>
 * A lot of the functions have been modeled after LibreOffice:<br>
 *   https://help.libreoffice.org/Calc/Functions_by_Category<br>
 * <br>
 * Additional functions:<br>
 * - env(String): String<br>
 * 	First argument is the name of the environment variable to retrieve.<br>
 * 	The result is the value of the environment variable.<br>
 * <br>
 * Additional procedures:<br>
 * - println(...)<br>
 * 	One or more arguments are printed as comma-separated list to stdout.<br>
 * 	If no argument is provided, a simple line feed is output.<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -expression &lt;string&gt;
 *  The expression to use.
 *  (default: 1.0)</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathExpressionClassifier
  extends AbstractClassifier
  implements UpdateableClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 8430850643799590721L;

  /** the expression. */
  protected MathematicalExpressionText m_Expression= new MathematicalExpressionText("1.0");

  /** the expression parser to use. */
  protected transient MathematicalExpression m_Parser;

  /**
   * Constructor.
   */
  public MathExpressionClassifier() {
    super();
    if (Environment.getEnvironmentClass() == null)
      Environment.setEnvironmentClass(Environment.class);
  }

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Simple classifier that uses a pre-defined formula that can make "
	+ "use of attribute values using their names.\n"
	+ "Grammar:\n"
	+ new MathematicalExpression().getGrammar();
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    result.addElement(new Option(
      "\tThe expression to use.\n"
        + "\t(default: 1.0)",
      "expression", 1, "-expression <string>"));

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions() {
    int       	i;
    Vector    	result;
    String[]  	options;

    result = new Vector();

    result.add("-expression");
    result.add("" + getExpression());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("expression", options);
    if (tmpStr.length() != 0)
      setExpression(new MathematicalExpressionText(tmpStr));
    else
      setExpression(new MathematicalExpressionText("1.0"));

    super.setOptions(options);
  }

  /**
   * Sets the mathematical expression to evaluate.
   *
   * @param value	the expression
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
  }

  /**
   * Returns the mathematical expression to evaluate.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The mathematical expression to evaluate.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should
   * override this method and first disable all capabilities and then
   * enable just those capabilities that make sense for the scheme.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities    result;

    result = new Capabilities(this);

    result.enable(Capability.STRING_ATTRIBUTES);
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);

    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Returns the parser to use.
   *
   * @return		the parser
   */
  protected synchronized MathematicalExpression getParser() {
    if (m_Parser == null) {
      m_Parser = new MathematicalExpression();
      m_Parser.setExpression(m_Expression.getValue());
    }

    return m_Parser;
  }

  /**
   * Builds the classifier on the training data.
   *
   * @param data	the data to use
   * @throws Exception	if training fails
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    m_Parser = getParser();
  }

  /**
   * Does nothing.
   *
   * @param instance
   * @throws Exception
   */
  @Override
  public void updateClassifier(Instance instance) throws Exception {
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Instance.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    Double    			result;
    MathematicalExpression	parser;
    List<BaseString>		values;
    int				i;
    String			name;
    int				type;

    parser = getParser();
    values = new ArrayList<>();
    for (i = 0; i < instance.numAttributes(); i++) {
      name = instance.attribute(i).name();
      type = instance.attribute(i).type();
      switch (type) {
	case Attribute.NUMERIC:
	  values.add(new BaseString(name + "=" + instance.value(i)));
	  break;
	case Attribute.DATE:
	case Attribute.NOMINAL:
	case Attribute.STRING:
	  values.add(new BaseString(name + "=" + instance.stringValue(i)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled attribute type: " + Attribute.typeToString(type));
      }
    }
    parser.setSymbols(values.toArray(new BaseString[values.size()]));
    result = getParser().evaluate();

    if (result == null)
      return Double.NaN;
    else
      return result;
  }

  /**
   * Returns the revision string.
   *
   * @return            the revision
   */
  @Override
  public String getRevision() {
    return "$Revision$";
  }

  /**
   * Returns a string representation of the built model.
   *
   * @return		the model string
   */
  @Override
  public String toString() {
    StringBuilder   result;

    result = new StringBuilder();
    result.append(getClass().getName() + "\n");
    result.append(getClass().getName().replaceAll(".", "=") + "\n");
    result.append("\n");
    result.append("Expression: " + getExpression() + "\n");

    return result.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args	the commandline parameters
   */
  public static void main(String[] args) {
    runClassifier(new MathExpressionClassifier(), args);
  }
}
