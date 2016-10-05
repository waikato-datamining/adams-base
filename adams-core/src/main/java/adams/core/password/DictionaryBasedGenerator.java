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
 * DictionaryBasedGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.password;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator that uses a dictionary as basis.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DictionaryBasedGenerator
  extends AbstractPasswordGenerator {

  private static final long serialVersionUID = 3653026118047928682L;

  /**
   * Defines what variations of the password from the dictionary will be
   * generated.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Variation {
    AS_IS,
    LOWER_CASE,
    UPPER_CASE,
    REVERSE,
    REVERSE_LOWER_CASE,
    REVERSE_UPPER_CASE,
  }

  /** the dictionary file to use. */
  protected PlaceholderFile m_DictionaryFile;

  /** the dictionary entries. */
  protected List<String> m_Dictionary;

  /** the variations to generate. */
  protected Variation[] m_Variations;

  /** the current passwords to output. */
  protected List<String> m_Passwords;

  /** the current index in the dictionary. */
  protected int m_Index;

  /**
   * Initializes the generator.
   *
   * @param dictionaryFile  the dictionary to use
   */
  public DictionaryBasedGenerator(PlaceholderFile dictionaryFile) {
    this(dictionaryFile, null);
  }

  /**
   * Initializes the generator.
   *
   * @param dictionaryFile	the dictionary to use
   * @param variations		the variations of the words in the dictionary to generate, can be null
   */
  public DictionaryBasedGenerator(PlaceholderFile dictionaryFile, Variation[] variations) {
    super();

    m_DictionaryFile = dictionaryFile;
    m_Variations     = variations;
    m_Dictionary     = null;

    initialize();
  }

  /**
   * Initializes the generator.
   *
   * @param dictionary		the words to use
   */
  public DictionaryBasedGenerator(List<String> dictionary) {
    this(dictionary, null);
  }

  /**
   * Initializes the generator.
   *
   * @param dictionary		the words to use
   * @param variations		the variations of the words in the dictionary to generate, can be null
   */
  public DictionaryBasedGenerator(List<String> dictionary, Variation[] variations) {
    super();

    m_DictionaryFile = null;
    m_Variations     = variations;
    m_Dictionary     = dictionary;

    initialize();
  }

  /**
   * Initializes the generator.
   */
  protected void initialize() {
    m_Passwords = new ArrayList<>();
    m_Index     = -1;
    if (m_Variations == null)
      m_Variations = (new Variation[]{Variation.AS_IS});

    if (m_Dictionary == null) {
      if (!m_DictionaryFile.exists())
	throw new IllegalArgumentException("Dictionary file does not exist: " + m_DictionaryFile);
      if (m_DictionaryFile.isDirectory())
	throw new IllegalArgumentException("Dictionary file points to a directory: " + m_DictionaryFile);
      m_Dictionary = FileUtils.loadFromFile(m_DictionaryFile);
      if (m_Dictionary == null)
	throw new IllegalArgumentException("Failed to load dictionary: " + m_DictionaryFile);
      if (m_Dictionary.size() == 0)
	throw new IllegalArgumentException("Dictionary has no entries!");
    }
    else {
      if (m_Dictionary.size() == 0)
	throw new IllegalArgumentException("Dictionary has no entries!");
    }
  }

  /**
   * Checks whether there is another password available.
   *
   * @return		true if another password available
   */
  @Override
  public boolean hasNext() {
    return (m_Passwords.size() > 0) || (m_Index < m_Dictionary.size() - 1);
  }

  /**
   * Returns the next password.
   *
   * @return		the next password, null if no more available
   */
  @Override
  public String next() {
    String	result;
    String	password;
    String	reverse;

    if (m_Passwords.size() == 0) {
      m_Index++;
      if (m_Index >= m_Dictionary.size() - 1)
	return null;
      password = m_Dictionary.get(m_Index);
      reverse  = new StringBuilder(password).reverse().toString();
      for (Variation variation: m_Variations) {
	switch (variation) {
	  case AS_IS:
	    m_Passwords.add(password);
	    break;
	  case LOWER_CASE:
	    m_Passwords.add(password.toLowerCase());
	    break;
	  case UPPER_CASE:
	    m_Passwords.add(password.toUpperCase());
	    break;
	  case REVERSE:
	    m_Passwords.add(reverse);
	    break;
	  case REVERSE_LOWER_CASE:
	    m_Passwords.add(reverse.toLowerCase());
	    break;
	  case REVERSE_UPPER_CASE:
	    m_Passwords.add(reverse.toUpperCase());
	    break;
	}
      }
    }

    result = m_Passwords.remove(0);

    return result;
  }
}
