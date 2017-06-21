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
 * XSLTPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.dialog.TextPanel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import org.w3c.dom.Document;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * Panel for performing XSLT on XML.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XSLTPanel
  extends BasePanel
  implements MenuBarProvider {

  private static final long serialVersionUID = -5677998941035693457L;

  /**
   * Panel for displaying text, loading and saving the content.
   */
  public static class SubPanel
    extends BasePanel {

    private static final long serialVersionUID = 3836566100162935753L;

    /** the panel for the text. */
    protected TextPanel m_PanelText;

    /** the panel with the buttons. */
    protected JPanel m_PanelButtons;

    /** the button for clearing the text. */
    protected JButton m_ButtonClear;

    /** the button for loading a file. */
    protected JButton m_ButtonOpen;

    /** the button for saving to a file. */
    protected JButton m_ButtonSave;

    /** the button for copy. */
    protected JButton m_ButtonCopy;

    /** the button for paste. */
    protected JButton m_ButtonPaste;

    /** the file chooser. */
    protected BaseFileChooser m_FileChooser;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_FileChooser = new BaseFileChooser();
      m_FileChooser.setAcceptAllFileFilterUsed(true);
      m_FileChooser.setAutoAppendExtension(true);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new BorderLayout());

      m_PanelText = new TextPanel();
      m_PanelText.setCanOpenFiles(true);
      add(m_PanelText, BorderLayout.CENTER);

      m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(m_PanelButtons, BorderLayout.SOUTH);

      m_ButtonClear = new JButton(GUIHelper.getIcon("new.gif"));
      m_ButtonClear.addActionListener((ActionEvent e) -> clear());
      m_PanelButtons.add(m_ButtonClear);

      m_ButtonOpen = new JButton(GUIHelper.getIcon("open.gif"));
      m_ButtonOpen.addActionListener((ActionEvent e) -> open());
      m_PanelButtons.add(m_ButtonOpen);

      m_ButtonSave = new JButton(GUIHelper.getIcon("save.gif"));
      m_ButtonSave.addActionListener((ActionEvent e) -> save());
      m_PanelButtons.add(m_ButtonSave);

      m_ButtonCopy = new JButton(GUIHelper.getIcon("copy.gif"));
      m_ButtonCopy.addActionListener((ActionEvent e) -> copy());
      m_PanelButtons.add(m_ButtonCopy);

      m_ButtonPaste = new JButton(GUIHelper.getIcon("paste.gif"));
      m_ButtonPaste.addActionListener((ActionEvent e) -> paste());
      m_PanelButtons.add(m_ButtonPaste);
    }

    /**
     * Clears the text.
     */
    public void clear() {
      m_PanelText.setContent("");
    }

    /**
     * Lets the user select a file to load.
     */
    public void open() {
      int	retVal;

      retVal = m_FileChooser.showOpenDialog(GUIHelper.getParentComponent(this));
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;

      m_PanelText.open(m_FileChooser.getSelectedFile());
    }

    /**
     * Lets the user save the content to a file.
     */
    public void save() {
      int	retVal;
      String	msg;

      retVal = m_FileChooser.showSaveDialog(this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;

      msg = FileUtils.writeToFileMsg(m_FileChooser.getSelectedFile().getAbsolutePath(), m_PanelText.getContent(), false, null);
      if (msg != null)
	GUIHelper.showErrorMessage(GUIHelper.getParentComponent(this), msg);
    }

    /**
     * Copies the text to the clipboard.
     */
    public void copy() {
      ClipboardHelper.copyToClipboard(m_PanelText.getContent());
    }

    /**
     * Replaces the text with the one from the clipboard.
     */
    public void paste() {
      m_PanelText.setContent(ClipboardHelper.pasteStringFromClipboard());
    }

    /**
     * Adds the extension filter to the file chooser.
     *
     * @param filter	the filter to add
     */
    public void addExtensionFilter(ExtensionFileFilter filter) {
      m_FileChooser.addChoosableFileFilter(filter);
    }

    /**
     * Sets the info text to display.
     *
     * @param info	the text to display, null or empty string to disable
     */
    public void setInfoText(String info) {
      m_PanelText.setInfoText(info);
    }

    /**
     * Sets the content to display. Resets the modified state.
     *
     * @param value	the text
     */
    public void setContent(String value) {
      m_PanelText.setContent(value);
    }

    /**
     * Returns the content to display.
     *
     * @return		the text
     */
    public String getContent() {
      return m_PanelText.getContent();
    }
  }

  /** the first split pane. */
  protected BaseSplitPane m_SplitPane1;

  /** the second split pane. */
  protected BaseSplitPane m_SplitPane2;

  /** the XML panel. */
  protected SubPanel m_PanelXML;

  /** the XSL panel. */
  protected SubPanel m_PanelXSL;

  /** the output panel. */
  protected SubPanel m_PanelOutput;

  /** the buttons panel. */
  protected JPanel m_PanelButtons;

  /** the Apply button. */
  protected JButton m_ButtonApply;

  /** the menu item for validating. */
  protected JCheckBoxMenuItem m_MenuItemValidating;

  /** the menu item for namespace aware. */
  protected JCheckBoxMenuItem m_MenuItemNamespaceAware;

  /** the menu item for XInclude aware. */
  protected JCheckBoxMenuItem m_MenuItemXInludeAware;

  /** the menu item for expanding entity references. */
  protected JCheckBoxMenuItem m_MenuItemExpandEntityReferences;

  /** the menu item for ignoring comments. */
  protected JCheckBoxMenuItem m_MenuItemIgnoreComments;

  /** the menu item for ignoring whitespaces. */
  protected JCheckBoxMenuItem m_MenuItemIgnoreWhitespaces;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane1 = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane1.setResizeWeight(0.33);
    add(m_SplitPane1, BorderLayout.CENTER);

    m_SplitPane2 = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane2.setResizeWeight(0.5);
    m_SplitPane1.setRightComponent(m_SplitPane2);

    m_PanelXML = new SubPanel();
    m_PanelXML.setInfoText("XML");
    m_PanelXML.addExtensionFilter(new ExtensionFileFilter("XML files", "xml"));
    m_SplitPane1.setLeftComponent(m_PanelXML);

    m_PanelXSL = new SubPanel();
    m_PanelXSL.setInfoText("XSL");
    m_PanelXSL.addExtensionFilter(new ExtensionFileFilter("XSL files", new String[]{"xsl", "xslt"}));
    m_SplitPane2.setLeftComponent(m_PanelXSL);

    m_PanelOutput = new SubPanel();
    m_PanelOutput.setInfoText("Output");
    m_PanelOutput.addExtensionFilter(new ExtensionFileFilter("Text files", "txt"));
    m_SplitPane2.setRightComponent(m_PanelOutput);

    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(m_PanelButtons, BorderLayout.SOUTH);

    m_ButtonApply = new JButton("Apply", GUIHelper.getIcon("run.gif"));
    m_ButtonApply.addActionListener((ActionEvent e) -> apply());
    m_PanelButtons.add(m_ButtonApply);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenu	menu;
    JMenuItem 	menuitem;

    if (m_MenuBar == null) {
      m_MenuBar = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      m_MenuBar.add(menu);

      // File/Close
      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener((ActionEvent e) -> closeParent());
      menu.add(menuitem);

      // Options
      menu = new JMenu("Options");
      menu.setMnemonic('O');
      m_MenuBar.add(menu);

      // Options/Validating
      menuitem = new JCheckBoxMenuItem("Validating");
      menu.add(menuitem);
      m_MenuItemValidating = (JCheckBoxMenuItem) menuitem;

      // Options/Namespace aware
      menuitem = new JCheckBoxMenuItem("Namespace aware");
      menuitem.setSelected(true);
      menu.add(menuitem);
      m_MenuItemNamespaceAware = (JCheckBoxMenuItem) menuitem;

      // Options/XInclude aware
      menuitem = new JCheckBoxMenuItem("XInclude aware");
      menu.add(menuitem);
      m_MenuItemXInludeAware = (JCheckBoxMenuItem) menuitem;

      // Options/Expand entity references
      menuitem = new JCheckBoxMenuItem("Expand entity references");
      menu.add(menuitem);
      m_MenuItemExpandEntityReferences = (JCheckBoxMenuItem) menuitem;

      // Options/Ignoring comments
      menuitem = new JCheckBoxMenuItem("Ignoring comments");
      menuitem.setSelected(true);
      menu.add(menuitem);
      m_MenuItemIgnoreComments = (JCheckBoxMenuItem) menuitem;

      // Options/Ignoring whitespaces
      menuitem = new JCheckBoxMenuItem("Ignoring whitespaces");
      menuitem.setSelected(true);
      menu.add(menuitem);
      m_MenuItemIgnoreWhitespaces = (JCheckBoxMenuItem) menuitem;
    }

    return m_MenuBar;
  }

  /**
   * Performs XSLT.
   */
  public void apply() {
    DocumentBuilderFactory 	factory;
    DocumentBuilder 		builder;
    Document 			doc;
    DOMSource 			dsource;
    StreamSource 		stylesource;
    TransformerFactory 		tFactory;
    Transformer 		transformer;
    ByteArrayOutputStream 	ostream;
    StreamResult 		sresult;

    try {
      factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(m_MenuItemValidating.isSelected());
      factory.setNamespaceAware(m_MenuItemNamespaceAware.isSelected());
      factory.setXIncludeAware(m_MenuItemXInludeAware.isSelected());
      factory.setExpandEntityReferences(m_MenuItemExpandEntityReferences.isSelected());
      factory.setIgnoringComments(m_MenuItemIgnoreComments.isSelected());
      factory.setIgnoringElementContentWhitespace(m_MenuItemIgnoreWhitespaces.isSelected());
      builder     = factory.newDocumentBuilder();
      doc         = builder.parse(new ByteArrayInputStream(m_PanelXML.getContent().getBytes()));
      dsource     = new DOMSource(doc);
      stylesource = new StreamSource(new StringReader(m_PanelXSL.getContent()));
      tFactory    = TransformerFactory.newInstance();
      transformer = tFactory.newTransformer(stylesource);
      ostream     = new ByteArrayOutputStream();
      sresult     = new StreamResult(ostream);
      transformer.transform(dsource, sresult);
      m_PanelOutput.setContent(new String(ostream.toByteArray()));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(GUIHelper.getParentComponent(this), "Failed to apply XSL!", e);
    }
  }
}
