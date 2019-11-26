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
 * LabelAwareObjectOverlap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.flow.transformer.locateobjects.LocatedObjects;
import com.github.fracpete.javautils.struct.Struct2;

/**
 * Interface for {@link ObjectOverlap} classes that can distinguish between
 * correct/incorrect labels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LabelAwareObjectOverlap
  extends ObjectOverlap{

  /**
   * Splits the overlapping objects into subsets of matching labels and mismatching ones.
   *
   * @param overlaps	all overlaps, to split
   * @return		split into matching/mismatching subsets
   */
  public Struct2<LocatedObjects,LocatedObjects> splitOverlaps(LocatedObjects overlaps);
}
