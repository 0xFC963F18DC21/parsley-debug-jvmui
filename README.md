# Parsley Debugger, JVM Visual Frontend

[Parsley](https://github.com/j-mie6/Parsley) is a fast parser-combinator library for Scala based on
Haskell's [Parsec](https://hackage.haskell.org/package/parsec).
This is a companion library aimed at providing a graphical debugger for your parsers written using
Parsley, ala ANTLR's `grun` command, but executed programmatically inside your parser's code intead
of as a command-line tool.

This project uses [ScalaFx](https://www.scalafx.org/) as its graphics user interface backend, and
will only run on the Java Virtual Machine (in other words, Scala Native and Scala.JS are unsupported
by this frontend).

## Usage with SBT

### Notice: The current version of this library depends on the `dev` branch on [my fork of Parsley](https://github.com/MF42-DZH/parsley), so it will be unusable until changes are merged into upstream.

Currently, this is not being uploaded to a library repository like Maven or SonaType. Therefore,
[JitPack](https://jitpack.io/) is currently the best way to include this in your SBT build:

1. Ensure that Parsley `v4.2.0` or higher is added as a dependency in your project.
2. Add `resolvers += "jitpack" at "https://jitpack.io"` to your project settings in `build.sbt`.
3. Finally, add `libraryDependencies += "com.github.mf42-dzh" % "parsley-debug-jvmui" % "VERSION"`
   to the same project's settings in `build.sbt`, where `VERSION` is the tag you want to use.
   If you want the bleeding edge updates, use `master-SNAPSHOT` as the version.

You will end up with something like:

```scala
lazy val root = (project in file("."))
  .settings(
    ...
    resolvers += "jitpack" at "https://jitpack.io",
    ...
    libraryDependencies += "com.github.mf42-dzh" % "parsley-debug-jvmui" % "VERSION",
    ...
  )
```

Currently, this library only works with Scala 2.13.x.

Documentation for usage is a TODO.

## Issues

This is still a very new library, so expect some bugs as both this library and Parsley 4 updates.
Feel free to use the Issues page to report any issues found with the library.

## Disclaimer

Please note that at the moment, this is being created as part of my 3rd year BEng software project
at Imperial College London. This means until (approximately) late July 2023, this repository **is**
**NOT accepting pull requests**. Issue reports are accepted and encouraged, however.
