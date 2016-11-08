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
 * DragAndDropTabbedPane.java
 * Copyright (C) TERAI Atsuhiro
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Tabbed pane that allows reordering of tabs via drag-n-drop.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "TERAI Atsuhiro",
    license = License.PUBLIC_DOMAIN,
    url = "http://java-swing-tips.blogspot.co.nz/2008/04/drag-and-drop-tabs-in-jtabbedpane.html"
)
public class DragAndDropTabbedPane
  extends BaseTabbedPane {
  
  /** for serialization. */
  private static final long serialVersionUID = 4341372770299945895L;

  protected static final int LINEWIDTH = 3;
  
  protected static final String NAME = "test";
  
  /** magic number of scroll button size. */
  protected final static int BUTTONSIZE = 30;
  
  protected final static int RWH = 20;

  protected GhostGlassPane m_GlassPane;
  
  protected Rectangle m_LineRect;
  
  protected Color m_LineColor;
  
  protected int m_DragTabIndex;

  protected static Rectangle m_RectBackward;
  
  protected static Rectangle m_RectForward;
  
  protected boolean m_HasGhost;
  
  protected boolean m_IsPaintScrollArea;

  /** whether the tab is being moved. */
  protected boolean m_MovingTab;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    if (m_RectBackward == null) {
      m_RectBackward = new Rectangle();
      m_RectForward  = new Rectangle();
    }
    m_GlassPane         = new GhostGlassPane();
    m_LineRect          = new Rectangle();
    m_LineColor         = new Color(0, 100, 255);
    m_DragTabIndex      = -1;
    m_HasGhost          = true;
    m_IsPaintScrollArea = true;
    m_MovingTab         = false;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    final DragSourceListener dsl = new DragSourceListener() {
      @Override
      public void dragEnter(DragSourceDragEvent e) {
	e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
      }
      @Override
      public void dragExit(DragSourceEvent e) {
	e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	m_LineRect.setRect(0, 0, 0, 0);
	m_GlassPane.setPoint(new Point(-1000, -1000));
	m_GlassPane.repaint();
      }
      @Override 
      public void dragOver(DragSourceDragEvent e) {
	Point glassPt = e.getLocation();
	SwingUtilities.convertPointFromScreen(glassPt, m_GlassPane);
	int targetIdx = getTargetTabIndex(glassPt);
	if (getTabAreaBounds().contains(glassPt) && targetIdx>=0 &&
	    targetIdx!=m_DragTabIndex && targetIdx!=m_DragTabIndex+1) {
	  e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
	  m_GlassPane.setCursor(DragSource.DefaultMoveDrop);
	}
	else {
	  e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	  m_GlassPane.setCursor(DragSource.DefaultMoveNoDrop);
	}
      }
      @Override 
      public void dragDropEnd(DragSourceDropEvent e) {
	m_LineRect.setRect(0, 0, 0, 0);
	m_DragTabIndex = -1;
	m_GlassPane.setVisible(false);
	if (hasGhost()) {
	  m_GlassPane.setVisible(false);
	  m_GlassPane.setImage(null);
	}
      }
      @Override 
      public void dropActionChanged(DragSourceDragEvent e) {
      }
    };
    
    final Transferable t = new Transferable() {
      protected final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
      @Override 
      public Object getTransferData(DataFlavor flavor) {
	return DragAndDropTabbedPane.this;
      }
      @Override 
      public DataFlavor[] getTransferDataFlavors() {
	DataFlavor[] f = new DataFlavor[1];
	f[0] = this.FLAVOR;
	return f;
      }
      @Override 
      public boolean isDataFlavorSupported(DataFlavor flavor) {
	return flavor.getHumanPresentableName().equals(NAME);
      }
    };
    
    final DragGestureListener dgl = new DragGestureListener() {
      @Override 
      public void dragGestureRecognized(DragGestureEvent e) {
	if (getTabCount() <= 1) 
	  return;
	Point tabPt = e.getDragOrigin();
	m_DragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
	//"disabled tab problem".
	if ((m_DragTabIndex < 0) || !isEnabledAt(m_DragTabIndex)) 
	  return;
	initGlassPane(e.getComponent(), e.getDragOrigin());
	try {
	  e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
	}
	catch(InvalidDnDOperationException idoe) {
	  idoe.printStackTrace();
	}
      }
    };
    
    new DropTarget(m_GlassPane, DnDConstants.ACTION_COPY_OR_MOVE,
	new CDropTargetListener(), true);
    
    new DragSource().createDefaultDragGestureRecognizer(
	this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
  }

  protected void clickArrowButton(String actionKey) {
    ActionMap map = getActionMap();
    if (map != null) {
      Action action = map.get(actionKey);
      if ((action != null) && action.isEnabled()) {
	action.actionPerformed(new ActionEvent(
	    this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
      }
    }
  }
  
  protected void autoScrollTest(Point glassPt) {
    Rectangle r = getTabAreaBounds();
    int tabPlacement = getTabPlacement();
    if ((tabPlacement == TOP) || (tabPlacement == BOTTOM)) {
      m_RectBackward.setBounds(r.x, r.y, RWH, r.height);
      m_RectForward.setBounds(r.x+r.width-RWH-BUTTONSIZE, r.y, RWH+BUTTONSIZE, r.height);
    }
    else if ((tabPlacement == LEFT) || (tabPlacement == RIGHT)) {
      m_RectBackward.setBounds(r.x, r.y, r.width, RWH);
      m_RectForward.setBounds(r.x, r.y+r.height-RWH-BUTTONSIZE, r.width, RWH+BUTTONSIZE);
    }
    m_RectBackward = SwingUtilities.convertRectangle(getParent(), m_RectBackward, m_GlassPane);
    m_RectForward  = SwingUtilities.convertRectangle(getParent(), m_RectForward,  m_GlassPane);
    if (m_RectBackward.contains(glassPt))
      clickArrowButton("scrollTabsBackwardAction");
    else if (m_RectForward.contains(glassPt))
      clickArrowButton("scrollTabsForwardAction");
  }

  protected class CDropTargetListener
    implements DropTargetListener{

    protected Point m_glassPt = new Point();

    @Override 
    public void dragEnter(DropTargetDragEvent e) {
      if (isDragAcceptable(e)) e.acceptDrag(e.getDropAction());
      else e.rejectDrag();
    }
    
    @Override 
    public void dragExit(DropTargetEvent e) {
    }

    @Override 
    public void dropActionChanged(DropTargetDragEvent e) {
    }

    @Override 
    public void dragOver(final DropTargetDragEvent e) {
      Point glassPt = e.getLocation();
      if ((getTabPlacement() == JTabbedPane.TOP) || (getTabPlacement()==JTabbedPane.BOTTOM))
	initTargetLeftRightLine(getTargetTabIndex(glassPt));
      else
	initTargetTopBottomLine(getTargetTabIndex(glassPt));
      if (hasGhost())
	m_GlassPane.setPoint(glassPt);
      if (!m_glassPt.equals(glassPt)) m_GlassPane.repaint();
      m_glassPt = glassPt;
      autoScrollTest(glassPt);
    }

    @Override 
    public void drop(DropTargetDropEvent e) {
      if (isDropAcceptable(e)) {
	convertTab(m_DragTabIndex, getTargetTabIndex(e.getLocation()));
	e.dropComplete(true);
      }
      else{
	e.dropComplete(false);
      }
      repaint();
    }
    
    protected boolean isDragAcceptable(DropTargetDragEvent e) {
      Transferable t = e.getTransferable();
      if ((t == null))
	return false;
      DataFlavor[] f = e.getCurrentDataFlavors();
      if (t.isDataFlavorSupported(f[0]) && (m_DragTabIndex >= 0))
	return true;
      return false;
    }
    
    protected boolean isDropAcceptable(DropTargetDropEvent e) {
      Transferable t = e.getTransferable();
      if (t == null)
	return false;
      DataFlavor[] f = t.getTransferDataFlavors();
      if (t.isDataFlavorSupported(f[0]) && (m_DragTabIndex >= 0))
	return true;
      return false;
    }
  }

  public void setPaintGhost(boolean flag) {
    m_HasGhost = flag;
  }
  
  public boolean hasGhost() {
    return m_HasGhost;
  }
  
  public void setPaintScrollArea(boolean flag) {
    m_IsPaintScrollArea = flag;
  }
  
  public boolean isPaintScrollArea() {
    return m_IsPaintScrollArea;
  }

  protected int getTargetTabIndex(Point glassPt) {
    Point tabPt = SwingUtilities.convertPoint(m_GlassPane, glassPt, DragAndDropTabbedPane.this);
    boolean isTB = (getTabPlacement() == JTabbedPane.TOP) || (getTabPlacement() == JTabbedPane.BOTTOM);
    for(int i = 0;i < getTabCount(); i++) {
      Rectangle r = getBoundsAt(i);
      if (isTB) 
	r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
      else
	r.setRect(r.x, r.y-r.height/2, r.width, r.height);
      if (r.contains(tabPt))
	return i;
    }
    Rectangle r = getBoundsAt(getTabCount()-1);
    if (isTB) 
      r.setRect(r.x+r.width/2, r.y,  r.width, r.height);
    else
      r.setRect(r.x, r.y+r.height/2, r.width, r.height);
    return r.contains(tabPt) ? getTabCount() : -1;
  }
  
  protected void convertTab(int prev, int next) {
    if ((next < 0) || (prev == next))
      return;
    Component cmp = getComponentAt(prev);
    Component tab = getTabComponentAt(prev);
    String str    = getTitleAt(prev);
    Icon icon     = getIconAt(prev);
    String tip    = getToolTipTextAt(prev);
    boolean flg   = isEnabledAt(prev);
    int tgtindex  = prev>next ? next : next-1;
    m_MovingTab   = true;
    remove(prev);
    m_MovingTab   = false;
    insertTab(str, icon, cmp, tip, tgtindex);
    setEnabledAt(tgtindex, flg);
    //When you drag'n'drop a disabled tab, it finishes enabled and selected.
    //pointed out by dlorde
    if (flg) setSelectedIndex(tgtindex);

    //I have a component in all tabs (jlabel with an X to close the tab)
    //and when i move a tab the component disappear.
    //pointed out by Daniel Dario Morales Salas
    setTabComponentAt(tgtindex, tab);
  }

  protected void initTargetLeftRightLine(int next) {
    if ((next < 0) || (m_DragTabIndex == next) || (next - m_DragTabIndex == 1)) {
      m_LineRect.setRect(0,0,0,0);
    }
    else if (next == 0) {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), m_GlassPane);
      m_LineRect.setRect(r.x-LINEWIDTH/2,r.y,LINEWIDTH,r.height);
    }
    else {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next-1), m_GlassPane);
      m_LineRect.setRect(r.x+r.width-LINEWIDTH/2,r.y,LINEWIDTH,r.height);
    }
  }
  
  protected void initTargetTopBottomLine(int next) {
    if ((next < 0) || (m_DragTabIndex == next) || (next - m_DragTabIndex == 1)) {
      m_LineRect.setRect(0,0,0,0);
    }
    else if (next == 0) {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), m_GlassPane);
      m_LineRect.setRect(r.x,r.y-LINEWIDTH/2,r.width,LINEWIDTH);
    }
    else {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next-1), m_GlassPane);
      m_LineRect.setRect(r.x,r.y+r.height-LINEWIDTH/2,r.width,LINEWIDTH);
    }
  }

  protected void initGlassPane(Component c, Point tabPt) {
    getRootPane().setGlassPane(m_GlassPane);
    if (hasGhost()) {
      Rectangle rect = getBoundsAt(m_DragTabIndex);
      BufferedImage image = new BufferedImage(
	  c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();
      c.paint(g);
      rect.x = rect.x < 0?0:rect.x;
      rect.y = rect.y < 0?0:rect.y;
      image = image.getSubimage(rect.x,rect.y,rect.width,rect.height);
      m_GlassPane.setImage(image);
    }
    Point glassPt = SwingUtilities.convertPoint(c, tabPt, m_GlassPane);
    m_GlassPane.setPoint(glassPt);
    m_GlassPane.setVisible(true);
  }

  protected Rectangle getTabAreaBounds() {
    Rectangle tabbedRect = getBounds();
    //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
    //Rectangle compRect   = getSelectedComponent().getBounds();
    Component comp = getSelectedComponent();
    int idx = 0;
    while ((comp == null) && (idx < getTabCount())) 
      comp = getComponentAt(idx++);
    
    Rectangle compRect = (comp==null)?new Rectangle():comp.getBounds();
    int tabPlacement = getTabPlacement();
    if (tabPlacement == TOP) {
      tabbedRect.height = tabbedRect.height - compRect.height;
    }
    else if (tabPlacement == BOTTOM) {
      tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
      tabbedRect.height = tabbedRect.height - compRect.height;
    }
    else if (tabPlacement == LEFT) {
      tabbedRect.width = tabbedRect.width - compRect.width;
    }
    else if (tabPlacement == RIGHT) {
      tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
      tabbedRect.width = tabbedRect.width - compRect.width;
    }
    tabbedRect.grow(2, 2);
    return tabbedRect;
  }

  /**
   * 
   */
  protected class GhostGlassPane
    extends JPanel {
    
    /** for serialization. */
    private static final long serialVersionUID = -1731274272252890281L;

    protected final AlphaComposite m_Composite;
    
    protected Point m_Location;
    
    protected BufferedImage m_DraggingGhost;
    
    public GhostGlassPane() {
      m_Location = new Point(0, 0);
      m_DraggingGhost = null;
      setOpaque(false);
      m_Composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
      //http://bugs.sun.com/view_bug.do?bug_id=6700748
      //setCursor(null);
    }
    
    public void setImage(BufferedImage draggingGhost) {
      this.m_DraggingGhost = draggingGhost;
    }
    
    public void setPoint(Point location) {
      this.m_Location = location;
    }
    
    @Override 
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setComposite(m_Composite);
      if (isPaintScrollArea() && getTabLayoutPolicy()==SCROLL_TAB_LAYOUT) {
	g2.setPaint(Color.RED);
	g2.fill(m_RectBackward);
	g2.fill(m_RectForward);
      }
      if (m_DraggingGhost != null) {
	double xx = m_Location.getX() - (m_DraggingGhost.getWidth(this) /2d);
	double yy = m_Location.getY() - (m_DraggingGhost.getHeight(this)/2d);
	g2.drawImage(m_DraggingGhost, (int) xx, (int) yy, null);
      }
      if (m_DragTabIndex >= 0) {
	g2.setPaint(m_LineColor);
	g2.fill(m_LineRect);
      }
    }
  }
}