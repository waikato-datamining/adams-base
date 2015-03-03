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
 * FavoritesManagementPanel.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.ClassLister;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.goe.Favorites;
import adams.gui.goe.Favorites.Favorite;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * A panel for managing one's GOE favorites.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FavoritesManagementPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 7641450241106552195L;

  /**
   * Abstract List model class for displaying favorites.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractFavoritesListModel
    extends DefaultListModel {

    /** for serialization. */
    private static final long serialVersionUID = -4439263856723765375L;

    /** the underlying favorites. */
    protected Favorites m_Favorites;

    /**
     * Initializes the model with no favorites.
     */
    public AbstractFavoritesListModel() {
      this(new Favorites(false));
    }

    /**
     * Initializes the model.
     *
     * @param fav	the favorites to use
     */
    public AbstractFavoritesListModel(Favorites fav) {
      super();

      setFavorites(fav);
    }

    /**
     * Sets the favorites to display.
     *
     * @param value	the favorites to use
     */
    public void setFavorites(Favorites value) {
      m_Favorites = value;
      update();
    }

    /**
     * Returns the underlying favorites.
     *
     * @return		the favorites in use
     */
    public Favorites getFavorites() {
      return m_Favorites;
    }

    /**
     * Updates the list.
     */
    public abstract void update();
  }

  /**
   * Table model class for displaying the superclasses.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SuperclassListModel
    extends AbstractFavoritesListModel {

    /** for serialization. */
    private static final long serialVersionUID = -744836869993575297L;

    /**
     * Initializes the model with no favorites.
     */
    public SuperclassListModel() {
      super();
    }

    /**
     * Initializes the model.
     *
     * @param fav	the favorites to use
     */
    public SuperclassListModel(Favorites fav) {
      super(fav);
    }

    /**
     * Updates the list.
     */
    @Override
    public void update() {
      Vector<String>	classes;
      int		i;

      clear();

      classes = m_Favorites.getSuperclasses();
      for (i = 0; i < classes.size(); i++)
	addElement(classes.get(i));
    }

    /**
     * Returns the underlying favorites.
     *
     * @return		the favorites in use
     */
    @Override
    public Favorites getFavorites() {
      return m_Favorites;
    }
  }

  /**
   * A list model for displaying the favorites of a specific superclass.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FavoritesListModel
    extends AbstractFavoritesListModel {

    /** for serialization. */
    private static final long serialVersionUID = -4331009712660382052L;

    /** the superclass to display. */
    protected Class m_Superclass;

    /**
     * Initializes the model with no favorites.
     */
    public FavoritesListModel() {
      super();
    }

    /**
     * Initializes the model.
     *
     * @param fav	the favorites to use
     */
    public FavoritesListModel(Favorites fav) {
      super(fav);

      setSuperclass((Class) null);
    }

    /**
     * Initializes the model.
     *
     * @param fav	the favorites to use
     * @param cls	the class to display
     */
    public FavoritesListModel(Favorites fav, Class cls) {
      super(fav);

      setSuperclass(cls);
    }

    /**
     * Sets the superclass to use.
     *
     * @param value	the superclass
     */
    public void setSuperclass(String value) {
      try {
	setSuperclass(Class.forName(value));
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    /**
     * Sets the superclass to use.
     *
     * @param value	the superclass
     */
    public void setSuperclass(Class value) {
      Vector<String>	classes;

      if (value == null) {
	classes = m_Favorites.getSuperclasses();
	if (classes.size() > 0) {
	  setSuperclass(classes.firstElement());
	  return;
	}
      }
      else {
	m_Superclass = value;
      }

      update();
    }

    /**
     * Returns the currently set superclass.
     *
     * @return		the superclass, can be null
     */
    public Class getSuperclass() {
      return m_Superclass;
    }

    /**
     * Updates the list.
     */
    @Override
    public void update() {
      Vector<Favorite>	favorites;
      int		i;

      clear();

      if (m_Superclass != null) {
	favorites = m_Favorites.getFavorites(m_Superclass);
	for (i = 0; i < favorites.size(); i++)
	  addElement(favorites.get(i).getName() + SEPARATOR + favorites.get(i).getCommandline());
      }
    }
    
    /**
     * Returns the name of the favorite at the specified location.
     * 
     * @param index	the list index of the favorite
     * @return		the name
     */
    public String getName(int index) {
      String	row;
      
      if ((index < 0) || (index >= size()))
	throw new IllegalArgumentException("Illegal index: " + index);
      
      row = getElementAt(index).toString();
      
      return row.substring(0, row.indexOf(SEPARATOR));
    }
  }

  /** the separator between name and commandline of favorite. */
  public final static String SEPARATOR = " | ";

  /** the favorites in use. */
  protected Favorites m_Favorites;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "save" menu item. */
  protected JMenuItem m_MenuItemSave;

  /** the "revert" menu item. */
  protected JMenuItem m_MenuItemRevert;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemClose;

  /** the panel for the superclasses. */
  protected BaseListWithButtons m_PanelSuperclasses;

  /** the button for adding a superclass. */
  protected JButton m_ButtonSuperclassAdd;

  /** the button for removing a superclass. */
  protected JButton m_ButtonSuperclassRemove;

  /** the button for removing all superclasses. */
  protected JButton m_ButtonSuperclassRemoveAll;

  /** the panel for the favorites. */
  protected BaseListWithButtons m_PanelFavorites;

  /** the button for adding a favorite. */
  protected JButton m_ButtonFavoriteAdd;

  /** the button for editing a favorite. */
  protected JButton m_ButtonFavoriteEdit;

  /** the button for renaming a favorite. */
  protected JButton m_ButtonFavoriteRename;

  /** the button for removing a favorite. */
  protected JButton m_ButtonFavoriteRemove;

  /** the button for removing all favorites. */
  protected JButton m_ButtonFavoriteRemoveAll;

  /** the list model for the superclasses. */
  protected SuperclassListModel m_ListModelSuperclass;

  /** the list model for the superclasses. */
  protected FavoritesListModel m_ListModelFavorites;

  /** the generic object editor for manipulating the favorites. */
  protected GenericObjectEditorDialog m_GOEDialog;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Favorites = Favorites.getSingleton().getClone();
    m_Favorites.setAutoSave(false);

    m_ListModelSuperclass = new SuperclassListModel(m_Favorites);
    m_ListModelFavorites  = new FavoritesListModel(m_Favorites);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new GridLayout(1, 2));

    // superclasses
    m_PanelSuperclasses = new BaseListWithButtons();
    m_PanelSuperclasses.setBorder(BorderFactory.createTitledBorder("Superclass"));
    m_PanelSuperclasses.setModel(m_ListModelSuperclass);
    m_PanelSuperclasses.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_PanelSuperclasses.getSelectedIndices().length == 1)
	  m_ListModelFavorites.setSuperclass((String) m_PanelSuperclasses.getSelectedValue());
	update();
      }
    });
    add(m_PanelSuperclasses);

    m_ButtonSuperclassAdd = new JButton("Add...");
    m_ButtonSuperclassAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addSuperclass();
	update();
      }
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassAdd);

    m_ButtonSuperclassRemove = new JButton("Remove");
    m_ButtonSuperclassRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	Object[] classes = m_PanelSuperclasses.getSelectedValues();
	for (int i = 0; i < classes.length; i++)
	  m_Favorites.removeFavorites(classes[i].toString());
	m_ListModelSuperclass.update();
	m_ListModelFavorites.update();
	update();
      }
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassRemove);

    m_ButtonSuperclassRemoveAll = new JButton("Remove all");
    m_ButtonSuperclassRemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Favorites.clear();
	m_ListModelFavorites.setSuperclass((Class) null);
	m_ListModelSuperclass.update();
	update();
      }
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassRemoveAll);

    // favorites
    m_PanelFavorites = new BaseListWithButtons();
    m_PanelFavorites.setBorder(BorderFactory.createTitledBorder("Favorites"));
    m_PanelFavorites.setModel(m_ListModelFavorites);
    m_PanelFavorites.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	update();
      }
    });
    add(m_PanelFavorites);

    m_ButtonFavoriteAdd = new JButton("Add...");
    m_ButtonFavoriteAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addFavorite(m_ListModelFavorites.getSuperclass());
	update();
      }
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteAdd);

    m_ButtonFavoriteEdit = new JButton("Edit...");
    m_ButtonFavoriteEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	editFavorite(m_ListModelFavorites.getSuperclass(), (String) m_PanelFavorites.getSelectedValue());
	update();
      }
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteEdit);
    m_PanelFavorites.setDoubleClickButton(m_ButtonFavoriteEdit);

    m_ButtonFavoriteRename = new JButton("Rename...");
    m_ButtonFavoriteRename.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	renameFavorite(m_ListModelFavorites.getSuperclass(), (String) m_PanelFavorites.getSelectedValue());
	update();
      }
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteRename);

    m_ButtonFavoriteRemove = new JButton("Remove");
    m_ButtonFavoriteRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ListModelFavorites.getSuperclass() == null)
	  return;
	int[] indices = m_PanelFavorites.getSelectedIndices();
	for (int i = 0; i < indices.length; i++)
	  m_Favorites.removeFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.getName(indices[i]));
	m_ListModelSuperclass.update();
	m_ListModelFavorites.update();
	update();
      }
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteRemove);

    m_ButtonFavoriteRemoveAll = new JButton("Remove all");
    m_ButtonFavoriteRemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ListModelFavorites.getSuperclass() == null)
	  return;
	m_Favorites.removeFavorites(m_ListModelFavorites.getSuperclass());
	m_ListModelSuperclass.update();
	m_ListModelFavorites.update();
	update();
      }
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteRemoveAll);

    update();
  }

  /**
   * Returns the GOE editor dialog to use.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog getGOEEditor() {
    if (m_GOEDialog == null) {
      if (getParentDialog() != null)
	m_GOEDialog = new GenericObjectEditorDialog(getParentDialog());
      else
	m_GOEDialog = new GenericObjectEditorDialog(getParentFrame());
      m_GOEDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
      m_GOEDialog.getGOEEditor().setCanChangeClassInDialog(true);
    }

    return m_GOEDialog;
  }

  /**
   * Adds a new superclass for favorites. Automatically pops up dialog for
   * adding a favorite.
   */
  protected void addSuperclass() {
    BaseDialog		dialog;
    BasePanel		panel;
    BasePanel		panelBox;
    JButton		buttonOK;
    JButton		buttonCancel;
    JLabel		label;
    Vector<String>	superclasses;

    if (getParentFrame() != null)
      dialog = new BaseDialog(getParentFrame(), true);
    else
      dialog = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Add superclass");
    dialog.getContentPane().setLayout(new BorderLayout());

    // combobox
    superclasses = new Vector<String>(ClassLister.getSingleton().getClasses().keySetAll());
    Collections.sort(superclasses);
    panelBox = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    final JComboBox combobox = new JComboBox(superclasses);
    label = new JLabel("Superclass");
    label.setDisplayedMnemonic('S');
    label.setLabelFor(combobox);
    panelBox.add(label);
    panelBox.add(combobox);
    dialog.getContentPane().add(panelBox, BorderLayout.CENTER);

    // buttons
    final BaseDialog dialogF = dialog;
    panel = new BasePanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panel, BorderLayout.SOUTH);
    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	try {
	  Class cls = Class.forName((String) combobox.getSelectedItem());
	  m_ListModelFavorites.setSuperclass(cls);
	  dialogF.setVisible(false);
	  addFavorite(cls);
	}
	catch (Exception ex) {
	  dialogF.setVisible(false);
	  ex.printStackTrace();
	  GUIHelper.showErrorMessage(
	      FavoritesManagementPanel.this,
	      "Error encountered:\n" + ex.toString());
	}
      }
    });
    panel.add(buttonOK);
    buttonCancel = new JButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	dialogF.setVisible(false);
      }
    });
    panel.add(buttonCancel);

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Pops up dialog for adding a new favorite.
   *
   * @param cls		the superclass the favorite is for
   */
  protected void addFavorite(Class cls) {
    String			name;
    GenericObjectEditorDialog	dialog;
    Class			subcls;
    String[]			classes;
    Object			obj;

    // get all available classes
    classes = ClassLister.getSingleton().getClassnames(cls);
    if (classes.length == 0) {
      GUIHelper.showErrorMessage(
	  this, "No classes available for superclass " + cls.getName());
      return;
    }

    // try to instantiate 1st class
    obj = null;
    try {
      subcls = Class.forName(classes[0]);
      obj    = subcls.newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  this, "Failed to instantiate class " + classes[0] + ":\n" + e.toString());
      return;
    }

    // setup favorite
    dialog = getGOEEditor();
    dialog.setTitle("Add Favorite (" + cls.getName() + ")");
    dialog.getGOEEditor().setClassType(cls);
    dialog.getGOEEditor().setValue(obj);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    // name favorite
    name = GUIHelper.showInputDialog(this, "Please input name for favorite:");
    if (name == null)
      return;

    // add favorite
    m_Favorites.addFavorite(cls, dialog.getCurrent(), name);
    m_ListModelSuperclass.update();
    m_ListModelFavorites.update();
  }

  /**
   * Pops up a dialog for editing a favorite.
   *
   * @param cls		the superclass of the favorite
   * @param entry	the entry in the list
   */
  protected void editFavorite(Class cls, String entry) {
    String			name;
    Favorite			favorite;
    GenericObjectEditorDialog	dialog;

    name     = entry.substring(0, entry.indexOf(SEPARATOR));
    favorite = m_Favorites.getFavorite(cls, name);
    if (favorite == null)
      return;

    // edit favorite
    dialog = getGOEEditor();
    dialog.setTitle("Edit Favorite (" + name + ")");
    dialog.getGOEEditor().setClassType(cls);
    dialog.setCurrent(favorite.getObject());
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    // update favorite
    m_Favorites.addFavorite(cls, dialog.getCurrent(), name);
    m_ListModelSuperclass.update();
    m_ListModelFavorites.update();
  }

  /**
   * Pops up a dialog for renaming a favorite.
   *
   * @param cls		the superclass of the favorite
   * @param entry	the entry in the list
   */
  protected void renameFavorite(Class cls, String entry) {
    String			name;
    String			newName;
    Favorite			favorite;

    name     = entry.substring(0, entry.indexOf(SEPARATOR));
    favorite = m_Favorites.getFavorite(cls, name);

    // input new name
    newName = GUIHelper.showInputDialog(this, "Please input new name:", name);
    if (newName == null)
      return;

    // rename
    m_Favorites.addFavorite(cls, favorite.getObject(), newName);
    m_Favorites.removeFavorite(cls, name);
    m_ListModelFavorites.update();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      // register window listener since we're part of a dialog or frame
      if (getParentFrame() != null) {
	final JFrame frame = (JFrame) getParentFrame();
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }
      else if (getParentDialog() != null) {
	final JDialog dialog = (JDialog) getParentDialog();
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	dialog.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }

      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  save();
	}
      });
      m_MenuItemSave = menuitem;

      // File/Revert
      menuitem = new JMenuItem("Revert");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  revert();
	}
      });
      m_MenuItemRevert = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });
      m_MenuItemClose = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    title = "Favorites management";
    if (m_Favorites.isModified())
      title = "*" + title;

    setParentTitle(title);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    updateTitle();

    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemSave.setEnabled(m_Favorites.isModified());
    m_MenuItemRevert.setEnabled(m_Favorites.isModified());
    m_MenuItemClose.setEnabled(true);
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    // superclass
    m_ButtonSuperclassAdd.setEnabled(true);
    m_ButtonSuperclassRemove.setEnabled(m_PanelSuperclasses.getSelectedIndices().length > 0);
    m_ButtonSuperclassRemoveAll.setEnabled(m_PanelSuperclasses.getModel().getSize() > 0);

    // favorites
    m_ButtonFavoriteAdd.setEnabled(true);
    m_ButtonFavoriteEdit.setEnabled(m_PanelFavorites.getSelectedIndices().length == 1);
    m_ButtonFavoriteRename.setEnabled(m_PanelFavorites.getSelectedIndices().length == 1);
    m_ButtonFavoriteRemove.setEnabled(m_PanelFavorites.getSelectedIndices().length > 0);
    m_ButtonFavoriteRemoveAll.setEnabled(m_PanelFavorites.getModel().getSize() > 0);
  }

  /**
   * Updates menu, buttons and title.
   */
  protected void update() {
    updateMenu();
    updateTitle();
    updateButtons();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the placeholders or discarded the changes.
   * In case of custom placeholdersm the use will never get asked.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;

    result = !m_Favorites.isModified();

    if (!result) {
      retVal = GUIHelper.showConfirmMessage(
	  this,
	  "Favorites are modified - save?",
	  "Favorites modified");

      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  save();
	  result = !m_Favorites.isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Saves the current favorites.
   */
  protected void save() {
    if (!m_Favorites.updateFavorites()) {
      GUIHelper.showErrorMessage(
	  this,
	  "Error saving favorites to '" + m_Favorites.getFilename() + "'!",
	  "Error saving favorites");
    }
    else {
      Favorites.reload();
      GUIHelper.showInformationMessage(
	  this,
	  "Favorites have been successfully saved to '" + m_Favorites.getFilename() + "'!",
	  "Favorites saved");
    }

    update();
  }

  /**
   * Reverts the changes.
   */
  protected void revert() {
    m_Favorites = Favorites.getSingleton().getClone();
    m_Favorites.setAutoSave(false);
    m_ListModelSuperclass.setFavorites(m_Favorites);
    m_ListModelFavorites.setFavorites(m_Favorites);

    update();
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    if (!checkForModified()) {
      if (getParentDialog() != null)
	getParentDialog().setVisible(true);
      else if (getParentFrame() != null)
	getParentFrame().setVisible(true);
      return;
    }

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }
}
