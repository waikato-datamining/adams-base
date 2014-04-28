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
 * FixDeprecatedEmail.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;
import adams.flow.sink.Email;
import adams.flow.sink.SendEmail;
import adams.flow.transformer.CreateEmail;

/**
 * Replaces the {@link Email} sink with {@link CreateEmail}
 * and {@link SendEmail} instances.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public class FixDeprecatedEmail
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -4170658262349662939L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Replaces the Email sink with CreateEmail "
	+ "and SendEmail instances.";
  }

  /**
   * Transfers the configuration from the Email actor to the CreateEmail one.
   * 
   * @param email	the source
   * @return		the configured actor
   */
  protected CreateEmail toCreateEmail(Email email) {
    CreateEmail	result;
    String	var;
    
    result = new CreateEmail();
    result.setName(email.getName());

    // from
    var = email.getOptionManager().getVariableForProperty("sender");
    if (var != null)
      result.getOptionManager().setVariableForProperty("sender", var);
    else
      result.setSender(email.getSender());

    // to
    var = email.getOptionManager().getVariableForProperty("recipients");
    if (var != null)
      result.getOptionManager().setVariableForProperty("recipients", var);
    else
      result.setRecipients(email.getRecipients());

    // cc
    var = email.getOptionManager().getVariableForProperty("CC");
    if (var != null)
      result.getOptionManager().setVariableForProperty("CC", var);
    else
      result.setCC(email.getCC());

    // bcc
    var = email.getOptionManager().getVariableForProperty("BCC");
    if (var != null)
      result.getOptionManager().setVariableForProperty("BCC", var);
    else
      result.setBCC(email.getBCC());

    // subject
    var = email.getOptionManager().getVariableForProperty("subject");
    if (var != null)
      result.getOptionManager().setVariableForProperty("subject", var);
    else
      result.setSubject(email.getSubject());

    // body
    var = email.getOptionManager().getVariableForProperty("body");
    if (var != null)
      result.getOptionManager().setVariableForProperty("body", var);
    else
      result.setBody(email.getBody());

    // signature
    var = email.getOptionManager().getVariableForProperty("signature");
    if (var != null)
      result.getOptionManager().setVariableForProperty("signature", var);
    else
      result.setSignature(email.getSignature());
    
    return result;
  }

  /**
   * Transfers the configuration from the Email actor to the SendEmail one.
   * 
   * @param email	the source
   * @return		the configured actor
   */
  protected SendEmail toSendEmail(Email email) {
    SendEmail	result;
    String	var;
    
    result = new SendEmail();
    result.setName(email.getName());
    var    = email.getOptionManager().getVariableForProperty("sendEmail");
    if (var != null)
      result.getOptionManager().setVariableForProperty("sendEmail", var);
    else
      result.setSendEmail(email.getSendEmail());
    
    return result;
  }
  
  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    List<AbstractActor> 	emails;
    Email			eactor;
    SendEmail			semail;
    CreateEmail			cemail;
    int				index;
    
    emails = ActorUtils.enumerate(actor, new Class[]{Email.class});
    for (AbstractActor email: emails) {
      eactor = (Email) email;
      cemail = toCreateEmail(eactor);
      semail = toSendEmail(eactor);
      index  = eactor.index();
      ((MutableActorHandler) eactor.getParent()).add(index, cemail);
      ((MutableActorHandler) eactor.getParent()).set(index + 1, semail);
      m_Modified = true;
    }
  }
}
