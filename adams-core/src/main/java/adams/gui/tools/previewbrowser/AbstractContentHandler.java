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
 * Copyright (C) 2011-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import com.googlecode.jfilechooserbookmarks.core.Utils;
import nz.ac.waikato.cms.locator.ClassCompare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for all content handlers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractContentHandler
  extends AbstractOptionHandler
  implements ContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2722977281064051787L;

  /** the extenstion archive handlers relation. */
  protected static Hashtable<String,List<Class>> m_Relation;

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
   * Reuses the last preview, if possible.
   * <br>
   * Default implementation just creates a new preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel lastPreview) {
    return createPreview(file);
  }

  /**
   * Creates the actual preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  public abstract PreviewPanel createPreview(File file);

  /**
   * Returns the preview for the specified file.
   *
   * @param file	the file to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  @Override
  public PreviewPanel getPreview(File file) {
    String	msg;

    msg = checkFile(file);
    if (msg == null) {
      try {
        return createPreview(file);
      }
      catch (Exception e) {
        msg = "Failed to create preview with " + getClass().getName() + ":\n\n" + Utils.throwableToString(e);
        getLogger().log(Level.SEVERE, msg);
      }
    }
    return new NoPreviewAvailablePanel(msg);
  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(ContentHandler.class);
  }

  /**
   * Returns the extension/handlers relation.
   *
   * @return		the relation
   */
  protected static synchronized Hashtable<String,List<Class>> getRelation() {
    String[]		handlers;
    int			i;
    int			n;
    ContentHandler	handler;
    String[]		extensions;
    String		ext;
    List<Class>		classes;

    if (m_Relation == null) {
      m_Relation = new Hashtable<>();
      handlers   = getHandlers();
      for (i = 0; i < handlers.length; i++) {
	try {
	  handler    = (ContentHandler) ClassManager.getSingleton().forName(handlers[i]).getDeclaredConstructor().newInstance();
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
    HashSet<Class>	set;
    String		extension;

    extension = FileUtils.getExtension(filename);
    if (extension != null)
      extension = extension.toLowerCase();

    if ((extension != null) && (getRelation().containsKey(extension)))
      set = new HashSet<>(getRelation().get(extension));
    else
      set = new HashSet<>();

    if (getRelation().containsKey(MATCH_ALL))
      set.addAll(getRelation().get(MATCH_ALL));

    if (set.size() == 0)
      result = null;
    else
      result = new ArrayList<>(set);

    Collections.sort(result, new ClassCompare());

    return result;
  }
}
