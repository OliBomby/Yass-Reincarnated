{
  description = "Yass Reincarnated Nix package";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachSystem [ "x86_64-linux" "aarch64-linux" ] (system:
      let
        pkgs = import nixpkgs { inherit system; };
        lib = pkgs.lib;
        pname = "yass-reincarnated";
        releaseVersion = builtins.getEnv "RELEASE_VERSION";
        version = if releaseVersion != "" then releaseVersion else "2026.7";
        runtimeLibs = with pkgs; [
          alsa-lib
          fontconfig
          freetype
          glib
          gtk3
          harfbuzz
          libGL
          pango
          zlib
        ] ++ (with pkgs.xorg; [
          libX11
          libXcursor
          libXi
          libXrandr
          libXrender
          libXtst
        ]);
      in {
        packages.default = pkgs.stdenv.mkDerivation {
          inherit pname version;
          src = ./.;

          nativeBuildInputs = with pkgs; [
            jdk21
            makeWrapper
            maven
          ];

          buildInputs = with pkgs; [
            ffmpeg
            aubio
          ] ++ runtimeLibs;

          buildPhase = ''
            runHook preBuild
            export HOME="$TMPDIR"
            mkdir -p "$TMPDIR/.m2/repository"
            mvn -q \
              -Dmaven.repo.local="$TMPDIR/.m2/repository" \
              -Drevision="$version" \
              clean package -DskipTests -Djavafx.platform=linux
            runHook postBuild
          '';

          installPhase = ''
            runHook preInstall

            mkdir -p "$out/share/${pname}"
            mkdir -p "$out/bin"
            mkdir -p "$out/share/applications"
            mkdir -p "$out/share/icons/hicolor/256x256/apps"

            cp target/Yass-Reincarnated.jar "$out/share/${pname}/Yass-Reincarnated.jar"
            cp src/yass/resources/icons/yass-icon.png "$out/share/icons/hicolor/256x256/apps/yass-reincarnated.png"

            cat > "$out/share/applications/yass-reincarnated.desktop" <<EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=Yass Reincarnated
Comment=Karaoke editor for UltraStar songs
Exec=$out/bin/yass-reincarnated
Icon=yass-reincarnated
Terminal=false
Categories=AudioVideo;Audio;Music;Utility;
StartupWMClass=YassMain
EOF

            makeWrapper ${pkgs.jdk21}/bin/java "$out/bin/yass-reincarnated" \
              --prefix PATH : ${lib.makeBinPath [ pkgs.ffmpeg pkgs.aubio ]} \
              --prefix LD_LIBRARY_PATH : ${lib.makeLibraryPath runtimeLibs} \
              --add-flags "-jar $out/share/${pname}/Yass-Reincarnated.jar"

            runHook postInstall
          '';

          meta = with lib; {
            description = "Yass Reincarnated karaoke editor";
            homepage = "https://github.com/OliBomby/Yass-Reincarnated";
            license = licenses.gpl3Plus;
            mainProgram = "yass-reincarnated";
            platforms = platforms.linux;
          };
        };

        apps.default = {
          type = "app";
          program = "${self.packages.${system}.default}/bin/yass-reincarnated";
        };

        devShells.default = pkgs.mkShell {
          packages = with pkgs; [
            jdk21
            maven
            ffmpeg
            aubio
          ];
        };
      });
}
