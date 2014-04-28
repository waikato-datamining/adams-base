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
 * DisplayPanelGrid.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.QuickInfoHelper;
import adams.core.VariableNameNoUpdate;
import adams.core.io.PlaceholderFile;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import adams.gui.print.PNGWriter;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 <!-- globalinfo-start -->
 * Sink that places a panel in the grid for each each arriving token.<br/>
 * Uses the user-defined panel provider for creating the panels.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DisplayPanelGrid
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-provider &lt;adams.flow.sink.DisplayPanelProvider&gt; (property: panelProvider)
 * &nbsp;&nbsp;&nbsp;The actor for generating the display panels.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.ImageViewer -writer adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-num-columns &lt;int&gt; (property: numColumns)
 * &nbsp;&nbsp;&nbsp;The number of columns to use in the grid.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-panel-width &lt;int&gt; (property: panelWidth)
 * &nbsp;&nbsp;&nbsp;The width of the individual panels.
 * &nbsp;&nbsp;&nbsp;default: 400
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-panel-height &lt;int&gt; (property: panelHeight)
 * &nbsp;&nbsp;&nbsp;The height of the individual panels.
 * &nbsp;&nbsp;&nbsp;default: 300
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-title-variable &lt;adams.core.VariableNameNoUpdate&gt; (property: titleVariable)
 * &nbsp;&nbsp;&nbsp;The variable to use for the panel title; gets ignored if variable not available 
 * &nbsp;&nbsp;&nbsp;and the index of the panel is used instead.
 * &nbsp;&nbsp;&nbsp;default: titleVariable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DisplayPanelGrid
  extends AbstractDisplay
  implements MenuBarProvider, ComponentSupplier, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8176623753237046447L;

  /** the number of columns to use. */
  protected int m_NumColumns;

  /** the actor to use for generating panels. */
  protected DisplayPanelProvider m_PanelProvider;
  
  /** the width of the individual panels. */
  protected int m_PanelWidth;

  /** the height of the individual panels. */
  protected int m_PanelHeight;

  /** the variable to use for naming the entries. */
  protected VariableNameNoUpdate m_TitleVariable;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the filedialog for loading/saving flows. */
  protected transient JComponentWriterFileChooser m_ComponentFileChooser;

  /** for displaying the panels. */
  protected BasePanel m_PanelAll;
  
  /** the panels to display. */
  protected List<DisplayPanel> m_DisplayPanels;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Sink that places a panel in the grid for each each arriving token.\n"
	+ "Uses the user-defined panel provider for creating the panels.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "provider", "panelProvider",
	    new ImageViewer());
    
    m_OptionManager.add(
	    "num-columns", "numColumns",
	    2, 1, null);
    
    m_OptionManager.add(
	    "panel-width", "panelWidth",
	    400, 1, null);
    
    m_OptionManager.add(
	    "panel-height", "panelHeight",
	    300, 1, null);
    
    m_OptionManager.add(
	    "title-variable", "titleVariable",
	    new VariableNameNoUpdate("titleVariable"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_DisplayPanels = new ArrayList<DisplayPanel>();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numColumns", m_NumColumns, ", #cols: ");
    result += QuickInfoHelper.toString(this, "panelWidth", m_PanelWidth, ", pW: ");
    result += QuickInfoHelper.toString(this, "panelHeight", m_PanelHeight, ", pH: ");
    result += QuickInfoHelper.toString(this, "titleVariable", m_TitleVariable, ", title var: ");
    result += QuickInfoHelper.toString(this, "panelProvider", m_PanelProvider.getClass(), ", provider: ");

    return result;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the panel provider to use for generating the panels.
   *
   * @param value	the panel provider to use
   */
  public void setPanelProvider(DisplayPanelProvider value) {
    m_PanelProvider = value;
    reset();
  }

  /**
   * Returns the panel provider in use for generating the panels.
   *
   * @return		the panel provider in use
   */
  public DisplayPanelProvider getPanelProvider() {
    return m_PanelProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelProviderTipText() {
    return "The actor for generating the display panels.";
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number of columns
   */
  public void setNumColumns(int value) {
    m_NumColumns = value;
    reset();
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return		the number of columns
   */
  public int getNumColumns() {
    return m_NumColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColumnsTipText() {
    return "The number of columns to use in the grid.";
  }

  /**
   * Sets the width of the individual panels.
   *
   * @param value 	the width
   */
  public void setPanelWidth(int value) {
    m_PanelWidth = value;
    reset();
  }

  /**
   * Returns the currently set width of the individual panels.
   *
   * @return 		the width
   */
  public int getPanelWidth() {
    return m_PanelWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelWidthTipText() {
    return "The width of the individual panels.";
  }

  /**
   * Sets the height of the individual panels.
   *
   * @param value 	the height
   */
  public void setPanelHeight(int value) {
    m_PanelHeight = value;
    reset();
  }

  /**
   * Returns the currently set height of the individual panels.
   *
   * @return 		the height
   */
  public int getPanelHeight() {
    return m_PanelHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelHeightTipText() {
    return "The height of the individual panels.";
  }

  /**
   * Sets the variable name which value gets used to name the entries. Gets
   * ignored if variable does not exist.
   *
   * @param value	the variable name
   */
  public void setTitleVariable(VariableNameNoUpdate value) {
    m_TitleVariable = value;
    reset();
  }

  /**
   * Returns the variable name which value gets used to name the entries.
   * Gets ignored if variable does not exist.
   *
   * @return		the variable name
   */
  public VariableNameNoUpdate getTitleVariable() {
    return m_TitleVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleVariableTipText() {
    return "The variable to use for the panel title; gets ignored if variable not available and the index of the panel is used instead.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class, java.awt.image.BufferedImage.class, adams.data.image.AbstractImage.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    if ((m_PanelProvider != null) && (m_PanelProvider instanceof InputConsumer))
      return ((InputConsumer) m_PanelProvider).accepts();
    else
      return new Class[]{Object.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_PanelAll.removeAll();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    
    m_PanelAll = new BasePanel(new GridLayout(1, m_NumColumns));
    result     = new BasePanel(new BorderLayout());
    result.add(new BaseScrollPane(m_PanelAll), BorderLayout.CENTER);
    
    return result;
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    BasePanel		panel;
    DisplayPanel	dpanel;
    int			rows;
    GridLayout		layout;
    String		title;
    
    if (getVariables().has(m_TitleVariable.getValue()))
      title = getVariables().get(m_TitleVariable.getValue());
    else
      title = "" + (m_DisplayPanels.size() + 1);
    
    panel = new BasePanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(m_PanelWidth, m_PanelHeight));
    panel.setMinimumSize(new Dimension(m_PanelWidth, m_PanelHeight));
    panel.setBorder(BorderFactory.createTitledBorder(title));
    
    // create panel
    dpanel = m_PanelProvider.createDisplayPanel(token);
    m_DisplayPanels.add(dpanel);
    
    // increase rows?
    rows   = (int) Math.ceil((double) m_DisplayPanels.size() / m_NumColumns) ;
    layout = (GridLayout) m_PanelAll.getLayout();
    if (layout.getRows() < rows)
      layout.setRows(rows);
    
    // add to grid
    //if (m_PanelProvider.displayPanelRequiresScrollPane())
    //  panel.add(new BaseScrollPane((JComponent) dpanel), BorderLayout.CENTER);
    //else
      panel.add((JComponent) dpanel, BorderLayout.CENTER);
    m_PanelAll.add(panel);
    m_PanelAll.getParent().validate();
    m_PanelAll.getParent().repaint();
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    result = new JMenuBar();

    // File
    menu = new JMenu("File");
    result.add(menu);
    menu.setMnemonic('F');
    menu.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // File/Save As
    menuitem = new JMenuItem("Save as...");
    menu.add(menuitem);
    menuitem.setMnemonic('a');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.setIcon(GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveAs();
      }
    });
    m_MenuItemFileSaveAs = menuitem;

    // File/Send to
    menu.addSeparator();
    if (SendToActionUtils.addSendToSubmenu(this, menu))
      menu.addSeparator();

    // File/Close
    menuitem = new JMenuItem("Close");
    menu.add(menuitem);
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
    menuitem.setIcon(GUIHelper.getIcon("exit.png"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	close();
      }
    });
    m_MenuItemFileClose = menuitem;

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    if (m_MenuBar == null) {
      m_MenuBar = createMenuBar();
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    m_MenuItemFileSaveAs.setEnabled(
          (m_PanelProvider instanceof ComponentSupplier)
       || (m_PanelProvider instanceof TextSupplier) );
  }

  /**
   * Returns (and initializes if necessary) the file chooser for the components.
   * 
   * @return		the file chooser
   */
  protected JComponentWriterFileChooser getComponentFileChooser() {
    if (m_ComponentFileChooser == null)
      m_ComponentFileChooser = new JComponentWriterFileChooser();
    
    return m_ComponentFileChooser;
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    m_PanelAll.closeParent();
  }

  /**
   * Saves the setups.
   */
  protected void saveAs() {
    int			retVal;
    JComponentWriter	writer;

    retVal = getComponentFileChooser().showSaveDialog(m_Panel);
    if (retVal != JComponentWriterFileChooser.APPROVE_OPTION)
      return;

    writer = getComponentFileChooser().getWriter();
    writer.setComponent(supplyComponent());
    try {
      writer.toOutput();
    }
    catch (Exception e) {
      handleException("Error saving panel to '" + writer.getFile() + "': ", e);
    }
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    if (m_PanelAll != null)
      m_PanelAll.removeAll();

    m_DisplayPanels.clear();
    
    m_MenuBar            = null;
    m_MenuItemFileSaveAs = null;
    m_MenuItemFileClose  = null;

    super.cleanUpGUI();
  }

  /**
   * Returns the current panel.
   *
   * @return		the current panel, can be null
   */
  public JComponent supplyComponent() {
    return m_PanelAll;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    if (m_PanelProvider instanceof ComponentSupplier)
      return new Class[]{PlaceholderFile.class, JComponent.class};
    else
      return new Class[]{};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	return (supplyComponent() != null);
      else
	return false;
    }

    if (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, String.class}, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	return (supplyComponent() != null);
      else
	return false;
    }

    return false;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    JComponent		comp;
    PNGWriter		writer;

    result = null;

    if (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, String.class}, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier) {
	comp = supplyComponent();
	if (comp != null) {
	  result = SendToActionUtils.nextTmpFile("actor-" + getName(), "png");
	  writer = new PNGWriter();
	  writer.setFile((PlaceholderFile) result);
	  writer.setComponent(comp);
	  try {
	    writer.generateOutput();
	  }
	  catch (Exception e) {
	    handleException("Failed to write image to " + result + ":", e);
	    result = null;
	  }
	}
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	result = supplyComponent();
    }

    return result;
  }
}
