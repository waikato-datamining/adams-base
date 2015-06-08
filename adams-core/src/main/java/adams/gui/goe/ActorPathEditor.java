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
 * ActorPathEditor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.option.AbstractOption;
import adams.flow.core.ActorPath;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.MouseUtils;
import adams.gui.flow.tree.Tree;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A PropertyEditor for ActorPath objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorPathEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler {

  /** The text field with the value. */
  protected JTextField m_TextValue;

  /** The tree displaying all the callable actors. */
  protected Tree m_Tree;

  /**
   * Returns the reference as string.
   *
   * @param option	the current option
   * @param object	the reference object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((ActorPath) object).toString();
  }

  /**
   * Returns a reference generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a reference
   * @return		the generated reference
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new ActorPath(str);
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
    return new ActorPath(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + ActorPath.class.getName() + "(\"" + Utils.backQuoteChars(getValue().toString()) + "\")";

    return result;
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
  protected ActorPath parse(String s) {
    ActorPath	result;

    try {
      result = new ActorPath(s);
      if (!result.toString().equals(s))
	result = null;
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel	panelTree;
    JPanel	panelAll;
    JPanel	panel;
    JLabel	label;
    JPanel 	panelButtons;
    JButton 	buttonOK;
    JButton 	buttonClose;

    panelTree = new JPanel(new BorderLayout(0, 5));
    panelTree.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    panelTree.setPreferredSize(new Dimension(600, 600));
    m_Tree    = new Tree(null);
    m_Tree.setAllowNodePopup(false);
    m_Tree.setAllowKeyboardShortcuts(false);
    m_Tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	if (m_Tree.getSelectedFullName() != null)
	  m_TextValue.setText(m_Tree.getSelectedFullName());
	else
	  m_TextValue.setText("");
      }
    });
    m_Tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (MouseUtils.isDoubleClick(e) && (m_Tree.getSelectedFullName() != null)) {
	  m_TextValue.setText(m_Tree.getSelectedFullName());
	  e.consume();
        }
        if (!e.isConsumed())
          super.mousePressed(e);
      }
    });
    panelTree.add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);

    panelTree.add(new JLabel("Select actor:"), BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout());
    panelTree.add(panelAll, BorderLayout.SOUTH);
    panel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panel, BorderLayout.CENTER);

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
    label = new JLabel("Manual reference");
    label.setDisplayedMnemonic('M');
    label.setLabelFor(m_TextValue);
    panel.add(label);
    panel.add(m_TextValue);

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

    return panelTree;
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
    return s.equals(getValue().toString());
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
    Tree	tree;

    super.initForDisplay();

    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());

    tree = FlowHelper.getTree(m_CustomEditor);
    if (tree != null) {
      m_Tree.setActor(tree.getActor());
      if (!m_TextValue.getText().isEmpty())
	m_Tree.locateAndDisplay(m_TextValue.getText());
    }

    m_TextValue.grabFocus();
  }
}
