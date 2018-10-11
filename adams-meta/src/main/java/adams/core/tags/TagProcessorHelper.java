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
 * TagProcessorHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.tags;

import adams.core.base.BaseKeyValuePair;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for {@link TagProcessor} classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TagProcessorHelper {

  /**
   * Checks whether the specified tag is present.
   *
   * @param obj		the object to check
   * @param tag		the tag to look for
   * @return		true if the object supports tags and the tag is present
   */
  public static boolean hasTag(Object obj, String tag) {
    List<BaseKeyValuePair>	tags;

    if (!(obj instanceof TagHandler))
      return false;

    tags = ((TagHandler) obj).getAllTags();
    for (BaseKeyValuePair t: tags) {
      if (t.getPairKey().equals(tag))
        return true;
    }

    return false;
  }

  /**
   * Returns the value of the tag if present.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @return		the associated value, otherwise null
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  protected static String getTagValue(Object obj, String tag) {
    List<BaseKeyValuePair>	tags;

    if (!(obj instanceof TagHandler))
      return null;

    tags = ((TagHandler) obj).getAllTags();
    for (BaseKeyValuePair t: tags) {
      if (t.getPairKey().equals(tag))
        return t.getPairValue();
    }

    return null;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static String getTagString(Object obj, String tag, String defValue) {
    String	result;

    result = getTagValue(obj, tag);
    if (result == null)
      result = defValue;

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static boolean getTagBoolean(Object obj, String tag, boolean defValue) {
    boolean	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Boolean.parseBoolean(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static byte getTagByte(Object obj, String tag, byte defValue) {
    byte		result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Byte.parseByte(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static int getTagInt(Object obj, String tag, int defValue) {
    int		result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Integer.parseInt(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static long getTagLong(Object obj, String tag, long defValue) {
    long	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Long.parseLong(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static float getTagFloat(Object obj, String tag, float defValue) {
    float	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Float.parseFloat(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static double getTagDouble(Object obj, String tag, double defValue) {
    double	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag);
    if (value != null) {
      try {
        result = Double.parseDouble(value);
      }
      catch (Exception e) {
        result = defValue;
      }
    }

    return result;
  }

  /**
   * Retrieves all tags from the tag handler,.
   *
   * @param handler 	the tag handler
   * @return		all tags
   */
  public static List<BaseKeyValuePair> getAllTags(TagHandler handler) {
    return new ArrayList<>(handler.getAllTags());
  }

  /**
   * Adds the tags to the map.
   *
   * @param map		the map for storing the tags (using their key as the map's key)
   * @param tags	the tags to add
   * @param override	if true, then existing tags can be replaced; otherwise only non-existing tags get added
   */
  protected static void addToMap(Map<String,BaseKeyValuePair> map, List<BaseKeyValuePair> tags, boolean override) {
    addToMap(map, tags.toArray(new BaseKeyValuePair[tags.size()]), override);
  }

  /**
   * Adds the tags to the map.
   *
   * @param map		the map for storing the tags (using their key as the map's key)
   * @param tags	the tags to add
   * @param override	if true, then existing tags can be replaced; otherwise only non-existing tags get added
   */
  protected static void addToMap(Map<String,BaseKeyValuePair> map, BaseKeyValuePair[] tags, boolean override) {
    for (BaseKeyValuePair tag: tags) {
      if (map.containsKey(tag.getPairValue()) && override)
        map.put(tag.getPairKey(), tag);
      else if (!map.containsKey(tag.getPairKey()))
        map.put(tag.getPairKey(), tag);
    }
  }

  /**
   * Retrieves all tags from the actor, going up in the actor tree, with lower ones overriding
   * ones defined higher up.
   *
   * @param actor	the actor to start
   * @param traverse 	whether to traverse upwards or not traverse at all
   * @return		all tags
   */
  public static List<BaseKeyValuePair> getAllTags(Actor actor, boolean traverse) {
    List<BaseKeyValuePair>		result;
    Map<String,BaseKeyValuePair>	tags;
    Actor 				parent;

    tags = new HashMap<>();
    if (actor instanceof TagHandler)
      addToMap(tags, ((TagHandler) actor).getTags(), false);

    if (traverse) {
      parent = actor.getParent();
      while (parent != null) {
	if (parent instanceof TagHandler) {
	  addToMap(tags, ((TagHandler) parent).getAllTags(), false);
	  // other actors do it recursively as well, so we can skip going higher up
	  break;
	}
	parent = parent.getParent();
      }
    }

    result = new ArrayList<>(tags.values());

    return result;
  }

  /**
   * Retrieves all tags from the actor, going up in the actor tree, with lower ones overriding
   * ones defined higher up.
   *
   * @param node	the actor to start
   * @param traverse 	whether to traverse upwards or not traverse at all
   * @return		all tags
   */
  public static List<BaseKeyValuePair> getAllTags(Node node, boolean traverse) {
    List<BaseKeyValuePair>		result;
    Map<String,BaseKeyValuePair>	tags;
    Node 				parent;

    tags = new HashMap<>();
    if (node.getActor() instanceof TagHandler)
      addToMap(tags, ((TagHandler) node.getActor()).getTags(), false);

    if (traverse) {
      parent = (Node) node.getParent();
      while (parent != null) {
	if (parent.getActor() instanceof TagHandler) {
	  addToMap(tags, getAllTags(parent, traverse), false);
	  // other actors do it recursively as well, so we can skip going higher up
	  break;
	}
	parent = (Node) parent.getParent();
      }
    }

    result = new ArrayList<>(tags.values());

    return result;
  }
}
