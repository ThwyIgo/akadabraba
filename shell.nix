{ pkgs ? import <nixpkgs> {} }:
with pkgs;
mkShell {
  buildInputs = [ jdk21 ];
  packages = [ jetbrains.idea-community ];

  shellHook = ''
    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.lib.makeLibraryPath [
                 libGL
                 openal
                 alsa-lib
                 flite
                 udev
               ]}";
    idea-community &disown
    exit
  '';
}
