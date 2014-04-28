/*
 * Clipboard.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedProducer;
import adams.flow.core.AbstractActor;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;

/**
 * A simple clipboard per flow editor window.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Clipboard
  extends AbstractEditorTab
  implements SelectionAwareEditorTab {

  /** for serialization. */
  private static final long serialVersionUID = 8054086756987403832L;

  /**
   * Container for an item in the clipboard, wraps around an actor.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ClipboardItem 
    implements Serializable {
    
    /** for serialization. */
    private static final long serialVersionUID = 7449734120644203603L;

    /** the name of the item. */
    protected String m_Name;
    
    /** the actor. */
    protected AbstractActor m_Actor;
    
    /**
     * Initializes the container item.
     * 
     * @param name	the name of the item
     * @param actor	the actor to store
     */
    public ClipboardItem(String name, AbstractActor actor) {
      super();
      
      m_Name  = name;
      m_Actor = actor;
    }
    
    /**
     * Sets the name of the item.
     * 
     * @param value	the name
     */
    public void setName(String value) {
      m_Name = value;
    }
    
    /**
     * The name of the item.
     * 
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }
    
    /**
     * Returns the stored actor.
     * 
     * @return		the actor
     */
    public AbstractActor getActor() {
      return m_Actor;
    }
    
    /**
     * Simply returns the name of the item.
     * 
     * @return		the item
     */
    @Override
    public String toString() {
      return m_Name;
    }
  }

  /** the split pane for list and preview. */
  protected BaseSplitPane m_SplitPane;
  
  /** for storing the clipboard items. */
  protected BaseList m_ListItems;
  
  /** the underlying list model. */
  protected DefaultListModel m_ListItemsModel;
  
  /** the preview. */
  protected FlowPanel m_PanelPreview;
  
  /** the button for clearing the clipboard. */
  protected JButton m_ButtonClear;
  
  /** the button for adding the actor to the clipboard. */
  protected JButton m_ButtonAdd;
  
  /** the button for removing the selected items from the clipboard. */
  protected JButton m_ButtonRemove;
  
  /** the button for copying the actor to the clipboard. */
  protected JButton m_ButtonCopy;
  
  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;
  
  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Clipboard";
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(0.5);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_ListItemsModel = new DefaultListModel();
    m_ListItems      = new BaseList(m_ListItemsModel);
    m_ListItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_ListItems.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
	updatePreview();
      }
    });
    m_SplitPane.setTopComponent(new BaseScrollPane(m_ListItems));
    
    m_PanelPreview = new FlowPanel();
    m_PanelPreview.getTree().setEditable(false);
    m_SplitPane.setBottomComponent(m_PanelPreview);
    
    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelButtons, BorderLayout.SOUTH);
    
    m_ButtonClear = new JButton(GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setToolTipText("Remove all entries");
    m_ButtonClear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_ListItemsModel.clear();
      }
    });
    m_PanelButtons.add(m_ButtonClear);
    
    m_ButtonAdd = new JButton(GUIHelper.getIcon("add.gif"));
    m_ButtonAdd.setToolTipText("Add currently selected actor to clipboard");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	AbstractActor actor = getCurrentPanel().getTree().getSelectedNode().getFullActor();
	String name = JOptionPane.showInputDialog(GUIHelper.getParentComponent(m_ListItems), "Please enter name for clipboard item:", actor.getName());
	if (name == null)
	  return;
	m_ListItemsModel.addElement(new ClipboardItem(name, actor));
      }
    });
    m_PanelButtons.add(m_ButtonAdd);
    
    m_ButtonRemove = new JButton(GUIHelper.getIcon("delete.gif"));
    m_ButtonRemove.setToolTipText("Remove selected entries");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int[] indices = m_ListItems.getSelectedIndices();
	Arrays.sort(indices);
	for (int i = indices.length - 1; i >= 0; i--)
	  m_ListItemsModel.removeElementAt(indices[i]);
      }
    });
    m_PanelButtons.add(m_ButtonRemove);
    
    m_ButtonCopy = new JButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.setToolTipText("Copy actor to system clipboard");
    m_ButtonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	ClipboardItem item = (ClipboardItem) m_ListItems.getSelectedValue();
	GUIHelper.copyToClipboard(AbstractOptionProducer.toString(NestedProducer.class, item.getActor()));
      }
    });
    m_PanelButtons.add(m_ButtonCopy);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    updateButtons();
    updatePreview();
  }
  
  /**
   * Notifies the tab of the currently selected actors.
   *
   * @param paths	the selected paths
   * @param actors	the currently selected actors
   */
  @Override
  public void actorSelectionChanged(TreePath[] paths, AbstractActor[] actors) {
    updateButtons();
  }
  
  /**
   * Updates the enabled status of the buttons.
   */
  protected void updateButtons() {
    m_ButtonClear.setEnabled(m_ListItemsModel.getSize() > 0);
    m_ButtonAdd.setEnabled((getCurrentPanel() != null) && (getCurrentPanel().getTree().getSelectionCount() == 1));
    m_ButtonRemove.setEnabled(m_ListItems.getSelectedIndices().length > 0);
    m_ButtonCopy.setEnabled(m_ListItems.getSelectedIndices().length == 1);
  }

  /**
   * Updates the preview panel.
   */
  protected void updatePreview() {
    ClipboardItem	item;
    AbstractActor	actor;
    
    if (m_ListItems.getSelectedIndex() == -1) {
      actor = null;
    }
    else {
      item = (ClipboardItem) m_ListItems.getSelectedValue();
      actor = item.getActor();
    }
    
    m_PanelPreview.getTree().setActor(actor);
  }
}
