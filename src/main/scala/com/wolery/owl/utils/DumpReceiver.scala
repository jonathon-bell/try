//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Displays the file format information of a MIDI file.
//*
//*
//*  See Also: https://www.midi.org/specifications/item/table-1-summary-of-midi-message
//*            http://www.somascape.org/midi/tech/mfile.html
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.utils

//****************************************************************************

import java.io.PrintStream
import javax.sound.midi._

import com.wolery.owl.core._

//****************************************************************************

class DumpReceiver(m_out: PrintStream = System.out) extends Receiver
{
  def close(): Unit =
  {}

  def send(mm: MidiMessage,ts: Long): Unit = synchronized
  {
    if (ts != -1)
    {
      m_out.print(s"$ts ")
    }

    if (true)
    {
      m_out.print(hex(mm))
      m_out.print(' ')
    }

    mm match
    {
      case m: ShortMessage if isChannelMessage(m)⇒ onChannelMessage(m)
      case m: ShortMessage                       ⇒ onSystemMessage(m)
      case m: MetaMessage                        ⇒ onMetaMessage(m)
      case m: SysexMessage                       ⇒ onSysexMessage(m)
      case _                                     ⇒ m_out.print("unknown message type")
    }

    m_out.println()
  }

  def isChannelMessage(mm: ShortMessage): Boolean =
  {
    mm.getCommand != 0xF0
  }

  def isSystemMessage (mm: ShortMessage): Boolean =
  {
    mm.getCommand == 0xF0
  }

  protected
  def onChannelMessage(mm: ShortMessage): Unit =
  {
    require(isChannelMessage(mm))

    def chan = f"ch ${mm.getChannel + 1}%02d"
    def note = f"${Pitch(mm.getData1).toString}%-3s"
    def val1 = f"${mm.getData1}%03d"
    def val2 = f"${mm.getData2}%03d"
    def long = (mm.getData1 & 0x7F) | ((mm.getData2 & 0x7F) << 7)

    m_out.print(chan)
    m_out.print(' ')

    mm.getCommand match
    {
      case 0x80 ⇒ m_out.print(s"note-off   $note ($val2)")
      case 0x90 ⇒ m_out.print(s"note-on    $note ($val2)")
      case 0xA0 ⇒ m_out.print(s"p-pressure $note ($val2)")
      case 0xB0 ⇒ m_out.print(s"controller $val1 ($val2)")
      case 0xC0 ⇒ m_out.print(s"program    $val1")
      case 0xD0 ⇒ m_out.print(s"c-pressure $note")
      case 0xE0 ⇒ m_out.print(s"pitch-bend $long")
      case _    ⇒ m_out.print("unknown message")
    }
  }

  protected
  def onSystemMessage(mm: ShortMessage): Unit =
  {
    require(isSystemMessage(mm))

    def long = (mm.getData1 & 0x7F) | ((mm.getData2 & 0x7F) << 7)
    def song = f"${mm.getData1}%03d"

    def mtcQuarterFrame =
    {
      mm.getData1 & 0x70 match
      {
        case 0x00 ⇒ m_out.print("frame count LS:   ")
        case 0x10 ⇒ m_out.print("frame count MS:   ")
        case 0x20 ⇒ m_out.print("seconds count LS: ")
        case 0x30 ⇒ m_out.print("seconds count MS: ")
        case 0x40 ⇒ m_out.print("minutes count LS: ")
        case 0x50 ⇒ m_out.print("minutes count MS: ")
        case 0x60 ⇒ m_out.print("hours count LS:   ")
        case 0x70 ⇒ m_out.print("hours count MS:   ")
      }

      m_out.print(mm.getData1 & 0x0F)
    }

    mm.getStatus & 0x0F match
    {
      case 0x0 ⇒ m_out.print(s"sysex[           ")
      case 0x1 ⇒ m_out.print(s"mtc quarter frame mtcQuarterFrame")
      case 0x2 ⇒ m_out.print(s"song position $long")
      case 0x3 ⇒ m_out.print(s"song select $song")
      case 0x6 ⇒ m_out.print(s"tune request     ")
      case 0x7 ⇒ m_out.print(s"sysex]           ")
      case 0x8 ⇒ m_out.print(s"timing clock     ")
      case 0xA ⇒ m_out.print(s"start            ")
      case 0xB ⇒ m_out.print(s"continue         ")
      case 0xC ⇒ m_out.print(s"stop             ")
      case 0xE ⇒ m_out.print(s"active sensing   ")
      case 0xF ⇒ m_out.print(s"system reset     ")
      case  _  ⇒ m_out.print(s"?")
    }
  }

