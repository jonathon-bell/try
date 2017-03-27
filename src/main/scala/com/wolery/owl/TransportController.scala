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

    onStateChange()
  }

  def meta(message: MetaMessage): Unit = message.getType match
  {
    case TEMPO ⇒ defer(onTempoChange(message.tempo))
    case _     ⇒
  }

  def onTempoChange(bpm: ℝ) =
  {
    tempo.setText(f"$bpm%2.2f")
  }

  def onReset(e: MouseEvent): Unit =
  {
    owl.sequencer.setTickPosition(0)
  }

  def onStateChange(): Unit =
  {
    stop.pseudoClassStateChanged(STOPPED,!isPlaying)
    play.pseudoClassStateChanged(PLAYING, isPlaying)
    loop.pseudoClassStateChanged(LOOPING, isLooping)
  }

  def onStop(e: MouseEvent): Unit =
  {
    owl.sequencer.stop()
    onStateChange()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    owl.sequencer.start()
    onStateChange()
  }

  def onLoop(e: MouseEvent): Unit =
  {
    owl.sequencer.setLoopCount(if (isLooping) 0 else -1)
    onStateChange()
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
