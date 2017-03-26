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

package com.wolery.owl.midi

//****************************************************************************

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import com.wolery.owl.core.Scale
import com.wolery.owl.core._

import javax.sound.midi.MetaMessage
import javax.sound.midi.ShortMessage
import javax.sound.midi.MidiMessage

//****************************************************************************

object messages
{
//Meta Events

  val SEQUENCE:      Byte = 0x00
  val TEXT:          Byte = 0x01
  val COPYRIGHT:     Byte = 0x02
  val TITLE:         Byte = 0x03
  val INSTRUMENT:    Byte = 0x04
  val LYRIC:         Byte = 0x05
  val MARKER:        Byte = 0x06
  val CUE:           Byte = 0x07
  val PROGRAM:       Byte = 0x08
  val DEVICE:        Byte = 0x09
  val CHANNEL:       Byte = 0x20
  val PORT:          Byte = 0x21
  val END:           Byte = 0x2F
  val TEMPO:         Byte = 0x51
  val SMPTE:         Byte = 0x54
  val TIME:          Byte = 0x58
  val KEY:           Byte = 0x59

//Owl Events

  val HARMONY:       Byte = 0x60
  val STRING:        Byte = 0x61
  val POSITION:      Byte = 0x62

  implicit final class ShortMessageEx(val m: ShortMessage) extends AnyVal
  {
    def isChannelMessage: Bool  = m.getCommand != 0xF0
    def isSystemMessage:  Bool  = m.getCommand == 0xF0
    def pitch          :  Pitch = Pitch(m.getData1)
    def integer        :  ℕ     = (m.getData1 & 0x7F) | ((m.getData2 & 0x7F) << 7)
  }

  implicit final class MetaMessageEx(val m: MetaMessage) extends AnyVal
  {
    def sequence: ℤ =
    {
      assert(m.getType == SEQUENCE)
      (int(0) << 8) | int(1)
    }

    def text:      String = asString(TEXT)
    def copyright: String = asString(COPYRIGHT)
    def title:     String = asString(TITLE)
    def instrument:String = asString(INSTRUMENT)
    def lyric:     String = asString(LYRIC)
    def marker:    String = asString(MARKER)
    def cue:       String = asString(CUE)
    def program:   String = asString(PROGRAM)
    def device:    String = asString(COPYRIGHT)

    def channel: ℕ =
    {
      assert(m.getType == CHANNEL)

      int(0)
    }

    def port: ℕ =
    {
      assert(m.getType == PORT)

      int(0)
    }

    def tempo: ℝ =
    {
      assert(m.getType == TEMPO)

      val mspb = (int(0) << 16) | (int(1) <<  8) | int(2)
      val mspq =
      if (mspb <= 0)
      {
        60e6f / 0.1F
      }
      else
      {
        60e6f / mspb
      }
        Math.round(mspq * 100.0F) / 100.0F
    }

    def smpte: (ℕ,ℕ,ℕ,ℕ,ℕ) =
    {
      assert(m.getType == SMPTE)

      (int(0),int(1),int(2),int(3),int(4))
    }

    def time: (ℕ,ℕ) =
    {
      assert(m.getType == TIME)

      (int(0) , 1 << int(1))
    }

    def key: String =
    {
      assert(m.getType == KEY)

      val keys = Seq("C♭","G♭","D♭","A♭","E♭","B♭","F","C","G","D","A","E","B","F♯","C♯")
      val mode = Seq(" maj"," min")

      keys(7 + int(0)) + mode(int(1))
    }

    def scale:Scale = asObject[Scale](HARMONY)
    def string:   ℕ = asObject[ℕ](STRING)
    def position: ℕ = asObject[ℕ](POSITION)

    private
    def int(index: ℕ): ℤ =
    {
      m.getData.apply(index) & 0xFF
    }

    private
    def asString(byte: Byte): String =
    {
      assert(m.getType == byte)

      new String(m.getData)
    }

    private
    def asObject[α](byte: Byte): α =
    {
      assert(m.getType == byte)
      val b = new ByteArrayInputStream(m.getData)
      val i = new ObjectInputStream(b)

      val t = i.readObject.asInstanceOf[α]
      i.close
      t
    }
  }

  private
  def create[α](byte: Byte,any: α): MetaMessage =
  {
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)

    o.writeObject(any)
    o.close

    new MetaMessage(byte,b.toByteArray,b.size)
  }

  def harmony(scale: Scale): MetaMessage = create[Scale](HARMONY,scale)
  def position(position: ℕ): MetaMessage = create[ℕ]    (POSITION,position)
}
//****************************************************************************
