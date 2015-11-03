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
 * WekaModelReader.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
import weka.classifiers.Classifier;
import weka.classifiers.ThreadSafeClassifier;
import weka.classifiers.meta.ThreadSafeClassifierWrapper;

/**
 <!-- globalinfo-start -->
 * Actor for loading a model (classifier or clusterer).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaModelReader
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-only-model &lt;boolean&gt; (property: outputOnlyModel)
 * &nbsp;&nbsp;&nbsp;If enabled, only the model will be output instead of a model container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-make-thread-safe &lt;boolean&gt; (property: makeThreadSafe)
 * &nbsp;&nbsp;&nbsp;If enabled, the classifier model gets wrapped in a weka.classifiers.meta.ThreadSafeClassifierWrapper 
 * &nbsp;&nbsp;&nbsp;meta-classifier.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaModelReader
  extends AbstractWekaModelReader {

  /** for serialization. */
  private static final long serialVersionUID = -1844897560777043045L;

  /** whether to wrap the model in a threadsafe wrapper. */
  protected boolean m_MakeThreadSafe;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Actor for loading a model (classifier or clusterer).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "make-thread-safe", "makeThreadSafe",
	    false);
  }

  /**
   * Sets whether to wrap classifier inside a threadsafe
   * {@link ThreadSafeClassifierWrapper} wrapper.
   *
   * @param value	true if to wrap
   */
  public void setMakeThreadSafe(boolean value) {
    m_MakeThreadSafe = value;
    reset();
  }

  /**
   * Returns whether to wrap classifier inside a threadsafe
   * {@link ThreadSafeClassifierWrapper} wrapper.
   *
   * @return		true if to wrap classifier
   */
  public boolean getMakeThreadSafe() {
    return m_MakeThreadSafe;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String makeThreadSafeTipText() {
    return "If enabled, the classifier model gets wrapped in a "
      + ThreadSafeClassifierWrapper.class.getName() + " meta-classifier.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = super.getQuickInfo();
    value  = QuickInfoHelper.toString(this, "makeThreadSafe", m_MakeThreadSafe, "thread-safe", "");
    if (value != null) {
      if (result.isEmpty())
	result = value;
      else
	result += ", " + value;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    ThreadSafeClassifierWrapper	wrapper;
    WekaModelContainer		cont;

    result = super.doExecute();

    if ((result == null) && m_MakeThreadSafe) {
      if (m_OutputOnlyModel) {
	if (!(m_OutputToken.getPayload() instanceof ThreadSafeClassifier)) {
	  if (isLoggingEnabled())
	    getLogger().info("Wrapping classifier");
	  wrapper = new ThreadSafeClassifierWrapper();
	  wrapper.setClassifier((Classifier) m_OutputToken.getPayload());
	  m_OutputToken.setPayload(wrapper);
	}
      }
      else {
	cont = (WekaModelContainer) m_OutputToken.getPayload();
 	if (!(cont.getValue(WekaModelContainer.VALUE_MODEL) instanceof ThreadSafeClassifier)) {
	  if (isLoggingEnabled())
	    getLogger().info("Wrapping classifier (in container)");
	  wrapper = new ThreadSafeClassifierWrapper();
	  wrapper.setClassifier((Classifier) cont.getValue(WekaModelContainer.VALUE_MODEL));
	  cont.setValue(WekaModelContainer.VALUE_MODEL, wrapper);
	  m_OutputToken.setPayload(cont);
	}
      }
    }

    return result;
  }
}
