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
 * GPD.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.functions;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Implements Gaussian Processes for regression without hyperparameter-tuning, with an inline RBF kernel.<br/>
 * For more information see<br/>
 * <br/>
 * David J.C. Mackay (1998). Introduction to Gaussian Processes. Dept. of Physics, Cambridge University, UK.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{Mackay1998,
 *    address = {Dept. of Physics, Cambridge University, UK},
 *    author = {David J.C. Mackay},
 *    title = {Introduction to Gaussian Processes},
 *    year = {1998},
 *    PS = {http://wol.ra.phy.cam.ac.uk/mackay/gpB.ps.gz}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> -L &lt;double&gt;
 *  Level of Gaussian Noise.
 *  (default: 1.0)</pre>
 * 
 * <pre> -G &lt;double&gt;
 *  Gamma for the RBF kernel.
 *  (default: 0.01)</pre>
 * 
 * <pre> -N
 *  Whether to 0=normalize/1=standardize/2=neither.
 *  (default: 0=normalize)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author Kurt Driessens (kurtd@cs.waikato.ac.nz)
 * @author Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
 */
public class GPD
  extends AbstractClassifier
  implements WeightedInstancesHandler, OptionHandler, TechnicalInformationHandler  {

  /** for serialization */
  static final long serialVersionUID = -8620066949967678545L;

  /** The filter used to make attributes numeric. */
  protected NominalToBinary m_NominalToBinary;

  /** normalizes the data */
  public static final int FILTER_NORMALIZE = GaussianProcesses.FILTER_NORMALIZE;

  /** standardizes the data */
  public static final int FILTER_STANDARDIZE = GaussianProcesses.FILTER_STANDARDIZE;

  /** no filter */
  public static final int FILTER_NONE = GaussianProcesses.FILTER_NONE;

  /** The filter to apply to the training data */
  public static final Tag[] TAGS_FILTER = {
    new Tag(FILTER_NORMALIZE, "Normalize training data"),
    new Tag(FILTER_STANDARDIZE, "Standardize training data"),
    new Tag(FILTER_NONE, "No normalization/standardization"),
  };

  /** The filter used to standardize/normalize all values. */
  protected Filter m_Filter = null;

  /** Whether to normalize/standardize/neither */
  protected int m_filterType = FILTER_NORMALIZE;

  /** The filter used to get rid of missing values. */
  protected ReplaceMissingValues m_Missing;

  /** Turn off all checks and conversions? Turning them off assumes
   that data is purely numeric, doesn't contain any missing values,
   and has a numeric class. */
  protected boolean m_checksTurnedOff = true;

  /** Gaussian Noise Value. */
  protected double m_delta = 0.01;

  /** The class index from the training data */
  protected int m_classIndex = -1;
  protected double[][] m_data = null;
  protected double m_gamma = 0.01;

  /** The parameters of the linear transforamtion realized
   * by the filter on the class attribute */
  protected double m_Alin;
  protected double m_Blin;

  /** The number of training instances */
  protected int m_NumTrain = 0;

  /** The training data. */
  protected double m_avg_target;

  /** The vector of target values. */
  protected double[] m_t;

  /** the fallback model. */
  protected GaussianProcesses m_FallBack;

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Implements Gaussian Processes for regression "
	+ "without hyperparameter-tuning, with an inline RBF kernel.\n"
	+ "For more information see\n\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "David J.C. Mackay");
    result.setValue(Field.YEAR, "1998");
    result.setValue(Field.TITLE, "Introduction to Gaussian Processes");
    result.setValue(Field.ADDRESS, "Dept. of Physics, Cambridge University, UK");
    result.setValue(Field.PS, "http://wol.ra.phy.cam.ac.uk/mackay/gpB.ps.gz");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
      "\tLevel of Gaussian Noise.\n"
        + "\t(default: 1.0)",
      "L", 1, "-L <double>"));

    result.addElement(new Option(
      "\tGamma for the RBF kernel.\n"
        + "\t(default: 0.01)",
      "G", 1, "-G <double>"));

    result.addElement(new Option(
      "\tWhether to 0=normalize/1=standardize/2=neither.\n"
        + "\t(default: 0=normalize)",
      "N", 1, "-N"));

    Enumeration enu = super.listOptions();
    while (enu.hasMoreElements())
      result.addElement(enu.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption('L', options);
    if (tmpStr.length() != 0)
      setNoise(Double.parseDouble(tmpStr));
    else
      setNoise(1.0);

    tmpStr = Utils.getOption('G', options);
    if (tmpStr.length() != 0)
      setGamma(Double.parseDouble(tmpStr));
    else
      setGamma(0.01);

    tmpStr = Utils.getOption('N', options);
    if (tmpStr.length() != 0)
      setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
    else
      setFilterType(new SelectedTag(FILTER_NORMALIZE, TAGS_FILTER));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    int       		i;
    Vector<String>	result;
    String[]		options;

    result = new Vector<String>();

    result.add("-G");
    result.add("" + getGamma());

    result.add("-L");
    result.add("" + getNoise());

    result.add("-N");
    result.add("" + m_filterType);

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets how the training data will be transformed. Should be one of
   * FILTER_NORMALIZE, FILTER_STANDARDIZE, FILTER_NONE.
   *
   * @param newType the new filtering mode
   */
  public void setFilterType(SelectedTag newType) {
    if (newType.getTags() == TAGS_FILTER) {
      m_filterType = newType.getSelectedTag().getID();
    }
  }

  /**
   * Gets how the training data will be transformed. Will be one of
   * FILTER_NORMALIZE, FILTER_STANDARDIZE, FILTER_NONE.2200Instances
   *
   * @return the filtering mode
   */
  public SelectedTag getFilterType() {
    return new SelectedTag(m_filterType, TAGS_FILTER);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String filterTypeTipText() {
    return "Determines how/if the data will be transformed.";
  }

  /**
   * Set the level of Gaussian Noise.
   *
   * @param v  Value to assign to noise.
   */
  public void setNoise(double v) {
    m_delta = v;
  }

  /**
   * Get the value of noise.
   *
   * @return Value of noise.
   */
  public double getNoise() {
    return m_delta;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String noiseTipText() {
    return "The level of Gaussian Noise (added to the diagonal of the Covariance Matrix).";
  }

  /**
   * Set the gamma for the RBF kernel.
   *
   * @param v	the gamma
   */
  public void setGamma(double v) {
    m_gamma = v;
  }

  /**
   * Returns the gamma for the RBF kernel.
   *
   * @return    the gamma
   */
  public double getGamma() {
    return m_gamma;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String gammaTipText() {
    return "gamma.";
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attribute
    result.disableAllAttributes();
    result.disableAllAttributeDependencies();
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.disableAllClasses();
    result.disableAllClassDependencies();
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Cholesky decomposition.
   *
   * @param A		the matrix.
   * @return		the decomposition
   */
  protected double[][] choleskyDecomposition(double[][] A) {
    // Initialize.
    int n = A.length;
    double[][] L = new double[n][n];

    // Main loop.
    for (int j = 0; j < n; j++) {
      double[] Lrowj = L[j];
      double d = 0.0;
      for (int k = 0; k < j; k++) {
        double[] Lrowk = L[k];
        double s = 0.0;
        for (int i = 0; i < k; i++)
          s += Lrowk[i]*Lrowj[i];
        Lrowj[k] = s = (A[j][k] - s)/L[k][k];
        d += s*s;
      }
      d = A[j][j] - d;

      L[j][j] = 0;
      if (d > 0)
        L[j][j] = Math.sqrt(d);

      for (int k = j+1; k < n; k++) {
        L[j][k] = 0.0;
      }
    }
    return L;
  }

  /**
   * specialised to solve A * x = b, where x and b are one-dimensional
   */
  protected double[] solveChol(double[][] L, double[] b) {
    // Copy right hand side.
    int n = b.length;
    double[] X = Arrays.copyOf(b, n);

    // Solve L*Y = B;
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < k ; i++) {
        X[k] -= X[i]*L[k][i];
      }
      X[k] /= L[k][k];
    }

    // Solve L'*X = Y;
    for (int k = n-1; k >= 0; k--) {
      for (int i = k+1; i < n ; i++) {
        X[k] -= X[i]*L[i][k];
      }
      X[k] /= L[k][k];
    }
    return X;
  }

  /**
   * Computes the squared distance.
   *
   * @param x
   * @param y
   * @return
   */
  protected double squaredDistance(double[] x, double[] y) {
    int n = x.length;
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
      double delta = x[i] - y[i];
      sum += delta*delta;
    }
    return sum;
  }

  /**
   * Computes the RBF kernel.
   *
   * @param x
   * @param y
   * @param gamma
   * @return
   */
  protected double rbfKernel(double[] x, double[] y, double gamma) {
    return Math.exp(-gamma*squaredDistance(x,y));
  }

  /**
   * Method for building the classifier.
   *
   * @param insts the set of training instances
   * @throws Exception if the classifier can't be built successfully
   */
  public void buildClassifier(Instances insts) throws Exception {
    if (!m_checksTurnedOff) {
      // can classifier handle the data?
      getCapabilities().testWithFail(insts);
      // remove instances with missing class
      insts = new Instances(insts);
      insts.deleteWithMissingClass();
    }

    Instances fallbackData = insts;

    if (!m_checksTurnedOff) {
      m_Missing = new ReplaceMissingValues();
      m_Missing.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Missing);
    }
    else {
      m_Missing = null;
    }

    if (getCapabilities().handles(Capability.NUMERIC_ATTRIBUTES)) {
      boolean onlyNumeric = true;
      if (!m_checksTurnedOff) {
        for (int i = 0; i < insts.numAttributes(); i++) {
          if (i != insts.classIndex()) {
            if (!insts.attribute(i).isNumeric()) {
              onlyNumeric = false;
              break;
            }
          }
        }
      }

      if (!onlyNumeric) {
        m_NominalToBinary = new NominalToBinary();
        m_NominalToBinary.setInputFormat(insts);
        insts = Filter.useFilter(insts, m_NominalToBinary);
      }
      else {
        m_NominalToBinary = null;
      }
    }
    else {
      m_NominalToBinary = null;
    }

    m_classIndex = insts.classIndex();
    if (m_filterType == FILTER_STANDARDIZE) {
      m_Filter = new Standardize();
      ((Standardize)m_Filter).setIgnoreClass(true);
      m_Filter.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Filter);
    }
    else if (m_filterType == FILTER_NORMALIZE) {
      m_Filter = new Normalize();
      ((Normalize)m_Filter).setIgnoreClass(true);
      m_Filter.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Filter);
    }
    else {
      m_Filter = null;
    }

    m_NumTrain = insts.numInstances();

    // determine which linear transformation has been
    // applied to the class by the filter
    if (m_Filter != null) {
      Instance witness = (Instance)insts.instance(0).copy();
      witness.setValue(m_classIndex, 0);
      m_Filter.input(witness);
      m_Filter.batchFinished();
      Instance res = m_Filter.output();
      m_Blin = res.value(m_classIndex);
      witness.setValue(m_classIndex, 1);
      m_Filter.input(witness);
      m_Filter.batchFinished();
      res = m_Filter.output();
      m_Alin = res.value(m_classIndex) - m_Blin;
    }
    else {
      m_Alin = 1.0;
      m_Blin = 0.0;
    }

    // cache instances
    int n = insts.numInstances();
    int classIndex = m_classIndex;
    m_t = insts.attributeToDoubleArray(classIndex);
    m_data = new double[m_t.length][];
    for (int i = 0; i < n; i++) {
      m_data[i] = insts.instance(i).toDoubleArray();
      m_data[i][classIndex] = 0.0;
    }

    // setup up kernel matrix ...
    double[][] a = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        double kv = rbfKernel( m_data[i], m_data[j], m_gamma);
        a[i][j] = kv;
        a[j][i] = kv;
      }
    }

    double diagonalValue = 1.0+ m_delta*m_delta;
    for (int i = 0; i < a.length; i++)
      a[i][i] = diagonalValue;

    m_avg_target = Utils.sum(m_t)/m_t.length;
    for (int i = 0; i < m_t.length; i++)
      m_t[i] -= m_avg_target;

    //solve(a,m_t);
    double[][] m_chol;
    m_chol = choleskyDecomposition(a);
    m_t = solveChol(m_chol,m_t);

    // did cholesky fail? -> build GaussianProcessed model
    m_FallBack = null;
    boolean failed = false;
    for (int i = 0; i < m_t.length; i++) {
      if (Double.isNaN(m_t[i])) {
        failed = true;
        break;
      }
    }
    if (failed) {
      System.err.println("Choleskey decomposition failed, falling back on default Gaussian Processes algorithm...");
      RBFKernel kernel = new RBFKernel();
      kernel.setGamma(getGamma());
      kernel.setChecksTurnedOff(m_checksTurnedOff);
      m_FallBack = new GaussianProcesses();
      m_FallBack.setDoNotCheckCapabilities(getDoNotCheckCapabilities());
      m_FallBack.setNoise(getNoise());
      m_FallBack.setKernel(kernel);
      m_FallBack.setFilterType(new SelectedTag(m_filterType, GaussianProcesses.TAGS_FILTER));
      m_FallBack.buildClassifier(fallbackData);
    }
  }

  /**
   * Classifies a given instance.
   *
   * @param inst the instance to be classified
   * @return the classification
   * @throws Exception if instance could not be classified
   * successfully
   */
  public double classifyInstance(Instance inst) throws Exception {
    if (m_FallBack != null)
      return m_FallBack.classifyInstance(inst);

    // Filter instance
    if (!m_checksTurnedOff) {
      m_Missing.input(inst);
      m_Missing.batchFinished();
      inst = m_Missing.output();
    }

    if (m_NominalToBinary != null) {
      m_NominalToBinary.input(inst);
      m_NominalToBinary.batchFinished();
      inst = m_NominalToBinary.output();
    }

    if (m_Filter != null) {
      m_Filter.input(inst);
      m_Filter.batchFinished();
      inst = m_Filter.output();
    }

    double[] x = inst.toDoubleArray();
    x[m_classIndex] = 0.0;
    double result = m_avg_target;
    for (int i = 0; i < m_NumTrain; i++)
      result += m_t[i] * rbfKernel(x, m_data[i], m_gamma);
    result = (result - m_Blin) / m_Alin;
    return result;
  }

  /**
   * Prints out the classifier.
   *
   * @return a description of the classifier as a string
   */
  public String toString() {
    StringBuilder text = new StringBuilder();

    // fallback model?
    if (m_FallBack != null) {
      text.append("Fallback model\n\n");
      text.append(m_FallBack.toString());
      return text.toString();
    }

    if (m_t == null)
      return "Gaussian Processes: No model built yet.";

    try {
      text.append("Gaussian Processes\n\n");
      text.append("RBFKernel with gamma = " + m_gamma + "\n\n");
      text.append("Average Target Value : " + m_avg_target + "\n");
      text.append("Inverted Covariance Matrix * Target-value Vector:\n");
      double min = m_t[0];
      double max = m_t[0];
      for (int i = 0; i < m_NumTrain; i++) {
        if (m_t[i] < min) {
          min = m_t[i];
        } else if (m_t[i] > max) {
          max = m_t[i];
        }
      }
      text.append("    Lowest Value = " + min + "\n");
      text.append("    Highest Value = " + max + "\n \n");
    }
    catch (Exception e) {
      return "Can't print the classifier.";
    }

    return text.toString();
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
   * @param args the commandline parameters
   */
  public static void main(String[] args) {
    runClassifier(new GPD(), args);
  }
}
