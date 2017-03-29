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
import com.wolery.owl.utils.implicits._
import com.wolery.owl.utils.implicits.asEventHandler
import com.wolery.owl.utils.implicits.asRunnable

import javafx.animation._
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.css.PseudoClass.getPseudoClass
import javafx.event._
import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.util.Duration.millis
import javax.sound.midi.MetaEventListener
import javax.sound.midi.MetaMessage
import javax.sound.midi.Sequence._
import javax.sound.midi.Sequencer

//****************************************************************************

class TransportController extends MetaEventListener
{
  @fx var m_bars:  Label = _
  @fx var m_prev:  Label = _
  @fx var m_rwnd:  Label = _
  @fx var m_fwrd:  Label = _
  @fx var m_next:  Label = _
  @fx var m_rset:  Label = _
  @fx var m_stop:  Label = _
  @fx var m_play:  Label = _
  @fx var m_loop:  Label = _
  @fx var m_left:  Label = _
  @fx var m_right: Label = _
  @fx var m_tempo: Label = _
  @fx var m_meter: Label = _
  @fx var m_scale: Label = _

      val m_seq  : Sequencer = owl.sequencer
      val m_tmr  : Timeline  = newTimer()
      var m_mtr  : Meter     = Meter(4,4)

  def initialize(): Unit =
  {
    m_prev.setText("\uf048")
    m_rwnd.setText("\uf04a")
    m_fwrd.setText("\uf04e")
    m_next.setText("\uf051")
    m_rset.setText("\uf049")
    m_stop.setText("\uf04d")
    m_play.setText("\uf04b")
    m_loop.setText("\uf01e")

    m_meter.setText(m_mtr.toString)
    m_tempo.setText("128.00")

    updateButtons()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case METER ⇒ defer(onMeterChange(message.meter))
    case TEMPO ⇒ defer(onTempoChange(message.bpm))
    case SCALE ⇒ defer(onScaleChange(message.scale))
    case _     ⇒
  }

  def onMeterChange(meter: Meter): Unit =
  {
     m_mtr = meter
     m_meter.setText(meter.toString)
  }

  def onTempoChange(bpm: ℝ): Unit =
  {
    println(bpm)
    m_tempo.setText(f"$bpm%3.2f")
  }

  def onScaleChange(scale: Scale): Unit =
  {
    m_scale.setText(scale.toString)
  }

  def onReset(e: MouseEvent): Unit =
  {
    m_seq.setTickPosition(0)
  }

  def onStop(e: MouseEvent): Unit =
  {
    m_seq.stop()
    m_tmr.stop()
    updateButtons()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    m_seq.start()
    m_tmr.play()
    updateButtons()
  }

  def onLoop(e: MouseEvent): Unit =
  {
    m_seq.setLoopCount(if (isLooping) 0 else -1)
    updateButtons()
  }

  def onPrevious (e: MouseEvent): Unit = println("previous")
  def onNext     (e: MouseEvent): Unit = println("next")
  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onAdvance  (e: MouseEvent): Unit = println("advance")
  def isLooping: Bool = m_seq.getLoopCount != 0
  def isPlaying: Bool = m_seq.isRunning

  def update(): Unit =
  {
    assert(m_seq.getSequence.getDivisionType == 0) // PPQ

    val tpb   = m_seq.getSequence.getResolution
    val tick  = m_seq.getTickPosition
    val beat  = tick / tpb
    val bars  = beat / m_mtr.count
    val beats = beat % m_mtr.count
    val parts = tick % tpb

    m_bars.setText(f"$bars%04d.$beats%02d.$parts%03d")
  }

  def updateButtons(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def newTimer(): Timeline =
  {
    val t = new Timeline(new KeyFrame(millis(100),(a:ActionEvent) ⇒ update()))
    t.setCycleCount(-1)
    t
  }

  val STOPPED = getPseudoClass("stopped")
  val PLAYING = getPseudoClass("playing")
  val LOOPING = getPseudoClass("looping")
}

//****************************************************************************
