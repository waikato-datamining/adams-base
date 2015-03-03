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
 *    GPSEditor.java
 *    Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.data.gps.AbstractGPS;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.dialog.ApprovalDialog;

/**
 * A PropertyEditor for GPS coordinates.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, 
             InlineEditorSupport {

  /** The text field with the coordinates. */
  protected JTextComponent m_TextCoordinates;

  /**
   * Turns the string into an instance of the specified GPS class.
   * 
   * @param cls		the GPS class to instantiate
   * @param str		the string to parse
   * @return		the instantiated GPS object
   */
  protected static AbstractGPS valueOf(Class cls, String str) {
    AbstractGPS	result;
    Constructor	constr;
    
    try {
      constr = cls.getConstructor(new Class[]{String.class});
      result = (AbstractGPS) constr.newInstance(new Object[]{str});
    }
    catch (Exception e) {
      try {
	result = (AbstractGPS) cls.newInstance();
      }
      catch (Exception ex) {
	System.err.println("Failed to instantiate " + cls.getName() + " as " + AbstractGPS.class.getName() + "!");
	ex.printStackTrace();
	result = null;
      }
    }
    
    return result;
  }
  
  /**
   * Returns the GPS coordinates as string.
   *
   * @param option	the current option
   * @param object	the GPS coordinates object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return object.toString();
  }

  /**
   * Returns GPS coordinates generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to GPS coordinates
   * @return		the generated GPS coordinates
   */
  public static Object valueOf(AbstractOption option, String str) {
    return valueOf(((AbstractArgumentOption) option).getBaseClass(), str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return obj.toString();
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(getValue().getClass(), str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		always "null"
   */
  @Override
  public String getJavaInitializationString() {
    return "new " + getValue().getClass().getName() + "(\"" + getValue().toString() + "\")";
  }

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	s;

    s = m_TextCoordinates.getText();
    if (isValid(s) && !isUnchanged(s))
      setValue(valueOf(getValue().getClass(), s));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Discards the input and closes the dialog.
   */
  protected void discardInput() {
    closeDialog(CANCEL_OPTION);
  }

  /**
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  protected boolean isValid(String s) {
    return ((AbstractGPS) getValue()).isValid(s);
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  protected boolean isUnchanged(String s) {
    return s.equals(getValue().toString());
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel		panel;
    JLabel		label;
    JPanel 		panelButtons;
    JButton 		buttonOK;
    JButton 		buttonClose;

    m_TextCoordinates = new BaseTextArea(1, 20);
    ((BaseTextArea) m_TextCoordinates).setLineWrap(true);
    m_TextCoordinates.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  acceptInput();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  discardInput();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });

    panelAll = new JPanel(new BorderLayout());

    label = new JLabel("Coordinates");
    label.setDisplayedMnemonic('C');
    label.setLabelFor(m_TextCoordinates);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.WEST);
    
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    panel.add(new BaseScrollPane(m_TextCoordinates), BorderLayout.CENTER);
    panelAll.add(panel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	discardInput();
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!m_TextCoordinates.getText().equals("" + getValue()))
      m_TextCoordinates.setText("" + getValue());
    m_TextCoordinates.setCaretPosition(m_TextCoordinates.getText().length());
    m_TextCoordinates.grabFocus();
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    FontMetrics fm = gfx.getFontMetrics();
    int vpad = (box.height - fm.getHeight()) / 2 ;
    AbstractGPS gps = (AbstractGPS) getValue();
    String val = "No coordinates";
    if (gps != null)
      val = gps.toString();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
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
    Vector<String>		lines;
    Class			cls;
    int				i;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the coordinates, one pair per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    cls = getValue().getClass();
    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = (Object[]) Array.newInstance(cls, lines.size());
      for (i = 0; i < lines.size(); i++)
	Array.set(result, i, valueOf(cls, lines.get(i)));
    }
    else {
      result = (Object[]) Array.newInstance(cls, 0);
    }

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
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    setValue(valueOf(getValue().getClass(), value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return getValue().toString();
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
