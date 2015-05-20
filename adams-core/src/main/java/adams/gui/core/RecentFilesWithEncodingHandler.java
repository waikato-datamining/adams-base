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
 * RecentFilesWithEncodingHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import adams.core.io.FileUtils;
import adams.env.Environment;

/**
 * A class that handles a list of recent files that also store file encoding 
 * information. Reads/writes them from/to a props file in the application's 
 * home directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Environment#getHome()
 */
public class RecentFilesWithEncodingHandler<M>
  extends AbstractRecentItemsHandler<M, String> {

  /** for serialization. */
  private static final long serialVersionUID = 7532226757387619342L;

  /** the property for storing the number of recent files. */
  public final static String RECENTFILES_COUNT = "RecentFilesCount";

  /** the property prefix for a recent file. */
  public final static String RECENTFILES_PREFIX = "RecentFile.";

  /** the minimum number of parent directories to use. */
  protected int m_MinNumParentDirs;
  
  /**
   * Initializes the handler with a maximum of 5 items.
   *
   * @param propsFile	the props file to store the files in
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentFilesWithEncodingHandler(String propsFile, M menu) {
    super(propsFile, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile	the props file to store the files in
   * @param maxCount	the maximum number of files to keep in menu
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentFilesWithEncodingHandler(String propsFile, int maxCount, M menu) {
    super(propsFile, maxCount, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile	the props file to store the files in
   * @param propPrefix	the properties prefix, use null to ignore
   * @param maxCount	the maximum number of files to keep in menu
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentFilesWithEncodingHandler(String propsFile, String propPrefix, int maxCount, M menu) {
    super(propsFile, propPrefix, maxCount, menu);
  }
  
  /**
   * Checks the item after obtaining from the props file.
   * <br><br>
   * File must exist.
   * 
   * @param item	the item to check
   * @return		true if checks passed
   */
  @Override
  protected boolean check(String item) {
    return getFile(item).exists();
  }
  
  /**
   * Determines the minimum number of parent directories that need to be
   * included in the filename to make the filenames in the menu distinguishable.
   *
   * @return		the minimum number of parent directories, -1 means
   * 			full path
   */
  protected synchronized int determineMinimumNumberOfParentDirs() {
    int			result;
    HashSet<String>	files;
    int			num;
    int			i;
    List<File>		list;
    File		file;
    int			max;

    result = -1;

    list = new ArrayList<File>();
    max  = 0;
    for (String item: m_RecentItems) {
      file = getFile(item);
      if (!list.contains(file)) {
	list.add(file);
	max = Math.max(max, FileUtils.getDirectoryDepth(file));
      }
    }
    num = 0;
    do {
      files = new HashSet<String>();
      for (i = 0; i < list.size(); i++)
	files.add(FileUtils.createPartialFilename(list.get(i), num));
      if (files.size() == list.size())
	result = num;
      else
	num++;
    }
    while (files.size() < list.size() && (num <= max));

    return result;
  }

  /**
   * Returns the key to use for the counts in the props file.
   * 
   * @return		the key
   */
  @Override
  protected String getCountKey() {
    return RECENTFILES_COUNT;
  }

  /**
   * Returns the key prefix to use for the items in the props file.
   * 
   * @return		the prefix
   */
  @Override
  protected String getItemPrefix() {
    return RECENTFILES_PREFIX;
  }

  /**
   * Turns an object into a string for storing in the props.
   * 
   * @param obj		the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(String obj) {
    return obj;
  }

  /**
   * Turns the string obtained from the props into an object again.
   * 
   * @param s		the string representation
   * @return		the parsed object
   */
  @Override
  protected String fromString(String s) {
    return s;
  }

  /**
   * Hook method which gets executed just before updating the menu.
   */
  @Override
  protected void preUpdateMenu() {
    super.preUpdateMenu();
    
    m_MinNumParentDirs = determineMinimumNumberOfParentDirs();
  }

  /**
   * Generates the text for the menuitem.
   * 
   * @param index	the index of the item
   * @param item	the item itself
   * @return		the generated text
   */
  @Override
  protected String createMenuItemText(int index, String item) {
    return FileUtils.createPartialFilename(getFile(item), m_MinNumParentDirs);
  }

  /**
   * Returns the file part of the item.
   * 
   * @param item	the item to get the file from
   * @return		the file
   */
  public static File getFile(String item) {
    File	result;
    String[]	parts;
    
    if (item.indexOf('\t') > -1) {
      parts = item.split("\t");
      result  = new File(parts[0]);
    }
    else {
      result = new File(item);
    }
    
    return result;
  }

  /**
   * Returns the file encoding part of the item.
   * 
   * @param item	the item to get the encoding from
   * @return		the encoding
   */
  public static String getEncoding(String item) {
    String	result;
    String[]	parts;
    
    if (item.indexOf('\t') > -1) {
      parts = item.split("\t");
      result  = parts[1];
    }
    else {
      result = "UTF-8";
    }
    
    return result;
  }
}
