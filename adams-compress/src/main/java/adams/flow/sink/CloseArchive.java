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
 * CloseArchive.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.io.ArchiveManager;
import adams.flow.source.NewArchive;
import adams.flow.transformer.AppendArchive;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CloseArchive
  extends AbstractSink
  implements ClassCrossReference {

  private static final long serialVersionUID = -6846016191868806376L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Finalizes the archive.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{NewArchive.class, AppendArchive.class};
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ArchiveManager.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    ArchiveManager	manager;

    manager = m_InputToken.getPayload(ArchiveManager.class);
    return manager.close();
  }
}
