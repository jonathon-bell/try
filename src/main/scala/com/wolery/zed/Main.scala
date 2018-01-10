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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.zed

import com.wolery.owl.util.Logging
import java.util.jar.Manifest

object Main extends Logging
{
  def getManifestAttributes: Map[String,String] =
  {
    val manifest = new Manifest(Thread.currentThread
                               .getContextClassLoader
                               .getResourceAsStream("META-INF/MANIFEST.MF"))
    val map = collection.mutable.Map[String,String]()

    manifest.getMainAttributes.forEach
    {
      case (k,v) ⇒ map += k.toString → v.toString
    }

    map.toMap
  }

//  def main(args: Array[String]): Unit =
//  {
//  for ((k,v) <-getManifestAttributes)
//    println(k + ": " + v)
//  }
  def main(args: Array[String]): Unit =
  {
    CreateSequence.main(args)
  }
}

//****************************************************************************
/*

create sequence
  form code
  from command line
  from DSL
  from Pattern/Key/Scale/Groove/ gui
  from davsed file

save S to file
drag drop to Logic

animate sequence master=logic
animate sequence master=owl, slave=logic

*/
