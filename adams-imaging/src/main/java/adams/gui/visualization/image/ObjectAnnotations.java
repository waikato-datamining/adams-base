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
 * ObjectAnnotations.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.flow.transformer.locateobjects.AcceptAllLocatedObjectsFilter;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjectFilter;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.SearchEvent;
import adams.gui.visualization.object.objectannotations.cleaning.AnnotationCleaner;
import adams.gui.visualization.object.objectannotations.colors.AnnotationColors;
import adams.gui.visualization.object.objectannotations.colors.FixedColor;
import adams.gui.visualization.object.objectannotations.label.LabelPlotter;
import adams.gui.visualization.object.objectannotations.label.NoLabel;
import adams.gui.visualization.object.objectannotations.outline.NoOutline;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.shape.NoShape;
import adams.gui.visualization.object.objectannotations.shape.ShapePlotter;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;

/**
 * Overlays object annotations from the report.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotations
  extends AbstractImageOverlay
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -3088909952142797917L;

  /**
   * The panel for displaying the located objects.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public final static class LocatedObjectsPanel
    extends BasePanel {

    private static final long serialVersionUID = -2961421584086204608L;

    /** the owner. */
    protected ObjectAnnotations m_Owner;

    /** the located objects. */
    protected LocatedObjects m_LocatedObjects;

    /** the table with the objects. */
    protected SortableAndSearchableTable m_TableObjects;

    /** the table model with the objects. */
    protected LocatedObjectsTableModel m_ModelObjects;

    /** the label for counts. */
    protected JLabel m_LabelCounts;

    /** the button for selecting all. */
    protected BaseButton m_ButtonAll;

    /** the button for selecting none. */
    protected BaseButton m_ButtonNone;

    /** the button for inverting the selection. */
    protected BaseButton m_ButtonInvert;

    /** the panel for searching the table. */
    protected SearchPanel m_PanelSearch;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Owner          = null;
      m_LocatedObjects = new LocatedObjects();
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel panelBottom;
      JPanel	panel;

      super.initGUI();

      setLayout(new BorderLayout());

      m_ModelObjects = new LocatedObjectsTableModel(LocatedObjectsTableModel.MetaDataDisplay.MULTI_COLUMN);
      m_TableObjects = new SortableAndSearchableTable(m_ModelObjects);
      m_TableObjects.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
      m_TableObjects.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      m_TableObjects.setShowSimplePopupMenus(true);
      m_TableObjects.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
	updateCounts();
	if (m_Owner == null)
	  return;
	if (m_Owner.getOwner() == null)
	  return;
	if (m_Owner.getOwner().getOwner() == null)
	  return;
	m_Owner.getOwner().update();
      });
      add(new BaseScrollPane(m_TableObjects), BorderLayout.CENTER);

      // bottom panel
      panelBottom = new JPanel(new GridLayout(3, 1, 0, 0));
      add(panelBottom, BorderLayout.SOUTH);

      // counts
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
      panelBottom.add(panel);
      m_LabelCounts = new JLabel();
      panel.add(m_LabelCounts);

      // buttons
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
      panelBottom.add(panel);

      m_ButtonAll = new BaseButton("All");
      m_ButtonAll.setToolTipText("Selects all objects");
      m_ButtonAll.addActionListener((ActionEvent) -> m_TableObjects.selectAll());
      panel.add(m_ButtonAll);

      m_ButtonNone = new BaseButton("None");
      m_ButtonNone.setToolTipText("Removes any selection");
      m_ButtonNone.addActionListener((ActionEvent) -> m_TableObjects.selectNone());
      panel.add(m_ButtonNone);

      m_ButtonInvert = new BaseButton("Invert");
      m_ButtonInvert.setToolTipText("Inverts the selection");
      m_ButtonInvert.addActionListener((ActionEvent) -> m_TableObjects.invertSelection());
      panel.add(m_ButtonInvert);

      setPreferredSize(new Dimension(250, 0));

      m_PanelSearch = new SearchPanel(SearchPanel.LayoutType.HORIZONTAL, false);
      m_PanelSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
      m_PanelSearch.addSearchListener((SearchEvent e) ->
	m_TableObjects.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
      panelBottom.add(m_PanelSearch);
    }

    /**
     * Finishes the initialization.
     */
    @Override
    protected void finishInit() {
      super.finishInit();
      updateCounts();
    }

    /**
     * Sets the owner.
     *
     * @param value	the owner to use
     */
    public void setOwner(ObjectAnnotations value) {
      m_Owner = value;
      if (m_Owner != null)
	update();
    }

    /**
     * Returns the owner.
     *
     * @return		the owner, null if none set
     */
    public ObjectAnnotations getOwner() {
      return m_Owner;
    }

    /**
     * Updates the labels with the counts.
     */
    protected void updateCounts() {
      m_LabelCounts.setText("Total: " + m_ModelObjects.getRowCount() + ", Selected: " + m_TableObjects.getSelectedRows().length);
    }

    /**
     * Sets the located objects to display. Automatically updates the view.
     *
     * @param value	the objects
     */
    public void setLocatedObjects(LocatedObjects value) {
      Set<LocatedObject> currentObjs;
      Set<LocatedObject> 	newObjs;

      if (value == null)
	return;

      // update necessary?
      currentObjs = new HashSet<>(m_LocatedObjects);
      newObjs     = new HashSet<>(value);
      if (currentObjs.containsAll(newObjs) && newObjs.containsAll(currentObjs))
	return;

      m_LocatedObjects = new LocatedObjects(value);
      m_LocatedObjects.sort((LocatedObject o1, LocatedObject o2) -> {
	int result = 0;
	if (result == 0)
	  result = Integer.compare(o1.getX(), o2.getX());
	if (result == 0)
	  result = Integer.compare(o1.getY(), o2.getY());
	if (result == 0)
	  result = Integer.compare(o1.getWidth(), o2.getWidth());
	if (result == 0)
	  result = Integer.compare(o1.getHeight(), o2.getHeight());
	return result;
      });
      update();
      m_TableObjects.setOptimalColumnWidths(new int[]{0, 1, 2, 3, 4});
      m_TableObjects.selectAll();
    }

    /**
     * Returns the located objects being displayed.
     *
     * @return		the objects
     */
    public LocatedObjects getLocatedObjects() {
      return m_LocatedObjects;
    }

    /**
     * Updates the display.
     */
    public void update() {
      LocatedObjectsTableModel	model;
      int[]			selRows;
      TIntList 			selected;
      int			index;

      if (m_Owner == null)
	return;

      selRows = m_TableObjects.getSelectedRows();
      model   = new LocatedObjectsTableModel(m_ModelObjects.getMetaDataDisplay(), m_LocatedObjects);

      m_ModelObjects = model;
      m_TableObjects.setModel(m_ModelObjects);
      selected = new TIntArrayList();
      for (int selRow: selRows) {
        index = model.indexOf(m_ModelObjects.getObjects().get(m_TableObjects.getActualRow(selRow)));
	if (index > -1)
	  selected.add(index);
      }
      m_TableObjects.setSelectedRows(selected.toArray());
    }

    /**
     * Returns a filter that accepts only the selected objects.
     *
     * @return		the filter
     */
    public LocatedObjectFilter getFilter() {
      Set<LocatedObject> 	selectedSet;
      int[]			selRows;

      if (m_ModelObjects == null)
	return new AcceptAllLocatedObjectsFilter();

      selectedSet = new HashSet<>();
      selRows     = m_TableObjects.getSelectedRows();
      for (int selRow: selRows)
        selectedSet.add(m_ModelObjects.getObjects().get(m_TableObjects.getActualRow(selRow)));

      return new AbstractObjectOverlayFromReport.SelectedObjectFilter(selectedSet);
    }
  }

  /** the prefix to use. */
  protected String m_Prefix;

  /** the cleaners to use. */
  protected AnnotationCleaner[] m_Cleaners;

  /** the shape plotters. */
  protected ShapePlotter[] m_ShapePlotters;

  /** the colorizers for the shape. */
  protected AnnotationColors[] m_ShapeColors;

  /** the outline plotters. */
  protected OutlinePlotter[] m_OutlinePlotters;

  /** the colorizers for the outline. */
  protected AnnotationColors[] m_OutlineColors;

  /** the label plotters. */
  protected LabelPlotter[] m_LabelPlotters;

  /** the colorizers for the labels. */
  protected AnnotationColors[] m_LabelColors;

  /** the annotations. */
  protected transient LocatedObjects m_Annotations;

  /** whether to show the located object panel. */
  protected boolean m_ShowObjectPanel;

  /** the panel with the located objects. */
  protected LocatedObjectsPanel m_PanelObjects;

  /** the owning panel. */
  protected transient ImagePanel.PaintPanel m_Owner;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays object annotations from the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "cleaner", "cleaners",
      new AnnotationCleaner[0]);

    m_OptionManager.add(
      "shape-plotter", "shapePlotters",
      new ShapePlotter[0]);

    m_OptionManager.add(
      "shape-color", "shapeColors",
      new AnnotationColors[0]);

    m_OptionManager.add(
      "outline-plotter", "outlinePlotters",
      new OutlinePlotter[0]);

    m_OptionManager.add(
      "outline-color", "outlineColors",
      new AnnotationColors[0]);

    m_OptionManager.add(
      "label-plotter", "labelPlotters",
      new LabelPlotter[0]);

    m_OptionManager.add(
      "label-color", "labelColors",
      new AnnotationColors[0]);

    m_OptionManager.add(
      "show-object-panel", "showObjectPanel",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Annotations = null;
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  @Override
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  @Override
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The report field prefix used for objects.";
  }

  /**
   * Sets the cleaners for the annotations.
   *
   * @param value 	the cleaners
   */
  public void setCleaners(AnnotationCleaner[] value) {
    m_Cleaners = value;
    reset();
  }

  /**
   * Returns the cleaners for the annotations.
   *
   * @return 		the cleaners
   */
  public AnnotationCleaner[] getCleaners() {
    return m_Cleaners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanersTipText() {
    return "The cleaners to apply to the annotations.";
  }

  /**
   * Sets the colorizers for the shape plotters.
   *
   * @param value 	the colorizers
   */
  public void setShapeColors(AnnotationColors[] value) {
    m_ShapeColors   = value;
    m_ShapePlotters = (ShapePlotter[]) Utils.adjustArray(m_ShapePlotters, m_ShapeColors.length, new NoShape());
    reset();
  }

  /**
   * Returns the colorizers for the shape plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getShapeColors() {
    return m_ShapeColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorsTipText() {
    return "The colorizers for the corresponding shape plotters.";
  }

  /**
   * Sets the plotters for the shapes.
   *
   * @param value 	the plotters
   */
  public void setShapePlotters(ShapePlotter[] value) {
    m_ShapePlotters = value;
    m_ShapeColors   = (AnnotationColors[]) Utils.adjustArray(m_ShapeColors, m_ShapePlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the shapes.
   *
   * @return 		the plotters
   */
  public ShapePlotter[] getShapePlotters() {
    return m_ShapePlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapePlottersTipText() {
    return "The plotters to use for drawing the shapes.";
  }

  /**
   * Sets the colorizers for the outline plotters.
   *
   * @param value 	the colorizers
   */
  public void setOutlineColors(AnnotationColors[] value) {
    m_OutlineColors   = value;
    m_OutlinePlotters = (OutlinePlotter[]) Utils.adjustArray(m_OutlinePlotters, m_OutlineColors.length, new NoOutline());
    reset();
  }

  /**
   * Returns the colorizers for the outline plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getOutlineColors() {
    return m_OutlineColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlineColorsTipText() {
    return "The colorizers for the corresponding outline plotters.";
  }

  /**
   * Sets the plotters for the outlines.
   *
   * @param value 	the plotters
   */
  public void setOutlinePlotters(OutlinePlotter[] value) {
    m_OutlinePlotters = value;
    m_OutlineColors   = (AnnotationColors[]) Utils.adjustArray(m_OutlineColors, m_OutlinePlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the outlines.
   *
   * @return 		the plotters
   */
  public OutlinePlotter[] getOutlinePlotters() {
    return m_OutlinePlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlinePlottersTipText() {
    return "The plotters to use for drawing the outlines.";
  }

  /**
   * Sets the colorizers for the label plotters.
   *
   * @param value 	the colorizers
   */
  public void setLabelColors(AnnotationColors[] value) {
    m_LabelColors   = value;
    m_LabelPlotters = (LabelPlotter[]) Utils.adjustArray(m_LabelPlotters, m_LabelColors.length, new NoLabel());
    reset();
  }

  /**
   * Returns the colorizers for the label plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getLabelColors() {
    return m_LabelColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelColorsTipText() {
    return "The colorizers for the corresponding label plotters.";
  }

  /**
   * Sets the plotters for the labels.
   *
   * @param value 	the plotters
   */
  public void setLabelPlotters(LabelPlotter[] value) {
    m_LabelPlotters = value;
    m_LabelColors   = (AnnotationColors[]) Utils.adjustArray(m_LabelColors, m_LabelPlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the labels.
   *
   * @return 		the plotters
   */
  public LabelPlotter[] getLabelPlotters() {
    return m_LabelPlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelPlottersTipText() {
    return "The plotters to use for drawing the labels.";
  }

  /**
   * Sets whether to show the panel with the located panels.
   *
   * @param value 	true if to show
   */
  public void setShowObjectPanel(boolean value) {
    m_ShowObjectPanel = value;
    reset();
  }

  /**
   * Returns whether to show the panel with the located objects.
   *
   * @return 		true if to show
   */
  public boolean getShowObjectPanel() {
    return m_ShowObjectPanel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showObjectPanelTipText() {
    return "If enabled, the panel for selecting located objects is being displayed.";
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner, null if none set
   */
  public ImagePanel.PaintPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the objects panel, instantiates it if necessary.
   *
   * @return		the panel
   */
  protected LocatedObjectsPanel getLocatedObjectsPanel() {
    if (m_PanelObjects == null) {
      m_PanelObjects = new LocatedObjectsPanel();
      m_PanelObjects.setOwner(this);
    }
    m_PanelObjects.setLocatedObjects(m_Annotations);

    return m_PanelObjects;
  }

  /**
   * Gets called when the image overlay got added to a paintable panel.
   *
   * @param panel	the panel it got added to
   */
  @Override
  public void overlayAdded(ImagePanel.PaintPanel panel) {
    super.overlayAdded(panel);

    m_Owner = panel;

    if (panel.getOwner() == null)
      return;

    if (m_ShowObjectPanel)
      panel.getOwner().setLeftPanel(getLocatedObjectsPanel());
    else
      panel.getOwner().removeLeftPanel();
  }

  /**
   * Gets called when the image overlay got removed from a paintable panel.
   *
   * @param panel	the panel it got removed from
   */
  @Override
  public void overlayRemoved(ImagePanel.PaintPanel panel) {
    if (m_ShowObjectPanel) {
      if (panel.getOwner() != null)
	panel.getOwner().removeLeftPanel();
    }

    m_Owner = null;

    super.overlayRemoved(panel);
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(ImagePanel.PaintPanel panel) {
    m_Annotations = null;
  }

  /**
   * Initializes the annotations.
   *
   * @param panel	the context
   */
  protected void initAnnotations(ImagePanel.PaintPanel panel) {
    MessageCollection	errors;

    if (m_Annotations != null)
      return;

    errors = new MessageCollection();

    // clean
    m_Annotations = LocatedObjects.fromReport(panel.getOwner().getAllProperties(), m_Prefix);
    for (AnnotationCleaner cleaner: m_Cleaners) {
      m_Annotations = cleaner.cleanAnnotations(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // shape colors
    for (AnnotationColors colors: m_ShapeColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // outline colors
    for (AnnotationColors colors: m_OutlineColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // label colors
    for (AnnotationColors colors: m_LabelColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }
  }

  /**
   * Performs the actual painting of the objects.
   *
   * @param panel the panel this overlay is for
   * @param g     the graphics context
   * @param annotations 	the annotations to paint
   */
  protected void doPaintObjects(ImagePanel.PaintPanel panel, Graphics g, LocatedObjects annotations) {
    Graphics2D	g2d;
    int		i;

    g2d = (Graphics2D) g;
    for (LocatedObject object: annotations) {
      for (i = 0; i < m_ShapePlotters.length; i++)
	m_ShapePlotters[i].plotShape(object, m_ShapeColors[i].getColor(object), g2d);
      for (i = 0; i < m_OutlinePlotters.length; i++)
	m_OutlinePlotters[i].plotOutline(object, m_OutlineColors[i].getColor(object), g2d);
      for (i = 0; i < m_LabelPlotters.length; i++)
	m_LabelPlotters[i].plotLabel(object, m_LabelColors[i].getColor(object), g2d);
    }
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel the panel this overlay is for
   * @param g     the graphics context
   */
  @Override
  protected void doPaintOverlay(ImagePanel.PaintPanel panel, Graphics g) {
    boolean			updated;
    JPanel			left;
    JPanel			objects;
    LocatedObjects		annotations;
    LocatedObjectFilter		filter;

    initAnnotations(panel);

    if (m_ShowObjectPanel) {
      left = panel.getOwner().getLeftPanel();
      objects = getLocatedObjectsPanel();
      if (left != objects)
	panel.getOwner().setLeftPanel(objects);
    }

    // filter
    filter = null;
    if (m_ShowObjectPanel)
      filter = m_PanelObjects.getFilter();
    if (filter == null)
      filter = new AcceptAllLocatedObjectsFilter();
    annotations = new LocatedObjects();
    if (m_Annotations != null) {
      for (LocatedObject obj : m_Annotations) {
	if (!filter.accept(obj))
	  continue;
	annotations.add(obj);
      }
    }

    doPaintObjects(panel, g, annotations);

    if (m_ShowObjectPanel)
      m_PanelObjects.setLocatedObjects(m_Annotations);
  }
}
