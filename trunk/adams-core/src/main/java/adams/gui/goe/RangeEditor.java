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
 * RangeEditor.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adams.core.Range;
import adams.core.Utils;
import adams.core.option.AbstractOption;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;

/**
 * A PropertyEditor for Range objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RangeEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, 
             InlineEditorSupport {

  /** The text field with the value. */
  protected JTextField m_TextValue;

  /** the panel with the text value (border indicates validity). */
  protected JPanel m_PanelValue;

  /** the help button for bringing up a dialog with the example text. */
  protected JButton m_ButtonHelp;

  /** the default background color of the panel. */
  protected Color m_DefaultBackground;

  /**
   * Returns the Compound as string.
   *
   * @param option	the current option
   * @param object	the Compound object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((Range) object).getRange();
  }
  
  /**
   * Returns a Compound generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a Compound
   * @return		the generated Compound
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new Range(str);
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
    return new Range(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    return "new " + getValue().getClass().getName() + "(\"" + getValue() + "\")";
  }

  /**
   * Returns the string to paint.
   *
   * @return		the string
   * @see		#paintValue(Graphics, Rectangle)
   */
  protected String getStringToPaint() {
    return "" + getValue();
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   * @see		#getStringToPaint()
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    FontMetrics 	fm;
    int 		vpad;
    String 		val;

    fm   = gfx.getFontMetrics();
    vpad = (box.height - fm.getHeight()) / 2;
    val  = getStringToPaint();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }

  /**
   * Parses the given string and returns the generated object. The string
   * has to be a valid one, i.e., the isValid(String) check has been
   * performed already and succeeded.
   *
   * @param s		the string to parse
   * @return		the generated object, or null in case of an error
   */
  protected Range parse(String s) {
    Range	result;

    result = null;
    
    if (s.length() == 0) {
      result = new Range();
    }
    else {
      try {
	if (Range.isValid(s, Integer.MAX_VALUE))
	  result = new Range(s);
      }
      catch (Exception e) {
	e.printStackTrace();
	result = null;
      }
    }

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		always null
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel	panelAll;
    JLabel	label;
    JPanel 	panelButtons;
    JButton 	buttonOK;
    JButton 	buttonClose;

    panelAll            = new JPanel(new BorderLayout());
    m_PanelValue        = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_DefaultBackground = m_PanelValue.getBackground();
    m_PanelValue.setBorder(BorderFactory.createLineBorder(m_DefaultBackground));
    panelAll.add(m_PanelValue, BorderLayout.CENTER);
    m_TextValue = new JTextField(20);
    m_TextValue.addKeyListener(new KeyAdapter() {
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
    m_TextValue.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void insertUpdate(DocumentEvent e) {
	indicateValidity();
      }
      public void changedUpdate(DocumentEvent e) {
	indicateValidity();
      }
    });

    m_ButtonHelp = new JButton();
    m_ButtonHelp.setVisible(false);
    m_ButtonHelp.setToolTipText(getHelpDescription());
    m_ButtonHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	TextDialog dlg = new TextDialog();
	dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	dlg.setDialogTitle("Help");
	dlg.setContent(getHelpDescription());
	dlg.setSize(400, 400);
	dlg.setLocationRelativeTo(m_ButtonHelp);
	dlg.setLineWrap(true);
	dlg.setEditable(false);
	dlg.setVisible(true);
      }
    });
    
    label = new JLabel("Range");
    label.setDisplayedMnemonic('R');
    label.setLabelFor(m_TextValue);
    m_PanelValue.add(label);
    m_PanelValue.add(m_TextValue);
    m_PanelValue.add(m_ButtonHelp);

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
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  protected boolean isValid(String s) {
    return (parse(s) != null);
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  protected boolean isUnchanged(String s) {
    return s.equals(((Range) getValue()).getRange());
  }

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	s;

    s = m_TextValue.getText();
    if (isValid(s) && !isUnchanged(s))
      setValue(parse(s));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Discards the input and closes the dialog.
   */
  protected void discardInput() {
    closeDialog(CANCEL_OPTION);
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    String	current;
    
    super.initForDisplay();
    
    current = ((Range) getValue()).getRange();
    if (!m_TextValue.getText().equals(current))
      m_TextValue.setText(current);
    m_TextValue.setToolTipText(((Range) getValue()).getExample());
    m_TextValue.grabFocus();
    // update help button
    if (m_ButtonHelp != null) {
      m_ButtonHelp.setVisible(getHelpDescription() != null);
      m_ButtonHelp.setToolTipText(getHelpDescription());
      if (getHelpIcon() != null)
	m_ButtonHelp.setIcon(GUIHelper.getIcon(getHelpIcon()));
      else
	m_ButtonHelp.setIcon(null);
      m_ButtonHelp.setText(getHelpTitle());
    }
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    Range[]			result;
    MultiLineValueDialog	dialog;
    Vector<String>		lines;
    int				i;

    dialog = new MultiLineValueDialog();
    dialog.setInfoText("Enter the ranges, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION) {
      lines = new Vector<String>(Arrays.asList(dialog.getContent().split("\n")));
      Utils.removeEmptyLines(lines);
      result = new Range[lines.size()];
      for (i = 0; i < lines.size(); i++)
	result[i] = parse(lines.get(i));
    }
    else {
      result = new Range[0];
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
    if (isValid(value))
      setValue(parse(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((Range) getValue()).getRange();
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
  
  /**
   * Updates the color of the border, indicating with RED if the
   * input is invalid.
   */
  protected void indicateValidity() {
    Color	curColor;
    Color	newColor;
    
    curColor = ((LineBorder) m_PanelValue.getBorder()).getLineColor();
    if (isValid(m_TextValue.getText()))
      newColor = m_DefaultBackground;
    else
      newColor = Color.RED;

    if (!newColor.equals(curColor))
      m_PanelValue.setBorder(BorderFactory.createLineBorder(newColor));
  }
}
