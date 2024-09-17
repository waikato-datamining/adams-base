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
import adams.data.geometry.PolygonUtils;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseTextField;
import adams.gui.core.ImageManager;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.image.RectangleUtils;
import adams.gui.visualization.object.annotator.NullAnnotator;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.outline.PolygonVertices;
import adams.gui.visualization.object.objectannotations.outline.VertexShape;
import adams.gui.visualization.object.overlay.MultiOverlay;
import adams.gui.visualization.object.overlay.ObjectAnnotations;
import adams.gui.visualization.object.overlay.Overlay;
import com.github.fracpete.javautils.struct.Struct2;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * For editing existing annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Edit
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = -3238804649373495561L;

  /** the radio button for bounding box. */
  protected JRadioButton m_RadioBoundingBox;

  /** the radio button for polygon. */
  protected JRadioButton m_RadioPolygon;

  /** the tolerance in pixels for selecting a vertex. */
  protected NumberTextField m_TextSelectionTolerance;

  /** the object prefix to use. */
  protected BaseTextField m_TextPrefix;

  /** whether to edit bboxes. */
  protected boolean m_BoundingBox;

  /** the tolerance in pixels for selecting a vertex. */
  protected int m_SelectionTolerance;

  /** the object prefix to use. */
  protected String m_Prefix;

  /** the currently selected object. */
  protected List<LocatedObject> m_SelectedObjects;

  /** the polygon plotter to use. */
  protected PolygonVertices m_PolygonVertices;

  /** whether a vertex was selected. */
  protected boolean m_Selected;

  /** the affected object. */
  protected LocatedObject m_Object;

  /** the old vertex. */
  protected Point m_VertexOld;

  /** the new vertex. */
  protected Point m_VertexNew;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For editing existing annotations.\n"
      + "Double-click on annotation to select for editing, handles for vertices will show up.\n"
      + "Left-click on existing handle and drag it to new position.\n"
      + "Left-click while holding CTRL+SHIFT to add a new vertex.";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_BoundingBox        = false;
    m_SelectionTolerance = 5;
    m_Prefix             = LocatedObjects.DEFAULT_PREFIX;
    m_Selected           = false;
    m_VertexOld          = null;
    m_VertexNew          = null;
    m_SelectedObjects    = new ArrayList<>();
    m_PolygonVertices    = new PolygonVertices();
    m_PolygonVertices.setShape(VertexShape.BOX_FILLED);
    m_PolygonVertices.setExtent(7);
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "Edit";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("edit.png");
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
      if (m_BoundingBox) {
	if (obj.getRectangle().contains(p))
	  m_SelectedObjects.add(obj);
      }
      else {
	if (obj.hasValidPolygon()) {
	  if (obj.getPolygon().contains(p))
	    m_SelectedObjects.add(obj);
	}
	else {
	  if (obj.getRectangle().contains(p))
	    m_SelectedObjects.add(obj);
	}
      }
    }

    updateVertexOverlays();

    if (isLoggingEnabled())
      getLogger().info("selectedObjects: " + m_SelectedObjects);
  }

  /**
   * Checks whether a vertex is close enough to the specified point clicked by the user.
   *
   * @param p		the point clicked by the user
   * @param tolerance	the tolerance in pixels
   * @param x		the x position of the vertex
   * @param y		the y position of the vertex
   * @return		true if close enough
   */
  protected boolean isHit(Point p, int tolerance, int x, int y) {
    boolean	xOK;
    boolean	yOK;

    xOK = (Math.abs(p.x - x) <= tolerance);
    yOK = (Math.abs(p.y - y) <= tolerance);

    return xOK && yOK;
  }

  /**
   * Returns the vertex that is associated with the location in the image.
   *
   * @param p		the location in the image
   * @return		the vertex, null if no hit
   */
  protected Struct2<LocatedObject,Point> selectVertex(Point p) {
    LocatedObject	object;
    Point 		vertex;
    Polygon		poly;
    int			tolerance;
    int[]		x;
    int[]		y;
    int			i;

    object    = null;
    vertex    = null;
    tolerance = (int) Math.round(m_SelectionTolerance / getCanvas().getActualZoom());

    for (LocatedObject obj: m_SelectedObjects) {
      if (m_BoundingBox) {
	x = new int[]{obj.getX(), obj.getX() + obj.getWidth() - 1, obj.getX() + obj.getWidth() - 1,  obj.getX()};
	y = new int[]{obj.getY(), obj.getY(),                      obj.getY() + obj.getHeight() - 1, obj.getY() + obj.getHeight() - 1};
      }
      else {
	if (!obj.hasPolygon() || !obj.hasValidPolygon()) {
	  poly = PolygonUtils.toPolygon(obj.getRectangle());
	  x    = poly.xpoints;
	  y    = poly.ypoints;
	}
	else {
	  x = obj.getPolygonX();
	  y = obj.getPolygonY();
	}
      }
      for (i = 0; i < x.length; i++) {
	if (isHit(p, tolerance, x[i], y[i])) {
	  object = obj;
	  vertex = new Point(x[i], y[i]);
	  break;
	}
      }
    }

    return new Struct2<>(object, vertex);
  }

  /**
   * Updates the selected object using the old/new vertices.
   *
   * @param objectOld 	the affected object
   * @param vertexOld	the old vertex
   * @param vertexNew	the new vertex
   * @return 		whether updated successfully
   */
  protected boolean updateVertex(LocatedObject objectOld, Point vertexOld, Point vertexNew) {
    Polygon		poly;
    int[]		x;
    int[]		y;
    int			i;
    int			index;
    Rectangle		rect;
    LocatedObject	objectNew;
    LocatedObjects	objectsNew;
    Report		reportNew;
    boolean		updated;

    if ((objectOld == null) || (vertexOld == null) || (vertexNew == null))
      return false;

    objectsNew = new LocatedObjects(getCanvas().getOwner().getObjects());
    index      = objectsNew.indexOf(objectOld);
    if (index == -1) {
      getLogger().warning("Failed to locate object, cannot update: " + objectOld);
      return false;
    }

    updated = false;
    if (m_BoundingBox) {
      rect      = objectOld.getRectangle();
      objectNew = new LocatedObject(RectangleUtils.updateCorner(rect, vertexOld, vertexNew));
      objectNew.getMetaData().putAll(objectOld.getMetaData(true));
      updated   = true;
    }
    else {
      if (!objectOld.hasPolygon() || !objectOld.hasValidPolygon()) {
	poly = PolygonUtils.toPolygon(objectOld.getRectangle());
	x    = poly.xpoints;
	y    = poly.ypoints;
      }
      else {
	x = objectOld.getPolygonX();
	y = objectOld.getPolygonY();
      }
      for (i = 0; i < x.length; i++) {
	if ((x[i] == vertexOld.x) && (y[i] == vertexOld.y)) {
	  x[i]    = vertexNew.x;
	  y[i]    = vertexNew.y;
	  updated = true;
	  break;
	}
      }
      poly = new Polygon(x, y, x.length);
      objectNew = new LocatedObject(poly.getBounds());
      objectNew.getMetaData().putAll(objectOld.getMetaData(true));
      objectNew.setPolygon(poly);
    }

    if (!updated) {
      getLogger().warning("Failed to update object: " + objectOld);
      return false;
    }

    if (isLoggingEnabled())
      getLogger().info("objectNew: " + objectNew);

    objectsNew.set(index, objectNew);
    reportNew = objectsNew.toReport(m_Prefix);
    getCanvas().getOwner().addUndoPoint("Updated object: " + objectOld + " -> " + objectNew);
    getCanvas().getOwner().setReport(reportNew);
    getCanvas().getOwner().annotationsChanged(this);
    getCanvas().getOwner().update();

    index = m_SelectedObjects.indexOf(objectOld);
    if (index == -1) {
      m_SelectedObjects.clear();
      getLogger().warning("updateVertex: Failed to locate object: " + objectOld);
    }
    else {
      m_SelectedObjects.set(index, objectNew);
    }

    updateVertexOverlays();

    return true;
  }

  /**
   * Adds the vertex to the specified object.
   *
   * @param objectOld	the object to update
   * @param vertex	the vertex to add
   * @return 		whether updated successfully
   */
  protected boolean addVertex(LocatedObject objectOld, Point vertex) {
    int			index;
    Polygon 		polyOld;
    Polygon		polyNew;
    LocatedObject	objectNew;
    LocatedObjects	objectsNew;
    Report		reportNew;

    if ((objectOld == null) || (vertex == null))
      return false;
    if (m_BoundingBox)
      return false;

    objectsNew = new LocatedObjects(getCanvas().getOwner().getObjects());
    index      = objectsNew.indexOf(objectOld);
    if (index == -1) {
      getLogger().warning("Failed to locate object, cannot update: " + objectOld);
      return false;
    }

    if (!objectOld.hasPolygon() || !objectOld.hasValidPolygon())
      polyOld = PolygonUtils.toPolygon(objectOld.getRectangle());
    else
      polyOld = objectOld.getPolygon();

    polyNew = PolygonUtils.addVertext(polyOld, vertex);
    objectNew = new LocatedObject(polyNew.getBounds());
    objectNew.getMetaData().putAll(objectOld.getMetaData(true));
    objectNew.setPolygon(polyNew);

    if (isLoggingEnabled())
      getLogger().info("objectNew: " + objectNew);

    objectsNew.set(index, objectNew);
    reportNew = objectsNew.toReport(m_Prefix);
    getCanvas().getOwner().addUndoPoint("Updated object: " + objectOld + " -> " + objectNew);
    getCanvas().getOwner().setReport(reportNew);
    getCanvas().getOwner().annotationsChanged(this);
    getCanvas().getOwner().update();

    index = m_SelectedObjects.indexOf(objectOld);
    if (index == -1) {
      getLogger().warning("addVertex: Failed to locate object: " + objectOld);
      m_SelectedObjects.clear();
    }
    else {
      m_SelectedObjects.set(index, objectNew);
    }

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
	if (MouseUtils.isDoubleClick(e) && MouseUtils.hasNoModifierKey(e)) {
	  selectObjects(getCanvas().mouseToPixelLocation(e.getPoint()));
	  getCanvas().repaint();
	  e.consume();
	  if (isLoggingEnabled())
	    getLogger().info("double click=" + m_SelectedObjects);
	}
	super.mouseClicked(e);
      }

      @Override
      public void mousePressed(MouseEvent e) {
	if (m_SelectedObjects.size() > 0) {
	  if (KeyUtils.isCtrlDown(e.getModifiersEx()) && KeyUtils.isShiftDown(e.getModifiersEx()) &&!m_BoundingBox) {
	    Point vertex = getCanvas().mouseToPixelLocation(e.getPoint());
	    addVertex(m_SelectedObjects.get(0), vertex);
	    e.consume();
	    if (isLoggingEnabled())
	      getLogger().info("mousePressed/addVertex: obj=" + m_Object + ", vertex=" + vertex);
	  }
	  else {
	    m_VertexNew = null;
	    Struct2<LocatedObject, Point> hit = selectVertex(getCanvas().mouseToPixelLocation(e.getPoint()));
	    m_Object = hit.value1;
	    m_VertexOld = hit.value2;
	    m_Selected = (m_VertexOld != null);
	    if (m_Selected)
	      e.consume();
	    getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    if (isLoggingEnabled())
	      getLogger().info("mousePressed/selectVertex: obj=" + m_Object + ", vertexOld=" + m_VertexOld);
	  }
	}
	super.mousePressed(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
	if (m_Selected) {
	  m_VertexNew = getCanvas().mouseToPixelLocation(e.getPoint());
	  if (isLoggingEnabled())
	    getLogger().info("mouseReleased/vertexNew=" + m_VertexNew);
	  updateVertex(m_Object, m_VertexOld, m_VertexNew);
	  m_Object    = null;
	  m_VertexOld = null;
	  m_VertexNew = null;
	  m_Selected  = false;
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
    return null;
  }

  /**
   * Creates the key listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolKeyAdapter createKeyListener() {
    return new ToolKeyAdapter(this) {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  m_Object    = null;
	  m_VertexOld = null;
	  m_VertexNew = null;
	  m_Selected  = false;
	  m_SelectedObjects.clear();
	  updateVertexOverlays();
	  getCanvas().setCursor(Cursor.getDefaultCursor());
	  getCanvas().repaint();
	  e.consume();
	  if (isLoggingEnabled())
	    getLogger().info("keypressed/ESC");
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
    m_BoundingBox        = m_RadioBoundingBox.isSelected();
    m_SelectionTolerance = m_TextSelectionTolerance.getValue().intValue();
    m_Prefix             = m_TextPrefix.getText();

    updateVertexOverlays();

    if (isLoggingEnabled())
      getLogger().info("doApply: bbox=" + m_BoundingBox + ", tolerance=" + m_SelectionTolerance + ", prefix=" + m_Prefix);
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    ButtonGroup group;

    group = new ButtonGroup();

    m_RadioBoundingBox = new JRadioButton();
    m_RadioBoundingBox.setSelected(m_BoundingBox);
    m_RadioBoundingBox.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioBoundingBox);
    paramPanel.addParameter("Bounding box", m_RadioBoundingBox);

    m_RadioPolygon = new JRadioButton();
    m_RadioPolygon.setSelected(!m_BoundingBox);
    m_RadioPolygon.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioPolygon);
    paramPanel.addParameter("Polygon", m_RadioPolygon);

    m_RadioBoundingBox.setSelected(true);

    m_TextSelectionTolerance = new NumberTextField(NumberTextField.Type.INTEGER, "5");
    m_TextSelectionTolerance.setColumns(5);
    m_TextSelectionTolerance.setToolTipText("The tolerance in pixels for selecting a vertex");
    m_TextSelectionTolerance.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 0, null));
    m_TextSelectionTolerance.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Tolerance", m_TextSelectionTolerance);

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
    ObjectAnnotations 	objAnn;
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
    Overlay current;
    MultiOverlay multi;

    // remove overlay for vertices
    current = getCanvas().getOwner().getOverlay();
    if (current instanceof MultiOverlay) {
      multi = (MultiOverlay) current;
      getCanvas().getOwner().setOverlay(multi.getOverlays()[0]);
    }

    super.deactivate();
  }
}
