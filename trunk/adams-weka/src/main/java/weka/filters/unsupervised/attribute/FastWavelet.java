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
 * FastWavelet.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
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
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;
import JSci.maths.wavelet.FWT;
import JSci.maths.wavelet.cdf2_4.FastCDF2_4;
import JSci.maths.wavelet.daubechies2.FastDaubechies2;
import JSci.maths.wavelet.haar.FastHaar;
import JSci.maths.wavelet.symmlet8.FastSymmlet8;
import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * A filter for wavelet transformation using the JSci library's fast wavelet transform (FWT) algorithms.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 *  (2009). JSci - A science API for Java.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    title = {JSci - A science API for Java},
 *    year = {2009},
 *    HTTP = {http://jsci.sourceforge.net/}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 * <pre> -A &lt;HAAR|CDF2_4|DAUBECHIES2|SYMMLET8&gt;
 *  The algorithm to use.
 *  (default: HAAR)</pre>
 *
 * <pre> -P &lt;ZERO&gt;
 *  The padding to use.
 *  (default: ZERO)</pre>
 *
 * <pre> -F &lt;filter specification&gt;
 *  The filter to use as preprocessing step (classname and options).
 *  (default: MultiFilter with ReplaceMissingValues and Normalize)</pre>
 *
 * <pre> -inverse
 *  Whether to perform the inverse transform (from wavelet space into
 *  normal space again).
 *  (default: off)</pre>
 *
 * <pre>
 * Options specific to filter weka.filters.MultiFilter ('-F'):
 * </pre>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 * <pre> -F &lt;classname [options]&gt;
 *  A filter to apply (can be specified multiple times).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FastWavelet
  extends SimpleBatchFilter
  implements TechnicalInformationHandler, UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8596728919861340618L;

  /** the type of algorithm: Haar. */
  public static final int ALGORITHM_HAAR = 0;
  /** the type of algorithm: CDF2 4. */
  public static final int ALGORITHM_CDF2_4 = 1;
  /** the type of algorithm: Daubechies2. */
  public static final int ALGORITHM_DAUBECHIES2 = 2;
  /** the type of algorithm: Symmlet8. */
  public static final int ALGORITHM_SYMMLET8 = 3;
  /** the types of algorithm. */
  public static final Tag[] TAGS_ALGORITHM = {
    new Tag(ALGORITHM_HAAR, "HAAR", "Haar"),
    new Tag(ALGORITHM_CDF2_4, "CDF2_4", "CDF2 4"),
    new Tag(ALGORITHM_DAUBECHIES2, "DAUBECHIES2", "Daubechies2"),
    new Tag(ALGORITHM_SYMMLET8, "SYMMLET8", "Symmlet8")
  };

  /** the type of padding: Zero padding. */
  public static final int PADDING_ZERO = 0;
  /** the types of padding. */
  public static final Tag[] TAGS_PADDING = {
    new Tag(PADDING_ZERO, "ZERO", "Zero")
  };

  /** an optional filter for preprocessing of the data. */
  protected Filter m_Filter = null;

  /** the type of algorithm. */
  protected int m_Algorithm = ALGORITHM_HAAR;

  /** the type of padding. */
  protected int m_Padding = PADDING_ZERO;

  /** whether to perform inverse transformation. */
  protected boolean m_InverseTransform = false;

  /**
   * default constructor.
   */
  public FastWavelet() {
    super();

    m_Filter = new MultiFilter();
    ((MultiFilter) m_Filter).setFilters(
	new Filter[]{
	    new weka.filters.unsupervised.attribute.ReplaceMissingValues(),
	    new weka.filters.unsupervised.attribute.Normalize()});
  }

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "A filter for wavelet transformation using the JSci library's fast "
      + "wavelet transform (FWT) algorithms.\n\n"
      + "For more information see:\n\n"
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
    result.setValue(Field.YEAR, "2009");
    result.setValue(Field.TITLE, "JSci - A science API for Java");
    result.setValue(Field.HTTP, "http://jsci.sourceforge.net/");

    return result;
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector		result;
    Enumeration		enm;
    String		param;
    SelectedTag		tag;
    int			i;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    param = "";
    for (i = 0; i < TAGS_ALGORITHM.length; i++) {
      if (i > 0)
	param += "|";
      tag = new SelectedTag(TAGS_ALGORITHM[i].getID(), TAGS_ALGORITHM);
      param += tag.getSelectedTag().getIDStr();
    }
    result.addElement(new Option(
	"\tThe algorithm to use.\n"
	+ "\t(default: " + new SelectedTag(ALGORITHM_HAAR, TAGS_ALGORITHM) + ")",
	"A", 1, "-A <" + param + ">"));

    param = "";
    for (i = 0; i < TAGS_PADDING.length; i++) {
      if (i > 0)
	param += "|";
      tag = new SelectedTag(TAGS_PADDING[i].getID(), TAGS_PADDING);
      param += tag.getSelectedTag().getIDStr();
    }
    result.addElement(new Option(
	"\tThe padding to use.\n"
	+ "\t(default: " + new SelectedTag(PADDING_ZERO, TAGS_PADDING) + ")",
	"P", 1, "-P <" + param + ">"));

    result.addElement(new Option(
	"\tThe filter to use as preprocessing step (classname and options).\n"
	+ "\t(default: MultiFilter with ReplaceMissingValues and Normalize)",
	"F", 1, "-F <filter specification>"));

    result.addElement(new Option(
	"\tWhether to perform the inverse transform (from wavelet space into\n"
	+ "\tnormal space again).\n"
	+ "\t(default: off)",
	"inverse", 0, "-inverse"));

    if (getFilter() instanceof OptionHandler) {
      result.addElement(new Option(
	  "",
	  "", 0, "\nOptions specific to filter "
	  + getFilter().getClass().getName() + " ('-F'):"));

      enm = ((OptionHandler) getFilter()).listOptions();
      while (enm.hasMoreElements())
	result.addElement(enm.nextElement());
    }

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  public String[] getOptions() {
    int       i;
    Vector    result;
    String[]  options;

    result = new Vector();
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    result.add("-A");
    result.add("" + getAlgorithm().getSelectedTag().getReadable());

    result.add("-P");
    result.add("" + getPadding().getSelectedTag().getReadable());

    result.add("-F");
    if (getFilter() instanceof OptionHandler)
      result.add(
	  getFilter().getClass().getName()
	+ " "
	+ Utils.joinOptions(((OptionHandler) getFilter()).getOptions()));
    else
      result.add(
	  getFilter().getClass().getName());

    if (getInverseTransform())
      result.add("-inverse");

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   *
   * <pre> -A &lt;HAAR|CDF2_4|DAUBECHIES2|SYMMLET8&gt;
   *  The algorithm to use.
   *  (default: HAAR)</pre>
   *
   * <pre> -P &lt;ZERO&gt;
   *  The padding to use.
   *  (default: ZERO)</pre>
   *
   * <pre> -F &lt;filter specification&gt;
   *  The filter to use as preprocessing step (classname and options).
   *  (default: MultiFilter with ReplaceMissingValues and Normalize)</pre>
   *
   * <pre> -inverse
   *  Whether to perform the inverse transform (from wavelet space into
   *  normal space again).
   *  (default: off)</pre>
   *
   * <pre>
   * Options specific to filter weka.filters.MultiFilter ('-F'):
   * </pre>
   *
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   *
   * <pre> -F &lt;classname [options]&gt;
   *  A filter to apply (can be specified multiple times).</pre>
   *
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String[]	tmpOptions;
    Filter	filter;

    super.setOptions(options);

    tmpStr = Utils.getOption("A", options);
    if (tmpStr.length() != 0)
      setAlgorithm(new SelectedTag(tmpStr, TAGS_ALGORITHM));
    else
      setAlgorithm(new SelectedTag(ALGORITHM_HAAR, TAGS_ALGORITHM));

    tmpStr = Utils.getOption("P", options);
    if (tmpStr.length() != 0)
      setPadding(new SelectedTag(tmpStr, TAGS_PADDING));
    else
      setPadding(new SelectedTag(PADDING_ZERO, TAGS_PADDING));

    tmpStr     = Utils.getOption("F", options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setFilter((Filter) OptionUtils.forName(Filter.class, tmpStr, tmpOptions));
    }
    else {
      filter = new MultiFilter();
      ((MultiFilter) filter).setFilters(
	  new Filter[]{
	      new weka.filters.unsupervised.attribute.ReplaceMissingValues(),
	      new weka.filters.unsupervised.attribute.Normalize()});
      setFilter(filter);
    }

    setInverseTransform(Utils.getFlag("inverse", options));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String filterTipText() {
    return "The preprocessing filter to use.";
  }

  /**
   * Set the preprocessing filter (only used for setup).
   *
   * @param value	the preprocessing filter.
   */
  public void setFilter(Filter value) {
    m_Filter = value;
  }

  /**
   * Get the preprocessing filter.
   *
   * @return 		the preprocessing filter
   */
  public Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String algorithmTipText() {
    return "Sets the type of algorithm to use.";
  }

  /**
   * Sets the type of algorithm to use.
   *
   * @param value 	the algorithm type
   */
  public void setAlgorithm(SelectedTag value) {
    if (value.getTags() == TAGS_ALGORITHM) {
      m_Algorithm = value.getSelectedTag().getID();
    }
  }

  /**
   * Gets the type of algorithm to use.
   *
   * @return 		the current algorithm type.
   */
  public SelectedTag getAlgorithm() {
    return new SelectedTag(m_Algorithm, TAGS_ALGORITHM);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String paddingTipText() {
    return "Sets the type of padding to use.";
  }

  /**
   * Sets the type of Padding to use.
   *
   * @param value 	the Padding type
   */
  public void setPadding(SelectedTag value) {
    if (value.getTags() == TAGS_PADDING) {
      m_Padding = value.getSelectedTag().getID();
    }
  }

  /**
   * Gets the type of Padding to use.
   *
   * @return 		the current Padding type.
   */
  public SelectedTag getPadding() {
    return new SelectedTag(m_Padding, TAGS_PADDING);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inverseTransformTipText() {
    return "If true, performs inverse transform (wavelet -> normal space).";
  }

  /**
   * Sets whether to use the inverse tranform.
   *
   * @param value 	true if to use inverse transform
   */
  public void setInverseTransform(boolean value) {
    m_InverseTransform = value;
  }

  /**
   * Gets whether to use the inverse transform.
   *
   * @return 		true if inverse transform is used
   */
  public boolean getInverseTransform() {
    return m_InverseTransform;
  }

  /**
   * returns the next bigger number that's a power of 2. If the number is
   * already a power of 2 then this will be returned. The number will be at
   * least 2^2..
   *
   * @param n		the number to start from
   * @return		the next bigger number
   */
  protected static int nextPowerOf2(int n) {
    int		exp;

    exp = (int) StrictMath.ceil(StrictMath.log(n) / StrictMath.log(2.0));
    exp = StrictMath.max(2, exp);

    return (int) StrictMath.pow(2, exp);
  }

  /**
   * pads the data to conform to the necessary number of attributes.
   *
   * @param data	the data to pad
   * @return		the padded data
   */
  protected Instances pad(Instances data) {
    Instances 			result;
    int 			i;
    int				n;
    String 			prefix;
    int				numAtts;
    boolean			isLast;
    int				index;
    Vector<Integer>		padded;
    int[]			indices;
    ArrayList<Attribute>	atts;

    // determine number of padding attributes
    switch (m_Padding) {
      case PADDING_ZERO:
	if (data.classIndex() > -1)
	  numAtts = (nextPowerOf2(data.numAttributes() - 1) + 1) - data.numAttributes();
	else
	  numAtts = nextPowerOf2(data.numAttributes()) - data.numAttributes();
	break;

      default:
	throw new IllegalStateException(
	    "Padding " + new SelectedTag(m_Algorithm, TAGS_PADDING)
	    + " not implemented!");
    }

    result = new Instances(data);
    prefix = getAlgorithm().getSelectedTag().getReadable();

    // any padding necessary?
    if (numAtts > 0) {
      // add padding attributes
      isLast = (data.classIndex() == data.numAttributes() - 1);
      padded = new Vector<Integer>();
      for (i = 0; i < numAtts; i++) {
	if (isLast)
	  index = result.numAttributes() - 1;
	else
	  index = result.numAttributes();

	result.insertAttributeAt(
	    new Attribute(prefix + "_padding_" + (i+1)),
	    index);

	// record index
	padded.add(new Integer(index));
      }

      // get padded indices
      indices = new int[padded.size()];
      for (i = 0; i < padded.size(); i++)
	indices[i] = padded.get(i);

      // determine number of padding attributes
      switch (m_Padding) {
	case PADDING_ZERO:
	  for (i = 0; i < result.numInstances(); i++) {
	    for (n = 0; n < indices.length; n++)
	      result.instance(i).setValue(indices[n], 0);
	  }
	  break;
      }
    }

    // rename all attributes apart from class
    data = result;
    atts = new ArrayList<Attribute>();
    n = 0;
    for (i = 0; i < data.numAttributes(); i++) {
      n++;
      if (i == data.classIndex())
	atts.add((Attribute) data.attribute(i).copy());
      else
	atts.add(new Attribute(prefix + "_" + n));
    }

    // create new dataset
    result = new Instances(data.relationName(), atts, data.numInstances());
    result.setClassIndex(data.classIndex());
    for (i = 0; i < data.numInstances(); i++)
      result.add(new DenseInstance(1.0, data.instance(i).toDoubleArray()));

    return result;
  }

  /**
   * Renames the attributes when using the inverse transform.
   *
   * @param data	the data to transform.
   * @return		the transformed data
   */
  protected Instances inverse(Instances data) {
    Instances			result;
    String			prefix;
    ArrayList<Attribute>	atts;
    int				i;
    int				n;

    data   = new Instances(data);
    atts   = new ArrayList<Attribute>();
    prefix = getAlgorithm().getSelectedTag().getReadable() + "-inv";

    n = 0;
    for (i = 0; i < data.numAttributes(); i++) {
      n++;
      if (i == data.classIndex())
	atts.add((Attribute) data.attribute(i).copy());
      else
	atts.add(new Attribute(prefix + "_" + n));
    }

    result = new Instances(data.relationName(), atts, data.numInstances());
    for (i = 0; i < data.numInstances(); i++)
      result.add(data.instance(i));

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   * @see   #hasImmediateOutputFormat()
   * @see   #batchFinished()
   */
  protected Instances determineOutputFormat(Instances inputFormat)
    throws Exception {

    if (m_InverseTransform)
      return inverse(new Instances(inputFormat, 0));
    else
      return pad(new Instances(inputFormat, 0));
  }

  /**
   * processes the instances using the HAAR/JSci algorithm.
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   * @see		JSci.maths.wavelet.haar.FastHaar
   */
  protected Instances processData(Instances instances) throws Exception {
    Instances	result;
    int		i;
    int		clsIdx;
    double[]	newVal;
    double[]	clsVal;
    Attribute	clsAtt;
    FWT		fwt;

    // prepare data (pad and remove class attribute)
    clsIdx  = instances.classIndex();
    clsVal  = null;
    clsAtt  = null;
    if (clsIdx > -1) {
      clsVal  = instances.attributeToDoubleArray(clsIdx);
      clsAtt  = (Attribute) instances.classAttribute().copy();
      instances.setClassIndex(-1);
      instances.deleteAttributeAt(clsIdx);
    }
    if (m_InverseTransform)
      instances = inverse(instances);
    else
      instances = pad(instances);
    result = new Instances(instances, 0);

    // set up algorithm
    switch (m_Algorithm) {
      case ALGORITHM_HAAR:
	fwt = new FastHaar();
	break;
      case ALGORITHM_CDF2_4:
	fwt = new FastCDF2_4();
	break;
      case ALGORITHM_DAUBECHIES2:
	fwt = new FastDaubechies2();
	break;
      case ALGORITHM_SYMMLET8:
	fwt = new FastSymmlet8();
	break;
      default:
	throw new IllegalStateException("Unhandled algorithm type: " + m_Algorithm);
    }

    // transform data
    for (i = 0; i < instances.numInstances(); i++) {
      newVal = instances.instance(i).toDoubleArray();
      float[] vals = new float[newVal.length];
      for (int n = 0; n < newVal.length; n++)
	vals[n] = (float) newVal[n];
      if (m_InverseTransform)
	fwt.invTransform(vals);
      else
	fwt.transform(vals);
      for (int n = 0; n < newVal.length; n++)
	newVal[n] = vals[n];
      result.add(new DenseInstance(1, newVal));
    }

    // add class again
    if (clsIdx > -1) {
      result.insertAttributeAt(clsAtt, result.numAttributes());
      result.setClassIndex(result.numAttributes() - 1);
      for (i = 0; i < clsVal.length; i++) {
	if (Utils.isMissingValue(clsVal[i])) {
	  result.instance(i).setClassMissing();
	  continue;
	}

	if (result.classAttribute().isString())
	  result.instance(i).setClassValue(result.classAttribute().addStringValue(clsAtt.value((int) clsVal[i])));
	else if (result.classAttribute().isRelationValued())
	  result.instance(i).setClassValue(result.classAttribute().addRelation(clsAtt.relation((int) clsVal[i])));
	else
	  result.instance(i).setClassValue(clsVal[i]);
      }
    }

    return result;
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attribute
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   * @see               #batchFinished()
   */
  protected Instances process(Instances instances) throws Exception {
    if (!isFirstBatchDone())
      m_Filter.setInputFormat(instances);
    instances = Filter.useFilter(instances, m_Filter);

    return processData(instances);
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
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new FastWavelet(), args);
  }
}

