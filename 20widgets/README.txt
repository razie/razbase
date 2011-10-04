/**  ____    __    ____  ____  ____/___      ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)    (  _ \(  )(  )(  _ \
 *   )   / /(__)\  / /_  _)(_  )__)\__ \     )___/ )(__)(  ) _ <
 *  (_)\_)(__)(__)(____)(____)(____)___/    (__)  (______)(____/
 *                      
 *  Copyright (c) Razvan Cojocaru, 2007+, Creative Commons Attribution 3.0
 */

NOTE: the sources for this project are actually in razbase...I'll move them back when the eclipse scala plugin is as fast as the Java one

What's this?
------------
Technology agnostic collection of widgets.

Shooting for the moon: trying to identify a set of 20 widgets that work on all graphical platforms.


Why?
----
Just like Java allows one to express an algorithm regardless on the hardware it will run, we need a way to express views regardless on the technology used to display them. I, henceforth, refuse to write any GUI code on any specific platform. If I have to dumb down all GUIs to achieve that, so be it! I have spoken!


Details
-------
The code is generally self-documented. Keep your eyes out for package.html and similar stuff.

This is an investigation of the concept. There's been many similar enterprises, I don't honestly know why I think this is any better :) I guess I'll keep playing until I find out either way.


Roadmap
-------
I will only maintain this as I need to. If there's some large user community developing (doubt that, really), 
we'll see - I could co-op some volunteers.

I will add more code as I need it/write it for all kinds of reasons.

Developing & Building
---------------------

These projects are setup as eclipse projects and also have ant build.xml files.

Here's how to build it:

1. Setup ant and scala
2. Make a workspace directory ${w}
3. checkout the following projects

   cd ${w}
   git clone git@github.com:razie/razbase.git
   git clone git@github.com:razie/razxml.git
   git clone git@github.com:razie/20widgets.git
   git clone git@github.com:razie/20widgets-swing.git

3.1. hack a bit - have to checkout my fork of CodeMirror in this specific location

  cd ${w}/20widgets/src/public
  git clone git@github.com:razie/CodeMirror.git

3.2. hack a bit - have to checkout my fork of ACE in this specific location

  git clone git@github.com:razie/ace.git
  cp -r ${w}/ace/build ${w}/20widgets/src/public
  cd ${w}/20widgets/src/public

4. edit ${w}/razbase/razie.properties and set the w property to the workspace

7. Eclipse setup

   * install the scala 2.8 plugin and the svn plugin
   * download a 2.8 scala distribution someplace, i.e. bin/scala - will need the complier.jar
   * download a 2.8-compatible scalatest distribution someplace - will need the library 
   
7.1. create the projects
   Create a project for each of the above: razbase, razxml, 20widgets

7.2. fix library dependencies

   Create two User Libraries (Window/Preferences/Java/Build Path/User Libraries):
   * scalatest - containing the scalatest-0.9.5.jar file or whichever is latest. Make sure you have the version that's compiled for scala 2.8
   * scalacompiler - with scala-compiler.jar (from the scala 2.8 installation) 

Good luck!


HACK
----

Currently, the sources for the swing mapping are included here because the eclipse scala plugin 
has issues with multiple projects...

