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
 * Filter.java
 * Copyright (C) 2022-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.data.imagesegmentation.filter.AbstractImageSegmentationContainerFilter;
import adams.data.imagesegmentation.filter.PassThrough;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.segmentation.layer.AbstractLayer;

import javax.swing.Icon;
import java.awt.Cursor;
import java.util.List;

/**
 * For applying filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Filter
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = -1508997962532101115L;

  /** the filter to apply. */
  protected GenericObjectEditorPanel m_PanelFilter;

  /** the current filter. */
  protected AbstractImageSegmentationContainerFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For applying filters.";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_Filter = new PassThrough();
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Filter";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("filter.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursor.getDefaultCursor();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    return null;
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
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    ImageSegmentationContainer  		cont;
    List<AbstractLayer.AbstractLayerState>  	settings;

    m_Filter = (AbstractImageSegmentationContainerFilter) m_PanelFilter.getCurrent();
    cont     = m_PanelCanvas.getOwner().toContainer();
    cont     = m_Filter.filter(cont);
    settings = m_PanelCanvas.getOwner().getManager().getSettings();
    m_PanelCanvas.getOwner().fromContainer(cont, m_PanelCanvas.getOwner().getContainerSettings(), settings, null);
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_PanelFilter = new GenericObjectEditorPanel(AbstractImageSegmentationContainerFilter.class, m_Filter, true);
    m_PanelFilter.setTextColumns(10);
    m_PanelFilter.setToolTipText("The filter to apply");
    paramPanel.addParameter("Filter", m_PanelFilter);
  }
}
