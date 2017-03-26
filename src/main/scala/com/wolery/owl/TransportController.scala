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

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javax.sound.midi.Receiver
import javax.sound.midi.MidiMessage

//****************************************************************************

class TransportController extends Receiver
{
  @fx var prev: Label = _
  @fx var next: Label = _
  @fx var loop: Label = _
  @fx var rset: Label = _
  @fx var rwnd: Label = _
  @fx var advn: Label = _
  @fx var stop: Label = _
  @fx var play: Label = _

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
  }

  def send(mm: MidiMessage,ts: Long): Unit = {println(".")}
  def close() = {}

  def onPrevious (e: MouseEvent): Unit = println("previous")
  def onNext     (e: MouseEvent): Unit = println("next")
  def onLoop     (e: MouseEvent): Unit = println("loop")
  def onRewind   (e: MouseEvent): Unit = println("rewind")
  def onAdvance  (e: MouseEvent): Unit = println("advance")

  def onReset(e: MouseEvent): Unit =
  {
    owl.sequencer.setTickPosition(0)
  }

  def onStop(e: MouseEvent): Unit =
  {
    stop.setStyle("-fx-text-fill:blue;")
    play.setStyle("-fx-text-fill:grey;")
    owl.sequencer.stop()
  }

  def onPlay(e: MouseEvent): Unit =
  {
    stop.setStyle("-fx-text-fill:grey;")
    play.setStyle("-fx-text-fill:green;")
    owl.sequencer.start()
  }

}

//****************************************************************************
