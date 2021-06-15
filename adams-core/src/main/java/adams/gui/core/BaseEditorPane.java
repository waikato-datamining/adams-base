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
 * BaseEditorPane.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * Extends the {@link JEditorPane} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseEditorPane
  extends JEditorPane {

  private static final long serialVersionUID = -468246726891443535L;

  /**
   * Default constructor.
   */
  public BaseEditorPane() {
    super();
    initialize();
  }

  /**
   * Initializes the pane and loads the specified page.
   *
   * @param url		the page to load
   * @throws IOException	if loading the page fails
   */
  public BaseEditorPane(URL url) throws IOException {
    this();
    setPage(url);
  }

  /**
   * Initializes the pane and loads the specified page.
   *
   * @param url		the page to load
   * @throws IOException	if loading the page fails
   */
  public BaseEditorPane(String url) throws IOException {
    this();
    setPage(url);
  }

  /**
   * Initializes the pane with the specified mimetype and text.
   *
   * @param type	the mimetype
   * @param text 	the text to display
   */
  public BaseEditorPane(String type, String text) {
    this();
    setContentType(type);
    setText(text);
  }

  /**
   * Initializes the widget.
   */
  protected void initialize() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isRightClick(e)) {
          BasePopupMenu menu = createRightClickMenu();
          menu.show(BaseEditorPane.this, e.getX(), e.getY());
        }
        else {
          super.mouseClicked(e);
        }
      }
    });
  }

  /**
   * Creates a right-click popup menu.
   *
   * @return		the menu
   */
  protected BasePopupMenu createRightClickMenu() {
    BasePopupMenu	result;
    JMenuItem		menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.addActionListener((ActionEvent e) -> copy());
    menuitem.setEnabled(getSelectedText() != null);
    result.add(menuitem);

    menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent e) -> selectAll());
    result.add(menuitem);

    return result;
  }
}
