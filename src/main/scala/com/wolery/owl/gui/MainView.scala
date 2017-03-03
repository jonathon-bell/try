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

package com.wolery.owl.gui

import com.wolery.owl.utils.Logging

import de.codecentric.centerdevice.MenuToolkit
import javafx.fxml.{FXML ⇒ fx}
import javafx.scene.control.MenuBar
import javafx.scene.layout.{BorderPane,Pane}
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

    val tk = MenuToolkit.toolkit()

    tk.setApplicationMenu(tk.createDefaultApplicationMenu("Owl"))
  }
}

//****************************************************************************

class MainView extends PrimaryStage
{
  val p = Load[BorderPane]("MainView")
  val f = Load[Pane]      ("Fretboard-1")

  p.setCenter(f)

  resizable = false
  title     = "Owl"
  scene     = new Scene(p)
}

//****************************************************************************
