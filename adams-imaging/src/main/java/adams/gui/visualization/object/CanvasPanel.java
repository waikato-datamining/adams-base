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
 * CanvasPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object;

import adams.data.RoundingUtils;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.AbstractImageWriter;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.image.interactionlogging.Null;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * For drawing the image and overlays.
 */
public class CanvasPanel
  extends BasePanel {

  private static final long serialVersionUID = 276009384422635395L;

  /** the owner. */
  protected ObjectAnnotationPanel m_Owner;

  /** the image to display. */
  protected BufferedImage m_Image;

  /** whether to use best fit. */
  protected boolean m_BestFit;

  /** the zoom (1.0 = 100%). */
  protected double m_Zoom;

  /** the actual zoom to use. */
  protected double m_ActualZoom;

  /** the brightness. */
  protected float m_Brightness;

  /** the last brightness. */
  protected Float m_LastBrightness;

  /** the brightened image. */
  protected BufferedImage m_BrightImage;

  /** whether a resize is required. */
  protected boolean m_ResizeRequired;

  /** first display. */
  protected boolean m_FirstDisplay;

  /** the interaction logger in use. */
  protected InteractionLoggingFilter m_InteractionLoggingFilter;

  /** the popup menu customizer to use. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;

  /** the file dialog for saving the image. */
  protected ImageFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner          = null;
    m_Image          = null;
    m_BestFit        = false;
    m_Zoom           = 1.0;
    m_ActualZoom     = 1.0;
    m_Brightness     = 100f;
    m_ResizeRequired = false;
    m_FirstDisplay   = true;
    m_FileChooser    = null;
    m_InteractionLoggingFilter = new Null();
    m_PopupMenuCustomizer      = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    addMouseWheelListener((MouseWheelEvent e) -> {
      double oldZoom = m_Owner.getZoom();
      double newZoom;
      int rotation = e.getWheelRotation();
      if (rotation < 0)
	newZoom = oldZoom * Math.pow(ObjectAnnotationPanel.ZOOM_FACTOR, -rotation);
      else
	newZoom = oldZoom / Math.pow(ObjectAnnotationPanel.ZOOM_FACTOR, rotation);
      newZoom = RoundingUtils.round(newZoom, 3);
      logMouseWheel(e, oldZoom, newZoom);
      getOwner().setZoom(newZoom);
      update();
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
	getOwner().updateStatus(e.getPoint());
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          JPopupMenu menu = createPopupMenu();
          menu.show(CanvasPanel.this, e.getX(), e.getY());
	}
      }
    });
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(ObjectAnnotationPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public ObjectAnnotationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Sets whether to use best fit or specified zoom.
   *
   * @param value	true if to use best fit
   */
  public void setBestFit(boolean value) {
    m_BestFit = value;
    getOwner().updateStatus();
  }

  /**
   * Sets whether to use best fit.
   *
   * @return		true if to use best fit
   */
  public boolean getBestFit() {
    return m_BestFit;
  }

  /**
   * Sets the zoom.
   *
   * @param value	the zoom to use (1 = 100%)
   */
  public void setZoom(double value) {
    m_Zoom       = value;
    m_ActualZoom = value;
    m_BestFit    = false;
    logScale(value);
    getOwner().updateStatus();
  }

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1 = 100%)
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Returns the actual zoom in use (taking best fit into account if set).
   *
   * @return		the zoom in use (1 = 100%)
   */
  public double getActualZoom() {
    return m_ActualZoom;
  }

  /**
   * Sets the brightness to use.
   *
   * @param value	the brightness (100 = default)
   */
  public void setBrightness(float value) {
    m_Brightness = value;
  }

  /**
   * Returns the brightness to use.
   *
   * @return		the brightness (100 = default)
   */
  public float getBrightness() {
    return m_Brightness;
  }

  /**
   * Sets the image to display.
   *
   * @param value	the image, null for none
   */
  public void setImage(BufferedImage value) {
    m_Image       = value;
    m_BrightImage = null;
  }

  /**
   * Returns the image on display.
   *
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return m_Image;
  }

  /**
   * Sets the interaction log filter to use.
   *
   * @param value	the filter
   */
  public void setInteractionLoggingFilter(InteractionLoggingFilter value) {
    m_InteractionLoggingFilter = value;
  }

  /**
   * Returns the interaction log filter in use.
   *
   * @return		the filter
   */
  public InteractionLoggingFilter getInteractionLoggingFilter() {
    return m_InteractionLoggingFilter;
  }

  /**
   * Sets the popup menu customizer to use.
   *
   * @param value	the customizer, null to unset
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the popup menu customizer in use.
   *
   * @return		the customizer, null if none used
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Creates the popup.
   *
   * @return		the popup menu
   */
  protected JPopupMenu createPopupMenu() {
    JPopupMenu		result;
    JMenuItem		menuitem;

    result = new JPopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_Image != null);
    menuitem.addActionListener((ActionEvent e) -> copyToClipboard());
    result.add(menuitem);

    menuitem = new JMenuItem("Save as...", GUIHelper.getIcon("save.gif"));
    menuitem.setEnabled(m_Image != null);
    menuitem.addActionListener((ActionEvent e) -> saveAs());
    result.add(menuitem);

    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, result);

    return result;
  }

  /**
   * Copies the image to the clipboard.
   */
  public void copyToClipboard() {
    if (m_Image == null)
      return;
    ClipboardHelper.copyToClipboard(this);
  }

  /**
   * Saves the image to disk.
   */
  public void saveAs() {
    int				retVal;
    AbstractImageWriter		writer;
    BufferedImageContainer	cont;
    String			msg;
    int				width;
    int				height;

    if (m_Image == null)
      return;

    if (m_FileChooser == null)
      m_FileChooser = new ImageFileChooser();

    retVal = m_FileChooser.showSaveDialog(getParent());
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    width  = (int) (m_Image.getWidth() * getActualZoom());
    height = (int) (m_Image.getHeight() * getActualZoom());
    cont   = new BufferedImageContainer();
    cont.setImage(BufferedImageHelper.toBufferedImage(this, getBackground(), width, height));
    writer = m_FileChooser.getImageWriter();
    msg = writer.write(m_FileChooser.getSelectedPlaceholderFile(), cont);
    if (msg != null)
      GUIHelper.showErrorMessage(getParent(), msg);
  }

  /**
   * Updates the image.
   */
  public void update() {
    update(false);
  }

  /**
   * Updates the image.
   *
   * @param doLayout 	whether to update the layout
   */
  public void update(boolean doLayout) {
    int		width;
    int		height;
    JScrollBar 	sbHor;
    JScrollBar	sbVer;
    double	zoomW;
    double	zoomH;

    if (m_Image != null) {
      m_ResizeRequired = m_FirstDisplay || (m_BestFit && (getOwner().getScrollPane().getWidth() == 0));

      // determine zoom
      if (m_BestFit && (getOwner().getScrollPane().getWidth() > 0)) {
	width  = getOwner().getScrollPane().getWidth()  - 20;
	height = getOwner().getScrollPane().getHeight() - 20;
	zoomW = (double) width / (double) m_Image.getWidth();
	zoomH = (double) height / (double) m_Image.getHeight();
	m_ActualZoom = Math.min(zoomW, zoomH);
      }
      else {
	m_ActualZoom = m_Zoom;
      }

      // calculate dimensions
      width = (int) (m_Image.getWidth() * m_ActualZoom);
      height = (int) (m_Image.getHeight() * m_ActualZoom);

      if ((width != getWidth()) || (height != getHeight())) {
	setSize(new Dimension(width, height));
	setMinimumSize(new Dimension(width, height));
	setPreferredSize(new Dimension(width, height));

	sbHor = getOwner().getScrollPane().getHorizontalScrollBar();
	sbHor.setUnitIncrement(width / 25);
	sbHor.setBlockIncrement(width / 10);

	sbVer = getOwner().getScrollPane().getVerticalScrollBar();
	sbVer.setUnitIncrement(height / 25);
	sbVer.setBlockIncrement(height / 10);

	doLayout = true;
      }
    }

    if (doLayout) {
      m_Owner.invalidate();
      m_Owner.revalidate();
      m_Owner.doLayout();
    }
    m_Owner.repaint();
  }

  /**
   * Turns the mouse position into pixel location.
   * Limits the pixel position to the size of the image, i.e., no negative
   * pixel locations or ones that exceed the image size are generated.
   *
   * @param mousePos	the mouse position
   * @return		the pixel location
   */
  public Point mouseToPixelLocation(Point mousePos) {
    int	x;
    int	y;

    x = (int) (mousePos.getX() / m_ActualZoom);
    if (x < 0)
      x = 0;
    y = (int) (mousePos.getY() / m_ActualZoom);
    if (y < 0)
      y = 0;

    if (m_Image != null) {
      if (x > m_Image.getWidth())
	x = m_Image.getWidth();
      if (y > m_Image.getHeight())
	y = m_Image.getHeight();
    }

    return new Point(x, y);
  }

  /**
   * Converts the pixel position (at 100% zoom) to a mouse location.
   *
   * @param pixelPos	the pixel position
   * @return		the mouse position
   */
  public Point pixelToMouseLocation(Point pixelPos) {
    int	x;
    int	y;

    x = (int) (pixelPos.x * m_ActualZoom);
    y = (int) (pixelPos.y * m_ActualZoom);

    return new Point(x, y);
  }

  /**
   * Logs a mouse button pressed.
   *
   * @param e		the mouse event to record
   */
  public void logMouseButtonPressed(MouseEvent e) {
    Map<String,Object> 	data;

    if (getOwner() == null)
      return;

    data = new HashMap<>();
    data.put("x", e.getX());
    data.put("y", e.getY());
    data.put("modifiers", MouseUtils.modifiersToStr(e));

    if (MouseUtils.isLeftClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "left-pressed", data));
    }
    else if (MouseUtils.isMiddleClick(e)) {
      data.put("scale", 1.0);
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "middle-pressed", data));
    }
    else if (MouseUtils.isRightClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "right-pressed", data));
    }
  }

  /**
   * Logs a mouse click.
   *
   * @param e		the mouse event to record
   */
  public void logMouseButtonClick(MouseEvent e) {
    Map<String,Object> 	data;

    if (getOwner() == null)
      return;

    data = new HashMap<>();
    data.put("x", e.getX());
    data.put("y", e.getY());
    data.put("modifiers", MouseUtils.modifiersToStr(e));

    if (MouseUtils.isLeftClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "left-click", data));
    }
    else if (MouseUtils.isMiddleClick(e)) {
      data.put("scale", 1.0);
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "middle-click", data));
    }
    else if (MouseUtils.isRightClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "right-click", data));
    }
  }

  /**
   * Logs a mouse button released.
   *
   * @param e		the mouse event to record
   */
  public void logMouseButtonReleased(MouseEvent e) {
    Map<String,Object> 	data;

    if (getOwner() == null)
      return;

    data = new HashMap<>();
    data.put("x", e.getX());
    data.put("y", e.getY());
    data.put("modifiers", MouseUtils.modifiersToStr(e));

    if (MouseUtils.isLeftClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "left-released", data));
    }
    else if (MouseUtils.isMiddleClick(e)) {
      data.put("scale", 1.0);
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "middle-released", data));
    }
    else if (MouseUtils.isRightClick(e)) {
      m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "right-released", data));
    }
  }

  /**
   * Logs a mouse wheel event (zoom).
   *
   * @param e		the mouse wheel event to record
   * @param oldZoom 	the old zoom
   * @param newZoom 	the new zoom
   */
  public void logMouseWheel(MouseWheelEvent e, double oldZoom, double newZoom) {
    Map<String,Object> data;

    if (getOwner() == null)
      return;

    data = new HashMap<>();
    data.put("x", e.getX());
    data.put("y", e.getY());
    data.put("rotation", e.getWheelRotation());
    data.put("oldScale", oldZoom);
    data.put("newScale", newZoom);
    m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "mouse-wheel", data));
  }

  /**
   * Logs a scale change from the menu.
   *
   * @param newScale 	the new scale
   */
  public void logScale(double newScale) {
    Map<String,Object> 	data;

    if (getOwner() == null)
      return;

    data = new HashMap<>();
    data.put("newScale", newScale);
    m_InteractionLoggingFilter.filterInteractionLog(new InteractionEvent(getOwner(), new Date(), "zoom", data));
  }

  /**
   * Paints the image or just a white background.
   *
   * @param g		the graphics context
   */
  @Override
  public void paint(Graphics g) {
    RescaleOp 	op;

    if (m_ResizeRequired) {
      m_ResizeRequired = false;
      m_FirstDisplay   = false;
      getOwner().bestFitZoom();
      return;
    }

    ((Graphics2D) g).scale(1.0, 1.0);
    g.setColor(getOwner().getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    if (m_Image != null) {
      ((Graphics2D) g).scale(m_ActualZoom, m_ActualZoom);
      if ((m_BrightImage == null) || (m_LastBrightness == null) || (m_LastBrightness != m_Brightness)) {
	op = new RescaleOp(m_Brightness / 100.0f, 0, null);
	m_BrightImage = new BufferedImage(m_Image.getWidth(), m_Image.getHeight(), m_Image.getType());
	m_BrightImage = op.filter(m_Image, m_BrightImage);
	m_LastBrightness = m_Brightness;
      }
      g.drawImage(m_BrightImage, 0, 0, getOwner().getBackground(), null);
    }

    getOwner().getOverlay().paint(getOwner(), g);
    getOwner().getAnnotator().paintSelection(g);
  }
}
