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

//****************************************************************************

import java.io.Writer

class ConsoleWriter (con: Console) extends Writer
{
  override
  def close: Unit = {} //flush

  override
  def flush:Unit = {}//Console.flush

  override
  def write(cbuf: Array[Char], off: Int, len: Int): Unit =
  {
    if (len > 0)
    {
      write(new String(cbuf.slice(off, off + len)))
    }
  }

  override
  def write(text: String): Unit =
  {
    con.appendText(text)
  }
}

class NewLinePrintWriter(con: Console,autoFlush: Boolean = false) extends PrintWriter(new ConsoleWriter(con),autoFlush)
{
  override def println() {print("\n"); flush()}
}

class ConsoleView
{
    val settings = new Settings
    settings.processArgumentString("-deprecation -feature -Xlint")
  def getPrompt1: String = "scala> "
  def getPrompt2: String = "    | "

  @fx var m_text: Console = _
      var m_intp: IMain = _
      var m_buff: String = ""

  def interpret(code: String): Unit =
  {
    m_intp.interpret(code) match
    {
      case Success    ⇒ m_buff = ""; m_text.appendText("scala> ")
      case Error      ⇒ m_buff = ""; m_text.appendText("scala> ")
      case Incomplete ⇒ m_buff += code; m_text.appendText("    | ")
     }
  }

  def initialize() =
  {
    m_intp = new IMain(settings,new NewLinePrintWriter(m_text))

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
