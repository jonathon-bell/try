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
//*  see also: jsresources.org CreateSequence.java
//*
//****************************************************************************

package com.wolery.zed

//****************************************************************************

import java.io.{ File, IOException }
import javax.sound.midi.{ InvalidMidiDataException, MidiEvent, MidiSystem, Sequence, ShortMessage, Track }

//****************************************************************************

object CreateSequence
{
  type Tick = Long

  val VELOCITY = 64

  def createType0(): Sequence =
  {
    val sequence: Sequence = new Sequence(Sequence.PPQ,1);
    val track   : Track    = sequence.createTrack();

    // first chord: C major
    track.add(createNoteOnEvent (60, 0));
    track.add(createNoteOnEvent (64, 0));
    track.add(createNoteOnEvent (67, 0));
    track.add(createNoteOnEvent (72, 0));
    track.add(createNoteOffEvent(60, 1));
    track.add(createNoteOffEvent(64, 1));
    track.add(createNoteOffEvent(67, 1));
    track.add(createNoteOffEvent(72, 1));

    // second chord: f minor N
    track.add(createNoteOnEvent (53, 1));
    track.add(createNoteOnEvent (65, 1));
    track.add(createNoteOnEvent (68, 1));
    track.add(createNoteOnEvent (73, 1));
    track.add(createNoteOffEvent(63, 2));
    track.add(createNoteOffEvent(65, 2));
    track.add(createNoteOffEvent(68, 2));
    track.add(createNoteOffEvent(73, 2));

    // third chord: C major 6-4
    track.add(createNoteOnEvent (55, 2));
    track.add(createNoteOnEvent (64, 2));
    track.add(createNoteOnEvent (67, 2));
    track.add(createNoteOnEvent (72, 2));
    track.add(createNoteOffEvent(64, 3));
    track.add(createNoteOffEvent(72, 3));

    // forth chord: G major 7
    track.add(createNoteOnEvent (65, 3));
    track.add(createNoteOnEvent (71, 3));
    track.add(createNoteOffEvent(55, 4));
    track.add(createNoteOffEvent(65, 4));
    track.add(createNoteOffEvent(67, 4));
    track.add(createNoteOffEvent(71, 4));

    // fifth chord: C major
    track.add(createNoteOnEvent (48, 4));
    track.add(createNoteOnEvent (64, 4));
    track.add(createNoteOnEvent (67, 4));
    track.add(createNoteOnEvent (72, 4));
    track.add(createNoteOffEvent(48, 8));
    track.add(createNoteOffEvent(64, 8));
    track.add(createNoteOffEvent(67, 8));
    track.add(createNoteOffEvent(72, 8));

    sequence
  }

  def saveToFile(sequence:Sequence,path: String) =
  {
    MidiSystem.write(sequence,0,new File(path));
  }

  def main(args: Array[String]): Unit =
  {
    if (args.length != 1)
    {
      printUsageAndExit();
    }

    val sequence: Sequence = createType0()

    saveToFile(sequence,args(0))
  }

  def createNoteOnEvent(nKey:Int,tick: Tick): MidiEvent =
  {
    createNoteEvent(ShortMessage.NOTE_ON,nKey,VELOCITY,tick);
  }

  def createNoteOffEvent(nKey:Int,tick: Tick): MidiEvent =
  {
    createNoteEvent(ShortMessage.NOTE_OFF,nKey,0,tick);
  }

  def createNoteEvent(nCommand:  Int,
                      nKey:      Int,
                      nVelocity: Int,
                      tick:     Tick): MidiEvent =
  {
    val m = new ShortMessage()

    m.setMessage(nCommand,
                 0,  // always on channel 1
                 nKey,
                 nVelocity);
    new MidiEvent(m,tick);
  }

  def printUsageAndExit() =
  {
    println("usage:");
    println("java CreateSequence <midifile>");
    System.exit(1);
  }
}

//****************************************************************************
/*

  message
    short
    meta
    sysex


  message
    channel

*
* */
//****************************************************************************
