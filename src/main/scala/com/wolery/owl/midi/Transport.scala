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

//****************************************************************************

import scala.collection.Searching.{ Found, InsertionPoint, search }
import scala.collection.mutable.ArrayBuffer

import com.wolery.owl.core.{ Bool, Meter }
import com.wolery.owl.midi.messages.{ METER, MetaMessageEx, TEMPO }

import javax.sound.midi.{ MetaMessage, Sequencer }

//****************************************************************************

final class Transport(m_seq: Sequencer)
{
  def isPlaying: Bool                         = m_seq.isRunning
  def isRunning: Bool                         = m_seq.isRunning
  def isLooping: Bool                         = m_seq.getLoopCount < 0

  def start(): Unit                           = m_seq.start()
  def stop(): Unit                            = m_seq.stop()

  def getTempoInBPM: Tempo                    = m_seq.getTempoFactor * m_seq.getTempoInBPM

  def setTempoInBPM(bpm: Tempo): Unit         = m_seq.setTempoFactor(bpm.toFloat / m_seq.getTempoInBPM)

  def getTickLength: Tick                     = m_seq.getTickLength
  def getTickPosition: Tick                   = m_seq.getTickPosition
  def setTickPosition(tick: Tick): Unit       = m_seq.setTickPosition(tick)

  def getMicrosecondLength: Long              = m_seq.getMicrosecondLength
  def getMicrosecondPosition: Long            = m_seq.getMicrosecondPosition
  def setMicrosecondPosition(ms: Long): Unit  = m_seq.setMicrosecondPosition(ms)

  def getLoopStartPoint: Tick                 = m_seq.getLoopStartPoint
  def setLoopStartPoint(tick: Tick): Unit     = m_seq.setLoopStartPoint(tick)
  def getLoopEndPoint: Tick                   = m_seq.getLoopEndPoint
  def setLoopEndPoint(tick: Tick): Unit       = m_seq.setLoopStartPoint(tick)
  def setLooping(loop: Bool): Unit            = m_seq.setLoopCount(if (loop) -1 else 0)

  def getTicksPerBeat: ℕ                      = m_seq.getSequence.getResolution

  private
  def getTimingMaps(): (Tick ⇒ Meter,Tick ⇒ Tempo)=
  {
    class TickMap[α](zero: α) extends (Tick => α)
    {
      val when = ArrayBuffer(0L)
      val what = ArrayBuffer(zero)

      def apply(tick: Tick): α =
      {
        when.search(tick) match
        {
          case Found(i)          ⇒ what(i)
          case InsertionPoint(i) ⇒ what(i - 1)
        }
      }

      def +=(tick: Tick,value: α): Unit =
      {
        if (value != what.last)
        {
          if (tick != when.last)
          {
            when.append(tick)
            what.append(value)
          }
          else
          {
            what(what.size - 1) = value
          }
        }
      }

      override
      def toString: String =
      {
        when.zip(what).mkString("TickMap(",", ",")")
      }
    }

    val meters = new TickMap(Meter(4,4))
    val tempos = new TickMap(120.0)
    val track0 = m_seq.getSequence.getTracks()(0)

    for (i ← 0 until track0.size)
    {
      val e = track0.get(i)

      e.getMessage match
      {
        case m: MetaMessage ⇒ m.getType match
        {
          case METER ⇒ meters += (e.getTick,m.meter)
          case TEMPO ⇒ tempos += (e.getTick,m.tempo)
          case _     ⇒
        }
        case _       ⇒
      }

    }

    (meters,tempos)
  }
  private
  val (m_meter,m_tempo) = getTimingMaps()
}

//****************************************************************************
