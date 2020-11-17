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
import adams.core.base.BasePassword;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Similar to a {@link BaseTabbedPane}, but with the names of the pages
 * listed in a {@link BaseList} on the left-hand side.
 * <br><br>
 * Attached {@link ActionListener}s received either {@link #ACTION_CANCEL}
 * or {@link #ACTION_FINISH} as command.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WizardPane
  extends AbstractWizardPane {

  /** for serialization. */
  private static final long serialVersionUID = 887135856139374858L;

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

  /** the page order. */
  protected List<String> m_PageOrder;
  
  /** the currently selected page. */
  protected int m_SelectedPage;

  /**
   * Initializes the wizard with no ID.
   */
  public WizardPane() {
    super("");
  }

  /**
   * Initializes the wizard.
   *
   * @param id		the ID of the wizard, used for logging purposes
   */
  public WizardPane(String id) {
    super(id);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PageOrder    = new ArrayList<>();
    m_SelectedPage = -1;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setOneTouchExpandable(false);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_ModelNames = new DefaultListModel<>();
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
  }

  /**
   * Goes to the previous page.
   */
  @Override
  protected void previousPage() {
    setSelectedPage(getSelectedIndex() - 1);
  }

  /**
   * Goes to the next page.
   */
  @Override
  protected void nextPage() {
    AbstractWizardPage currPage;
    AbstractWizardPage nextPage;

    currPage = getPageAt(getSelectedIndex());
    nextPage = getPageAt(getSelectedIndex() + 1);
    if (currPage.getProceedAction() != null)
      currPage.getProceedAction().onProceed(currPage, nextPage);
    setSelectedPage(getSelectedIndex() + 1);
    currPage.validate();
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
  public void removeAllPages() {
    super.removeAllPages();
    m_PageOrder.clear();
    m_ModelNames.clear();
    m_SelectedPage = -1;
  }
  
  /**
   * Adds the page under the given name.
   * 
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
    GenericObjectEditorPage goepage = new GenericObjectEditorPage("Any Reader");
    shpage.setDescription("<html><h3>Class hierarchy</h3>Here you can select a class of the " + SpreadSheetReader.class.getName() + " class hierarchy.");
    goepage.setClassType(SpreadSheetReader.class);
    goepage.setCanChangeClassInDialog(true);
    goepage.setValue(new CsvSpreadSheetReader());
    wizard.addPage(goepage);
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
    SelectDirectoryPage seldir = new SelectDirectoryPage();
    seldir.setPageName("Select directory");
    seldir.setDescription("Please select any existing file by clicking on the '...' button.");
    wizard.addPage(seldir);
    SelectMultipleDirectoriesPage selmdir = new SelectMultipleDirectoriesPage();
    selmdir.setPageName("Select multiple directories");
    selmdir.setDescription("Please select as many directories as you like.");
    wizard.addPage(selmdir);
    TextAreaPage textpage = new TextAreaPage("Free text");
    textpage.setDescription("Please enter some text");
    textpage.setText("blah\nblah\nblah");
    wizard.addPage(textpage);
    wizard.addPage(new FinalPage());
    final BaseFrame frame = new BaseFrame("Example Wizard");
    wizard.addActionListener((ActionEvent e) -> {
      System.out.println(e.getActionCommand());
      System.out.println(wizard.getProperties(true));
      frame.setVisible(false);
      frame.dispose();
    });
    wizard.update();
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(wizard, BorderLayout.CENTER);
    frame.setSize(GUIHelper.getDefaultDialogDimension());
    frame.setVisible(true);
  }
}
