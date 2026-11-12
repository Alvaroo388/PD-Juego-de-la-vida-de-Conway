package gameoflife
object GameTests extends App:
  val game = new Game(5, 5, 4, 100)
  val initial = game.initialState
  assert(initial.turn == Player.Player1)
  assert(initial.remainingCells == Map(Player.Player1 -> 4, Player.Player2 -> 4))
  assert(initial.boards.values.forall(b => b.rows == 5 && b.columns == 5 && b.aliveCount == 0))
  assert(!game.hasFinished(initial, Player.Player1))
  assert(game.hasFinished(initial.copy(remainingCells = Map(Player.Player1 -> 0, Player.Player2 -> 4)), Player.Player1))
  List(Position(-1,0), Position(0,-1), Position(5,0), Position(0,5)).foreach { p =>
    assert(game.placeCell(initial,p).isLeft)
  }
  val afterFirst = game.placeCell(initial,Position(0,0)).toOption.get
  assert(initial.boards(Player.Player1).aliveCount == 0)
  assert(initial.remainingCells(Player.Player1) == 4)
  assert(afterFirst.boards(Player.Player1).get(Position(0,0)) == Alive)
  assert(afterFirst.boards(Player.Player2) == initial.boards(Player.Player2))
  assert(afterFirst.remainingCells == Map(Player.Player1 -> 3, Player.Player2 -> 4))
  assert(afterFirst.turn == Player.Player2)
  // La misma coordenada está libre en el tablero del rival.
  val afterSecond = game.placeCell(afterFirst,Position(0,0)).toOption.get
  assert(afterSecond.turn == Player.Player1)
  assert(game.placeCell(afterSecond,Position(0,0)).isLeft)
  val finished = initial.copy(remainingCells = Map(Player.Player1 -> 0, Player.Player2 -> 4))
  assert(game.placeCell(finished,Position(0,0)).isLeft)
  val rivalFinished = initial.copy(remainingCells = Map(Player.Player1 -> 1, Player.Player2 -> 0))
  val lastMove = game.placeCell(rivalFinished,Position(1,1)).toOption.get
  assert(lastMove.turn == Player.Player1 && lastMove.remainingCells.values.forall(_ == 0))
  val blockPositions = List(Position(1,1), Position(1,2), Position(2,1), Position(2,2))
  val block = GameOfLifeMatrix.withAlive(5,5,blockPositions)
  val empty = GameOfLifeMatrix.empty(5,5)
  val blinker = GameOfLifeMatrix.withAlive(5,5,List(Position(2,1),Position(2,2),Position(2,3)))
  assert(game.simulate(block) == (1,4))
  assert(game.simulate(empty) == (1,0))
  assert(game.simulate(blinker) == (2,3))
  assert(game.simulate(GameOfLifeMatrix.withAlive(5,5,List(Position(2,2)))) == (2,0))
  val glider = GameOfLifeMatrix.withAlive(8,8,List(Position(0,1),Position(1,2),Position(2,0),Position(2,1),Position(2,2)))
  assert(new Game(8,8,5,3).simulate(glider) == (3,5))
  assert(new Game(5,5,0,0).simulate(block) == (0,4))
  assert(new Game(5,5,0,1).simulate(blinker) == (1,3))
  assert(game.determineWinner((10,3),(7,50)) == Some(Player.Player1))
  assert(game.determineWinner((7,50),(10,3)) == Some(Player.Player2))
  assert(game.determineWinner((5,8),(5,4)) == Some(Player.Player1))
  assert(game.determineWinner((5,4),(5,8)) == Some(Player.Player2))
  assert(game.determineWinner((5,4),(5,4)).isEmpty)
  def scripted(pattern: List[Position]): Strategy = new Strategy:
    def choosePosition(s: State, p: Player, e: Option[String]): Position =
      assert(e.isEmpty)
      pattern.find(pos => s.boards(p).get(pos) == Dead).get
  val corners = List(Position(0,0),Position(0,4),Position(4,0),Position(4,4))
  val result = game.play(Map(Player.Player1 -> scripted(blockPositions), Player.Player2 -> scripted(corners)))
  assert(result.results == Map(Player.Player1 -> (1,4), Player.Player2 -> (2,0)))
  assert(result.winner == Some(Player.Player2))
  assert(result.finalState.boards(Player.Player1) == block)
  assert(result.finalState.boards(Player.Player2).aliveCount == 4)
  assert(result.finalState.remainingCells.values.forall(_ == 0))
  val zero = new Game(2,2,0,100).play(Map.empty)
  assert(zero.winner.isEmpty && zero.results.values.forall(_ == (1,0)))
  println("OK: simulaciones, límites, desempates, partida completa y patrones iniciales conservados")
