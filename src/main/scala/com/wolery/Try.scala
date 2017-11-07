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

package com.wolery

import java.util.jar.Manifest

object Main
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

  def main(args: Array[String]): Unit =
  {
  for ((k,v) <-getManifestAttributes)
    println(k + ": " + v)
  }
}

//****************************************************************************
