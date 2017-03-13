//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Displays the file format information of a MIDI file.
//*
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
import javax.sound.midi.SysexMessage._

//****************************************************************************

final class DumpReceiver(m_out: PrintStream = System.out, m_ticks: Boolean = false) extends Receiver
{
  val notes = Seq(
    "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

  val keys = Seq(
    "Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A", "E", "B", "F#", "C#")

  val SYSTEM_MESSAGE_TEXT = Seq(
    "System Exclusive (should not be in ShortMessage!)",
    "MTC Quarter Frame: ",
    "Song Position: ",
    "Song Select: ",
    "Undefined",
    "Undefined",
    "Tune Request",
    "End of SysEx (should not be in ShortMessage!)",
    "Timing clock",
    "Undefined",
    "Start",
    "Continue",
    "Stop",
    "Undefined",
    "Active Sensing",
    "System Reset")

  val QUARTER_FRAME_MESSAGE_TEXT = Seq(
    "frame count LS: ",
    "frame count MS: ",
    "seconds count LS: ",
    "seconds count MS: ",
    "minutes count LS: ",
    "minutes count MS: ",
    "hours count LS: ",
    "hours count MS: ")

  val FRAME_TYPE_TEXT = Seq(
    "24 frames/second",
    "25 frames/second",
    "30 frames/second (drop)",
    "30 frames/second (non-drop)")

  def close() = {}

  def send(mm: MidiMessage,ts: Long) =
  {
    onTime(ts)

    mm match
    {
      case sm: ShortMessage ⇒ onShortMessage(sm)
      case sx: SysexMessage ⇒ onSysExMessage(sx)
      case mm: MetaMessage  ⇒ m_out.print(onMetaMessage(mm))
      case _                ⇒ m_out.print("unknown message type")
    }

    m_out.println()
  }

  def onShortMessage(mm: ShortMessage): Unit =
  {
    onHex(mm)

    if (mm.getCommand != 0xF0)
    {
      m_out.print(f"channel ${mm.getChannel + 1}%2d: ")
    }

    mm.getCommand match
    {
      case 0x80 ⇒ m_out.print("note Off "          +noteName(mm.getData1)+" velocity: "+mm.getData2)
      case 0x90 ⇒ m_out.print("note On "           +noteName(mm.getData1)+" velocity: "+mm.getData2)
      case 0xa0 ⇒ m_out.print("poly key pressure " +noteName(mm.getData1)+" pressure: "+mm.getData2)
      case 0xb0 ⇒ m_out.print("control change "    +mm.getData1+             " value: "+mm.getData2)
      case 0xc0 ⇒ m_out.print("program change "    +mm.getData1)
      case 0xd0 ⇒ m_out.print("key pressure "      +noteName(mm.getData1)+" pressure: "+mm.getData2)
      case 0xe0 ⇒ m_out.print("pitch wheel change "+get14bitValue(mm.getData1, mm.getData2))
      case 0xF0 ⇒ m_out.print(SYSTEM_MESSAGE_TEXT(mm.getChannel))

        mm.getChannel match
        {
          case 0x1 ⇒
            val nQType = (mm.getData1 & 0x70) >> 4
            var nQData =  mm.getData1 & 0x0F

            if (nQType == 7)
            {
              nQData = nQData & 0x1
            }

            m_out.print(QUARTER_FRAME_MESSAGE_TEXT(nQType))
            m_out.print(nQData.toString)

            if (nQType == 7)
            {
              val nFrameType = (mm.getData1 & 0x06) >> 1

              m_out.print(", frame type: "+FRAME_TYPE_TEXT(nFrameType))
            }

          case 0x2 ⇒ m_out.print(get14bitValue(mm.getData1, mm.getData2))
          case 0x3 ⇒ m_out.print(mm.getData1)
        }

      case _ ⇒ m_out.print("unknown message: status = "+mm.getStatus+", byte1 = "+mm.getData1+", byte2 = "+mm.getData2)
    }
  }

  def onSysExMessage(mm: SysexMessage): Unit =
  {
    mm.getStatus match
    {
      case SYSTEM_EXCLUSIVE         ⇒ m_out.print("Sysex message: F0"         +getHexString(mm.getData))
      case SPECIAL_SYSTEM_EXCLUSIVE ⇒ m_out.print("Continued Sysex message F7"+getHexString(mm.getData))
      case _                        ⇒
    }
  }

  def onMetaMessage(mm: MetaMessage): String =
    {
      val abMessage: Array[Byte] = mm.getMessage();
      val abData: Array[Byte] = mm.getData();
      val nDataLength: Int = mm.getLength();
      var strMessage: String = null;
      // System.out.println("data array length: " + abData.length);
      (mm.getType()) match {
        case 0 ⇒
          val nSequenceNumber: Int = ((abData(0) & 0xFF) << 8) | (abData(1) & 0xFF)
          strMessage = "Sequence Number: "+nSequenceNumber

        case 1 ⇒
          val strText: String = new String(abData)
          strMessage = "Text Event: "+strText

        case 2 ⇒
          val strCopyrightText: String = new String(abData)
          strMessage = "Copyright Notice: "+strCopyrightText

        case 3 ⇒
          val strTrackName: String = new String(abData)
          strMessage = "Sequence/Track Name: "+strTrackName

        case 4 ⇒
          val strInstrumentName: String = new String(abData)
          strMessage = "Instrument Name: "+strInstrumentName

        case 5 ⇒
          val strLyrics: String = new String(abData)
          strMessage = "Lyric: "+strLyrics

        case 6 ⇒
          val strMarkerText: String = new String(abData)
          strMessage = "Marker: "+strMarkerText

        case 7 ⇒
          val strCuePointText: String = new String(abData)
          strMessage = "Cue Point: "+strCuePointText

        case 0x20 ⇒
          val nChannelPrefix: Int = abData(0) & 0xFF
          strMessage = "MIDI Channel Prefix: "+nChannelPrefix.toString

        case 0x2F ⇒
          strMessage = "End of Track";

        case 0x51 ⇒
          val nTempo: Int = ((abData(0) & 0xFF) << 16) |
            ((abData(1) & 0xFF) << 8) |
            (abData(2) & 0xFF) // tempo in microseconds per beat
          var bpm: Float = convertTempo(nTempo)
          // truncate it to 2 digits after dot
          bpm = (Math.round(bpm * 100.0f) / 100.0f)
          strMessage = "Set Tempo: "+bpm+" bpm"

        case 0x54 ⇒
          // System.out.println("data array length: " + abData.length);
          strMessage = "SMTPE Offset: "+(abData(0) & 0xFF).toString+":"+
            (abData(1) & 0xFF).toString+":"+
            (abData(2) & 0xFF).toString+"."+
            (abData(3) & 0xFF).toString+"."+
            (abData(4) & 0xFF)

        case 0x58 ⇒
          strMessage = "Time Signature: "
          +(abData(0) & 0xFF)+"/"+
            (1 << (abData(1) & 0xFF))+
            ", MIDI clocks per metronome tick: "+(abData(2) & 0xFF)+", 1/32 per 24 MIDI clocks: "+(abData(3) & 0xFF);

        case 0x59 ⇒
          val strGender: String = if (abData(1) == 1) "minor" else "major"
          strMessage = "Key Signature: "+keys(abData(0) + 7)+" "+strGender;

        case 0x7F ⇒
          // TODO: decode vendor code, dump data in rows
          val strDataDump: String = getHexString(abData)
          strMessage = "Sequencer-Specific Meta event: "+strDataDump;

        case _ ⇒
          val strUnknownDump: String = getHexString(abData);
          strMessage = "unknown Meta event: "+strUnknownDump;
      }
      strMessage
    }

  def noteName(midi: Midi): String =
  {
    if (midi > 127)
    {
      "illegal value"
    }
    else
    {
      notes(midi % 12) + (midi / 12 - 1).toString
    }
  }

  def get14bitValue(lo: Int,hi: Int): Int =
  {
    (lo  & 0x7F) | ((hi & 0x7F) << 7)
  }

  // convert from microseconds per quarter note to beats per minute and vice versa

  def convertTempo(value: Float): Float =
  {
    if (value <= 0)
      60e6f / 0.1f
    else
      60e6f / value
  }

  val hexDigits: Array[Char] = Array(
    '0', '1', '2', '3',
    '4', '5', '6', '7',
    '8', '9', 'A', 'B',
    'C', 'D', 'E', 'F')

  def getHexString(bytes: Array[Byte]): String =
  {
    val s = new StringBuffer(bytes.length * 3 + 2)

    for (i ← 0 until bytes.length)
    {
      s.append(' ');
      s.append(hexDigits((bytes(i) & 0xF0) >> 4))
      s.append(hexDigits( bytes(i) & 0x0F))
    }

    s.toString
  }

  def onTime(ts: Long): Unit =
  {
    if (ts == -1L)
    {
      m_out.print("time: [?] ")
    }
    else
    if (m_ticks)
    {
      m_out.print(s"tick: $ts ")
    }
    else
    {
      m_out.print(s"time: $ts us ")
    }
  }

  def hex(i: Int): String = f"$i%02X"

  def onHex(mm: ShortMessage) =
  {
    m_out.print('[')
    m_out.print(hex(mm.getStatus))

    if (mm.getLength > 1)
    {
      m_out.print(' ')
      m_out.print(hex(mm.getData1))
    }

    if (mm.getLength > 2)
    {
      m_out.print(' ')
      m_out.print(hex(mm.getData2))
    }

    m_out.print(" " * (9 - 3 * mm.getLength))
    m_out.print("] ")
  }

  def onHex(bytes: Array[Byte]): Unit =
  {
    for (i ← 0 until bytes.length)
    {
      m_out.print(' ');
      m_out.print(hex(bytes(i)))
    }
  }
}

//****************************************************************************
