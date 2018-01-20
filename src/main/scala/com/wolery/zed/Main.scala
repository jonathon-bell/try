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

import com.wolery.owl.util.Logging

object Main extends Logging
{
  def main(args: Array[String]): Unit =
  {
    for ((k, v) ← owl.util.manifest.attributes)
      println(k + ": " + v)
  }
//  def main(args: Array[String]): Unit =
//  {
//    CreateSequence.main(args)
//  }
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
