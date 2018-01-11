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
 * MultiPLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.supervised.attribute;

import adams.core.base.BaseRegExp;
import adams.core.option.OptionUtils;
import adams.data.instancesanalysis.pls.AbstractPLS;
import adams.data.instancesanalysis.pls.PLS1;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.GenericPLSMatrixAccess;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * For each Y that gets identified by the regular expression for Y attributes, the specified PLS (partial least squares) algorithm gets applied to the X attributes identified by the corresponding regular expression.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -x-regexp &lt;value&gt;
 *  The regular expression to identify the X attributes for PLS.
 *  (default: amplitude-.*)</pre>
 * 
 * <pre> -y-regexp &lt;value&gt;
 *  The regular expression to identify the Y attributes for PLS.
 *  (default: A_.*)</pre>
 * 
 * <pre> -algorithm &lt;value&gt;
 *  The PLS algorithm to apply.
 *  (default: adams.data.instancesanalysis.pls.PLS1)</pre>
 * 
 * <pre> -drop-non-class-ys &lt;value&gt;
 *  If enabled, Y attributes that aren't the class attribute are removed from the output.
 *  (default: off)</pre>
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
public class MultiPLS
  extends SimpleBatchFilter
  implements SupervisedFilter, GenericPLSMatrixAccess {

  /** for serialization */
  static final long serialVersionUID = -3335106965521265631L;

  /** the regular expression for the X columns. */
  protected BaseRegExp m_XRegExp = getDefaultXRegExp();

  /** the regular expression for the Y columns. */
  protected BaseRegExp m_YRegExp = getDefaultYRegExp();

  /** the PLS algorithm. */
  protected AbstractPLS m_Algorithm = getDefaultAlgorithm();

  /** whether to keep Ys that are not the class or not. */
  protected boolean m_DropNonClassYs;

  /** the indices of the X attributes. */
  protected TIntList m_XIndices = new TIntArrayList();

  /** the indices of the Y attributes. */
  protected TIntList m_YIndices = new TIntArrayList();

  /** the indices of the other attributes. */
  protected TIntList m_OtherIndices = new TIntArrayList();

  /** the PLS algorithms corresponding to the Y attributes. */
  protected Map<String, AbstractPLS> m_PLS = new HashMap<>();

  /** the matrix names. */
  protected String[] m_MatrixNames = new String[0];

  /**
   * Returns a string describing this classifier.
   *
   * @return a description of the classifier suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
      "For each Y that gets identified by the regular expression for Y attributes, "
      + "the specified PLS (partial least squares) algorithm gets applied to the "
      + "X attributes identified by the corresponding regular expression.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, XRegExpTipText(), getDefaultXRegExp().getValue(), "x-regexp");
    WekaOptionUtils.addOption(result, YRegExpTipText(), getDefaultYRegExp().getValue(), "y-regexp");
    WekaOptionUtils.addOption(result, algorithmTipText(), getDefaultAlgorithm().getClass().getName(), "algorithm");
    WekaOptionUtils.addOption(result, dropNonClassYsTipText(), "off", "drop-non-class-ys");
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

    WekaOptionUtils.add(result, "x-regexp", getXRegExp());
    WekaOptionUtils.add(result, "y-regexp", getYRegExp());
    WekaOptionUtils.add(result, "algorithm", getAlgorithm());
    WekaOptionUtils.add(result, "drop-non-class-ys", getDropNonClassYs());
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
    setXRegExp((BaseRegExp) WekaOptionUtils.parse(options, "x-regexp", getDefaultXRegExp()));
    setYRegExp((BaseRegExp) WekaOptionUtils.parse(options, "y-regexp", getDefaultYRegExp()));
    setAlgorithm((AbstractPLS) WekaOptionUtils.parse(options, "algorithm", getDefaultAlgorithm()));
    setDropNonClassYs(Utils.getFlag("drop-non-class-ys", options));
    super.setOptions(options);
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Returns the default X regexp.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultXRegExp() {
    return new BaseRegExp("amplitude-.*");
  }

  /**
   * Sets the regular expression to identify the X attributes.
   *
   * @param value 	the expression
   */
  public void setXRegExp(BaseRegExp value) {
    m_XRegExp = value;
  }

  /**
   * Returns the regular expression to identify the X attributes.
   *
   * @return 		the expression
   */
  public BaseRegExp getXRegExp() {
    return m_XRegExp;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String XRegExpTipText() {
    return "The regular expression to identify the X attributes for PLS.";
  }

  /**
   * Returns the default Y regexp.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultYRegExp() {
    return new BaseRegExp("A_.*");
  }

  /**
   * Sets the regular expression to identify the Y attributes.
   *
   * @param value 	the expression
   */
  public void setYRegExp(BaseRegExp value) {
    m_YRegExp = value;
  }

  /**
   * Returns the regular expression to identify the Y attributes.
   *
   * @return 		the expression
   */
  public BaseRegExp getYRegExp() {
    return m_YRegExp;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String YRegExpTipText() {
    return "The regular expression to identify the Y attributes for PLS.";
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
   * Sets whether to remove Y attributes from the output that are not the
   * class attribute.
   *
   * @param value 	true if to drop
   */
  public void setDropNonClassYs(boolean value) {
    m_DropNonClassYs = value;
  }

  /**
   * Returns whether to remove Y attributes from the output that are not the
   * class attribute.
   *
   * @return 		true if to to drop
   */
  public boolean getDropNonClassYs() {
    return m_DropNonClassYs;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String dropNonClassYsTipText() {
    return "If enabled, Y attributes that aren't the class attribute are removed from the output.";
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
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				n;
    Attribute			att;
    String			name;
    String			className;
    List<String>		names;

    m_XIndices.clear();
    m_YIndices.clear();
    m_OtherIndices.clear();
    m_PLS.clear();

    // locate attributes
    className = null;
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      att = inputFormat.attribute(i);
      if (m_XRegExp.isMatch(att.name()) && att.isNumeric())
	m_XIndices.add(i);
      else if (m_YRegExp.isMatch(att.name()) && att.isNumeric())
	m_YIndices.add(i);
      else if ((i != inputFormat.classIndex()))
	m_OtherIndices.add(i);
      if (i == inputFormat.classIndex())
	className = att.name();
    }
    if (getDebug()) {
      System.out.println("X: " + m_XIndices);
      System.out.println("Y: " + m_YIndices);
      System.out.println("Other: " + m_OtherIndices);
    }

    // assemble header
    atts = new ArrayList<>();
    for (i = 0; i < m_OtherIndices.size(); i++)
      atts.add((Attribute) inputFormat.attribute(m_OtherIndices.get(i)).copy());
    for (i = 0; i < m_YIndices.size(); i++) {
      for (n = 0; n < m_Algorithm.getNumComponents(); n++) {
	name = inputFormat.attribute(m_YIndices.get(i)).name()
	  + "-" + m_Algorithm.getClass().getSimpleName() + "_" + (n+1);
	atts.add(new Attribute(name));
      }
    }
    for (i = 0; i < m_YIndices.size(); i++) {
      if (m_DropNonClassYs) {
	if (m_YIndices.get(i) == inputFormat.classIndex())
	  atts.add((Attribute) inputFormat.attribute(m_YIndices.get(i)).copy());
      }
      else {
	atts.add((Attribute) inputFormat.attribute(m_YIndices.get(i)).copy());
      }
    }
    if ((inputFormat.classIndex() > -1) && !m_YIndices.contains(inputFormat.classIndex()))
      atts.add((Attribute) inputFormat.classAttribute().copy());
    result = new Instances(inputFormat.relationName(), atts, 0);
    if (className != null)
      result.setClassIndex(result.attribute(className).index());

    // instantiate PLS
    for (i = 0; i < m_YIndices.size(); i++)
      m_PLS.put(inputFormat.attribute(m_YIndices.get(i)).name(), (AbstractPLS) OptionUtils.shallowCopy(m_Algorithm));

    // matrix names
    names = new ArrayList<>();
    for (i = 0; i < m_YIndices.size(); i++) {
      for (n = 0; n < m_Algorithm.getMatrixNames().length; n++) {
	names.add(
	  inputFormat.attribute(m_YIndices.get(i)).name()
	    + "-"
	    + m_Algorithm.getMatrixNames()[n]);
      }
    }
    m_MatrixNames = names.toArray(new String[names.size()]);

    if (getDebug())
      System.out.println("Matrix names: " + Utils.arrayToString(m_MatrixNames));

    return result;
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
    Instances			result;
    Instances			subset;
    TIntList			indices;
    int				i;
    int				n;
    int				m;
    int				index;
    Remove 			remove;
    Map<String, Instances> 	plsed;
    int				classIndex;
    String			name;
    double[]			values;

    indices    = new TIntArrayList();
    remove     = new Remove();
    plsed      = new HashMap<>();
    classIndex = instances.classIndex();
    instances.setClassIndex(-1);

    // init/apply PLS
    for (i = 0; i < m_YIndices.size(); i++) {
      name = instances.attribute(m_YIndices.get(i)).name();
      if (getDebug()) {
	if (!isFirstBatchDone())
	  System.out.println("Initializing PLS #" + (i + 1) + ": " + name);
	else
	  System.out.println("Applying PLS #" + (i + 1) + ": " + name);
      }
      indices.clear();
      indices.add(m_XIndices.toArray());
      indices.add(m_YIndices.get(i));
      remove.setAttributeIndicesArray(indices.toArray());
      remove.setInvertSelection(true);
      remove.setInputFormat(instances);
      subset = Filter.useFilter(instances, remove);
      subset.setClassIndex(subset.numAttributes() - 1);
      if (!isFirstBatchDone())
	m_PLS.get(name).determineOutputFormat(subset);
      plsed.put(name, m_PLS.get(name).transform(subset));
    }

    // assemble output
    result = getOutputFormat();
    for (n = 0; n < instances.numInstances(); n++) {
      values = new double[result.numAttributes()];

      // other
      index = 0;
      for (m = 0; m < m_OtherIndices.size(); m++) {
	if (m == classIndex)
	  continue;
	if (instances.instance(n).isMissing(m_OtherIndices.get(m)))
	  values[index] = Utils.missingValue();
	else if (result.attribute(index).isString())
	  values[index] = result.attribute(index).addStringValue(instances.instance(n).stringValue(m_OtherIndices.get(m)));
	else if (result.attribute(index).isRelationValued())
	  values[index] = result.attribute(index).addRelation(instances.instance(n).relationalValue(m_OtherIndices.get(m)));
	else
	  values[index] = instances.instance(n).value(m_OtherIndices.get(m));
	index++;
      }

      // PLS attributes (excl class)
      for (i = 0; i < m_YIndices.size(); i++) {
	name   = instances.attribute(m_YIndices.get(i)).name();
	subset = plsed.get(name);
	for (m = 0; m < subset.numAttributes(); m++) {
	  if (m == subset.classIndex())
	    continue;
	  values[index] = subset.instance(n).value(m);
	  index++;
	}
      }

      // Y attributes
      for (i = 0; i < m_YIndices.size(); i++) {
	if (m_DropNonClassYs) {
	  if (m_YIndices.get(i) == classIndex) {
	    values[index] = instances.instance(n).value(m_YIndices.get(i));
	    index++;
	  }
	}
	else {
	  values[index] = instances.instance(n).value(m_YIndices.get(i));
	  index++;
	}
      }

      // class
      if ((classIndex > -1) && !m_YIndices.contains(classIndex)) {
	if (instances.instance(n).isMissing(classIndex))
	  values[index] = Utils.missingValue();
	else if (result.attribute(index).isString())
	  values[index] = result.attribute(index).addStringValue(instances.instance(n).stringValue(classIndex));
	else if (result.attribute(index).isRelationValued())
	  values[index] = result.attribute(index).addRelation(instances.instance(n).relationalValue(classIndex));
	else
	  values[index] = instances.instance(n).value(classIndex);
      }

      // add instance
      result.add(new DenseInstance(instances.instance(n).weight(), values));
    }

    return result;
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  public String[] getMatrixNames() {
    return m_MatrixNames;
  }

  /**
   * Extracts key in the PLS map from the combined matrix name.
   *
   * @param matrixName	the matrix name to process
   * @return		the key, input if failed to extract
   */
  protected String extractPLSKey(String matrixName) {
    String	search;
    int		i;

    for (i = 0; i < m_Algorithm.getMatrixNames().length; i++) {
      search = "-" + m_Algorithm.getMatrixNames()[i];
      if (matrixName.endsWith(search))
	return matrixName.substring(0, matrixName.length() - search.length());
    }

    return matrixName;
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  public Matrix getMatrix(String name) {
    String	key;

    key = extractPLSKey(name);
    if (m_PLS.containsKey(key))
      return m_PLS.get(key).getMatrix(name.substring(key.length()));
    else
      return null;
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		always false
   * @see		#getLoadings()
   */
  public boolean hasLoadings() {
    return false;
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		always null
   */
  public Matrix getLoadings() {
    return null;
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
    runFilter(new MultiPLS(), args);
  }
}
