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

import javafx.beans.property.{ObjectProperty,SimpleObjectProperty}
import javafx.event.{ActionEvent,EventHandler}
import javafx.scene.control.TextArea

/**
 * @author Jonathon Bell
 */
class NewlineEvent(val text: String) extends ActionEvent

/**
 * buffer
 * onNewline
 * @author Jonathon Bell
 */
class Console extends TextArea
{
  type Handler = EventHandler[NewlineEvent]

  private
  var m_pos: ℕ = 0

  private
  var m_rdy: Bool = true

  def getBuffer: String =
  {
    getText.substring(m_pos)
  }

  def getOnNewline: Handler =
  {
    onNewlineProperty.get()
  }

  def setOnNewline(handler: Handler) =
  {
    onNewlineProperty.set(handler)
  }

  val onNewlineProperty: ObjectProperty[Handler] =
  {
    new SimpleObjectProperty(this,"onNewline")
  }

  override
  def replaceText(start: ℕ,end: ℕ,text: String) =
  {
    if (start >= m_pos)
    {
      super.replaceText(start,end,text)

      if (m_rdy && text.contains("\n"))
      {
        m_rdy = false
        getOnNewline.handle(new NewlineEvent(getBuffer))
        m_pos = getLength
        m_rdy = true
      }
    }
  }

  def getPrintWriter: java.io.PrintWriter =
  {
    object writer extends java.io.Writer
    {
      def close: Unit = {}
      def flush: Unit = {}
      def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
      {
        appendText(new String(array.slice(offset,offset + length)))
      }
    }

    new java.io.PrintWriter(writer,true)
  }
}

//****************************************************************************
