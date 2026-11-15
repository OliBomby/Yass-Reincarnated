<meta name="description" content="Yass Reincarnated is a free, open-source Karaoke editor for Ultrastar songs. Finetune notes, edit lyrics, and manage your song library. Available for Windows, macOS, and Linux.">
<meta name="keywords" content="Yass, Yass Reincarnated, Karaoke, Karaoke Editor, Ultrastar, Ultrastar Deluxe, Song Editor, Lyrics Editor, Open Source, Java, Windows, macOS, Linux">

# Yass Reincarnated – Open-Source Karaoke Editor for UltraStar Songs
Documentation: [https://olibomby.github.io/Yass-Reincarnated/](https://olibomby.github.io/Yass-Reincarnated/)

## Tutorial Videos

These upstream Yass Reloaded tutorials also cover the inherited editor workflows:

- English: [Yass Reloaded Tutorial](https://youtu.be/7sSvb6YL70Y)
- German: [Yass Reloaded Tutorial (Deutsch)](https://youtu.be/IB4ik6cxEUY)

## What is Yass Reincarnated?

Yass Reincarnated is a free, open-source karaoke editor for creating, editing, and maintaining UltraStar and UltraStar Deluxe song files.
It allows karaoke creators to precisely edit notes, timing, and lyrics, manage large song libraries, and fix common errors in existing karaoke tracks.

The application is written in Java and runs on Windows, macOS, and Linux.  
It is actively developed and based on modern Java (Java 21).

Yass Reincarnated provides a graphical user interface for fine-tuning UltraStar karaoke songs.
You can drag and drop notes, adjust syllables, correct timing issues, and quickly detect common formatting errors.

In addition, Yass Reincarnated offers powerful tools for managing karaoke libraries, including filtering, mass-tagging, and batch correction of song metadata.

## Who is this for?

Yass Reincarnated is designed for:
- UltraStar karaoke song creators
- Karaoke enthusiasts who want to edit lyrics and timing precisely
- Users maintaining large UltraStar song libraries
- Advanced users who need batch editing and error detection

## Features of the UltraStar Karaoke Editor

*   **Graphical Note and Lyric Editing:** Easily drag and drop notes, adjust timing, and edit lyrics with a user-friendly interface.
*   **Advanced Song Management:** Filter your song library, batch-edit tags, and organize your collection efficiently.
*   **Error Detection:** Automatically finds common errors in your karaoke files to ensure they play perfectly.
*   **Wide Audio Format Support:** Thanks to FFmpeg integration, Yass Reincarnated supports a vast range of audio formats, including AAC, OGG, and OPUS.
*   **Improved User Experience:** Numerous tweaks and improvements make the editing process faster and more intuitive.
*   **Modern and Actively Developed:** Built on Java 21 and continuously updated with new features and bug fixes.

## Supported Languages

Yass Reincarnated has been localized to:
- English
- German
- French
- Spanish
- Polish
- Hungarian

## Project Background
Yass Reincarnated is a fork of [Yass Reloaded](https://github.com/DoubleDee73/Yass-Reloaded)
by DoubleDee. Yass Reloaded is based on Yass 2.4.3, originally developed by Saruta
since 2009, and modernized the codebase to Java 21. This fork continues that work
under the Yass Reincarnated name.

## Differences Compared to the Yass Reloaded
- Improved user experience focused on real-world karaoke song creation
- Fixed bugs so editing lyrics is no longer a pain

## Downloads

Prebuilt binaries for Windows, macOS, and Linux are available in the GitHub
[Release Section](https://github.com/OliBomby/Yass-Reincarnated/releases)

### Windows

**Recommended:**  
Download and run the Windows installer:

- `yass-reincarnated-installer.exe`

Java is bundled. No manual setup required.

### macOS

Download the macOS build matching your system:

- Apple Silicon (M1/M2/M3): `yass-reincarnated-macos-arm64.dmg`
- Intel Macs: `yass-reincarnated-macos-x64.dmg`

Java is bundled.

If macOS blocks the app:
- Right-click → Open → Confirm

### Linux

Options:
- Debian/Ubuntu: `yass-reincarnated-linux-amd64.deb`
- Nix-based Linux package: `yass-reincarnated-linux-x86_64-nix.tar.gz`
- or run the Fat JAR `Yass-Reincarnated.jar` (see below)

FFmpeg must be installed via your package manager.

## Running the Fat JAR (Advanced)

If no native build is available, you can use the Fat JAR:

```bash
java -jar Yass-Reincarnated.jar
```
### Requirements for starting the JAR
Minimum requirement is Java 21.

Yass Reincarnated also requires FFmpeg to support a variety of audio formats.
https://www.ffmpeg.org/download.html
- Windows (`winget install --id=Gyan.FFmpeg -e`) - Check out: https://www.gyan.dev/ffmpeg/builds/
- MacOS (`brew install ffmpeg`)
- Linux (`sudo apt install ffmpeg`)

# Starting Yass Reincarnated
If you install Yass Reincarnated with the Windows installer, you can simply run `yass.exe`

If you use one of the packaged builds (`.exe`, `.dmg`, `.deb`, or the Linux Nix package), no manual JavaFX setup is required.

Alternatively, you can download the jar file and start it from the console:

On Windows, you can simply do this: `java -jar Yass-Reincarnated.jar`

On macOS/Linux, `java -jar Yass-Reincarnated.jar` also works if your Java installation already includes JavaFX.

Only if you use a plain JDK without JavaFX, add a JavaFX SDK manually, for example:

`export PATH_TO_FX=path/to/javafx-sdk-[whateverversion]/lib`

and start the jar like this:

`java --module-path $PATH_TO_FX --add-modules javafx.swing,javafx.media -jar Yass-Reincarnated.jar`

# Developers

Install JDK 21 and set `JAVA_HOME` to its installation directory. The included
Maven Wrapper downloads Maven 3.9.16 on first use and caches it for later builds;
you do not need to install Maven separately. The first build requires internet
access to download Maven and project dependencies.

From the repository root on Windows (PowerShell):

```powershell
.\mvnw.cmd compile exec:exec
```

On macOS or Linux:

```sh
sh ./mvnw compile exec:exec
```

Run tests with `.\mvnw.cmd -q test`, or a single Spock spec with
`.\mvnw.cmd -q "-Dtest=YassRowSpec" test`. Package the application with
`.\mvnw.cmd -q -DskipTests package`; the resulting JAR is
`target/Yass-Reincarnated.jar`. On macOS/Linux, use `sh ./mvnw` in place of
`.\mvnw.cmd` for these commands.

FFmpeg and ffprobe are needed for media handling; see the requirements above.
Implemented feature specs and code entry points live in `specs/`, and tests live
in `test/groovy/`.

# Yass Reincarnated uses
- Java Look & Feel Graphics Repository
- FFmpeg
- [jAudiotagger](https://www.jthink.net/jaudiotagger)
- [FFmpeg CLI Wrapper for Java](https://github.com/bramp/ffmpeg-cli-wrapper)
- iText
- JInput
- TeX Hyphenatorpeg
- [Optimaize Language Detector](https://github.com/optimaize/language-detector)
- [JAudiotagger](https://www.jthink.net/jaudiotagger/)
- Java Media Framework (JMF)
- Robert Eckstein's Wizard code
- juniversalchardet

Licenses are stated in the application's help section.

Speed measure 'Inverse Duration' based on Marcel Taeumel's approach (http://uman.sf.net).

# Support for external tools
## yt-dlp
yt-dlp is used to download audio and video files from YouTube
https://github.com/yt-dlp/yt-dlp#release-files

## aubio
aubio is used to determine the BPM when using the Song Creation Wizard and visualizing the pitch of the vocal track in 
the song editor
https://aubio.org/download
You should select the package WITHOUT bundled ffMpeg

## MVSEP
MVSEP can be configured in `External Tools > MVSEP` and is used for cloud-based stem separation.
Yass Reincarnated can upload the current `#AUDIO` file, monitor the separation job, download the finished stems into the
song folder, and link `#VOCALS` / `#INSTRUMENTAL` automatically when possible.
https://mvsep.com/

# License

Copyright (C) 2009-2023 Saruta, 2023-2026 DoubleDee, 2026 OliBomby

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

# Support
To support OliBomby's Yass Reincarnated work, you can use the donation link below.

<a href='https://ko-fi.com/V7V2HPH5F' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://storage.ko-fi.com/cdn/kofi6.png?v=6' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

# Troubleshooting
## Yass Reincarnated does nothing after showing the splash screen
- Have you checked your Java version? 
  - Open a command line window and type in `java -version`. 
  - You should have Java 21
- Have you tried opening it as a jar?
  - Download the jar, open a command line window and type in `java -jar Yass-Reincarnated.jar`
  - Any error messages are then printed out in the command line window for debugging

## Cannot find FFmpeg?
Yass Reincarnated has a built-in auto-detection for FFmpeg.
It will look in any PATH environment variables and try to find both ffmpeg and ffprobe.  
If you are sure, you have installed ffmpeg, maybe you did not choose to have the PATH variable automatically configured.  
If this is the cae, then it is recommended, that you add the FFmpeg path to your PATH variables

You can also add the path to FFmpeg to the Yass Reincarnated configuration file. The user.xml should be in the ~/.yass/ folder

There should be an entry:

`<entry key="ffmpegPath"></entry>`

If it's not, add it, and put the FFmpeg path before </entry>, e. g.:

`<entry key="ffmpegPath">/usr/bin</entry>`

or

`<entry key="ffmpegPath">C:\ffmpeg\bin</entry>`

depending on your OS.

It is also possible, that when you use Automator on Mac to start Yass Reincarnated,
that due to restrictions to the PATH variables, the auto-detection fails.

In this case, start Yass Reincarnated from the console. If FFmpeg is then detected, the path
will be written into the user.xml, and you can use Automator again.
