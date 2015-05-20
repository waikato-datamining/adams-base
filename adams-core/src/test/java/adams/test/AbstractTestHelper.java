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
 * TestHelper.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.test;

import adams.core.base.BasePassword;
import adams.core.io.FileUtils;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Ancestor for helper classes for tests.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <I> the type of data container to process (input)
 * @param <O> the type of data container to process (output)
 */
public abstract class AbstractTestHelper<I extends DataContainer, O> {

  /** the owning test case. */
  protected AdamsTestCase m_Owner;

  /** the data directory to use. */
  protected String m_DataDirectory;

  /** the database connection in use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the helper class.
   *
   * @param owner	the owning test case
   * @param dataDir	the data directory to use
   */
  public AbstractTestHelper(AdamsTestCase owner, String dataDir) {
    super();

    m_Owner              = owner;
    m_DataDirectory      = dataDir;
    m_DatabaseConnection = null;
  }

  /**
   * Returns the data directory in use.
   *
   * @return		the directory
   */
  public String getDataDirectory() {
    return m_DataDirectory;
  }

  /**
   * Returns the tmp directory.
   *
   * @return		the tmp directory
   */
  public String getTmpDirectory() {
    return AdamsTestSuite.getTmpDirectory();
  }

  /**
   * Returns the location in the tmp directory for given resource.
   *
   * @param resource	the resource (path in project) to get the tmp location for
   * @return		the tmp location
   * @see		#getTmpDirectory()
   */
  public String getTmpLocationFromResource(String resource) {
    String	result;
    File	file;

    file   = new File(resource);
    result = getTmpDirectory() + File.separator + file.getName();

    return result;
  }

  /**
   * Copies the given resource to the tmp directory.
   *
   * @param resource	the resource (path in project) to copy
   * @return		false if copying failed
   * @see		#getTmpLocationFromResource(String)
   */
  public boolean copyResourceToTmp(String resource) {
    boolean			result;
    BufferedInputStream		input;
    BufferedOutputStream	output;
    FileOutputStream		fos;
    byte[]			buffer;
    int				read;
    String			ext;

    input    = null;
    output   = null;
    resource = getDataDirectory() + "/" + resource;

    fos = null;
    try {
      input  = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(resource));
      fos    = new FileOutputStream(getTmpLocationFromResource(resource));
      output = new BufferedOutputStream(fos);
      buffer = new byte[1024];
      while ((read = input.read(buffer)) != -1) {
	output.write(buffer, 0, read);
	if (read < buffer.length)
	  break;
      }
      result = true;
    }
    catch (IOException e) {
      if (e.getMessage().equals("Stream closed")) {
	ext = resource.replaceAll(".*\\.", "");
	System.err.println(
	    "Resource '" + resource + "' not available? "
	    + "Or extension '*." + ext + "' not in pom.xml ('project.build.testSourceDirectory') listed?");
      }
      e.printStackTrace();
      result = false;
    }
    catch (Exception e) {
      e.printStackTrace();
      result = false;
    }

    FileUtils.closeQuietly(input);
    FileUtils.closeQuietly(output);
    FileUtils.closeQuietly(fos);

    return result;
  }

  /**
   * Copies the given resource to the tmp directory and renames it.
   *
   * @param resource	the resource (path in project) to copy
   * @param newName	the new name of the file
   * @return		false if copying/renaming failed
   * @see		#copyResourceToTmp(String)
   * @see		#renameTmpFile(String, String)
   */
  public boolean copyResourceToTmp(String resource, String newName) {
    boolean	result;

    result = copyResourceToTmp(resource);
    if (result)
      result = renameTmpFile(resource, newName);

    return result;
  }

  /**
   * Removes the file from the tmp directory.
   *
   * @param filename	the file in the tmp directory to delete (no path!)
   * @return		true if deleting succeeded or file not present
   */
  public boolean deleteFileFromTmp(String filename) {
    boolean	result;
    File	file;

    result = true;
    file   = new File(getTmpDirectory() + File.separator + filename);
    if (file.exists())
      result = file.delete();

    return result;
  }

  /**
   * Renames a file in the tmp directory.
   *
   * @param oldName	the old name of the file
   * @param newName	the new name of the file
   * @return		true if renaming succeeded or file not present
   */
  public boolean renameTmpFile(String oldName, String newName) {
    boolean	result;
    File	oldFile;
    File	newFile;

    result  = true;
    oldFile = new File(getTmpDirectory() + File.separator + new File(oldName).getName());
    if (oldFile.exists()) {
      newFile = new File(getTmpDirectory() + File.separator + newName);
      try {
	FileUtils.move(oldFile, newFile);
	result = newFile.exists();
      }
      catch (Exception e) {
	System.err.println("Failed to move file '" + oldFile + "' to '" + newFile + "':");
	e.printStackTrace();
	result = false;
      }
    }

    return result;
  }

  /**
   * Attempts to create a directory in the temp directory.
   * 
   * @param dirname	the relative directory path
   * @return		true if successfully created
   */
  public boolean createDirInTmp(String dirname) {
    File	file;
    
    file = new File(getTmpDirectory() + File.separator + dirname);
    return file.mkdirs();
  }
  
  /**
   * Removes the directory recursively from the tmp directory.
   *
   * @param dirname	the directory in the tmp directory to delete (relative path!)
   * @return		true if deleting succeeded or dir not present
   */
  public boolean deleteDirFromTmp(String dirname) {
    return FileUtils.delete(new File(getTmpDirectory() + File.separator + dirname));
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   * @see		#getDataDirectory()
   */
  public abstract I load(String filename);

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  public abstract boolean save(O data, String filename);

  /**
   * Returns the database connection.
   *
   * @param url		the URL to use
   * @param user	the database user
   * @param password	the database password
   */
  public abstract AbstractDatabaseConnection getDatabaseConnection(String url, String user, BasePassword password);

  /**
   * Tries to connect to the database.
   *
   * @param url		the URL to use
   * @param user	the database user
   * @param password	the database password
   */
  public abstract void connect(String url, String user, BasePassword password);

  /**
   * Hook method for actions after connecting to a database.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void postConnect() {
  }
}
