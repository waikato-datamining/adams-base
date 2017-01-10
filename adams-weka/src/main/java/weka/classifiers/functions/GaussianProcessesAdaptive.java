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
 *    LinearRegression.java
 *    Copyright (C) 1999 Eibe Frank,Len Trigg
 *
 */

package weka.classifiers.functions;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.IntervalEstimator;
import weka.classifiers.functions.supportVector.CachedKernel;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Statistics;
import weka.core.Tag;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.NormalizeAdaptive;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Implements Gaussian Processes for regression without hyperparameter-tuning. For more information see<br>
 * <br>
 * David J.C. Mackay (1998). Introduction to Gaussian Processes. Dept. of Physics, Cambridge University, UK.
 * <br><br>
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
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -L &lt;double&gt;
 *  Level of Gaussian Noise. (default 0.1)</pre>
 *
 * <pre> -M &lt;double&gt;
 *  Level of Gaussian Noise for the class. (default 0.1)</pre>
 *
 * <pre> -F &lt;double&gt;
 *  Level of fDev. (default 0.0)</pre>
 *
 * <pre> -N
 *  Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</pre>
 *
 * <pre> -K &lt;classname and parameters&gt;
 *  The Kernel to use.
 *  (default: weka.classifiers.functions.supportVector.PolyKernel)</pre>
 *
 * <pre>
 * Options specific to kernel weka.classifiers.functions.supportVector.RBFKernel:
 * </pre>
 *
 * <pre> -D
 *  Enables debugging output (if available) to be printed.
 *  (default: off)</pre>
 *
 * <pre> -no-checks
 *  Turns off all checks - use with caution!
 *  (default: checks on)</pre>
 *
 * <pre> -C &lt;num&gt;
 *  The size of the cache (a prime number), 0 for full cache and
 *  -1 to turn it off.
 *  (default: 250007)</pre>
 *
 * <pre> -G &lt;num&gt;
 *  The Gamma parameter.
 *  (default: 0.01)</pre>
 *
 <!-- options-end -->
 *
 * @author Kurt Driessens (kurtd@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class GaussianProcessesAdaptive
  extends AbstractClassifier
  implements OptionHandler, IntervalEstimator, TechnicalInformationHandler, WeightedInstancesHandler {

  /** for serialization */
  static final long serialVersionUID = -8620066941167678545L;

  /** The filter used to make attributes numeric. */
  protected NominalToBinary m_NominalToBinary;

  /** normalizes the data */
  public static final int FILTER_NORMALIZE = 0;

  /** standardizes the data */
  public static final int FILTER_STANDARDIZE = 1;

  /** no filter */
  public static final int FILTER_NONE = 2;

  /** The filter to apply to the training data */
  public static final Tag[] TAGS_FILTER = { new Tag(FILTER_NORMALIZE, "Normalize training data"),
    new Tag(FILTER_STANDARDIZE, "Standardize training data"),
    new Tag(FILTER_NONE, "No normalization/standardization"), };

  /** The filter used to standardize/normalize all values. */
  protected Filter m_Filter = null;

  /** Whether to normalize/standardize/neither */
  protected int m_filterType = FILTER_NORMALIZE;

  /** The filter used to get rid of missing values. */
  protected ReplaceMissingValues m_Missing;

  /**
   * Turn off all checks and conversions? Turning them off assumes that data
   * is purely numeric, doesn't contain any missing values, and has a numeric
   * class.
   */
  protected boolean m_checksTurnedOff = true;

  /** Gaussian Noise Value. */
  protected double m_delta = 1.0;

  /** Gaussian Noise Value for class. */
  protected double m_deltaClass = 1.0;

  /** The class index from the training data */
  protected int m_classIndex = -1;

  /**
   * The parameters of the linear transforamtion realized by the filter on the
   * class attribute
   */
  protected double m_Alin;

  protected double m_Blin;

  /** Kernel to use * */
  protected Kernel m_kernel = null;

  /** The number of training instances */
  protected int m_NumTrain = 0;

  /** The training data. */
  protected double m_avg_target;

  /** (negative) covariance matrix in symmetric matrix representation **/
  public double[][] m_L;

  /** The vector of target values. */
  protected weka.core.matrix.Matrix m_t;

  /** whether the kernel is a linear one */
  protected boolean m_KernelIsLinear = false;

  /**
   * the default constructor
   */
  public GaussianProcessesAdaptive() {
    super();

    m_kernel = new RBFKernel();
    ((RBFKernel) m_kernel).setGamma(1.0);
  }

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Implements Gaussian Processes for regression "
    + "without hyperparameter-tuning. "
    + "For more information see\n\n" + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "David J.C. Mackay");
    result.setValue(Field.YEAR, "1998");
    result.setValue(Field.TITLE, "Introduction to Gaussian Processes");
    result.setValue(Field.ADDRESS, "Dept. of Physics, Cambridge University, UK");
    result.setValue(Field.PS, "http://wol.ra.phy.cam.ac.uk/mackay/gpB.ps.gz");

    return result;
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = getKernel().getCapabilities();
    result.setOwner(this);

    // attribute
    result.enableAllAttributeDependencies();
    // with NominalToBinary we can also handle nominal attributes, but only
    // if the kernel can handle numeric attributes
    if (result.handles(Capability.NUMERIC_ATTRIBUTES))
      result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.disableAllClasses();
    result.disableAllClassDependencies();
    result.disable(Capability.NO_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  public void buildClassifier(Instances insts) throws Exception {
    buildClassifier2(insts);
  } // buildClassifier

  double m_fDev  = 0;

  /**
   * Method for building the classifier.
   *
   * @param insts
   *            the set of training instances
   * @throws Exception
   *             if the classifier can't be built successfully
   */
  public void buildClassifier2(Instances insts) throws Exception {

    /* check the set of training instances */
    if (!m_checksTurnedOff) {
      // can classifier handle the data?
      getCapabilities().testWithFail(insts);

      // remove instances with missing class
      insts = new Instances(insts);
      insts.deleteWithMissingClass();
    }

    if (!m_checksTurnedOff) {
      m_Missing = new ReplaceMissingValues();
      m_Missing.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Missing);
    } else {
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
      } else {
	m_NominalToBinary = null;
      }
    } else {
      m_NominalToBinary = null;
    }

    m_classIndex = insts.classIndex();
    if (m_filterType == FILTER_STANDARDIZE) {
      m_Filter = new Standardize();
      ((Standardize)m_Filter).setIgnoreClass(true);
      m_Filter.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Filter);
    } else if (m_filterType == FILTER_NORMALIZE) {
      m_Filter = new NormalizeAdaptive();
      ((NormalizeAdaptive)m_Filter).setIgnoreClass(true);
      ((NormalizeAdaptive)m_Filter).setScale(m_fDev);
      ((NormalizeAdaptive)m_Filter).setClassIndex(insts.classIndex());
      m_Filter.setInputFormat(insts);
      insts = Filter.useFilter(insts, m_Filter);
    } else {
      m_Filter = null;
    }

    m_NumTrain = insts.numInstances();

    // determine which linear transformation has been
    // applied to the class by the filter
    if (m_Filter != null) {
      Instance witness = (Instance) insts.instance(0).copy();
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
    } else {
      m_Alin = 1.0;
      m_Blin = 0.0;
    }

    // Initialize kernel
    try {
      CachedKernel cachedKernel = (CachedKernel) m_kernel;
      cachedKernel.setCacheSize(0);
    } catch (Exception e) {
      // ignore
    }
    m_kernel.buildKernel(insts);
    m_KernelIsLinear = (m_kernel instanceof PolyKernel) && (((PolyKernel) m_kernel).getExponent() == 1.0);

    // Build Inverted Covariance Matrix

    double sum = 0.0;

    for (int i = 0; i < insts.numInstances(); i++) {
      sum += insts.instance(i).classValue();
    }
    m_avg_target = sum / insts.numInstances();

    int n = insts.numInstances();

    m_L = new double[n][];
    for (int i = 0; i < n; i++) {
      m_L[i] = new double[i+1];
    }
    // initialize kernel matrix/covariance matrix
    double kv = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
	kv = m_kernel.eval(i, j, insts.instance(i));
	m_L[i][j] = kv;
	// K[i][j] = kv;
	// K[j][i] = kv;
      }
      kv = m_kernel.eval(i, i, insts.instance(i));
      // K[i][i] = kv + (m_delta * m_delta);
      m_L[i][i] = kv + m_delta * m_delta;
    }
    //		int iClass = insts.classIndex();
    //		if (iClass >= 0) {
    //			m_L[iClass][iClass] += - m_delta * m_delta +  m_deltaClass * m_deltaClass;
    //		}

    // Calculate inverse matrix exploiting symmetry of covariance matrix
    // NB this replaces the kernel matrix with (the negative of) its inverse and does
    // not require any extra memory for a solution matrix
    double [] tmprow = new double [n];
    double tmp2 = 0, tmp = 0;
    for (int i = 0; i < n; i++) {
      // K_ii=1/K_ii
      // double tmp = -K[i][i];
      tmp = -m_L[i][i];
      // K[i][i] = 1.0 / tmp;
      m_L[i][i] = 1.0 / tmp;
      for (int j = 0; j < n; j++) {
	if (j != i) {
	  // K_ji=K_ji / K_ii for all j!=i (Column elements)
	  // K_ij = - K_ii K_ij for all j!=i (Row elements)
	  if (j < i) {
	    // K[i][j] = K[i][j] / tmp;
	    tmprow[j] = m_L[i][j];
	    m_L[i][j] /= tmp;
	    tmp2 = m_L[i][j];
	    // K[j][i] = K[i][j];
	    // K[j][j] = K[j][j] + (K[i][j] * K[i][j] * tmp);
	    m_L[j][j] += tmp2 * tmp2 * tmp;
	  } else if (j > i) {
	    // K[j][i] = K[j][i] / tmp;
	    tmprow[j] = m_L[j][i];
	    m_L[j][i] /= tmp;
	    tmp2 = m_L[j][i];
	    // K[i][j] = K[j][i];
	    // K[j][j] = K[j][j] + (K[j][i] * K[j][i] * tmp);
	    m_L[j][j] += tmp2 * tmp2 * tmp;
	  }
	}
      }

      // K_jk = K_jk - K_ji K_ik for all j=!i and k != i(other elements)
      for (int j = 0; j < n; j++) {
	if (j != i) {
	  if (i < j) {
	    for (int k = 0; k < i; k++) {
	      m_L[j][k] += tmprow[j] * m_L[i][k];
	    }
	  } else {
	    for (int k = 0; k < j; k++) {
	      m_L[j][k] += tmprow[j] * m_L[i][k];
	    }

	  }
	  for (int k = i + 1; k < j; k++) {
	    m_L[j][k] += tmprow[j] * m_L[k][i];
	  }
	}
      }
    }
    // TODO: Should we make m_L positive here or is this a waste of time? Waste of time...

    m_t = new weka.core.matrix.Matrix(insts.numInstances(), 1);
    double [] tt = new double[n];
    for (int i = 0; i < n; i++) {
      tt[i] = insts.instance(i).classValue() - m_avg_target;
    }

    // calculate m_t = tt . m_L
    for (int i = 0; i < n; i++) {
      double s = 0;
      for (int k = 0; k < i; k++) {
	s -= m_L[i][k] * tt[k];
      }
      for (int k = i; k < n; k++) {
	s -= m_L[k][i] * tt[k];
      }
      m_t.set(i, 0, s);
    }
  } // buildClassifier

  /**
   * Classifies a given instance.
   *
   * @param inst
   *            the instance to be classified
   * @return the classification
   * @throws Exception
   *             if instance could not be classified successfully
   */
  public double classifyInstance(Instance inst) throws Exception {

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

    // Build K vector

    weka.core.matrix.Matrix k = new weka.core.matrix.Matrix(m_NumTrain, 1);
    for (int i = 0; i < m_NumTrain; i++) {
      k.set(i, 0, m_kernel.eval(-1, i, inst));
    }

    double result = k.transpose().times(m_t).get(0, 0) + m_avg_target;
    result = (result - m_Blin) / m_Alin;

    return result;

  }

  /**
   * Predicts a confidence interval for the given instance and confidence
   * level.
   *
   * @param inst
   *            the instance to make the prediction for
   * @param confidenceLevel
   *            the percentage of cases the interval should cover
   * @return a 1*2 array that contains the boundaries of the interval
   * @throws Exception
   *             if interval could not be estimated successfully
   */
  public double[][] predictIntervals(Instance inst, double confidenceLevel) throws Exception {
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

    // Build K vector (and Kappa)

    weka.core.matrix.Matrix k = new weka.core.matrix.Matrix(m_NumTrain, 1);
    for (int i = 0; i < m_NumTrain; i++) {
      k.set(i, 0, m_kernel.eval(-1, i, inst));
    }

    double estimate = k.transpose().times(m_t).get(0, 0) + m_avg_target;

    double kappa = m_kernel.eval(-1, -1, inst) + m_deltaClass * m_deltaClass;
    //double kappa = k.transpose().times(k).get(0, 0)  + m_delta * m_delta;

    double s = 0; // = k.transpose().times(-m_L).times(k)
    int n = m_L.length;
    for (int i = 0; i < n; i++) {
      double t = 0;
      for (int j = 0; j < n; j++) {
	t -= k.get(j,0) * (i>j? m_L[i][j] : m_L[j][i]);
      }
      s += t * k.get(i,0);
    }

    //double sigma = Math.sqrt(kappa - k.transpose().times(m_L).times(k).get(0, 0));
    double sigma = Math.sqrt(kappa - s);

    confidenceLevel = 1.0 - ((1.0 - confidenceLevel) / 2.0);
    //confidenceLevel = 0.5 + confidenceLevel / 2.0;

    double z = Statistics.normalInverse(confidenceLevel);

    double[][] interval = new double[1][2];


    interval[0][0] = estimate - z * sigma;
    interval[0][1] = estimate + z * sigma;

    interval[0][0] = (interval[0][0] - m_Blin) / m_Alin;
    interval[0][1] = (interval[0][1] - m_Blin) / m_Alin;

    return interval;

  }

  /**
   * Gives the variance of the prediction at the given instance
   *
   * @param inst
   *            the instance to get the variance for
   * @return tha variance
   * @throws Exception
   *             if computation fails
   */
  public double getStandardDeviation(Instance inst) throws Exception {

    // Filter instance
    if (!m_checksTurnedOff) {
      m_Missing.input(inst);
      m_Missing.batchFinished();
      inst = m_Missing.output();
    }

    if (m_NominalToBinary != null) {
      m_NominalToBinary.input(inst);
      m_Alin = 1.0;
      m_Blin = 0.0;

      m_NominalToBinary.batchFinished();
      inst = m_NominalToBinary.output();
    }

    if (m_Filter != null) {
      m_Filter.input(inst);
      m_Filter.batchFinished();
      inst = m_Filter.output();
    }

    weka.core.matrix.Matrix k = new weka.core.matrix.Matrix(m_NumTrain, 1);
    for (int i = 0; i < m_NumTrain; i++) {
      k.set(i, 0, m_kernel.eval(-1, i, inst));
    }

    double kappa = m_kernel.eval(-1, -1, inst) + m_deltaClass * m_deltaClass;

    double s = 0; // = k.transpose().times(-m_L).times(k)
    int n = m_L.length;
    for (int i = 0; i < n; i++) {
      double t = 0;
      for (int j = 0; j < n; j++) {
	t -= k.get(j,0) * (i>j? m_L[i][j] : m_L[j][i]);
      }
      s += t * k.get(i,0);
    }

    //double var = kappa - k.transpose().times(m_C).times(k).get(0, 0);
    double var = kappa - s;

    if (var < 0)
      System.out.println("Aiaiai: variance is negative (" + var + ")!!!");

    double sigma = Math.sqrt(var);

    sigma = sigma / m_Alin;

    return sigma;
  }

  /** calculate log likelihood of set of instances according to the Gaussian Processes model
   * @param instances: set of instances over which to calculate log likelihood
   * @return log likelihood
   * @throws Exception
   */
  double getLogLikelihood(Instances instances, GaussianProcessesAdaptive GP) throws Exception {
    double L = 0;
    for (int i = 0; i < instances.numInstances(); i++) {
      Instance instance = instances.instance(i);
      double m = GP.classifyInstance(instance);
      double v = GP.getStandardDeviation(instance);
      double c = instance.classValue();
      //L += log(1/sqrt(2 pi)v ) - (m-c)^2/2v^2
      L += - 0.5 * Math.log(2.0 * Math.PI) - Math.log(v) - (m-c)*(m-c)/(2.0 * v * v);
    }
    return L;
  } // getikelihood

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

    result.addElement(new Option("\tLevel of Gaussian Noise." + " (default 0.1)", "L", 1, "-L <double>"));
    result.addElement(new Option("\tLevel of Gaussian Noise for the class." + " (default 0.1)", "M", 1, "-M <double>"));
    result.addElement(new Option("\tLevel of fDev." + " (default 0.0)", "F", 1, "-F <double>"));

    result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. " + "(default 0=normalize)",
	"N", 1, "-N"));

    result.addElement(new Option("\tThe Kernel to use.\n"
	+ "\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1,
    "-K <classname and parameters>"));

    result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName()
	+ ":"));

    enm = ((OptionHandler) getKernel()).listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -L &lt;double&gt;
   *  Level of Gaussian Noise. (default 0.1)</pre>
   *
   * <pre> -M &lt;double&gt;
   *  Level of Gaussian Noise for the class. (default 0.1)</pre>
   *
   * <pre> -F &lt;double&gt;
   *  Level of fDev. (default 0.0)</pre>
   *
   * <pre> -N
   *  Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</pre>
   *
   * <pre> -K &lt;classname and parameters&gt;
   *  The Kernel to use.
   *  (default: weka.classifiers.functions.supportVector.PolyKernel)</pre>
   *
   * <pre>
   * Options specific to kernel weka.classifiers.functions.supportVector.RBFKernel:
   * </pre>
   *
   * <pre> -D
   *  Enables debugging output (if available) to be printed.
   *  (default: off)</pre>
   *
   * <pre> -no-checks
   *  Turns off all checks - use with caution!
   *  (default: checks on)</pre>
   *
   * <pre> -C &lt;num&gt;
   *  The size of the cache (a prime number), 0 for full cache and
   *  -1 to turn it off.
   *  (default: 250007)</pre>
   *
   * <pre> -G &lt;num&gt;
   *  The Gamma parameter.
   *  (default: 0.01)</pre>
   *
   <!-- options-end -->
   *
   * @param options
   *            the list of options as an array of strings
   * @throws Exception
   *             if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String tmpStr;
    String[] tmpOptions;

    tmpStr = Utils.getOption('L', options);
    if (tmpStr.length() != 0)
      setNoise(Double.parseDouble(tmpStr));
    else
      setNoise(0.1);

    tmpStr = Utils.getOption('M', options);
    if (tmpStr.length() != 0)
      setClassNoise(Double.parseDouble(tmpStr));
    else
      setClassNoise(0.1);

    tmpStr = Utils.getOption('F', options);
    if (tmpStr.length() != 0)
      setDev(Double.parseDouble(tmpStr));
    else
      setDev(0.0);


    tmpStr = Utils.getOption('N', options);
    if (tmpStr.length() != 0)
      setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
    else
      setFilterType(new SelectedTag(FILTER_NORMALIZE, TAGS_FILTER));

    tmpStr = Utils.getOption('K', options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr = tmpOptions[0];
      tmpOptions[0] = "";
      setKernel(Kernel.forName(tmpStr, tmpOptions));
    }

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    int i;
    Vector result;
    String[] options;

    result = new Vector();
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    result.add("-L");
    result.add("" + getNoise());

    result.add("-M");
    result.add("" + getClassNoise());

    result.add("-F");
    result.add("" + getDev());

    result.add("-N");
    result.add("" + m_filterType);

    result.add("-K");
    result.add("" + m_kernel.getClass().getName() + " " + Utils.joinOptions(m_kernel.getOptions()));

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String kernelTipText() {
    return "The kernel to use.";
  }

  /**
   * Gets the kernel to use.
   *
   * @return the kernel
   */
  public Kernel getKernel() {
    return m_kernel;
  }

  /**
   * Sets the kernel to use.
   *
   * @param value
   *            the new kernel
   */
  public void setKernel(Kernel value) {
    m_kernel = value;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String filterTypeTipText() {
    return "Determines how/if the data will be transformed.";
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
   * Sets how the training data will be transformed. Should be one of
   * FILTER_NORMALIZE, FILTER_STANDARDIZE, FILTER_NONE.
   *
   * @param newType
   *            the new filtering mode
   */
  public void setFilterType(SelectedTag newType) {

    if (newType.getTags() == TAGS_FILTER) {
      m_filterType = newType.getSelectedTag().getID();
    }
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String noiseTipText() {
    return "The level of Gaussian Noise (added to the diagonal of the Covariance Matrix).";
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
   * Set the level of Gaussian Noise.
   *
   * @param v
   *            Value to assign to noise.
   */
  public void setNoise(double v) {
    m_delta = v;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String devTipText() {
    return "dev";
  }

  public void setDev(double v) {
    m_fDev = v;
  }

  public double getDev() {
    return m_fDev;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String classNoiseTipText() {
    return "The level of class noise.";
  }

  /**
   * Get the value of noise for the class.
   *
   * @return Value of noise.
   */
  public double getClassNoise() {
    return m_deltaClass;
  }

  /**
   * Set the level of Gaussian Noise for the class.
   *
   * @param v
   *            Value to assign to noise.
   */
  public void setClassNoise(double v) {
    m_deltaClass = v;
  }

  /**
   * Prints out the classifier.
   *
   * @return a description of the classifier as a string
   */
  public String toString() {

    StringBuffer text = new StringBuffer();

    if (m_t == null)
      return "Gaussian Processes: No model built yet.";

    try {

      text.append("Gaussian Processes\n\n");
      text.append("Kernel used:\n  " + m_kernel.toString() + "\n\n");

      text.append("Average Target Value : " + m_avg_target + "\n");

      text.append("Inverted Covariance Matrix:\n");
      double min = -m_L[0][0];
      double max = -m_L[0][0];
      for (int i = 0; i < m_NumTrain; i++)
	for (int j = 0; j <= i; j++) {
	  if (-m_L[i][j] < min)
	    min = -m_L[i][j];
	  else if (-m_L[i][j] > max)
	    max = -m_L[i][j];
	}
      text.append("    Lowest Value = " + min + "\n");
      text.append("    Highest Value = " + max + "\n");
      text.append("Inverted Covariance Matrix * Target-value Vector:\n");
      min = m_t.get(0, 0);
      max = m_t.get(0, 0);
      for (int i = 0; i < m_NumTrain; i++) {
	if (m_t.get(i, 0) < min)
	  min = m_t.get(i, 0);
	else if (m_t.get(i, 0) > max)
	  max = m_t.get(i, 0);
      }
      text.append("    Lowest Value = " + min + "\n");
      text.append("    Highest Value = " + max + "\n \n");
      text.append("Dev = "  + getDev());
    } catch (Exception e) {
      return "Can't print the classifier.";
    }

    return text.toString();
  }

  /**
   * Main method for testing this class.
   *
   * @param argv
   *            the commandline parameters
   */
  public static void main(String[] argv) {
    runClassifier(new GaussianProcessesAdaptive(), argv);
  }

  public String getRevision() {
    return "$Revision$";
  }
}
