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

import com.wolery.owl.core.Meter
import javax.sound.midi._
import com.wolery.owl.midi.messages._

//****************************************************************************

final class TickMap[α](zero: α) extends (Tick => α)
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

  def +=(tick: Tick,value: α): this.type =
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
        what(what.size-1) = value
      }
    }

    this
  }

  override
  def toString: String =
  {
    when.zip(what).mkString("TickMap(",", ",")")
  }
}

object TempoMap
{
  def getTimingMaps(sequence: Sequence): (Tick ⇒ Meter,Tick ⇒ Tempo) =
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
    val track0 = sequence.getTracks()(0)

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
}

//****************************************************************************
