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

package com.wolery.owl.scraps
import javax.sound.midi._
import com.wolery.owl.utils.Logging

object play_midi extends Logging
{
  def main(args: Array[String]): Unit =
  {
    log.info("enter")

    val s : Sequence = load

    play(s)

    log.info("exit")
  }

  def play(sequence: Sequence): Unit =
  {
    val sequencer = MidiSystem.getSequencer()

    sequencer.open()
    sequencer.setTempoInBPM(120)
    sequencer.setSequence(sequence)
    sequencer.start()

    while (sequencer.isRunning)
    {
      Thread.sleep(500)
    }

    sequencer.close()
  }

  def load: Sequence =
  {
    MidiSystem.getSequence(getClass.getResource("/sample.mid"))
  }

  def note(channel:  Int,
           note:     Int,
           tick:     Long,
           velocity: Int = 80) =
  {
    new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,channel,note,velocity),tick)
  }

  def programChange(channel:Int,patch: Int) =
  {
    new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,channel,patch,0),0)
  }

  def addMelodyTrack(sequence: Sequence) =
  {
    val t = sequence.createTrack()

    t.add(programChange(0,73))

    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,69,127),0))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF,0,69,127),2))

    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,68,80), 1))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,67,80), 2))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,66,80), 3))

    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,65,127),4))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,64,80), 5))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,63,80), 6))
    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0,62,80), 7))

    t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,2,61,127),8))
/*
    addNote(t,69,0,1)
    addNote(t,68,1,1)
    addNote(t,67,2,1)
    addNote(t,66,3,1)
    addNote(t,65,4,1)
    addNote(t,64,5,1)
    addNote(t,63,6,1)
    addNote(t,62,7,1)
    addNote(t,61,8,1)
*/
    sequence
  }

  def addClickTrack(sequence: Sequence) =
  {
    val t = sequence.createTrack()

    addBeat(t,60,0,127)
    addBeat(t,60,1,80)
    addBeat(t,60,2,80)
    addBeat(t,60,3,80)
    addBeat(t,60,4,127)
    addBeat(t,60,5,80)
    addBeat(t,60,6,80)
    addBeat(t,60,7,80)
    addBeat(t,60,8,127)

    sequence
  }

  def create:Sequence =
  {
    val sequence  = new Sequence(Sequence.PPQ,1,1);
    addMelodyTrack(sequence)
    addClickTrack (sequence)
  }

  def addBeat(track:    Track,
              note:     Int,
              tick:     Long,
              velocity: Byte = 80): Track =
  {
    track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,9,note,velocity),tick))
    track
  }

  def file: Sequence =
  {
    MidiSystem.getSequence(new java.io.File("canyon.mid"));
  }
}

//****************************************************************************
