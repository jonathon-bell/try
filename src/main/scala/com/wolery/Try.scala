//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain

//****************************************************************************

object Main
{
  def main(args: Array[String]): Unit =
  {
    println("Howdy")
    val s = new Settings()
    s.usejavacp.value = true
    println(s.usejavacp.value)

    val urls = this.getClass.getClassLoader match
    {
      case cl: java.net.URLClassLoader => cl.getURLs.toList
    }

    for (url <- urls)
      println(url)
    val i = new IMain(s)

    s.classpath.value =
    "/Users/jbell/.m2/repository/org/scala-lang/scala-library/2.12.2/scala-library-2.12.2.jar"
    println(s.classpath.value)
    i.bind("X","Int",78)

    println("Done")
  }
}

//****************************************************************************
