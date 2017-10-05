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

import scala.tools.nsc.interpreter._
import scala.tools.nsc.Settings
import javax.script._
import java.io.{BufferedReader, StringReader, PrintWriter}
import Results._
import ConsoleView.prefs
import java.io.Writer

//****************************************************************************

class ConsoleWriter (console: Console) extends Writer
{
  def close: Unit = {}

  def flush: Unit = {}

  def x : Int = 3
  def x_=(x:Int) :Unit= {}

  def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
  {
    console.appendText(new String(array.slice(offset,offset + length)))
  }
}

class ConsoleView
{
  val settings = new scala.tools.nsc.Settings
  settings.processArgumentString(prefs.compiler())
  prefs.compiler("-deprecation")

  val prompt1: String = prefs.prompt1()
  def prompt2: String = prefs.prompt2()

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
    m_intp = new IMain(settings,new PrintWriter(new ConsoleWriter(m_text)))

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
  object prefs extends utils.Preferences(owl.getClass)
  {
    val compiler = string("compiler","-deprecation -feature -Xlint")
    val prompt1  = string("prompt1","scala> ")
    val prompt2  = string("prompt2","    | ")
  }

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
