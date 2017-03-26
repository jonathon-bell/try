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
import com.wolery.owl.utils._

import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import javafx.stage.{ Stage, StageStyle }
import javax.sound.midi.{ MetaEventListener, MetaMessage }

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

    setup()
  }

  def onCIonian(ae: ActionEvent)    = {}
  def onCWholeTone(ae: ActionEvent) = {}
  def onClose()                     = {}
  def onPlay()                      = {}

  def setup(): Unit =
  {
    owl.sequencer.getTransmitter.setReceiver(controller)
    owl.sequencer.addMetaEventListener(new MetaEventListener{def meta(m:MetaMessage):Unit = controller.send(m,-1)})
    controller.send(message.harmony(Scale(F,"whole tone").get))
    owl.sequencer.setSequence(load.sequence("sample"))
  }
}

//****************************************************************************

object MainView
{
  def apply(): Unit =
  {
    val instrument = stringed.StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))

    val (_,c) = instrument.view("Guitar")
    val (m,_) = load.view("MainView"     ,new MainController(c))
    val (t,_) = load.view("TransportView",new TransportController)

   // m.asInstanceOf[javafx.scene.layout.VBox].setSpacing(20)
    m.getChildren.addAll(c.view,t)

    new Stage(StageStyle.DECORATED)
    {
      setResizable(false)
      setTitle    ("Owl")
      setScene    (new Scene(m))
      show        ()
    }
  }
}

//****************************************************************************
