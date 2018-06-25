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
 * GenericObjectEditorClassTreePopupMenu.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe;

import adams.gui.core.BasePopupMenu;
import adams.gui.goe.classtree.ClassTree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

/**
 * Creates a popup menu containing a tree that is aware
 * of the screen dimensions.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Xin Xu (xx5@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorClassTreePopupMenu
  extends BasePopupMenu {

  /** for serialization. */
  static final long serialVersionUID = -3404546329655057387L;

  /** the tree panel. */
  protected GenericObjectEditorClassTreePanel m_PanelTree;

  /**
   * Constructs a new popup menu.
   *
   * @param tree 	the tree to put in the menu
   */
  public GenericObjectEditorClassTreePopupMenu(ClassTree tree) {
    super();

    setLayout(new BorderLayout());

    m_PanelTree = new GenericObjectEditorClassTreePanel(tree);
    m_PanelTree.setCloseButtonVisible(true);
    add(m_PanelTree, BorderLayout.CENTER);
  }

  /**
   * Sets the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @param value	the minimum number of characters (>= 1)
   */
  public void setMinimumChars(int value) {
    m_PanelTree.setMinimumChars(value);
  }

  /**
   * Returns the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @return		the minimum number of characters (>= 1)
   */
  public int getMinimumChars() {
    return m_PanelTree.getMinimumChars();
  }

  /**
   * Sets the info text to display at the top.
   * Use "_" before the character to use as the mnemonic for jumping into the
   * tree via the keyboard.
   *
   * @param value	the info text, null or empty to remove
   */
  public void setInfoText(String value) {
    m_PanelTree.setInfoText(value);
  }

  /**
   * Returns the current info text, if any.
   *
   * @return		the text, empty if none displayed
   */
  public String getInfoText() {
    return m_PanelTree.getInfoText();
  }

  /**
   * Displays the menu, making sure it will fit on the screen.
   *
   * @param invoker 	the component thast invoked the menu
   * @param x 	the x location of the popup
   * @param y 	the y location of the popup
   */
  @Override
  public void show(Component invoker, int x, int y) {
    super.show(invoker, x, y);

    // calculate available screen area for popup
    Point location = getLocationOnScreen();
    Dimension screenSize = getToolkit().getScreenSize();
    int maxWidth = (int) (screenSize.getWidth() - location.getX());
    int maxHeight = (int) (screenSize.getHeight() - location.getY());

    // if the part of the popup goes off the screen then resize it
    Dimension scrollerSize = m_PanelTree.getScrollPane().getPreferredSize();
    int height = (int) scrollerSize.getHeight();
    int width = (int) scrollerSize.getWidth();
    if (width > maxWidth) width = maxWidth;
    if (height > maxHeight) height = maxHeight;

    // commit any size changes
    m_PanelTree.getScrollPane().setPreferredSize(new Dimension(width, height));
    revalidate();
    pack();

    // request focus
    m_PanelTree.focusSearch();
  }
}
