let pkgs = import <nixpkgs> {};

in pkgs.stdenv.mkDerivation rec {
  name = "pimote-api";

  buildInputs = with pkgs; [
    leiningen
  ];
}
