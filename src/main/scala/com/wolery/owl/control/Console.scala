//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose : $Header:$
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package control

import java.io.Writer

import javafx.beans.property.{ ObjectProperty, SimpleObjectProperty }
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.control.TextArea

//****************************************************************************

class NewlineEvent(val line: String) extends ActionEvent

//****************************************************************************

class Console extends TextArea
{
  type Handler = EventHandler[NewlineEvent]

  var m_pos: ℕ = 0
  var m_rdy: Bool = true

  def getBuffer: String                 = getText.substring(m_pos)

  def getOnNewline                  : Handler   = onNewlineProperty.get()
  def setOnNewline(handler: Handler): Unit      = onNewlineProperty.set(handler)
  val onNewlineProperty:ObjectProperty[Handler] = new SimpleObjectProperty(this,"onNewline")

  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
    if (start >= m_pos)
    {
      super.replaceText(start,end,text)

      if (m_rdy && text.contains(preferences.eol))
      {
        val e = new NewlineEvent(getBuffer)
      //foo(fireEvent(e))
        foo(getOnNewline.handle(e))
      }
    }
  }

  private
  def foo(action: ⇒ Unit): Unit =
  {
    m_rdy = false
    action
    m_rdy = true
    m_pos = getLength
  }

  def setPrompt(prompt: String): Unit =
  {
    foo(appendText(prompt))
  }

  val writer = new Writer
  {
    def close: Unit = {}
    def flush: Unit = {}
    def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
    {
      appendText(new String(array.slice(offset,offset + length)))
    }
  }
}

//****************************************************************************
