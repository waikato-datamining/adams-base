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
 * JdbcUrlEditor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.HelpProvider;
import adams.core.Utils;
import adams.core.option.parsing.JdbcUrlParsing;
import adams.db.AbstractDatabaseConnection;
import adams.db.ConnectionParameters;
import adams.db.DatabaseConnection;
import adams.db.JdbcUrl;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.BrowserHelper;
import adams.gui.core.GUIHelper;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A PropertyEditor for JdbcUrl objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see JdbcUrl
 */
public class JdbcUrlEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, MultiSelectionEditor, 
             InlineEditorSupportWithFavorites, HelpProvider {

  /** The text field with the value. */
  protected JTextComponent m_TextValue;

  /** the button for the history. */
  protected BaseButton m_ButtonHistory;

  /** the help button for bringing up a browser with the Java Pattern class. */
  protected BaseButton m_ButtonHelp;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return JdbcUrlParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return new JdbcUrl(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + Utils.backQuoteChars(getValue().toString()) + "\")";

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
    if (val.isEmpty())
      val = AbstractPropertyEditorSupport.EMPTY;
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
  protected JdbcUrl parse(String s) {
    JdbcUrl	result;

    try {
      result = (JdbcUrl) getValue().getClass().newInstance();
      result.setValue(s);
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
   * @return 		always null
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel		panel;
    JLabel		label;
    JPanel 		panelButtons;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    m_TextValue = new BaseTextArea(2, 30);
    ((BaseTextArea) m_TextValue).setLineWrap(true);
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

    panelAll = new JPanel(new BorderLayout());

    label = new JLabel("Value");
    label.setDisplayedMnemonic('V');
    label.setLabelFor(m_TextValue);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.WEST);
    
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    panel.add(new BaseScrollPane(m_TextValue), BorderLayout.CENTER);
    panelAll.add(panel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panel, BorderLayout.EAST);

    m_ButtonHistory = new BaseButton(GUIHelper.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent connections");
    m_ButtonHistory.addActionListener((ActionEvent e) -> showConnectionsPopup());
    panel.add(m_ButtonHistory);

    m_ButtonHelp = new BaseButton();
    m_ButtonHelp.setVisible(false);
    m_ButtonHelp.addActionListener((ActionEvent e) -> BrowserHelper.openURL(getHelpURL()));
    panel.add(m_ButtonHelp);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> acceptInput());
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener((ActionEvent e) -> discardInput());
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Shows the popup menu with the connections.
   */
  protected void showConnectionsPopup() {
    BasePopupMenu 	menu;
    JMenuItem 		menuitem;
    List<JMenuItem> 	menuitems;

    menuitems = new ArrayList<>();
    for (ConnectionParameters params: DatabaseConnection.getSingleton().getConnections()) {
      final ConnectionParameters fParams = params;
      menuitem = new JMenuItem(params.toString());
      menuitem.addActionListener((ActionEvent e) -> connect(fParams));
      menuitems.add(menuitem);
    }
    menu = BasePopupMenu.createCascadingMenu(menuitems, (int) m_ButtonHistory.getLocationOnScreen().getY(), -1, "More...");
    menu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight());
  }

  /**
   * Connects using the parameters.
   *
   * @param params	the connection parameters
   */
  protected void connect(ConnectionParameters params) {
    String			error;
    AbstractDatabaseConnection 	conn;

    error = null;
    conn  = params.toDatabaseConnection(DatabaseConnection.getSingleton().getClass());

    if (!conn.isConnected()) {
      try {
        conn.resetFailedConnectAttempt();
	conn.connect();
	if (!conn.isConnected()) {
	  error = "Failed to connect to: " + params.getURL();
	}
	else {
	  params = conn.toConnectionParameters(conn);
	  conn.addConnection(params);
	}
      }
      catch (Exception e) {
        error = Utils.handleException(conn, "Failed to connect to: " + params.getURL(), e);
      }
    }
    if (error != null)
      GUIHelper.showErrorMessage(GUIHelper.getParentDialog(m_ButtonHistory), error);
    else
      m_TextValue.setText(conn.getURL());
  }

  /**
   * Checks whether the string is valid.
   *
   * @param s		the string to check
   * @return		true if the string is valid
   */
  protected boolean isValid(String s) {
    return ((JdbcUrl) getValue()).isValid(s);
  }

  /**
   * Checks whether the string is the same as the currently used one.
   *
   * @param s		the string to check
   * @return		true if the strings are the same
   */
  protected boolean isUnchanged(String s) {
    return s.equals(((JdbcUrl) getValue()).getValue());
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
    String	toolTip;

    super.initForDisplay();

    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setCaretPosition(0);
    toolTip = ((JdbcUrl) getValue()).getTipText();
    if (toolTip != null)
      toolTip = "<html>" + GUIHelper.processTipText(toolTip, 120) + "</html>";
    m_TextValue.setToolTipText(toolTip);
    m_TextValue.grabFocus();
    // update help button
    if (m_ButtonHelp != null) {
      m_ButtonHelp.setVisible(getHelpURL() != null);
      m_ButtonHelp.setToolTipText("<html>" + GUIHelper.processTipText(getHelpDescription(), 120) + "</html>");
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
    Object[]			result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the string representations, one per line:");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = (Object[]) Array.newInstance(JdbcUrl.class, lines.size());
    for (i = 0; i < lines.size(); i++)
      Array.set(result, i, parse(lines.get(i)));

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
    return ((JdbcUrl) getValue()).getValue();
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
   * Checks whether favorites support is available.
   *
   * @return		true if enabled
   */
  public boolean isInlineFavoritesEnabled() {
    return ((JdbcUrl) getValue()).hasFavoritesSupport();
  }

  /**
   * Returns the class to use for favorites.
   *
   * @return		the class to use
   */
  public Class getInlineFavoritesClass() {
    return getValue().getClass();
  }

  /**
   * Returns a URL with additional information.
   *
   * @return		the URL (or multiple URLs tab-separated), null if not available
   */
  public String getHelpURL() {
    return "https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html";
  }

  /**
   * Returns a long help description, e.g., used in tiptexts.
   *
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    return new JdbcUrl().getTipText();
  }

  /**
   * Returns a short title for the help, e.g., used for buttons.
   *
   * @return		the short title, null if not available
   */
  public String getHelpTitle() {
    return null;
  }

  /**
   * Returns the name of a help icon, e.g., used for buttons.
   *
   * @return		the icon name, null if not available
   */
  public String getHelpIcon() {
    return "help.gif";
  }
}
