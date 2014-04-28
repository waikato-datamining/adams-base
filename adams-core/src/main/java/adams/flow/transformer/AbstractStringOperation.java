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
 * AbstractStringOperation.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.flow.core.Token;

/**
 * Ancestor for transformers that perform operations on strings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStringOperation
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2139865260017627684L;

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		String/String[]
   */
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		String/String[]
   */
  public Class[] generates() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Processes the string. If null is returned, this output will be ignored.
   *
   * @param s		the string to process
   * @return		the processed string or null if nothing produced
   */
  protected abstract String process(String s);

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		str;
    List<String>	strNew;
    String		strTmp;
    boolean		isArray;
    int			i;

    result = null;

    try {
      isArray = (m_InputToken.getPayload() instanceof String[]);
      if (isArray)
	str = (String[]) m_InputToken.getPayload();
      else
	str = new String[]{(String) m_InputToken.getPayload()};

      strNew = new ArrayList<String>();
      for (i = 0; i < str.length; i++) {
	strTmp = process(str[i]);
	if (strTmp != null)
	  strNew.add(strTmp);
      }

      if (isArray) {
	m_OutputToken = new Token(strNew.toArray(new String[strNew.size()]));
      }
      else {
	if (strNew.size() > 0)
	  m_OutputToken = new Token(strNew.get(0));
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to process string:", e);
    }

    return result;
  }
}
