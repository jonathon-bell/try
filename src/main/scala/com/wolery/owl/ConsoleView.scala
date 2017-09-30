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

package com.wolery.owl

import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.Scene
import javafx.stage.Stage
import control.{Console,NewlineEvent}

import utils.load

//****************************************************************************

class ConsoleView
{
  @fx var m_text: Console = _

  def initialize() =
  {
    m_text.setOnNewline(onNewline(_))
  }

  def onNewline(e: NewlineEvent) =
  {
    println("onNewline: " + e.text)
  }
}

//****************************************************************************

object ConsoleView
{
  def apply(stage: Stage): Unit =
  {
    val (r,c) = load.view[ConsoleView]("ConsoleView")

    stage.setTitle    ("Owl - Console")
    stage.setMinWidth (r.getPrefWidth)
    stage.setMinHeight(r.getPrefHeight)
    stage.setScene    (new Scene(r))
    stage.show        ()
  }
}

//****************************************************************************
