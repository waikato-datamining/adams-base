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
 *    BaseColorEditor.java
 *    Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.base.BaseColor;
import adams.core.option.parsing.BaseColorParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.List;

/**
 * A PropertyEditor for {@link BaseColor} objects that lets the user select a color from
 * the color dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseColorEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, 
             InlineEditorSupport {

  /** The color chooser used for selecting colors. */
  protected JColorChooser m_ColorChooser;

  /** The alpha value. */
  protected JSpinner m_SpinnerAlpha;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return BaseColorParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return BaseColorParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		always "null"
   */
  @Override
  public String getJavaInitializationString() {
    return "null";
  }

  /**
   * Returns the current color.
   * 
   * @return		the color
   */
  protected Color getColor() {
    Color 	result;
    Color 	current;
    int 	alpha;
    
    current = m_ColorChooser.getColor();
    alpha   = (Integer) m_SpinnerAlpha.getValue();
    result  = new Color(current.getRed(), current.getGreen(), current.getBlue(), alpha);
    
    return result;
  }
  
  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    BasePanel	result;
    JPanel	panelColor;
    JPanel	panelAlpha;
    JPanel	panelButtons;
    JLabel	labelAlpha;
    BaseButton	buttonOK;
    BaseButton	buttonCancel;
    BaseColor 	currentColor;

    currentColor = (BaseColor) getValue();

    result = new BasePanel(new BorderLayout());

    panelColor = new JPanel(new BorderLayout());
    result.add(panelColor, BorderLayout.CENTER);

    // chooser
    m_ColorChooser = new JColorChooser();
    m_ColorChooser.setColor(currentColor.toColorValue());
    panelColor.add(m_ColorChooser, BorderLayout.CENTER);

    // alpha
    panelAlpha = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelColor.add(panelAlpha, BorderLayout.SOUTH);
    m_SpinnerAlpha = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerAlpha.getModel()).setMinimum(0);
    ((SpinnerNumberModel) m_SpinnerAlpha.getModel()).setMaximum(255);
    ((SpinnerNumberModel) m_SpinnerAlpha.getModel()).setValue(currentColor.toColorValue().getAlpha());
    m_SpinnerAlpha.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	Color newColor = getColor();
	m_ColorChooser.setColor(newColor);
      }
    });
    labelAlpha = new JLabel("Alpha");
    labelAlpha.setDisplayedMnemonic('A');
    labelAlpha.setLabelFor(m_SpinnerAlpha);
    panelAlpha.add(labelAlpha);
    panelAlpha.add(m_SpinnerAlpha);
    
    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    result.add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setValue(new BaseColor(getColor()));
	closeDialog(APPROVE_OPTION);
      }
    });
    panelButtons.add(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panelButtons.add(buttonCancel);

    return result;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    BaseColor 	currentColor;

    super.initForDisplay();
    
    currentColor = (BaseColor) getValue();
    if (currentColor != null) {
      m_ColorChooser.setColor(currentColor.toColorValue());
      m_SpinnerAlpha.setValue(currentColor.toColorValue().getAlpha());
    }
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    int[] offset;
    BaseColor color = (BaseColor) getValue();
    String val = "No color";
    if (color != null)
      val = BaseColorParsing.toString(null, color);
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Object[]			result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the colors (#RRGGBB; #AARRGGBB; R,G,B; A,R,G,B), one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = (Object[]) Array.newInstance(BaseColor.class, lines.size());
    for (i = 0; i < lines.size(); i++)
      Array.set(result, i, new BaseColor(lines.get(i)));

    return result;
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  protected boolean isValid(String s) {
    return (ColorHelper.valueOf(s, null) != null);
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isValid(value))
      setValue(new BaseColor(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((BaseColor) getValue()).getValue();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return isValid(value);
  }
}
