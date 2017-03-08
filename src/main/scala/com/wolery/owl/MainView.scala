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
import com.wolery.owl.keyboard.KeyboardInstrument
import com.wolery.owl.utils.Logging

import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.control.MenuBar
import javafx.scene.layout.BorderPane
import scalafx.Includes.jfxBorderPane2sfx
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

//****************************************************************************

class MainController extends Logging
{
  @fx var menubar: MenuBar = _

  def initialize() =
  {
    log.info("initialize")

    menubar.setUseSystemMenuBar(true)

    val tk = de.codecentric.centerdevice.MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }
}

//****************************************************************************

class MainView extends PrimaryStage
{
  val instrument:Instrument = StringedInstrument(24,E(2),A(2),D(3),G(3),B(3),E(4))

  val (m,_) = load[BorderPane,MainController]("MainView")
  val (n,c) = instrument.view                ("Fretboard-1")

  m.setCenter(n)

  resizable = false
  title     = "Owl"
  scene     = new Scene(m)

  val scale = Scale(F,"whole tone").get
  val all   = instrument.pitches

  c.update(0,Seq(all))
  c.update(1,Seq(all.filter(p => scale.contains(p.note))))
 }

//****************************************************************************
