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
 * ListAnnotationTags.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.util.List;

import adams.core.base.BaseAnnotation;
import adams.core.base.BaseAnnotation.Tag;
import adams.core.base.BaseRegExp;
import adams.flow.core.Actor;

/**
 * Processor that lists tags in annotations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5383 $
 */
public class ListAnnotationTags
  extends AbstractListingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -1049176597434370431L;

  /** the regular expression to match the tags against. */
  protected BaseRegExp m_RegExp;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all tags in actor annotations.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the regular expression that the tags must match.
   *
   * @param value	the regexp
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the tags must match.
   *
   * @return		the regexp
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return "The regular expression that the tags must match.";
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   * 
   * @param obj		the object to check
   * @return		true if valid
   */
  @Override
  protected boolean isValid(Object obj) {
    boolean		result;
    BaseAnnotation	ann;
    List<Tag>		tags;
    
    result = (obj instanceof Actor);
    if (result) {
      ann = ((Actor) obj).getAnnotations();
      if (m_RegExp.isMatchAll()) {
	result = ann.hasTag();
      }
      else {
	result = false;
	tags   = ann.getTags();
	for (Tag tag: tags) {
	  if (m_RegExp.isMatch(tag.getName())) {
	    result = true;
	    break;
	  }
	}
      }
    }
    
    return result;
  }

  /**
   * Returns the string representation of the object that is added to the list.
   * 
   * @param obj		the object to turn into a string
   * @return		the string representation, null if to ignore the item
   */
  @Override
  protected String objectToString(Object obj) {
    String		result;
    Actor		actor;
    List<Tag>		tags;
    String		tagStr;

    actor = (Actor) obj;
    if (actor.getFullName().indexOf(',') > -1)
      result = "\"" + actor.getFullName() + "\"";
    else
      result = actor.getFullName();
    tagStr = "";
    tags   = actor.getAnnotations().getTags();
    for (Tag tag: tags) {
      if (!m_RegExp.isMatch(tag.getName()))
	continue;
      if (tagStr.length() > 0)
	tagStr += ", ";
      tagStr += tag.getName();
    }

    // any matches?
    if (tagStr.length() == 0)
      result = null;
    else
      result +=  "," + "\"" + tagStr + "\"";
    
    return result;
  }
  
  /**
   * Finishes up the list.
   */
  @Override
  protected void finalizeList() {
    String	header;
    
    super.finalizeList();
    
    if (m_List.size() > 0) {
      header  = "Actor,";
      header += "Tag(s)";
      m_List.add(0, header);
    }
  }

  /**
   * Returns whether the list should be sorted.
   * 
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return true;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   * 
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   * 
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Tags found:";
  }
}
