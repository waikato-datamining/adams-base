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
 * WizardPane.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

/**
 * Ancestor for wizard panes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWizardPane
  extends BasePanel
  implements LoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 887135856139374858L;

  /** the action for cancelling the wizard. */
  public final static String ACTION_CANCEL = "Cancel";

  /** the action for finishing the wizard. */
  public final static String ACTION_FINISH = "Finish";

  /** the logger to use. */
  protected Logger m_Logger;

  /** the ID of the wizard. */
  protected String m_ID;

  /** the pages lookup. */
  protected Map<String, AbstractWizardPage> m_PageLookup;

  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;

  /** the button for the previous page. */
  protected BaseButton m_ButtonBack;

  /** the button for the next page. */
  protected BaseButton m_ButtonNext;

  /** the button for the cancelling/finishing. */
  protected BaseButton m_ButtonCancelFinish;

  /** the action listeners (ie hitting cancel/finish). */
  protected HashSet<ActionListener> m_ActionListeners;

  /** the custom text for the "finish" button. */
  protected String m_CustomFinishText;

  /** the panel for the properties buttons. */
  protected JPanel m_PanelButtonsProperties;

  /** the load props button. */
  protected BaseButton m_ButtonLoad;

  /** the save props button. */
  protected BaseButton m_ButtonSave;

  /** the filechooser for loading/saving properties. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the wizard with no ID.
   */
  public AbstractWizardPane() {
    this("");
  }

  /**
   * Initializes the wizard.
   *
   * @param id		the ID of the wizard, used for logging purposes
   */
  public AbstractWizardPane(String id) {
    super();

    m_ID     = id;
    m_Logger = null;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PageLookup       = new HashMap<>();
    m_ActionListeners  = new HashSet<>();
    m_CustomFinishText = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();
    
    setLayout(new BorderLayout());

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.SOUTH);

    m_PanelButtonsProperties = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(m_PanelButtonsProperties, BorderLayout.WEST);
    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(m_PanelButtons, BorderLayout.EAST);
    
    m_ButtonBack = new BaseButton("Back");
    m_ButtonBack.addActionListener((ActionEvent e) -> previousPage());
    m_PanelButtons.add(m_ButtonBack);
    
    m_ButtonNext = new BaseButton("Next");
    m_ButtonNext.addActionListener((ActionEvent e) -> nextPage());
    m_PanelButtons.add(m_ButtonNext);
    
    m_ButtonCancelFinish = new BaseButton("");
    m_ButtonCancelFinish.addActionListener((ActionEvent e) -> cancelFinish());
    m_PanelButtons.add(m_ButtonCancelFinish);

    m_ButtonLoad = new BaseButton(GUIHelper.getIcon("open.gif"));
    m_ButtonLoad.addActionListener((ActionEvent e) -> loadProperties());
    m_PanelButtonsProperties.add(m_ButtonLoad);

    m_ButtonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> saveProperties());
    m_PanelButtonsProperties.add(m_ButtonSave);
  }
  
  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    updateButtons();
  }

  /**
   * Goes to the previous page.
   */
  protected abstract void previousPage();

  /**
   * Goes to the next page.
   */
  protected abstract void nextPage();

  /**
   * Gets called when the cancel/finish button gets clicked.
   */
  protected void cancelFinish() {
    String 	action;

    if (m_ButtonCancelFinish.getText().equals(ACTION_CANCEL))
      action = ACTION_CANCEL;
    else
      action = ACTION_FINISH;

    notifyActionListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
  }

  /**
   * Returns the ID of the wizard, if any.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Removes all pages.
   */
  public void removeAllPages() {
    m_PageLookup.clear();
  }
  
  /**
   * Adds the page under the given name.
   * 
   * @param page	the page
   */
  public void addPage(AbstractWizardPage page) {
    page.setOwner(this);
    m_PageLookup.put(page.getPageName(), page);
    updateButtons();
  }

  /**
   * Returns the currently active page.
   * 
   * @return		the page, null if not available
   */
  public abstract AbstractWizardPage getSelectedPage();

  /**
   * Sets the properties of all the pages.
   *
   * @param props	the combined properties
   * @param usePrefix	whether to use the page name as prefix
   */
  public void setProperties(Properties props, boolean usePrefix) {
    Properties		sub;
    AbstractWizardPage	page;

    for (String name: m_PageLookup.keySet()) {
      page = m_PageLookup.get(name);
      if (usePrefix) {
	sub = props.subset(page.getPageName() + ".");
	for (String key: sub.keySetAll()) {
	  sub.setProperty(key.substring(page.getPageName().length() + 1), sub.getProperty(key));
	  sub.removeKey(key);
	}
	page.setProperties(sub);
      }
      else {
	page.setProperties(props.getClone());
      }
    }
  }

  /**
   * Returns the properties from all the pages.
   * 
   * @param usePrefix	whether to use the page name as prefix
   * @return		the combined properties
   */
  public Properties getProperties(boolean usePrefix) {
    Properties		result;
    Properties		sub;
    AbstractWizardPage	page;
    
    result = new Properties();
    for (String name: m_PageLookup.keySet()) {
      page = m_PageLookup.get(name);
      sub  = page.getProperties();
      if (usePrefix)
	result.add(sub, page.getPageName() + ".");
      else
	result.add(sub);
    }
    
    return result;
  }
  
  /**
   * Updates the status of the buttons.
   */
  public abstract void updateButtons();

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

    setProperties(props, true);
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

    props = getProperties(true);
    if (!props.save(getFileChooser().getSelectedFile().getAbsolutePath()))
      GUIHelper.showErrorMessage(this, "Failed to save properties to: " + getFileChooser().getSelectedFile());
  }

  /**
   * Adds the specified listener.
   * 
   * @param l		the listener to add
   */
  public void addActionListener(ActionListener l) {
    m_ActionListeners.add(l);
  }
  
  /**
   * Removes the specified listener.
   * 
   * @param l		the listener to remove
   */
  public void removeActionListener(ActionListener l) {
    m_ActionListeners.remove(l);
  }
  
  /**
   * Notifies all change listeners with the specified event.
   * 
   * @param e		the event to send
   */
  protected void notifyActionListeners(ActionEvent e) {
    ActionListener[]	listeners;
    
    listeners = m_ActionListeners.toArray(new ActionListener[m_ActionListeners.size()]);
    for (ActionListener listener: listeners)
      listener.actionPerformed(e);
  }
  
  /**
   * Sets custom text to use for the "finish" button.
   * 
   * @param value	the text, null or empty string to use default
   */
  public void setCustomFinishText(String value) {
    if ((value != null) && (value.trim().length() == 0))
	value = null;
    m_CustomFinishText = value;
  }
  
  /**
   * Returns the custom text to use for the "finish" button, if any.
   * 
   * @return		the text, null if not used
   */
  public String getCustomFinishText() {
    return m_CustomFinishText;
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null) {
      m_Logger = LoggingHelper.getLogger(getID().isEmpty() ? getClass().getName() : (getClass().getName() + "/" + getID()));
      m_Logger.setLevel(Level.INFO);
      m_Logger.removeHandler(LoggingHelper.getDefaultHandler());
      m_Logger.addHandler(LoggingHelper.getDefaultHandler());
      m_Logger.setUseParentHandlers(false);
    }

    return m_Logger;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		always true
   */
  public boolean isLoggingEnabled() {
    return true;
  }
}
