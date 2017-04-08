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

import utilities.isPowerOf2

//****************************************************************************

case class Meter(meter: ℕ = 4,beat: ℕ = 4,clocks: ℕ = 24,n32s: ℕ = 8)
{
  assert(meter>0 && isPowerOf2(beat) && clocks>0 && n32s>0)

  override
  def toString(): String = s"$meter / $beat"
}

//****************************************************************************