  protected
  def onMetaMessage(mm: MetaMessage): Unit =
  {
    def i(i: ℕ): ℤ   = mm.getData.apply(i) & 0xFF
    def text: String = "'" + new String(mm.getData) + "'"

    def key : String =
    {
      val keys = Seq("C♭","G♭","D♭","A♭","E♭","B♭","F","C","G","D","A","E","B","F♯","C♯")
      val mode = Seq(" maj"," min")

      keys(7 + i(0)) + mode(i(1))
    }

    def tempo: Float =
    {
      // tempo in microseconds per beat
      val mspb = (i(0) << 16) | (i(1) <<  8) | i(2)
      val mspq = if (mspb <= 0) 60e6f / 0.1f
                 else           60e6f / mspb
      // truncate it to 2 digits after dot
      Math.round(mspq * 100.0F) / 100.0F
    }

    def offset: String        = s"${i(0)}:${i(1)}:${i(2)}:${i(3)}:${i(4)}"

    def timesig: String       = s"${i(0)}/${1<<i(1)}; clocks per pulse: ${i(2)}; 1/32 per 24 clocks: ${i(3)}"

    def sequence: ℤ           = (i(0) << 8) | i(1)

    mm.getType match
    {
      case 0x00 ⇒ m_out.print(s"sequence-number   $sequence")
      case 0x01 ⇒ m_out.print(s"text              $text")
      case 0x02 ⇒ m_out.print(s"copyright         $text")
      case 0x03 ⇒ m_out.print(s"title             $text")
      case 0x04 ⇒ m_out.print(s"instrument        $text")
      case 0x05 ⇒ m_out.print(s"lyric             $text")
      case 0x06 ⇒ m_out.print(s"marker            $text")
      case 0x07 ⇒ m_out.print(s"cue point         $text")
      case 0x08 ⇒ m_out.print(s"program           $text")
      case 0x09 ⇒ m_out.print(s"device            $text")
      case 0x20 ⇒ m_out.print(s"channel           ${i(0)}")
      case 0x21 ⇒ m_out.print(s"port              ${i(0)}")
      case 0x2F ⇒ m_out.print(s"end-of-track")
      case 0x51 ⇒ m_out.print(s"tempo             $tempo bpm")
      case 0x54 ⇒ m_out.print(s"SMTPE-offset      $offset")
      case 0x58 ⇒ m_out.print(s"time-signature    $timesig")
      case 0x59 ⇒ m_out.print(s"key-signature     $key")
      case 0x7F ⇒ m_out.print("sequencer-specific"+hex(mm))
      case _    ⇒ m_out.print("unknown meta event"+hex(mm))
    }
  }

  protected
  def onSysexMessage(mm: SysexMessage): Unit =
  {
    m_out.print("sysex " + hex(mm,1,10))
  }

  protected
  def hex(mm: MidiMessage,min: ℕ = 3,max: ℕ = 3): String =
  {
    require(0<min && min <= max)

    val b = mm.getMessage.take(max)
    val n = mm.getLength
    val s = new StringBuffer(3 * (max+1) + 2)

    s.append(s"${Console.CYAN}[")

    for (i ← 0 until 1)
    {
      s.append(f"${b(i)}%02X")
    }

    for (i ← 1 until b.size)
    {
      s.append(f" ${b(i)}%02X")
    }

    for (i ← b.size until min)
    {
      s.append("   ")
    }

    if (max < n)
    {
      s.append('…')
    }
    else
    {
      s.append(']')
    }

    s.append(s"${Console.RESET}")
    s.toString
  }
}

//****************************************************************************
