package gameoflife

object PlayerTests extends App:
  assert(otherPlayer(Player.Player1) == Player.Player2)
  assert(otherPlayer(Player.Player2) == Player.Player1)
  Player.values.foreach(p => assert(otherPlayer(otherPlayer(p)) == p))
  assert(playerName(Player.Player1) == "Jugador 1")
  assert(playerName(Player.Player2) == "Jugador 2")
  println("OK: rival y nombres de los jugadores")
