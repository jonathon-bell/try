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

package com.wolery.owl.instrument.stringed

import com.wolery.owl.core.Bool
import com.wolery.owl.core.ℕ
import com.wolery.owl.utils.Logging

import javafx.fxml.FXML
import javafx.scene.{layout ⇒ jfxl}

//****************************************************************************

case class FretboardDescription
(
  strings: ℕ       = 6,
  frets  : ℕ       = 24,
  dots   : Seq[ℕ]  = Seq(3,5,7,9,12,15,17,19,21,24),
  left   : Bool    = false,
  zero   : Bool    = true
)

class View() extends Logging
{
  @FXML var frets:      jfxl.Pane = _
  @FXML var strings:    jfxl.Pane = _
  @FXML var markers:    jfxl.Pane = _
  @FXML var grid:       jfxl.GridPane = _

  def initialize =
  {
    log.info("Initializing com.wolery.owl.instrument.stringed.View")

    val n = grid.getChildren.get(1)
    jfxl.GridPane.setConstraints(n,14,3)
    log.info(n.toString)
  }
}

//****************************************************************************
