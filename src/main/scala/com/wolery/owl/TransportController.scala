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

import com.wolery.owl.core._
import com.wolery.owl.midi.messages._
import com.wolery.owl.utils.implicits.asEventHandler
import com.wolery.owl.utils.implicits.asRunnable

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.css.PseudoClass.getPseudoClass
import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.util.Duration.millis
import javax.sound.midi.MetaEventListener
import javax.sound.midi.MetaMessage
import javax.sound.midi.Sequencer
import javax.management.timer.Timer
import javafx.util.Duration.millis
import javafx.animation._
import javafx.event.ActionEvent
import javafx.event._
import com.wolery.owl.utils.implicits._
import javax.sound.midi.Sequence._

//****************************************************************************

class TransportController extends MetaEventListener
{
  @fx var m_prev:  Label = _
  @fx var m_next:  Label = _
  @fx var m_loop:  Label = _
  @fx var m_rset:  Label = _
  @fx var m_rwnd:  Label = _
  @fx var m_advn:  Label = _
  @fx var m_stop:  Label = _
  @fx var m_play:  Label = _

  @fx var m_harm:  Label = _
  @fx var m_bars:  Label = _
  @fx var m_left:  Label = _
  @fx var m_right: Label = _
  @fx var m_tempo: Label = _

      val m_seq: Sequencer = owl.sequencer
      val m_tmr: Timeline = newTimer()
      var m_meter: Meter  = Meter(1,1)

  var m_num:ℕ = 4
  var m_den:ℕ = 4

  def initialize(): Unit =
  {
    m_prev.setText("\uf048")
    m_next.setText("\uf051")
    m_loop.setText("\uf01e")
    m_rset.setText("\uf049")
    m_rwnd.setText("\uf04a")
    m_advn.setText("\uf04e")
    m_stop.setText("\uf04d")
    m_play.setText("\uf04b")

    m_tempo.setText("1.00")

    m_tmr.setCycleCount(Animation.INDEFINITE)
    onUpdateButtons()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TEMPO ⇒ defer(onTempoChange(message.tempo))
    case METER ⇒ defer(onMeterChange(message.meter))
    case SCALE ⇒ defer(onScaleChange(message.scale))
    case _     ⇒
  }

  def onTempoChange(bpm: ℝ): Unit =
  {
    m_tempo.setText(f"$bpm%2.2f")
  }

  def onMeterChange(meter: Meter): Unit =
  {
    m_meter = meter
  }

  def onScaleChange(scale: Scale): Unit =
  {
    m_harm.setText(scale.toString)
  }

  def onTimeSignatureChange(numerator: ℕ,denominator: ℕ): Unit =
  {
    m_num = numerator
    m_den = denominator
  }

  def onUpdateCounters(): Unit =
  {
    val tpb   = ticksPerBeat
    val tick  = m_seq.getTickPosition
    val beats = tick / tpb
    val b     = beats / m_num
    val ticks = tick %  tpb

    m_bars.setText(s"$b:$beats:$ticks")
  }

  def ticksPerBeat: ℕ =
  {
    assert(m_seq.getSequence.getDivisionType == PPQ)

    m_seq.getSequence.getResolution
  }

  def onReset(e: MouseEvent): Unit =
  {
    m_seq.setTickPosition(0)
  }

  def onUpdateButtons(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def onStop(e: MouseEvent): Unit =
  {
    m_seq.stop()
    m_tmr.stop()
    onUpdateButtons()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    m_seq.start()
    m_tmr.play()
    onUpdateButtons()
  }

  def onLoop(e: MouseEvent): Unit =
  {
    m_seq.setLoopCount(if (isLooping) 0 else -1)
    onUpdateButtons()
  }

  def onPrevious (e: MouseEvent): Unit = println("previous")
  def onNext     (e: MouseEvent): Unit = println("next")
  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onAdvance  (e: MouseEvent): Unit = println("advance")
  def isLooping: Bool = owl.sequencer.getLoopCount != 0
  def isPlaying: Bool = owl.sequencer.isRunning

  def onUpdate(): Unit =
  {
    assert(m_seq.getSequence.getDivisionType == 0) // PPQ

    val tick = m_seq.getTickPosition
    val bars = tick / m_meter.count
    val beat = tick % m_meter.count

    m_bars.setText(f"$bars%04d.$beat%02d.00")
  }

  def newTimer(): Timeline =
  {
    val t = new Timeline(new KeyFrame(millis(100),(a:ActionEvent) ⇒ onUpdate()))
    t.setCycleCount(-1)
    t
  }

  val STOPPED = getPseudoClass("stopped")
  val PLAYING = getPseudoClass("playing")
  val LOOPING = getPseudoClass("looping")
}

//****************************************************************************
