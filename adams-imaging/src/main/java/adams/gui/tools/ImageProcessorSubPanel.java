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
 * ImageProcessorSubPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.flow.control.SubProcess;
import adams.flow.core.AbstractActor;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.CustomPopupMenuProvider;
import adams.gui.core.GUIHelper;
import adams.gui.core.Undo;
import adams.gui.flow.FlowPanel;
import adams.gui.visualization.image.ImagePanel;

/**
 * A panel with three panes: original image, processed image and flow for
 * processing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageProcessorSubPanel
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5617124372054881553L;

  /**
   * The type of layout to use.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum LayoutType {
    HORIZONTAL,
    VERTICAL
  }
  
  /**
   * Custom popup menu provider for the {@link ImagePanel} instances.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ImagePopupMenuProvider
    implements CustomPopupMenuProvider {

    /** the {@link ImagePanel} this provider is for. */
    protected ImagePanel m_Panel;
    
    /**
     * Initializes the provider.
     * 
     * @param panel	the panel this provider is for
     */
    public ImagePopupMenuProvider(ImagePanel panel) {
      if (panel == null)
	throw new IllegalArgumentException("ImagePanel instance cannot be null!");
      m_Panel = panel;
    }
    
    /**
     * Returns the {@link ImagePanel} this provider is for.
     * 
     * @return		the panel
     */
    public ImagePanel getPanel() {
      return m_Panel;
    }
    
    /**
     * Creates a popup menu for the given mouse event.
     * 
     * @param e		the event that triggered the request
     * @return		the menu, null if none was generated
     */
    @Override
    public JPopupMenu getCustomPopupMenu(MouseEvent e) {
      JPopupMenu	menu;
      JMenuItem		menuitem;
      JMenu		submenu;
      int		i;
      int[]		zooms;

      menu = new JPopupMenu();

      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setEnabled(getPanel().getCurrentImage() != null);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  GUIHelper.copyToClipboard(getPanel().getCurrentImage());
	}
      });
      menu.add(menuitem);

      menuitem = new JMenuItem("Export...", GUIHelper.getIcon("save.gif"));
      menuitem.setEnabled(getPanel().getCurrentImage() != null);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getPanel().export();
	}
      });
      menu.add(menuitem);

      // View/Zoom
      submenu = new JMenu("Zoom");
      menu.addSeparator();
      menu.add(submenu);
      submenu.setIcon(GUIHelper.getIcon("glasses.gif"));

      //View/Zoom/Zoom in
      menuitem = new JMenuItem("Zoom in");
      submenu.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_Panel.setScale(m_Panel.getActualScale() * 1.5);
	}
      });

      //View/Zoom/Zoom out
      menuitem = new JMenuItem("Zoom out");
      submenu.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_Panel.setScale(m_Panel.getActualScale() / 1.5);
	}
      });

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
      submenu.addSeparator();
      for (i = 0; i < zooms.length; i++) {
	final int fZoom = zooms[i];
	if (zooms[i] == -100)
	  menuitem = new JMenuItem("Best fit");
	else
	  menuitem = new JMenuItem(zooms[i] + "%");
	submenu.add(menuitem);
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    m_Panel.setScale(fZoom / 100.0);
	  }
	});
      }

      return menu;
    }
  }
  
  /** the layout. */
  protected LayoutType m_LayoutType;
  
  /** the original image. */
  protected ImagePanel m_PanelOriginal;
  
  /** the processed image. */
  protected ImagePanel m_PanelProcessed;
  
  /** the flow panel. */
  protected FlowPanel m_PanelFlow;
  
  /** the "check flow" button. */
  protected JButton m_ButtonCheck;
  
  /** the "run flow" button. */
  protected JButton m_ButtonRun;
  
  /** the splitpane for images/flow. */
  protected BaseSplitPane m_SplitImagesFlow;
  
  /** the splitpane for the images. */
  protected BaseSplitPane m_SplitImages;
  
  /**
   * Initializes the panel with a vertical layout.
   */
  public ImageProcessorSubPanel() {
    this(LayoutType.VERTICAL);
  }
  
  /**
   * Initializes the panel.
   * 
   * @param layout	the layout to use
   */
  public ImageProcessorSubPanel(LayoutType layout) {
    super();
    setLayoutType(layout);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LayoutType = LayoutType.VERTICAL;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel			panel;
    JPanel			panelButtons;
    ImagePopupMenuProvider	popup;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitImagesFlow = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitImagesFlow.setResizeWeight(1.0);
    add(m_SplitImagesFlow, BorderLayout.CENTER);

    m_PanelFlow   = new FlowPanel();
    m_PanelFlow.setMinimumSize(new Dimension(250, 0));
    m_PanelFlow.getUndo().clear();
    m_PanelFlow.setCurrentFlow(new SubProcess());
    panel = new JPanel(new BorderLayout());    
    panel.add(m_PanelFlow, BorderLayout.CENTER);
    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);
    
    m_ButtonCheck = new JButton("Check", GUIHelper.getIcon("validate.png"));
    m_ButtonCheck.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	checkFlow(false);
      }
    });
    panelButtons.add(m_ButtonCheck);
    
    m_ButtonRun = new JButton("Run", GUIHelper.getIcon("run.gif"));
    m_ButtonRun.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	runFlow();
      }
    });
    panelButtons.add(m_ButtonRun);
    
    m_SplitImages = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitImages.setResizeWeight(0.5);
    m_SplitImagesFlow.setLeftComponent(m_SplitImages);
    m_SplitImagesFlow.setRightComponent(panel);
    
    m_PanelOriginal = new ImagePanel();
    m_PanelOriginal.setShowProperties(false);
    m_PanelOriginal.setShowLog(false);
    popup = new ImagePopupMenuProvider(m_PanelOriginal);
    m_PanelOriginal.setCustomPopupMenuProvider(popup);
    m_PanelProcessed = new ImagePanel();
    m_PanelProcessed.setShowProperties(false);
    m_PanelProcessed.setShowLog(false);
    popup = new ImagePopupMenuProvider(m_PanelProcessed);
    m_PanelProcessed.setCustomPopupMenuProvider(popup);
    
    m_SplitImages.setTopComponent(m_PanelOriginal);
    m_SplitImages.setBottomComponent(m_PanelProcessed);
  }
  
  /**
   * Updates the layout based on the current {@link LayoutType}.
   * 
   * @see #m_LayoutType
   */
  protected void updateLayout() {
    switch (m_LayoutType) {
      case HORIZONTAL:
	m_SplitImages.setOrientation(BaseSplitPane.HORIZONTAL_SPLIT);
	break;
      case VERTICAL:
	m_SplitImages.setOrientation(BaseSplitPane.VERTICAL_SPLIT);
	break;
      default:
	throw new IllegalStateException("Unhandled layout type: " + m_LayoutType);
    }
  }
  
  /**
   * Sets the layout to use.
   * 
   * @param value	the layout
   */
  public void setLayoutType(LayoutType value) {
    m_LayoutType = value;
    updateLayout();
  }
  
  /**
   * Returns the layout in use.
   * 
   * @return		the layout
   */
  public LayoutType getLayoutType() {
    return m_LayoutType;
  }
  
  /**
   * Returns the panel for the original image.
   * 
   * @return		the panel
   */
  public ImagePanel getOriginal() {
    return m_PanelOriginal;
  }
  
  /**
   * Returns the panel for the original image.
   * 
   * @return		the panel
   */
  public ImagePanel getProcessed() {
    return m_PanelProcessed;
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
   * Opens the file with the specified image reader.
   *
   * @param file	the file to open
   * @return		true if successfully read
   */
  public boolean load(File file) {
    boolean	result;
    
    result = m_PanelOriginal.load(file);
    if (result)
      m_PanelProcessed.setCurrentImage(m_PanelOriginal.getCurrentImage());
    
    return result;
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return m_PanelOriginal.getCurrentFile();
  }

  /**
   * Checks the flow.
   * 
   * @param silent	only pops up a dialog if invalid flow
   * @return		true if flow ok
   */
  protected boolean checkFlow(boolean silent) {
    AbstractActor	actor;
    SubProcess		sub;
    String		msg;
    Compatibility	comp;
    
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
    BufferedImageContainer	contIn;
    SubProcess			sub;
    String			msg;
    AbstractImageContainer	contOut;
    
    if (!checkFlow(true))
      return;
    
    contIn = new BufferedImageContainer();
    contIn.setImage(m_PanelOriginal.getCurrentImage());
    contOut = null;
    
    sub = (SubProcess) m_PanelFlow.getCurrentFlow();
    msg = sub.setUp();
    if (msg == null) {
      sub.input(new Token(contIn));
      msg = sub.execute();
    }
    if (msg == null) {
      if (sub.hasPendingOutput())
	contOut = (AbstractImageContainer) sub.output().getPayload();
      else
	msg = "No output produced?";
    }
    sub.cleanUp();
    sub.destroy();
    
    if (contOut != null) {
      m_PanelProcessed.setCurrentImage(contOut.toBufferedImage());
      m_PanelProcessed.setScale(-1.0);
    }
    else if (msg != null) {
      GUIHelper.showErrorMessage(this, "Flow execution failed:\n" + msg);
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
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_PanelOriginal.cleanUp();
    m_PanelProcessed.cleanUp();
    m_PanelFlow.cleanUp();
  }
}
