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
 * PropertySheetPanelPage.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.goe.PropertySheetPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Wizard page that use a {@link PropertySheetPanel} for displaying
 * the properties of an object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertySheetPanelPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** the identifier for the commandline of the object. */
  public final static String PROPERTY_CMDLINE = "Commandline";
  
  /** the parameter panel for displaying the parameters. */
  protected PropertySheetPanel m_PanelSheet;

  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;

  /** the load props button. */
  protected JButton m_ButtonLoad;

  /** the save props button. */
  protected JButton m_ButtonSave;

  /** the filechooser for loading/saving properties. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Default constructor.
   */
  public PropertySheetPanelPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public PropertySheetPanelPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();
    
    m_PanelSheet = new PropertySheetPanel();
    m_PanelSheet.setShowAboutBox(false);
    add(m_PanelSheet, BorderLayout.CENTER);

    m_PanelButtons = new JPanel(new BorderLayout());
    add(m_PanelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelButtons.add(panel, BorderLayout.WEST);

    m_ButtonLoad = new JButton(GUIHelper.getIcon("open.gif"));
    m_ButtonLoad.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	loadProperties();
      }
    });
    panel.add(m_ButtonLoad);

    m_ButtonSave = new JButton(GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	saveProperties();
      }
    });
    panel.add(m_ButtonSave);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setButtonPanelVisible(false);
  }

  /**
   * Returns the underlying property sheet panel.
   * 
   * @return		the property sheet panel
   */
  public PropertySheetPanel getParameterPanel() {
    return m_PanelSheet;
  }
  
  /**
   * Sets the object to display the properties for.
   * 
   * @param value	the object
   */
  public void setTarget(Object value) {
    m_PanelSheet.setTarget(value);
  }
  
  /**
   * Returns the current object.
   * 
   * @return		the object
   */
  public Object getTarget() {
    return m_PanelSheet.getTarget();
  }

  /**
   * Sets the properties to base the properties on.
   *
   * @param value	the properties to use
   */
  public void setProperties(Properties value) {
    String	cmdline;

    cmdline = value.getProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_PanelSheet.getTarget()));
    try {
      setTarget(OptionUtils.forAnyCommandLine(Object.class, cmdline));
    }
    catch (Exception e) {
      System.err.println("Failed to parse commandline: " + cmdline);
      e.printStackTrace();
      setTarget(m_PanelSheet.getTarget());
    }
    updateButtons();
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();
    result.setProperty(PROPERTY_CMDLINE, OptionUtils.getCommandLine(m_PanelSheet.getTarget()));
    
    return result;
  }

  /**
   * Returns the file chooser to use for loading/saving of props files.
   *
   * @return		the file chooser
   */
  protected synchronized BaseFileChooser getFileChooser() {
    FileFilter filter;

    if (m_FileChooser == null) {
      m_FileChooser = new BaseFileChooser();
      m_FileChooser.setAutoAppendExtension(true);
      filter        = ExtensionFileFilter.getPropertiesFileFilter();
      m_FileChooser.addChoosableFileFilter(filter);
      m_FileChooser.setFileFilter(filter);
    }

    return m_FileChooser;
  }

  /**
   * Loads properties from a file, prompts the user to select props file.
   */
  protected void loadProperties() {
    int		retVal;
    Properties	props;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    props = new Properties();
    if (!props.load(getFileChooser().getSelectedFile().getAbsolutePath())) {
      GUIHelper.showErrorMessage(this, "Failed to load properties from: " + getFileChooser().getSelectedFile());
      return;
    }

    setProperties(props);
  }

  /**
   * Saves properties to a file, prompts the user to select props file.
   */
  protected void saveProperties() {
    int		retVal;
    Properties	props;

    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    props = getProperties();
    if (!props.save(getFileChooser().getSelectedFile().getAbsolutePath()))
      GUIHelper.showErrorMessage(this, "Failed to save properties to: " + getFileChooser().getSelectedFile());
  }

  /**
   * Sets the visibility state of the buttons panel (load/save).
   *
   * @param value	true if to show buttons
   */
  public void setButtonPanelVisible(boolean value) {
    m_PanelButtons.setVisible(value);
  }

  /**
   * Returns the visibility state of the buttons panel (load/save).
   *
   * @return		true if buttons displayed
   */
  public boolean isButtonPanelVisible() {
    return m_PanelButtons.isVisible();
  }
}
