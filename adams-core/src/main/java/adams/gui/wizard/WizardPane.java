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
 * WizardPane.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.BaseList;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Similar to a {@link BaseTabbedPane}, but with the names of the pages
 * listed in a {@link BaseList} on the left-hand side.
 * <br><br>
 * Attached {@link ActionListener}s received either {@link #ACTION_CANCEL}
 * or {@link #ACTION_FINISH} as command.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WizardPane
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 887135856139374858L;

  /** the action for cancelling the wizard. */
  public final static String ACTION_CANCEL = "Cancel";

  /** the action for finishing the wizard. */
  public final static String ACTION_FINISH = "Finish";
  
  /** the model for displaying the page names. */
  protected DefaultListModel<String> m_ModelNames;

  /** the list for displaying the page names. */
  protected BaseList m_ListNames;
  
  /** the scrollpane for the names list. */
  protected BaseScrollPane m_ScrollPaneNames;
  
  /** the split pane. */
  protected BaseSplitPane m_SplitPane;
  
  /** for displaying the page component. */
  protected JPanel m_PageComponent;
  
  /** the pages lookup. */
  protected HashMap<String, AbstractWizardPage> m_PageLookup;
  
  /** the page order. */
  protected List<String> m_PageOrder;
  
  /** the currently selected page. */
  protected int m_SelectedPage;
  
  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;
  
  /** the button for the previous page. */
  protected JButton m_ButtonBack;
  
  /** the button for the next page. */
  protected JButton m_ButtonNext;
  
  /** the button for the cancelling/finishing. */
  protected JButton m_ButtonCancelFinish;
  
  /** the action listeners (ie hitting cancel/finish). */
  protected HashSet<ActionListener> m_ActionListeners;
  
  /** the custom text for the "finish" button. */
  protected String m_CustomFinishText;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PageLookup       = new HashMap<String, AbstractWizardPage>();
    m_PageOrder        = new ArrayList<String>();
    m_SelectedPage     = -1;
    m_ActionListeners  = new HashSet<ActionListener>();
    m_CustomFinishText = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setOneTouchExpandable(false);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_ModelNames = new DefaultListModel<String>();
    m_ListNames  = new BaseList(m_ModelNames);
    m_ListNames.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	m_ListNames.setSelectedIndex(m_SelectedPage);
      }
    });
    m_ScrollPaneNames = new BaseScrollPane(m_ListNames);
    m_SplitPane.setLeftComponent(m_ScrollPaneNames);
    
    m_PageComponent = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PageComponent);
    
    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(m_PanelButtons, BorderLayout.SOUTH);
    
    m_ButtonBack = new JButton("Back");
    m_ButtonBack.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setSelectedPage(getSelectedIndex() - 1);
      }
    });
    m_PanelButtons.add(m_ButtonBack);
    
    m_ButtonNext = new JButton("Next");
    m_ButtonNext.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	AbstractWizardPage currPage = getPageAt(getSelectedIndex());
	AbstractWizardPage nextPage = getPageAt(getSelectedIndex() + 1);
	if (currPage.getProceedAction() != null)
	  currPage.getProceedAction().onProceed(currPage, nextPage);
	setSelectedPage(getSelectedIndex() + 1);
      }
    });
    m_PanelButtons.add(m_ButtonNext);
    
    m_ButtonCancelFinish = new JButton("");
    m_ButtonCancelFinish.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String action;
	if (m_ButtonCancelFinish.getText().equals(ACTION_CANCEL))
	  action = ACTION_CANCEL;
	else
	  action = ACTION_FINISH;
	notifyActionListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action));
      }
    });
    m_PanelButtons.add(m_ButtonCancelFinish);
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
   * Returns the underlying split pane.
   * 
   * @return		the split pane
   */
  public BaseSplitPane getSplitPane() {
    return m_SplitPane;
  }
  
  /**
   * Removes all pages.
   */
  @Override
  public void removeAll() {
    m_PageOrder.clear();
    m_PageLookup.clear();
    m_ModelNames.clear();
    m_SelectedPage = -1;
  }
  
  /**
   * Adds the page under the given name.
   * 
   * @param name	the name of the page
   * @param page	the page
   */
  public void addPage(AbstractWizardPage page) {
    page.setOwner(this);
    m_PageLookup.put(page.getPageName(), page);
    if (m_PageOrder.contains(page.getPageName()))
      m_PageOrder.remove(page.getPageName());
    m_PageOrder.add(page.getPageName());
    if (m_ModelNames.contains(page.getPageName()))
      m_ModelNames.removeElement(page.getPageName());
    m_ModelNames.addElement(page.getPageName());
    
    if (m_SelectedPage == -1)
      setSelectedPage(0);
    else
      updateButtons();
  }
  
  /**
   * Removes the page at the specified index.
   * 
   * @param index	the index of the page to remove
   */
  public void removePageAt(int index) {
    String		name;
    AbstractWizardPage	page;
    
    if ((index < 0) || (index >= m_PageOrder.size()))
      return;
    
    name = m_PageOrder.get(index);
    m_PageComponent.removeAll();
    m_PageOrder.remove(index);
    page = m_PageLookup.remove(name);
    if (page != null)
      page.setOwner(null);
    m_ModelNames.removeElement(name);
    
    if (index == m_SelectedPage) {
      if (m_PageOrder.size() > 0) {
	if (m_SelectedPage == m_PageOrder.size())
	  m_SelectedPage--;
	m_ListNames.setSelectedIndex(m_SelectedPage);
      }
    }
    
    updateButtons();
  }
  
  /**
   * Returns the currently active page index.
   * 
   * @return		the index, -1 if not available
   */
  public int getSelectedIndex() {
    return m_SelectedPage;
  }
  
  /**
   * Returns the currently active page.
   * 
   * @return		the page, null if not available
   */
  public AbstractWizardPage getSelectedPage() {
    if (m_SelectedPage == -1)
      return null;
    else
      return m_PageLookup.get(m_PageOrder.get(m_SelectedPage));
  }

  /**
   * Sets the specified page as active one.
   * 
   * @param index	the index of the page to use as active page
   */
  public void setSelectedPage(int index) {
    m_PageComponent.removeAll();
    m_SelectedPage = index;
    m_ListNames.setSelectedIndex(index);
    m_PageComponent.add(m_PageLookup.get(m_PageOrder.get(m_SelectedPage)));
    
    invalidate();
    validate();
    repaint();
    
    updateButtons();
  }
  
  /**
   * Returns the current page count.
   * 
   * @return		the number of pages
   */
  public int getPageCount() {
    return m_PageLookup.size();
  }
  
  /**
   * Returns the specified page.
   * 
   * @param index	the page index
   * @return		the page
   */
  public AbstractWizardPage getPageAt(int index) {
    return m_PageLookup.get(m_PageOrder.get(index));
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
    for (String name: m_PageOrder) {
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
  public void updateButtons() {
    AbstractWizardPage	current;
    
    current = getSelectedPage();
    
    m_ButtonBack.setEnabled(m_SelectedPage > 0);
    m_ButtonNext.setEnabled((current != null) && (m_SelectedPage < getPageCount() - 1) && current.canProceed());
    m_ButtonCancelFinish.setEnabled(true);
    if (m_SelectedPage == getPageCount() - 1)
      m_ButtonCancelFinish.setText((m_CustomFinishText == null) ?  ACTION_FINISH : m_CustomFinishText);
    else
      m_ButtonCancelFinish.setText(ACTION_CANCEL);
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
   * For testing only.
   * 
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    final WizardPane wizard = new WizardPane();
    wizard.addPage(new StartPage());
    ParameterPanelPage page = new ParameterPanelPage();
    page.setPageName("Parameters 1");
    page.setDescription("Here is the description of the first parameter page.\n\nAnother line.");
    page.getParameterPanel().addPropertyType("doublevalue", PropertyType.DOUBLE);
    page.getParameterPanel().setLabel("doublevalue", "Double value");
    page.getParameterPanel().addPropertyType("booleanvalue", PropertyType.BOOLEAN);
    page.getParameterPanel().setLabel("booleanvalue", "Boolean value");
    page.getParameterPanel().addPropertyType("stringlist", PropertyType.LIST);
    page.getParameterPanel().setLabel("stringlist", "String list");
    page.getParameterPanel().setList("stringlist", new String[]{"A", "B", "C"});
    page.getParameterPanel().addPropertyType("passwordvalue", PropertyType.PASSWORD);
    page.getParameterPanel().setLabel("passwordvalue", "Password value");
    page.getParameterPanel().addPropertyType("sqlvalue", PropertyType.SQL);
    page.getParameterPanel().setLabel("sqlvalue", "SQL value");
    Properties props = new Properties();
    props.setDouble("doublevalue", 1.234);
    props.setBoolean("booleanvalue", true);
    props.setProperty("stringlist", "B");
    props.setPassword("passwordvalue", new BasePassword("secret"));
    props.setProperty("sqlvalue", "select * from table1 where a < b");
    page.setProperties(props);
    page.setDescription("Here is the description of the first parameter page.\n\nAnother line.");
    wizard.addPage(page);
    page = new ParameterPanelPage();
    page.setDescription("<html><h3>More parameters</h3>Here is the description of the 2nd parameter page.<br><br>Another line.");
    page.setPageName("Parameters 2");
    wizard.addPage(page);
    page = new ParameterPanelPage();
    page.setPageName("Parameters 3");
    page.setDescription("Nothing here.");
    wizard.addPage(page);
    PropertySheetPanelPage shpage = new PropertySheetPanelPage("Reader");
    shpage.setDescription("<html><h3>Object properties</h3>Here you can change all properties of the " + CsvSpreadSheetReader.class.getName() + ".");
    shpage.setTarget(new CsvSpreadSheetReader());
    wizard.addPage(shpage);
    ListPage lpage = new ListPage();
    lpage.setPageName("List");
    lpage.setDescription("Select any number of items from the list below");
    lpage.setValues(new String[]{"1", "2", "3", "4", "5"});
    lpage.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    lpage.setSelectedValues(new String[]{"3", "5"});
    wizard.addPage(lpage);
    ExtensionFileFilter filter;
    SelectFilePage selpage = new SelectFilePage();
    selpage.setPageName("Select file");
    selpage.setDescription("Please select any existing file by clicking on the '...' button.");
    selpage.addChoosableFileFilter(new ExtensionFileFilter("Log files", "log"));
    selpage.addChoosableFileFilter(filter = new ExtensionFileFilter("Text files", "txt"));
    selpage.setFileFilter(filter);
    wizard.addPage(selpage);
    SelectMultipleFilesPage selmpage = new SelectMultipleFilesPage();
    selmpage.setPageName("Select multiple files");
    selmpage.setDescription("Please select as many files as you like.");
    selmpage.addChoosableFileFilter(new ExtensionFileFilter("Log files", "log"));
    selmpage.addChoosableFileFilter(filter = new ExtensionFileFilter("Text files", "txt"));
    selmpage.setFileFilter(filter);
    wizard.addPage(selmpage);
    TextAreaPage textpage = new TextAreaPage("Free text");
    textpage.setDescription("Please enter some text");
    textpage.setText("blah\nblah\nblah");
    wizard.addPage(textpage);
    wizard.addPage(new FinalPage());
    final BaseFrame frame = new BaseFrame("Example Wizard");
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	System.out.println(e.getActionCommand());
	System.out.println(wizard.getProperties(true));
	frame.setVisible(false);
	frame.dispose();
      }
    });
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(wizard, BorderLayout.CENTER);
    frame.setSize(800, 600);
    frame.setVisible(true);
  }
}
