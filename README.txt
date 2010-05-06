/**  ____    __    ____  ____  ____/___      ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)    (  _ \(  )(  )(  _ \
 *   )   / /(__)\  / /_  _)(_  )__)\__ \     )___/ )(__)(  ) _ <
 *  (_)\_)(__)(__)(____)(____)(____)___/    (__)  (______)(____/
 *                      
 *  Copyright (c) Razvan Cojocaru, 2007+, Creative Commons Attribution 3.0
 */

What's this?
------------
Some interesting concepts and related utilities.

Why?
----
Well, just like i wish I didn't have to write it, there's no point in making others write it, should they need it. 


Details
-------
The code is generally self-documented. Keep your eyes out for package.html and similar stuff.


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

4. edit ${w}/razbase/razie.properties and set the w property to the workspace

... 

7. Eclipse setup

   * install the scala 2.8 plugin and the svn plugin
   * download a 2.8 scala distribution someplace, i.e. bin/scala - will need the complier.jar
   * download a 2.8-compatible scalatest distribution someplace - will need the library 
   
7.1. create the projects
   Create a project for each of the above: razbase, razxml, 20widgets, razweb, scripster, gremlins

7.2. fix library dependencies

   Create two User Libraries (Window/Preferences/Java/Build Path/User Libraries):
   * scalatest - containing the scalatest-0.9.5.jar file or whichever is latest. Make sure you have the version that's compiled for scala 2.8
   * scalacompiler - with scala-compiler.jar (from the scala 2.8 installation) 

Good luck!

