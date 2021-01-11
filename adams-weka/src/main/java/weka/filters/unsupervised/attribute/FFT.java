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
 * FFT.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import JSci.maths.Complex;
import JSci.maths.FourierMath;
import adams.data.padding.PaddingHelper;
import adams.data.padding.PaddingType;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * A filter that transforms the data with Fast Fourier Transform.<br>
 * <br>
 * Pads with zeroes.<br>
 * For more information see:<br>
 * <br>
 * Mark Hale (2009). JSci - A science API for Java.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{Hale2009,
 *    author = {Mark Hale},
 *    title = {JSci - A science API for Java},
 *    year = {2009},
 *    HTTP = {http://jsci.sourceforge.net/}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -inverse
 *  Whether to compute the inverse.
 *  (default: no)</pre>
 *
 * <pre> -real
 *  Whether to return imaginary or real part is returned.
 *  (default: imaginary)</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FFT
  extends SimpleStreamFilter
  implements TechnicalInformationHandler, UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2244583826137735498L;

  /** the type of padding to use. */
  protected PaddingType m_PaddingType;

  /** whether to perform inverse transformation (wavelet -&gt; normal space). */
  protected boolean m_InverseTransform;

  /** whether to return complex or real part of the transformation. */
  protected boolean m_Real;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "A filter that transforms the data with Fast Fourier Transform.\n\n"
        + "Pads with zeroes.\n"
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
    TechnicalInformation result;

    result = new TechnicalInformation(TechnicalInformation.Type.MISC);
    result.setValue(TechnicalInformation.Field.AUTHOR, "Mark Hale");
    result.setValue(TechnicalInformation.Field.YEAR, "2009");
    result.setValue(TechnicalInformation.Field.TITLE, "JSci - A science API for Java");
    result.setValue(TechnicalInformation.Field.HTTP, "http://jsci.sourceforge.net/");

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
	"\tWhether to compute the inverse.\n"
	+ "\t(default: no)",
	"inverse", 0, "-inverse"));

    result.addElement(new Option(
	"\tWhether to return imaginary or real part is returned.\n"
	+ "\t(default: imaginary)",
	"real", 0, "-real"));

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
    reset();

    setInverseTransform(Utils.getFlag("inverse", options));

    setReal(Utils.getFlag("real", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<>();

    if (m_InverseTransform)
      result.add("-inverse");

    if (m_Real)
      result.add("-real");

    return result.toArray(new String[0]);
  }

  /**
   * Sets whether to compute inverse.
   *
   * @param value 	true if inverse
   */
  public void setInverseTransform(boolean value) {
    m_InverseTransform = value;
    reset();
  }

  /**
   * Returns whether to compute inverse.
   *
   * @return 		true if inverse
   */
  public boolean getInverseTransform() {
    return m_InverseTransform;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inverseTransformTipText() {
    return "If enabled, the inverse transform is computed.";
  }

  /**
   * Sets whether to return real or imaginary part.
   *
   * @param value 	true if real
   */
  public void setReal(boolean value) {
    m_Real = value;
    reset();
  }

  /**
   * Returns whether to return real or imaginary part.
   *
   * @return 		true if real
   */
  public boolean getReal() {
    return m_Real;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String realTipText() {
    return "If enabled, the real part instead of imaginary one is returned.";
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
    count = PaddingHelper.nextPowerOf2(count);

    // create new attributes
    atts = new ArrayList<>();
    for (i = 0; i < count; i++)
      atts.add(new Attribute((m_Real ? "real" : "img") + (i+1)));

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
    Complex[]	transformed;
    boolean	hasClass;
    int		count;
    int		i;
    int		n;

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

    // pad
    valuesOld = PaddingHelper.padPow2(valuesOld, PaddingType.ZERO);

    // transform
    if (m_InverseTransform)
      transformed = FourierMath.inverseTransform(valuesOld);
    else
      transformed = FourierMath.transform(valuesOld);

    // generate output
    values = new double[valuesOld.length + (hasClass ? 1 : 0)];
    for (i = 0; i < transformed.length; i++) {
      if (m_Real)
	values[i] = transformed[i].real();
      else
	values[i] = transformed[i].imag();
    }

    // add class value
    if (hasClass) {
      if (instance.classIsMissing())
        values[values.length - 1] = Utils.missingValue();
      else
        values[values.length - 1] = instance.classValue();
    }

    // create instance
    result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());

    copyValues(result, false, instance.dataset(), getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new FFT(), args);
  }
}
