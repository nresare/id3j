id3j - A small library for generating ID3 metatdata for MP3 files
Project web page: http://fs.voxbiblia.com/id3j/

This is a lightweight and easy to use library to create and add ID3 metadata
tags to MP3-files. It only concerns itself with ID3 version 3.2.0 and only
handles the most common types of information, but creating a tag and
adding it to a file is much easier than with the other solutions that I have
used. 

It also has one fairly esoteric feature that is interesting from a algorithmical
standpoint but that I doubt that anyone but me will ever use, and that is the
ability to create CRC32 neutral ID3-tags. That means that you can add such
a tag to any MP3-file and the CRC32 sum of the resulting MP3 file with the
tag is the same as the file without the tag. A handy feature if you have a
system that generates dynamic metadata information for pre-generated
MP3-files and use the combination of dynamic metadata and static MP3 files
to create large dynamic zip archives and don't want to re-calculate all the
checksums. I happened to have built such a system that can be used at
http://voxbiblia.com/ but it would surprise me if anyone else has use for
that feature, ever.

Features

- Lightweight. The binary jar is less than 16k at the moment, and has no
external dependencies except JDK 1.5. When developing, I use JUnit for
automated testing but that is not needed when using or compiling the library.

- Easy to use and develop. Clean and maintainable code.

- Handles international characters outside of the US-ASCII range correctly,
using full unicode when needed.

- Free software. Released under the GPL 3.0 license, this software can be used,
modified and redistributed by anyone respecting the terms of the license. If you
need other licensing options, please contact me.

Usage

The basic use case looks like this

  ID3Tag tag = new ID3Tag();
  tag.setArtist("Tingsek");
  tag.setAlbum("World Of It's Own");
  tag.setName("So Real");
  ID3Tool.writeToFile(new File("so_real.mp3"), tag);

For further details, please see the JavaDoc documentation available at 
http://fs.voxbiblia.com/id3j/javadoc/

If you want to build and test the software you need to unpack the files
named id3j-*-src.tar.bz2 and id3j-*-test.tar.bz2 and use Apache Ant
with the build.xml file that is included.

Credits

This software is developed and maintained by Noa Resare with support from
Voxbiblia. Thanks guys for letting me do this! 

Contact

Feel free to write me with comments, suggestions, bug reports and patches.
Noa Resare (noa@voxbiblia.com)
