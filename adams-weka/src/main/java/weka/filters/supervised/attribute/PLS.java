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
 * PLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.supervised.attribute;

import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.core.matrix.Matrix;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;
import weka.filters.supervised.attribute.pls.AbstractPLS;
import weka.filters.supervised.attribute.pls.PLS1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the specified partial least squares (PLS) algorithm to the data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;book{Naes2002,
 *    author = {Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies},
 *    publisher = {NIR Publications},
 *    title = {A User Friendly Guide to Multivariate Calibration and Classification},
 *    year = {2002},
 *    ISBN = {0-9528666-2-5}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -algorithm &lt;value&gt;
 *  The PLS algorithm to apply.
 *  (default: weka.filters.supervised.attribute.pls.PLS1)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PLS
  extends SimpleBatchFilter
  implements SupervisedFilter, TechnicalInformationHandler {

  /** for serialization */
  static final long serialVersionUID = -3335106965521265631L;

  /** the PLS algorithm. */
  protected AbstractPLS m_Algorithm = getDefaultAlgorithm();

  /**
   * Returns a string describing this classifier.
   *
   * @return a description of the classifier suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified partial least squares (PLS) algorithm to the data.";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.BOOK);
    result.setValue(Field.AUTHOR, "Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies");
    result.setValue(Field.YEAR, "2002");
    result.setValue(Field.TITLE, "A User Friendly Guide to Multivariate Calibration and Classification");
    result.setValue(Field.PUBLISHER, "NIR Publications");
    result.setValue(Field.ISBN, "0-9528666-2-5");

    return result;
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, algorithmTipText(), getDefaultAlgorithm().getClass().getName(), "algorithm");
    WekaOptionUtils.add(result, super.listOptions());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, "algorithm", getAlgorithm());
    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options the options to use
   * @throws Exception if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setAlgorithm((AbstractPLS) WekaOptionUtils.parse(options, "algorithm", getDefaultAlgorithm()));
    super.setOptions(options);
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Returns the default algorithm.
   *
   * @return		the default
   */
  protected AbstractPLS getDefaultAlgorithm() {
    return new PLS1();
  }

  /**
   * Sets the PLS algorithm to use.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractPLS value) {
    m_Algorithm = value;
  }

  /**
   * Returns the PLS algorithm to use.
   *
   * @return 		the algorithm
   */
  public AbstractPLS getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String algorithmTipText() {
    return "The PLS algorithm to apply.";
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return m_Algorithm.determineOutputFormat(inputFormat);
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    return m_Algorithm.getCapabilities();
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    return m_Algorithm.transform(instances);
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  public String[] getMatrixNames() {
    return m_Algorithm.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  public Matrix getMatrix(String name) {
    return m_Algorithm.getMatrix(name);
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10364 $");
  }

  /**
   * runs the filter with the given arguments.
   *
   * @param args the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new PLS(), args);
  }
}
