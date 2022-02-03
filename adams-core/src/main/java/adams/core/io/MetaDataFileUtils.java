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
import adams.core.logging.LoggingObject;
import adams.flow.source.filesystemsearch.LocalFileSearch;

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
   * Returns the list of meta-data files that were identified using the provided
   * data file name.
   *
   * @param dataFile	the data to list the meta-data files for
   * @param location 	how to locate the meta-data files
   * @param defaultExt 	the default extension to look for
   * @param exts 	all available extensions to look for
   * @return		the files
   */
  public static PlaceholderFile[] find(LoggingObject source, PlaceholderFile dataFile, MetaDataLocation location, String defaultExt, String[] exts) {
    List<PlaceholderFile> 	result;
    PlaceholderFile		file;
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
	      result.add(new PlaceholderFile(matches.get(i)));
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

    return result.toArray(new PlaceholderFile[0]);
  }

}
