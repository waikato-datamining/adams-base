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
 * WekaExperimentFile.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import java.io.File;
import java.net.URI;

import adams.core.io.PlaceholderFile;

/**
 * A dummy class for the GOE, for special handling of experiments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentFile
  extends PlaceholderFile {

  /** for serialization. */
  private static final long serialVersionUID = -4345693975779199292L;

  /**
   * Creates a new ExperimentFile instance from a parent abstract pathname and a child
   * pathname string.
   *
   * @param parent	the parent
   * @param child	the child
   */
  public WekaExperimentFile(File parent, String child) {
    super(parent, child);
  }

  /**
   * Creates a new ExperimentFile instance by converting the given pathname string into
   * an abstract pathname.
   *
   * @param pathname	the path
   */
  public WekaExperimentFile(String pathname) {
    super(pathname);
  }

  /**
   * Creates a new ExperimentFile instance from a parent pathname string and a child
   * pathname string.
   *
   * @param parent	the parent
   * @param child	the child
   */
  public WekaExperimentFile(String parent, String child) {
    super(parent, child);
  }

  /**
   * Creates a new ExperimentFile instance by converting the given file: URI into an
   * abstract pathname.
   *
   * @param uri		the identifier
   */
  public WekaExperimentFile(URI uri) {
    super(uri);
  }

  /**
   * Creates a new ExperimentFile instance by using the given file.
   *
   * @param file	the file to use
   */
  public WekaExperimentFile(File file) {
    super(file.getAbsolutePath());
  }

  /**
   * Returns a file object.
   *
   * @return		the file
   */
  public File toFile() {
    return new File(getPath());
  }
}
