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
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
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
