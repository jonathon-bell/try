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

import Math.max

import com.wolery.owl.core._
import com.wolery.owl.core.utilities._
import com.wolery.owl.midi.messages._
import com.wolery.owl.utils.implicits._

import javafx.animation.{ KeyFrame, Timeline }
import javafx.application.Platform.{ runLater ⇒ defer }
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass.getPseudoClass
import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.{ Label, Spinner, SpinnerValueFactory }
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory
import javafx.scene.input.MouseEvent
import javafx.util.Duration.millis
import javafx.util.StringConverter
import javax.sound.midi.{ MetaEventListener, MetaMessage, Sequencer }
import sequencer._

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
    initTempoSpinner()
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
    val bpm = m_seq.getEffectiveTempoInBPM

    m_tempo.getValueFactory.setValue(bpm)
  }

  def onTempoSpinChange(was: ℝ,now: ℝ): Unit =
  {
    m_seq.setEffectiveTempoInBPM(now)
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
    m_seq.setLoopCount(if (m_seq.isLooping) 0 else -1)
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

  var m_tap: Tick = 0

  def onTapTempo(me: MouseEvent): Unit =
  {
    if (m_seq.isPlaying)
    {
      val t = m_seq.getTickPosition
      val d = max(t - m_tap,1) * (if (me.isShiftDown) 2 else 1) * (if (me.isShiftDown)   2 else 1)
      val e = m_seq.getEffectiveTempoInBPM * m_seq.getTicksPerBeat / d

      if (isBetween(e,MIN_TEMPO,MAX_TEMPO))
      {
        m_seq.setEffectiveTempoInBPM(e)

        m_tempo.getValueFactory.setValue(e)
      }

      m_tap = t
    }
  }

  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onForward  (e: MouseEvent): Unit = println("advance")

  def updateClock(): Unit =
  {
    assert(m_seq.getSequence.getDivisionType == 0) // PPQ

    val tpb   = m_seq.getSequence.getResolution
    val tick  = m_seq.getTickPosition
    val beat  = tick / tpb
    val bars  = 1 + beat / m_mtr.beats
    val beats = 1 + beat % m_mtr.beats
    val parts = 1 + Math.floor(((tick % tpb).toFloat / tpb) * 4).toInt % 4

    m_bars.setText(f"$bars%04d.$beats%02d.$parts%02d")
  }

  def updateButtons(): Unit =
  {
    m_stop.pseudoClassStateChanged(STOPPED,!m_seq.isPlaying)
    m_play.pseudoClassStateChanged(PLAYING, m_seq.isPlaying)
    m_loop.pseudoClassStateChanged(LOOPING, m_seq.isLooping)
  }

  def newTimer(): Timeline =
  {
    val t = new Timeline(new KeyFrame(millis(100),(a:ActionEvent) ⇒ updateClock()))
    t.setCycleCount(-1)
    t
  }

  def initTempoSpinner() =
  {
    val f = new DoubleSpinnerValueFactory(MIN_TEMPO,MAX_TEMPO,m_seq.getEffectiveTempoInBPM)
    {
      val c = getConverter

      setConverter(new StringConverter[java.lang.Double]
      {
        def toString  (r: java.lang.Double): String = f"$r%.2f"
        def fromString(s: String): java.lang.Double = c.fromString(s)
      })
    }

    m_tempo.setValueFactory(f.asInstanceOf[SpinnerValueFactory[ℝ]])
    m_tempo.valueProperty.addListener(onTempoSpinChange _)
  }

  val MIN_TEMPO =   1.0F
  val MAX_TEMPO = 300.0F
  val STOPPED   = getPseudoClass("stopped")
  val PLAYING   = getPseudoClass("playing")
  val LOOPING   = getPseudoClass("looping")
}

//****************************************************************************
