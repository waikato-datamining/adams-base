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
 * ButtonSelectorGenerator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.core.base.BaseString;
import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Generates a panel with a combobox with the labels and an 'Unset' button.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ComboBoxSelectorGenerator
  extends AbstractLabelSelectorGenerator {

  private static final long serialVersionUID = -5510358816315202316L;

  /** the labels. */
  protected BaseString[] m_Labels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a panel with a combobox with the labels and an 'Unset' button.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);
  }

  /**
   * Sets the labels to use for annotating.
   *
   * @param value 	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use for annotating.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use for annotating.";
  }

  /**
   * Generates the panel.
   *
   * @param owner 	the owning panel
   * @return		the panel, null if none generated
   */
  @Override
  public AbstractLabelSelectorPanel generate(ObjectAnnotationPanel owner) {
    ComboBoxSelectorPanel result;

    result = new ComboBoxSelectorPanel(owner, m_Labels);

    return result;
  }
}
