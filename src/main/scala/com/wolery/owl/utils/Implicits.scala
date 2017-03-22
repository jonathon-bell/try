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

package com.wolery.owl.utils

//****************************************************************************

import javafx.event.EventHandler
import scala.language.implicitConversions

//****************************************************************************

object implicits
{
  implicit
  def asEventHandler[E <: javafx.event.Event](lambda: E ⇒ Unit): EventHandler[E] =
  {
    new EventHandler[E] {def handle(e: E) = lambda(e)}
  }

  implicit
  def asRunnable(lambda: ⇒ Unit): Runnable =
  {
    new Runnable {def run() = lambda}
  }
}

//****************************************************************************
