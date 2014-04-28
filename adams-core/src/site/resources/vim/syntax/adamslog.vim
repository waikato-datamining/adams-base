" Vim syntax file
" Language:	Adams log output
" Maintainer:	fracpete (fracpete at waikato dot ac dot nz)
" Last Change:	2009 Sep 2

" Syntax highlighting for Adams log output

" For version 5.x: Clear all syntax items
" For version 6.x: Quit when a syntax file was already loaded
if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

syn case ignore

" keywords
syn match adamsLogPrefix	"^\[.*-DEBUG\] "
syn match adamsLogPrefix	"^\[.*-OUT\] "
syn match adamsLogPrefix	"^\[.*-ERR\] "
syn match adamsClassname	"^[\t]*adams\..*$"
syn match wekaClassname "^[\t]*weka\..*$"

" numbers
syn match  adamsNumber	"-\=\<\d\+\>"
syn match  adamsFloat	"-\=\<\d\+\.\d\+\>"
syn match  adamsFloat	"-\=\<\d\+\.\d\+[eE]-\=\d\+\>"

" date/time
syn match  adamsDate	"\d\d\d\d-\d\d-\d\d"
syn match  adamsTime	"\d\d:\d\d:\d\d"

" symbols
syn match  adamsSymbol	"/"
syn match  adamsSymbol	"\\t"
syn match  adamsSymbol	"\^"
syn match  adamsSymbol	"\$"
syn match  adamsSymbol	"#"
syn match  adamsSymbol	"\.\*"
syn match  adamsSymbol   "{[^\}]*}"

" Define the default highlighting.
" For version 5.7 and earlier: only when not done already
" For version 5.8 and later: only when an item doesn't have highlighting yet
if version >= 508 || !exists("did_adamslog_syn_inits")
  if version < 508
    let did_adamslog_syn_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif

  HiLink adamsLogPrefix	Statement
  HiLink adamsClassname	Type
  HiLink wekaClassname	Type
  HiLink adamsSymbol	Special
  HiLink adamsFloat	Float
  HiLink adamsNumber	Number
  HiLink adamsDate	String
  HiLink adamsTime	String

  delcommand HiLink
endif

let b:current_syntax = "adamslog"

" vim: ts=8
