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
 * ImageProcessorSubPanel.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.flow.control.SubProcess;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.CustomPopupMenuProvider;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;

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
    public BasePopupMenu getCustomPopupMenu(MouseEvent e) {
      BasePopupMenu	menu;
      JMenuItem		menuitem;
      JMenu		submenu;
      int		i;
      int[]		zooms;

      menu = new BasePopupMenu();

      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setEnabled(getPanel().getCurrentImage() != null);
      menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(getPanel().getCurrentImage()));
      menu.add(menuitem);

      menuitem = new JMenuItem("Export...", GUIHelper.getIcon("save.gif"));
      menuitem.setEnabled(getPanel().getCurrentImage() != null);
      menuitem.addActionListener((ActionEvent ae) -> getPanel().export());
      menu.add(menuitem);

      menuitem = new JMenuItem("Save report...", GUIHelper.getEmptyIcon());
      menuitem.setEnabled(getPanel().getCurrentImage() != null);
      menuitem.addActionListener((ActionEvent ae) ->getPanel().saveReport());
      menu.add(menuitem);

      // separator
      menu.addSeparator();

      // View/Zoom
      submenu = new JMenu("Zoom");
      menu.add(submenu);
      submenu.setIcon(GUIHelper.getIcon("glasses.gif"));

      //View/Zoom/Zoom in
      menuitem = new JMenuItem("Zoom in");
      submenu.add(menuitem);
      menuitem.addActionListener((ActionEvent ae) -> m_Panel.setScale(m_Panel.getActualScale() * 1.5));

      //View/Zoom/Zoom out
      menuitem = new JMenuItem("Zoom out");
      submenu.add(menuitem);
      menuitem.addActionListener((ActionEvent ae) -> m_Panel.setScale(m_Panel.getActualScale() / 1.5));

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
	menuitem.addActionListener((ActionEvent ae) -> m_Panel.setScale(fZoom / 100.0));
      }

      return menu;
    }
  }

  /** the owner. */
  protected ImageProcessorTabbedPane m_Owner;
  
  /** the layout. */
  protected LayoutType m_LayoutType;
  
  /** the original image. */
  protected ImagePanel m_PanelOriginal;
  
  /** the processed image. */
  protected ImagePanel m_PanelProcessed;

  /** the splitpane for the images. */
  protected BaseSplitPane m_SplitImages;

  /**
   * Initializes the panel with a vertical layout.
   *
   * @param owner	the owner
   */
  public ImageProcessorSubPanel(ImageProcessorTabbedPane owner) {
    this(owner, LayoutType.VERTICAL);
  }
  
  /**
   * Initializes the panel.
   * 
   * @param owner	the owner
   * @param layout	the layout to use
   */
  public ImageProcessorSubPanel(ImageProcessorTabbedPane owner, LayoutType layout) {
    super();
    m_Owner = owner;
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
    ImagePopupMenuProvider	popup;
    
    super.initGUI();
    
    setLayout(new BorderLayout());

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

    m_SplitImages = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitImages.setResizeWeight(0.5);
    m_SplitImages.setTopComponent(m_PanelOriginal);
    m_SplitImages.setBottomComponent(m_PanelProcessed);
    add(m_SplitImages, BorderLayout.CENTER);
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
   * Opens the file.
   *
   * @param file	the file to open
   * @return		true if successfully read
   */
  public boolean load(File file) {
    return load(file, null);
  }

  /**
   * Opens the file with the specified image reader.
   *
   * @param file	the file to open
   * @param reader	the reader to use, null for auto-detection
   * @return		true if successfully read
   */
  public boolean load(File file, AbstractImageReader reader) {
    boolean	result;

    result = m_PanelOriginal.load(file, reader);
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
   * Returns the flow panel.
   *
   * @return		the panel
   */
  public FlowPanel getFlowPanel() {
    return m_Owner.m_Owner.getFlow();
  }

  /**
   * Runs the flow.
   *
   * @return		null if successful, otherwise error message
   */
  public String runFlow() {
    String			result;
    BufferedImageContainer	contIn;
    SubProcess			sub;
    String			msg;
    AbstractImageContainer	contOut;

    result = null;

    contIn = new BufferedImageContainer();
    contIn.setImage(m_PanelOriginal.getCurrentImage());
    contOut = null;
    
    sub = (SubProcess) getFlowPanel().getCurrentFlow();
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
    
    if (contOut != null)
      m_PanelProcessed.setCurrentImage(contOut.toBufferedImage(), m_PanelProcessed.getScale());
    else if (msg != null)
      result = "Flow execution failed:\n" + msg;

    return result;
  }

  /**
   * Adds the overlay.
   *
   * @param original	whether to add the overlay in the original image or the processed one
   */
  public void addImageOverlay(boolean original, ImageOverlay overlay) {
    if (original)
      m_PanelOriginal.addImageOverlay(overlay);
    else
      m_PanelProcessed.addImageOverlay(overlay);
  }

  /**
   * Removes all overlays.
   * 
   * @param original	whether to clear the overlays in the original image or the processed one
   */
  public void clearImageOverlays(boolean original) {
    if (original)
      m_PanelOriginal.clearImageOverlays();
    else
      m_PanelProcessed.clearImageOverlays();
  }

  /**
   * Returns iterator over current overlays.
   * 
   * @param original	whether to locate objects in the original image or the processed one
   * @return		the iterator
   */
  public Iterator<ImageOverlay> imageOverlays(boolean original) {
    if (original)
      return m_PanelOriginal.imageOverlays();
    else
      return m_PanelProcessed.imageOverlays();
  }
  
  /**
   * Uses the specified object locator.
   * 
   * @param original	whether to locate objects in the original image or the processed one
   * @param locator	the locator to use
   */
  public void locateObjects(boolean original, AbstractObjectLocator locator) {
    LocatedObjects			located;
    ObjectLocationsOverlayFromReport	overlay;

    // locate objects
    clearImageOverlays(original);
    overlay = new ObjectLocationsOverlayFromReport();
    if (original) {
      m_PanelOriginal.addImageOverlay(overlay);
      located = locator.locate(m_PanelOriginal.getCurrentImage());
      m_PanelOriginal.setAdditionalProperties(located.toReport(overlay.getPrefix()));
    }
    else {
      m_PanelProcessed.addImageOverlay(overlay);
      located = locator.locate(m_PanelProcessed.getCurrentImage());
      m_PanelProcessed.setAdditionalProperties(located.toReport(overlay.getPrefix()));
    }
    
  }

  /**
   * Sets the scaling factor (0-16). Use -1 to fit inside panel.
   *
   * @param original	if true setting the scale for the original,
   *                    otherwise for the processed image
   * @param value	the scaling factor
   */
  public void setScale(boolean original, double value) {
    if (original)
      m_PanelOriginal.setScale(value);
    else
      m_PanelProcessed.setScale(value);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_PanelOriginal.cleanUp();
    m_PanelProcessed.cleanUp();
  }
}
