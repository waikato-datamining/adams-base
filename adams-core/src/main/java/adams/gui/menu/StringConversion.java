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
 * OptionsConversion.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.option.OptionUtils;
import adams.data.conversion.AbstractStringConversion;
import adams.data.conversion.UnBackQuote;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the StringConversion dialog.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringConversion
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -3102369171000332548L;

  /**
   * Initializes the menu item with no owner.
   */
  public StringConversion() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public StringConversion(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    final BasePanel 			panel;
    JPanel				strings;
    JPanel				left;
    JPanel				right;
    final GenericObjectEditorPanel	conversion;
    JLabel 				label;
    final BaseTextAreaWithButtons 	textLeft;
    JButton				button;
    final BaseTextAreaWithButtons	textRight;
    JPanel				panelText;
    JPanel				buttons;
    JButton 				buttonConvert;
    JButton 				buttonCancel;

    panel = new BasePanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    conversion = new GenericObjectEditorPanel(AbstractStringConversion.class, new UnBackQuote(), true);
    conversion.setPrefix("C_onversion");
    panel.add(conversion, BorderLayout.NORTH);

    strings = new JPanel(new GridLayout(1, 2));
    panel.add(strings, BorderLayout.CENTER);

    left = new JPanel(new BorderLayout());
    strings.add(left);
    textLeft = new BaseTextAreaWithButtons(4, 20);
    textLeft.setLineWrap(true);
    textLeft.setWrapStyleWord(true);
    textLeft.setTextFont(GUIHelper.getMonospacedFont());
    button = new JButton(GUIHelper.getIcon("copy.gif"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(textLeft.getText());
      }
    });
    textLeft.addToButtonsPanel(button);
    button = new JButton(GUIHelper.getIcon("paste.gif"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	textLeft.setText(GUIHelper.pasteStringFromClipboard());
      }
    });
    textLeft.addToButtonsPanel(button);
    label = new JLabel("Input");
    label.setDisplayedMnemonic('I');
    label.setLabelFor(textLeft);
    panelText = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelText.add(label);
    left.add(panelText, BorderLayout.NORTH);
    left.add(textLeft, BorderLayout.CENTER);

    right = new JPanel(new BorderLayout());
    right.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    strings.add(right);
    textRight = new BaseTextAreaWithButtons(4, 20);
    textRight.setEditable(false);
    textRight.setLineWrap(true);
    textRight.setWrapStyleWord(true);
    textRight.setTextFont(GUIHelper.getMonospacedFont());
    button = new JButton(GUIHelper.getIcon("copy.gif"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(textRight.getText());
      }
    });
    textRight.addToButtonsPanel(button);
    label = new JLabel("Output");
    label.setDisplayedMnemonic('u');
    label.setLabelFor(textRight);
    panelText = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelText.add(label);
    right.add(panelText, BorderLayout.NORTH);
    right.add(textRight, BorderLayout.CENTER);

    buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(buttons, BorderLayout.SOUTH);

    buttonConvert = new JButton("Convert");
    buttonConvert.setMnemonic('v');
    buttonConvert.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	AbstractStringConversion conv = (AbstractStringConversion) conversion.getCurrent();
	conv.setInput(textLeft.getText());
	String msg = conv.convert();
	if (msg == null) {
	  textRight.setText("" + conv.getOutput());
	}
	else {
	  textRight.setText("");
	  GUIHelper.showErrorMessage(
	    panel, "Failed to convert string using '" + OptionUtils.getCommandLine(conversion) + "':\n" + msg);
	}
      }
    });
    buttons.add(buttonConvert);

    buttonCancel = new JButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	panel.closeParent();
      }
    });
    buttons.add(buttonCancel);

    createChildFrame(panel, 600, 400);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "String conversion";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "json_string.gif";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.DEVELOPER;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_MAINTENANCE;
  }
}