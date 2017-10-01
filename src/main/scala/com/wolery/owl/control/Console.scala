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

  final
  def getOnNewline: Handler =
  {
    onNewlineProperty.get()
  }

  final
  def setOnNewline(handler: Handler) =
  {
    onNewlineProperty.set(handler)
  }

  final
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
}

//****************************************************************************
