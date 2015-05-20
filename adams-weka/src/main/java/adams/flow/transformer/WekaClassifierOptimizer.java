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
 * WekaClassifierOptimizer.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.meta.MultiSearch;
import weka.core.Instances;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Evaluates a classifier optimizer on an incoming dataset. The best setup (untrained) found is then forwarded.<br>
 * At the moment, only GridSearch and MultiSearch are supported as optimizers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br>
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
 * &nbsp;&nbsp;&nbsp;default: ClassifierOptimizer
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
 * <pre>-optimizer &lt;weka.classifiers.Classifier [options]&gt; (property: optimizer)
 * &nbsp;&nbsp;&nbsp;The classifier optimizer to use, eg, GridSearch or MultiSearch.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.meta.GridSearch -E CC -y-property classifier.ridge -y-min -10.0 -y-max 5.0 -y-step 1.0 -y-base 10.0 -y-expression pow(BASE,I) -filter \"weka.filters.supervised.attribute.PLSFilter -C 20 -M -A PLS1 -P center\" -x-property filter.numComponents -x-min 5.0 -x-max 20.0 -x-step 1.0 -x-base 10.0 -x-expression I -sample-size 100.0 -traversal COLUMN-WISE -log-file /home/fracpete/development/projects/adams -num-slots 1 -S 1 -W weka.classifiers.functions.LinearRegression -- -S 1 -C -R 1.0E-8
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierOptimizer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3601873599767767806L;

  /** the classifier optimizer. */
  protected weka.classifiers.Classifier m_Optimizer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a classifier optimizer on an incoming dataset. The best "
      + "setup (untrained) found is then forwarded.\n"
      + "At the moment, only GridSearch and MultiSearch are supported as "
      + "optimizers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "optimizer", "optimizer",
	    new weka.classifiers.meta.GridSearch());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The global classifier actor to cross-validate on the input data.";
  }

  /**
   * Sets the optimizer to use.
   *
   * @param value	the optimizer
   */
  public void setOptimizer(weka.classifiers.Classifier value) {
    if (    (value instanceof weka.classifiers.meta.GridSearch)
	 || (value instanceof weka.classifiers.meta.MultiSearch)) {
      m_Optimizer = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Only GridSearch and MultiSearch are currently supported, "
	  + "provided: " + value.getClass().getName() + "!");
    }
  }

  /**
   * Returns the optimizer in use.
   *
   * @return		the optimizer
   */
  public weka.classifiers.Classifier getOptimizer() {
    return m_Optimizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optimizerTipText() {
    return "The classifier optimizer to use, eg, GridSearch or MultiSearch.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String.class or weka.classifiers.Evaluation.class
   */
  public Class[] generates() {
    return new Class[]{weka.classifiers.Classifier.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instances			data;
    weka.classifiers.Classifier	cls;
    weka.classifiers.Classifier	best;

    result = null;

    try {
      // determine best classifier
      data = (Instances) m_InputToken.getPayload();
      cls  = AbstractClassifier.makeCopy(m_Optimizer);
      cls.buildClassifier(data);
      if (cls instanceof GridSearch) {
	best = new FilteredClassifier();
	((FilteredClassifier) best).setClassifier(((GridSearch) cls).getBestClassifier());
	((FilteredClassifier) best).setFilter(((GridSearch) cls).getBestFilter());
      }
      else if (cls instanceof MultiSearch) {
	best = ((MultiSearch) cls).getBestClassifier();
      }
      else {
	best   = null;
	result = "Unhandled optimizer: " + m_Optimizer.getClass().getName();
      }

      // broadcast result

      if (best != null)
	m_OutputToken = new Token(best);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to optimize: ", e);
    }

    return result;
  }
}
