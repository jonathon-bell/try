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

package com.wolery.owl.core

//****************************************************************************

case class Meter(beats: ℕ,pulse: ℕ)
{
  assert(beats>0 && pulse>0)

  override
  def toString(): String = s"$beats / $pulse"
}

//****************************************************************************
