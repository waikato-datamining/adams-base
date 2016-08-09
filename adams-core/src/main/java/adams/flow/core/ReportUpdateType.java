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
 * ReportUpdateType.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Enum that determines how the report is being updated.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12159 $
 */
public enum ReportUpdateType {
  /** replace the existing report. */
  REPLACE,
  /** doesn't overwrite values in current report, only adds additional from other. */
  MERGE_CURRENT_WITH_OTHER,
  /** doesn't overwrite values in other report, only adds additional from current. */
  MERGE_OTHER_WITH_CURRENT,
}
