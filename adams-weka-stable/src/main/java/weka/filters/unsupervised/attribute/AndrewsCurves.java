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
 * AndrewsCurves.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import gnu.trove.list.array.TDoubleArrayList;
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Generates Andrews Curves from array data.<br>
 * César Ignacio García Osorio, Colin Fyfe (2003). AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Osorio2003,
 *    author = {César Ignacio García Osorio and Colin Fyfe},
 *    title = {AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS},
 *    year = {2003},
 *    HTTP = {http://cib.uco.es/documents/Garcia03SIGEF.pdf}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -num-points &lt;int&gt;
 *  The number of points to generate (&gt; 0).
 *  (default: 100)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4521 $
 */
public class AndrewsCurves
  extends SimpleStreamFilter
  implements TechnicalInformationHandler, UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2244583826137735498L;
  
  /** the number of data points. */
  protected int m_NumPoints = 100;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates Andrews Curves from array data.\n"
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

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.YEAR, "2003");
    result.setValue(Field.AUTHOR, "César Ignacio García Osorio and Colin Fyfe");
    result.setValue(Field.TITLE, "AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS");
    result.setValue(Field.HTTP, "http://cib.uco.es/documents/Garcia03SIGEF.pdf");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tThe number of points to generate (> 0).\n"
	+ "\t(default: 100)",
	"num-points", 1, "-num-points <int>"));

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
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();

    tmpStr = Utils.getOption("num-points", options);
    if (tmpStr.length() > 0)
      setNumPoints(Integer.parseInt(tmpStr));
    else
      setNumPoints(100);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-num-points");
    result.add("" + getNumPoints());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the number of points to generate.
   *
   * @param value 	the number of points
   */
  public void setNumPoints(int value) {
    if (value > 0) {
      m_NumPoints = value;
      reset();
    }
    else {
      System.err.println(
	  "The number of points to the left must be > 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the number of points to generate.
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
    return "The number of points to generate, > 0.";
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
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
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    boolean			hasClass;

    hasClass = (inputFormat.classIndex() > -1);

    // create new attributes
    atts = new ArrayList<Attribute>();
    for (i = 0; i < m_NumPoints; i++)
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
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance		result;
    TDoubleArrayList	valuesNew;
    TDoubleArrayList	values;
    boolean		hasClass;
    int			i;
    int			n;
    double		t;
    double		y;

    hasClass = (instance.classIndex() > -1);

    values = new TDoubleArrayList();
    for (i = 0; i < instance.numAttributes(); i++) {
      if (i == instance.classIndex())
	continue;
      values.add(instance.value(i));
    }

    valuesNew = new TDoubleArrayList();
    for (i = 0; i < m_NumPoints; i++) {
      t = -Math.PI + (Math.PI * 2) / m_NumPoints * i;
      y = values.get(0) / Math.sqrt(2);
      for (n = 1; n < values.size(); n++) {
	if ((n + 1) % 2 == 0)
	  y += values.get(n) * Math.sin(t * Math.ceil(n / 2));
	else
	  y += values.get(n) * Math.cos(t * Math.ceil(n / 2));
      }
      valuesNew.add(y);
    }
    if (hasClass)
      valuesNew.add(instance.classValue());

    // create instance
    result = new DenseInstance(instance.weight(), valuesNew.toArray());
    result.setDataset(getOutputFormat());

    copyValues(result, false, instance.dataset(), getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 4521 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new AndrewsCurves(), args);
  }
}
