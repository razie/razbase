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

There's a very simple and light, multi-threaded and **embedded http server** which can support any socket protocol, see https://github.com/razie/razbase/tree/master/web - this server supports streaming etc.

Distributed infrastructure, managing assets and other agent logic moved to https://github.com/razie/razmutant see http://homecloud.wikidot.com/agents

There's a simple **Light SOA framework**, which allows you to easily create "services": 
just have a Java class, annotate the methods you want available over http and register it with the server.
While the servlet thing is great, it's not that easy to use and most services/methods are really simple 
and this way you can test them either directly by Java calls or via the http server.

Two things I was investigating were drawing and streaming.

**Drawing** - basically any code anywhere will draw something. Even if it doesn't, the objects it uses could be seen by 
someone somewhere, be it a web page, AJAX, SVG, Eclipse, SWING ... god knows what. So, here's a simple and 
generic drawing framework. 
- See https://github.com/razie/razbase/blob/master/20widgets/src/main/scala/razie/Draw.scala for details or
- https://github.com/razie/razbase/blob/master/20widgets/src/main/scala/razie/draw/samples/SimpleModel.scala for an example. 
- The widgets are here: https://github.com/razie/razbase/tree/master/20widgets/src/main/scala/razie/draw/widgets 
- Note: this drawing support is not mainted anymore.

**Streaming** - instead of hard-coding the communication protocols throughout the code, I'm trying to abstract the basics
of a communications framework, with callbacks all the way into the browser - by streaming say result sets in parallel.
- for instance, you can stream elements into a table and these will be rendered to the browser when the table is complete. Alternatively, if you're streaming elements into a list, these are streamed to the browser as they become available, so the actual client sees the page as it is built.
- design is here: https://github.com/razie/razbase/blob/master/20widgets/src/main/scala/razie/draw/package-info.java
- see the streams and accumulators here: https://github.com/razie/razbase/tree/master/20widgets/src/main/scala/razie/draw
- Note: this streaming support is not maintained anymore

Razbase is used in http://www.tryscala.org and http://www.effectiveskiing.com


Roadmap
-------
I only maintain this as I need to - some parts of it are still used in other projects. Most of the ideas pursued originally are now available in much better form in projects like reactive streams etc.


How to use
---------------------

The sbt/maven artifact is:  

    def razBase = "com.razie" %% "razbase"         % "0.6.3-SNAPSHOT"

Make sure that, if you use a SNAPSHOT version, the snapshots repository is added to sbt, as in https://github.com/razie/scripster/blob/master/project/Build.scala :

    resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                      "releases"  at "http://oss.sonatype.org/content/repositories/releases")

For an example, see usage in my snakked project. Good luck!

Versions

- 0.9.1-SNAPSHOT is the 2.11.8 build
- 0.6.4-SNAPSHOT is the 2.10.0 build, no other code changes
- 0.6.3-SNAPSHOT is the last 2.9.1 build


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

   * install the scala plugin 
   * use sbt and run the command "update" to get all the libraries downloaded
   
7.1. create the projects
   open the project in eclipse


Good luck!

