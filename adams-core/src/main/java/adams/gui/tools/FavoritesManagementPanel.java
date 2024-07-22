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
 * FavoritesManagementPanel.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.goe.Favorites;
import adams.gui.goe.Favorites.Favorite;
import adams.gui.goe.GenericArrayEditorDialog;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * A panel for managing one's GOE favorites.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
      List<String>	classes;
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
   */
  public static class FavoritesListModel
    extends AbstractFavoritesListModel {

    /** for serialization. */
    private static final long serialVersionUID = -4331009712660382052L;

    /** the superclass to display. */
    protected Class m_Superclass;

    /** whether we are managing arrays. */
    protected boolean m_Array;

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

      setSuperclass((Class) null, false);
    }

    /**
     * Initializes the model.
     *
     * @param fav	the favorites to use
     * @param cls	the class to display
     * @param array 	whether this is for arrays
     */
    public FavoritesListModel(Favorites fav, Class cls, boolean array) {
      super(fav);

      setSuperclass(cls, array);
    }

    /**
     * Sets the superclass to use.
     *
     * @param value	the superclass, can have trailing [] to denote arrays
     */
    public void setSuperclass(String value) {
      if (value.endsWith("[]"))
	setSuperclass(value.substring(0, value.length() - 2), true);
      else
	setSuperclass(value, false);
    }

    /**
     * Sets the superclass to use.
     *
     * @param value	the superclass
     * @param array 	whether this is for arrays
     */
    public void setSuperclass(String value, boolean array) {
      try {
	setSuperclass(ClassManager.getSingleton().forName(value), array);
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    /**
     * Sets the superclass to use.
     *
     * @param value	the superclass
     * @param array 	whether this is for arrays
     */
    public void setSuperclass(Class value, boolean array) {
      List<String>	classes;

      if (value == null) {
	classes = m_Favorites.getSuperclasses();
	if (!classes.isEmpty()) {
	  setSuperclass(classes.get(0), array);
	  return;
	}
      }
      else {
	m_Superclass = value;
	m_Array      = array;
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
     * Returns whether this is for arrays.
     *
     * @return		true if for arrays
     */
    public boolean isArray() {
      return m_Array;
    }

    /**
     * Updates the list.
     */
    @Override
    public void update() {
      List<Favorite>	favorites;
      int		i;
      Class		favClass;

      clear();

      if (m_Superclass != null) {
	if (m_Array)
	  favClass = Array.newInstance(m_Superclass, 0).getClass();
	else
	  favClass = m_Superclass;
	favorites = m_Favorites.getFavorites(favClass);
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
  protected BaseButton m_ButtonSuperclassAdd;

  /** the button for removing a superclass. */
  protected BaseButton m_ButtonSuperclassRemove;

  /** the button for removing all superclasses. */
  protected BaseButton m_ButtonSuperclassRemoveAll;

  /** the panel for the favorites. */
  protected BaseListWithButtons m_PanelFavorites;

  /** the button for adding a favorite. */
  protected BaseButton m_ButtonFavoriteAdd;

  /** the button for editing a favorite. */
  protected BaseButton m_ButtonFavoriteEdit;

  /** the button for renaming a favorite. */
  protected BaseButton m_ButtonFavoriteRename;

  /** the button for removing a favorite. */
  protected BaseButton m_ButtonFavoriteRemove;

  /** the button for removing all favorites. */
  protected BaseButton m_ButtonFavoriteRemoveAll;

  /** the list model for the superclasses. */
  protected SuperclassListModel m_ListModelSuperclass;

  /** the list model for the superclasses. */
  protected FavoritesListModel m_ListModelFavorites;

  /** the generic object editor for manipulating the favorites. */
  protected GenericObjectEditorDialog m_GOEDialog;

  /** the generic array editor for manipulating the favorites. */
  protected GenericArrayEditorDialog m_GAEDialog;

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
    m_PanelSuperclasses.addListSelectionListener((ListSelectionEvent e) -> {
      if (m_PanelSuperclasses.getSelectedIndices().length == 1)
	m_ListModelFavorites.setSuperclass((String) m_PanelSuperclasses.getSelectedValue());
      update();
    });
    add(m_PanelSuperclasses);

    m_ButtonSuperclassAdd = new BaseButton("Add...");
    m_ButtonSuperclassAdd.addActionListener((ActionEvent e) -> {
      addSuperclass();
      update();
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassAdd);

    m_ButtonSuperclassRemove = new BaseButton("Remove");
    m_ButtonSuperclassRemove.addActionListener((ActionEvent e) -> {
      Object[] classes = m_PanelSuperclasses.getSelectedValuesList().toArray();
      for (int i = 0; i < classes.length; i++)
	m_Favorites.removeFavorites(classes[i].toString());
      m_ListModelSuperclass.update();
      m_ListModelFavorites.update();
      update();
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassRemove);

    m_ButtonSuperclassRemoveAll = new BaseButton("Remove all");
    m_ButtonSuperclassRemoveAll.addActionListener((ActionEvent e) -> {
      m_Favorites.clear();
      m_ListModelFavorites.setSuperclass((Class) null, false);
      m_ListModelSuperclass.update();
      update();
    });
    m_PanelSuperclasses.addToButtonsPanel(m_ButtonSuperclassRemoveAll);

    // favorites
    m_PanelFavorites = new BaseListWithButtons();
    m_PanelFavorites.setBorder(BorderFactory.createTitledBorder("Favorites"));
    m_PanelFavorites.setModel(m_ListModelFavorites);
    m_PanelFavorites.addListSelectionListener((ListSelectionEvent e) -> update());
    add(m_PanelFavorites);

    m_ButtonFavoriteAdd = new BaseButton("Add...");
    m_ButtonFavoriteAdd.addActionListener((ActionEvent e) -> {
      addFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray());
      update();
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteAdd);

    m_ButtonFavoriteEdit = new BaseButton("Edit...");
    m_ButtonFavoriteEdit.addActionListener((ActionEvent e) -> {
      editFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray(), (String) m_PanelFavorites.getSelectedValue());
      update();
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteEdit);
    m_PanelFavorites.setDoubleClickButton(m_ButtonFavoriteEdit);

    m_ButtonFavoriteRename = new BaseButton("Rename...");
    m_ButtonFavoriteRename.addActionListener((ActionEvent e) -> {
      renameFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray(), (String) m_PanelFavorites.getSelectedValue());
      update();
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteRename);

    m_ButtonFavoriteRemove = new BaseButton("Remove");
    m_ButtonFavoriteRemove.addActionListener((ActionEvent e) -> {
      if (m_ListModelFavorites.getSuperclass() == null)
	return;
      int[] indices = m_PanelFavorites.getSelectedIndices();
      for (int i = 0; i < indices.length; i++)
	m_Favorites.removeFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray(), m_ListModelFavorites.getName(indices[i]));
      m_ListModelSuperclass.update();
      m_ListModelFavorites.update();
      update();
    });
    m_PanelFavorites.addToButtonsPanel(m_ButtonFavoriteRemove);

    m_ButtonFavoriteRemoveAll = new BaseButton("Remove all");
    m_ButtonFavoriteRemoveAll.addActionListener((ActionEvent e) -> {
      if (m_ListModelFavorites.getSuperclass() == null)
	return;
      m_Favorites.removeFavorites(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray());
      m_ListModelSuperclass.update();
      m_ListModelFavorites.update();
      update();
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
   * Returns the GAE editor dialog to use.
   *
   * @return		the dialog
   */
  protected GenericArrayEditorDialog getGAEEditor() {
    if (m_GAEDialog == null) {
      if (getParentDialog() != null)
	m_GAEDialog = new GenericArrayEditorDialog(getParentDialog());
      else
	m_GAEDialog = new GenericArrayEditorDialog(getParentFrame());
      m_GAEDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    }

    return m_GAEDialog;
  }

  /**
   * Adds a new superclass for favorites. Automatically pops up dialog for
   * adding a favorite.
   */
  protected void addSuperclass() {
    BaseDialog			dialog;
    BasePanel			panel;
    BasePanel 			panelClasses;
    BasePanel			panelArray;
    BasePanel			panelParams;
    BaseButton			buttonOK;
    BaseButton			buttonCancel;
    JLabel			label;
    String[] 			superclasses;
    final BaseComboBox<String> 	comboBoxClasses;
    BaseCheckBox		checkBoxArray;

    if (getParentFrame() != null)
      dialog = new BaseDialog(getParentFrame(), true);
    else
      dialog = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Add superclass");
    dialog.getContentPane().setLayout(new BorderLayout());

    panelParams = new BasePanel(new GridLayout(2, 1));
    dialog.getContentPane().add(panelParams, BorderLayout.CENTER);

    // classes
    superclasses = ClassLister.getSingleton().getSuperclasses();
    Arrays.sort(superclasses);
    panelClasses = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    comboBoxClasses = new BaseComboBox<>(superclasses);
    label = new JLabel("Superclass");
    label.setDisplayedMnemonic('S');
    label.setLabelFor(comboBoxClasses);
    panelClasses.add(label);
    panelClasses.add(comboBoxClasses);
    panelParams.add(panelClasses, BorderLayout.CENTER);

    // array?
    panelArray = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    checkBoxArray = new BaseCheckBox();
    label = new JLabel("Array?");
    label.setDisplayedMnemonic('A');
    label.setLabelFor(checkBoxArray);
    panelArray.add(label);
    panelArray.add(checkBoxArray);
    panelParams.add(panelArray, BorderLayout.CENTER);

    // buttons
    final BaseDialog dialogF = dialog;
    panel = new BasePanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panel, BorderLayout.SOUTH);
    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener((ActionEvent e) -> {
      try {
	m_ListModelFavorites.setSuperclass(comboBoxClasses.getSelectedItem(), checkBoxArray.isSelected());
	dialogF.setVisible(false);
	addFavorite(m_ListModelFavorites.getSuperclass(), m_ListModelFavorites.isArray());
      }
      catch (Exception ex) {
	dialogF.setVisible(false);
	ex.printStackTrace();
	GUIHelper.showErrorMessage(
	  FavoritesManagementPanel.this,
	  "Error encountered:\n" + ex);
      }
    });
    panel.add(buttonOK);
    buttonCancel = new BaseButton("Cancel");
    buttonCancel.setMnemonic('C');
    buttonCancel.addActionListener((ActionEvent e) -> dialogF.setVisible(false));
    panel.add(buttonCancel);

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Pops up dialog for adding a new favorite.
   *
   * @param cls		the superclass the favorite is for
   * @param array 	whether this for an array
   */
  protected void addFavorite(Class cls, boolean array) {
    String			name;
    GenericArrayEditorDialog	gae;
    GenericObjectEditorDialog 	goe;
    Class			subcls;
    String[]			classes;
    Object			obj;
    Object			current;

    // get all available classes
    classes = ClassLister.getSingleton().getClassnames(cls);
    if (classes.length == 0) {
      GUIHelper.showErrorMessage(
	  this, "No classes available for superclass " + cls.getName());
      return;
    }

    if (array) {
      obj = Array.newInstance(cls, 0);
      gae = getGAEEditor();
      gae.setTitle("Add Favorite (" + cls.getName() + ")");
      gae.setCurrent(obj);
      gae.pack();
      gae.setLocationRelativeTo(this);
      gae.setVisible(true);
      if (gae.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;
      current = gae.getCurrent();
    }
    else {
      // try to instantiate 1st class
      try {
	subcls = ClassManager.getSingleton().forName(classes[0]);
	obj    = subcls.getDeclaredConstructor().newInstance();
      }
      catch (Exception e) {
	e.printStackTrace();
	GUIHelper.showErrorMessage(
	  this, "Failed to instantiate class " + classes[0] + ":\n" + e);
	return;
      }
      goe = getGOEEditor();
      goe.setTitle("Add Favorite (" + cls.getName() + ")");
      goe.getGOEEditor().setClassType(cls);
      goe.getGOEEditor().setValue(obj);
      goe.pack();
      goe.setLocationRelativeTo(this);
      goe.setVisible(true);
      if (goe.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;
      current = goe.getCurrent();
    }

    // name favorite
    name = GUIHelper.showInputDialog(this, "Please input name for favorite:");
    if (name == null)
      return;

    // add favorite
    m_Favorites.addFavorite(cls, array, current, name);
    m_ListModelSuperclass.update();
    m_ListModelFavorites.update();
  }

  /**
   * Pops up a dialog for editing a favorite.
   *
   * @param cls		the superclass of the favorite
   * @param array 	whether this for an array
   * @param entry	the entry in the list
   */
  protected void editFavorite(Class cls, boolean array, String entry) {
    String			name;
    Favorite			favorite;
    GenericObjectEditorDialog 	goe;
    GenericArrayEditorDialog	gae;
    Object			current;

    name     = entry.substring(0, entry.indexOf(SEPARATOR));
    favorite = m_Favorites.getFavorite(cls, array, name);
    if (favorite == null)
      return;

    // edit favorite
    if (array) {
      gae = getGAEEditor();
      gae.setCurrent(favorite.getObject());
      gae.pack();
      gae.setLocationRelativeTo(this);
      gae.setVisible(true);
      if (gae.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;
      current = gae.getCurrent();
    }
    else {
      goe = getGOEEditor();
      goe.setTitle("Edit Favorite (" + name + ")");
      goe.getGOEEditor().setClassType(cls);
      goe.setCurrent(favorite.getObject());
      goe.pack();
      goe.setLocationRelativeTo(this);
      goe.setVisible(true);
      if (goe.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;
      current = goe.getCurrent();
    }

    // update favorite
    m_Favorites.addFavorite(cls, array, current, name);
    m_ListModelSuperclass.update();
    m_ListModelFavorites.update();
  }

  /**
   * Pops up a dialog for renaming a favorite.
   *
   * @param cls		the superclass of the favorite
   * @param array 	whether for an array
   * @param entry	the entry in the list
   */
  protected void renameFavorite(Class cls, boolean array, String entry) {
    String			name;
    String			newName;
    Favorite			favorite;

    name     = entry.substring(0, entry.indexOf(SEPARATOR));
    favorite = m_Favorites.getFavorite(cls, array, name);

    // input new name
    newName = GUIHelper.showInputDialog(this, "Please input new name:", name);
    if (newName == null)
      return;

    // rename
    m_Favorites.addFavorite(cls, array, favorite.getObject(), newName);
    m_Favorites.removeFavorite(cls, array, name);
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
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(ImageManager.getIcon("save.gif"));
      menuitem.addActionListener((ActionEvent e) -> save());
      m_MenuItemSave = menuitem;

      // File/Revert
      menuitem = new JMenuItem("Revert");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setIcon(ImageManager.getIcon("revert.png"));
      menuitem.addActionListener((ActionEvent e) -> revert());
      m_MenuItemRevert = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> close());
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
