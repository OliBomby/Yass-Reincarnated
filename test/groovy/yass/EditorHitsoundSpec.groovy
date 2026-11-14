package yass

import spock.lang.Specification

import javax.sound.sampled.AudioSystem

class EditorHitsoundSpec extends Specification {
    def "click loader preserves the sample frames and byte order"() {
        given:
        def stream = AudioSystem.getAudioInputStream(
                getClass().getResource('/yass/resources/samples/click.wav'))
        byte[] expected
        try {
            assert stream.format.channels == 2
            assert stream.format.sampleSizeInBits == 16
            assert stream.format.sampleRate == 44100
            assert !stream.format.bigEndian
            expected = stream.readAllBytes()
        } finally {
            stream.close()
        }

        when:
        YassSynth.loadWav()
        byte[] actual = YassSynth.getWavSampleAsByteArray()

        then:
        actual == expected
    }

    def "editor preserves the original stereo clicks at note starts"() {
        given:
        def properties = new YassProperties() {
            @Override
            void load() {
                // Keep this audio-rendering test independent of devices and user settings.
            }
        }
        properties.setProperty('use-sample', 'true')
        def player = new YassPlayer(null, properties)
        player.initNoteMap()
        byte[] click = YassSynth.getWavSampleAsByteArray()
        def clicks = [new Click(1_100_000L, 0, 1_400_000L),
                      new Click(1_500_000L, 7, 2_000_000L)] as Click[]

        when:
        byte[] audio = player.createAudioStreamFromClicks(clicks, rate, true, false, 1_000_000L)

        then:
        byte[] expected = new byte[audio.length]
        for (def note : clicks) {
            int start = (int) (((note.start() - 1_000_000L) / 1_000_000d) / rate * 44100)
            System.arraycopy(click, 0, expected, start * 4, click.length)
        }
        audio == expected

        when:
        byte[] muted = player.createAudioStreamFromClicks(clicks, rate, false, false, 1_000_000L)

        then:
        muted.every { it == 0 }

        where:
        rate << [1d, 0.5d]
    }
}
