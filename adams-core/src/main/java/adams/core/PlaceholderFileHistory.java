/**
 * PlaceholderFileHistory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.io.PlaceholderFile;

/**
 * History for PlaceholderFile objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6358 $
 */
public class PlaceholderFileHistory
  extends AbstractFileBasedHistory<PlaceholderFile> {

  /** for serialization. */
  private static final long serialVersionUID = -5716154035144840331L;

  /**
   * Creates a new file object from the string.
   *
   * @param path 	the path to create the object from
   */
  protected PlaceholderFile newInstance(String path) {
    return new PlaceholderFile(path);
  }
}
