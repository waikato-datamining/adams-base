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
 * WorkspaceHelper.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.gui.explorer.Explorer.ExplorerPanel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Helper class for loading/saving workspaces.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WorkspaceHelper {

  /**
   * the additional associations between {@link ExplorerPanel} and
   * {@link AbstractExplorerPanelHandler}.
   */
  protected static HashMap<Class, AbstractExplorerPanelHandler> m_AdditionalHandlers;

  static {
    m_AdditionalHandlers = new HashMap<Class, AbstractExplorerPanelHandler>();
  }

  /**
   * Registers an additional handler for an {@link ExplorerPanel}.
   *
   * @param explorerPanel the panel to register the handler for
   * @param handler       the handler to register
   */
  public static void registerAdditionalHandler(Class explorerPanel, AbstractExplorerPanelHandler handler) {
    if (!ClassLocator.hasInterface(ExplorerPanel.class, explorerPanel))
      throw new IllegalArgumentException(
	"Panel class '" + explorerPanel.getName() + "' does not implement '" + ExplorerPanel.class.getName() + "'!");
    m_AdditionalHandlers.put(explorerPanel, handler);
  }

  /**
   * Creates a filechooser for loading/saving workspaces.
   *
   * @return the filechooser
   */
  public static BaseFileChooser newFileChooser() {
    BaseFileChooser 	result;
    ExtensionFileFilter filter;

    result = new BaseFileChooser();
    filter = new ExtensionFileFilter("Explorer workspace", "ews");
    result.addChoosableFileFilter(filter);
    result.setDefaultExtension("ews");
    result.setFileFilter(filter);
    result.setAutoAppendExtension(true);
    result.setFileSelectionMode(BaseFileChooser.FILES_ONLY);
    result.setMultiSelectionEnabled(false);

    return result;
  }

  /**
   * Returns all available handlers, with the {@link DefaultHandler} being the last one.
   *
   * @throws Exception if instantiation of handlers fails
   * @return the handlers
   */
  protected static AbstractExplorerPanelHandler[] getHandlers() throws Exception {
    int 					i;
    int 					def;
    String[] 					cnames;
    ArrayList<AbstractExplorerPanelHandler> 	handlers;
    AbstractExplorerPanelHandler 		handler;

    cnames = AbstractExplorerPanelHandler.getHandlers();
    handlers = new ArrayList<AbstractExplorerPanelHandler>();
    def = -1;
    for (i = 0; i < cnames.length; i++) {
      if (cnames[i].equals(DefaultHandler.class.getName()))
	def = i;
      handlers.add((AbstractExplorerPanelHandler) Class.forName(cnames[i]).newInstance());
    }

    if ((def != -1) && (handlers.size() > 1)) {
      handler = handlers.remove(def);
      handlers.add(handler);
    }

    return handlers.toArray(new AbstractExplorerPanelHandler[handlers.size()]);
  }

  /**
   * Obtains the options from the explorer to be saved in the workspace.
   *
   * @param explorer the explorer to extract the options from
   * @return the options
   */
  protected static Hashtable<String, Object> getExplorerOptions(Explorer explorer) {
    Hashtable<String, Object> 	result;

    result = new Hashtable<String, Object>();

    // reserved for future use

    return result;
  }

  /**
   * Serializes the explorer instance to the output stream.
   *
   * @param expext	the explorer to serialize
   * @param name	the name of the explorer
   * @param oos		the output stream
   * @param skipHistory	whether to skip copying the history
   * @throws Exception	if serialization fails
   */
  protected static void serialize(ExplorerExt expext, String name, ObjectOutputStream oos, boolean skipHistory) throws Exception {
    AbstractExplorerPanelHandler[] 	handlers;
    ArrayList<ExplorerPanel> 		panels;

    handlers = getHandlers();

    oos.writeObject(name);
    oos.writeObject(getExplorerOptions(expext));
    oos.writeObject(expext.getPanels().size() + 1);

    panels = new ArrayList<ExplorerPanel>();
    panels.add(expext.getPreprocessPanel());
    panels.addAll(expext.getPanels());
    for (ExplorerPanel panel : panels) {
      if (m_AdditionalHandlers.containsKey(panel.getClass())) {
	AbstractExplorerPanelHandler handler = m_AdditionalHandlers.get(panel.getClass());
	handler.setSkipHistory(skipHistory);
	oos.writeObject(panel.getClass().getName());
	oos.writeObject(handler.getClass().getName());
	oos.writeObject(handler.serialize(panel));
      }
      else {
	for (AbstractExplorerPanelHandler handler : handlers) {
	  if (handler.handles(panel)) {
	    handler.setSkipHistory(skipHistory);
	    oos.writeObject(panel.getClass().getName());
	    oos.writeObject(handler.getClass().getName());
	    oos.writeObject(handler.serialize(panel));
	    break;
	  }
	}
      }
    }
  }

  /**
   * Saves the explorer session to the given file.
   *
   * @param explorer the explorer to save
   * @param file     the file to save the workspace to
   * @throws Exception if saving fails
   */
  public static void write(MultiExplorer explorer, File file) throws Exception {
    ObjectOutputStream 			oos;
    FileOutputStream 			fos;
    int 				i;
    ExplorerExt 			expext;
    String 				name;

    fos = new FileOutputStream(file);
    oos = new ObjectOutputStream(new BufferedOutputStream(fos));

    oos.writeObject(explorer.getEntryPanel().count());
    for (i = 0; i < explorer.getEntryPanel().count(); i++) {
      name = explorer.getEntryPanel().getEntryName(i);
      expext = explorer.getEntryPanel().getEntry(i);
      serialize(expext, name, oos, false);
    }

    FileUtils.closeQuietly(oos);
    FileUtils.closeQuietly(fos);
  }

  /**
   * Restores the Explorer options from the hashtable.
   *
   * @param explorer the explorer to restore
   * @param options  the settings of the session
   */
  protected static void setExplorerOptions(Explorer explorer, Hashtable<String, Object> options) {
    // reserved for future use
  }

  /**
   * Deserializes an explorer instance from the input stream.
   *
   * @param ois		the input stream to read
   * @return		the name (= 0) and the explorer instance (= 1)
   * @throws Exception	if deserialization fails
   */
  protected static Object[] deserialize(ObjectInputStream ois) throws Exception {
    ExplorerExt 			expext;
    String 				name;
    int 				panelCount;
    Class 				cpanel;
    AbstractExplorerPanelHandler 	handler;
    ArrayList<ExplorerPanel> 		panels;
    Hashtable<String, Object> 		options;
    int					n;

    expext = new ExplorerExt();
    name = (String) ois.readObject();
    options = (Hashtable<String, Object>) ois.readObject();
    panelCount = (Integer) ois.readObject();
    setExplorerOptions(expext, options);

    panels = new ArrayList<ExplorerPanel>();
    panels.add(expext.getPreprocessPanel());
    panels.addAll(expext.getPanels());
    for (n = 0; n < panelCount; n++) {
      cpanel = Class.forName((String) ois.readObject());
      handler = (AbstractExplorerPanelHandler) Class.forName((String) ois.readObject()).newInstance();
      for (ExplorerPanel panel : panels) {
	if (panel.getClass().equals(cpanel)) {
	  handler.deserialize(panel, ois.readObject());
	  break;
	}
      }
    }

    return new Object[]{name, expext};
  }

  /**
   * Reads the explorer session and initializes the explorer object.
   *
   * @param file     the file to load the session from
   * @param explorer the explorer to initialize with the session
   * @throws Exception if loading fails
   */
  public static void read(File file, MultiExplorer explorer) throws Exception {
    ObjectInputStream 	ois;
    FileInputStream 	fis;
    int 		i;
    ExplorerExt 	expext;
    String 		name;
    int 		expCount;
    Object[]		exp;

    fis = new FileInputStream(file);
    ois = new ObjectInputStream(new BufferedInputStream(fis));

    explorer.getEntryPanel().clear();
    expCount = (Integer) ois.readObject();
    for (i = 0; i < expCount; i++) {
      exp    = deserialize(ois);
      name   = (String) exp[0];
      expext = (ExplorerExt) exp[1];
      explorer.addPanel(expext, name);
    }

    FileUtils.closeQuietly(ois);
    FileUtils.closeQuietly(fis);
  }

  /**
   * Copies an explorer instance.
   *
   * @param expext	the explorer instance to copy
   * @param skipHistory	whether to skip copying the history
   * @return		the copy
   * @throws Exception	if copying fails
   */
  public static ExplorerExt copy(ExplorerExt expext, boolean skipHistory) throws Exception {
    Object[]			exp;
    ByteArrayOutputStream	bos;
    ObjectOutputStream		oos;
    byte[]			data;
    ByteArrayInputStream	bis;
    ObjectInputStream		ois;

    bos = new ByteArrayOutputStream();
    oos = new ObjectOutputStream(bos);
    serialize(expext, "dummy", oos, skipHistory);
    data = bos.toByteArray();

    bis = new ByteArrayInputStream(data);
    ois = new ObjectInputStream(bis);
    exp = deserialize(ois);

    return (ExplorerExt) exp[1];
  }
}
