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
 * BaseColorTextField.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseColor;
import adams.event.AnyChangeListenerSupporter;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

/**
 * Text field designed for entering a regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseColorTextField
  extends BasePanel
  implements AnyChangeListenerSupporter {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * Custom color text field.
   */
  public static class CustomColorTextField
    extends BaseObjectTextField<BaseColor> {

    private static final long serialVersionUID = 2620268109385119824L;

    /**
     * Constructs a new <code>TextField</code>.
     */
    public CustomColorTextField() {
      this(Color.BLACK);
    }

    /**
     * Constructs a new <code>TextField</code>.
     *
     * @param initial	the initial color
     */
    public CustomColorTextField(Color initial) {
      super(new BaseColor(initial), ColorHelper.toHex(initial));
    }

    /**
     * Sets the color.
     *
     * @param value	the color
     */
    public void setColor(Color value) {
      setObject(new BaseColor(value));
    }

    /**
     * Returns the current color.
     *
     * @return		the color
     */
    public Color getColor() {
      return getObject().toColorValue();
    }

    /**
     * Pops up dialog to select color.
     */
    public void chooseColor() {
      Color   newColor;

      newColor = JColorChooser.showDialog(getParent(), "Select color", getColor());
      if (newColor != null)
	setColor(newColor);
    }

    /**
     * Returns a popup menu when right-clicking on the edit field.
     *
     * @return		the menu, null if non available
     */
    protected BasePopupMenu getPopupMenu() {
      BasePopupMenu result;
      JMenuItem menuitem;

      result = super.getPopupMenu();

      menuitem = new JMenuItem("Choose...", ImageManager.getIcon("colorpicker.png"));
      menuitem.addActionListener((ActionEvent e) -> chooseColor());
      result.addSeparator();
      result.add(menuitem);

      return result;
    }
  }

  /** the actual text field. */
  protected CustomColorTextField m_TextField;

  /** the button for displaying the color and popping up the color picker dialog. */
  protected BaseFlatButton m_Button;

  /**
   * Constructs a new <code>TextField</code>.
   */
  public BaseColorTextField() {
    this(Color.BLACK);
  }

  /**
   * Constructs a new <code>TextField</code>.
   *
   * @param initial	the initial color
   */
  public BaseColorTextField(Color initial) {
    super();
    setColor(initial);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextField = new CustomColorTextField();
    m_TextField.addAnyChangeListener((ChangeEvent e) -> updateButtonColor(m_TextField.getColor()));
    add(m_TextField, BorderLayout.CENTER);

    m_Button = new BaseFlatButton();
    m_Button.setOpaque(true);
    m_Button.addActionListener((ActionEvent e) -> m_TextField.chooseColor());
    m_Button.setToolTipText("Open color picker dialog");
    m_Button.setPreferredSize(new Dimension((int) m_TextField.getPreferredSize().getHeight(), (int) m_TextField.getPreferredSize().getHeight()));
    add(m_Button, BorderLayout.EAST);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_TextField.setColor(Color.BLACK);
  }

  /**
   * Updates the color of the button.
   *
   * @param color	the color to use
   */
  protected void updateButtonColor(Color color) {
    setBackground(color);
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
    m_TextField.setColumns(columns);
  }

  /**
   * Returns the number of columns in this <code>TextField</code>.
   *
   * @return the number of columns &gt;= 0
   */
  public int getColumns() {
    return m_TextField.getColumns();
  }

  /**
   * Adds the listener for listening to any text changes.
   *
   * @param l		the listener to add
   */
  @Override
  public void addAnyChangeListener(ChangeListener l) {
    m_TextField.addAnyChangeListener(l);
  }

  /**
   * Removes the listener from listening to any text changes.
   *
   * @param l		the listener to remove
   */
  @Override
  public void removeAnyChangeListener(ChangeListener l) {
    m_TextField.removeAnyChangeListener(l);
  }

  /**
   * Sets the text in the field.
   *
   * @param value	the text
   */
  public void setText(String value) {
    m_TextField.setText(value);
  }

  /**
   * Returns the text in the field.
   *
   * @return		the text
   */
  public String getText() {
    return m_TextField.getText();
  }

  /**
   * Sets the object.
   *
   * @param value	the object
   */
  public void setObject(BaseColor value) {
    m_TextField.setObject(value);
  }

  /**
   * Returns the current color.
   *
   * @return		the color
   */
  public BaseColor getObject() {
    return m_TextField.getObject();
  }

  /**
   * Sets the color.
   *
   * @param value	the object
   */
  public void setColor(Color value) {
    m_TextField.setColor(value);
  }

  /**
   * Returns the current color.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_TextField.getObject().toColorValue();
  }
}
