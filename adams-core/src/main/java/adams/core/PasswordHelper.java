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
 * PasswordHelper.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.InteractiveActor;
import adams.flow.core.StopHelper;
import adams.gui.dialog.PasswordDialog;

import java.awt.Dialog.ModalityType;

/**
 * Helper methods for dealing with passwords (prompts, env vars).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PasswordHelper {

  /**
   * Obtains the password from the specified environment variable.
   *
   * @param context	the context for obtaining the env var name
   * @return		null if no env var specified or successfully obtained, otherwise error message
   * @param <T>		the type of context
   */
  public static <T extends EnvironmentPasswordSupporter> String fromEnvVar(T context) {
    String	result;
    String	password;

    result = null;

    if (!context.getPasswordEnvVar().isEmpty()) {
      password = System.getenv(context.getPasswordEnvVar());
      if (password == null)
	result = "Environment variable for password not set: " + context.getPasswordEnvVar();
      else
	context.setActualPassword(new BasePassword(password));
    }

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   * @param <T>		the type of actor
   */
  public static <T extends InteractiveActor & PasswordPrompter> String interact(T context) {
    BasePassword password;

    password = promptPassword(context);
    context.setActualPassword(password);
    if (password == null)
      return InteractiveActor.INTERACTION_CANCELED;
    else
      return null;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		null if successfully interacted, otherwise error message
   * @param <T>		the type of actor
   */
  public static <T extends InteractiveActor & PasswordPrompter> String interactHeadless(T context) {
    String		result;
    BasePassword 	password;

    result   = InteractiveActor.INTERACTION_CANCELED;
    password = ConsoleHelper.enterPassword("Please enter password (" + context.getName() + "):");
    if (password != null) {
      result = null;
      context.setActualPassword(password);
    }

    return result;
  }

  /**
   * Performs the prompting, if necessary.
   *
   * @param context	the actor that triggered the prompt
   * @return		null if successfully prompted, otherwise error message
   * @param <T>		the type of actor
   */
  public static <T extends InteractiveActor & PasswordPrompter> String prompt(T context) {
    String	result;
    String	msg;

    result = null;

    if (context.getPromptForPassword() && (context.getActualPassword().getValue().isEmpty())) {
      if (!context.isHeadless()) {
	msg = context.doInteract();
	if (msg != null) {
	  if (context.getStopFlowIfCanceled()) {
	    if ((context.getCustomStopMessage() == null) || (context.getCustomStopMessage().trim().isEmpty()))
	      StopHelper.stop(context, context.getStopMode(), "Flow canceled: " + context.getFullName());
	    else
	      StopHelper.stop(context, context.getStopMode(), context.getCustomStopMessage());
	    result = context.getStopMessage();
	  }
	}
      }
      else if (context.supportsHeadlessInteraction()) {
	msg = context.doInteractHeadless();
	if (msg != null) {
	  if (context.getStopFlowIfCanceled()) {
	    if ((context.getCustomStopMessage() == null) || (context.getCustomStopMessage().trim().isEmpty()))
	      StopHelper.stop(context, context.getStopMode(), "Flow canceled: " + context.getFullName());
	    else
	      StopHelper.stop(context, context.getStopMode(), context.getCustomStopMessage());
	    result = context.getStopMessage();
	  }
	}
      }
    }

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @param context	the context to use
   * @return		null if successfully interacted, otherwise error message
   */
  public static BasePassword promptPassword(Actor context) {
    return promptPassword(context, null, null);
  }

  /**
   * Performs the interaction with the user.
   *
   * @param context	the context to use
   * @param labelText 	the text for the label, uses default if null
   * @param comments 	the comments to display, ignored if null
   * @return		null if successfully interacted, otherwise error message
   */
  public static BasePassword promptPassword(Actor context, String labelText, String comments) {
    PasswordDialog dlg;

    dlg = new PasswordDialog(null, ModalityType.DOCUMENT_MODAL);
    if (labelText != null)
      dlg.setLabelPassword(labelText);
    if (comments != null)
      dlg.setComments(comments);
    dlg.setLocationRelativeTo(context.getParentComponent());
    ((Flow) context.getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) context.getRoot()).deregisterWindow(dlg);
    if (dlg.getOption() == PasswordDialog.APPROVE_OPTION)
      return dlg.getPassword();
    else
      return null;
  }
}
