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
  @fx var rew : Label = _
  @fx var fwrd: Label = _
  @fx var next: Label = _
  @fx var begn: Label = _
  @fx var stop: Label = _
  @fx var play: Label = _
  @fx var loop: Label = _

  def initialize=
  {
    prev.setText("\uf048")
    rew .setText("\uf04a")
    fwrd.setText("\uf04e")
    next.setText("\uf051")
    begn.setText("\uf049")
    stop.setText("\uf04d")
    play.setText("\uf04b")
    loop.setText("\uf01e")
  }

  def send(mm: MidiMessage,ts: Long): Unit = {println(".")}
  def close() = {}

  def onPrevious(e: MouseEvent): Unit = println("previous")
  def onRewind(e: MouseEvent): Unit = println("rewind")
  def onForward(e: MouseEvent): Unit = println("forward")
  def onNext(e: MouseEvent): Unit = println("next")
  def onBegin(e: MouseEvent): Unit = println("begin")
  def onLoop(e: MouseEvent): Unit = println("loop")

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
