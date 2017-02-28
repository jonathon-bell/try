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

import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}

//****************************************************************************

class FretboardController extends Logging
{
  @FXML var frets:   Pane = _
  @FXML var strings: Pane = _
  @FXML var markers: Pane = _
  @FXML var grid:    GridPane = _

  def initialize =
  {
    log.info("initialize")

    val n = grid.getChildren.get(1)

    GridPane.setConstraints(n,14,3)
  }
}

//****************************************************************************
