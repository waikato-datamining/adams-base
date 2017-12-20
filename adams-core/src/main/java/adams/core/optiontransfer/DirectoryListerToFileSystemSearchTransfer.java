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
 * DirectoryListerToFileSystemSearchTransfer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.source.DirectoryLister;
import adams.flow.source.FileSystemSearch;
import adams.flow.source.filesystemsearch.AbstractFileSystemSearchlet;
import adams.flow.source.filesystemsearch.LocalDirectorySearch;
import adams.flow.source.filesystemsearch.LocalFileSearch;
import adams.flow.source.filesystemsearch.MultiSearch;

/**
 * Transfers options from DirectoryLister to FileSystemSearch actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryListerToFileSystemSearchTransfer
  extends AbstractOptionTransfer {

  /**
   * Returns whether it can handle the transfer.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		true if options can be transferred by this class
   */
  @Override
  public boolean handles(Object source, Object target) {
    return (source instanceof DirectoryLister) && (target instanceof FileSystemSearch);
  }

  /**
   * Does the actual transfer of options.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransfer(Object source, Object target) {
    DirectoryLister 	dl;
    FileSystemSearch 	fs;

    dl = (DirectoryLister) source;
    fs = (FileSystemSearch) target;

    fs.setOutputArray(dl.getOutputArray());
    transferVariable(fs, dl, "outputArray");

    if (dl.getListDirs() && dl.getListFiles()) {
      LocalDirectorySearch dirs = new LocalDirectorySearch();
      dirs.setDirectory(dl.getWatchDir()); transferVariable(dl, "watchDir", dirs, "directory");
      dirs.setSorting(dl.getSorting()); transferVariable(dl, dirs, "sorting");
      dirs.setSortDescending(dl.getSortDescending()); transferVariable(dl, dirs, "sortDescending");
      dirs.setRegExp(dl.getRegExp()); transferVariable(dl, dirs, "regExp");
      dirs.setMaxDepth(dl.getMaxDepth()); transferVariable(dl, dirs, "maxDepth");
      dirs.setMaxItems(dl.getMaxItems()); transferVariable(dl, dirs, "maxItems");
      dirs.setRecursive(dl.getRecursive()); transferVariable(dl, dirs, "recursive");

      LocalFileSearch files = new LocalFileSearch();
      files.setDirectory(dl.getWatchDir()); transferVariable(dl, "watchDir", files, "directory");
      files.setSorting(dl.getSorting()); transferVariable(dl, files, "sorting");
      files.setSortDescending(dl.getSortDescending()); transferVariable(dl, files, "sortDescending");
      files.setRegExp(dl.getRegExp()); transferVariable(dl, files, "regExp");
      files.setMaxDepth(dl.getMaxDepth()); transferVariable(dl, files, "maxDepth");
      files.setMaxItems(dl.getMaxItems()); transferVariable(dl, files, "maxItems");
      files.setRecursive(dl.getRecursive()); transferVariable(dl, files, "recursive");

      MultiSearch multi = new MultiSearch();
      multi.setSearches(new AbstractFileSystemSearchlet[]{dirs, files});
      fs.setSearch(multi);
    }
    else if (dl.getListDirs()) {
      LocalDirectorySearch dirs = new LocalDirectorySearch();
      dirs.setDirectory(dl.getWatchDir()); transferVariable(dl, "watchDir", dirs, "directory");
      dirs.setSorting(dl.getSorting()); transferVariable(dl, dirs, "sorting");
      dirs.setSortDescending(dl.getSortDescending()); transferVariable(dl, dirs, "sortDescending");
      dirs.setRegExp(dl.getRegExp()); transferVariable(dl, dirs, "regExp");
      dirs.setMaxDepth(dl.getMaxDepth()); transferVariable(dl, dirs, "maxDepth");
      dirs.setMaxItems(dl.getMaxItems()); transferVariable(dl, dirs, "maxItems");
      dirs.setRecursive(dl.getRecursive()); transferVariable(dl, dirs, "recursive");
      fs.setSearch(dirs);
    }
    else {
      LocalFileSearch files = new LocalFileSearch();
      files.setDirectory(dl.getWatchDir()); transferVariable(dl, "watchDir", files, "directory");
      files.setSorting(dl.getSorting()); transferVariable(dl, files, "sorting");
      files.setSortDescending(dl.getSortDescending()); transferVariable(dl, files, "sortDescending");
      files.setRegExp(dl.getRegExp()); transferVariable(dl, files, "regExp");
      files.setMaxDepth(dl.getMaxDepth()); transferVariable(dl, files, "maxDepth");
      files.setMaxItems(dl.getMaxItems()); transferVariable(dl, files, "maxItems");
      files.setRecursive(dl.getRecursive()); transferVariable(dl, files, "recursive");
      fs.setSearch(files);
    }
    if (dl.getOptionManager().hasVariableForProperty("files"))
      fs.getOptionManager().setVariableForProperty("initialFiles", dl.getOptionManager().getVariableForProperty("files"));

    return null;
  }
}
