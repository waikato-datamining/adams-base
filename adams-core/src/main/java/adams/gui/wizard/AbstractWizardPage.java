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
 * AbstractWizardPage.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.logging.LoggingSupporter;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextPane;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ancestor for wizard pages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWizardPage
  extends BasePanel
  implements LoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 391442213775313771L;
  
  /** the wizard this page belongs to. */
  protected WizardPane m_Owner;
  
  /** the name of the page. */
  protected String m_PageName;

  /** the description of the page. */
  protected BaseTextPane m_TextDescription;
  
  /** the scroll pane for the description. */
  protected BaseScrollPane m_ScrollPaneDescription;

  /** the page check to perform. */
  protected PageCheck m_PageCheck;
  
  /** the action to perform when proceeding. */
  protected ProceedAction m_ProceedAction;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PageName      = "Page";
    m_PageCheck     = new DummyPageCheck();
    m_ProceedAction = new DummyProceedAction();
    m_Owner         = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextDescription = new BaseTextPane();
    m_TextDescription.setEditable(false);
    setDescriptionHeight(100);
    
    m_ScrollPaneDescription = new BaseScrollPane(m_TextDescription);
    add(m_ScrollPaneDescription, BorderLayout.NORTH);
  }

  /**
   * Sets the wizard this page belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(WizardPane value) {
    m_Owner = value;
  }
  
  /**
   * Returns the wizard this page belongs to.
   * 
   * @return		the owner, null if none set
   */
  public WizardPane getOwner() {
    return m_Owner;
  }
  
  /**
   * Sets the page name.
   * 
   * @param value	the name
   */
  public void setPageName(String value) {
    m_PageName = value;
  }
  
  /**
   * Returns the page name.
   * 
   * @return		the name
   */
  public String getPageName() {
    return m_PageName;
  }
  
  /**
   * Sets the description of the page. Checks for "html" tag to determine
   * whether the content is HTML or plain-text.
   * 
   * @param value	the description (plain-text or html)
   */
  public void setDescription(String value) {
    setDescription(value, (value.toLowerCase().indexOf("<html>") > -1));
  }
  
  /**
   * Sets the description of the page.
   * 
   * @param value	the description
   * @param isHtml	if true then text is interpreted as HTML 3
   */
  public void setDescription(String value, boolean isHtml) {
    if (isHtml)
      m_TextDescription.setEditorKit(new HTMLEditorKit());
    else
      m_TextDescription.setEditorKit(new StyledEditorKit());
    m_TextDescription.setText(value);
  }
  
  /**
   * Returns the description of the page.
   * 
   * @return		the description (may be html)
   */
  public String getDescription() {
    return m_TextDescription.getText();
  }
  
  /**
   * Sets the preferred height of the description box.
   * 
   * @param value	the new height in pixel
   */
  public void setDescriptionHeight(int value) {
    m_TextDescription.setPreferredSize(new Dimension(0, value));
  }
  
  /**
   * Returns the preferred height of the description box.
   * 
   * @return		the height in pixel
   */
  public int getDescriptionHeight() {
    return m_TextDescription.getPreferredSize().height;
  }
  
  /**
   * Sets the page check instance to use.
   * 
   * @param value	the check to use
   */
  public void setPageCheck(PageCheck value) {
    m_PageCheck = value;
  }
  
  /**
   * Returns the page check instance in use.
   * 
   * @return		the check in use
   */
  public PageCheck getPageCheck() {
    return m_PageCheck;
  }
  
  /**
   * Sets the proceed action instance to use.
   * 
   * @param value	the action to use
   */
  public void setProceedAction(ProceedAction value) {
    m_ProceedAction = value;
  }
  
  /**
   * Returns the proceed action instance in use.
   * 
   * @return		the action in use
   */
  public ProceedAction getProceedAction() {
    return m_ProceedAction;
  }
  
  /**
   * Returns whether we can proceed with the next page.
   * 
   * @return		true if we can proceed
   * @see		#getPageCheck()
   */
  public boolean canProceed() {
    return m_PageCheck.checkPage(this);
  }
  
  /**
   * Updates the wizard's buttons.
   */
  public void updateButtons() {
    if (m_Owner != null)
      m_Owner.updateButtons();
  }

  /**
   * Sets the content of the page (ie parameters) as properties.
   *
   * @param value	the parameters as properties
   */
  public abstract void setProperties(Properties value);

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  public abstract Properties getProperties();

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public Logger getLogger() {
    return m_Owner.getLogger();
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return m_Owner.isLoggingEnabled();
  }
}
