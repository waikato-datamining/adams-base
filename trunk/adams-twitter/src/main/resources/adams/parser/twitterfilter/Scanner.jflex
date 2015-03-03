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
 * Scanner.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.twitterfilter;

import java_cup.runtime.SymbolFactory;
import java.io.*;

/**
 * A scanner for twitter filter expressions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
%%
%unicode
%cup
%public
%class Scanner
%{
  // Author: FracPete (fracpete at waikato dot ac dot nz)
  // Version: $Revision$
  protected SymbolFactory sf;

  public Scanner(InputStream r, SymbolFactory sf){
    this(r);
    this.sf = sf;
  }
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%{
  StringBuilder string = new StringBuilder();
%}

%state STRING

%%
<YYINITIAL> {
	// matching
	":" { return sf.newSymbol("substring match", sym.SUBSTRING_MATCH); }
	"=" { return sf.newSymbol("exact match", sym.EXACT_MATCH); }
	"~" { return sf.newSymbol("regexp match", sym.REGEXP_MATCH); }

	// strings
    \"  { string.setLength(0); yybegin(STRING); }

	// boolean stuff
	"not" { return sf.newSymbol("not", sym.NOT); }
	"and" { return sf.newSymbol("and", sym.AND); }
	"or"  { return sf.newSymbol("or", sym.OR); }
	"xor" { return sf.newSymbol("xor", sym.XOR); }
    "<"   { return sf.newSymbol("less than", sym.LT); }
    "<="  { return sf.newSymbol("less or equal than", sym.LE); }
    ">"   { return sf.newSymbol("greater than", sym.GT); }
    ">="  { return sf.newSymbol("greater or equal than", sym.GE); }
    "<>"  { return sf.newSymbol("not equal", sym.NOT_EQ); }

	// predicates
	"langcode"    { return sf.newSymbol("language code", sym.LANGUAGE_CODE); }
	"country"     { return sf.newSymbol("country", sym.COUNTRY); }
	"countrycode" { return sf.newSymbol("country code", sym.COUNTRY_CODE); }
	"place"       { return sf.newSymbol("place", sym.PLACE); }
	"text"        { return sf.newSymbol("text", sym.TEXT); }
	"source"      { return sf.newSymbol("source", sym.SOURCE); }
	"user"        { return sf.newSymbol("user", sym.USER); }
	"lat"         { return sf.newSymbol("latitude", sym.LATITUDE); }
	"latitude"    { return sf.newSymbol("latitude", sym.LATITUDE); }
	"long"        { return sf.newSymbol("longitude", sym.LONGITUDE); }
	"longitude"   { return sf.newSymbol("longitude", sym.LONGITUDE); }
	"hashtag"     { return sf.newSymbol("hashtag", sym.HASHTAG); }
	"usermention" { return sf.newSymbol("usermention", sym.USERMENTION); }
	"statuslang"  { return sf.newSymbol("usermention", sym.STATUSLANG); }
	"retweet"     { return sf.newSymbol("usermention", sym.RETWEET); }
	"isretweeted" { return sf.newSymbol("usermention", sym.ISRETWEETED); }
	"favcount"    { return sf.newSymbol("usermention", sym.FAVCOUNT); }

	// functions
	"if"     { return sf.newSymbol("ifelse", sym.IFELSE); }
	"ifelse" { return sf.newSymbol("ifelse", sym.IFELSE); }
	"has"    { return sf.newSymbol("has", sym.HAS); }

    // numbers
    [0-9]*\.?[0-9]+(E(-)?[1-9][0-9]*)? { return sf.newSymbol("Number", sym.NUMBER, new Double(yytext())); }

	// whitespaces
	[ \r\n\t\f] { /* ignore white space. */ }

	// various
	"," { return sf.newSymbol("Comma", sym.COMMA); }
	"(" { return sf.newSymbol("Left Bracket", sym.LPAREN); }
	")" { return sf.newSymbol("Right Bracket", sym.RPAREN); }
}

<STRING> {
  \"            { yybegin(YYINITIAL);
                  return sf.newSymbol("String", sym.STRING, string.toString()); }
  [^\n\r\"\\]+  { string.append(yytext()); }
  \\t           { string.append('\t'); }
  \\n           { string.append('\n'); }
  \\r           { string.append('\r'); }
  \\\"          { string.append('\"'); }
  \\            { string.append('\\'); }
}

// catch all
. { System.err.println("Illegal character: " + yytext()); }
