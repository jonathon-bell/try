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

import owl.util.Logging

//****************************************************************************

object Main extends Logging
{
  def main(args: Array[String]): Unit =
  {
    for ((k, v) ← owl.util.manifest.attributes)
      println(k + ": " + v)
  }
}

//****************************************************************************
