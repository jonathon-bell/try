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

object load
{
  def apply[Node,Controller](fxml: String): (Node,Controller) =
  {
    val f = getClass.getResource(s"/gui/$fxml.fxml")
    val l = new javafx.fxml.FXMLLoader(f)
    val c = l.getController[Controller]

    (l.load[Node],c)
  }
}

//****************************************************************************
