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
 * AbstractArchiveHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import adams.core.ClassLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor of all archive handlers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractArchiveHandler
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3774402480647722078L;

  /** the archive to extract the files from. */
  protected PlaceholderFile m_Archive;

  /** the extenstion archive handlers relation. */
  protected static Hashtable<String,Vector<Class>> m_Relation;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"archive", "archive",
	new PlaceholderFile("."));
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  public abstract String[] getExtensions();

  /**
   * Sets the archive to get the files from.
   *
   * @param value	the archive
   */
  public void setArchive(PlaceholderFile value) {
    m_Archive = value;
    reset();
  }

  /**
   * Returns the current archive.
   *
   * @return		the archive
   */
  public PlaceholderFile getArchive() {
    return m_Archive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String archiveTipText() {
    return "The archive to obtain the files from.";
  }

  /**
   * Performs some checks on the archive.
   */
  protected void checkArchive() {
    if (!m_Archive.exists())
      throw new IllegalStateException("Archive does not exist: " + m_Archive);
    if (m_Archive.isDirectory())
      throw new IllegalStateException("Archive is not a file: " + m_Archive);
  }

  /**
   * Performs actual listing of files.
   *
   * @return		the stored files
   */
  protected abstract String[] listFiles();

  /**
   * Returns the files stored in the archive.
   *
   * @return		the files
   * @see		#extract(String, File)
   */
  public String[] getFiles() {
    checkArchive();
    return listFiles();
  }

  /**
   * Extracts the specified file and saves it locally.
   *
   * @param archiveFile	the file in the archive to extract
   * @param outFile	the local file to store the content in
   * @return		true if successfully extracted
   * @see		#listFiles()
   */
  protected abstract boolean doExtract(String archiveFile, File outFile);

  /**
   * Extracts the specified file and saves it locally.
   *
   * @param archiveFile	the file in the archive to extract
   * @param outFile	the local file to store the content in
   * @return		true if successfully extracted
   * @see		#listFiles()
   */
  public boolean extract(String archiveFile, File outFile) {
    checkArchive();
    return doExtract(archiveFile, outFile);
  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(AbstractArchiveHandler.class);
  }

  /**
   * Returns the extension/handlers relation.
   *
   * @return		the relation
   */
  protected static synchronized Hashtable<String,Vector<Class>> getRelation() {
    String[]			handlers;
    int				i;
    int				n;
    AbstractArchiveHandler	handler;
    String[]			extensions;
    Vector<Class>		classes;

    if (m_Relation == null) {
      m_Relation = new Hashtable<String,Vector<Class>>();
      handlers   = getHandlers();
      for (i = 0; i < handlers.length; i++) {
	try {
	  handler    = (AbstractArchiveHandler) Class.forName(handlers[i]).newInstance();
	  extensions = handler.getExtensions();
	  for (n = 0; n < extensions.length; n++) {
	    if (!m_Relation.containsKey(extensions[n]))
	      m_Relation.put(extensions[n], new Vector<Class>());
	    classes = m_Relation.get(extensions[n]);
	    classes.add(handler.getClass());
	  }
	}
	catch (Exception e) {
	  System.err.println("Error processing archive handler: " + handlers[i]);
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

    if (extension != null)
      return getRelation().containsKey(extension);
    else
      return false;
  }

  /**
   * Returns the handlers registered for the extension of the specified file.
   *
   * @param file	the file to get the handlers for
   * @return		the handlers, null if none available
   */
  public static Vector<Class> getHandlersForFile(File file) {
    return getHandlersForFile(file.getAbsolutePath());
  }

  /**
   * Returns the handlers registered for the extension of the specified file.
   *
   * @param filename	the file to get the handlers for
   * @return		the handlers, null if none available
   */
  public static Vector<Class> getHandlersForFile(String filename) {
    Vector<Class>	result;
    String		extension;

    extension = FileUtils.getExtension(filename);
    if (extension != null)
      extension = extension.toLowerCase();

    if ((extension != null) && (getRelation().containsKey(extension)))
      result = getRelation().get(extension);
    else
      result = null;

    return result;
  }
}
