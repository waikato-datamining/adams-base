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
 * WizardPaneWithBranches.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Similar to {@link WizardPane} but allows branching.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WizardPaneWithBranches
  extends AbstractWizardPane {

  /** for serialization. */
  private static final long serialVersionUID = 887135856139374858L;

  /** for displaying the page component. */
  protected JPanel m_PageComponent;

  /** the page layout. */
  protected DefaultMutableTreeNode m_Pages;

  /** the last page added. */
  protected AbstractWizardPage m_LastPageAdded;

  /** the currently selected page. */
  protected AbstractWizardPage m_SelectedPage;

  /** the final pages. */
  protected Set<AbstractWizardPage> m_FinalPages;

  /**
   * Initializes the wizard with no ID.
   */
  public WizardPaneWithBranches() {
    super("");
  }

  /**
   * Initializes the wizard.
   *
   * @param id		the ID of the wizard, used for logging purposes
   */
  public WizardPaneWithBranches(String id) {
    super(id);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Pages         = null;
    m_LastPageAdded = null;
    m_SelectedPage  = null;
    m_FinalPages    = new HashSet<>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PageComponent = new JPanel(new BorderLayout());
    add(m_PageComponent, BorderLayout.CENTER);
  }

  /**
   * Goes to the previous page.
   */
  @Override
  protected void previousPage() {
    DefaultMutableTreeNode	current;

    current = findNode(getSelectedPage());
    if (current == null)
      return;

    if (current.getParent() != null)
      setSelectedPage((AbstractWizardPage) ((DefaultMutableTreeNode) current.getParent()).getUserObject());
    else
      getLogger().warning("No parent, cannot go to previous page");
  }

  /**
   * Goes to the next page.
   */
  @Override
  protected void nextPage() {
    AbstractWizardPage		currPage;
    DefaultMutableTreeNode 	currNode;

    currPage = getSelectedPage();
    currNode = findNode(currPage);
    if (currNode == null)
      return;

    if (currNode.getChildCount() > 0)
      setSelectedPage((AbstractWizardPage) ((DefaultMutableTreeNode) currNode.getChildAt(0)).getUserObject());
    else
      getLogger().warning("No children, cannot go to next page");

    currPage.validate();
  }

  /**
   * Removes all pages.
   */
  public void removeAllPages() {
    super.removeAllPages();
    if (m_Pages != null)
      m_Pages.removeAllChildren();
    m_Pages         = null;
    m_SelectedPage  = null;
    m_LastPageAdded = null;
    m_FinalPages.clear();
  }
  
  /**
   * Adds the page under the given name. Uses the last page as parent.
   * 
   * @param page	the page
   */
  public void addPage(AbstractWizardPage page) {
    addPage(m_LastPageAdded, page);
  }

  /**
   * Finds the node associated with the specified page.
   *
   * @param page	the page to get the node for
   * @return		the node, null if failed to locate
   */
  protected DefaultMutableTreeNode findNode(AbstractWizardPage page) {
    DefaultMutableTreeNode	result;
    Enumeration<TreeNode>	nodes;
    DefaultMutableTreeNode	node;

    result = null;

    if (m_Pages != null) {
      nodes = m_Pages.depthFirstEnumeration();
      while (nodes.hasMoreElements()) {
        node = (DefaultMutableTreeNode) nodes.nextElement();
        if (node.getUserObject() == page) {
          result = node;
          break;
	}
      }
    }

    return result;
  }

  /**
   * Adds the page under the given name. Uses the specified parent.
   *
   * @param parent 	the parent, null for root
   * @param page	the page
   */
  public void addPage(AbstractWizardPage parent, AbstractWizardPage page) {
    DefaultMutableTreeNode	nodeParent;
    DefaultMutableTreeNode	node;

    if (parent == null) {
      m_Pages = new DefaultMutableTreeNode();
      m_Pages.setUserObject(page);
    }
    else {
      nodeParent = findNode(parent);
      if (nodeParent == null)
        throw new IllegalArgumentException("Parent page not found in wizard!");
      node = new DefaultMutableTreeNode();
      node.setUserObject(page);
      nodeParent.add(node);
    }

    page.setOwner(this);
    m_PageLookup.put(page.getPageName(), page);

    m_LastPageAdded = page;
    if (m_SelectedPage == null)
      setSelectedPage(page);
    else
      updateButtons();
  }

  /**
   * Removes the page (and all its subsequent pages).
   * 
   * @param page	the page to remove
   */
  public void removePage(AbstractWizardPage page) {
    DefaultMutableTreeNode	nodePage;
    DefaultMutableTreeNode	node;
    Enumeration<TreeNode>	nodes;
    AbstractWizardPage		currPage;

    nodePage = findNode(page);
    if (nodePage == null)
      return;

    // remove sub-tree
    nodes = nodePage.breadthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      node = (DefaultMutableTreeNode) nodes.nextElement();
      currPage = (AbstractWizardPage) node.getUserObject();
      m_PageLookup.remove(currPage.getPageName());
    }

    // remove node itself
    m_PageLookup.remove(page.getPageName());
    if (nodePage.getParent() != null)
      ((DefaultMutableTreeNode) nodePage.getParent()).remove(nodePage);

    m_PageComponent.removeAll();

    updateButtons();
  }

  /**
   * Marks the page as a final one (for the finish button to be displayed).
   *
   * @param page	the page
   */
  public void markFinalPage(AbstractWizardPage page) {
    m_FinalPages.add(page);
  }

  /**
   * Unmarks the page as a final one (for the finish button to be displayed).
   *
   * @param page	the page
   */
  public void unmarkFinalPage(AbstractWizardPage page) {
    m_FinalPages.remove(page);
  }

  /**
   * Returns the currently active page.
   * 
   * @return		the page, null if not available
   */
  public AbstractWizardPage getSelectedPage() {
    return m_SelectedPage;
  }

  /**
   * Sets the specified page as active one.
   * 
   * @param page	the page to use as active page
   */
  public void setSelectedPage(AbstractWizardPage page) {
    DefaultMutableTreeNode	node;

    node = findNode(page);
    if (node == null)
      throw new IllegalArgumentException("Page is not part of the wizard!");

    m_SelectedPage = page;
    m_PageComponent.removeAll();
    m_PageComponent.add(m_PageLookup.get(m_SelectedPage.getPageName()));
    
    invalidate();
    validate();
    repaint();
    
    updateButtons();
  }

  /**
   * Returns all the child pages for the specified page.
   *
   * @param page	the page to get the children for
   * @return		the children
   */
  public AbstractWizardPage[] getChildPages(AbstractWizardPage page) {
    List<AbstractWizardPage> 	result;
    DefaultMutableTreeNode	node;
    int				i;

    node = findNode(page);
    if (node == null)
      throw new IllegalArgumentException("Page does not belong to this wizard!");

    result = new ArrayList<>();
    for (i = 0; i < node.getChildCount(); i++)
      result.add((AbstractWizardPage) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject());

    return result.toArray(new AbstractWizardPage[0]);
  }

  /**
   * Updates the status of the buttons.
   */
  public void updateButtons() {
    AbstractWizardPage		current;
    DefaultMutableTreeNode	node;
    
    current = getSelectedPage();
    node    = findNode(current);
    
    m_ButtonBack.setEnabled((node != null) && (node.getParent() != null));
    m_ButtonNext.setEnabled((current != null) && (node != null) && (node.getChildCount() > 0) && current.canProceed());
    m_ButtonCancelFinish.setEnabled(true);
    if (m_FinalPages.contains(current))
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
    final WizardPaneWithBranches wizard = new WizardPaneWithBranches();

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
    page.setButtonPanelVisible(false);
    wizard.addPage(page);

    page = new ParameterPanelPage();
    page.setPageName("Parameters 3");
    page.setDescription("Nothing here.");
    page.setButtonPanelVisible(false);
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

    BranchSelectionPage branch = new BranchSelectionPage();
    branch.setPageName("Selection");
    branch.setDescription("Please select next operation");
    wizard.addPage(branch);
    {
      FinalPage finalPage;
      ExtensionFileFilter filter;

      {
	ListPage lpage = new ListPage();
	lpage.setPageName("List");
	lpage.setDescription("Select any number of items from the list below");
	lpage.setValues(new String[]{"1", "2", "3", "4", "5"});
	lpage.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	lpage.setSelectedValues(new String[]{"3", "5"});
	wizard.addPage(branch, lpage);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish list");
	wizard.addPage(lpage, finalPage);
	wizard.markFinalPage(finalPage);
      }

      {
	SelectFilePage selpage = new SelectFilePage();
	selpage.setPageName("Select file");
	selpage.setDescription("Please select any existing file by clicking on the '...' button.");
	selpage.addChoosableFileFilter(new ExtensionFileFilter("Log files", "log"));
	selpage.addChoosableFileFilter(filter = new ExtensionFileFilter("Text files", "txt"));
	selpage.setFileFilter(filter);
	wizard.addPage(branch, selpage);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish file");
	wizard.addPage(selpage, finalPage);
	wizard.markFinalPage(finalPage);
      }

      {
	SelectMultipleFilesPage selmpage = new SelectMultipleFilesPage();
	selmpage.setPageName("Select multiple files");
	selmpage.setDescription("Please select as many files as you like.");
	selmpage.addChoosableFileFilter(new ExtensionFileFilter("Log files", "log"));
	selmpage.addChoosableFileFilter(filter = new ExtensionFileFilter("Text files", "txt"));
	selmpage.setFileFilter(filter);
	wizard.addPage(branch, selmpage);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish multiple files");
	wizard.addPage(selmpage, finalPage);
	wizard.markFinalPage(finalPage);
      }

      {
	SelectDirectoryPage seldir = new SelectDirectoryPage();
	seldir.setPageName("Select directory");
	seldir.setDescription("Please select any existing file by clicking on the '...' button.");
	wizard.addPage(seldir);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish dir");
	wizard.addPage(seldir, finalPage);
	wizard.markFinalPage(finalPage);
      }

      {
	SelectMultipleDirectoriesPage selmdir = new SelectMultipleDirectoriesPage();
	selmdir.setPageName("Select multiple directories");
	selmdir.setDescription("Please select as many directories as you like.");
	wizard.addPage(selmdir);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish multiple dirs");
	wizard.addPage(selmdir, finalPage);
	wizard.markFinalPage(finalPage);
      }

      {
	TextAreaPage textpage = new TextAreaPage("Free text");
	textpage.setDescription("Please enter some text");
	textpage.setText("blah\nblah\nblah");
	wizard.addPage(branch, textpage);

	finalPage = new FinalPage();
	finalPage.setPageName("Finish text");
	wizard.addPage(textpage, finalPage);
	wizard.markFinalPage(finalPage);
      }
    }

    final BaseFrame frame = new BaseFrame("Example Branch Wizard");
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
