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
 * MultiTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import weka.core.RevisionUtils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Combines the tokens of several tokenizers, skipping duplicate tokens.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiTokenizer
  extends Tokenizer {

  private static final long serialVersionUID = 6094968316580671771L;

  public static final String TOKENIZER = "tokenizer";

  /** the tokenizers to use. */
  protected Tokenizer[] m_Tokenizers = getDefaultTokenizers();

  /** the collected tokens. */
  protected List<String> m_Tokens = new ArrayList<>();

  /**
   * Returns a string describing the stemmer
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public String globalInfo() {
    return "Combines the tokens of several tokenizers, skipping duplicate tokens.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, tokenizersTipText(), "none", TOKENIZER);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setTokenizers((Tokenizer[]) WekaOptionUtils.parse(options, TOKENIZER, getDefaultTokenizers(), Tokenizer.class));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, TOKENIZER, getTokenizers());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default token tokenizers.
   *
   * @return		the default
   */
  protected Tokenizer[] getDefaultTokenizers() {
    return new Tokenizer[0];
  }

  /**
   * Sets the tokenizers to use.
   *
   * @param value	the tokenizers
   */
  public void setTokenizers(Tokenizer[] value) {
    m_Tokenizers = value;
  }

  /**
   * Returns the tokenizers to use.
   *
   * @return		the tokenizers
   */
  public Tokenizer[] getTokenizers() {
    return m_Tokenizers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tokenizersTipText() {
    return "The tokenizers to apply.";
  }

  /**
   * Tests if this enumeration contains more elements.
   *
   * @return true if and only if this enumeration object contains at least one
   *         more element to provide; false otherwise.
   */
  @Override
  public boolean hasMoreElements() {
    return (m_Tokens.size() > 0);
  }

  /**
   * Returns the next element of this enumeration if this enumeration object has
   * at least one more element to provide.
   *
   * @return the next element of this enumeration.
   */
  @Override
  public String nextElement() {
    return m_Tokens.remove(0);
  }

  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   *
   * @param s the string to tokenize
   */
  @Override
  public void tokenize(String s) {
    Set<String>		tokens;
    String		token;

    m_Tokens.clear();
    tokens = new HashSet<>();
    for (Tokenizer t: m_Tokenizers) {
      t.tokenize(s);
      while (t.hasMoreElements()) {
	token = t.nextElement();
	if (tokens.contains(token))
	  continue;
	m_Tokens.add(token);
	tokens.add(token);
      }
    }
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10824 $");
  }
}
