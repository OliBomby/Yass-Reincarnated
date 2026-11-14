/*
 * Yass - Karaoke Editor
 * Copyright (C) 2009 Saruta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package yass;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description of the Class
 *
 * @author Saruta
 */
public class YassSynth {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /**
     * Description of the Field
     */
    public final static int SINE = 0;
    /**
     * Description of the Field
     */
    public final static int RECT = 1;
    private static int channels = 1;
    private static int bytesPerSample = 1;
    private static int sampleRate = 44100;// full spectrum to 127 would require 44100
    private static byte[][] data;
    private static AudioFormat audioFormat = null;
    private static AudioFormat wavaudioFormat = null;
    private static SourceDataLine sourceDataLine = null;
    private static SourceDataLine wavsourceDataLine = null;
    private static byte[] buffer = null;
    private static int BUFFER_SIZE = 512000;
    /**
     * Description of the Method
     */
    private static byte[] tempBuff = new byte[BUFFER_SIZE];

    // generate signed 16-bit data

    /**
     * Description of the Method
     *
     * @param args Description of the Parameter
     */
    public static void main(String args[]) {
        // opening more than 8 clips in parallel fails under Linux/ALSA
        // new approach:
        // open one or more SourceDataLine and write directly, in extra thread
        //
        //Clip clips[] = new Clip[128];

        loadWav();

        long t;

        long s = System.nanoTime() / 1000000L;

        data = new byte[128][];
        for (int i = 0; i < 128; i++) {
            data[i] = create(i, 15, SINE);
            //audioInputStream[i] = cash(data[i], null);
        }
        t = System.nanoTime() / 1000000L - s;
        LOGGER.info("Sound creation took " + t + "ms.");

        openLine();
        openWavLine();
        s = System.nanoTime() / 1000000L;
        for (int i = 60; i < 128; i++) {
            t = System.nanoTime() / 1000000L - s;
            LOGGER.info("n=" + i + " " + getFrequency(i));

            //clips[i].setMicrosecondPosition(0);
            //clips[i].start();

            play(data[i]);
            //playWav();

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
            }
        }
        closeLine();
        closeWavLine();
    }

    /**
     * Description of the Method
     *
     * @param note Description of the Parameter
     * @param ms   Description of the Parameter
     * @param type Description of the Parameter
     * @return Description of the Return Value
     */
    public static byte[] create(int note, int ms, int type) {
        double f = 440 * Math.pow(2, (note - 69) / 12.0);
        byte[] b = new byte[(int) (sampleRate * ms / 1000.0)];
        for (int i = 0; i < b.length; i++) {
            double t = i / (double) sampleRate;
            double sinValue = Math.sin(2 * Math.PI * f * t);
            if (type != SINE) {
                if (type == RECT) {
                    sinValue = sinValue > 0 ? 1 : -1;
                }
            }
            float fac = (b.length - i) / (float) b.length;
            b[i] = (byte) Math.min(127, (sinValue * 127.0 * fac * fac));
        }
        return b;
    }

    private static double getFrequency(int midi) {
        return 440 * Math.pow(2, (midi - 69) / 12.0);
    }

    /**
     * Description of the Method
     *
     * @param ms Description of the Parameter
     * @return Description of the Return Value
     */
    public static byte[] createRect(double ms) {
        byte data[] = new byte[(int) (sampleRate * bytesPerSample * ms / 1000)];
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        int sampleLength = data.length / bytesPerSample;
        for (int i = 0; i < sampleLength; i++) {
            double value;
            if (i % 8 < 4) {
                value = 1;
            } else {
                value = -1;
            }
            shortBuffer.put((short) (16000 * value));
        }
        return data;
    }

    /**
     * Description of the Method
     */
    public static void openLine() {
        try {
            if (audioFormat == null) {
                audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
            }
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

    }

    /**
     * Description of the Method
     */
    public static void openWavLine() {
        try {
            if (audioFormat == null) {
                audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
            }
            if (wavaudioFormat == null) {
                wavaudioFormat = new AudioFormat(sampleRate, 16, 2, true, false);
            }
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            wavsourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            wavsourceDataLine.open(wavaudioFormat);
            wavsourceDataLine.start();
        } catch (Exception e) {
            System.err.println("YassSynth: Cannot open line");
            //LOGGER.log(Level.INFO, e.getMessage(), e);
        }

    }


    /**
     *  Description of the Method
     *
     * @param  dat  Description of the Parameter
     */

    /**
     * Description of the Method
     */
    public static synchronized void loadWav() {

        if (audioFormat == null) {
            audioFormat = new AudioFormat(sampleRate, bytesPerSample * 8, channels, true, true);
        }
        if (wavaudioFormat == null) {
            // Preserve the original click's stereo channels in the editor mixer's byte order.
            wavaudioFormat = new AudioFormat(sampleRate, 16, 2, true, false);
        }

        try (InputStream istream = YassSynth.class.getResourceAsStream("/yass/resources/samples/click.wav");
             AudioInputStream ostream = AudioSystem.getAudioInputStream(new BufferedInputStream(istream));
             AudioInputStream stream = AudioSystem.getAudioInputStream(wavaudioFormat, ostream)) {
            buffer = stream.readAllBytes();
            LOGGER.info("Loaded click noise");
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
    }

    /**
     * Description of the Method
     *
     * @param dat Description of the Parameter
     */
    public static void play(byte dat[]) {
        try {
            if (sourceDataLine != null) sourceDataLine.write(dat, 0, dat.length);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

    }

    /**
     * Description of the Method
     */
    public static void closeLine() {
        try {
            if (sourceDataLine!= null) sourceDataLine.close();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
    }

    /**
     * Description of the Method
     */
    public static void closeWavLine() {
        try {
            if (wavsourceDataLine != null) wavsourceDataLine.close();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
    }

    /**
     * Description of the Method
     *
     * @param targetFormat Description of the Parameter
     * @param audioData    Description of the Parameter
     * @return Description of the Return Value
     */
    public static AudioInputStream cash(byte audioData[], AudioFormat targetFormat) {
		/*
		 *  try {
		 *  InputStream bis = new ByteArrayInputStream(data);
		 *  AudioFormat af = new AudioFormat((float) sampleRate, bytesPerSample * 8, channels, true, true);
		 *  AudioInputStream din = new AudioInputStream(bis, af, data.length / af.getFrameSize());
		 *  if (targetFormat != null) {
		 *  din = AudioSystem.getAudioInputStream(targetFormat, din);
		 *  }
		 *  DataLine.Info info = new DataLine.Info(Clip.class, af, ((int) din.getFrameLength() * af.getFrameSize()));
		 *  Clip clip = (Clip) AudioSystem.getLine(info);
		 *  try {
		 *  clip.open(din);
		 *  }
		 *  catch (Exception e) {
		 *  LOGGER.log(Level.INFO, e.getMessage(), e);
		 *  return null;
		 *  }
		 *  return clip;
		 *  }
		 *  catch (Exception e) {
		 *  LOGGER.log(Level.INFO, e.getMessage(), e);
		 *  }
		 *  return null;
		 */
        try {
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            return new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the click as signed 16-bit little-endian stereo PCM at 44.1 kHz.
     */
    public static synchronized byte[] getWavSampleAsByteArray() {
        if (buffer == null || buffer.length == 0) {
            loadWav();
        }
        if (buffer == null || buffer.length == 0) {
            return new byte[0]; // Fallback: leeres Array
        }
        return buffer.clone();
    }
}
