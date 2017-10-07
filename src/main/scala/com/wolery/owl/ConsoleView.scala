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

import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.interpreter.Results.{ Error, Incomplete, Success }

import com.wolery.owl.control.{ Console, NewlineEvent }
import com.wolery.owl.utils.load

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Scene
import javafx.stage.Stage

//****************************************************************************

class ConsoleView
{
  val settings = new scala.tools.nsc.Settings
  settings.processArgumentString(preferences.compiler())

  val prompt1: String = preferences.prompt1()
  val prompt2: String = preferences.prompt2()

  @fx var m_text: Console = _
      var m_intp: IMain   = _
      var m_buff: String  = ""

  def interpret(code: String): Unit =
  {
    m_intp.interpret(code) match
    {
      case Success    ⇒ m_buff = "";    m_text.appendText(prompt1)
      case Error      ⇒ m_buff = "";    m_text.appendText(prompt1)
      case Incomplete ⇒ m_buff += code; m_text.appendText(prompt2)
     }
  }

  def initialize() =
  {
    m_intp = new IMain(settings,m_text.getPrintWriter)

    m_text.setOnNewline(onNewline(_))

    m_intp.bind("xx", "Double", 3.0)
  }

  def onNewline(e: NewlineEvent) =
  {
    interpret(m_buff + e.text)
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
