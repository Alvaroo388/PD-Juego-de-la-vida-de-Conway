package gameoflife
object PlacementLoopTests extends App:
  val p1 = Player.Player1
  val p2 = Player.Player2
  val forbidden = new Strategy:
    def choosePosition(s: State, p: Player, e: Option[String]): Position =
      throw new AssertionError("No debe preguntar a un jugador terminado")
  val zero = new Game(2, 2, 0, 10)
  assert(zero.placementLoop(zero.initialState, Map.empty) == zero.initialState)
  val game = new Game(2, 2, 2, 10)
  var calls = 0
  val scripted = new Strategy:
    def choosePosition(s: State, p: Player, e: Option[String]): Position =
      calls += 1
      calls match
        case 1 =>
          assert(p == p1 && e.isEmpty)
          Position(-1, 0)
        case 2 =>
          assert(p == p1 && e.nonEmpty && s == game.initialState)
          Position(0, 0)
        case 3 =>
          assert(p == p2 && e.isEmpty)
          Position(0, 0)
        case 4 =>
          assert(p == p1 && e.isEmpty)
          Position(0, 0)
        case 5 =>
          assert(p == p1 && e.nonEmpty)
          Position(0, 1)
        case 6 =>
          assert(p == p2 && e.isEmpty)
          Position(0, 1)
        case _ => throw new AssertionError("Demasiadas peticiones")
  val result = game.placementLoop(game.initialState, Map(p1 -> scripted, p2 -> scripted))
  assert(calls == 6 && result.remainingCells.values.forall(_ == 0))
  assert(result.boards(p1).aliveCount == 2 && result.boards(p2).aliveCount == 2)
  val partial = game.initialState.copy(remainingCells = Map(p1 -> 0, p2 -> 1))
  val remaining = new Strategy:
    def choosePosition(s: State, p: Player, e: Option[String]): Position =
      assert(p == p2 && e.isEmpty)
      Position(1, 1)
  val completed = game.placementLoop(partial, Map(p1 -> forbidden, p2 -> remaining))
  assert(completed.remainingCells.values.forall(_ == 0))
  println("OK: presupuesto cero, turnos, posición exterior, casilla ocupada, errores y jugador terminado")