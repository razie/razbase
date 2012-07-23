    /**  ____    __    ____  ____  ____/___      ____  __  __  ____
     *  (  _ \  /__\  (_   )(_  _)( ___) __)    (  _ \(  )(  )(  _ \
     *   )   / /(__)\  / /_  _)(_  )__)\__ \     )___/ )(__)(  ) _ <
     *  (_)\_)(__)(__)(____)(____)(____)___/    (__)  (______)(____/
     *                      
     *  Copyright (c) Razvan Cojocaru, 2007+, Creative Commons Attribution 3.0
     */

What's this?
------------
Some code I dind't want to have to write...


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


How to use
---------------------

The sbt/maven artifact is:  

    def razBase = "com.razie" %% "razbase"         % "0.6.2-SNAPSHOT"

Make sure that, if you use a SNAPSHOT version, the snapshots repository is added to sbt 0.11:

    resolvers += ScalaToolsSnapshots,

For an example, see usage in my snakked project. Good luck!


Developing & Building
---------------------

These projects are setup as eclipse projects and also have ant build.xml files.

Here's how to build it:

1. Make a workspace directory ${w}
2. checkout the following projects

   cd ${w}
   git clone git@github.com:razie/razbase.git

Note: if you don't have a github ssh key setup, use the anonymous checkout:

    git clone http://github.com/razie/razbase.git

4. Setup sbt 0.11 (simple build tool) and scala 2.9.1 distribution

... 

7. Eclipse setup

   * install the scala 2.9.1 plugin 
   * use sbt and run the command "update" to get all the libraries downloaded
   
7.1. create the projects
   open the project in eclipse


Good luck!

