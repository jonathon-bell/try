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

import javafx.fxml.FXMLLoader

//****************************************************************************

object load
{
  def apply[Node,Controller](fxml: String): (Node,Controller) =
  {
    val l = loader(fxml)
    val n = l.load[Node]
    val c = l.getController[Controller]

    (n,c)
  }

  def apply[Node,Controller](fxml: String,controller: Controller): (Node,Controller) =
  {
    val l = loader(fxml)

    l.setController(controller)

    (l.load[Node],controller)
  }

  private
  def loader(fxml: String): FXMLLoader =
  {
    new FXMLLoader(getClass.getResource(s"/fxml/$fxml.fxml"))
  }
}

//****************************************************************************
