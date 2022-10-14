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
 * BaseFlatButton.java
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom class for flat buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseFlatButton
  extends BaseButton {

  private static final long serialVersionUID = 443538647764642995L;

  /** the inactive border. */
  protected Border m_BorderInactive;

  /** the active border. */
  protected Border m_BorderActive;

  /**
   * Creates a button with no set text or icon.
   */
  public BaseFlatButton() {
    super();
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon the Icon image to display on the button
   */
  public BaseFlatButton(Icon icon) {
    super(icon);
  }

  /**
   * Creates a button with text.
   *
   * @param text the text of the button
   */
  public BaseFlatButton(String text) {
    super(text);
  }

  /**
   * Creates a button where properties are taken from the
   * <code>Action</code> supplied.
   *
   * @param a the <code>Action</code> used to specify the new button
   * @since 1.3
   */
  public BaseFlatButton(Action a) {
    super();
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text the text of the button
   * @param icon the Icon image to display on the button
   */
  public BaseFlatButton(String text, Icon icon) {
    super(text, icon);
  }

  /**
   * Initializes members.
   */
  protected void initButton() {
    super.initButton();

    m_BorderActive   = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker()), BorderFactory.createEmptyBorder(2, 2, 2, 2));
    m_BorderInactive = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(2, 2, 2, 2));

    setBorderPainted(true);
    setFocusPainted(true);
    setFocusable(true);
    setContentAreaFilled(false);
    setRolloverEnabled(true);
    setBorder(m_BorderInactive);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        setBorder(m_BorderActive);
      }
      @Override
      public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        setBorder(m_BorderInactive);
      }
    });
  }
}
