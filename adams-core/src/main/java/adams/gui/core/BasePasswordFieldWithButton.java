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
 * BasePasswordFieldWithButton.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.base.BasePassword;

import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * A {@link BasePasswordField} with a button to show/hide the password.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BasePasswordFieldWithButton
  extends BasePanel {

  /** the password field. */
  protected BasePasswordField m_Field;

  /** the button for showing/hiding the password. */
  protected BaseButton m_Button;

  /** the echo character. */
  protected char m_EchoChar;

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   */
  public BasePasswordFieldWithButton() {
    super();
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   *
   * @param text the text to be displayed, or <code>null</code>
   */
  public BasePasswordFieldWithButton(String text) {
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
  public BasePasswordFieldWithButton(int columns) {
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
  public BasePasswordFieldWithButton(String text, int columns) {
    super();
    setText(text);
    setColumns(columns);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout(0, 0));

    m_Field = new BasePasswordField();
    m_Field.addEchoCharChangeListener((ChangeEvent e) -> {
      if (m_Field.getEchoChar() == m_EchoChar)
	m_Button.setIcon(ImageManager.getIcon("show"));
      else
	m_Button.setIcon(ImageManager.getIcon("hide"));
    });
    m_EchoChar = m_Field.getEchoChar();
    add(m_Field, BorderLayout.CENTER);

    m_Button = new BaseButton(ImageManager.getIcon("show"));
    m_Button.addActionListener((ActionEvent e) -> {
      if (m_Field.getEchoChar() == m_EchoChar)
	m_Field.setEchoChar((char) 0);
      else
	m_Field.setEchoChar(m_EchoChar);
    });
    add(m_Button, BorderLayout.EAST);
  }

  /**
   * Returns the text contained in this <code>TextComponent</code>.
   * If the underlying document is <code>null</code>, will give a
   * <code>NullPointerException</code>.  For stronger
   * security, it is recommended that the returned character array be
   * cleared after use by setting each character to zero.
   *
   * @return the text
   */
  public char[] getPassword() {
    return m_Field.getPassword();
  }

  /**
   * Sets the password.
   *
   * @param t		the password
   */
  public void setText(String t) {
    m_Field.setText(t);
  }

  /**
   * Returns the character to be used for echoing.  The default is '*'.
   * The default may be different depending on the currently running Look
   * and Feel. For example, Metal/Ocean's default is a bullet character.
   *
   * @return the echo character, 0 if unset
   */
  public char getEchoChar() {
    return m_Field.getEchoChar();
  }

  /**
   * Sets the echo character for this <code>JPasswordField</code>.
   * Note that this is largely a suggestion, since the
   * view that gets installed can use whatever graphic techniques
   * it desires to represent the field.  Setting a value of 0 indicates
   * that you wish to see the text as it is typed, similar to
   * the behavior of a standard <code>JTextField</code>.
   *
   * @param c the echo character to display
   */
  public void setEchoChar(char c) {
    m_Field.setEchoChar(c);
  }

  /**
   * Returns true if this <code>JPasswordField</code> has a character
   * set for echoing.  A character is considered to be set if the echo
   * character is not 0.
   *
   * @return true if a character is set for echoing
   */
  public boolean echoCharIsSet() {
    return m_Field.echoCharIsSet();
  }

  /**
   * Returns the number of columns in this <code>TextField</code>.
   *
   * @return the number of columns &gt;= 0
   */
  public int getColumns() {
    return m_Field.getColumns();
  }

  /**
   * Sets the number of columns in this <code>TextField</code>,
   * and then invalidate the layout.
   *
   * @param columns the number of columns &gt;= 0
   * @exception IllegalArgumentException if <code>columns</code>
   *          is less than 0
   */
  public void setColumns(int columns) {
    m_Field.setColumns(columns);
  }

  /**
   * Sets whether to show the popup menu.
   *
   * @param value	true if to show
   */
  public void setShowPopupMenu(boolean value) {
    m_Field.setShowPopupMenu(value);
  }

  /**
   * Returns whether the popup menu is shown.
   *
   * @return		true if shown
   */
  public boolean getShowPopupMenu() {
    return m_Field.getShowPopupMenu();
  }

  /**
   * Sets the password.
   *
   * @param value	the password
   */
  public void setBasePassword(BasePassword value) {
    m_Field.setBasePassword(value);
  }

  /**
   * Return the password.
   *
   * @return		the password
   */
  public BasePassword getBasePassword() {
    return m_Field.getBasePassword();
  }
}
