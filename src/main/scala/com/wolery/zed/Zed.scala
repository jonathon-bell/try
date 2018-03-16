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
package zed

//****************************************************************************

object zed extends util.Logging
{
  def main(args: Array[String]): Unit =
  {
    for ((k, v) ← util.manifest.attributes)
      println(k + ": " + v)
  }
}

//****************************************************************************
