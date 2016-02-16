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
 * AbstractCsvArchive.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.net.TwitterHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.twitter.SimulatedPlace;
import adams.data.twitter.SimulatedScopes;
import adams.data.twitter.SimulatedStatus;
import adams.data.twitter.SimulatedSymbolEntity;
import adams.data.twitter.SimulatedURLEntity;
import adams.data.twitter.SimulatedUser;
import adams.data.twitter.TwitterField;
import twitter4j.GeoLocation;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Dummy replayer that just outputs tweets from manually set status texts.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-tweet &lt;adams.core.base.BaseString&gt; [-tweet ...] (property: tweets)
 * &nbsp;&nbsp;&nbsp;The status texts to generate the tweets from.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCsvArchive
  extends AbstractTweetReplay {

  /** for serialization. */
  private static final long serialVersionUID = -3432288686771377759L;

  /** the archive to obtain the tweets from. */
  protected PlaceholderFile m_Archive;

  /** the reader used for reading the CSV archive. */
  protected CsvSpreadSheetReader m_Reader;

  /** the current chunk. */
  protected SpreadSheet m_Chunk;

  /** the twitter field, column index relation. */
  protected HashMap<TwitterField,Integer> m_Columns;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "archive", "archive",
	    new PlaceholderFile("."));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Reader = new CsvSpreadSheetReader();
    m_Reader.setChunkSize(1);
    m_Reader.setParseFormulas(false);
    //m_Reader.setTextColumns(new Range(Range.ALL));
    m_Reader.setMissingValue("");
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Chunk   = null;
    m_Columns = null;
  }

  /**
   * Sets the ADAMS CSV archive file.
   *
   * @param value	the file
   */
  public void setArchive(PlaceholderFile value) {
    m_Archive = value;
    reset();
  }

  /**
   * Returns the ADAMS CSV archive file.
   *
   * @return		the file
   */
  public PlaceholderFile getArchive() {
    return m_Archive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String archiveTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "archive", m_Archive, "archive: ");
  }

  /**
   * Configures the mapping between column names and twitter fields.
   *
   * @param chunk	the spreadsheet to use as template
   * @return		the generated mapping
   */
  protected abstract HashMap<TwitterField,Integer> configureColumns(SpreadSheet chunk);

  /**
   * Performs the actual setup.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String doConfigure() {
    String	result;

    result  = null;

    m_Chunk = m_Reader.read(m_Archive);
    if (m_Chunk == null)
      result = "Failed to read from archive?";

    if (result == null)
      m_Columns = configureColumns(m_Chunk);

    return result;
  }

  /**
   * Checks whether there is another tweet available.
   *
   * @return		true if tweet available
   */
  @Override
  public boolean hasNext() {
    return (m_Chunk != null) || m_Reader.hasMoreChunks();
  }

  /**
   * Hook method for preprocessing the new chunk of data before converting it.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param chunk	the chunk to convert
   * @return		the preprocessed chunk
   */
  protected SpreadSheet preprocessChunk(SpreadSheet chunk) {
    return chunk;
  }

  /**
   * Converts the row to a status object.
   *
   * @param row		the row to convert
   * @return		the generated status
   */
  protected Status convert(Row row) {
    SimulatedStatus	result;
    int			index;
    Cell		cell;
    String[]		parts;
    URLEntity[]		urlEntities;
    SymbolEntity[]	symEntities;
    Scopes		scopes;
    int			i;

    result = new SimulatedStatus();
    result.setUser(new SimulatedUser());
    result.setPlace(new SimulatedPlace());
    if (   m_Columns.containsKey(TwitterField.RETWEETED_STATUS_ID)
	|| m_Columns.containsKey(TwitterField.RETWEETED_STATUS_USER_ID)
	|| m_Columns.containsKey(TwitterField.RETWEETED_STATUS_CREATED)) {
      result.setRetweetedStatus(new SimulatedStatus());
      ((SimulatedStatus) result.getRetweetedStatus()).setUser(new SimulatedUser());
    }
    for (TwitterField field: m_Columns.keySet()) {
      index = m_Columns.get(field);
      try {
	if (!row.hasCell(index) || row.getCell(index).isMissing())
	  continue;
      }
      catch (Throwable t) {
	if (isLoggingEnabled())
	  Utils.handleException(this, "Failed to check missing status of cell at #" + index, t);
	continue;
      }
      cell = row.getCell(index);

      try {
	switch (field) {
	  case ID:
	    result.setId(cell.toLong());
	    break;
	  case USER_ID:
	    ((SimulatedUser) result.getUser()).setId(cell.toLong());
	    break;
	  case USER_NAME:
	    ((SimulatedUser) result.getUser()).setName(cell.getContent());
	    break;
	  case SOURCE:
	    result.setSource(cell.getContent());
	    break;
	  case TEXT:
	    result.setText(cell.getContent());
	    result.setHashtagEntities(TwitterHelper.extractHashtags(cell.getContent()));
	    result.setUserMentionEntities(TwitterHelper.extractUserMentions(cell.getContent()));
	    break;
	  case CREATED:
	    result.setCreatedAt(cell.toAnyDateType());
	    break;
	  case RETWEET:
	    result.setRetweet(cell.toBoolean());
	    break;
	  case RETWEET_COUNT:
	    result.setRetweetCount(cell.toLong().intValue());
	    break;
	  case RETWEET_BY_ME:
	    result.setRetweetedByMe(cell.toBoolean());
	    break;
	  case RETWEETED_STATUS_ID:
	    ((SimulatedStatus) result.getRetweetedStatus()).setId(cell.toLong());
	    break;
	  case RETWEETED_STATUS_USER_ID:
	    ((SimulatedUser) ((SimulatedStatus) result.getRetweetedStatus()).getUser()).setId(cell.toLong());
	    break;
	  case RETWEETED_STATUS_CREATED:
	    ((SimulatedStatus) result.getRetweetedStatus()).setCreatedAt(cell.toAnyDateType());
	    break;
	  case EXPANDED_URLS:
	    parts = cell.getContent().split(",");
	    if (parts.length > 0) {
	      urlEntities = new URLEntity[parts.length];
	      for (i = 0; i < parts.length; i++)
		urlEntities[i] = new SimulatedURLEntity(parts[i]);
	      result.setURLEntities(urlEntities);
	    }
	    break;
	  case IN_REPLY_TO_STATUS_ID:
	    result.setInReplyToStatusId(cell.toLong());
	    break;
	  case IN_REPLY_TO_USER_ID:
	    result.setInReplyToUserId(cell.toLong());
	    break;
	  case FAVORITED:
	    if (cell.isDouble())
	      result.setFavorited(cell.toDouble() != 0.0);
	    else
	      result.setFavorited(cell.toBoolean());
	    break;
	  case COUNTRY:
	    ((SimulatedPlace) result.getPlace()).setCountry(cell.getContent());
	    break;
	  case COUNTRY_CODE:
	    ((SimulatedPlace) result.getPlace()).setCountryCode(cell.getContent());
	    break;
	  case LANGUAGE_CODE:
	    ((SimulatedUser) result.getUser()).setLang(cell.getContent());
	    break;
	  case PLACE:
	    ((SimulatedPlace) result.getPlace()).setName(cell.getContent());
	    break;
	  case PLACE_TYPE:
	    ((SimulatedPlace) result.getPlace()).setPlaceType(cell.getContent());
	    break;
	  case PLACE_URL:
	    ((SimulatedPlace) result.getPlace()).setURL(cell.getContent());
	    break;
	  case STREET_ADDRESS:
	    ((SimulatedPlace) result.getPlace()).setStreetAddress(cell.getContent());
	    break;
	  case POSSIBLY_SENSITIVE:
	    if (cell.isDouble())
	      result.setPossiblySensitive(cell.toDouble() != 0.0);
	    else
	      result.setPossiblySensitive(cell.toBoolean());
	    break;
	  case GEO_LATITUDE:
	  case GEO_LONGITUDE:
	    if (result.getGeoLocation() == null)
	      result.setGeoLocation(
		  new GeoLocation(
		      row.getCell(m_Columns.get(TwitterField.GEO_LATITUDE)).toDouble(),
		      row.getCell(m_Columns.get(TwitterField.GEO_LONGITUDE)).toDouble()));
	    break;
	  case SYMBOL_ENTITIES:
	    parts       = cell.getContent().split(",");
	    symEntities = new SymbolEntity[parts.length];
	    for (i = 0; i < parts.length; i++) {
	      symEntities[i] = new SimulatedSymbolEntity();
	      ((SimulatedSymbolEntity) symEntities[i]).setText(parts[i]);
	    }
	    result.setSymbolEntities(symEntities);
	    break;
	  case IS_RETWEETED:
	    result.setIsRetweeted(cell.toBoolean());
	    break;
	  case FAVORITE_COUNT:
	    result.setFavoriteCount(cell.toLong().intValue());
	    break;
	  case STATUS_LANG:
	    result.setLang(cell.getContent());
	    break;
	  case SCOPES:
	    parts  = cell.getContent().split(",");
	    scopes = new SimulatedScopes();
	    ((SimulatedScopes) scopes).setPlaceIds(parts);
	    result.setScopes(scopes);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled twitter field: " + field);
	}
      }
      catch (Throwable t) {
	if (isLoggingEnabled())
	  Utils.handleException(this, "Failed to convert field '" + field + "': " + cell + "\n" + row, t);
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the next tweet.
   *
   * @return		the next tweet, null if none available
   */
  @Override
  public Status next() {
    Status	result;

    result = null;

    if (m_Chunk.getRowCount() > 0)
      result = convert(m_Chunk.getRow(0));

    m_Chunk = m_Reader.nextChunk();

    return result;
  }
}
