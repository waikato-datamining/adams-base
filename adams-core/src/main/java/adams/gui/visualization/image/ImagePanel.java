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
 * ImagePanel.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTable;
import adams.gui.core.CustomPopupMenuProvider;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.UndoPanel;
import adams.gui.event.ImagePanelSelectionEvent;
import adams.gui.event.ImagePanelSelectionListener;
import adams.gui.event.UndoEvent;
import adams.gui.print.PrintMouseListener;
import adams.gui.visualization.report.ReportFactory;

/**
 * For displaying a single image.
 * <p/>
 * The scroll wheel allows zooming in/out. Mouse-wheel clicking sets scale
 * back to 100%.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePanel
  extends UndoPanel
  implements StatusMessageHandler, TableModelListener {

  /** for serialization. */
  private static final long serialVersionUID = -3102446345758890249L;

  /**
   * The panel used for painting.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PaintPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 184259023085417961L;

    /** the owning image panel. */
    protected ImagePanel m_Owner;

    /** the scaling factor. */
    protected double m_Scale;

    /** the current image. */
    protected BufferedImage m_CurrentImage;

    /** the mouse listener. */
    protected PrintMouseListener m_PrintMouseListener;

    /** the image overlays. */
    protected HashSet<ImageOverlay> m_ImageOverlays;
    
    /** whether to use a custom popup menu provider. */
    protected CustomPopupMenuProvider m_CustomPopupMenuProvider;

    /** whether selection via box is enabled. */
    protected boolean m_SelectionEnabled;

    /** the color of the selection box. */
    protected Color m_SelectionBoxColor;

    /** whether the selection box is currently been drawn. */
    protected boolean m_Selecting;

    /** whether dragging has happened at all. */
    protected boolean m_Dragged;

    /** the top left corner of the selection box. */
    protected Point m_SelectionTopLeft;

    /** the bottom right corner of the selection box. */
    protected Point m_SelectionBottomRight;

    /** the selection listeners. */
    protected HashSet<ImagePanelSelectionListener> m_SelectionListeners;

    /**
     * Initializes the panel.
     *
     * @param owner	the image panel this paint panel belongs to
     */
    public PaintPanel(ImagePanel owner) {
      super();

      m_Owner = owner;
    }

    /**
     * Initializes members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_CurrentImage            = null;
      m_Scale                   = 1.0;
      m_ImageOverlays           = new HashSet<ImageOverlay>();
      m_CustomPopupMenuProvider = null;
      m_Selecting               = false;
      m_Dragged                 = false;
      m_SelectionBoxColor       = Color.GRAY;
      m_SelectionEnabled        = false;
      m_SelectionListeners      = new HashSet<ImagePanelSelectionListener>();
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      addMouseMotionListener(new MouseMotionAdapter() {
	// for selection
	@Override
	public void mouseDragged(MouseEvent e) {
	  super.mouseDragged(e);

	  // update zoom box
	  if (m_Selecting && !e.isShiftDown()) {
	    m_Dragged              = true;
	    m_SelectionBottomRight = e.getPoint();

	    repaint();
	  }
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	  updateStatus(e.getPoint());
	  if (e.getButton() == MouseEvent.BUTTON1) {
	    // get top/left coordinates for zoom
	    if (!e.isShiftDown()) {
	      if (m_SelectionEnabled) {
		m_Selecting        = true;
		m_Dragged          = false;
		m_SelectionTopLeft = e.getPoint();
	      }
	    }
	  }
	  super.mouseMoved(e);
	}
      });

      addMouseListener(new MouseAdapter() {
	// start selection
	@Override
	public void mousePressed(MouseEvent e) {
	  super.mousePressed(e);

	  if (e.getButton() == MouseEvent.BUTTON1) {
	    // get top/left coordinates for selection
	    if (!e.isShiftDown()) {
	      if (m_SelectionEnabled) {
		m_Selecting        = true;
		m_Dragged          = false;
		m_SelectionTopLeft = e.getPoint();
	      }
	    }
	  }
	}
	      
	// start selection
	@Override
	public void mouseMoved(MouseEvent e) {
	  if (e.getButton() == MouseEvent.BUTTON1) {
	    // get top/left coordinates for selection
	    if (!e.isShiftDown()) {
	      if (m_SelectionEnabled) {
		m_Selecting        = true;
		m_Dragged          = false;
		m_SelectionTopLeft = e.getPoint();
	      }
	    }
	  }
	  super.mouseMoved(e);
	}

	// perform selection
	@Override
	public void mouseReleased(MouseEvent e) {
	  super.mouseReleased(e);

	  if (e.getButton() == MouseEvent.BUTTON1) {
	    // get bottom/right coordinates for selection
	    if (m_Selecting && m_Dragged) {
	      m_Selecting            = false;
	      m_Dragged              = false;
	      m_SelectionBottomRight = e.getPoint();
	      if (m_SelectionTopLeft.getX() > m_SelectionBottomRight.getX())
		notifySelectionListeners(m_SelectionBottomRight, m_SelectionTopLeft, e.getModifiersEx());
	      else
		notifySelectionListeners(m_SelectionTopLeft, m_SelectionBottomRight, e.getModifiersEx());
	    }
	  }

	  m_Selecting = false;
	}
	
	// popups/reset zoom
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isMiddleClick(e)) {
	    setScale(1.0);
	    updateStatus();
	    e.consume();
	  }
	  else if (MouseUtils.isRightClick(e)) {
	    showPopup(e);
	    e.consume();
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      });

      addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(MouseWheelEvent e) {
          int rotation = e.getWheelRotation();
          double scale = getScale();
          if (rotation < 0)
            scale = scale * Math.pow(1.2, -rotation);
          else
            scale = scale / Math.pow(1.2, rotation);
          setScale(scale);
          updateStatus();
        }
      });

      m_PrintMouseListener = new PrintMouseListener(this);
    }

    /**
     * Updates the status bar.
     */
    protected void updateStatus() {
      Point	pos;

      pos = getMousePosition();
      if (pos != null)
	updateStatus(pos.getLocation());
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
      
      x = (int) (mousePos.getX() / m_Scale);
      if (x < 0)
	x = 0;
      y = (int) (mousePos.getY() / m_Scale);
      if (y < 0)
	y = 0;

      if (m_CurrentImage != null) {
	if (x > m_CurrentImage.getWidth())
	  x = m_CurrentImage.getWidth();
	if (y > m_CurrentImage.getHeight())
	  y = m_CurrentImage.getHeight();
      }
      
      return new Point(x, y);
    }
    
    /**
     * Updates the status bar.
     *
     * @param pos	the mouse position
     */
    protected void updateStatus(Point pos) {
      Point	loc;

      if (getOwner() == null)
	return;

      loc = mouseToPixelLocation(pos);
      getOwner().showStatus(
  	    "X: " + (int) (loc.getX() + 1)
	  + "   "
	  + "Y: " + (int) (loc.getY() + 1)
	  + "   "
	  + "Zoom: " + Utils.doubleToString(getScale() * 100, 1) + "%");
    }

    /**
     * Displays a popup-menu. Either provided by the custom popup menu provider
     * or the default one.
     *
     * @param e		the event that triggered the popup
     * @see		#m_CustomPopupMenuProvider
     */
    protected void showPopup(MouseEvent e) {
      JPopupMenu	menu;
      JMenuItem		menuitem;

      menu = null;
      if (m_CustomPopupMenuProvider != null)
	menu = m_CustomPopupMenuProvider.getCustomPopupMenu(e);
	
      if (menu == null) {
	menu = new JPopupMenu();

	menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
	menuitem.setEnabled(getCurrentImage() != null);
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    GUIHelper.copyToClipboard(getCurrentImage());
	  }
	});
	menu.add(menuitem);

	menuitem = new JMenuItem("Export...", GUIHelper.getIcon("save.gif"));
	menuitem.setEnabled(getCurrentImage() != null);
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    m_PrintMouseListener.saveComponent();
	  }
	});
	menu.addSeparator();
	menu.add(menuitem);
      }

      menu.show(this, e.getX(), e.getY());
    }

    /**
     * Returns the owning panel.
     *
     * @return		the owner
     */
    public ImagePanel getOwner() {
      return m_Owner;
    }

    /**
     * Sets the image to display.
     *
     * @param value	the image to use
     */
    public void setCurrentImage(BufferedImage value) {
      m_CurrentImage = value;
      setScale(1.0);
      repaint();
    }

    /**
     * Returns the current image.
     *
     * @return		the image, can be null if not set
     */
    public BufferedImage getCurrentImage() {
      return m_CurrentImage;
    }
    
    /**
     * Sets the scaling factor (0-16).
     * TODO: allow "fit" via "-1.0"
     *
     * @param value	the scaling factor
     */
    public void setScale(double value) {
      int	width;
      int	height;

      if ((value > 0) && (value <= 16)) {
	m_Scale = value;
	if (m_CurrentImage != null) {
	  width  = (int) ((double) m_CurrentImage.getWidth() * m_Scale);
	  height = (int) ((double) m_CurrentImage.getHeight() * m_Scale);
	}
	else {
	  width  = 320;
	  height = 200;
	}
	setSize(new Dimension(width, height));
	setMinimumSize(new Dimension(width, height));
	setPreferredSize(new Dimension(width, height));
	getOwner().getScrollPane().getHorizontalScrollBar().setUnitIncrement(width / 25);
	getOwner().getScrollPane().getHorizontalScrollBar().setBlockIncrement(width / 10);
	getOwner().getScrollPane().getVerticalScrollBar().setUnitIncrement(height / 25);
	getOwner().getScrollPane().getVerticalScrollBar().setBlockIncrement(height / 10);
	update();
      }
    }
    
    /**
     * Updates the image.
     */
    protected void update() {
      getOwner().invalidate();
      getOwner().validate();
      repaint();
    }

    /**
     * Returns the scaling factor (0-16).
     *
     * @return		the scaling factor
     */
    public double getScale() {
      return m_Scale;
    }

    /**
     * Removes all image overlays.
     */
    public void clearImageOverlays() {
      m_ImageOverlays.clear();
      update();
    }

    /**
     * Adds the image overlay.
     *
     * @param io	the image overlay to add
     */
    public void addImageOverlay(ImageOverlay io) {
      m_ImageOverlays.add(io);
      update();
    }

    /**
     * Removes the image overlay.
     *
     * @param io	the image overlay to remove
     */
    public void removeImageOverlay(ImageOverlay io) {
      m_ImageOverlays.remove(io);
      update();
    }

    /**
     * Returns an iterator over all the image overlays.
     * 
     * @return		iterator on image overlays
     */
    public Iterator<ImageOverlay> imageOverlays() {
      return m_ImageOverlays.iterator();
    }
    
    /**
     * Sets the custom popup menu provider.
     * 
     * @param value	the provider, use null to remove
     */
    public void setCustomPopupMenuProvider(CustomPopupMenuProvider value) {
      m_CustomPopupMenuProvider = value;
    }
    
    /**
     * Returns the custom popup menu provider.
     * 
     * @return		the provider, null if none set
     */
    public CustomPopupMenuProvider getCustomPopupMenuProvider() {
      return m_CustomPopupMenuProvider;
    }

    /**
     * Paints the selection box, if necessary (i.e., currently selecting/dragging).
     *
     * @param g		the graphics context
     */
    protected void paintSelectionBox(Graphics g) {
      int	topX;
      int	bottomX;
      int	topY;
      int	bottomY;
      int	tmp;

      if (m_Selecting && m_Dragged) {
        g.setColor(m_SelectionBoxColor);

        topX    = (int) mouseToPixelLocation(m_SelectionTopLeft).getX();
        topY    = (int) mouseToPixelLocation(m_SelectionTopLeft).getY();
        bottomX = (int) mouseToPixelLocation(m_SelectionBottomRight).getX();
        bottomY = (int) mouseToPixelLocation(m_SelectionBottomRight).getY();

        // swap necessary?
        if (topX > bottomX) {
          tmp     = topX;
          topX    = bottomX;
          bottomX = tmp;
        }
        if (topY > bottomY) {
          tmp     = topY;
          topY    = bottomY;
          bottomY = tmp;
        }

        g.drawRect(
            topX,
            topY,
            (bottomX - topX + 1),
            (bottomY - topY + 1));
      }
    }

    /**
     * Paints the image or just a white background.
     *
     * @param g		the graphics context
     */
    @Override
    public void paint(Graphics g) {
      ImageOverlay[]	overlays;

      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());

      if (m_CurrentImage != null) {
	((Graphics2D) g).scale(m_Scale, m_Scale);
        g.drawImage(m_CurrentImage, 0, 0, getOwner().getBackgroundColor(), null);

        // overlays
        overlays = m_ImageOverlays.toArray(new ImageOverlay[m_ImageOverlays.size()]);
        for (ImageOverlay overlay: overlays)
          overlay.paintOverlay(this, g);
        
        paintSelectionBox(g);
      }
    }

    /**
     * Sets the color for the selection box.
     *
     * @param value	the color to use
     */
    public void setSelectionBoxColor(Color value) {
      m_SelectionBoxColor = value;
      if (m_Selecting)
        repaint();
    }

    /**
     * Returns the color for the selection box currently in use.
     *
     * @return		the color in use
     */
    public Color getSelectionBoxColor() {
      return m_SelectionBoxColor;
    }

    /**
     * Sets whether selection is enabled or not.
     *
     * @param value	if true then selection is enabled
     */
    public void setSelectionEnabled(boolean value) {
      m_SelectionEnabled = value;
    }

    /**
     * Returns whether selection is enabled.
     *
     * @return		true if selection is enabled
     */
    public boolean isSelectionEnabled() {
      return m_SelectionEnabled;
    }

    /**
     * Adds the given listener to the internal list of selection listeners.
     *
     * @param l		the listener to add
     */
    public void addSelectionListener(ImagePanelSelectionListener l) {
      m_SelectionListeners.add(l);
    }

    /**
     * Removes the given listener from the internal list of selection listeners.
     *
     * @param l		the listener to remove
     */
    public void removeSelectionListener(ImagePanelSelectionListener l) {
      m_SelectionListeners.remove(l);
    }

    /**
     * Notifies all selection listeners.
     *
     * @param topLeft		the top-left of the selection
     * @param bottomRight	the bottom-right of the selection
     * @param modifiersEx	the extended modifiers
     * @see			MouseEvent#getModifiersEx()
     */
    public void notifySelectionListeners(Point topLeft, Point bottomRight, int modifiersEx) {
      Iterator<ImagePanelSelectionListener>	iter;
      ImagePanelSelectionEvent			e;

      e    = new ImagePanelSelectionEvent(getOwner(), topLeft, bottomRight, modifiersEx);
      iter = m_SelectionListeners.iterator();
      while (iter.hasNext())
        iter.next().selected(e);
    }
  }

  /** the current filename. */
  protected PlaceholderFile m_CurrentFile;

  /** the panel to paint on. */
  protected PaintPanel m_PaintPanel;

  /** the JScrollPane that embeds the paint panel. */
  protected BaseScrollPane m_ScrollPane;

  /** the status bar label. */
  protected BaseStatusBar m_StatusBar;

  /** the panel with the properties. */
  protected BasePanel m_PanelProperties;

  /** the table model with all the properties. */
  protected ReportFactory.Model m_ModelProperties;

  /** the table with the image properties. */
  protected ReportFactory.Table m_TableProperties;

  /** for displaying image and properties. */
  protected BaseSplitPane m_SplitPane;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** whether the image was modified. */
  protected boolean m_Modified;
  
  /** the image properties. */
  protected Report m_ImageProperties;
  
  /** the additional properties to display. */
  protected Report m_AdditionalProperties;

  /**
   * Initializes the panel.
   */
  public ImagePanel() {
    super(Object.class, true);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BackgroundColor      = getBackground();
    m_Modified             = false;
    m_ImageProperties      = new Report();
    m_AdditionalProperties = null;
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane();
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);

    m_PaintPanel = new PaintPanel(this);
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.add(m_PaintPanel);
    m_ScrollPane = new BaseScrollPane(panel);
    m_SplitPane.setLeftComponent(m_ScrollPane);
    m_SplitPane.setLeftComponentHidden(false);

    m_PanelProperties = new BasePanel(new BorderLayout());
    m_PanelProperties.setMinimumSize(new Dimension(200, 0));
    m_SplitPane.setRightComponent(m_PanelProperties);
    m_SplitPane.setRightComponentHidden(true);

    m_ModelProperties = new ReportFactory.Model();
    m_TableProperties = new ReportFactory.Table(m_ModelProperties);
    m_TableProperties.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableProperties.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_TableProperties.sort(0);
    m_PanelProperties.add(new BaseScrollPane(m_TableProperties));

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);

    clear();
  }

  /**
   * Returns the current state of the panel.
   *
   * @return		the state
   * @see		#setState(Vector)
   */
  protected Vector getState() {
    Vector	result;

    result = new Vector();
    result.add(getCurrentFile());
    result.add(getCurrentImage());
    result.add(m_Modified);
    result.add(getScale());

    return result;
  }

  /**
   * Sets the state of the image panel.
   *
   * @param state	the state
   * @see		#getState()
   */
  protected void setState(Vector value) {
    setCurrentImage((BufferedImage) value.get(1));
    m_CurrentFile = (PlaceholderFile) value.get(0);
    m_Modified    = (Boolean) value.get(2);
    setScale((Double) value.get(3));
  }

  /**
   * Adds an undo point, if possible.
   *
   * @param statusMsg	the status message to display while adding the undo point
   * @param undoComment	the comment for the undo point
   */
  public void addUndoPoint(String statusMsg, String undoComment) {
    if (isUndoSupported() && getUndo().isEnabled()) {
      showStatus(statusMsg);
      getUndo().addUndo(getState(), undoComment);
      showStatus("");
    }
  }

  /**
   * Sets the underlying image. Removes the filename.
   *
   * @param value	the image to display
   */
  public void setCurrentImage(BufferedImage value) {
    m_CurrentFile = null;
    m_PaintPanel.setCurrentImage(value);
    updateImageProperties();
  }

  /**
   * Sets the underlying image. Removes the filename.
   *
   * @param value	the image to display
   */
  public void setCurrentImage(AbstractImage value) {
    m_CurrentFile = null;
    m_PaintPanel.setCurrentImage(value.toBufferedImage());
    updateImageProperties();
    setAdditionalProperties(value.getReport());
  }

  /**
   * Returns the underlying image.
   *
   * @return		the current image, can be null
   */
  public BufferedImage getCurrentImage() {
    return m_PaintPanel.getCurrentImage();
  }

  /**
   * Sets the filename of the current image.
   */
  public void setCurrentFile(File value) {
    if (getCurrentImage() != null) {
      if (value != null)
	m_CurrentFile = new PlaceholderFile(value);
      else
	m_CurrentFile = null;
      updateImageProperties();
    }
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Sets the scaling factor (0-16). Use -1 to fit inside panel.
   *
   * @param value	the scaling factor
   */
  public void setScale(double value) {
    double	scaleW;
    double	scaleH;
    int		width;
    int		height;
    
    //addUndoPoint("Saving undo data...", "Scaling with factor " + value);
    
    // TODO keep "best fit" scale and re-adjust image when resizing component
    
    if ((value == -1) && (getCurrentImage() != null)) {
      if (getShowProperties()) {
	width  = getWidth() - m_SplitPane.getRightComponent().getMinimumSize().width;
	height = getHeight();
      }
      else {
	width  = getWidth();
	height = getHeight();
      }
      width  -= 30;  // -30 for border
      height -= 40;  // -40 for border
      scaleW = (double) width / (double) getCurrentImage().getWidth();
      scaleH = (double) height / (double) getCurrentImage().getHeight();
      value  = Math.min(scaleW, scaleH);
    }
    
    m_PaintPanel.setScale(value);
  }

  /**
   * Returns the scaling factor (0-16).
   *
   * @return		the scaling factor
   */
  public double getScale() {
    return m_PaintPanel.getScale();
  }

  /**
   * Sets the background color.
   *
   * @param value 	the color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    repaint();
  }

  /**
   * Returns the background color.
   *
   * @return 		the color
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Returns the BaseScrollPane that embeds the paint panel.
   *
   * @return		the BaseScrollPane
   */
  protected BaseScrollPane getScrollPane() {
    return m_ScrollPane;
  }

  /**
   * Returns the split pane that shows image (left) and properties (right).
   *
   * @return		the split pane
   */
  public BaseSplitPane getSplitPane() {
    return m_SplitPane;
  }

  /**
   * Removes the image.
   */
  public void clear() {
    addUndoPoint("Saving undo data...", "Removing image");
    m_CurrentFile = null;
    m_PaintPanel.setCurrentImage(null);
    updateImageProperties();
    showStatus("");
    repaint();
  }

  /**
   * Opens the file with the specified image reader.
   *
   * @param file	the file to open
   * @return		true if successfully read
   */
  public boolean load(File file) {
    boolean			result;
    BufferedImageContainer	cont;

    addUndoPoint("Saving undo data...", "Loading file '" + file + "'");
    try {
      cont = BufferedImageHelper.read(file);
      m_PaintPanel.setCurrentImage(cont.toBufferedImage());
      m_CurrentFile = new PlaceholderFile(file);
      result        = true;
      updateImageProperties(cont.getReport());
      repaint();
    }
    catch (Exception e) {
      e.printStackTrace();
      clear();
      result = false;
    }

    return result;
  }

  /**
   * Writes the current image to the given file.
   * Sets the modified flag to false if successfully saved.
   *
   * @param file	the file to write to
   * @return		true if successfully written, false if not or no image
   * @see		#isModified()
   */
  public boolean save(File file) {
    boolean	result;
    String	formatName;

    result = false;

    if (m_PaintPanel.getCurrentImage() != null) {
      try {
	file = new File(file.getAbsolutePath());
	formatName = ImageFileChooser.getWriterFormatName(file);
	if (formatName != null) {
	  ImageIO.write(m_PaintPanel.getCurrentImage(), formatName, file);
	  m_CurrentFile = new PlaceholderFile(file);
	}
	else {
	  System.err.println("Failed to find format name for '" + file + "'!");
	}
	result = true;
      }
      catch (Exception e) {
	e.printStackTrace();
	result = false;
      }
    }

    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Returns whether the image properties are currently displayed or not.
   *
   * @return		true if the properties are displayed
   */
  public boolean getShowProperties() {
    return !m_SplitPane.isRightComponentHidden();
  }

  /**
   * Sets the display status of the properties panel.
   *
   * @param value	if true then the properties get displayed
   */
  public void setShowProperties(boolean value) {
    m_SplitPane.setRightComponentHidden(!value);
  }

  /**
   * Sets the modified state.
   *
   * @param value	if true then the image gets flagged as modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Returns whether the image was modified or not.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Updates the properties of the image.
   */
  protected void updateImageProperties() {
    updateImageProperties(null);
  }

  /**
   * Updates the properties of the image.
   * 
   * @param props	additional properties, null to skip
   */
  protected void updateImageProperties(Report props) {
    Report		report;
    BufferedImage	image;

    if (props == null)
      report = new Report();
    else
      report = props;

    image = getCurrentImage();
    if (image != null) {
      if (m_CurrentFile != null)
	report.setStringValue("File", "" + m_CurrentFile);
      report.setNumericValue("Width", image.getWidth());
      report.setNumericValue("Height", image.getHeight());
    }
    
    m_ImageProperties = report;
    
    displayProperties();
  }
  
  /**
   * Displays the image and (optional) additional properties.
   */
  protected void displayProperties() {
    Report	merged;
    
    if (m_TableProperties != null)
      m_TableProperties.getModel().removeTableModelListener(this);
    
    merged = m_ImageProperties.getClone();
    if (m_AdditionalProperties != null)
      merged.mergeWith(m_AdditionalProperties);
    m_ModelProperties = new ReportFactory.Model(merged);
    m_TableProperties.setModel(m_ModelProperties);
    m_TableProperties.setOptimalColumnWidth();
    m_TableProperties.sort(0);
    m_TableProperties.getModel().addTableModelListener(this);
  }

  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    // did the report got modified?
    if (updateProperties(m_ModelProperties.getReport(), m_AdditionalProperties)) {
      displayProperties();
      m_PaintPanel.update();
    }
  }

  /**
   * Updates the current report: removes all the fields that are no longer
   * present in the modified report, updates all modified fields.
   * 
   * @param modified	the updated report
   * @param current	the report to update (ie remove fields from)
   * @return		true if report changed
   */
  protected boolean updateProperties(Report modified, Report current) {
    boolean		result;
    List<AbstractField>	fields;
    
    result = false;

    fields = current.getFields();
    for (AbstractField field: fields) {
      // deleted?
      if (!modified.hasValue(field)) {
	current.removeValue(field);
	result = true;
      }
      // modified?
      else if (!modified.getValue(field).equals(current.getValue(field))) {
	current.setValue(field, modified.getValue(field));
	result = true;
      }
    }
    
    return result;
  }
  
  /**
   * Sets the additional properties.
   * 
   * @param value	the properties, null to unset
   */
  public void setAdditionalProperties(Report value) {
    m_AdditionalProperties = value;
    displayProperties();
  }
  
  /**
   * Returns the additional properties.
   * 
   * @return		the properties, null if none set
   */
  public Report getAdditionalProperties() {
    return m_AdditionalProperties;
  }
  
  /**
   * Returns the image properties by themselves.
   * 
   * @return		the properties
   */
  public Report getImageProperties() {
    return m_ImageProperties;
  }
  
  /**
   * Returns the all the properties (image and additional combined).
   * 
   * @return		the properties
   */
  public Report getAllProperties() {
    return m_ModelProperties.getReport();
  }
  
  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    // ignored
  }

  /**
   * Returns the actual panel that displays the image.
   *
   * @return		the panel
   */
  public PaintPanel getPaintPanel() {
    return m_PaintPanel;
  }

  /**
   * Removes all image overlays.
   */
  public void clearImageOverlays() {
    m_PaintPanel.clearImageOverlays();
  }

  /**
   * Adds the image overlay.
   *
   * @param io	the image overlay to add
   */
  public void addImageOverlay(ImageOverlay io) {
    m_PaintPanel.addImageOverlay(io);
  }

  /**
   * Removes the image overlay.
   *
   * @param io	the image overlay to remove
   */
  public void removeImageOverlay(ImageOverlay io) {
    m_PaintPanel.removeImageOverlay(io);
  }

  /**
   * Returns an iterator over all the image overlays.
   * 
   * @return		iterator on image overlays
   */
  public Iterator<ImageOverlay> imageOverlays() {
    return m_PaintPanel.imageOverlays();
  }

  /**
   * Sets the custom popup menu provider.
   * 
   * @param value	the provider, use null to remove
   */
  public void setCustomPopupMenuProvider(CustomPopupMenuProvider value) {
    m_PaintPanel.setCustomPopupMenuProvider(value);
  }
  
  /**
   * Returns the custom popup menu provider.
   * 
   * @return		the provider, null if none set
   */
  public CustomPopupMenuProvider getCustomPopupMenuProvider() {
    return m_PaintPanel.getCustomPopupMenuProvider();
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
    return m_PaintPanel.mouseToPixelLocation(mousePos);
  }

  /**
   * Adds the given listener to the internal list of selection listeners.
   *
   * @param l		the listener to add
   */
  public void addSelectionListener(ImagePanelSelectionListener l) {
    m_PaintPanel.addSelectionListener(l);
  }

  /**
   * Removes the given listener from the internal list of selection listeners.
   *
   * @param l		the listener to remove
   */
  public void removeSelectionListener(ImagePanelSelectionListener l) {
    m_PaintPanel.removeSelectionListener(l);
  }

  /**
   * Sets the color for the selection box.
   *
   * @param value	the color to use
   */
  public void setSelectionBoxColor(Color value) {
    m_PaintPanel.setSelectionBoxColor(value);
  }

  /**
   * Returns the color for the selection box currently in use.
   *
   * @return		the color in use
   */
  public Color getSelectionBoxColor() {
    return m_PaintPanel.getSelectionBoxColor();
  }

  /**
   * Sets whether selection is enabled or not.
   *
   * @param value	if true then selection is enabled
   */
  public void setSelectionEnabled(boolean value) {
    m_PaintPanel.setSelectionEnabled(value);
  }

  /**
   * Returns whether selection is enabled.
   *
   * @return		true if selection is enabled
   */
  public boolean isSelectionEnabled() {
    return m_PaintPanel.isSelectionEnabled();
  }
}
