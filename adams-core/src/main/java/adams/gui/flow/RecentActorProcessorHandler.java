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
 * RecentActorProcessorHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import adams.core.Shortening;
import adams.core.option.OptionUtils;
import adams.flow.processor.ActorProcessor;
import adams.flow.processor.ListAllVariables;
import adams.gui.core.AbstractRecentItemsHandler;

/**
 * Recent actor processors handler.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RecentActorProcessorHandler<M>
  extends AbstractRecentItemsHandler<M, ActorProcessor> {

  private static final long serialVersionUID = 8256752129584941151L;

  /** the property for storing the number of recent statements. */
  public final static String RECENTPROCESSORS_COUNT = "RecentProcessorsCount";

  /** the property prefix for a recent statement. */
  public final static String RECENTPROCESSORS_PREFIX = "RecentProcessor.";

  /** the maximum chars in for the menu item. */
  public final static int MAX_CHARS = 40;

  /**
   * Initializes the handler with a maximum of 5 items.
   *
   * @param propsFile the props file to store the items in
   * @param menu      the menu to add the recent items as subitems to
   */
  public RecentActorProcessorHandler(String propsFile, M menu) {
    super(propsFile, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile the props file to store the items in
   * @param maxCount  the maximum number of items to keep in menu
   * @param menu      the menu to add the recent items as subitems to
   */
  public RecentActorProcessorHandler(String propsFile, int maxCount, M menu) {
    super(propsFile, maxCount, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile  the props file to store the items in
   * @param propPrefix the properties prefix, use null to ignore
   * @param maxCount   the maximum number of items to keep in menu
   * @param menu       the menu to add the recent items as subitems to
   */
  public RecentActorProcessorHandler(String propsFile, String propPrefix, int maxCount, M menu) {
    super(propsFile, propPrefix, maxCount, menu);
  }

  /**
   * Returns the key to use for the counts in the props file.
   *
   * @return		the key
   */
  @Override
  protected String getCountKey() {
    return RECENTPROCESSORS_COUNT;
  }

  /**
   * Returns the key prefix to use for the items in the props file.
   *
   * @return		the prefix
   */
  @Override
  protected String getItemPrefix() {
    return RECENTPROCESSORS_PREFIX;
  }

  /**
   * Turns an object into a string for storing in the props.
   *
   * @param obj the object to convert
   * @return the string representation
   */
  @Override
  protected String toString(ActorProcessor obj) {
    return obj.toCommandLine();
  }

  /**
   * Turns the string obtained from the props into an object again.
   *
   * @param s the string representation
   * @return the parsed object
   */
  @Override
  protected ActorProcessor fromString(String s) {
    try {
      return (ActorProcessor) OptionUtils.forCommandLine(ActorProcessor.class, s);
    }
    catch (Exception e) {
      return new ListAllVariables();
    }
  }

  /**
   * Generates the text for the menuitem.
   *
   * @param index the index of the item
   * @param item  the item itself
   * @return the generated text
   */
  @Override
  protected String createMenuItemText(int index, ActorProcessor item) {
    String	result;

    result = item.toCommandLine();
    result = result.substring(item.getClass().getPackage().getName().length() + 1);
    result = Shortening.shortenEnd(result, MAX_CHARS);

    return result;
  }
}
