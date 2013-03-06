Drumfish: Java 6 utility library inspired by functional programming languages
=============================================================================

[![Build Status](https://travis-ci.org/Gekkio/drumfish.png)](https://travis-ci.org/Gekkio/drumfish)

## Introduction
Drumfish is a collection of useful Java 6+ libraries inspired by ideas used in functional programming languages. It doesn't contain any earth-shattering new ideas or utility code, but provider some commonly used functional programming concepts to Java programmers. Using this library correctly can lead to significantly more robust and type-safe Java code, which is a breeze of new air in a world dominated by annotations, reflection, and XML. Drumfish requires [Google Guava](http://code.google.com/p/guava-libraries), and can be in a way seen as an extension to it.

Since Java doesn't have any language support for lambdas, code that uses Drumfish can be verbose. One of the best reasons of using this library now is that Java 8 is scheduled to be released soon. You can start using this library now, and reap the benefits of Java 8 later once it is released and you decide to upgrade to it. Replacing anonymous classes with the new lambda syntax is a fairly simple refactoring process, and could be at least in theory automated.

Drumfish is licensed under the Apache License, Version 2.0.

### Design goals
The primary design goals of Drumfish are compile-time safety (which includes, but is not limited to type safety) and immutability. Maximizing compile-time safety in a software project makes large-scale refactoring possible, and can provide certain safety that tests cannot provide. Favoring immutability leads to thread-safe code, and usually leads to API design where tracking side effects and mutations is extremely simple.

### Documentation

[Release API documentation](http://gekkio.fi/apidocs/drumfish/0.1.0/).

[Snapshot API documentation](http://gekkio.fi/apidocs/drumfish/0.2.0-SNAPSHOT/).

## What is included?
Drumfish consists of four modules: *data*, *lang*, *frp*, and *validation*.

Drumfish releases are available in Maven central under the groupId `fi.gekkio.drumfish`. Use Maven, or any Maven-compatible build tool to fetch the artifacts and include them in your projects.

### Drumfish Lang
Lang consists of functional programming interfaces, Option and Either types, and several useful reusable functions that operate on standard Java types.

    <dependency>
      <groupId>fi.gekkio.drumfish</groupId>
      <artifactId>drumfish-lang</artifactId>
      <version>0.1.0</version>
    </dependency>

*Stable*. Nearly all features of this module have been used in production apps for several years.

### Drumfish Data
Data consists of immutable persistent data structures. Currently it contains a generic finger tree implementation, and a sequence implementation based on it.

*Prototype*.
Everything is still work in progress. Some design choices in the finger tree implementation are based on experiences with an older Java implementation, which has been in production use for years.
Needs more practical implementations (priority queue, interval tree, etc.), or at least some example documentation.

### Drumfish FRP
FRP consists of a rich, composable API for Functional Reactive Programming.

    <dependency>
      <groupId>fi.gekkio.drumfish</groupId>
      <artifactId>drumfish-frp</artifactId>
      <version>0.1.0</version>
    </dependency>

*Experimental*. This module is in production use, but the API is still in flux. Some currently open questions are:

+ Naming and behaviour of flatMap. The name is not directly intuitive, but is consistent with monadic patterns in Scala. The actual behaviour currently does switching, but with event streams another option is to instead collect all events from all past streams.
+ Drumfish FRP does not use a three-stream model (= events, errors, end-of-stream). Thus, it is simpler but less powerful than alternatives. On the other hand, it's possible to build a three-stream API on top of the current one.
+ Potential performance improvements. Right now all the API methods create new objects even though it could be avoided in some cases (e.g. calling distinct() twice)
+ Thread-safety. Great care has been taken to ensure thread-safety of the implementation, but a careful analysis is needed before the library can be claimed to be thread-safe.

### Drumfish Validation
Validation provides a type-safe, composable validation API, which is inspired by the Validation type in [Scalaz](https://github.com/scalaz/scalaz), but removes a lot of abstraction in order to keep the API more usable in Java. The resulting API has a reasonable balance between abstraction and ease of use.

    <dependency>
      <groupId>fi.gekkio.drumfish</groupId>
      <artifactId>drumfish-validation</artifactId>
      <version>0.1.0</version>
    </dependency>

*Experimental*. This module is in production use, but the API is still in flux, and still lacks many useful features. The use of an immutable, two-state Validation types is on the other hand a proven concept used in functional programming languages.

## Modifying Drumfish
Compiling on the command line is simple, because it's enough to have Maven 3.0.x and Java 1.6+ installed. If you intend to use an IDE, Scala and Project Lombok support are required. If you are a JRebel user, there's a Maven profile called `jrebel` which activates automatic rebel.xml generation.

For example, Eclipse users will need an up-to-date Eclipse installation with m2e, and the following extensions:

+ [Scala IDE for Scala 2.10.x](http://scala-ide.org)
+ [m2eclipse-scala](https://github.com/sonatype/m2eclipse-scala)
+ [Project Lombok](http://projectlombok.org/)
