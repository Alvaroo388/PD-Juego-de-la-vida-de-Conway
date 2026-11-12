package gameoflife

enum Player:
  case Player1, Player2

// Cambia de jugador
def otherPlayer(p: Player): Player = p match {
  case Player.Player1 => Player.Player2
  case Player.Player2 => Player.Player1
}

// Devuelve el nombre del jugador
def playerName(p: Player): String = p match {
  case Player.Player1 => "Jugador 1"
  case Player.Player2 => "Jugador 2"
}
