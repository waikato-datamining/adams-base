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
 * Copyright (C) 2017-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.tags;

import adams.core.ClassLister;
import adams.core.Variables;
import adams.core.logging.LoggingHelper;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Helper class for {@link TagProcessor} classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TagProcessorHelper {

  /** for caching the tag info per tag processor. */
  protected static Map<Class,Set<TagInfo>> m_SupportedTagInfoCache;

  /** for caching the tag info per applicable. */
  protected static Map<Class,Set<TagInfo>> m_ApplicableTagInfoCache;

  /** the lookup by name. */
  protected static Map<String,TagInfo> m_TagInfos;

  /**
   * Checks whether the specified tag is present.
   *
   * @param obj		the object to check
   * @param tag		the tag to look for
   * @return		true if the object supports tags and the tag is present
   */
  public static boolean hasTag(Object obj, String tag) {
    List<Tag>	tags;

    if (!(obj instanceof TagHandler))
      return false;

    tags = ((TagHandler) obj).getAllTags();
    for (Tag t: tags) {
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
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise null
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  protected static String getTagValue(Object obj, String tag, Variables vars) {
    String	result;
    List<Tag>	tags;

    if (!(obj instanceof TagHandler))
      return null;

    tags = ((TagHandler) obj).getAllTags();
    for (Tag t: tags) {
      if (t.getPairKey().equals(tag)) {
        result = t.getPairValue();
        if ((vars != null) && (result.contains(Variables.START))) {
          result = vars.expand(result);
          // failed to expand? use null
          if (result.contains(Variables.START))
            result = null;
        }
        return result;
      }
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
    return getTagString(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static String getTagString(Object obj, String tag, String defValue, Variables vars) {
    String	result;

    result = getTagValue(obj, tag, vars);
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
    return getTagBoolean(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static boolean getTagBoolean(Object obj, String tag, boolean defValue, Variables vars) {
    boolean	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
    if (value != null) {
      try {
	result = Boolean.parseBoolean(value);
      }
      catch (Exception e) {
	// ignored
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
    return getTagByte(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static byte getTagByte(Object obj, String tag, byte defValue, Variables vars) {
    byte		result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
    if (value != null) {
      try {
	result = Byte.parseByte(value);
      }
      catch (Exception e) {
	// ignored
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
    return getTagInt(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static int getTagInt(Object obj, String tag, int defValue, Variables vars) {
    int		result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
    if (value != null) {
      try {
	result = Integer.parseInt(value);
      }
      catch (Exception e) {
	// ignored
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
    return getTagLong(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static long getTagLong(Object obj, String tag, long defValue, Variables vars) {
    long	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
    if (value != null) {
      try {
	result = Long.parseLong(value);
      }
      catch (Exception e) {
	// ignored
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
    return getTagFloat(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static float getTagFloat(Object obj, String tag, float defValue, Variables vars) {
    float	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
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
    return getTagDouble(obj, tag, defValue, null);
  }

  /**
   * Returns the value of the tag if present, otherwise the default value.
   *
   * @param obj		the object to obtain the tag value from
   * @param tag		the tag to look for
   * @param defValue	the default value
   * @param vars	the variables to use, ignored if null
   * @return		the associated value, otherwise the default value
   * 			(eg if the object is not a tag handler or the tag
   * 			is not present)
   */
  public static double getTagDouble(Object obj, String tag, double defValue, Variables vars) {
    double	result;
    String	value;

    result = defValue;
    value  = getTagValue(obj, tag, vars);
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
  public static List<Tag> getAllTags(TagHandler handler) {
    return new ArrayList<>(handler.getAllTags());
  }

  /**
   * Adds the tags to the map.
   *
   * @param map		the map for storing the tags (using their key as the map's key)
   * @param tags	the tags to add
   * @param override	if true, then existing tags can be replaced; otherwise only non-existing tags get added
   */
  protected static void addToMap(Map<String,Tag> map, List<Tag> tags, boolean override) {
    addToMap(map, tags.toArray(new Tag[0]), override);
  }

  /**
   * Adds the tags to the map.
   *
   * @param map		the map for storing the tags (using their key as the map's key)
   * @param tags	the tags to add
   * @param override	if true, then existing tags can be replaced; otherwise only non-existing tags get added
   */
  protected static void addToMap(Map<String,Tag> map, Tag[] tags, boolean override) {
    for (Tag tag: tags) {
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
  public static List<Tag> getAllTags(Actor actor, boolean traverse) {
    List<Tag>		result;
    Map<String,Tag>	tags;
    Actor 			parent;

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
  public static List<Tag> getAllTags(Node node, boolean traverse) {
    List<Tag>		result;
    Map<String,Tag>	tags;
    Node 		parent;

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

  /**
   * Returns the list of all classes that implement the TagProcessor interface.
   *
   * @return		the classes
   */
  public static Class[] getTagProcessors() {
    return ClassLister.getSingleton().getClasses(TagProcessor.class);
  }

  /**
   * Initializes the caches for the TagInfo items.
   */
  protected static synchronized void initTagInfoCache() {
    Map<Class,Set<TagInfo>> 	supportedCache;
    Map<Class,Set<TagInfo>> 	applicableCache;
    Map<String,TagInfo>		infos;
    TagProcessor 		processor;
    Set<TagInfo> 		supported;

    if (m_ApplicableTagInfoCache == null) {
      supportedCache  = new HashMap<>();
      applicableCache = new HashMap<>();
      infos           = new HashMap<>();
      for (Class cls: getTagProcessors()) {
	try {
	  processor = (TagProcessor) cls.getDeclaredConstructor().newInstance();
	  supported = processor.getSupportedTags();
	  supportedCache.put(cls, supported);
	  for (TagInfo info: supported) {
	    for (Class appliesTo: info.getAppliesTo()) {
	      if (!applicableCache.containsKey(appliesTo))
		applicableCache.put(appliesTo, new HashSet<>());
	      applicableCache.get(appliesTo).add(info);
	    }
	    infos.put(info.getName(), info);
	  }
	}
	catch (Exception e) {
	  LoggingHelper.global().log(Level.SEVERE, "Failed to instantiate tag processor class: " + cls.getName(), e);
	}
      }
      m_SupportedTagInfoCache  = supportedCache;
      m_ApplicableTagInfoCache = applicableCache;
      m_TagInfos               = infos;
    }
  }

  /**
   * Returns all the tags that can be applied to the specified class.
   *
   * @param cls		the class to look up the tags for
   * @return		the applicable tags
   */
  public static List<TagInfo> getApplicableTags(Class cls) {
    List<TagInfo>	result;
    Set<TagInfo>	tags;

    initTagInfoCache();

    tags   = m_ApplicableTagInfoCache.getOrDefault(cls, new HashSet<>());
    result = new ArrayList<>(tags);
    Collections.sort(result);

    return result;
  }

  /**
   * Returns all the tags that can are supported by the specified class.
   *
   * @param cls		the class to look up the tags for
   * @return		the supported tags
   */
  public static List<TagInfo> getSupportedTags(Class cls) {
    List<TagInfo>	result;
    Set<TagInfo>	tags;

    initTagInfoCache();

    tags   = m_SupportedTagInfoCache.getOrDefault(cls, new HashSet<>());
    result = new ArrayList<>(tags);
    Collections.sort(result);

    return result;
  }

  /**
   * Returns a list of all possible tags.
   *
   * @return		the tags
   */
  public static List<TagInfo> getAllTags() {
    List<TagInfo>	result;
    Set<TagInfo>	all;

    initTagInfoCache();

    all = new HashSet<>();
    for (Class cls: m_SupportedTagInfoCache.keySet())
      all.addAll(m_SupportedTagInfoCache.get(cls));

    result = new ArrayList<>(all);
    Collections.sort(result);

    return result;
  }

  /**
   * Returns the tag info for the tag's name.
   *
   * @param name	the name of the tag to get the info for
   * @return		the info or null if unknown name
   */
  public static TagInfo getTagInfo(String name) {
    initTagInfoCache();

    return m_TagInfos.get(name);
  }
}
