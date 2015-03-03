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
 * AbstractBreakpoint.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 * Ancestor for breakpoints for execution listeners.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBreakpoint
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4654096855875796107L;

  /** whether the breakpoint is disabled. */
  protected boolean m_Disabled;
  
  /** break on preInput. */
  protected boolean m_OnPreInput;

  /** break on postInput. */
  protected boolean m_OnPostInput;

  /** break on preExecute. */
  protected boolean m_OnPreExecute;

  /** break on postExecute. */
  protected boolean m_OnPostExecute;

  /** break on preOutput. */
  protected boolean m_OnPreOutput;

  /** break on postOutput. */
  protected boolean m_OnPostOutput;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	    "disabled", "disabled",
	    false);
    
    m_OptionManager.add(
	    "on-pre-input", "onPreInput",
	    false);
    
    m_OptionManager.add(
	    "on-post-input", "onPostInput",
	    false);
    
    m_OptionManager.add(
	    "on-pre-execute", "onPreExecute",
	    false);
    
    m_OptionManager.add(
	    "on-post-execute", "onPostExecute",
	    false);
    
    m_OptionManager.add(
	    "on-pre-output", "onPreOutput",
	    false);
    
    m_OptionManager.add(
	    "on-post-output", "onPostOutput",
	    false);
  }
  
  /**
   * Sets whether to disable this breakpoint.
   *
   * @param value	true if to disable
   */
  public void setDisabled(boolean value) {
    m_Disabled = value;
    reset();
  }

  /**
   * Returns whether this breakpoint is disabled.
   *
   * @return		true if disabled
   */
  public boolean getDisabled() {
    return m_Disabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String disabledTipText() {
    return "If set to true, the breakpoint is completely disabled.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on pre-input (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPreInput(boolean value) {
    m_OnPreInput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-input (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPreInput() {
    return m_OnPreInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreInputTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-input (of token) time; token available.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on post-input (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPostInput(boolean value) {
    m_OnPostInput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-input (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPostInput() {
    return m_OnPostInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostInputTipText() {
    return "If set to true, the breakpoint gets evaluated at post-input (of token) time.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on pre-execute.
   *
   * @param value	true if evaluated
   */
  public void setOnPreExecute(boolean value) {
    m_OnPreExecute = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-execute.
   *
   * @return		true if evaluated
   */
  public boolean getOnPreExecute() {
    return m_OnPreExecute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreExecuteTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-execute time.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on post-execute.
   *
   * @param value	true if evaluated
   */
  public void setOnPostExecute(boolean value) {
    m_OnPostExecute = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-execute.
   *
   * @return		true if evaluated
   */
  public boolean getOnPostExecute() {
    return m_OnPostExecute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostExecuteTipText() {
    return "If set to true, the breakpoint gets evaluated at post-execute time.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on pre-output (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPreOutput(boolean value) {
    m_OnPreOutput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-output (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPreOutput() {
    return m_OnPreOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreOutputTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-output (of token) time; token available.";
  }
  
  /**
   * Sets whether to evaluate the breakpoing on post-output (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPostOutput(boolean value) {
    m_OnPostOutput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-output (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPostOutput() {
    return m_OnPostOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostOutputTipText() {
    return "If set to true, the breakpoint gets evaluated at post-output (of token) time.";
  }
  
  /**
   * Evaluates the breakpoint at pre-input.
   * 
   * @param actor	the current actor
   * @param token	the token available for input
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreInput(Actor actor, Token token);
  
  /**
   * Evaluates the breakpoint at post-input.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostInput(Actor actor);
  
  /**
   * Evaluates the breakpoint at pre-execute.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreExecute(Actor actor);
  
  /**
   * Evaluates the breakpoint at post-execute.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostExecute(Actor actor);
  
  /**
   * Evaluates the breakpoint at pre-output.
   * 
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreOutput(Actor actor);
  
  /**
   * Evaluates the breakpoint at post-output.
   * 
   * @param actor	the current actor
   * @param token	the token available for output
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostOutput(Actor actor, Token token);
  
  /**
   * Checks whether the breakpoint gets triggered in pre-input.
   * 
   * @param actor	the current actor
   * @param token	the token available for input
   * @return		true if triggered
   */
  public boolean triggersPreInput(Actor actor, Token token) {
    return m_OnPreInput && evaluatePreInput(actor, token);
  }
  
  /**
   * Checks whether the breakpoint gets triggered in post-input.
   * 
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPostInput(Actor actor) {
    return m_OnPostInput && evaluatePostInput(actor);
  }
  
  /**
   * Checks whether the breakpoint gets triggered in pre-execute.
   * 
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPreExecute(Actor actor) {
    return m_OnPreExecute && evaluatePreExecute(actor);
  }
  
  /**
   * Checks whether the breakpoint gets triggered in post-execute.
   * 
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPostExecute(Actor actor) {
    return m_OnPostExecute && evaluatePostExecute(actor);
  }
  
  /**
   * Checks whether the breakpoint gets triggered in pre-output.
   * 
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPreOutput(Actor actor) {
    return m_OnPreOutput && evaluatePreOutput(actor);
  }
  
  /**
   * Checks whether the breakpoint gets triggered in post-output.
   * 
   * @param actor	the current actor
   * @param token	the token available for output
   * @return		true if triggered
   */
  public boolean triggersPostOutput(Actor actor, Token token) {
    return m_OnPostOutput && evaluatePostOutput(actor, token);
  }
}
