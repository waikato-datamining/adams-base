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
 * AbstractContentHandler.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.ClassLister;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import nz.ac.waikato.cms.locator.ClassCompare;

import javax.swing.JPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Ancestor for all content handlers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractContentHandler
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2722977281064051787L;

  /** the match-all extension. */
  public final static String MATCH_ALL = "*";

  /** the extenstion archive handlers relation. */
  protected static Hashtable<String,List<Class>> m_Relation;

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  public abstract String[] getExtensions();

  /**
   * Performs some checks on the file.
   *
   * @param file	the file to check
   * @return		null if check passed, otherwise error message
   */
  protected String checkFile(File file) {
    if (!file.exists())
      return "File '" + file + "' does not exist!";

    if (file.isDirectory())
      return "File '" + file + "' is a directory!";

    return null;
  }

  /**
   * Creates the actual preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  protected abstract PreviewPanel createPreview(File file);

  /**
   * Returns the preview for the specified file.
   *
   * @param file	the file to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  public JPanel getPreview(File file) {
    String	msg;

    msg = checkFile(file);
    if (msg == null) {
      return createPreview(file);
    }
    else {
      getLogger().severe(msg);
      return new NoPreviewAvailablePanel();
    }
  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(AbstractContentHandler.class);
  }

  /**
   * Returns the extension/handlers relation.
   *
   * @return		the relation
   */
  protected static synchronized Hashtable<String,List<Class>> getRelation() {
    String[]			handlers;
    int				i;
    int				n;
    AbstractContentHandler	handler;
    String[]			extensions;
    String			ext;
    List<Class>			classes;

    if (m_Relation == null) {
      m_Relation = new Hashtable<>();
      handlers   = getHandlers();
      for (i = 0; i < handlers.length; i++) {
	try {
	  handler    = (AbstractContentHandler) Class.forName(handlers[i]).newInstance();
	  extensions = handler.getExtensions();
	  for (n = 0; n < extensions.length; n++) {
	    ext = extensions[n];
	    if (ext.startsWith("."))
	      ext = ext.substring(1);
	    if (!m_Relation.containsKey(ext))
	      m_Relation.put(ext, new ArrayList<>());
	    classes = m_Relation.get(ext);
	    if (!classes.contains(handler.getClass()))
	      classes.add(handler.getClass());
	  }
	}
	catch (Exception e) {
	  System.err.println("Error processing content handler: " + handlers[i]);
	  e.printStackTrace();
	}
      }
    }

    return m_Relation;
  }

  /**
   * Checks whether the specified file is an archive that can be managed.
   *
   * @param file	the file to check
   * @return		true if the file represents a managed archive
   */
  public static boolean hasHandler(File file) {
    return hasHandler(file.getAbsolutePath());
  }

  /**
   * Checks whether the specified file is an archive that can be managed.
   *
   * @param filename	the file to check
   * @return		true if the file represents a managed archive
   */
  public static boolean hasHandler(String filename) {
    String	extension;

    extension = FileUtils.getExtension(filename);
    if (extension != null)
      extension = extension.toLowerCase();

    if (extension != null) {
      return (getRelation().containsKey(MATCH_ALL))
	|| getRelation().containsKey(extension);
    }
    else {
      return (getRelation().containsKey(MATCH_ALL));
    }
  }

  /**
   * Returns the handlers registered for the extension of the specified file.
   *
   * @param file	the file to get the handlers for
   * @return		the handlers, null if none available
   */
  public static List<Class> getHandlersForFile(File file) {
    return getHandlersForFile(file.getAbsolutePath());
  }

  /**
   * Returns the handlers registered for the extension of the specified file.
   *
   * @param filename	the file to get the handlers for
   * @return		the handlers, null if none available
   */
  public static List<Class> getHandlersForFile(String filename) {
    List<Class>		result;
    List<Class> 	matchAll;
    HashSet<Class>	set;
    String		extension;

    extension = FileUtils.getExtension(filename);
    if (extension != null)
      extension = extension.toLowerCase();

    if ((extension != null) && (getRelation().containsKey(extension)))
      set = new HashSet<>(getRelation().get(extension));
    else
      set = new HashSet<>();

    if (set.size() == 0)
      result = null;
    else
      result = new ArrayList<>(set);

    Collections.sort(result, new ClassCompare());

    matchAll = new ArrayList<>();
    if (getRelation().containsKey(MATCH_ALL)) {
      matchAll.addAll(getRelation().get(MATCH_ALL));
      Collections.sort(matchAll, new ClassCompare());
      result.addAll(matchAll);
    }

    return result;
  }
}
