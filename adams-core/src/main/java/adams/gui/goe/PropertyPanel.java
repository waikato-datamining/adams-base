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
 *    PropertyPanel.java
 *    Copyright (C) 1999-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.goe.Favorites.FavoriteSelectionEvent;
import adams.gui.goe.Favorites.FavoriteSelectionListener;

/**
 * Support for drawing a property value in a component.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.PropertyPanel
 */
public class PropertyPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 5370025273466728904L;

  /**
   * Interface for editors that can customize the popup menu.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static interface PopupMenuCustomizer 
    extends adams.gui.core.PopupMenuCustomizer<BasePanel>{

    /**
     * For customizing the popup menu.
     *
     * @param owner	the property panel from where the menu originates
     * @param menu	the menu to customize
     */
    public void customizePopupMenu(BasePanel owner, JPopupMenu menu);
  }

  /** the method name for custom panel suppliers. */
  public final static String METHOD_CUSTOMPANEL = "getCustomPanel";

  /** The property editor. */
  protected PropertyEditor m_Editor;

  /** The currently displayed property dialog, if any. */
  protected GenericObjectEditorDialog m_Dialog;

  /** Whether the editor has provided its own panel. */
  protected boolean m_HasCustomPanel;

  /** the button for bringing up the properties. */
  protected JButton m_ButtonProperties;

  /** the panel itself. */
  protected PropertyPanel m_Self;

  /** the panel to draw the text on. */
  protected BasePanel m_PanelText;

  /** the custom panel, if any. */
  protected JPanel m_PanelCustom;

  /**
   * Create the panel with the supplied property editor.
   *
   * @param pe the PropertyEditor
   */
  public PropertyPanel(PropertyEditor pe) {
    this(pe, false);
  }

  /**
   * Create the panel with the supplied property editor,
   * optionally ignoring any custom panel the editor can provide.
   *
   * @param pe the PropertyEditor
   * @param ignoreCustomPanel whether to make use of any available custom panel
   */
  public PropertyPanel(PropertyEditor pe, boolean ignoreCustomPanel) {
    super();

    m_Editor         = pe;
    m_HasCustomPanel = (!ignoreCustomPanel && isCustomPanelSupplier(m_Editor));
    m_PanelCustom    = null;
    m_PanelText      = null;

    initGUI();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_HasCustomPanel = false;
    m_Self           = this;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    AbstractGenericObjectEditorHandler	handler;

    if (m_Editor == null)
      return;

    if (m_HasCustomPanel) {
      setLayout(new BorderLayout());
      handler = AbstractGenericObjectEditorHandler.getHandler(m_Editor);
      m_PanelCustom = handler.getCustomPanel(m_Editor);
      add(m_PanelCustom, BorderLayout.CENTER);
    }
    else {
      createDefaultPanel();
    }
  }

  /**
   * Creates the default style of panel for editors that do not
   * supply their own.
   */
  protected void createDefaultPanel() {
    Dimension 		newPref;

    setLayout(new BorderLayout());

    m_PanelText = new BasePanel() {
      private static final long serialVersionUID = 6257101099036104231L;
      @Override
      public void paintComponent(Graphics g) {
	if (!m_HasCustomPanel) {
	  Insets i = getInsets();
	  Rectangle box = new Rectangle(i.left, i.top,
	      getSize().width - i.left - i.right - 1,
	      getSize().height - i.top - i.bottom - 1);

	  g.clearRect(i.left, i.top,
	      getSize().width - i.right - i.left,
	      getSize().height - i.bottom - i.top);
	  m_Editor.paintValue(g, box);
	}
      }
    };
    m_PanelText.setBorder(BorderFactory.createEtchedBorder());
    m_PanelText.setToolTipText("Left-click to edit properties for this object, right-click/Alt+Shift+left-click for menu");
    m_PanelText.setOpaque(true);
    add(m_PanelText, BorderLayout.CENTER);

    m_PanelText.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent evt) {
        if (MouseUtils.isLeftClick(evt)) {
          evt.consume();
          showPropertyDialog();
        }
        else if (MouseUtils.isRightClick(evt)) {
          evt.consume();

          GenericObjectEditorPopupMenu menu = new GenericObjectEditorPopupMenu(m_Editor, m_Self);

          JMenuItem item = new JMenuItem("Edit...", GUIHelper.getIcon("properties.gif"));
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              showPropertyDialog();
            }
          });
          menu.insert(new JPopupMenu.Separator(), 0);
          menu.insert(item, 0);

          // Variables
          PropertySheetPanel parent = VariableSupport.findParent(PropertyPanel.this);
          if (parent != null)
            VariableSupport.updatePopup(parent, m_Editor, menu);

          // Favorites
          if (m_Editor instanceof GenericObjectEditor) {
            menu.addSeparator();
            Favorites.getSingleton().customizePopupMenu(
        	menu,
        	((GenericObjectEditor) m_Editor).getClassType(),
        	m_Editor.getValue(),
        	new FavoriteSelectionListener() {
        	  public void favoriteSelected(FavoriteSelectionEvent e) {
        	    m_Editor.setValue(e.getFavorite().getObject());
        	  }
        	});
          }

          // customized popup?
          if (m_Editor instanceof PopupMenuCustomizer)
            ((PopupMenuCustomizer) m_Editor).customizePopupMenu(PropertyPanel.this, menu);

          menu.show(m_Self, evt.getX(), evt.getY());
        }
      }
    });

    newPref = getPreferredSize();
    newPref.height = getFontMetrics(getFont()).getHeight() * 5 / 4;
    newPref.width = newPref.height * 5;
    setPreferredSize(newPref);

    m_Editor.addPropertyChangeListener(new PropertyChangeListener () {
      public void propertyChange(PropertyChangeEvent evt) {
	repaint();
      }
    });

    m_ButtonProperties = new JButton("...");
    m_ButtonProperties.setToolTipText("Click to edit properties");
    m_ButtonProperties.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showPropertyDialog();
      }
    });
    add(m_ButtonProperties, BorderLayout.EAST);
  }

  /**
   * Displays the property edit dialog for the panel.
   */
  public void showPropertyDialog() {
    if (m_Editor.getValue() != null) {
      if (m_Dialog == null) {
	m_Dialog = GenericObjectEditorDialog.createDialog(this, m_Editor);
	GUIHelper.setSizeAndLocation(m_Dialog, getLocationOnScreen().y, getLocationOnScreen().x);
	m_Dialog.setLocationRelativeTo(this);
	m_Dialog.setVisible(true);
      }
      else {
	m_Dialog.setVisible(true);
      }
      // make sure that m_Backup is correctly initialized!
      m_Editor.setValue(m_Editor.getValue());
    }
  }

  /**
   * Cleans up when the panel is destroyed.
   */
  @Override
  public void removeNotify() {
    super.removeNotify();

    if (m_Dialog != null) {
      m_Dialog.dispose();
      m_Dialog = null;
    }
  }

  /**
   * Checks whether the editor supplies its own panel.
   *
   * @param editor	the editor to check
   * @return		true if the editor supplies its own panel
   */
  public boolean isCustomPanelSupplier(PropertyEditor editor) {
    AbstractGenericObjectEditorHandler 	handler;

    handler = AbstractGenericObjectEditorHandler.getHandler(editor);

    return handler.hasCustomPanel(editor);
  }

  /**
   * Registers the text to display in a tool tip.
   * The text displays when the cursor lingers over the component.
   * <br><br>
   * Forwards the tool tip to its sub-panels.
   *
   * @param text	the string to display; if the text is <code>null</code>,
   *             	the tool tip is turned off for this component
   * @see		#m_PanelText
   * @see		#m_PanelCustom
   */
  @Override
  public void setToolTipText(String text) {
    JPanel	panel;

    super.setToolTipText(text);

    panel = null;

    if (m_HasCustomPanel)
      panel = m_PanelCustom;
    else
      panel = m_PanelText;

    if (panel != null)
      panel.setToolTipText(text);
  }
}
