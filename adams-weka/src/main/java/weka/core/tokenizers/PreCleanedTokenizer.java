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
 * PreCleanedTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import weka.core.RevisionUtils;
import weka.core.WekaOptionUtils;
import weka.core.tokenizers.cleaners.PassThrough;
import weka.core.tokenizers.cleaners.TokenCleaner;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Allows the cleaning of tokens before actual tokenization.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreCleanedTokenizer
  extends Tokenizer {

  private static final long serialVersionUID = 6094968316580671771L;

  public static final String PRE_TOKENIZER = "pre-tokenizer";

  public static final String CLEANER = "cleaner";

  public static final String POST_TOKENIZER = "post-tokenizer";

  /** the pre tokenizer to use. */
  protected Tokenizer m_PreTokenizer = getDefaultPreTokenizer();

  /** the cleaner to use. */
  protected TokenCleaner m_Cleaner = getDefaultCleaner();

  /** the post tokenizer to use. */
  protected Tokenizer m_PostTokenizer = getDefaultPostTokenizer();

  /**
   * Returns a string describing the stemmer
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the cleaning of tokens before actual tokenization.\n"
      + "Process:\n"
      + "- apply pre-tokenizer to obtain tokens to clean\n"
      + "- apply token cleaner to tokens\n"
      + "- combine cleaned tokens back into a string (separated by blanks)\n"
      + "- apply post-tokenizer to produce final set of tokens";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, preTokenizerTipText(), getDefaultPreTokenizer().getClass().getName(), PRE_TOKENIZER);
    WekaOptionUtils.addOption(result, cleanerTipText(), getDefaultCleaner().getClass().getName(), CLEANER);
    WekaOptionUtils.addOption(result, postTokenizerTipText(), getDefaultPostTokenizer().getClass().getName(), POST_TOKENIZER);
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
    setPreTokenizer((Tokenizer) WekaOptionUtils.parse(options, PRE_TOKENIZER, getDefaultPreTokenizer()));
    setCleaner((TokenCleaner) WekaOptionUtils.parse(options, CLEANER, getDefaultCleaner()));
    setPostTokenizer((Tokenizer) WekaOptionUtils.parse(options, POST_TOKENIZER, getDefaultPostTokenizer()));
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
    WekaOptionUtils.add(result, PRE_TOKENIZER, getPreTokenizer());
    WekaOptionUtils.add(result, CLEANER, getCleaner());
    WekaOptionUtils.add(result, POST_TOKENIZER, getPostTokenizer());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default (pre) token tokenizer.
   *
   * @return		the default
   */
  protected Tokenizer getDefaultPreTokenizer() {
    return new WordTokenizer();
  }

  /**
   * Sets the tokenizer to use for the initial tokenization (before cleaning).
   *
   * @param value	the tokenizer
   */
  public void setPreTokenizer(Tokenizer value) {
    m_PreTokenizer = value;
  }

  /**
   * Returns the tokenizer to use for the initial tokenization (before cleaning).
   *
   * @return		the tokenizer
   */
  public Tokenizer getPreTokenizer() {
    return m_PreTokenizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preTokenizerTipText() {
    return "The tokenizer to use for the initial tokenization (before cleaning).";
  }

  /**
   * Returns the default cleaner.
   *
   * @return		the default
   */
  protected TokenCleaner getDefaultCleaner() {
    return new PassThrough();
  }

  /**
   * Sets the cleaner to use for cleaning the tokens from the initial tokenization.
   *
   * @param value	the cleaner
   */
  public void setCleaner(TokenCleaner value) {
    m_Cleaner = value;
  }

  /**
   * Returns the cleaner to use for cleaning the tokens from the initial tokenization.
   *
   * @return		the cleaner
   */
  public TokenCleaner getCleaner() {
    return m_Cleaner;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanerTipText() {
    return "The cleaner to use for cleaning the tokens from the initial tokenization.";
  }

  /**
   * Returns the default (post) token tokenizer.
   *
   * @return		the default
   */
  protected Tokenizer getDefaultPostTokenizer() {
    return new WordTokenizer();
  }

  /**
   * Sets the tokenizer to use for the final tokenization (after cleaning).
   *
   * @param value	the tokenizer
   */
  public void setPostTokenizer(Tokenizer value) {
    m_PostTokenizer = value;
  }

  /**
   * Returns the tokenizer to use for the final tokenization (after cleaning).
   *
   * @return		the tokenizer
   */
  public Tokenizer getPostTokenizer() {
    return m_PostTokenizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postTokenizerTipText() {
    return "The tokenizer to use for the final tokenization (after cleaning).";
  }

  /**
   * Tests if this enumeration contains more elements.
   *
   * @return true if and only if this enumeration object contains at least one
   *         more element to provide; false otherwise.
   */
  @Override
  public boolean hasMoreElements() {
    return m_PostTokenizer.hasMoreElements();
  }

  /**
   * Returns the next element of this enumeration if this enumeration object has
   * at least one more element to provide.
   *
   * @return the next element of this enumeration.
   */
  @Override
  public String nextElement() {
    return m_PostTokenizer.nextElement();
  }

  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   *
   * @param s the string to tokenize
   */
  @Override
  public void tokenize(String s) {
    StringBuilder	clean;
    String		token;

    // initial tokenization
    m_PreTokenizer.tokenize(s);

    // cleaning
    clean = new StringBuilder();
    while (m_PreTokenizer.hasMoreElements()) {
      token = m_PreTokenizer.nextElement();
      token = m_Cleaner.clean(token);
      if (token == null)
	continue;
      if (clean.length() > 0)
	clean.append(" ");
      clean.append(token);
    }

    // final tokenization
    m_PostTokenizer.tokenize(clean.toString());
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
