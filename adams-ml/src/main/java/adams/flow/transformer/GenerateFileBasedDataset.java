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
 * GenerateFileBasedDataset.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.flow.container.FileBasedDatasetContainer;
import adams.flow.control.SetContainerValue;
import adams.flow.core.Token;
import adams.flow.transformer.generatefilebaseddataset.AbstractFileBasedDatasetGeneration;
import adams.flow.transformer.generatefilebaseddataset.SimpleFileList;

/**
 <!-- globalinfo-start -->
 * Applies the specified generator to the incoming dataset container.<br>
 * <br>
 * See also:<br>
 * adams.flow.control.SetContainerValue<br>
 * adams.flow.transformer.PrepareFileBasedDataset
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.FileBasedDatasetContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.FileBasedDatasetContainer: Train, Test, Validation, Negative
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
 * &nbsp;&nbsp;&nbsp;default: GenerateFileBasedDataset
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-generation &lt;adams.flow.transformer.generatefilebaseddataset.AbstractFileBasedDatasetGeneration&gt; (property: generation)
 * &nbsp;&nbsp;&nbsp;The dataset generation scheme to apply to the container.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.generatefilebaseddataset.SimpleFileList
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenerateFileBasedDataset
  extends AbstractTransformer
  implements ClassCrossReference {

  private static final long serialVersionUID = -5135595330787325026L;

  /** the generation to use. */
  protected AbstractFileBasedDatasetGeneration m_Generation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified generator to the incoming dataset container.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{SetContainerValue.class, PrepareFileBasedDataset.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generation", "generation",
      new SimpleFileList());
  }

  /**
   * Sets the dataset generation scheme.
   *
   * @param value	the scheme
   */
  public void setGeneration(AbstractFileBasedDatasetGeneration value) {
    m_Generation = value;
    reset();
  }

  /**
   * Returns the dataset generation scheme.
   *
   * @return  		the scheme
   */
  public AbstractFileBasedDatasetGeneration getGeneration() {
    return m_Generation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generationTipText() {
    return "The dataset generation scheme to apply to the container.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generation", m_Generation);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{FileBasedDatasetContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_Generation.generates()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	output;

    result = null;

    try {
      output        = m_Generation.generate(m_InputToken.getPayload(FileBasedDatasetContainer.class));
      m_OutputToken = new Token(output);
    }
    catch (Exception e) {
      result = handleException("Failed to generate dataset!", e);
    }

    return result;
  }
}
