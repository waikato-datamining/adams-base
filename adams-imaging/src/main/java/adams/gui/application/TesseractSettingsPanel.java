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
 * TesseractSettingsPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;

import javax.swing.JComboBox;

import adams.core.TesseractHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.TesseractLanguage;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ParameterPanel;

/**
 * Panel for configuring the tesseract settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TesseractSettingsPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the chooser for the executable. */
  protected FileChooserPanel m_PanelExecutable;
  
  /** the combobox with all the languages. */
  protected JComboBox m_ComboBoxLanguages;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.NORTH);

    m_PanelExecutable = new FileChooserPanel();
    m_PanelParameters.addParameter("_Executable", m_PanelExecutable);
    
    m_ComboBoxLanguages = new JComboBox(TesseractLanguage.values());
    m_PanelParameters.addParameter("_Language", m_ComboBoxLanguages);

    // display values
    load();
  }

  /**
   * Loads the values from the props file and displays them.
   */
  protected void load() {
    TesseractHelper	helper;

    helper = TesseractHelper.getSingleton();
    helper.reload();

    m_PanelExecutable.setCurrent(new PlaceholderFile(helper.getExecutable()));
    m_ComboBoxLanguages.setSelectedItem(helper.getLanguage());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Tesseract";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return true;
  }
  
  /**
   * Activates the proxy settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    boolean		result;    
    TesseractHelper	helper;

    helper = TesseractHelper.getSingleton();

    helper.setExecutable(m_PanelExecutable.getCurrent().getAbsolutePath());
    
    if (m_ComboBoxLanguages.getSelectedIndex() > -1)
      helper.setLanguage((TesseractLanguage) m_ComboBoxLanguages.getSelectedItem());
    else
      helper.setLanguage(TesseractLanguage.ENGLISH);

    result = helper.save();
    
    if (result)
      return null;
    else
      return "Failed to save tesseract setup!";
  }
}
