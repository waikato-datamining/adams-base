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
 * Invert.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfinder;

import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Inverts the indices of the base object finder.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-object-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: objectFinder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.NullFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Invert
  extends AbstractMetaObjectFinder {

  private static final long serialVersionUID = -8034475536001525696L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inverts the indices of the base object finder.";
  }

  /**
   * Performs the actual filtering of the objects in the report.
   *
   * @param img		the image to filter
   * @return		the filtered image
   */
  @Override
  protected int[] doFind(AbstractImageContainer img) {
    TIntList		result;
    TIntSet 		base;
    LocatedObjects	objects;
    int			index;

    result  = new TIntArrayList();
    base    = new TIntHashSet(m_ObjectFinder.find(img));
    objects = LocatedObjects.fromReport(img.getReport(), m_Prefix);
    for (LocatedObject obj: objects) {
      if (obj.getMetaData() != null) {
	try {
	  if (obj.getMetaData().containsKey(LocatedObjects.KEY_INDEX)) {
	    index = Integer.parseInt("" + obj.getMetaData().get(LocatedObjects.KEY_INDEX));
	    if (!base.contains(index))
	      result.add(index);
	  }
	  else if (isLoggingEnabled()) {
	    getLogger().warning("Object has no index in meta-data: " + obj);
	  }
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to parse index: " + obj.getMetaData().get(LocatedObjects.KEY_INDEX), e);
	}
      }
      else {
        if (isLoggingEnabled())
          getLogger().warning("Object has no meta-data: " + obj);
      }
    }

    return result.toArray();
  }
}
