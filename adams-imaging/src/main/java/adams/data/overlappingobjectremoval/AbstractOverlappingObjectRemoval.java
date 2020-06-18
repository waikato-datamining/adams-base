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
 * AbstractOverlappingObjectRemoval.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.overlappingobjectremoval;

import adams.core.option.AbstractOptionHandler;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;
import java.util.Set;

/**
 * Ancestor for schemes that remove overlapping images objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOverlappingObjectRemoval
  extends AbstractOptionHandler
  implements OverlappingObjectRemoval {

  private static final long serialVersionUID = 6991707947586230873L;

  /**
   * Removes overlapping image objects.
   *
   * @param objects	the objects to clean up
   * @param matches	the matches that were determined by an algorithm, used as basis for removal
   * @return		the updated objects
   */
  @Override
  public abstract LocatedObjects removeOverlaps(LocatedObjects objects, Map<LocatedObject, Set<LocatedObject>> matches);

  /**
   * Removes overlapping objects between the two reports.
   *
   * @param thisReport	the first report
   * @param otherReport	the second report
   * @param finder	the finder for locating the objects
   * @param detection	detects the overlaps
   * @param removal	applies the removal technique
   * @return		the new report (thisReport-non-object fields plus left-over objects)
   * @throws Exception	if instantiation of report fails
   */
  public static Report remove(Report thisReport, Report otherReport, ObjectFinder finder, ObjectOverlap detection, OverlappingObjectRemoval removal) throws Exception {
    Report 					result;
    LocatedObjects				thisObjs;
    LocatedObjects				otherObjs;
    LocatedObjects 				newObjs;
    Map<LocatedObject, Set<LocatedObject>> 	matches;

    thisObjs  = finder.findObjects(LocatedObjects.fromReport(thisReport,  finder.getPrefix()));
    otherObjs = finder.findObjects(LocatedObjects.fromReport(otherReport, finder.getPrefix()));
    matches   = detection.matches(thisObjs, otherObjs);
    newObjs   = removal.removeOverlaps(thisObjs, matches);

    // assemble new report
    result = thisReport.getClass().newInstance();

    // transfer non-object fields
    for (AbstractField field: thisReport.getFields()) {
      if (!field.getName().startsWith(finder.getPrefix())) {
        result.addField(field);
        result.setValue(field, thisReport.getValue(field));
      }
    }

    // store objects
    result.mergeWith(newObjs.toReport(finder.getPrefix()));

    return result;
  }
}
