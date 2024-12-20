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
 * AbstractObjectOverlayFromReport.java
 * Copyright (C) 2017-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.data.image.ImageAnchor;
import adams.flow.transformer.locateobjects.AcceptAllLocatedObjectsFilter;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjectFilter;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.core.SearchPanel;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.SearchEvent;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.TranslucentColorProvider;
import adams.gui.visualization.image.ImagePanel.PaintPanel;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for overlays that use object locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectOverlayFromReport
  extends AbstractImageOverlay
  implements PopupMenuCustomizer<PaintPanel>, TypeColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = ReportObjectOverlay.PREFIX_DEFAULT;

  /**
   * Filter for located objects that only accepts the currently selected ones.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public final static class SelectedObjectFilter
    implements Serializable, LocatedObjectFilter {

    private static final long serialVersionUID = -2342481415499910354L;

    /** the selected objects. */
    protected Set<LocatedObject> m_Selected;

    /**
     * Initializes the filter.
     *
     * @param selected	the only objects to object
     */
    public SelectedObjectFilter(Set<LocatedObject> selected) {
      m_Selected = selected;
    }

    /**
     * Whether to accept the located object.
     *
     * @param obj the object to check
     * @return true if accepted
     */
    @Override
    public boolean accept(LocatedObject obj) {
      return m_Selected.contains(obj);
    }
  }

  /**
   * The panel for displaying the located objects.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public final static class LocatedObjectsPanel
    extends BasePanel {

    private static final long serialVersionUID = -2961421584086204608L;

    /** the owner. */
    protected AbstractObjectOverlayFromReport m_Owner;

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
      JPanel	panelBottom;
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

      m_PanelSearch = new SearchPanel(SearchPanel.LayoutType.HORIZONTAL, false);
      m_PanelSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
      m_PanelSearch.addSearchListener((SearchEvent e) ->
	m_TableObjects.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
      panelBottom.add(m_PanelSearch);

      setPreferredSize(new Dimension(250, 0));
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
    public void setOwner(AbstractObjectOverlayFromReport value) {
      m_Owner = value;
      if (m_Owner != null)
	update();
    }

    /**
     * Returns the owner.
     *
     * @return		the owner, null if none set
     */
    public AbstractObjectOverlayFromReport getOwner() {
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
      Set<LocatedObject> 	currentObjs;
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
      int			actRow;
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
        actRow = m_TableObjects.getActualRow(selRow);
        if (actRow == -1)
          continue;
	index = model.indexOf(m_ModelObjects.getObjects().get(actRow));
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

  /** the overlay handler. */
  protected ReportObjectOverlay m_Overlays;

  /** the listeners for locations updates. */
  protected Set<ChangeListener> m_LocationsUpdatedListeners;

  /** whether to show the located object panel. */
  protected boolean m_ShowObjectPanel;

  /** the panel with the located objects. */
  protected LocatedObjectsPanel m_PanelObjects;

  /** the owning panel. */
  protected transient PaintPanel m_Owner;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      PREFIX_DEFAULT);

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "use-colors-per-type", "useColorsPerType",
      false);

    m_OptionManager.add(
      "type-color-provider", "typeColorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "type-suffix", "typeSuffix",
      ".type");

    m_OptionManager.add(
      "type-regexp", "typeRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "label-format", "labelFormat",
      "#");

    m_OptionManager.add(
      "label-font", "labelFont",
      Fonts.getSansFont(14));

    m_OptionManager.add(
      "label-anchor", "labelAnchor",
      getDefaultLabelAnchor());

    m_OptionManager.add(
      "label-offset-x", "labelOffsetX",
      getDefaultLabelOffsetX());

    m_OptionManager.add(
      "label-offset-y", "labelOffsetY",
      getDefaultLabelOffsetY());

    m_OptionManager.add(
      "predefined-labels", "predefinedLabels",
      new BaseString[0]);

    m_OptionManager.add(
      "vary-shape-color", "varyShapeColor",
      false);

    m_OptionManager.add(
      "shape-color-provider", "shapeColorProvider",
      new TranslucentColorProvider());

    m_OptionManager.add(
      "show-object-panel", "showObjectPanel",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner                     = null;
    m_Overlays                  = new ReportObjectOverlay();
    m_LocationsUpdatedListeners = new HashSet<>();
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner, null if none set
   */
  public PaintPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the underlying report object overlay object.
   *
   * @return		the overlay
   */
  protected ReportObjectOverlay getOverlays() {
    return m_Overlays;
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Overlays.setPrefix(value);
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Overlays.getPrefix();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return m_Overlays.prefixTipText();
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Overlays.setColor(value);
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Overlays.getColor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return m_Overlays.colorTipText();
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_Overlays.setUseColorsPerType(value);
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_Overlays.getUseColorsPerType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return m_Overlays.useColorsPerTypeTipText();
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_Overlays.setTypeColorProvider(value);
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_Overlays.getTypeColorProvider();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return m_Overlays.typeColorProviderTipText();
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_Overlays.setTypeSuffix(value);
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_Overlays.getTypeSuffix();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return m_Overlays.typeSuffixTipText();
  }

  /**
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_Overlays.setTypeRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_Overlays.getTypeRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return m_Overlays.typeRegExpTipText();
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_Overlays.setLabelFormat(value);
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_Overlays.getLabelFormat();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
    return m_Overlays.labelFormatTipText();
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_Overlays.setLabelFont(value);
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_Overlays.getLabelFont();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return m_Overlays.labelFontTipText();
  }

  /**
   * Returns the default label anchor.
   *
   * @return		the anchor
   */
  protected ImageAnchor getDefaultLabelAnchor() {
    return ImageAnchor.TOP_RIGHT;
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setLabelAnchor(ImageAnchor value) {
    m_Overlays.setLabelAnchor(value);
    reset();
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getLabelAnchor() {
    return m_Overlays.getLabelAnchor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelAnchorTipText() {
    return m_Overlays.labelAnchorTipText();
  }

  /**
   * Returns the default label offset for X.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetX() {
    return 0;
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setLabelOffsetX(int value) {
    m_Overlays.setLabelOffsetX(value);
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_Overlays.getLabelOffsetX();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return m_Overlays.labelOffsetXTipText();
  }

  /**
   * Returns the default label offset for Y.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetY() {
    return 0;
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setLabelOffsetY(int value) {
    m_Overlays.setLabelOffsetY(value);
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_Overlays.getLabelOffsetY();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return m_Overlays.labelOffsetYTipText();
  }

  /**
   * Sets the predefined labels.
   *
   * @param value	the labels
   */
  public void setPredefinedLabels(BaseString[] value) {
    m_Overlays.setPredefinedLabels(value);
    reset();
  }

  /**
   * Returns the predefined labels.
   *
   * @return		the labels
   */
  public BaseString[] getPredefinedLabels() {
    return m_Overlays.getPredefinedLabels();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedLabelsTipText() {
    return m_Overlays.predefinedLabelsTipText();
  }

  /**
   * Sets whether to vary the colors of the shapes.
   *
   * @param value 	true if to vary
   */
  public void setVaryShapeColor(boolean value) {
    m_Overlays.setVaryShapeColor(value);
    reset();
  }

  /**
   * Returns whether to vary the colors of the shapes.
   *
   * @return 		true if to vary
   */
  public boolean getVaryShapeColor() {
    return m_Overlays.getVaryShapeColor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varyShapeColorTipText() {
    return m_Overlays.varyShapeColorTipText();
  }

  /**
   * Sets the color provider to use when varying the shape colors.
   *
   * @param value 	the provider
   */
  public void setShapeColorProvider(ColorProvider value) {
    m_Overlays.setShapeColorProvider(value);
    reset();
  }

  /**
   * Returns the color provider to use when varying the shape colors.
   *
   * @return 		the provider
   */
  public ColorProvider getShapeColorProvider() {
    return m_Overlays.getShapeColorProvider();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorProviderTipText() {
    return m_Overlays.shapeColorProviderTipText();
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
   * Checks whether a color has been stored for the given object type.
   *
   * @param type	the type to check
   * @return		true if custom color available
   */
  @Override
  public boolean hasTypeColor(String type) {
    return m_Overlays.hasTypeColor(type);
  }

  /**
   * Returns the color for the object type.
   *
   * @param type	the type to get the color for
   * @return		the color, null if none available
   */
  @Override
  public Color getTypeColor(String type) {
    return m_Overlays.getTypeColor(type);
  }

  /**
   * Adds the listener for location updates.
   *
   * @param l		the listener to add
   */
  public void addLocationsUpdatedListeners(ChangeListener l) {
    m_LocationsUpdatedListeners.add(l);
  }

  /**
   * Removes the listener for location updates.
   *
   * @param l		the listener to remove
   */
  public void removeLocationsUpdatedListeners(ChangeListener l) {
    m_LocationsUpdatedListeners.remove(l);
  }

  /**
   * Notifies all the listeners that the notifications have been updated.
   */
  protected void notifyLocationsUpdatedListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_LocationsUpdatedListeners)
      l.stateChanged(e);
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
    m_PanelObjects.setLocatedObjects(m_Overlays.getAllObjects());

    return m_PanelObjects;
  }

  /**
   * Gets called when the image overlay got added to a paintable panel.
   *
   * @param panel	the panel it got added to
   */
  @Override
  public void overlayAdded(PaintPanel panel) {
    super.overlayAdded(panel);

    m_Owner = panel;

    if (panel.getOwner() == null)
      return;

    panel.getOwner().setLeftDetachedFrameTitle("Objects");
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
  public void overlayRemoved(PaintPanel panel) {
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
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Overlays.reset();
  }

  /**
   * Performs the actual painting of the objects.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   * @param locations	the locations to paint
   */
  protected abstract void doPaintObjects(PaintPanel panel, Graphics g, List<Polygon> locations);

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    boolean			updated;
    LocatedObjectFilter		filter;
    JPanel			left;
    JPanel			objects;

    if (m_ShowObjectPanel) {
      left = panel.getOwner().getLeftPanel();
      objects = getLocatedObjectsPanel();
      if (left != objects)
	panel.getOwner().setLeftPanel(objects);
    }

    filter = null;
    if (m_ShowObjectPanel)
      filter = m_PanelObjects.getFilter();
    if (filter == null)
      filter = new AcceptAllLocatedObjectsFilter();

    updated = m_Overlays.determineLocations(panel.getOwner().getAdditionalProperties(), filter);
    if (m_Overlays.hasLocations())
      doPaintObjects(panel, g, m_Overlays.getLocations());
    if (updated) {
      notifyLocationsUpdatedListeners();
      if (m_ShowObjectPanel)
	m_PanelObjects.setLocatedObjects(m_Overlays.getAllObjects());
    }
  }

  /**
   * For customizing the popup menu.
   *
   * @param source	the source, e.g., event
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(PaintPanel source, JPopupMenu menu) {
    JMenuItem		menuitem;

    if (!getTypeSuffix().isEmpty()) {
      menuitem = new JMenuItem("Displayed types", ImageManager.getIcon("objecttypes.gif"));
      menuitem.addActionListener((ActionEvent e) -> {
	String type = GUIHelper.showInputDialog(source, "Regular expression for type", getTypeRegExp().getValue());
	if (type == null)
	  return;
	if (!getTypeRegExp().isValid(type)) {
	  GUIHelper.showErrorMessage(source, "Invalid regular expression: " + type);
	  return;
	}
	setTypeRegExp(new BaseRegExp(type));
	source.update();
      });
      menu.add(menuitem);
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_LocationsUpdatedListeners != null)
      m_LocationsUpdatedListeners.clear();
    super.cleanUp();
  }
}
