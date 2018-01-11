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
 * WekaExperimentGenerator.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import weka.classifiers.Classifier;
import weka.experiment.CSVResultListener;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.SplitEvaluator;

import javax.swing.DefaultListModel;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Generates an experiment setup that can be used in conjunction with the Experiment transformer actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExperimentGenerator
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-exp-type &lt;CLASSIFICATION|REGRESSION&gt; (property: experimentType)
 * &nbsp;&nbsp;&nbsp;The type of experiment to perform.
 * &nbsp;&nbsp;&nbsp;default: CLASSIFICATION
 * </pre>
 *
 * <pre>-eval-type &lt;CROSS_VALIDATION|TRAIN_TEST_SPLIT_RANDOMIZED|TRAIN_TEST_SPLIT_ORDER_PRESERVED&gt; (property: evaluationType)
 * &nbsp;&nbsp;&nbsp;The type of evaluation to perform.
 * &nbsp;&nbsp;&nbsp;default: CROSS_VALIDATION
 * </pre>
 *
 * <pre>-runs &lt;int&gt; (property: runs)
 * &nbsp;&nbsp;&nbsp;The number of runs to perform.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 *
 * <pre>-split &lt;double&gt; (property: splitPercentage)
 * &nbsp;&nbsp;&nbsp;The percentage to use in train/test splits.
 * &nbsp;&nbsp;&nbsp;default: 66.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * &nbsp;&nbsp;&nbsp;maximum: 99.9999
 * </pre>
 *
 * <pre>-result-format &lt;ARFF|CSV&gt; (property: resultFormat)
 * &nbsp;&nbsp;&nbsp;The data format the experimental results are stored in.
 * &nbsp;&nbsp;&nbsp;default: ARFF
 * </pre>
 *
 * <pre>-result-file &lt;adams.core.io.PlaceholderFile&gt; (property: resultFile)
 * &nbsp;&nbsp;&nbsp;The file to store the experimental results in.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to store the experiment setup in (the extension determines the
 * &nbsp;&nbsp;&nbsp;type).
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentGenerator
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -586416108746115363L;

  /**
   * The experiment type.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ExperimentType {
    /** classification. */
    CLASSIFICATION,
    /** regression. */
    REGRESSION
  }

  /**
   * The evaluation type.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum EvaluationType {
    /** cross-validation. */
    CROSS_VALIDATION,
    /** train/test split randomized. */
    TRAIN_TEST_SPLIT_RANDOMIZED,
    /** train/test split order preserved. */
    TRAIN_TEST_SPLIT_ORDER_PRESERVED
  }

  /**
   * The data format the experiment data is stored in.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ResultFormat {
    /** ARFF. */
    ARFF,
    /** CSV. */
    CSV
  }

  /** the type of experiment. */
  protected ExperimentType m_ExperimentType;

  /** the type of evaluation. */
  protected EvaluationType m_EvaluationType;

  /** the number of runs to perform. */
  protected int m_Runs;

  /** the number of folds to use (only cross-validation). */
  protected int m_Folds;

  /** the split-percentage to use (only train/test splits). */
  protected double m_SplitPercentage;

  /** the result format. */
  protected ResultFormat m_ResultFormat;

  /** the file to store the result in. */
  protected PlaceholderFile m_ResultFile;

  /** the file to store the experiment in. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates an experiment setup that can be used in conjunction with "
      + "the Experiment transformer actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "exp-type", "experimentType",
	    ExperimentType.CLASSIFICATION);

    m_OptionManager.add(
	    "eval-type", "evaluationType",
	    EvaluationType.CROSS_VALIDATION);

    m_OptionManager.add(
	    "runs", "runs",
	    10, 1, null);

    m_OptionManager.add(
	    "folds", "folds",
	    10, 2, null);

    m_OptionManager.add(
	    "split", "splitPercentage",
	    66.0, 0.0001, 99.9999);

    m_OptionManager.add(
	    "result-format", "resultFormat",
	    ResultFormat.ARFF);

    m_OptionManager.add(
	    "result-file", "resultFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "output", "outputFile",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "Output: ");
  }

  /**
   * Sets the type of experiment to perform.
   *
   * @param value	the type
   */
  public void setExperimentType(ExperimentType value) {
    m_ExperimentType = value;
    reset();
  }

  /**
   * Returns the type of experiment to perform.
   *
   * @return		the type
   */
  public ExperimentType getExperimentType() {
    return m_ExperimentType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String experimentTypeTipText() {
    return "The type of experiment to perform.";
  }

  /**
   * Sets the type of evaluation to perform.
   *
   * @param value	the type
   */
  public void setEvaluationType(EvaluationType value) {
    m_EvaluationType = value;
    reset();
  }

  /**
   * Returns the type of evaluation to perform.
   *
   * @return		the type
   */
  public EvaluationType getEvaluationType() {
    return m_EvaluationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String evaluationTypeTipText() {
    return "The type of evaluation to perform.";
  }

  /**
   * Sets the number of runs to perform.
   *
   * @param value	the runs
   */
  public void setRuns(int value) {
    if (value >= 1) {
      m_Runs = value;
      reset();
    }
    else {
      getLogger().severe("At least 1 run must be performed, provided: " + value);
    }
  }

  /**
   * Returns the number of runs to perform.
   *
   * @return		the runs
   */
  public int getRuns() {
    return m_Runs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runsTipText() {
    return "The number of runs to perform.";
  }

  /**
   * Sets the number of folds to use (only CV).
   *
   * @param value	the folds
   */
  public void setFolds(int value) {
    if (value > 1) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe("At least 2 folds are necessary, provided: " + value);
    }
  }

  /**
   * Returns the number of folds to use (only CV).
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in cross-validation.";
  }

  /**
   * Sets the split percentage (only train/test splits).
   *
   * @param value	the percentage (0-100)
   */
  public void setSplitPercentage(double value) {
    if ((value > 0) && (value < 100)) {
      m_SplitPercentage = value;
      reset();
    }
    else {
      getLogger().severe("Split percentage must be 0 < x < 100, provided: " + value);
    }
  }

  /**
   * Returns the split percentage (only train/test splits).
   *
   * @return		the data format
   */
  public double getSplitPercentage() {
    return m_SplitPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitPercentageTipText() {
    return "The percentage to use in train/test splits.";
  }

  /**
   * Sets the data format the results are stored in.
   *
   * @param value	the format
   */
  public void setResultFormat(ResultFormat value) {
    m_ResultFormat = value;
    reset();
  }

  /**
   * Returns the data format the results are stored in.
   *
   * @return		the format
   */
  public ResultFormat getResultFormat() {
    return m_ResultFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resultFormatTipText() {
    return "The data format the experimental results are stored in.";
  }

  /**
   * Sets the file to store the experimental results in.
   *
   * @param value 	the file
   */
  public void setResultFile(PlaceholderFile value) {
    m_ResultFile = value;
    reset();
  }

  /**
   * Returns the file to store the experimental results in.
   *
   * @return 		the file
   */
  public PlaceholderFile getResultFile() {
    return m_ResultFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resultFileTipText() {
    return "The file to store the experimental results in.";
  }

  /**
   * Sets the file to store the experiment setup in (the extensions determines the type).
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the file to store the experiment in.
   *
   * @return		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The file to store the experiment setup in (the extension determines the type).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.classifiers.Classifier[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Experiment 				exp;
    SplitEvaluator 			se;
    Classifier 				sec;
    CrossValidationResultProducer 	cvrp;
    RandomSplitResultProducer 		rsrp;
    PropertyNode[] 			propertyPath;
    DefaultListModel 			model;
    InstancesResultListener 		irl;
    CSVResultListener			crl;

    result = null;

    if (m_ResultFile.isDirectory())
      result = "Result file points to a directory: " + m_ResultFile;
    else if (m_OutputFile.isDirectory())
      result = "Output file points to a directory: " + m_OutputFile;

    if (result == null) {
      exp = new Experiment();
      exp.setPropertyArray(new Classifier[0]);
      exp.setUsePropertyIterator(true);

      // classification or regression
      se = null;
      sec = null;
      if (m_ExperimentType == ExperimentType.CLASSIFICATION) {
        se = new ClassifierSplitEvaluator();
        sec = ((ClassifierSplitEvaluator) se).getClassifier();
      }
      else if (m_ExperimentType == ExperimentType.REGRESSION) {
        se = new RegressionSplitEvaluator();
        sec = ((RegressionSplitEvaluator) se).getClassifier();
      }
      else {
        throw new IllegalStateException("Unhandled experiment type: " + m_ExperimentType);
      }

      // crossvalidation or train/test split
      if (m_EvaluationType == EvaluationType.CROSS_VALIDATION) {
        cvrp = new CrossValidationResultProducer();
        cvrp.setNumFolds(m_Folds);
        cvrp.setSplitEvaluator(se);

        propertyPath = new PropertyNode[2];
        try {
          propertyPath[0] = new PropertyNode(
            se,
            new PropertyDescriptor("splitEvaluator",
              CrossValidationResultProducer.class),
            CrossValidationResultProducer.class);
          propertyPath[1] = new PropertyNode(
            sec,
            new PropertyDescriptor("classifier",
              se.getClass()),
            se.getClass());
        }
        catch (IntrospectionException e) {
          e.printStackTrace();
        }

        exp.setResultProducer(cvrp);
        exp.setPropertyPath(propertyPath);

      }
      else if ((m_EvaluationType == EvaluationType.TRAIN_TEST_SPLIT_RANDOMIZED)
        || (m_EvaluationType == EvaluationType.TRAIN_TEST_SPLIT_ORDER_PRESERVED)) {
        rsrp = new RandomSplitResultProducer();
        rsrp.setRandomizeData(m_EvaluationType == EvaluationType.TRAIN_TEST_SPLIT_RANDOMIZED);
        rsrp.setTrainPercent(m_SplitPercentage);
        rsrp.setSplitEvaluator(se);

        propertyPath = new PropertyNode[2];
        try {
          propertyPath[0] = new PropertyNode(
            se,
            new PropertyDescriptor("splitEvaluator",
              RandomSplitResultProducer.class),
            RandomSplitResultProducer.class);
          propertyPath[1] = new PropertyNode(
            sec,
            new PropertyDescriptor("classifier",
              se.getClass()),
            se.getClass());
        }
        catch (IntrospectionException e) {
          e.printStackTrace();
        }

        exp.setResultProducer(rsrp);
        exp.setPropertyPath(propertyPath);
      }
      else {
        throw new IllegalStateException("Unhandled evaluation type: " + m_EvaluationType);
      }

      // runs
      exp.setRunLower(1);
      exp.setRunUpper(m_Runs);

      // classifier
      exp.setPropertyArray((Classifier[]) m_InputToken.getPayload());

      // datasets (empty for the template)
      model = new DefaultListModel();
      exp.setDatasets(model);

      // result
      if (m_ResultFormat == ResultFormat.ARFF) {
        irl = new InstancesResultListener();
        irl.setOutputFile(new File(m_ResultFile.getAbsolutePath()));
        exp.setResultListener(irl);
      }
      else if (m_ResultFormat == ResultFormat.CSV) {
        crl = new CSVResultListener();
        crl.setOutputFile(new File(m_ResultFile.getAbsolutePath()));
        exp.setResultListener(crl);
      }
      else {
        throw new IllegalStateException("Unhandled result format: " + m_ResultFormat);
      }

      // save template
      try {
        Experiment.write(m_OutputFile.getAbsolutePath(), exp);
      }
      catch (Exception e) {
        result = handleException("Failed to save experiment to '" + m_OutputFile + "': ", e);
      }
    }

    return result;
  }
}
