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
 * Flags.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net.imap;

import jodd.mail.EmailFilter;

import javax.mail.Flags;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * IMAP message flags.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public enum Flag {
  ANSWERED,
  NOT_ANSWERED,
  DELETED,
  NOT_DELETED,
  DRAFT,
  NOT_DRAFT,
  FLAGGED,
  NOT_FLAGGED,
  RECENT,
  NOT_RECENT,
  SEEN,
  NOT_SEEN;

  /**
   * Checks whether the flags are valid.
   *
   * @param flags	the flags to check
   * @return		true if valid
   */
  public static boolean isValid(Flag[] flags) {
    Set<Flag> 	all;

    all = new HashSet<>(Arrays.asList(flags));

    if (all.contains(ANSWERED) && all.contains(NOT_ANSWERED))
      return false;
    if (all.contains(DELETED) && all.contains(NOT_DELETED))
      return false;
    if (all.contains(DRAFT) && all.contains(NOT_DRAFT))
      return false;
    if (all.contains(FLAGGED) && all.contains(NOT_FLAGGED))
      return false;
    if (all.contains(RECENT) && all.contains(NOT_RECENT))
      return false;
    if (all.contains(SEEN) && all.contains(NOT_SEEN))
      return false;

    return true;
  }

  /**
   * Sets the flags on the filter.
   * 
   * @param flags	the flags to set
   * @param filter 	the filter to update
   */
  public static void updateFilter(Flag[] flags, EmailFilter filter) {
    for (Flag flag: flags) {
      switch (flag) {
	case ANSWERED:
	  filter.flag(Flags.Flag.ANSWERED, true);
	  break;
	case NOT_ANSWERED:
	  filter.flag(Flags.Flag.ANSWERED, false);
	  break;
	case DELETED:
	  filter.flag(Flags.Flag.DELETED, true);
	  break;
	case NOT_DELETED:
	  filter.flag(Flags.Flag.DELETED, false);
	  break;
	case DRAFT:
	  filter.flag(Flags.Flag.DRAFT, true);
	  break;
	case NOT_DRAFT:
	  filter.flag(Flags.Flag.DRAFT, false);
	  break;
	case FLAGGED:
	  filter.flag(Flags.Flag.FLAGGED, true);
	  break;
	case NOT_FLAGGED:
	  filter.flag(Flags.Flag.FLAGGED, false);
	  break;
	case RECENT:
	  filter.flag(Flags.Flag.RECENT, true);
	  break;
	case NOT_RECENT:
	  filter.flag(Flags.Flag.RECENT, false);
	  break;
	case SEEN:
	  filter.flag(Flags.Flag.SEEN, true);
	  break;
	case NOT_SEEN:
	  filter.flag(Flags.Flag.SEEN, false);
	  break;
      }
    }
  }
}
