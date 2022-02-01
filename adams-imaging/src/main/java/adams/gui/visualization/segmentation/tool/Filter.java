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
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.data.imagesegmentation.filter.AbstractImageSegmentationContainerFilter;
import adams.data.imagesegmentation.filter.PassThrough;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.segmentation.layer.AbstractLayer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Filter.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Filter
    extends AbstractTool {

  private static final long serialVersionUID = -1508997962532101115L;

  /** the filter to apply. */
  protected GenericObjectEditorPanel m_PanelFilter;

  /** the apply button. */
  protected BaseFlatButton m_ButtonApply;

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
    return GUIHelper.getIcon("filter.png");
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
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel		panel;
    JPanel		panel2;

    result = new BasePanel();
    result.setBorder(BorderFactory.createTitledBorder(getName()));

    m_ButtonApply = createApplyButton();

    panel = new JPanel(new GridLayout(0, 1));
    result.add(panel, BorderLayout.NORTH);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(Fonts.usePlain(new JLabel("Filter")));
    m_PanelFilter = new GenericObjectEditorPanel(AbstractImageSegmentationContainerFilter.class, m_Filter, true);
    m_PanelFilter.setTextColumns(10);
    m_PanelFilter.setToolTipText("The filter to apply");
    panel2.add(m_PanelFilter);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(m_ButtonApply);

    return result;
  }
}
