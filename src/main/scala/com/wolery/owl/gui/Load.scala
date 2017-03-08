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
    val n = l.load[Node]
    val c = l.getController[Controller]

    (n,c)
  }

  def apply[Node,Controller](fxml: String,controller: Controller): (Node,Controller) =
  {
    val f = getClass.getResource(s"/gui/$fxml.fxml")
    val l = new javafx.fxml.FXMLLoader(f)
    l.setController(controller)

    (l.load[Node],controller)
  }
}

//****************************************************************************
