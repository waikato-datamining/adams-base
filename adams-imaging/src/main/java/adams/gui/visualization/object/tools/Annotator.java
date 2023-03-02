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
 * Annotator.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.object.annotator.AbstractAnnotator;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import java.awt.Cursor;

/**
 * For switching between types of annotator tools.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Annotator
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = -3238804649373495561L;

  /** the GOE for selecting the annotator. */
  protected GenericObjectEditorPanel m_GOEAnnotator;

  /** the annotator to use. */
  protected AbstractAnnotator m_Annotator;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For switching between types of annotator tools.";
  }

  /**
   * The name of the tool.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "Annotator";
  }

  /**
   * The icon of the tool.
   *
   * @return the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon( "locateobjects.gif");
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
    m_Annotator = (AbstractAnnotator) m_GOEAnnotator.getCurrent();
    getCanvas().getOwner().setAnnotator(m_Annotator);
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_GOEAnnotator = new GenericObjectEditorPanel(AbstractAnnotator.class, getCanvas().getOwner().getAnnotator(), true);
    m_GOEAnnotator.addChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Annotator", m_GOEAnnotator);
  }

  /**
   * Called when image or annotations change.
   */
  @Override
  public void update() {
    super.update();
    if ((m_GOEAnnotator != null) && !isModified())
      m_GOEAnnotator.setCurrent(getCanvas().getOwner().getAnnotator());
  }
}
