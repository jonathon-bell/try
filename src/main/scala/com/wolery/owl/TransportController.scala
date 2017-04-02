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
import com.wolery.owl.core.utilities._
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
import javafx.util.StringConverter;

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
  @fx var m_tempo: Spinner[ℝℝ] = _
  @fx var m_meter: Label = _
  @fx var m_scale: Label = _

      val m_seq  : Sequencer = owl.sequencer
      val m_tmr  : Timeline  = newTimer()
      var m_mtr  : Meter     = _

  type ℝℝ    = java.lang.Double

  def initialize(): Unit =
  {
    val f = (new DoubleSpinnerValueFactory(2,208,getEffectiveTempoInBPM))
    val c = f.getConverter
    f.setConverter(new StringConverter[ℝℝ]
    {
      def toString  (r: ℝℝ): String = f"$r%.2f"
      def fromString(s: String): ℝℝ = c.fromString(s)
    })

    m_tempo.setValueFactory(f)
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

//    m_tempo.getValueFactory.setValue(bpm * fac)
  }

  def onTempoSpinChange(ov: ObservableValue[_<: ℝℝ],was: ℝℝ,now: ℝℝ): Unit =
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

  var m_tap: Tick = 0

  def onTapTempo(me: MouseEvent): Unit =
  {
    if (isPlaying)
    {
      val t = m_seq.getTickPosition
      val d = Math.max(t - m_tap, 0)
      val s = if (me.isShiftDown)   2 else 1
      val c = if (me.isControlDown) 4 else 1
      val e = getEffectiveTempoInTPM.toFloat / (d *  s * c)

      if (between(e,2,208))
      {
    val was = m_seq.getTempoInBPM
    val fac = e / was

    m_seq.setTempoFactor(fac.toFloat)
    m_tempo.getValueFactory.setValue(e)
      }

      m_tap = t
    }
  }

  def getEffectiveTempoInBPM: ℝ = m_seq.getTempoInBPM * m_seq.getTempoFactor
  def getEffectiveTempoInTPM: ℝ = m_seq.getTempoInBPM * m_seq.getTempoFactor * m_seq.getSequence.getResolution

  ////

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
/*
class TempoSpinnerValueFactory(m_min: Double = 1,m_max: Double = 208,m_val: Double = 120,m_step: Double=1) extends SpinnerValueFactory[Double]
{
  setMin(m_min)
  this.setMax(m_max)
  this.setAmountToStepBy(m_step)
  this.setConverter(new StringConverter[Double]
  {
      def toString(value:Double): String = f"$value%.2f"

      def fromString(value:String):Double
      {
          try
          {
              // If the specified value is null or zero-length, return null
              if (value == null) {
                  return null;
              }

              value = value.trim();

              if (value.length() < 1)
              {
                  return null;
              }

              // Perform the requested parsing
              return df.parse(value).doubleValue();
          }
          catchsex)
          {
              throw new RuntimeException(ex);
          }
      }
  });

  valueProperty().addListener((o, oldValue, newValue) -> {

      if (newValue < getMin()) {
          setValue(getMin());
      } else if (newValue > getMax()) {
          setValue(getMax());
      }
  });

  setValue(initialValue >= min && initialValue <= max ? initialValue : min);
}
*/