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
 * PrepareFileBasedDataset.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.flow.container.FileBasedDatasetContainer;
import adams.flow.control.SetContainerValue;
import adams.flow.core.Token;
import adams.flow.transformer.preparefilebaseddataset.AbstractFileBasedDatasetPreparation;
import adams.flow.transformer.preparefilebaseddataset.TrainTestSplit;

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
public class PrepareFileBasedDataset
  extends AbstractTransformer
  implements ClassCrossReference {

  private static final long serialVersionUID = -5135595330787325026L;

  /** the preparation to use. */
  protected AbstractFileBasedDatasetPreparation m_Preparation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Processes the incoming files and generates a dataset container.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{SetContainerValue.class, GenerateFileBasedDataset.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "preparation", "preparation",
      new TrainTestSplit());
  }

  /**
   * Sets the file preparation scheme.
   *
   * @param value	the scheme
   */
  public void setPreparation(AbstractFileBasedDatasetPreparation value) {
    m_Preparation = value;
    reset();
  }

  /**
   * Returns the file preparation scheme.
   *
   * @return  		the scheme
   */
  public AbstractFileBasedDatasetPreparation getPreparation() {
    return m_Preparation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preparationTipText() {
    return "The preparation scheme to apply to the files.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "preparation", m_Preparation);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{m_Preparation.accepts()};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{FileBasedDatasetContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    FileBasedDatasetContainer	cont;

    result = null;

    try {
      cont          = m_Preparation.prepare(m_InputToken.getPayload());
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to prepare files!", e);
    }

    return result;
  }
}
