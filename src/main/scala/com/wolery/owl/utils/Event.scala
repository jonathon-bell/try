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

//****************************************************************************

object event
{
  implicit
  def foo[E <: javafx.event.Event](lambda: E ⇒ Unit): EventHandler[E] =
  {
    new EventHandler[E] {def handle(e: E) = lambda(e)}
  }
}

//****************************************************************************
