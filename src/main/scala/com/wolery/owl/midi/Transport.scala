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

import scala.collection.Searching._
import scala.collection.mutable.ArrayBuffer

import com.wolery.owl.core._
import com.wolery.owl.midi.messages._
import com.wolery.owl.core.utilities._

import javax.sound.midi.MetaMessage
import javax.sound.midi.Sequencer

//****************************************************************************

final class Transport(m_seq: Sequencer)
{
  type Microsecond = Long
  type Millisecond = Long

  def isPlaying                       : Bool   = ???
  def isLooping                       : Bool   = ???

//def startsAt                        : Tick   = ???
//def beginsAt                        : Tick   = ???
//def endsAt                          : Tick   = ???
//def first                           : Tick   = ???
//def last                            : Tick   = ???
  def end                             : Tick   = ???
//def offset                          : Tick   = ???
//def extent                          : Tick   = ???
//def span                            : Region = ???
//def finish                          : Tick   = ???

  def play()                          : Unit   = ???
  def stop()                          : Unit   = ???

  def cursor                          : Tick   = ???
  def cursor_=(tick: Tick)            : Unit   = ???

  def meter                           : Meter  = ???
//def meter(tick: Tick)               : Meter  = ???

  def scale                           : Scale  = ???
//def scale(tick: Tick)               : Scale  = ???

  def tempo                           : Tempo  = ???
//def tempo(tick: Tick)               : Tempo  = ???
  def tempo_=(tempo: Tempo)           : Unit   = ???

  def loop                            : Region = ???
  def loop_=(region: Region)          : Unit   = ???

  def looping                         : Bool   = ???
  def looping_=(loop: Bool)           : Unit   = ???

  def ticksPerBeat                    : ℕ      = ???

  case class Region(from: Tick,to: Tick)
  {
    assert(isBetween(from,0L,to))
    assert(isBetween(to,from,end))

    override
    def toString: String = s"[$from,$to]"
  }

  case class Time   (millisecond: ℕ)
  case class Measure(measure: ℕ = 0,beat: ℕ = 0,partial: ℕ = 0)

//implicit def asMeasure(tick: Tick)        : Measure = ???
//implicit def asTime   (tick: Tick)        : Time    = ???
//implicit def asTick   (time: Time)        : Tick    = ???
//implicit def asTick   (measure: Measure)  : Tick    = ???

  def meter      (tick: Tick): Meter          = m_map.m_meter(tick)
  def tempo      (tick: Tick): Tempo          = m_map.m_tempo(tick)
  def scale      (tick: Tick): Scale          = m_map.m_scale(tick)
  def microsecond(tick: Tick): Microsecond    = m_map.m_time (tick)
  def measure    (tick: Tick): Measure        = Measure(m_map.m_measure(tick))
  def tick       (time : Microsecond): Tick   = m_map.m_time.invert(time)
  def tick       (measure: Measure) : Tick    = m_map.m_measure.invert(measure.measure)

  private[this]
  object m_map
  {
    val m_track   = m_seq.getSequence.getTracks.apply(0)
    val m_meter   = new TickFunction [Meter]      (Meter(4,4))
    val m_tempo   = new TickFunction [Tempo]      (120.0)
    val m_scale   = new TickFunction [Scale]      (Scale(C,"ionian").get)
    val m_measure = new TickBijection[ℕ]          (1)
    val m_time    = new TickBijection[Microsecond](0L)

    for (i ← 0 until m_track.size)
    {
      val e = m_track.get(i)

      e.getMessage match
      {
        case m: MetaMessage ⇒ m.getType match
        {
          case METER ⇒
          {
            m_measure += (e.getTick,(e,v) ⇒ (v + e / ticksPerBeat * m_meter.recent.meter).toInt)
            m_meter   += (e.getTick,m.meter)
          }
          case TEMPO ⇒
          {
            m_time    += (e.getTick,(e,v) ⇒ (v + e / (ticksPerBeat * m_tempo.recent) * 60e6).toLong)
            m_tempo   += (e.getTick,m.tempo)
          }
          case SCALE ⇒
          {
            m_scale   += (e.getTick,m.scale)
          }
          case _     ⇒
        }
        case _       ⇒
      }
    }
  }
}

//****************************************************************************

class TickFunction[Value](value: Value,hint: ℕ = 64)
{
  val m_keys = new ArrayBuffer[Tick] (hint)
  val m_vals = new ArrayBuffer[Value](hint)

  m_keys.append(0)
  m_vals.append(value)

  def apply(tick: Tick): Value =
  {
    m_keys.search(tick) match
    {
      case Found(i)          ⇒ m_vals(i)
      case InsertionPoint(i) ⇒ m_vals(i - 1)
    }
  }

  def +=(tick: Tick,value: Value) =
  {
    if (value != m_vals.last)
    {
      if (tick != m_keys.last)
      {
        m_keys.append(tick)
        m_vals.append(value)
      }
      else
      {
        assert(tick > m_keys.last)

        m_vals(m_vals.size - 1) = value
      }
    }

    assert(m_keys.size == m_vals.size)
  }

  def recent: Value = m_vals.last
}

//****************************************************************************

class TickBijection[Value: Ordering](value: Value,hint: ℕ = 64) extends
      TickFunction [Value]          (value: Value,hint: ℕ)
{
  def invert(value: Value): Tick =
  {
    m_vals.search(value) match
    {
      case Found(i)          ⇒ m_keys(i)
      case InsertionPoint(i) ⇒ m_keys(i - 1)
    }
  }

  def +=(tick: Tick,next: (Tick,Value) ⇒ Value) =
  {
    super.+= (tick,next(tick - m_keys.last,m_vals.last))
  }
}

//****************************************************************************
//  m_meter   += (0,Meter(4,4))
//  m_scale   += (0,Scale(C,"ionian").get)
//  m_time    += (0,0)
//  m_measure += (0,0)

//****************************************************************************
