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
 * Display.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.data.io.output.NullWriter;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;

/**
 <!-- globalinfo-start -->
 * Actor that outputs any object that arrives at its input port via the 'toString()' method.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Display
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Monospaced-PLAIN-12
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Display
  extends AbstractTextualDisplay 
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3655490351179936332L;

  /** the print menu item. */
  protected JMenuItem m_MenuItemFilePrint;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemEditUndo;

  /** the redo menu item. */
  protected JMenuItem m_MenuItemEditRedo;

  /** the cut menu item. */
  protected JMenuItem m_MenuItemEditCut;

  /** the copy menu item. */
  protected JMenuItem m_MenuItemEditCopy;

  /** the paste menu item. */
  protected JMenuItem m_MenuItemEditPaste;

  /** the select all menu item. */
  protected JMenuItem m_MenuItemEditSelectAll;

  /** the find menu item. */
  protected JMenuItem m_MenuItemEditFind;

  /** the find next menu item. */
  protected JMenuItem m_MenuItemEditFindNext;

  /** the font menu item. */
  protected JMenuItem m_MenuItemViewFont;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor that outputs any object that arrives at its input port via "
      + "the 'toString()' method.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 640;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 480;
  }

  /**
   * Creates the "File" menu.
   *
   * @return		the generated menu
   */
  @Override
  protected JMenu createFileMenu() {
    JMenu	result;
    JMenuItem	menuitem;
    int		pos;

    result = super.createFileMenu();

    // File/Print
    menuitem = new JMenuItem("Print...");
    menuitem.setMnemonic('P');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed P"));
    menuitem.setIcon(GUIHelper.getIcon("print.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	((TextEditorPanel) m_Panel).printText();
      }
    });
    pos = indexOfMenuItem(result, m_MenuItemFileClose);
    result.insertSeparator(pos);
    result.insert(menuitem, pos);
    m_MenuItemFilePrint = menuitem;

    return result;
  }

  /**
   * Creates the "Edit" menu.
   *
   * @return		the menu
   */
  protected JMenu createEditMenu() {
    JMenu			result;
    JMenuItem			menuitem;
    final TextEditorPanel	fPanel;

    fPanel = (TextEditorPanel) m_Panel;

    // Edit
    result = new JMenu("Edit");
    result.setMnemonic('E');
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // Edit/Undo
    menuitem = new JMenuItem("Undo");
    menuitem.setMnemonic('U');
    menuitem.setEnabled(fPanel.canUndo());
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
    menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.undo();
      }
    });
    result.add(menuitem);
    m_MenuItemEditUndo = menuitem;

    menuitem = new JMenuItem("Redo");
    menuitem.setMnemonic('R');
    menuitem.setEnabled(fPanel.canUndo());
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
    menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.redo();
      }
    });
    result.add(menuitem);
    m_MenuItemEditRedo = menuitem;

    // Edit/Cut
    menuitem = new JMenuItem("Cut", GUIHelper.getIcon("cut.gif"));
    menuitem.setMnemonic('u');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed X"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.cut();
      }
    });
    result.addSeparator();
    result.add(menuitem);
    m_MenuItemEditCut = menuitem;

    // Edit/Copy
    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.copy();
      }
    });
    result.add(menuitem);
    m_MenuItemEditCopy = menuitem;

    // Edit/Paste
    menuitem = new JMenuItem("Paste", GUIHelper.getIcon("paste.gif"));
    menuitem.setMnemonic('P');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed V"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.paste();
      }
    });
    result.add(menuitem);
    m_MenuItemEditPaste = menuitem;

    // Edit/Select all
    menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('S');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed A"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.selectAll();
      }
    });
    result.addSeparator();
    result.add(menuitem);
    m_MenuItemEditSelectAll = menuitem;

    // Edit/Find
    menuitem = new JMenuItem("Find", GUIHelper.getIcon("find.gif"));
    menuitem.setMnemonic('F');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.find();
      }
    });
    result.addSeparator();
    result.add(menuitem);
    m_MenuItemEditFind = menuitem;

    // Edit/Find next
    menuitem = new JMenuItem("Find next", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('n');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed G"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.findNext();
      }
    });
    result.add(menuitem);
    m_MenuItemEditFindNext = menuitem;

    return result;
  }

  /**
   * Creates the "Edit" menu.
   *
   * @return		the menu
   */
  protected JMenu createViewMenu() {
    JMenu	result;
    JMenuItem	menuitem;

    // View
    result = new JMenu("View");
    result.setMnemonic('V');
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // View/Font
    menuitem = new JMenuItem("Choose font...");
    result.add(menuitem);
    menuitem.setIcon(GUIHelper.getIcon("font.png"));
    menuitem.setMnemonic('f');
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	((TextEditorPanel) m_Panel).selectFont();
      }
    });
    m_MenuItemViewFont = menuitem;

    return result;
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  @Override
  protected JMenuBar createMenuBar() {
    JMenuBar	result;

    result = super.createMenuBar();
    result.add(createEditMenu());
    result.add(createViewMenu());

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    boolean		contentAvailable;
    TextEditorPanel	panel;

    super.updateMenu();

    panel            = (TextEditorPanel) m_Panel;
    contentAvailable = (panel.getContent().length() > 0);

    // Edit
    m_MenuItemEditUndo.setEnabled(panel.canUndo());
    m_MenuItemEditRedo.setEnabled(panel.canRedo());
    m_MenuItemEditCut.setEnabled(panel.canCut());
    m_MenuItemEditCopy.setEnabled(panel.canCopy());
    m_MenuItemEditPaste.setEnabled(panel.canPaste());
    m_MenuItemEditFind.setEnabled(contentAvailable);
    m_MenuItemEditFindNext.setEnabled(contentAvailable && (panel.getLastFind() != null));
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((TextEditorPanel) m_Panel).clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    TextEditorPanel	result;

    result = new TextEditorPanel();
    result.setTextFont(getFont());

    return result;
  }

  /**
   * Returns the text to save.
   *
   * @return		the text, null if no text available
   */
  @Override
  public String supplyText() {
    String		result;
    TextEditorPanel	panel;

    result = null;

    panel = (TextEditorPanel) m_Panel; 
    if (panel != null) {
      if (panel.getContent().length() != 0)
	result = panel.getContent();
    }

    return result;
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		always true
   */
  @Override
  protected boolean supportsClear() {
    return true;
  }

  /**
   * Clears the display.
   */
  @Override
  protected void clear() {
    ((TextEditorPanel) m_Panel).clear();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Object.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    TextEditorPanel 	panel;

    panel = (TextEditorPanel) m_Panel;
    panel.append(token.getPayload() + "\n");
    panel.setModified(false);
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractTextDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 7384093089760722339L;
      protected TextEditorPanel m_TextEditorPanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_TextEditorPanel = new TextEditorPanel();
	add(m_TextEditorPanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_TextEditorPanel.setContent(token.getPayload() + "\n");
      }
      @Override
      public void cleanUp() {
	m_TextEditorPanel.setContent("");
      }
      @Override
      public void clearPanel() {
	m_TextEditorPanel.clear();
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
	return null;
      }
      @Override
      public String supplyText() {
	return m_TextEditorPanel.getContent();
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
