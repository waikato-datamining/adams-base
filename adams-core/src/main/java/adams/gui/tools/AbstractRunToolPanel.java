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
 * RunTool.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.chooser.AbstractChooserPanel.PopupMenuCustomizer;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingCommandCode;
import adams.gui.scripting.ScriptingEngineHandler;
import adams.tools.AbstractTool;
import adams.tools.InitializeTables;
import adams.tools.RunTool;

/**
 * A panel for executing tools from the GUI.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRunToolPanel
  extends BasePanel
  implements ScriptingEngineHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7309370485661269159L;

  /** the current tool. */
  protected AbstractTool m_CurrentTool;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_CurrentTool = new InitializeTables();
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel			panel;
    JPanel			panel2;
    JButton			buttonOK;
    JButton			buttonCancel;
    GenericObjectEditorPanel 	panelTool;

    super.initGUI();

    setLayout(new BorderLayout());

    panelTool = new GenericObjectEditorPanel(AbstractTool.class, m_CurrentTool, true);
    panelTool.setPrefix("Tool");
    panelTool.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_CurrentTool = (AbstractTool) ((GenericObjectEditorPanel) e.getSource()).getCurrent();
      }
    });
    panelTool.setPopupMenuCustomizer(new PopupMenuCustomizer() {
      public void customizePopupMenu(AbstractChooserPanel owner, JPopupMenu menu) {
	JMenuItem item = new JMenuItem("Copy RunTool setup to clipboard");
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    RunTool runTool = getRunTool();
	    runTool.setTool(m_CurrentTool);
            GUIHelper.copyToClipboard(AbstractOptionProducer.toString(NestedProducer.class, runTool));
	  }
	});
	menu.addSeparator();
	menu.add(item);
      }
    });

    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeParent();
	run(m_CurrentTool);
      }
    });

    buttonCancel = new JButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeParent();
      }
    });

    // tool
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel2.add(panelTool, BorderLayout.CENTER);
    add(panel2, BorderLayout.NORTH);

    // buttons
    panel2 = new JPanel(new BorderLayout());
    add(panel2, BorderLayout.SOUTH);
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel2.add(panel, BorderLayout.NORTH);
    panel.add(buttonOK);
    panel.add(buttonCancel);
  }

  /**
   * Returns the project-specific RunTool instance.
   *
   * @return		the RunTool instance to use
   */
  protected RunTool getRunTool() {
    return new RunTool();
  }

  /**
   * applies the given tool.
   *
   * @param tool	the tool to apply
   */
  protected void run(AbstractTool tool) {
    if (tool == null)
      return;

    getScriptingEngine().add(
	null,
	adams.gui.scripting.RunTool.ACTION + " " + OptionUtils.getCommandLine(tool),
	new ScriptingCommandCode() {
	  public void execute() {
	    JOptionPane.showMessageDialog(AbstractRunToolPanel.this, "Execution of tool finished!");
	  }
	});
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  public abstract AbstractScriptingEngine getScriptingEngine();
}
