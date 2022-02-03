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
 * MetaDataFileUtils.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.base.BaseRegExp;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.logging.LoggingObject;
import adams.flow.source.filesystemsearch.LocalFileSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Helper class for locating meta-data files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MetaDataFileUtils {

  /**
   * Enum for locating the meta-data.
   */
  public enum MetaDataLocation {
    SAME_NAME,
    STARTING_WITH,
  }

  /**
   * Lists all the files in the directory that match the specified prefix.
   *
   * @param dir		the directory to scan
   * @param prefix	the prefix that the files require
   * @return		the matching files
   */
  public static File[] list(File dir, String prefix) {
    List<File>			result;
    String			dirAbs;
    LocalDirectoryLister	lister;
    String[]			files;

    dirAbs = dir.getAbsolutePath();
    lister = new LocalDirectoryLister();
    lister.setWatchDir(dirAbs);
    lister.setUseRelativePaths(true);
    lister.setRecursive(false);
    lister.setListDirs(false);
    lister.setListFiles(true);
    files = lister.list();

    result = new ArrayList<>();
    for (String file: files) {
      if (file.startsWith(prefix))
	result.add(new File(dirAbs, file));
    }

    return result.toArray(new File[0]);
  }

  /**
   * Returns the list of meta-data files that were identified using the provided
   * data file name.
   *
   * @param source 	the caller, can be null
   * @param dataFile	the data to list the meta-data files for
   * @param location 	how to locate the meta-data files
   * @param defaultExt 	the default extension to look for (no dot)
   * @param exts 	all available extensions to look for (no dot)
   * @return		the files
   */
  public static File[] find(LoggingObject source, PlaceholderFile dataFile, MetaDataLocation location, String defaultExt, String[] exts) {
    List<File> 			result;
    File			file;
    LocalFileSearch 		search;
    List<String>		matches;
    int				i;
    Set<String> 		extensions;

    result = new ArrayList<>();

    switch (location) {
      case SAME_NAME:
	file = FileUtils.replaceExtension(dataFile, "." + defaultExt);
	if (file.exists())
	  result.add(file);
	break;
      case STARTING_WITH:
	search = new LocalFileSearch();
	search.setDirectory(new PlaceholderDirectory(dataFile.getParentFile()));
	search.setRecursive(false);
	search.setRegExp(new BaseRegExp(FileUtils.replaceExtension(dataFile.getName(), "") + ".*\\." + defaultExt));
	extensions = new HashSet<>(Arrays.asList(exts));
	try {
	  matches = search.search();
	  for (i = 0; i < matches.size(); i++) {
	    if (!extensions.contains(FileUtils.getExtension(matches.get(i))))
	      result.add(new File(matches.get(i)));
	  }
	}
	catch (Exception e) {
	  if (source != null)
	    source.getLogger().log(Level.SEVERE, "Failed to locate meta-data files using: " + search.toCommandLine(), e);
	  result = new ArrayList<>();
	}
	break;
      default:
	throw new IllegalStateException("Unhandled meta-data location: " + location);
    }

    return result.toArray(new File[0]);
  }

  /**
   * Looks for a meta-data file for the data file, using the specified extension.
   * In case of "*" as extension, the first file that matches (and is not the data file)
   * will get returned.
   *
   * @param source 	the caller, can be null
   * @param dataFile	the file to look for meta-data file
   * @param ext		the extension the meta-data file should have ('*' can be used)
   * @return		the meta-data file, null if not found
   */
  public static File find(LoggingObject source, File dataFile, String ext) {
    return find(source, dataFile, null, ext);
  }

  /**
   * Looks for a meta-data file for the data file, using the specified suffix and extension.
   * In case of "*" as extension, the first file that matches suffix (and is not the data file)
   * will get returned.
   *
   * @param source 	the caller, can be null
   * @param dataFile	the file to look for meta-data file
   * @param suffix	the (optional) suffix the meta-data file has (before .ext)
   * @param ext		the extension the meta-data file should have ('*' can be used)
   * @return		the meta-data file, null if not found
   */
  public static File find(LoggingObject source, File dataFile, String suffix, String ext) {
    File 	result;
    File[]	files;
    File	dfile;

    result = null;

    dfile = dataFile.getAbsoluteFile();

    if (suffix == null)
      suffix = "";

    if (ext.equals("*")) {
      files = list(dfile.getParentFile(), FileUtils.replaceExtension(dfile, "").getName());
      for (File file: files) {
        if (file.equals(dfile))
          continue;
        if (!suffix.isEmpty() && !file.getName().startsWith(suffix))
          continue;
        result = file;
        break;
      }
    }
    else {
      result = FileUtils.replaceExtension(dfile, suffix + "." + ext);
    }

    if (result == null) {
      if (source != null)
        source.getLogger().severe("Failed to locate meta-data file for '" + dataFile + "', using extension '" + ext + "'!");
    }

    return result;
  }
}
