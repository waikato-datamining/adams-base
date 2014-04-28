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
 *    PdfFontEditor.java
 *    Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.core.io.PdfFont;
import adams.core.option.AbstractOption;
import adams.gui.chooser.PdfFontChooserPanel;

/**
 * A PropertyEditor for iText Font objects that lets the user select a font from
 * the font dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PdfFontEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler {

  /** The Font chooser used for selecting colors. */
  protected PdfFontChooserPanel m_FontChooserPanel;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the close button. */
  protected JButton m_ButtonClose;

  /**
   * Returns the color as string.
   *
   * @param option	the current option
   * @param object	the color object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((PdfFont) object).toString();
  }

  /**
   * Returns a color generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a color
   * @return		the generated color
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new PdfFont(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		always "null"
   */
  public String getJavaInitializationString() {
    return "null";
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  protected JComponent createCustomEditor() {
    JPanel 	panel;
    PdfFont 	currentFont;

    currentFont  = (PdfFont) getValue();
    m_FontChooserPanel = new PdfFontChooserPanel();
    m_FontChooserPanel.setCurrent(currentFont);

    panel = new JPanel(new BorderLayout());
    panel.add(m_FontChooserPanel, BorderLayout.CENTER);

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setValue(m_FontChooserPanel.getCurrent());
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonClose = new JButton("Cancel");
    m_ButtonClose.setMnemonic('C');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// select current one value again, to make sure that it is displayed
	// when the dialog is popped up again. otherwise the last selection
	// (but not ok-ed!) will be displayed.
	m_FontChooserPanel.setCurrent((PdfFont) getValue());
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtons.add(m_ButtonClose);

    return panel;
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    PdfFont 	currentFont;

    super.initForDisplay();

    currentFont = (PdfFont) getValue();
    if (currentFont != null)
      m_FontChooserPanel.setCurrent(currentFont);
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    FontMetrics fm = gfx.getFontMetrics();
    int vpad = (box.height - fm.getHeight()) / 2 ;
    PdfFont font = (PdfFont) getValue();
    String val = "No font";
    if (font != null)
      val = font.toString();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }
}

