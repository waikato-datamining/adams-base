" Vim filetype plugin file
" Language:	Adams flows (based on Make)
" Maintainer:	FracPete (fracpete at waikato dot ac dot nz)
" Last Change:	2009 Jul 20

" Only do this when not done yet for this buffer
if exists("b:did_ftplugin")
  finish
endif
let b:did_ftplugin = 1

let b:undo_ftplugin = "setl et< sts<"

" Make sure a hard tab is used
setlocal noexpandtab softtabstop=2 tabstop=2 shiftwidth=2

" Including files.
let &l:include = '^\s*include'
