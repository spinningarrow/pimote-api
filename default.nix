let pkgs = import <nixpkgs> {};

in pkgs.stdenv.mkDerivation rec {
  name = "pimote";

  buildInputs = with pkgs; [
    leiningen
  ];
}
