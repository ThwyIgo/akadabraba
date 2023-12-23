{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  shellHook = ''
    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${pkgs.lib.makeLibraryPath (with pkgs; [
                 libGL
                 openal
                 alsa-lib
                 flite
                 udev
               ])}";
    idea-community && exit
  '';
}
