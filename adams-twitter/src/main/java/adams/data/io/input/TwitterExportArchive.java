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
 * TwitterExportArchive.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.util.HashMap;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.twitter.TwitterField;

/**
 <!-- globalinfo-start -->
 * Replays tweets stored in a CSV file generate by a Twitter export.<br>
 * Extracts hashtags ('#...') and usermentions ('&#64;...') automatically.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-archive &lt;adams.core.io.PlaceholderFile&gt; (property: archive)
 * &nbsp;&nbsp;&nbsp;The Twitter export CSV file to load the tweets from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterExportArchive
  extends AbstractCsvArchive {

  /** for serialization. */
  private static final long serialVersionUID = -3432288686771377759L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Replays tweets stored in a CSV file generate by a Twitter export.\n"
	+ "Extracts hashtags ('#...') and usermentions ('@...') automatically.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String archiveTipText() {
    return "The Twitter export CSV file to load the tweets from.";
  }

  /**
   * Configures the mapping between column names and twitter fields.
   *
   * @param chunk	the spreadsheet to use as template
   * @return		the generated mapping
   */
  @Override
  protected HashMap<TwitterField,Integer> configureColumns(SpreadSheet chunk) {
    HashMap<TwitterField,Integer>	result;
    Row					row;
    int					i;

    result = new HashMap<TwitterField,Integer>();
    row       = m_Chunk.getHeaderRow();
    for (i = 0; i < m_Chunk.getColumnCount(); i++) {
      switch (row.getCell(i).getContent()) {
	case "tweet_id":
	  result.put(TwitterField.ID, i);
	  break;
	case "in_reply_to_status_id":
	  result.put(TwitterField.IN_REPLY_TO_STATUS_ID, i);
	  break;
	case "in_reply_to_user_id":
	  result.put(TwitterField.IN_REPLY_TO_USER_ID, i);
	  break;
	case "timestamp":
	  result.put(TwitterField.CREATED, i);
	  break;
	case "source":
	  result.put(TwitterField.SOURCE, i);
	  break;
	case "text":
	  result.put(TwitterField.TEXT, i);
	  break;
	case "retweeted_status_id":
	  result.put(TwitterField.RETWEETED_STATUS_ID, i);
	  break;
	case "retweeted_status_user_id":
	  result.put(TwitterField.RETWEETED_STATUS_USER_ID, i);
	  break;
	case "retweeted_status_timestamp":
	  result.put(TwitterField.RETWEETED_STATUS_CREATED, i);
	  break;
	case "expanded_urls":
	  result.put(TwitterField.EXPANDED_URLS, i);
	  break;
      }
    }

    return result;
  }
}
