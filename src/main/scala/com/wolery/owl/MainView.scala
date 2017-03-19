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

import com.wolery.owl.core._
import com.wolery.owl.utils.load
import com.wolery.owl.stringed.StringedInstrument
import com.wolery.owl.utils.Logging

import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javax.sound.midi.MetaEventListener
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiSystem

//****************************************************************************

class MainController(controller: Controller) extends Logging
{
  @fx
  var menubar   : MenuBar    = _
  val instrument: Instrument = controller.instrument
  val playable  : Pitches    = instrument.playable

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)

    val tk = de.codecentric.centerdevice.MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))

    controller.update(0,Seq(playable))
  }

  def onCIonian(ae: ActionEvent) =
  {
    val s = Scale(C,"ionian").get

    controller.update(1,Seq(playable.filter(p ⇒ s.contains(p.note))))
  }

  def onCWholeTone(ae: ActionEvent) =
  {
    val s = Scale(C,"whole tone").get

    controller.update(1,Seq(playable.filter(p ⇒ s.contains(p.note))))
  }

  def onPlay() =
  {
    owl.sequencer.setSequence(load.sequence("sample"))
    owl.sequencer.start()
  }

  def onClose() =
  {
    owl.sequencer.stop()
  }
}

//****************************************************************************
