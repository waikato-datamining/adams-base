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
 * BaseAnnotation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class used for annotating actors in the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseAnnotation
  extends BaseText {

  /** for serialization. */
  private static final long serialVersionUID = -8038455603270458019L;

  /**
   * Container class for storing tag information.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Tag
    implements Serializable, Comparable<Tag> {
    
    /** for serialization. */
    private static final long serialVersionUID = 874856743336401021L;

    /** the name of the tag. */
    protected String m_Name;
    
    /** the options. */
    protected HashMap<String,String> m_Options;
    
    /**
     * Instantiates the tag with the given name and options.
     * 
     * @param name	the name of the tag
     */
    public Tag(String name) {
      this(name, null);
    }
    
    /**
     * Instantiates the tag with the given name and options.
     * 
     * @param name	the name of the tag
     * @param options	the options of the tag, can be null
     */
    public Tag(String name, HashMap<String,String> options) {
      m_Name = name;
      if (options == null)
	m_Options = new HashMap<String,String>();
      else
	m_Options = (HashMap<String,String>) options.clone();
    }
    
    /**
     * Returns the tag name.
     * 
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }
    
    /**
     * Returns the tag options.
     * 
     * @return		the options
     */
    public HashMap<String,String> getOptions() {
      return m_Options;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     */
    @Override
    public int compareTo(Tag o) {
      int	result;
      
      if (o == null)
	throw new NullPointerException();
      
      result = m_Name.compareTo(o.getName());
      if (result == 0)
	result = new Integer(m_Options.size()).compareTo(o.getOptions().size());
      if (result == 0) {
	// this
	for (String key: m_Options.keySet()) {
	  if (!o.getOptions().containsKey(key)) {
	    result = 1;
	    break;
	  }
	  result = m_Options.get(key).compareTo(o.getOptions().get(key));
	  if (result != 0)
	    break;
	}
	// o
	for (String key: o.getOptions().keySet()) {
	  if (!m_Options.containsKey(key)) {
	    result = -1;
	    break;
	  }
	  result = m_Options.get(key).compareTo(o.getOptions().get(key));
	  if (result != 0)
	    break;
	}
      }
      
      return 0;
    }
    
    /**
     * Checks whether the two objects are the same.
     * Returns true if they are both tags with the same name and options.
     * 
     * @param obj	the object to compare with
     * @return		true if both tags and both the same name/options
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Tag)
	return (compareTo((Tag) obj) == 0);
      else
	return false;
    }
    
    /**
     * Returns a short string representation of the tag.
     * 
     * @return		the string
     */
    @Override
    public String toString() {
      return "name=" + m_Name + ", options=" + m_Options;
    }
    
    /**
     * Parses the tag string (!{...}) and generates a Tag object from it.
     * Format: !{name[:key=value[,key=value...]]}
     * 
     * @param s		the string to parse
     * @return		the tag or null if failed to parse
     */
    public static Tag parse(String s) {
      Tag			result;
      String			name;
      String[]			options;
      String[]			parts;
      HashMap<String,String>	map;
      
      result = null;
      
      if (s.indexOf(TAG_START) == -1)
	return result;
      if (s.indexOf(TAG_END) < s.indexOf(TAG_START))
	return result;
      s = s.substring(s.indexOf(TAG_START) + TAG_START.length());
      s = s.substring(0, s.indexOf(TAG_END));

      // options?
      if (s.indexOf(":") > -1) {
	parts   = s.split(":");
	name    = parts[0];
	options = parts[1].split(",");
	map     = new HashMap<String,String>();
	for (String option: options) {
	  parts = option.trim().split("=");
	  if (parts.length == 2)
	    map.put(parts[0], parts[1]);
	}
	result = new Tag(name, map);
      }
      else {
	name = s;
	result = new Tag(name);
      }
      
      return result;
    }
  }
  
  /** the start of a custom tag. */
  public final static String TAG_START = "!{";

  /** the end of a custom tag. */
  public final static String TAG_END = "}";
  
  /** the tags that have been located. */
  protected List<Tag> m_Tags;
  
  /** the parsed list of strings and tags. */
  protected List m_Parts;
  
  /**
   * Initializes the string with length 0.
   */
  public BaseAnnotation() {
    super();
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseAnnotation(String s) {
    super(s);
  }
  
  /**
   * Converts the string according to the specified conversion.
   * <p/>
   * Just resets the located tags.
   *
   * @param value	the string to convert
   * @return		the converted string
   */
  @Override
  protected String convert(String value) {
    m_Tags  = null;
    m_Parts = null;
    return super.convert(value);
  }
  
  /**
   * Returns whether at least one tag is present.
   * 
   * @return		true if tag is present
   */
  public boolean hasTag() {
    return (getValue().indexOf(TAG_START) > -1);
  }
  
  /**
   * Parses the annotation string, if necessary.
   */
  protected void parse() {
    List<Tag>	tags;
    List	parts;
    String	s;
    String	tagStr;
    Tag		tag;
    int		start;
    
    if (m_Tags != null)
      return;
    
    tags  = new ArrayList<Tag>();
    parts = new ArrayList();
    s     = getValue();
    while ((s.length() > 0) && ((start = s.indexOf(TAG_START)) > -1)) {
      if (start > 0)
	parts.add(s.substring(0, start));
      s = s.substring(start);
      if (s.indexOf(TAG_END) > -1) {
	tagStr = s.substring(0, s.indexOf(TAG_END) + TAG_END.length());
	s      = s.substring(s.indexOf(TAG_END) + TAG_END.length());
	tag    = Tag.parse(tagStr);
	if (tag != null) {
	  tags.add(tag);
	  parts.add(tag);
	}
      }
      else {
	if (s.length() > 0)
	  parts.add(s);
	s = "";
      }
    }
    if (s.length() > 0)
      parts.add(s);
    
    m_Tags  = tags;
    m_Parts = parts;
  }
  
  /**
   * Returns the extracted tags from the annotation.
   * 
   * @return		all the located tags
   */
  public synchronized List<Tag> getTags() {
    parse();
    return m_Tags;
  }
  
  /**
   * Returns the building blocks of the annotation, String and Tag objects.
   * 
   * @return		all the parts of the annotation string
   */
  public synchronized List getParts() {
    parse();
    return m_Parts;
  }
  
  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return 
	"An arbitrary annotation string, supports tags of format '" 
	+ TAG_START + "tagname[:comma-separated list of key=value pairs]" + TAG_END + "'.";
  }
}
