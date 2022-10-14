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
 * DirectoryChooserFactory.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.io.PlaceholderDirectory;

import javax.swing.JFileChooser;
import java.io.File;

/**
 * Factor for instantiating directory choosers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryChooserFactory {

  /** Return value if cancel is chosen. */
  public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;

  /** Return value if approve (yes, ok) is chosen. */
  public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

  /** Return value if an error occurred. */
  public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;

  /** Instruction to display only files. */
  public static final int FILES_ONLY = JFileChooser.FILES_ONLY;

  /** Instruction to display only directories. */
  public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;

  /** Instruction to display both files and directories. */
  public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

  /** Instruction to cancel the current selection. */
  public static final String CANCEL_SELECTION = "CancelSelection";

  /** Instruction to approve the current selection (same as pressing yes or ok). */
  public static final String APPROVE_SELECTION = "ApproveSelection";

  /**
   * Returns a directory chooser instance with the user's default directory as initial directory.
   *
   * @return		the chooser
   */
  public static FileChooser createChooser() {
    return new SimpleDirectoryChooser();
  }

  /**
   * Returns a directory chooser instance with the specified initial directory.
   *
   * @param initialDir	the initial directory to use
   * @return		the chooser
   */
  public static FileChooser createChooser(String initialDir) {
    return new SimpleDirectoryChooser(initialDir);
  }

  /**
   * Returns a directory chooser instance with the specified initial directory.
   *
   * @param initialDir	the initial directory to use
   * @return		the chooser
   */
  public static FileChooser createChooser(File initialDir) {
    return new SimpleDirectoryChooser(initialDir);
  }

  /**
   * Retrieves the current directory as PlaceholderDirectory from the chooser.
   *
   * @param chooser	the chooser to obtain the current dir from
   * @return		the dir, null if not available
   */
  public static PlaceholderDirectory getSelectedDirectory(FileChooser chooser) {
    PlaceholderDirectory result;
    File			file;

    result = null;
    file   = chooser.getSelectedFile();
    if (file != null)
      result = new PlaceholderDirectory(file);

    return result;
  }
}
