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
import com.wolery.owl.gui.load
import com.wolery.owl.stringed.StringedInstrument
import com.wolery.owl.utils.Logging

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javax.swing.event.MenuEvent
import javafx.event.ActionEvent

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
}

//****************************************************************************

class MainView extends PrimaryStage
{
  val instrument = StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))

  val (_,c) = instrument.view ("Guitar")
  val (m,_) = load[BorderPane]("MainView",new MainController(c))

  m.setCenter(c.view)

  resizable = false
  title     = "Owl"
  scene     = new Scene(m)
}

//****************************************************************************
