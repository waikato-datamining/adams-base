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
 * Edit.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.core.ObjectCopyHelper;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseTextField;
import adams.gui.core.Cursors;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.object.annotator.NullAnnotator;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.outline.PolygonVertices;
import adams.gui.visualization.object.overlay.MultiOverlay;
import adams.gui.visualization.object.overlay.ObjectAnnotations;
import adams.gui.visualization.object.overlay.Overlay;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * For editing existing annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Move
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = -3238804649373495561L;

  /** the object prefix to use. */
  protected BaseTextField m_TextPrefix;

  /** the object prefix to use. */
  protected String m_Prefix;

  /** the currently selected object. */
  protected List<LocatedObject> m_SelectedObjects;

  /** whether the mouse is being dragged. */
  protected boolean m_Dragging;

  /** the starting point of the drag. */
  protected Point m_DragStart;

  /** the end point of the drag. */
  protected Point m_DragEnd;

  /** the polygon plotter to use. */
  protected PolygonVertices m_PolygonVertices;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For moving existing annotations:\n"
      + "Left-click and drag with mouse.\n"
      + "Double-click and use arrow keys to move by one pixel or hold Shift and move by 10 pixels.\n"
      + "Esc to clear selection.";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Prefix          = LocatedObjects.DEFAULT_PREFIX;
    m_Dragging        = false;
    m_DragStart       = null;
    m_DragEnd         = null;
    m_SelectedObjects = new ArrayList<>();
    m_PolygonVertices = new PolygonVertices();
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "Move";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("move.png");
  }

  /**
   * Creates the mouse cursor to use.
   *
   * @return the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursor.getDefaultCursor();
  }

  /**
   * Updates overlay(s) for the vertices.
   */
  protected void updateVertexOverlays() {
    if (m_SelectedObjects.size() == 0)
      m_PolygonVertices.setPlotSubset(new LocatedObject[0]);
    else
      m_PolygonVertices.setPlotSubset(m_SelectedObjects);
  }

  /**
   * Attempts to select the object(s) at the specified location.
   *
   * @param p		the image location to select the annotation(s) from
   */
  protected void selectObjects(Point p) {
    m_SelectedObjects.clear();

    for (LocatedObject obj: getCanvas().getOwner().getObjects()) {
      if (obj.hasValidPolygon()) {
	if (obj.getPolygon().contains(p))
	  m_SelectedObjects.add(obj);
      }
      else {
	if (obj.getRectangle().contains(p))
	  m_SelectedObjects.add(obj);
      }
    }

    updateVertexOverlays();

    if (isLoggingEnabled())
      getLogger().info("selectedObjects: " + m_SelectedObjects);
  }

  /**
   * Moves the selected objects from starting point to end point.
   *
   * @param start	the start point
   * @param end		the end point
   * @return 		whether the objects got moved
   */
  protected boolean moveObjects(Point start, Point end) {
    Point			diff;
    List<LocatedObject>		moved;
    LocatedObject		objectOld;
    LocatedObject		objectNew;
    LocatedObjects		objectsNew;
    Report			reportNew;
    int				index;
    int				i;
    Rectangle			rect;
    Polygon			poly;

    if ((start == null) || (end == null) || start.equals(end))
      return false;

    diff       = new Point(end.x - start.x, end.y - start.y);
    moved      = new ArrayList<>();
    objectsNew = new LocatedObjects(getCanvas().getOwner().getObjects());
    for (i = 0; i < m_SelectedObjects.size(); i++) {
      objectOld = m_SelectedObjects.get(i);
      index     = objectsNew.indexOf(objectOld);
      if (index == -1) {
	getLogger().warning("Failed to locate object, cannot update: " + objectOld);
	return false;
      }

      rect = objectOld.getRectangle();
      rect.translate(diff.x, diff.y);
      poly = null;
      if (objectOld.hasPolygon()) {
	poly = objectOld.getPolygon();
	poly.translate(diff.x, diff.y);
      }
      objectNew = new LocatedObject(rect);
      objectNew.getMetaData().putAll(objectOld.getMetaData(true));
      if (poly != null)
	objectNew.setPolygon(poly);

      moved.add(objectNew);
      objectsNew.set(index, objectNew);
    }

    if (isLoggingEnabled())
      getLogger().info("objectsNew: " + objectsNew);

    reportNew = objectsNew.toReport(m_Prefix);
    getCanvas().getOwner().addUndoPoint("Moved objects: " + m_SelectedObjects + " -> " + moved);
    getCanvas().getOwner().setReport(reportNew);
    getCanvas().getOwner().annotationsChanged(this);
    getCanvas().getOwner().update();

    m_SelectedObjects = moved;
    updateVertexOverlays();

    return true;
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    return new ToolMouseAdapter(this) {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e)) {
          selectObjects(getCanvas().mouseToPixelLocation(e.getPoint()));
	  if (isLoggingEnabled())
	    getLogger().info("mouseClicked/selectObjects=" + m_SelectedObjects);
	  getCanvas().setCursor(Cursors.fromIcon("cursor_move.png", 15, 15));
	  getCanvas().requestFocus();
	  getCanvas().repaint();
          e.consume();
	}
	super.mouseClicked(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
	if (m_Dragging) {
	  m_DragEnd  = getCanvas().mouseToPixelLocation(e.getPoint());
	  if (isLoggingEnabled())
	    getLogger().info("mouseReleased/moveObject=" + m_DragStart + " -> " + m_DragEnd);
	  moveObjects(m_DragStart, m_DragEnd);
	  m_Dragging  = false;
	  m_DragStart = null;
	  m_DragEnd   = null;
	  getCanvas().setCursor(Cursor.getDefaultCursor());
	  getCanvas().repaint();
	  e.consume();
	}
	super.mouseReleased(e);
      }
    };
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return new ToolMouseMotionAdapter(this) {
      @Override
      public void mouseDragged(MouseEvent e) {
	if (!m_Dragging) {
	  selectObjects(getCanvas().mouseToPixelLocation(e.getPoint()));
	  if (m_SelectedObjects.size() > 0) {
	    m_DragStart = getCanvas().mouseToPixelLocation(e.getPoint());
	    m_Dragging  = true;
	    getCanvas().setCursor(Cursors.fromIcon("cursor_move.png", 15, 15));
	    if (isLoggingEnabled())
	      getLogger().info("mouseDragged/start=" + m_DragStart);
	    getCanvas().repaint();
	  }
	}
	super.mouseDragged(e);
      }
    };
  }

  @Override
  protected ToolKeyAdapter createKeyListener() {
    return new ToolKeyAdapter(this) {
      @Override
      public void keyPressed(KeyEvent e) {
        Point end = null;
        int inc = KeyUtils.isShiftDown(e.getModifiersEx()) ? 10 : 1;
	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  m_Dragging  = false;
	  m_DragStart = null;
	  m_DragEnd   = null;
	  m_SelectedObjects.clear();
	  updateVertexOverlays();
	  getCanvas().setCursor(Cursor.getDefaultCursor());
	  getCanvas().repaint();
	  e.consume();
	  if (isLoggingEnabled())
	    getLogger().info("keypressed/ESC");
	}
	else if (e.getKeyCode() == KeyEvent.VK_UP) {
	  end = new Point(10, 10 - inc);
	  e.consume();
	}
	else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
	  end = new Point(10, 10 + inc);
	  e.consume();
	}
	else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	  end = new Point(10 - inc, 10);
	  e.consume();
	}
	else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	  end = new Point(10 + inc, 10);
	  e.consume();
	}
	if (end != null) {
	  Point start = new Point(10, 10);
	  moveObjects(start, end);
	}
	super.keyPressed(e);
      }
    };
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_Prefix = m_TextPrefix.getText();
    if (isLoggingEnabled())
      getLogger().info("doApply: prefix=" + m_Prefix);
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_TextPrefix = new BaseTextField(LocatedObjects.DEFAULT_PREFIX, 10);
    m_TextPrefix.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Prefix", m_TextPrefix);
  }

  /**
   * Gets called to activate the tool.
   */
  @Override
  public void activate() {
    Overlay		current;
    ObjectAnnotations	objAnn;
    ObjectAnnotations	objAnnCurr;
    MultiOverlay	multi;

    // integrate overlay for selected vertices
    current = getCanvas().getOwner().getOverlay();
    objAnn  = new ObjectAnnotations();
    objAnn.setOutlinePlotters(new OutlinePlotter[]{m_PolygonVertices});
    // can we align the colors?
    if (current instanceof ObjectAnnotations) {
      objAnnCurr = (ObjectAnnotations) current;
      if (objAnnCurr.getOutlinePlotters().length == 1)
        objAnn.setOutlineColors(ObjectCopyHelper.copyObjects(objAnnCurr.getOutlineColors()));
    }
    multi = new MultiOverlay();
    multi.setOverlays(new Overlay[]{current, objAnn});
    getCanvas().getOwner().setOverlay(multi);

    updateVertexOverlays();

    super.activate();
    getCanvas().getOwner().setAnnotator(new NullAnnotator());
  }

  /**
   * Gets called to deactivate the tool.
   */
  @Override
  public void deactivate() {
    Overlay		current;
    MultiOverlay	multi;

    // remove overlay for vertices
    current = getCanvas().getOwner().getOverlay();
    if (current instanceof MultiOverlay) {
      multi = (MultiOverlay) current;
      getCanvas().getOwner().setOverlay(multi.getOverlays()[0]);
    }

    super.deactivate();
  }
}
