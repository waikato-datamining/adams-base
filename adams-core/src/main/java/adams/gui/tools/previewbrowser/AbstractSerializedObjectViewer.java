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
 * AbstractSerializedFileViewer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import javax.swing.JPanel;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for all serialized object viewers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSerializedObjectViewer
  extends AbstractOptionHandler 
  implements SerializedObjectViewer {

  /** for serialization. */
  private static final long serialVersionUID = 2722977281064051787L;

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  protected abstract PreviewPanel createPreview(Object obj);

  /**
   * Creates a {@link PreviewPanel} for the given object.
   * 
   * @param obj		the object to create a preview for
   * @return		the preview, null if failed to generate
   */
  @Override
  public JPanel getPreview(Object obj) {
    JPanel	result;
    
    result = createPreview(obj);
    if (result == null)
      result = new NoPreviewAvailablePanel();
    
    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  @Override
  public int compareTo(SerializedObjectViewer o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * <br><br>
   * Uses the commandline for comparison.
   * 
   * @param obj		the object to compare with
   * @return		true if the same commandline
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SerializedObjectViewer)
      return (compareTo((SerializedObjectViewer) obj) == 0);
    else
      return false;
  }
  
  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getViewers() {
    return ClassLister.getSingleton().getClassnames(SerializedObjectViewer.class);
  }
}
