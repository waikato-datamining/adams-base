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
 * ImageProcessorPanel.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.core.MessageCollection;
import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.FlowReader;
import adams.data.io.output.FlowWriter;
import adams.flow.control.SubProcess;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.PassThrough;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.TitleGenerator;
import adams.gui.core.Undo;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.flow.FlowPanel;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.ImageProcessorSubPanel.LayoutType;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImageViewerPanel;
import adams.gui.visualization.image.NullOverlay;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Interface for processing images using a flow snippet.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageProcessorPanel
  extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5882173310359920644L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ImageProcessorSession.props";

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the tabbed pane for the images. */
  protected ImageProcessorMultiPagePane m_MultiPagePane;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the menu item "open". */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the menu item "close". */
  protected JMenuItem m_MenuItemFileClose;

  /** the menu item "close all". */
  protected JMenuItem m_MenuItemFileCloseAll;

  /** the menu item "horizontal". */
  protected JMenuItem m_MenuItemViewHorizontal;

  /** the menu item "vertical". */
  protected JMenuItem m_MenuItemViewVertical;

  /** the menu item "undo". */
  protected JMenuItem m_MenuItemEditUndo;

  /** the menu item "redo". */
  protected JMenuItem m_MenuItemEditRedo;

  /** the menu "locate objects". */
  protected JMenuItem m_MenuViewLocateObjects;

  /** the menu "remove overlays". */
  protected JMenuItem m_MenuViewRemoveOverlays;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the file chooser for the pictures. */
  protected ImageFileChooser m_FileChooserImage;

  /** the file chooser for the flows. */
  protected FlowFileChooser m_FileChooserFlow;

  /** the flow panel. */
  protected FlowPanel m_PanelFlow;

  /** the label for the progress. */
  protected JLabel m_LabelProgress;

  /** the "new flow" button. */
  protected BaseButton m_ButtonNew;

  /** the "load flow" button. */
  protected BaseButton m_ButtonLoad;

  /** the "save flow" button. */
  protected BaseButton m_ButtonSave;

  /** the "check flow" button. */
  protected BaseButton m_ButtonCheck;

  /** the "run flow" button. */
  protected BaseButton m_ButtonRun;

  /** the last object locator in use (original). */
  protected AbstractObjectLocator m_LastObjectLocatorOriginal;

  /** the last object locator in use (processed). */
  protected AbstractObjectLocator m_LastObjectLocatorProcessed;

  /** the last image overlay in use (original). */
  protected ImageOverlay m_LastImageOverlayOriginal;

  /** the last image overlay in use (processed). */
  protected ImageOverlay m_LastImageOverlayProcessed;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler = null;
    m_TitleGenerator     = new TitleGenerator("Image processor", true);
    m_FileChooserImage = new ImageFileChooser();
    m_FileChooserImage.setCurrentDirectory(new File(ImageViewerPanel.getProperties().getPath("InitialDir", "%h")));
    m_FileChooserImage.setAutoAppendExtension(true);
    m_FileChooserImage.setMultiSelectionEnabled(true);
    m_FileChooserFlow = new FlowFileChooser(FlowPanel.getProperties().getPath("InitialDir", "%h"));
    m_FileChooserFlow.setMultiSelectionEnabled(false);
    m_LastObjectLocatorOriginal  = new PassThrough();
    m_LastObjectLocatorProcessed = new PassThrough();
    m_LastImageOverlayOriginal   = new NullOverlay();
    m_LastImageOverlayProcessed  = new NullOverlay();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelBottom;
    JPanel	panelStatus;
    JPanel	panelButtons;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);

    m_MultiPagePane = new ImageProcessorMultiPagePane(this);
    m_MultiPagePane.addChangeListener((ChangeEvent e) -> update());
    m_SplitPane.setLeftComponent(m_MultiPagePane);

    m_PanelFlow   = new FlowPanel();
    m_PanelFlow.getTitleGenerator().setEnabled(false);
    m_PanelFlow.setMinimumSize(new Dimension(400, 0));
    m_PanelFlow.getUndo().clear();
    panel = new JPanel(new BorderLayout());
    panel.add(m_PanelFlow, BorderLayout.CENTER);
    panelBottom = new JPanel(new BorderLayout());
    panel.add(panelBottom, BorderLayout.SOUTH);
    panelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBottom.add(panelStatus, BorderLayout.WEST);
    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBottom.add(panelButtons, BorderLayout.EAST);

    m_LabelProgress = new JLabel();
    panelStatus.add(m_LabelProgress);

    m_ButtonNew = new BaseButton(GUIHelper.getIcon("new.gif"));
    m_ButtonNew.addActionListener((ActionEvent e) -> newFlow());
    panelButtons.add(m_ButtonNew);

    m_ButtonLoad = new BaseButton(GUIHelper.getIcon("open.gif"));
    m_ButtonLoad.addActionListener((ActionEvent e) -> loadFlow());
    panelButtons.add(m_ButtonLoad);

    m_ButtonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> saveFlow());
    panelButtons.add(m_ButtonSave);

    m_ButtonCheck = new BaseButton(GUIHelper.getIcon("validate.png"));
    m_ButtonCheck.addActionListener((ActionEvent e) -> checkFlow(false));
    panelButtons.add(m_ButtonCheck);

    m_ButtonRun = new BaseButton(GUIHelper.getIcon("run.gif"));
    m_ButtonRun.addActionListener((ActionEvent e) -> runFlow());
    panelButtons.add(m_ButtonRun);
    m_SplitPane.setRightComponent(panel);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    newFlow();
  }

  /**
   * Updates the title and menu.
   */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Returns the title generator in use.
   *
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updats the title of the dialog/frame if applicable.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;
    setParentTitle(m_TitleGenerator.generate(getCurrentFile()));
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return m_MultiPagePane.getCurrentFile();
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    return m_MultiPagePane.getFileAt(index);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    ImageProcessorSubPanel	panel;
    boolean			hasPanel;

    if (m_MenuBar == null)
      return;

    panel    = getCurrentPanel();
    hasPanel = (panel != null);

    // File
    m_MenuItemFileLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemFileClose.setEnabled(hasPanel);

    // Edit
    if (getUndo().canUndo()) {
      m_MenuItemEditUndo.setEnabled(true);
      m_MenuItemEditUndo.setText("Undo - " + getUndo().peekUndoComment());
    }
    else {
      m_MenuItemEditUndo.setEnabled(false);
      m_MenuItemEditUndo.setText("Undo");
    }
    if (getUndo().canRedo()) {
      m_MenuItemEditRedo.setEnabled(true);
      m_MenuItemEditRedo.setText("Redo - " + getUndo().peekRedoComment());
    }
    else {
      m_MenuItemEditRedo.setEnabled(false);
      m_MenuItemEditRedo.setText("Redo");
    }

    // View
    m_MenuItemViewHorizontal.setEnabled(hasPanel);
    m_MenuItemViewVertical.setEnabled(hasPanel);
    m_MenuViewLocateObjects.setEnabled(hasPanel);
    m_MenuViewRemoveOverlays.setEnabled(hasPanel);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenu		subsubmenu;
    JMenuItem		menuitem;
    ButtonGroup		group;
    int			i;
    int[]		zooms;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener((ActionEvent e) -> open());
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load(e.getItem().getFile(), (AbstractImageReader) e.getItem().getHandler());
	}
      });
      m_MenuItemFileLoadRecent = submenu;

      // File/Close page
      menuitem = new JMenuItem("Close page");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setIcon(GUIHelper.getIcon("close_tab_focused.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
      menuitem.addActionListener((ActionEvent e) -> close());
      m_MenuItemFileClose = menuitem;

      // File/Close all pages
      menuitem = new JMenuItem("Close all pages");
      menu.add(menuitem);
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("delete_all.gif"));
      menuitem.addActionListener((ActionEvent e) -> closeAll());
      m_MenuItemFileCloseAll = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> exit());

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menu.add(menuitem);
      menuitem.setMnemonic('U');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
      menuitem.addActionListener((ActionEvent e) -> undo());
      m_MenuItemEditUndo = menuitem;

      // Edit/Redo
      menuitem = new JMenuItem("Redo");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
      menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
      menuitem.addActionListener((ActionEvent e) -> redo());
      m_MenuItemEditRedo = menuitem;

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      group = new ButtonGroup();

      // View/Horizontal
      menuitem = new JRadioButtonMenuItem("Horizontal");
      menu.add(menuitem);
      menuitem.setMnemonic('H');
      menuitem.setSelected(false);
      menuitem.setIcon(GUIHelper.getIcon("ip_layout_horizontal.png"));
      menuitem.addActionListener((ActionEvent e) -> {
        for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.setLayoutType(LayoutType.HORIZONTAL);
	updateMenu();
      });
      group.add(menuitem);
      m_MenuItemViewHorizontal = menuitem;

      // View/Vertical
      menuitem = new JRadioButtonMenuItem("Vertical");
      menu.add(menuitem);
      menuitem.setMnemonic('V');
      menuitem.setSelected(true);
      menuitem.setIcon(GUIHelper.getIcon("ip_layout_vertical.png"));
      menuitem.addActionListener((ActionEvent e) -> {
        for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.setLayoutType(LayoutType.VERTICAL);
	updateMenu();
      });
      group.add(menuitem);
      m_MenuItemViewVertical = menuitem;

      menu.addSeparator();

      // View/Add overlay
      submenu = new JMenu("Add overlay");
      menu.add(submenu);
      submenu.setMnemonic('A');
      submenu.setIcon(GUIHelper.getIcon("add.gif"));
      m_MenuViewRemoveOverlays = submenu;

      // View/Add overlay/Original
      menuitem = new JMenuItem("Original");
      submenu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.addActionListener((ActionEvent e) -> {
        ImageOverlay overlay = selectImageOverlay(m_LastImageOverlayOriginal);
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.addImageOverlay(true, overlay);
      });

      // View/Add overlay/Processed
      menuitem = new JMenuItem("Processed");
      submenu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener((ActionEvent e) -> {
        ImageOverlay overlay = selectImageOverlay(m_LastImageOverlayOriginal);
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.addImageOverlay(false, overlay);
      });

      // View/Remove overlays
      submenu = new JMenu("Remove overlays");
      menu.add(submenu);
      submenu.setMnemonic('R');
      submenu.setIcon(GUIHelper.getIcon("remove.gif"));
      m_MenuViewRemoveOverlays = submenu;

      // View/Remove overlays/Original
      menuitem = new JMenuItem("Original");
      submenu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.addActionListener((ActionEvent e) -> {
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.clearImageOverlays(true);
      });

      // View/Remove overlays/Processed
      menuitem = new JMenuItem("Processed");
      submenu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener((ActionEvent e) -> {
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.clearImageOverlays(false);
      });

      // View/Locate objects
      submenu = new JMenu("Locate objects");
      menu.add(submenu);
      submenu.setMnemonic('L');
      submenu.setIcon(GUIHelper.getIcon("locateobjects.gif"));
      m_MenuViewLocateObjects = submenu;

      // View/Locate objects/Original
      menuitem = new JMenuItem("Original");
      submenu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.addActionListener((ActionEvent e) -> {
        AbstractObjectLocator loc = selectObjectLocator(m_LastObjectLocatorOriginal);
        if (loc == null)
          return;
        m_LastObjectLocatorOriginal = loc;
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.locateObjects(true, loc);
      });

      // View/Locate objects/Processed
      menuitem = new JMenuItem("Processed");
      submenu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener((ActionEvent e) -> {
        AbstractObjectLocator loc = selectObjectLocator(m_LastObjectLocatorOriginal);
        if (loc == null)
          return;
        m_LastObjectLocatorOriginal = loc;
	for (ImageProcessorSubPanel panel: getAllPanels())
	  panel.locateObjects(false, loc);
      });

      // View/Zoom
      submenu = new JMenu("Zoom");
      menu.add(submenu);
      submenu.setIcon(GUIHelper.getIcon("glasses.gif"));

      // View/Zoom/Original
      subsubmenu = new JMenu("Original");
      submenu.add(subsubmenu);

      // zoom levels
      zooms = new int[]{
	  -100,
	  25,
	  50,
	  66,
	  75,
	  100,
	  150,
	  200,
	  400,
	  800};
      for (i = 0; i < zooms.length; i++) {
	final int fZoom = zooms[i];
	if (zooms[i] == -100)
	  menuitem = new JMenuItem("Best fit");
	else
	  menuitem = new JMenuItem(zooms[i] + "%");
	subsubmenu.add(menuitem);
	menuitem.addActionListener((ActionEvent ae) -> setScale(fZoom / 100.0, true));
      }

      // View/Zoom/Processed
      subsubmenu = new JMenu("Processed");
      submenu.add(subsubmenu);

      // zoom levels
      zooms = new int[]{
	  -100,
	  25,
	  50,
	  66,
	  75,
	  100,
	  150,
	  200,
	  400,
	  800};
      for (i = 0; i < zooms.length; i++) {
	final int fZoom = zooms[i];
	if (zooms[i] == -100)
	  menuitem = new JMenuItem("Best fit");
	else
	  menuitem = new JMenuItem(zooms[i] + "%");
	subsubmenu.add(menuitem);
	menuitem.addActionListener((ActionEvent ae) -> setScale(fZoom / 100.0, false));
      }

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImageProcessorSubPanel getCurrentPanel() {
    return m_MultiPagePane.getCurrentPanel();
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImageProcessorSubPanel getPanelAt(int index) {
    return m_MultiPagePane.getPanelAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImageProcessorSubPanel[] getAllPanels() {
    return m_MultiPagePane.getAllPanels();
  }

  /**
   * Opens an image.
   */
  protected void open() {
    int		retVal;
    File[]	files;

    retVal = m_FileChooserImage.showOpenDialog(this);
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    files = m_FileChooserImage.getSelectedFiles();
    for (File file: files)
      load(file, m_FileChooserImage.getImageReader());
  }

  /**
   * Loads the specified file in a new panel. Uses default reader.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    load(file, m_FileChooserImage.getReaderForFile(file));
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   * @param reader      the reader to use
   */
  public void load(File file, AbstractImageReader reader) {
    if (m_MultiPagePane.load(file, reader)) {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(new Setup(file, reader));
    }
    update();
  }

  /**
   * Closes the current image.
   */
  protected void close() {
    int				index;
    ImageProcessorSubPanel	panel;

    index = m_MultiPagePane.getSelectedIndex();
    panel = getPanelAt(index);
    panel.cleanUp();
    m_MultiPagePane.remove(index);
    update();
  }

  /**
   * Closes all images.
   */
  protected void closeAll() {
    m_MultiPagePane.removeAllPages();
    update();
  }

  /**
   * Exits the viewer.
   */
  protected void exit() {
    int		i;

    i = 0;
    while (i < m_MultiPagePane.getPageCount())
      m_MultiPagePane.remove(i);

    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    return getFlow().getUndo();
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return getFlow().isUndoSupported();
  }

  /**
   * peforms an undo if possible.
   */
  public void undo() {
    getFlow().undo();
  }

  /**
   * peforms an redo if possible.
   */
  public void redo() {
    getFlow().redo();
  }

  /**
   * Returns the panel for the flow.
   *
   * @return		the panel
   */
  public FlowPanel getFlow() {
    return m_PanelFlow;
  }

  /**
   * Replaces the current flow snippet with an empty one.
   */
  protected void newFlow() {
    m_PanelFlow.setCurrentFlow(new SubProcess());
  }

  /**
   * Allows the user to load a flow snippet.
   */
  protected void loadFlow() {
    int		retVal;
    FlowReader	reader;
    Actor	actor;

    retVal = m_FileChooserFlow.showOpenDialog(this);
    if (retVal != FlowFileChooser.APPROVE_OPTION)
      return;

    reader = m_FileChooserFlow.getReader();
    actor  = reader.readActor(m_FileChooserFlow.getSelectedFile());
    if (actor instanceof SubProcess)
      m_PanelFlow.setCurrentFlow(actor);
    else
      GUIHelper.showErrorMessage(
        this, "The outermost actor in the flow must a " + SubProcess.class.getName()
	  + ", encountered: " + actor.getClass().getName());
  }

  /**
   * Allows the user to save the current flow snippet.
   */
  protected void saveFlow() {
    int		retVal;
    FlowWriter	writer;

    retVal = m_FileChooserFlow.showSaveDialog(this);
    if (retVal != FlowFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooserFlow.getWriter();
    if (!writer.write(m_PanelFlow.getCurrentFlow(), m_FileChooserFlow.getSelectedFile()))
      GUIHelper.showErrorMessage(
        this, "Failed to write flow snippet to: " + m_FileChooserFlow.getSelectedFile());
  }

  /**
   * Checks the flow.
   *
   * @param silent	only pops up a dialog if invalid flow
   * @return		true if flow ok
   */
  protected boolean checkFlow(boolean silent) {
    Actor actor;
    SubProcess		sub;
    String		msg;
    Compatibility comp;

    msg   = null;
    actor = m_PanelFlow.getCurrentFlow();
    sub   = null;

    // subprocess?
    if (!(actor instanceof SubProcess))
      msg = "Outermost actor must be a " + SubProcess.class.getName() + ", found: " + actor.getClass().getName();
    else
      sub = (SubProcess) actor;

    // check compatibility with images
    if (msg == null) {
      comp = new Compatibility();
      if (!comp.isCompatible(new Class[]{AbstractImageContainer.class}, sub.accepts()))
	msg = "Flow snippet does not accept " + AbstractImageContainer.class.getClass() + ", found: " + Utils.classesToString(sub.accepts());
      else if (!comp.isCompatible(sub.generates(), new Class[]{AbstractImageContainer.class}))
	msg = "Flow snippet does not generate " + AbstractImageContainer.class.getClass() + ", found: " + Utils.classesToString(sub.generates());
    }

    if (msg != null)
      GUIHelper.showErrorMessage(this, "Flow failed test:\n" + msg);
    else if (!silent)
      GUIHelper.showInformationMessage(this,"Flow passed test!");

    return (msg == null);
  }

  /**
   * Runs the flow.
   */
  protected void runFlow() {
    SwingWorker 	worker;

    if (!checkFlow(true))
      return;

    worker = new SwingWorker() {
      protected MessageCollection m_Errors;
      @Override
      protected Object doInBackground() throws Exception {
        m_Errors = new MessageCollection();
        m_ButtonCheck.setEnabled(false);
        m_ButtonRun.setEnabled(false);
        ImageProcessorSubPanel[] panels = getAllPanels();
	for (int i = 0; i < panels.length; i++) {
	  m_LabelProgress.setText((i+1) + "/" + panels.length + "...");
	  String msg = panels[i].runFlow();
	  if (msg != null) {
	    m_Errors.add(
	      "\nFile #" + (i + 1) + ": " + panels[i].getCurrentFile().getName()
		+ " encountered the following error:\n" + msg);
	  }
	}
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_LabelProgress.setText("");
        m_ButtonCheck.setEnabled(true);
        m_ButtonRun.setEnabled(true);
        if (!m_Errors.isEmpty())
          GUIHelper.showErrorMessage(ImageProcessorPanel.this, m_Errors.toString().trim());
      }
    };
    worker.execute();
  }

  /**
   * Displays a dialog for the user to configure an image overlay and then
   * returns it.
   *
   * @return		the image overaly, null if cancelled
   */
  public ImageOverlay selectImageOverlay(ImageOverlay last) {
    ImageOverlay		result;
    GenericObjectEditorDialog	dialog;

    result = null;

    // create dialog
    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Image overlay");
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(ImageOverlay.class);
    dialog.setCurrent(last);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      result = (ImageOverlay) dialog.getCurrent();
    dialog.dispose();

    return result;
  }

  /**
   * Displays a dialog for the user to configure an object locator and then
   * returns it.
   *
   * @return		the object locator, null if cancelled
   */
  public AbstractObjectLocator selectObjectLocator(AbstractObjectLocator last) {
    AbstractObjectLocator		result;
    GenericObjectEditorDialog		dialog;

    result = null;

    // create dialog
    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Locate objects");
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(AbstractObjectLocator.class);
    dialog.setCurrent(last);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      result = (AbstractObjectLocator) dialog.getCurrent();
    dialog.dispose();

    return result;
  }

  /**
   * Sets the scaling factor (0-16). Use -1 to fit inside panel.
   *
   * @param value	the scaling factor
   * @param original 	if true setting the scale for the original,
   *                    otherwise for the processed image
   */
  public void setScale(double value, boolean original) {
    for (ImageProcessorSubPanel panel: getAllPanels())
      panel.setScale(original, value);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_MultiPagePane.cleanUp();
    m_PanelFlow.cleanUp();
  }
}
