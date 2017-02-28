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

package com.wolery.owl.gui

//****************************************************************************

object Load
{
  def apply[Node](fxml: String): Node =
  {
    val f = getClass.getResource(s"/gui/$fxml.fxml")
    val l = new javafx.fxml.FXMLLoader(f)

    l.load[Node]()
  }
}

//****************************************************************************
