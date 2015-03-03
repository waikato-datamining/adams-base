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
 * PartitionedStackingClassifier.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.ParallelMultipleClassifiersCombiner;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 <!-- globalinfo-start -->
 * Builds the base-classifiers on subsets of the data defined by ranges that correspond to the base-classifiers. The base-classifiers expect the class attribute to be the last attribute in the range of attributes that is defined for them.<br/>
 * The predictions of the base-classifiers and the original class attribute are used to generated a new meta-dataset that is used as input for the meta-level classifier.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -R &lt;range&gt;
 *  The attributes ranges to use for training the base-classifiers.
 *  Must be specified as often as there are base-classifiers.
 *  This is a comma separated list of attribute indices, with
 *  "first" and "last" valid values. Specify an inclusive
 *  range with "-". E.g: "first-3,5,6-10,last".</pre>
 *
 * <pre> -M &lt;classifier specification&gt;
 *  Full class name of the classifier to use for the meta-level,
 *  followed by scheme options.
 *  (default: "weka.classifiers.trees.M5P")</pre>
 *
 * <pre> -num-slots &lt;num&gt;
 *  Number of execution slots.
 *  (default 1 - i.e. no parallelism)</pre>
 *
 * <pre> -B &lt;classifier specification&gt;
 *  Full class name of classifier to include, followed
 *  by scheme options. May be specified multiple times.
 *  (default: "weka.classifiers.rules.ZeroR")</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PartitionedStacking
  extends ParallelMultipleClassifiersCombiner {

  /** for serialization. */
  private static final long serialVersionUID = -8282901622586083613L;

  /** the meta-level classifier. */
  protected Classifier m_MetaLevelClassifier = new M5P();

  /** the attribute ranges for the base-classifiers. */
  protected Range[] m_Ranges = new Range[]{new Range("first-last")};

  /** the filters for removing the unwanted attributes for the base classifiers. */
  protected Remove[] m_Remove;

  /** the header for the meta-level data. */
  protected Instances m_MetaLevelData;

  /**
   * Returns a string describing this classifier.
   *
   * @return 		a description of the classifier suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Builds the base-classifiers on subsets of the data defined by "
      + "ranges that correspond to the base-classifiers. The base-classifiers "
      + "expect the class attribute to be the last attribute in the range of "
      + "attributes that is defined for them.\n"
      + "The predictions of the base-classifiers and the original class attribute "
      + "are used to generated a new meta-dataset that is used as input for the "
      + "meta-level classifier.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
	"\tThe attributes ranges to use for training the base-classifiers.\n"
	+ "\tMust be specified as often as there are base-classifiers.\n"
	+ "\tThis is a comma separated list of attribute indices, with\n"
	+ "\t\"first\" and \"last\" valid values. Specify an inclusive \n"
	+ "\trange with \"-\". E.g: \"first-3,5,6-10,last\".",
	"R", 1, "-R <range>"));

    result.addElement(new Option(
	"\tFull class name of the classifier to use for the meta-level,\n"
	+ "\tfollowed by scheme options.\n"
	+ "\t(default: \"" + M5P.class.getName() + "\")",
	"M", 1, "-M <classifier specification>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -R &lt;range&gt;
   *  The attributes ranges to use for training the base-classifiers.
   *  Must be specified as often as there are base-classifiers.
   *  This is a comma separated list of attribute indices, with
   *  "first" and "last" valid values. Specify an inclusive
   *  range with "-". E.g: "first-3,5,6-10,last".</pre>
   *
   * <pre> -M &lt;classifier specification&gt;
   *  Full class name of the classifier to use for the meta-level,
   *  followed by scheme options.
   *  (default: "weka.classifiers.trees.M5P")</pre>
   *
   * <pre> -num-slots &lt;num&gt;
   *  Number of execution slots.
   *  (default 1 - i.e. no parallelism)</pre>
   *
   * <pre> -B &lt;classifier specification&gt;
   *  Full class name of classifier to include, followed
   *  by scheme options. May be specified multiple times.
   *  (default: "weka.classifiers.rules.ZeroR")</pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String		tmpStr;
    Vector<Range>	ranges;
    String		clsName;
    String[]		clsOptions;

    ranges = new Vector<Range>();
    while (true) {
      tmpStr = Utils.getOption('R', options);
      if (tmpStr.length() == 0)
        break;
      ranges.add(new Range(tmpStr));
    }
    if (ranges.size() == 0)
      ranges.add(new Range("first-last"));
    setRanges(ranges.toArray(new Range[ranges.size()]));

    tmpStr = Utils.getOption('M', options);
    if (tmpStr.length() != 0) {
      clsOptions    = Utils.splitOptions(tmpStr);
      clsName       = clsOptions[0];
      clsOptions[0] = "";
      setMetaLevelClassifier((Classifier) Utils.forName(Classifier.class, clsName, clsOptions));
    }
    else {
      setMetaLevelClassifier(new M5P());
    }

    super.setOptions(options);

    if (getClassifiers().length != getRanges().length)
      throw new IllegalArgumentException(
	  "Number of base-classifiers and attribute ranges don't match: "
	  + getClassifiers().length + " != " + getRanges().length);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    for (Range range: getRanges()) {
      result.add("-R");
      result.add(range.getRanges());
    }

    result.add("-M");
    result.add(Utils.toCommandLine(getMetaLevelClassifier()));

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the attribute ranges for the base-classifiers.
   *
   * @param value	the ranges
   */
  public void setRanges(Range[] value) {
    m_Ranges = value;
  }

  /**
   * Returns the attribute ranges for the base-classifiers.
   *
   * @return 		the ranges
   */
  public Range[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String rangesTipText() {
    return "The attribute ranges for base-classifiers.";
  }

  /**
   * Sets the meta-level classifier.
   *
   * @param value	the meta-level classifier
   */
  public void setMetaLevelClassifier(Classifier value) {
    m_MetaLevelClassifier = value;
  }

  /**
   * Returns the meta-level classifier.
   *
   * @return 		the meta-level classifier
   */
  public Classifier getMetaLevelClassifier() {
    return m_MetaLevelClassifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String metaLevelClassifierTipText() {
    return "The meta-level classifier to use.";
  }

  /**
   * Returns combined capabilities of the base classifiers, i.e., the
   * capabilities all of them have in common.
   *
   * @return      the capabilities of the base classifiers
   */
  public Capabilities getCapabilities() {
    Capabilities      result;

    result = super.getCapabilities();
    result.and(m_MetaLevelClassifier.getCapabilities());
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Performs blocking or notifying.
   *
   * @param tf		if true the method blocks, otherwise notifies
   */
  private synchronized void block(boolean tf) {
    if (tf) {
      try {
        wait();
      }
      catch (InterruptedException ex) {
      }
    }
    else {
      notifyAll();
    }
  }

  /**
   * Does the actual construction of the base-classifiers.
   *
   * @param data	the data to use as basis for base-classifiers
   * @throws Exception 	if something goes wrong during the training process
   */
  protected synchronized void buildClassifiers(final Instances data) throws Exception {
    int		i;

    m_Remove = new Remove[m_Classifiers.length];

    for (i = 0; i < m_Classifiers.length; i++) {
      final Classifier currentClassifier = m_Classifiers[i];
      final int iteration = i;
      m_Ranges[i].setUpper(data.numAttributes() - 1);

      Runnable newTask = new Runnable() {
	public void run() {
	  try {
	    // remove unwanted attributes
	    m_Remove[iteration] = new Remove();
	    m_Remove[iteration].setAttributeIndicesArray(m_Ranges[iteration].getSelection());
	    m_Remove[iteration].setInvertSelection(true);
	    m_Remove[iteration].setInputFormat(data);
	    Instances newData = Filter.useFilter(data, m_Remove[iteration]);

	    // train classifier
	    if (m_Debug)
	      System.out.println("Training classifier (" + (iteration +1) + ")");
	    currentClassifier.buildClassifier(newData);
	    if (m_Debug)
	      System.out.println("Finished classifier (" + (iteration +1) + ")");
	    completedClassifier(iteration, true);
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	    completedClassifier(iteration, false);
	  }
	}
      };

      // launch this task
      m_executorPool.execute(newTask);
    }

    if (m_completed + m_failed < m_Classifiers.length)
      block(true);
  }

  /**
   * Builds the classifier.
   *
   * @param data 	the training data to be used for generating the classifier
   * @throws Exception 	if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    Instances			metaData;
    Instances			subData;
    ArrayList<Attribute>	atts;
    int				i;
    int				n;

    if (m_MetaLevelClassifier == null)
      throw new IllegalArgumentException("No meta-level classifier has been set");

    if (m_numExecutionSlots < 1)
      throw new Exception("Number of execution slots needs to be >= 1!");

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    data = new Instances(data);
    data.deleteWithMissingClass();

    if (m_Debug)
      System.out.println(
	  "Starting executor pool with " + m_numExecutionSlots + " slot(s)...");
    startExecutorPool();
    m_completed = 0;
    m_failed    = 0;

    // build base-classifiers
    buildClassifiers(data);

    // build meta-dataset
    atts = new ArrayList<Attribute>();
    for (i = 0; i < m_Ranges.length; i++)
      atts.add(new Attribute(m_Ranges[i].getRanges()));
    atts.add((Attribute) data.classAttribute().copy());
    metaData = new Instances(data.relationName() + "-meta", atts, data.numInstances());
    metaData.setClassIndex(metaData.numAttributes() - 1);
    for (i = 0; i < m_Ranges.length; i++) {
      subData = Filter.useFilter(data, m_Remove[i]);
      for (n = 0; n < subData.numInstances(); n++) {
	metaData.add(new DenseInstance(metaData.numAttributes()));
	metaData.instance(n).setValue(i, m_Classifiers[i].classifyInstance(subData.instance(n)));
	metaData.instance(n).setClassValue(data.instance(n).classValue());
      }
    }

    // build meta-level classifier
    m_MetaLevelClassifier.buildClassifier(metaData);

    // store header information for predictions
    m_MetaLevelData = new Instances(metaData, 0);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Utils.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  public double classifyInstance(Instance instance) throws Exception {
    Instance	subInstance;
    Instance	metaInstance;
    double[]	values;
    int		i;

    values = new double[m_MetaLevelData.numAttributes()];
    values[m_MetaLevelData.classIndex()] = Utils.missingValue();

    for (i = 0; i < m_Ranges.length; i++) {
      m_Remove[i].input(instance);
      m_Remove[i].batchFinished();
      subInstance = m_Remove[i].output();
      values[i] = m_Classifiers[i].classifyInstance(subInstance);
    }

    metaInstance = new DenseInstance(instance.weight(), values);
    metaInstance.setDataset(m_MetaLevelData);

    return m_MetaLevelClassifier.classifyInstance(metaInstance);
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
   * Main method for running this classifier.
   *
   * @param args 	the parameters, use -h to display them
   */
  public static void main(String[] args)  {
    runClassifier(new PartitionedStacking(), args);
  }
}
