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

import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

//****************************************************************************

class TransportController extends MetaEventListener
{
  @fx var m_bars:  Label = _
  @fx var m_prev:  Pane  = _
  @fx var m_rwnd:  Pane  = _
  @fx var m_fwrd:  Pane  = _
  @fx var m_next:  Pane  = _
  @fx var m_rset:  Pane  = _
  @fx var m_stop:  Pane  = _
  @fx var m_play:  Pane  = _
  @fx var m_loop:  Pane  = _
  @fx var m_left:  Label = _
  @fx var m_right: Label = _
  @fx var m_tempo: Spinner[ℝ] = _
  @fx var m_meter: Label = _
  @fx var m_scale: Label = _

      val m_seq  : Sequencer = owl.sequencer
      val m_tmr  : Timeline  = newTimer()
      var m_mtr  : Meter     = _

  def initialize(): Unit =
  {

    m_tempo.setValueFactory((new DoubleSpinnerValueFactory(1,300,owl.sequencer.getTempoInBPM))
                            .asInstanceOf[SpinnerValueFactory[ℝ]])
    m_tempo.valueProperty.addListener(onTempoSpinChange _)

    onMeterChange(Meter(4,4))
    updateButtons()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TEMPO ⇒ defer(onTempoChange(message.bpm))
    case METER ⇒ defer(onMeterChange(message.meter))
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
    val fac = m_seq.getTempoFactor

    m_tempo.getValueFactory.setValue(bpm * fac)
  }

  def onTempoSpinChange(ov: ObservableValue[_<: ℝ],was: ℝ,now: ℝ): Unit =
  {
    val was = m_seq.getTempoInBPM
    val fac = now / was

    m_seq.setTempoFactor(fac.toFloat)
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

  def onPrevious (e: MouseEvent): Unit =
  {
    m_seq.setTickPosition(m_seq.getLoopStartPoint)
    updateClock()
  }

  def onNext(e: MouseEvent): Unit =
  {
    m_seq.setTickPosition(m_seq.getLoopEndPoint)
    updateClock()
  }

  def onTempoTap(): Unit =
  {
    println("onTempoTap")
  }

  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onForward  (e: MouseEvent): Unit = println("advance")
  def isLooping: Bool = m_seq.getLoopCount != 0
  def isPlaying: Bool = m_seq.isRunning

  def updateClock(): Unit =
  {
    assert(m_seq.getSequence.getDivisionType == 0) // PPQ

    val tpb   = m_seq.getSequence.getResolution
    val tick  = m_seq.getTickPosition
    val beat  = tick / tpb
    val bars  = 1 + beat / m_mtr.count
    val beats = 1 + beat % m_mtr.count
    val parts = 1 + Math.floor(((tick % tpb).toFloat / tpb) * 4).toInt % 4

    m_bars.setText(f"$bars%04d.$beats%02d.$parts%02d")
  }

  def updateButtons(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def newTimer(): Timeline =
  {
    val t = new Timeline(new KeyFrame(millis(100),(a:ActionEvent) ⇒ updateClock()))
    t.setCycleCount(-1)
    t
  }

  val STOPPED = getPseudoClass("stopped")
  val PLAYING = getPseudoClass("playing")
  val LOOPING = getPseudoClass("looping")
}

//****************************************************************************
