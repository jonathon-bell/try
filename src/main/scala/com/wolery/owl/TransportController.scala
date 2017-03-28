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
import com.wolery.owl.midi.messages.HARMONY
import com.wolery.owl.midi.messages.MetaMessageEx
import com.wolery.owl.midi.messages.TEMPO
import com.wolery.owl.midi.messages.TIME
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
      var m_meter: (ℕ,ℕ) = (1,1)

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

    onStateChange()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TEMPO   ⇒ defer(onTempoChange(message.tempo))
    case TIME    ⇒ defer(onMeterChange(message.time))
    case HARMONY ⇒ defer(onScaleChange(message.scale))
    case _       ⇒
  }

  def onTempoChange(bpm: ℝ) =
  {
    m_tempo.setText(f"$bpm%2.2f")
  }

  def onMeterChange(meter: (ℕ,ℕ)): Unit =
  {
    m_meter = meter
  }

  def onScaleChange(scale: Scale): Unit =
  {
    m_harm.setText(scale.toString)
  }

  def onReset(e: MouseEvent): Unit =
  {
    m_seq.setTickPosition(0)
  }

  def onStateChange(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def onStop(e: MouseEvent): Unit =
  {
    m_seq.stop()
    m_tmr.stop()
    onStateChange()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    m_seq.start()
    m_tmr.play()
    onStateChange()
  }

  def onLoop(e: MouseEvent): Unit =
  {
    m_seq.setLoopCount(if (isLooping) 0 else -1)
    onStateChange()
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
    val bars = tick / m_meter._1
    val beat = tick % m_meter._1

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
