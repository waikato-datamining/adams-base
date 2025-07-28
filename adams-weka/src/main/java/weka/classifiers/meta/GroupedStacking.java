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
 *    GroupedStacking.java
 *    Copyright (C) 1999-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.meta;

import adams.core.ObjectCopyHelper;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.classifiers.Classifier;
import weka.classifiers.CrossValidationFoldGenerator;
import weka.classifiers.DefaultCrossValidationFoldGenerator;
import weka.classifiers.ParallelMultipleClassifiersCombiner;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.CommandlineRunnable;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Combines several classifiers using the stacking method. Can do classification or regression.<br>
 * <br>
 * Uses the specified fold generator for generating the fold pairs for the meta-level.<br>
 * <br>
 * For more information, see<br>
 * <br>
 * David H. Wolpert (1992). Stacked generalization. Neural Networks. 5:241-259.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Wolpert1992,
 *    author = {David H. Wolpert},
 *    journal = {Neural Networks},
 *    pages = {241-259},
 *    publisher = {Pergamon Press},
 *    title = {Stacked generalization},
 *    volume = {5},
 *    year = {1992}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -M &lt;scheme specification&gt;
 *  Full name of meta classifier, followed by options.
 *  (default: "weka.classifiers.rules.Zero")</pre>
 *
 * <pre> -generator &lt;classname and options&gt;
 *  Sets the cross-validation fold generator to use.
 *  (default: weka.classifiers.DefaultCrossValidationFoldGenerator)</pre>
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
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
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
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 * <pre>
 * Options specific to meta classifier weka.classifiers.rules.ZeroR:
 * </pre>
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
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 */
public class GroupedStacking
  extends ParallelMultipleClassifiersCombiner
  implements TechnicalInformationHandler {

  /** for serialization */
  private static final long serialVersionUID = 5134738557155845452L;

  /** The meta classifier */
  protected Classifier m_MetaClassifier = new ZeroR();

  /** Format for meta data */
  protected Instances m_MetaFormat = null;

  /** Format for base data */
  protected Instances m_BaseFormat = null;

  /** the fold generator. */
  protected CrossValidationFoldGenerator m_Generator = new DefaultCrossValidationFoldGenerator();

  /** the actual fold generator in use. */
  protected CrossValidationFoldGenerator m_ActualGenerator;

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {

    return "Combines several classifiers using the stacking method. "
	     + "Can do classification or regression.\n\n"
	     + "Uses the specified fold generator for generating the fold pairs for the meta-level.\n\n"
	     + "For more information, see\n\n"
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

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "David H. Wolpert");
    result.setValue(Field.YEAR, "1992");
    result.setValue(Field.TITLE, "Stacked generalization");
    result.setValue(Field.JOURNAL, "Neural Networks");
    result.setValue(Field.VOLUME, "5");
    result.setValue(Field.PAGES, "241-259");
    result.setValue(Field.PUBLISHER, "Pergamon Press");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    result.addElement(new Option(
      "\tFull name of meta classifier, followed by options.\n" +
	"\t(default: \"weka.classifiers.rules.Zero\")",
      "M", 0, "-M <scheme specification>"));
    result.addElement(new Option(
      "\tSets the cross-validation fold generator to use.\n"
	+ "\t(default: " + OptionUtils.getCommandLine(new DefaultCrossValidationFoldGenerator()) + ")",
      "generator", 1, "-generator <classname and options>"));

    result.addAll(Collections.list(super.listOptions()));

    if (getMetaClassifier() instanceof OptionHandler) {
      result.addElement(new Option(
	"",
	"", 0, "\nOptions specific to meta classifier "
		 + getMetaClassifier().getClass().getName() + ":"));
      result.addAll(Collections.list(((OptionHandler)getMetaClassifier()).listOptions()));
    }
    return result.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p>
   *
   * <pre> -M &lt;scheme specification&gt;
   *  Full name of meta classifier, followed by options.
   *  (default: "weka.classifiers.rules.Zero")</pre>
   *
   * <pre> -generator &lt;classname and options&gt;
   *  Sets the cross-validation fold generator to use.
   *  (default: weka.classifiers.DefaultCrossValidationFoldGenerator)</pre>
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
   * <pre> -batch-size
   *  The desired batch size for batch prediction  (default 100).</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.rules.ZeroR:
   * </pre>
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
   * <pre> -batch-size
   *  The desired batch size for batch prediction  (default 100).</pre>
   *
   * <pre>
   * Options specific to meta classifier weka.classifiers.rules.ZeroR:
   * </pre>
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
   * <pre> -batch-size
   *  The desired batch size for batch prediction  (default 100).</pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	value;

    value = Utils.getOption("generator", options);
    if (!value.isEmpty())
      setGenerator((CrossValidationFoldGenerator) OptionUtils.forCommandLine(CrossValidationFoldGenerator.class, value));
    else
      setGenerator(new DefaultCrossValidationFoldGenerator());

    value = Utils.getOption("M", options);
    if (!value.isEmpty())
      setMetaClassifier((Classifier) OptionUtils.forAnyCommandLine(Classifier.class, value));
    else
      setMetaClassifier(new ZeroR());

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<>();

    result.add("-generator");
    result.add(OptionUtils.getCommandLine(getGenerator()));

    result.add("-M");
    result.add(OptionUtils.getCommandLine(getMetaClassifier()));

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String generatorTipText() {
    return "The cross-validation generator to use.";
  }

  /**
   * Gets the cross-validation fold generator to use.
   *
   * @return the generator
   */
  public CrossValidationFoldGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Sets the cross-validation fold generator to use.
   *
   * @param value the generator
   */
  public void setGenerator(CrossValidationFoldGenerator value) {
    m_Generator = value;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String metaClassifierTipText() {
    return "The meta classifiers to be used.";
  }

  /**
   * Adds meta classifier
   *
   * @param classifier the classifier with all options set.
   */
  public void setMetaClassifier(Classifier classifier) {
    m_MetaClassifier = classifier;
  }

  /**
   * Gets the meta classifier.
   *
   * @return the meta classifier
   */
  public Classifier getMetaClassifier() {
    return m_MetaClassifier;
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
    if (getGenerator().getNumFolds() > 0)
      result.setMinimumNumberInstances(getGenerator().getNumFolds());
    else
      result.setMinimumNumberInstances(1);

    return result;
  }

  /**
   * Buildclassifier selects a classifier from the set of classifiers
   * by minimising error on the training data.
   *
   * @param data the training data to be used for generating the
   * boosted classifier.
   * @throws Exception if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    if (m_MetaClassifier == null)
      throw new IllegalArgumentException("No meta classifier has been set");

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    Instances newData = new Instances(data);
    m_BaseFormat = new Instances(data, 0);
    newData.deleteWithMissingClass();

    m_ActualGenerator = ObjectCopyHelper.copyObject(m_Generator);
    m_ActualGenerator.setData(newData);

    // Create meta level
    generateMetaLevel(newData);

    // restart the executor pool because at the end of processing
    // a set of classifiers it gets shutdown to prevent the program
    // executing as a server
    super.buildClassifier(newData);

    // Rebuild all the base classifiers on the full training data
    buildClassifiers(newData);
  }

  /**
   * Generates the meta data
   *
   * @param newData the data to work on
   * @throws Exception if generation fails
   */
  protected void generateMetaLevel(Instances newData) throws Exception {
    Instances 			metaData;
    WekaTrainTestSetContainer	cont;
    Instances			train;
    Instances			test;

    metaData = metaFormat(newData);
    m_MetaFormat = new Instances(metaData, 0);
    m_ActualGenerator.initializeIterator();
    while (m_ActualGenerator.hasNext()) {
      cont  = m_ActualGenerator.next();
      train = cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN, Instances.class);
      test  = cont.getValue(WekaTrainTestSetContainer.VALUE_TEST,  Instances.class);

      // start the executor pool (if necessary)
      // has to be done after each set of classifiers as the
      // executor pool gets shut down in order to prevent the
      // program executing as a server (and not returning to
      // the command prompt when run from the command line
      super.buildClassifier(train);

      // construct the actual classifiers
      buildClassifiers(train);

      // Classify test instances and add to meta data
      for (int i = 0; i < test.numInstances(); i++) {
	metaData.add(metaInstance(test.instance(i)));
      }
    }

    m_MetaClassifier.buildClassifier(metaData);
  }

  /**
   * Returns class probabilities.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_MetaClassifier.distributionForInstance(metaInstance(instance));
  }

  /**
   * Output a representation of this classifier
   *
   * @return a string representation of the classifier
   */
  public String toString() {
    if (m_Classifiers.length == 0)
      return "Stacking: No base schemes entered.";
    if (m_MetaClassifier == null)
      return "Stacking: No meta scheme selected.";
    if (m_MetaFormat == null)
      return "Stacking: No model built yet.";
    String result = "Stacking\n\nBase classifiers\n\n";
    for (int i = 0; i < m_Classifiers.length; i++)
      result += getClassifier(i).toString() +"\n\n";

    result += "\n\nMeta classifier\n\n";
    result += m_MetaClassifier.toString();

    return result;
  }

  /**
   * Makes the format for the level-1 data.
   *
   * @param instances the level-0 format
   * @return the format for the meta data
   * @throws Exception if the format generation fails
   */
  protected Instances metaFormat(Instances instances) throws Exception {
    ArrayList<Attribute> attributes = new ArrayList<>();
    Instances metaFormat;

    for (int k = 0; k < m_Classifiers.length; k++) {
      Classifier classifier = getClassifier(k);
      String name = classifier.getClass().getName() + "-" + (k+1);
      if (m_BaseFormat.classAttribute().isNumeric()) {
	attributes.add(new Attribute(name));
      } else {
	for (int j = 0; j < m_BaseFormat.classAttribute().numValues(); j++) {
	  attributes.add(
	    new Attribute(
	      name + ":" + m_BaseFormat.classAttribute().value(j)));
	}
      }
    }
    attributes.add((Attribute) m_BaseFormat.classAttribute().copy());
    metaFormat = new Instances("Meta format", attributes, 0);
    metaFormat.setClassIndex(metaFormat.numAttributes() - 1);
    return metaFormat;
  }

  /**
   * Makes a level-1 instance from the given instance.
   *
   * @param instance the instance to be transformed
   * @return the level-1 instance
   * @throws Exception if the instance generation fails
   */
  protected Instance metaInstance(Instance instance) throws Exception {
    double[] values = new double[m_MetaFormat.numAttributes()];
    Instance metaInstance;
    int i = 0;
    for (int k = 0; k < m_Classifiers.length; k++) {
      Classifier classifier = getClassifier(k);
      if (m_BaseFormat.classAttribute().isNumeric()) {
	values[i++] = classifier.classifyInstance(instance);
      } else {
	double[] dist = classifier.distributionForInstance(instance);
	for (int j = 0; j < dist.length; j++) {
	  values[i++] = dist[j];
	}
      }
    }
    values[i] = instance.classValue();
    metaInstance = new DenseInstance(1, values);
    metaInstance.setDataset(m_MetaFormat);
    return metaInstance;
  }

  @Override
  public void preExecution() throws Exception {
    super.preExecution();
    if (getMetaClassifier() instanceof CommandlineRunnable) {
      ((CommandlineRunnable) getMetaClassifier()).preExecution();
    }
  }

  @Override
  public void postExecution() throws Exception {
    super.postExecution();
    if (getMetaClassifier() instanceof CommandlineRunnable) {
      ((CommandlineRunnable) getMetaClassifier()).postExecution();
    }
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain the following arguments:
   * -t training file [-T test file] [-c class index]
   */
  public static void main(String[] args) {
    runClassifier(new GroupedStacking(), args);
  }
}
