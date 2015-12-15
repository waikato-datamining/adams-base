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
 * SendToActionUtils.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import adams.core.ClassLister;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.gui.core.GUIHelper;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Helper class for SendTo actions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendToActionUtils {

  /** the default menu item text. */
  public final static String MENUITEM_SENDTO = "Send to";

  /** the cache for sendto actions (accepted class - available actions). */
  protected static Hashtable<Class, ArrayList<Class>> m_AcceptedCache;

  /** the counter for temporary files. */
  protected static int m_Counter;
  static {
    m_Counter = 0;
  }

  /**
   * Returns the cache of what sendto actions can accept what classes.
   *
   * @return		the cache
   */
  protected static synchronized Hashtable<Class, ArrayList<Class>> getAcceptedCache() {
    String[]			classes;
    AbstractSendToAction	sendto;
    Class[]			accepted;

    if (m_AcceptedCache == null) {
      m_AcceptedCache = new Hashtable<Class, ArrayList<Class>>();
      classes = ClassLister.getSingleton().getClassnames(AbstractSendToAction.class);
      for (String cls: classes) {
	try {
	  sendto   = (AbstractSendToAction) Class.forName(cls).newInstance();
	  accepted = sendto.accepts();
	  for (Class accept: accepted) {
	    if (!m_AcceptedCache.containsKey(accept))
	      m_AcceptedCache.put(accept, new ArrayList<Class>());
	    m_AcceptedCache.get(accept).add(sendto.getClass());
	  }
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate sendto action '" + cls + "':");
	  e.printStackTrace();
	}
      }
    }

    return m_AcceptedCache;
  }

  /**
   * Returns all the actions that can handle the specified class.
   *
   * @param owner	the owner
   * @return		the available actions
   */
  public static AbstractSendToAction[] getActions(final SendToActionSupporter owner) {
    ArrayList<AbstractSendToAction>	result;
    ArrayList<Class>			classes;
    Class[]				cls;
    int					i;
    AbstractSendToAction		action;

    result = new ArrayList<AbstractSendToAction>();
    cls    = owner.getSendToClasses();
    for (i = 0; i < cls.length; i++) {
      classes = getAcceptedCache().get(cls[i]);
      if (classes != null) {
	for (Class c: classes) {
	  try {
	    action = (AbstractSendToAction) c.newInstance();
	    if (!result.contains(action)) {
	      action.setOwner(owner);
	      result.add(action);
	    }
	  }
	  catch (Exception e) {
	    System.err.println("Failed to instantiate sendto action '" + c.getName() + "':");
	    e.printStackTrace();
	  }
	}
      }
    }

    if (result.size() > 1)
      Collections.sort(result);

    return result.toArray(new AbstractSendToAction[result.size()]);
  }

  /**
   * Adds all the available sendto actions for the specified class to the 
   * submenu.
   *
   * @param owner	the owner to get the item to send from
   * @param submenu	the menu to add the "Send to" submenu to if available
   * @return		true if submenu was added
   */
  protected static boolean createSubmenu(final SendToActionSupporter owner, final JMenu submenu) {
    AbstractSendToAction[]	actions;

    submenu.removeAll();
    actions = getActions(owner);

    for (final AbstractSendToAction action: actions) {
      final JMenuItem menuitem = new JMenuItem(action.getAction());
      menuitem.setIcon(action.getIcon());
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Component parent = null;
          if (owner instanceof Component)
            parent = (Component) owner;
          Object item = owner.getSendToItem(action.accepts());
          if (item == null) {
            GUIHelper.showInformationMessage(parent, "Nothing to send right now!");
            return;
          }
          String error = action.send(item);
          if (error != null)
            GUIHelper.showErrorMessage(parent, error);
        }
      });
      submenu.add(menuitem);
    }

    return true;
  }

  /**
   * Attaches a listener to the top-level menu that triggers the generation 
   * of the send-to submenu.
   *
   * @param owner	the owner to get the item to send from
   * @param submenu	the menu to add the "Send to" submenu to if available
   * @return		true if submenu was added
   */
  protected static boolean addSubmenu(final SendToActionSupporter owner, final JMenu submenu) {
    final JMenu		topLevelMenu;

    topLevelMenu = GUIHelper.getTopLevelMenu(submenu);
    // popup menu?
    if (topLevelMenu == null) {
      createSubmenu(owner, submenu);
    }
    else {      
      topLevelMenu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  createSubmenu(owner, submenu);
	}
      });
    }

    return true;
  }

  /**
   * Adds all the available sendto actions for the specified class to the
   *
   * @param owner	the owner to get the item to send from
   * @param menu	the menu to add the "Send to" submenu to if available
   * @return		true if submenu was added
   */
  public static boolean addSendToSubmenu(final SendToActionSupporter owner, JPopupMenu menu) {
    JMenu			submenu;
    AbstractSendToAction[]	actions;

    actions = getActions(owner);
    if (actions.length == 0)
      return false;

    submenu = new JMenu(MENUITEM_SENDTO);
    menu.add(submenu);

    return addSubmenu(owner, submenu);
  }

  /**
   * Adds all the available sendto actions for the specified class to the
   *
   * @param owner	the owner to get the item to send from
   * @param menu	the menu to add the "Send to" submenu to if available
   * @return		true if submenu was added
   */
  public static boolean addSendToSubmenu(final SendToActionSupporter owner, JMenu menu) {
    JMenu			submenu;
    AbstractSendToAction[]	actions;

    actions = getActions(owner);
    if (actions.length == 0)
      return false;

    submenu = new JMenu(MENUITEM_SENDTO);
    submenu.setIcon(GUIHelper.getIcon("sendto.png"));
    menu.add(submenu);

    return addSubmenu(owner, submenu);
  }

  /**
   * Generates a new temporary file, which will get deleted when the JVM exits.
   *
   * @param prefix	the prefix for the filename (within the TMP directory)
   * @param extension	the file extension to use (without dot!)
   * @return		the generated file
   */
  public static synchronized PlaceholderFile nextTmpFile(String prefix, String extension) {
    PlaceholderFile	result;
    String		tmp;
    File		file;

    tmp = TempUtils.getTempDirectory().getAbsolutePath();
    do {
      m_Counter++;
      file = new File(tmp + File.separator + prefix + m_Counter + "." + extension);
    }
    while (file.exists());

    result = new PlaceholderFile(file);
    result.deleteOnExit();

    return result;
  }

  /**
   * Checks whether a class (needle) is among a list of classes (haystack).
   *
   * @param needle	the class to look for
   * @param haystack	the available classes
   */
  public static boolean isAvailable(Class needle, Class[] haystack) {
    boolean	result;

    result = false;

    for (Class c: haystack) {
      if (c.equals(needle)) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether at least one class (of the needles) is among a list of
   * classes (haystack).
   *
   * @param needles	the classes to look for
   * @param haystack	the available classes
   */
  public static boolean isAvailable(Class[] needles, Class[] haystack) {
    boolean	result;

    result = false;

    for (Class needle: needles) {
      result = isAvailable(needle, haystack);
      if (result)
	break;
    }

    return result;
  }
}
