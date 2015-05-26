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
 * Constants.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.io.Serializable;

/**
 * Contains some commonly used constants.
 *
 * @author  peter (peter at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Constants
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 6844330611108433085L;

  /** the unitialized ID. */
  public final static int NO_ID = -1;

  /** the dummy ID. */
  public final static String DUMMY_ID = "dummy";

  /** the placeholder for the ID. */
  public static final String PLACEHOLDER_ID = "{ID}";

  /** the placeholder for the database ID. */
  public static final String PLACEHOLDER_DATABASEID = "{DBID}";

  /** the date format for timestamps. */
  public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** the date format for timestamps (incl msecs). */
  public final static String TIMESTAMP_FORMAT_MSECS = "yyyy-MM-dd HH:mm:ss.S";

  /** the date format for timestamps (ISO-8601). */
  public final static String TIMESTAMP_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

  /** the character to use for "hiding" the password characters. */
  public final static char PASSWORD_CHAR = '*';

  /** the format for times. */
  public final static String TIME_FORMAT = "HH:mm:ss";

  /** the format for times (incl msecs). */
  public final static String TIME_FORMAT_MSECS = "HH:mm:ss.S";

  /** the format for dates. */
  public final static String DATE_FORMAT = "yyyy-MM-dd";

  /** the characters to backquote. */
  public final static char[] BACKQUOTE_CHARS = new char[]{'\t'};

  /** the backquoted string representations of characters to backquote. */
  public final static String[] BACKQUOTED_STRINGS = new String[]{"\\t"};

  /** the characters to escape. */
  public final static char[] ESCAPE_CHARS = new char[]{'\\', '\'', '\t', '\n', '\r', '"'};

  /** the escaped string representations of characters to backquote. */
  public final static String[] ESCAPE_STRINGS = new String[]{"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\""};
}
