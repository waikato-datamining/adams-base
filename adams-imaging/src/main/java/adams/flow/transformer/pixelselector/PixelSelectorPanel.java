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

/**
 * PixelSelectorPanel.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import adams.core.CleanUpHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.CustomPopupMenuProvider;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.report.reportfactory.AbstractTableAction;
import adams.gui.visualization.report.reportfactory.AddField;
import adams.gui.visualization.report.reportfactory.CopyFieldName;
import adams.gui.visualization.report.reportfactory.CopyFieldValue;
import adams.gui.visualization.report.reportfactory.ModifyValue;
import adams.gui.visualization.report.reportfactory.RemoveField;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Combines an ImagePanel and a Report table.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PixelSelectorPanel
  extends BasePanel 
  implements CustomPopupMenuProvider, CleanUpHandler, TableModelListener, MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 8742441848602035650L;

  /** the CANCEL option. */
  public final static int CANCEL_OPTION = 0;

  /** the APPROVE option. */
  public final static int APPROVE_OPTION = 1;
  
  /** the current image container. */
  protected AbstractImageContainer m_Image;
  
  /** the split pane. */
  protected BaseSplitPane m_SplitPane;
  
  /** the image panel. */
  protected ImagePanel m_ImagePanel;
  
  /** the report table. */
  protected ReportFactory.Table m_ReportTable;
  
  /** the OK button. */
  protected JButton m_ButtonOK;
  
  /** the Cancel button. */
  protected JButton m_ButtonCancel;
  
  /** the result (ok or cancel). */
  protected int m_Result;
  
  /** the action listeners. */
  protected HashSet<ActionListener> m_ActionListeners;

  /** the actions that are available from the popup menu. */
  protected AbstractPixelSelectorAction[] m_Actions;
  
  /** the menu bar. */
  protected JMenuBar m_MenuBar;
  
  /** the last action result for a selector action. */
  protected Hashtable<Class,Object> m_LastActionResult;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Image            = null;
    m_Result           = CANCEL_OPTION;
    m_ActionListeners  = new HashSet<ActionListener>();
    m_Actions          = new AbstractPixelSelectorAction[0];
    m_LastActionResult = new Hashtable<Class,Object>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane();
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);
    
    // image
    m_ImagePanel = new ImagePanel();
    m_ImagePanel.setCustomPopupMenuProvider(this);
    m_SplitPane.setLeftComponent(m_ImagePanel);
    
    // report
    m_ReportTable = new ReportFactory.Table(new Report());
    m_ReportTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_ReportTable.setPopupActions(
	new String[]{
	    CopyFieldName.class.getName(),
	    CopyFieldValue.class.getName(),
	    ModifyValue.class.getName(),
	    AbstractTableAction.SEPARATOR,
	    RemoveField.class.getName(),
	    AddField.class.getName(),
	}
    );
    m_SplitPane.setRightComponent(new BaseScrollPane(m_ReportTable));

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);
    
    m_ButtonOK = new JButton("Accept");
    m_ButtonOK.setMnemonic('A');
    m_ButtonOK.setIcon(GUIHelper.getIcon("accept.png"));
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(APPROVE_OPTION);
      }
    });
    panel.add(m_ButtonOK);
    
    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    m_ButtonCancel.setIcon(GUIHelper.getIcon("delete.gif"));
    m_ButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panel.add(m_ButtonCancel);
  }
  
  /**
   * Sets the location of the splitter/divider.
   * 
   * @param value	the position in pixels
   */
  public void setSplitterPosition(int value) {
    m_SplitPane.setDividerLocation(value);
  }
  
  /**
   * Returns the current location of the splitter/divider.
   * 
   * @return		the position in pixels
   */
  public int getSplitterPosition() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Sets the available popup actions.
   *
   * @param value	the actions
   */
  public void setActions(AbstractPixelSelectorAction[] value) {
    m_Actions = value;
  }

  /**
   * Returns the available popup actions.
   *
   * @return		the actions
   */
  public AbstractPixelSelectorAction[] getActions() {
    return m_Actions;
  }
  
  /**
   * Sets the image.
   * 
   * @param value	the image
   */
  public void setImage(AbstractImageContainer value) {
    m_ReportTable.getModel().removeTableModelListener(this);
    
    m_Image = value;
    if (m_Image == null)
      m_ImagePanel.setCurrentImage((BufferedImage) null);
    else
      m_ImagePanel.setCurrentImage(m_Image.toBufferedImage());
    update();

    m_ReportTable.getModel().addTableModelListener(this);
  }
  
  /**
   * Checks whether an image is currently displayed.
   * 
   * @return		true if an image is available
   */
  public boolean hasImage() {
    return (m_Image != null);
  }
  
  /**
   * Returns the current image.
   * 
   * @return		the image, null if none set
   */
  public AbstractImageContainer getImage() {
    return m_Image;
  }
  
  /**
   * Adds a listener for button actions.
   * 
   * @param l		the lister to add
   */
  public void addActionListener(ActionListener l) {
    m_ActionListeners.add(l);
  }
  
  /**
   * Removes a listener for button actions.
   * 
   * @param l		the lister to remove
   */
  public void removeActionListener(ActionListener l) {
    m_ActionListeners.remove(l);
  }
  
  /**
   * Notifies all the action listeners.
   * 
   * @param button	the button that triggered this event (OK/Cancel)
   */
  protected void notifyActionListeners(int button) {
    ActionEvent		e;
    String		cmd;
    
    if (button == APPROVE_OPTION)
      cmd = "OK";
    else
      cmd = "Cancel";
    
    e = new ActionEvent(this, button, cmd);
    for (ActionListener l: m_ActionListeners)
      l.actionPerformed(e);
  }

  /**
   * Removes all image overlays.
   */
  public void clearImageOverlays() {
    m_ImagePanel.clearImageOverlays();
  }

  /**
   * Adds the image overlay.
   *
   * @param io	the image overlay to add
   */
  public void addImageOverlay(ImageOverlay io) {
    if (io instanceof AbstractPixelSelectorOverlay)
      ((AbstractPixelSelectorOverlay) io).setImage(m_Image);
    m_ImagePanel.addImageOverlay(io);
  }

  /**
   * Removes the image overlay.
   *
   * @param io	the image overlay to remove
   */
  public void removeImageOverlay(ImageOverlay io) {
    m_ImagePanel.removeImageOverlay(io);
  }

  /**
   * Returns an iterator over all the image overlays.
   * 
   * @return		iterator on image overlays
   */
  public Iterator<ImageOverlay> imageOverlays() {
    return m_ImagePanel.imageOverlays();
  }

  /**
   * Closes the dialog.
   * 
   * @param result	whether successful or not
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  protected void closeDialog(int result) {
    m_Result = result;
    notifyActionListeners(m_Result);
    closeParent();
  }
  
  /**
   * Edits the current overlays.
   */
  protected void editOverlays() {
    List<AbstractPixelSelectorOverlay>	list;
    AbstractPixelSelectorOverlay[]	overlays;
    AbstractPixelSelectorOverlay	overlay;
    Iterator<ImageOverlay>		iter;
    GenericArrayEditorDialog		dialog;
    int					i;
    
    // get current overlays
    list = new ArrayList<AbstractPixelSelectorOverlay>();
    iter = m_ImagePanel.imageOverlays();
    while (iter.hasNext()) {
      overlay = ((AbstractPixelSelectorOverlay) iter.next()).shallowCopy();
      list.add(overlay);
    }
    
    // setup dialog
    if (getParentDialog() != null)
      dialog = new GenericArrayEditorDialog(getParentDialog());
    else
      dialog = new GenericArrayEditorDialog(getParentFrame());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Overlays");
    dialog.setCurrent(list.toArray(new AbstractPixelSelectorOverlay[list.size()]));
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    
    // update overlays
    if (dialog.getResult() == GenericArrayEditorDialog.APPROVE_OPTION) {
      m_ImagePanel.clearImageOverlays();
      overlays = (AbstractPixelSelectorOverlay[]) dialog.getCurrent();
      for (i = 0; i < overlays.length; i++)
	m_ImagePanel.addImageOverlay(overlays[i]);
    }
    update();
  }
  
  /**
   * Checks for the specified action whether a previous result is available.
   * 
   * @param cls		the action class to check
   * @return		true if a previous result is available
   */
  public boolean hasLastActionResult(Class cls) {
    return m_LastActionResult.containsKey(cls);
  }
  
  /**
   * Stores the result for the specified action.
   * 
   * @param cls		the action class to store the result for
   * @param value	the action result, null removes the association
   * @return		the result or null if not available
   */
  public void setLastActionResult(Class cls, Object value) {
    if (value == null)
      m_LastActionResult.remove(cls);
    else
      m_LastActionResult.put(cls, value);
  }
  
  /**
   * Retrieves the previous result for the specified action if available.
   * 
   * @param cls		the action class to get the result for
   * @return		the result or null if not available
   */
  public Object getLastActionResult(Class cls) {
    return m_LastActionResult.get(cls);
  }
  
  /**
   * Returns the current result.
   * 
   * @return		the user action
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getResult() {
    return m_Result;
  }

  /**
   * Creates a popup menu for the given mouse event.
   * 
   * @param e		the event that triggered the request
   * @return		the menu, null if none was generated
   */
  public BasePopupMenu getCustomPopupMenu(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		menuitem;
    Point		loc;
    
    result = null;

    if (m_Actions.length > 0) {
      loc    = m_ImagePanel.mouseToPixelLocation(e.getPoint());
      result = new BasePopupMenu();
      for (AbstractPixelSelectorAction action: m_Actions) {
	// store necessary information in action
	action.setMousePosition(e.getPoint());
	action.setPixelPosition(loc);
	action.setPanel(PixelSelectorPanel.this);
	// create menu item
	menuitem = new JMenuItem(action);
	result.add(menuitem);
      }
    }
    
    return result;
  }
  
  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   * 
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;
    
    if (m_MenuBar == null) {
      result = new JMenuBar();

      // Dialog
      menu = new JMenu("Dialog");
      menu.setMnemonic('D');
      result.add(menu);
      
      // Dialog/Cancel
      menuitem = new JMenuItem("Cancel");
      menuitem.setMnemonic('C');
      menuitem.setIcon(GUIHelper.getIcon("delete.gif"));
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeDialog(CANCEL_OPTION);
        }
      });
      menu.add(menuitem);
      
      // Dialog/Accept
      menuitem = new JMenuItem("Accept");
      menuitem.setMnemonic('A');
      menuitem.setIcon(GUIHelper.getIcon("accept.png"));
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeDialog(APPROVE_OPTION);
        }
      });
      menu.addSeparator();
      menu.add(menuitem);
      
      // View
      menu = new JMenu("View");
      menu.setMnemonic('V');
      result.add(menu);
      
      // View/Overlays
      menuitem = new JMenuItem("Overlays...");
      menuitem.setMnemonic('O');
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editOverlays();
        }
      });
      menu.add(menuitem);
      
      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }
    
    return result;
  }

  /**
   * Updates the report.
   */
  public void updateReport() {
    if (m_Image != null)
      m_ReportTable.setReport(m_Image.getReport());
    else
      m_ReportTable.setReport(new Report());
    m_ReportTable.setOptimalColumnWidth();
  }

  /**
   * Updates the image.
   */
  public void updateImage() {
    Iterator<ImageOverlay>	overlays;
    ImageOverlay		overlay;

    overlays = imageOverlays();
    while (overlays.hasNext()) {
      overlay = overlays.next();
      if (overlay instanceof AbstractPixelSelectorOverlay) {
	((AbstractPixelSelectorOverlay) overlay).setImage(m_Image);
      }
    }
    m_ImagePanel.repaint();
  }

  /**
   * Updates the report/image.
   */
  public void update() {
    updateReport();
    updateImage();
  }

  /**
   * Whenever the data in the report changes.
   * 
   * @param e		the table event
   * @see		#updateImage()
   */
  public void tableChanged(TableModelEvent e) {
    updateImage();
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_ActionListeners.clear();
    m_Image   = null;
    m_Actions = new AbstractPixelSelectorAction[0];
  }
}
