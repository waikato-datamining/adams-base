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
 * BaseTextFieldWithButtons.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.event.AnyChangeListenerSupporter;

import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * {@link BaseTextFieldWithButtons} with additional support for buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseTextFieldWithButtons
  extends BasePanel
  implements AnyChangeListenerSupporter, BaseTextComponent {

  private static final long serialVersionUID = -8562372761976614736L;

  /** the underlying text field. */
  protected BaseTextField m_Component;

  /** the layout for the buttons. */
  protected GridLayout m_LayoutButtons;

  /** the panel with the buttons (on the right). */
  protected BasePanel m_PanelButtons;

  /**
   * The default constructor.
   */
  public BaseTextFieldWithButtons() {
    super();
  }

  /**
   * Initializes the list with the given text.
   *
   * @param text	the text to use
   */
  public BaseTextFieldWithButtons(String text) {
    super();
    setText(text);
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   *
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public BaseTextFieldWithButtons(int columns) {
    super();
    setColumns(columns);
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   *
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public BaseTextFieldWithButtons(String text, int columns) {
    super();
    setText(text);
    setColumns(columns);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Component = new BaseTextField();
    add(m_Component, BorderLayout.CENTER);

    m_LayoutButtons = new GridLayout(1, 0, 1, 0);
    m_PanelButtons  = new BasePanel(m_LayoutButtons);
    add(m_PanelButtons, BorderLayout.EAST);
  }

  /**
   * Adds the component to the panel with the buttons.
   *
   * @param comp	the component to add
   */
  public void addToButtonsPanel(Component comp) {
    removeFromButtonsPanel(comp);
    m_LayoutButtons.setColumns(m_LayoutButtons.getColumns() + 1);
    m_PanelButtons.add(comp);
  }

  /**
   * Removes the component from the panel with the buttons.
   *
   * @param comp	the component to remove
   */
  public void removeFromButtonsPanel(Component comp) {
    m_LayoutButtons.setColumns(m_LayoutButtons.getColumns() - 1);
    m_PanelButtons.remove(comp);
  }

  /**
   * Returns the underlying text field.
   *
   * @return		the text field
   */
  public BaseTextField getComponent() {
    return m_Component;
  }

  /**
   * Returns the underlying document.
   *
   * @return		the document
   */
  public Document getDocument() {
    return m_Component.getDocument();
  }

  /**
   * Sets the text.
   *
   * @param value	the text to display
   */
  public void setText(String value) {
    m_Component.setText(value);
  }

  /**
   * Returns the underlying text.
   *
   * @return		the underlying text
   */
  public String getText() {
    return m_Component.getText();
  }

  /**
   * Returns the underlying text.
   *
   * @return		the underlying text
   */
  public String getSelectedText() {
    return m_Component.getSelectedText();
  }

  /**
   * Sets the number of columns in this <code>TextField</code>,
   * and then invalidate the layout.
   *
   * @param value the number of columns &gt;= 0
   * @throws IllegalArgumentException if <code>columns</code> is less than 0
   */
  public void setColumns(int value) {
    m_Component.setColumns(value);
  }

  /**
   * Returns the number of columns in this <code>TextField</code>.
   *
   * @return the number of columns &gt;= 0
   */
  public int getColumns() {
    return m_Component.getColumns();
  }

  /**
   * Sets whether the text area is editable or not.
   *
   * @param value	if true the text area is editable
   */
  public void setEditable(boolean value) {
    m_Component.setEditable(value);
  }

  /**
   * Returns whether the text area is editable or not.
   *
   * @return		true if the text area is editable
   */
  public boolean isEditable() {
    return m_Component.isEditable();
  }

  /**
   * Sets the text font.
   *
   * @param value	the font
   */
  public void setTextFont(Font value) {
    m_Component.setFont(value);
  }

  /**
   * Returns the text font in use.
   *
   * @return		the font
   */
  public Font getTextFont() {
    return m_Component.getFont();
  }

  /**
   * Sets the caret position.
   *
   * @param pos 	the position (0-based)
   */
  public void setCaretPosition(int pos) {
    m_Component.setCaretPosition(pos);
  }

  /**
   * Returns the current caret position.
   *
   * @return		the position (0-based)
   */
  public int getCaretPosition() {
    return m_Component.getCaretPosition();
  }

  /**
   * Adds the listener for listening to any text changes.
   *
   * @param l		the listener to add
   */
  @Override
  public void addAnyChangeListener(ChangeListener l) {
    getComponent().addAnyChangeListener(l);
  }

  /**
   * Removes the listener from listening to any text changes.
   *
   * @param l		the listener to remove
   */
  @Override
  public void removeAnyChangeListener(ChangeListener l) {
    getComponent().removeAnyChangeListener(l);
  }
}
