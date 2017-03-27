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

import com.wolery.owl.core.Bool
import com.wolery.owl.midi.messages._
import com.wolery.owl.utils.implicits.asRunnable

import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.css.PseudoClass.getPseudoClass
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
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
  @fx var prev:  Label = _
  @fx var next:  Label = _
  @fx var loop:  Label = _
  @fx var rset:  Label = _
  @fx var rwnd:  Label = _
  @fx var advn:  Label = _
  @fx var stop:  Label = _
  @fx var play:  Label = _

  @fx var secs:  Label = _
  @fx var bars:  Label = _
  @fx var left:  Label = _
  @fx var right: Label = _
  @fx var tempo: Label = _

  val m_seq: Sequencer = owl.sequencer
  val m_tml: Timeline  = new Timeline(
      new KeyFrame(
          millis(100),
          (e: ActionEvent) => onUpdateCounters()))

  var m_num:ℕ = 4
  var m_den:ℕ = 4

  def initialize(): Unit =
  {
    prev.setText("\uf048")
    next.setText("\uf051")
    loop.setText("\uf01e")
    rset.setText("\uf049")
    rwnd.setText("\uf04a")
    advn.setText("\uf04e")
    stop.setText("\uf04d")
    play.setText("\uf04b")

    tempo.setText("1.00")

    m_tml.setCycleCount(Animation.INDEFINITE)
    onUpdateButtons()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TIME  ⇒ defer(onTimeSignatureChange(message.numerator,message.denominator))
    case TEMPO ⇒ defer(onTempoChange(message.tempo))
    case _     ⇒
  }

  def onTempoChange(bpm: ℝ): Unit =
  {
    tempo.setText(f"$bpm%2.2f")
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

    bars.setText(s"$b:$beats:$ticks")
  }

  def ticksPerBeat: ℕ =
  {
    assert(m_seq.getSequence.getDivisionType == PPQ)

    m_seq.getSequence.getResolution
  }

  def onReset(e: MouseEvent): Unit =
  {
    owl.sequencer.setTickPosition(0)
  }

  def onUpdateButtons(): Unit =
  {
    stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    play.pseudoClassStateChanged(PLAYING, isPlaying)
    loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def onStop(e: MouseEvent): Unit =
  {
    m_seq.stop()
    m_tml.stop()
    onUpdateButtons()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    m_seq.start()
    m_tml.play()
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

  val STOPPED = getPseudoClass("stopped")
  val PLAYING = getPseudoClass("playing")
  val LOOPING = getPseudoClass("looping")
}

//****************************************************************************
