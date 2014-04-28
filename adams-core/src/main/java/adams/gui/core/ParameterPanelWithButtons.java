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
 * ParameterPanelWithButtons.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Component;

import adams.gui.chooser.AbstractChooserPanel;

/**
 * Panel that offers associated buttons on the right-hand side.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ParameterPanelWithButtons
  extends BasePanelWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = 2480939317042703826L;

  /** the parameter panel. */
  protected ParameterPanel m_PanelParameters;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);
  }

  /**
   * Removes all parameters.
   */
  public void clearParameters() {
    m_PanelParameters.clearParameters();
  }

  /**
   * Adds the label and component as new row at the end.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   */
  public void addParameter(String label, Component comp) {
    m_PanelParameters.addParameter(label, comp);
  }

  /**
   * Adds the chooser panel at the end.
   *
   * @param chooser	the chooser panel to add
   */
  public void addParameter(AbstractChooserPanel chooser) {
    m_PanelParameters.addParameter(chooser);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @param index	the row index to insert the label/editfield at
   */
  public void addParameter(int index, String label, Component comp) {
    m_PanelParameters.addParameter(index, label, comp);
  }

  /**
   * Inserts the chooser panel as new row at the specified row.
   *
   * @param chooser	the chooser panel to insert
   * @param index	the row index to insert the label/editfield at
   */
  public void addParameter(int index, AbstractChooserPanel chooser) {
    m_PanelParameters.addParameter(index, chooser);
  }

  /**
   * Removes the parameter at the specified location.
   *
   * @param index	the row index
   */
  public void removeParameter(int index) {
    m_PanelParameters.removeParameter(index);
  }

  /**
   * Returns the parameter component at the specified location.
   *
   * @param index	the row index
   * @return		the requested component
   */
  public Component getParameter(int index) {
    return m_PanelParameters.getParameter(index);
  }
}
