/**
 * PlaceholderDirectoryHistory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.io.PlaceholderDirectory;

/**
 * History for PlaceholderDirectory objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlaceholderDirectoryHistory
  extends AbstractFileBasedHistory<PlaceholderDirectory> {

  /** for serialization. */
  private static final long serialVersionUID = -5716154035144840331L;

  /**
   * Creates a new file object from the string.
   *
   * @param path 	the path to create the object from
   */
  protected PlaceholderDirectory newInstance(String path) {
    return new PlaceholderDirectory(path);
  }
}
