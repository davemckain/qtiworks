#!/usr/bin/perl -w

local $/ = undef;
my $file = shift;
open FILE, "<$file" or die "Can't open $file for reading: $!";
my $text = <FILE>;
close FILE or die "Error reading $file: $!";

my $copyright = <<HERE;
/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
HERE

# Normalize line endings
$text =~ s!\r!!sg;

# Tabs to spaces (just in case)
$text =~ s!\t!    !sg;

# Replace header Javadoc
$text =~ s!^\s*/\*.+?\*/\s*!$copyright!s;

# Remove double blank line after import statements
$text =~ s!^(import.+?)\n\n\n(/\*\*)!$1\n\n$2!sm;

# Remove stupid serial versions
$text =~ s!\n *private static final long serialVersionUID = 1L; *\n!!sg;

# Convert braces opened on new lines unless current is //...
$text =~ s!(//[^\n]*)\n( *\{)!$1\n\n$2!sg; # Add extra space after // ... \n { before next removes it
$text =~ s!\n *\{! \{!sg;

# Remove @version SVN tags
$text =~ s!\n *\* *\@version.+?\n!\n!s;

# Fix spacing in @author SVN tags
$text =~ s!(\@author) *!$1 !s;

# Fix logger
$text =~ s!\n */\*\* *Logger\.? *\*/ ?!!sg;
$text =~ s!protected static Logger!private static final Logger!sg;

#print $text;
#die;

open FILE, ">$file" or die "Can't open $file for writing: $!";
print FILE $text;
close FILE or die "Error writing $file: $!";

