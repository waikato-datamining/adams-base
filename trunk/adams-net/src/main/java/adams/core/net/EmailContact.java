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
 * EmailContact.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Encapsulates an email contact.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailContact
  extends AbstractOptionHandler
  implements Comparable<EmailContact>, ShallowCopySupporter<EmailContact> {

  /** for serialization. */
  private static final long serialVersionUID = 8140311860464886389L;

  /** the first name. */
  protected String m_FirstName;

  /** the last name. */
  protected String m_LastName;
  
  /** the email address. */
  protected String m_Email;
  
  /** the address. */
  protected String m_Address;
  
  /** the phone. */
  protected String m_Phone;
  
  /** the note. */
  protected String m_Note;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates a basic email contact.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "first", "firstName",
	    "");

    m_OptionManager.add(
	    "last", "lastName",
	    "");

    m_OptionManager.add(
	    "email", "email",
	    "");

    m_OptionManager.add(
	    "phone", "phone",
	    "");

    m_OptionManager.add(
	    "address", "address",
	    "");

    m_OptionManager.add(
	    "note", "note",
	    "");
  }
  
  /**
   * Sets the first name.
   * 
   * @param value	the first name
   */
  public void setFirstName(String value) {
    if (value == null)
      value = "";
    m_FirstName = value;
    reset();
  }
  
  /**
   * Returns the first name.
   * 
   * @return		the first name
   */
  public String getFirstName() {
    return m_FirstName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstNameTipText() {
    return "The first name.";
  }

  /**
   * Sets the last name.
   * 
   * @param value	the last name
   */
  public void setLastName(String value) {
    if (value == null)
      value = "";
    m_LastName = value;
    reset();
  }
  
  /**
   * Returns the last name.
   * 
   * @return		the last name
   */
  public String getLastName() {
    return m_LastName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lastNameTipText() {
    return "The last name.";
  }
  
  /**
   * Sets the email.
   * 
   * @param value	the email
   */
  public void setEmail(String value) {
    if (value == null)
      value = "";
    m_Email = value;
    reset();
  }
  
  /**
   * Returns the email name.
   * 
   * @return		the email
   */
  public String getEmail() {
    return m_Email;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String emailTipText() {
    return "The email address (uniquely identifies a contact).";
  }
  
  /**
   * Sets the phone.
   * 
   * @param value	the phone
   */
  public void setPhone(String value) {
    if (value == null)
      value = "";
    m_Phone = value;
    reset();
  }
  
  /**
   * Returns the phone.
   * 
   * @return		the phone
   */
  public String getPhone() {
    return m_Phone;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String phoneTipText() {
    return "An optional phone.";
  }
  
  /**
   * Sets the address.
   * 
   * @param value	the address
   */
  public void setAddress(String value) {
    if (value == null)
      value = "";
    m_Address = value;
    reset();
  }
  
  /**
   * Returns the address.
   * 
   * @return		the address
   */
  public String getAddress() {
    return m_Address;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addressTipText() {
    return "An optional address.";
  }
  
  /**
   * Sets the note.
   * 
   * @param value	the note
   */
  public void setNote(String value) {
    if (value == null)
      value = "";
    m_Note = value;
    reset();
  }
  
  /**
   * Returns the note.
   * 
   * @return		the note
   */
  public String getNote() {
    return m_Note;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noteTipText() {
    return "An optional note.";
  }

  /**
   * Compares this contact with the provided one.
   * 
   * @param o		the contact to compare with
   * @return		-1 if less than, 0 if equals, +1 if greater than
   * 			the provided contact
   */
  @Override
  public int compareTo(EmailContact o) {
    int		result;
    
    if (o == null)
      return 1;
    
    result = m_Email.compareTo(o.m_Email);
    if (result == 0)
      result = m_LastName.compareTo(o.m_LastName);
    if (result == 0)
      result = m_FirstName.compareTo(o.m_FirstName);
    if (result == 0)
      result = m_Phone.compareTo(o.m_Phone);
    if (result == 0)
      result = m_Address.compareTo(o.m_Address);
    if (result == 0)
      result = m_Note.compareTo(o.m_Note);
    
    return result;
  }
  
  /**
   * Compares this object against the specified one.
   * 
   * @param obj		the object to compare with
   * @return		true if the same object
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof EmailContact)
      return (compareTo((EmailContact) obj) == 0);
    else
      return false;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public EmailContact shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public EmailContact shallowCopy(boolean expand) {
    return (EmailContact) OptionUtils.shallowCopy(this, expand);
  }
  
  /**
   * Turns the contact into an email address.
   * 
   * @return		the generated email address
   */
  public EmailAddress toEmailAddress() {
    EmailAddress	result;
    String		name;
    
    name = m_FirstName + " " + m_LastName;
    name = name.trim();
    
    if (name.length() > 0)
      result = new EmailAddress(Utils.doubleQuote(name) + " <" + getEmail() + ">");
    else
      result = new EmailAddress(getEmail());
    
    return result;
  }
}
