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
 * SavitzkyGolay.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * A filter that applies Savitzky-Golay smoothing.<br/>
 * If a class attribute is present this will not be touched and moved to the end.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 * A. Savitzky, Marcel J.E. Golay (1964). Smoothing and Differentiation of Data by Simplified Least Squares Procedures. Analytical Chemistry. 36:1627-1639.<br/>
 * <br/>
 * William H. Press, Saul A. Teukolsky, William T. Vetterling, Brian P. Flannery (1992). Savitzky-Golay Smoothing Filters.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Savitzky1964,
 *    author = {A. Savitzky and Marcel J.E. Golay},
 *    journal = {Analytical Chemistry},
 *    pages = {1627-1639},
 *    title = {Smoothing and Differentiation of Data by Simplified Least Squares Procedures},
 *    volume = {36},
 *    year = {1964},
 *    HTTP = {http://dx.doi.org/10.1021/ac60214a047}
 * }
 *
 * &#64;inbook{Press1992,
 *    author = {William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery},
 *    chapter = {14.8},
 *    edition = {Second},
 *    pages = {650-655},
 *    publisher = {Cambridge University Press},
 *    series = {Numerical Recipes in C},
 *    title = {Savitzky-Golay Smoothing Filters},
 *    year = {1992},
 *    PDF = {http://www.nrbook.com/a/bookcpdf/c14-8.pdf}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -left &lt;int&gt;
 *  The number of points to the left (&gt;= 0).
 *  (default: 3)</pre>
 *
 * <pre> -right &lt;int&gt;
 *  The number of points to the right (&gt;= 0).
 *  (default: 3)</pre>
 *
 * <pre> -polynomial &lt;int&gt;
 *  The polynomial order (&gt;= 2).
 *  (default: 2)</pre>
 *
 * <pre> -derivative &lt;int&gt;
 *  The order of the derivative (&gt;= 0).
 *  (default: 1)</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4521 $
 */
public class SavitzkyGolay2
  extends SimpleStreamFilter
  implements TechnicalInformationHandler, UnsupervisedFilter {

  /** for serialization. */
 

  /**
	 * 
	 */
	private static final long serialVersionUID = 6860559375223030676L;

/** the polynomial order. */
  protected int m_PolynomialOrder = 2;

  /** the order of the derivative. */
  protected int m_DerivativeOrder = 1;

  /** the number of points to the left of a data point. */
  protected int m_NumPoints = 3;

  /** the number of points to the right of a data point. */
  //protected int m_NumPointsRight = 3;

  /** the calculated coefficients. */
  protected double[] m_Coefficients;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "A filter that applies Savitzky-Golay smoothing.\n"
      + "If a class attribute is present this will not be touched and moved to the end.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    TechnicalInformation 	additional;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "A. Savitzky and Marcel J.E. Golay");
    result.setValue(Field.TITLE, "Smoothing and Differentiation of Data by Simplified Least Squares Procedures");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "36");
    result.setValue(Field.PAGES, "1627-1639");
    result.setValue(Field.YEAR, "1964");
    result.setValue(Field.HTTP, "http://dx.doi.org/10.1021/ac60214a047");

    additional = result.add(Type.INBOOK);
    additional.setValue(Field.AUTHOR, "William H. Press and Saul A. Teukolsky and William T. Vetterling and Brian P. Flannery");
    additional.setValue(Field.SERIES, "Numerical Recipes in C");
    additional.setValue(Field.EDITION, "Second");
    additional.setValue(Field.TITLE, "Savitzky-Golay Smoothing Filters");
    additional.setValue(Field.CHAPTER, "14.8");
    additional.setValue(Field.PAGES, "650-655");
    additional.setValue(Field.YEAR, "1992");
    additional.setValue(Field.PUBLISHER, "Cambridge University Press");
    additional.setValue(Field.PDF, "http://www.nrbook.com/a/bookcpdf/c14-8.pdf");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tThe number of points to the left (>= 0).\n"
	+ "\t(default: 3)",
	"left", 1, "-left <int>"));

    result.addElement(new Option(
	"\tThe number of points to the right (>= 0).\n"
	+ "\t(default: 3)",
	"right", 1, "-right <int>"));

    result.addElement(new Option(
	"\tThe polynomial order (>= 2).\n"
	+ "\t(default: 2)",
	"polynomial", 1, "-polynomial <int>"));

    result.addElement(new Option(
	"\tThe order of the derivative (>= 0).\n"
	+ "\t(default: 1)",
	"derivative", 1, "-derivative <int>"));

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   * @see    		#reset()
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();

    tmpStr = Utils.getOption("points", options);
    if (tmpStr.length() > 0)
      setNumPoints(Integer.parseInt(tmpStr));
    else
      setNumPoints(3);

   

    tmpStr = Utils.getOption("polynomial", options);
    if (tmpStr.length() > 0)
      setPolynomialOrder(Integer.parseInt(tmpStr));
    else
      setPolynomialOrder(2);

    tmpStr = Utils.getOption("derivative", options);
    if (tmpStr.length() > 0)
      setDerivativeOrder(Integer.parseInt(tmpStr));
    else
      setDerivativeOrder(1);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-points");
    result.add("" + getNumPoints());


    result.add("-polynomial");
    result.add("" + getPolynomialOrder());

    result.add("-derivative");
    result.add("" + getDerivativeOrder());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Resets the filter.
   */
  protected void reset() {
    super.reset();

    m_Coefficients = null;
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setPolynomialOrder(int value) {
    if (value >= 2) {
      m_PolynomialOrder = value;
      reset();
    }
    else {
      System.err.println(
	  "The polynomial order must be at least 2 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
   */
  public int getPolynomialOrder() {
    return m_PolynomialOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polynomialOrderTipText() {
    return "The polynomial order to use, must be at least 2.";
  }

  /**
   * Sets the order of the derivative.
   *
   * @param value 	the order
   */
  public void setDerivativeOrder(int value) {
    if (value >= 0) {
      m_DerivativeOrder = value;
      reset();
    }
    else {
      System.err.println(
	  "The order of the derivative must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the order of the derivative.
   *
   * @return 		the order
   */
  public int getDerivativeOrder() {
    return m_DerivativeOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String derivativeOrderTipText() {
    return "The order of the derivative to use, >= 0.";
  }

  /**
   * Sets the number of points to the left of a data point.
   *
   * @param value 	the number of points
   */
  public void setNumPoints(int value) {
    if (value >= 0) {
      m_NumPoints = value;
      reset();
    }
    else {
      System.err.println(
	  "The number of points to the left must be at least 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to the left of a data point.
   *
   * @return 		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of points left of a data point, >= 0.";
  }

  

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.enable(Capability.NUMERIC_ATTRIBUTES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called
   * from batchFinished() after the call of preprocess(Instances), in which,
   * e.g., statistics for the actual processing step can be gathered.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				count;
    boolean			hasClass;

    hasClass = (inputFormat.classIndex() > -1);

    // determine number of attributes
    count = inputFormat.numAttributes();
    if (hasClass)
      count--;
    count -= m_NumPoints + m_NumPoints + 1;

    // create new attributes
    atts = new ArrayList<Attribute>();
    for (i = 0; i < count; i++)
      atts.add(new Attribute("att" + (i+1)));

    // add class attribute (if present)
    if (hasClass)
      atts.add((Attribute) inputFormat.classAttribute().copy());

    // create new dataset
    result = new Instances(inputFormat.relationName(), atts, 0);
    if (hasClass)
      result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	valuesOld;
    double[]	values;
    boolean	hasClass;
    int		count;
    int		width;
    int		i;
    int		n;

    if (m_Coefficients == null)
      m_Coefficients = adams.data.utils.SavitzkyGolay.determineCoefficients(m_NumPoints, m_NumPoints, m_PolynomialOrder, m_DerivativeOrder);

    hasClass = (instance.classIndex() > -1);
    count    = instance.numAttributes();
    if (hasClass)
      count--;

    // get original values
    valuesOld = new double[count];
    n         = 0;
    for (i = 0; i < instance.numAttributes(); i++) {
      if (i == instance.classIndex())
	continue;
      valuesOld[n] = instance.value(i);
      n++;
    }

    // smooth values
    width = m_NumPoints + m_NumPoints + 1;
    if (hasClass)
      values = new double[count - width + 1];
    else
      values = new double[count - width];
    for (i = 0; i <= count - width; i++) {
      values[i] = 0;
      for (n = 0; n < width; n++)
	values[i] += m_Coefficients[n] * valuesOld[i + n];
    }

    // add class value
    if (hasClass)
      values[values.length - 1] = instance.classValue();

    // create instance
    result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 4521 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new SavitzkyGolay(), args);
  }
}
