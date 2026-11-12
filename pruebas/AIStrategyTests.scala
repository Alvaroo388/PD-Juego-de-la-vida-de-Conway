package gameoflife

object AIStrategyTests extends App:
  def comprobar(rows: Int, columns: Int, budget: Int): Unit =
    val game = new Game(rows, columns, budget, 10)
    val ai = AIStrategy(game, 3)
    assert(ai.chosenPattern.length == budget)
    assert(ai.chosenPattern.distinct.length == budget)
    assert(ai.chosenPattern.forall(game.initialState.boards(Player.Player1).inside))
    val estado = game.placementLoop(game.initialState,
      Map(Player.Player1 -> ai, Player.Player2 -> ai))
    assert(estado.boards.values.forall(_.aliveCells.toSet == ai.chosenPattern.toSet))
    assert(estado.remainingCells.values.forall(_ == 0))

  List((15,15,0), (15,15,1), (15,15,15), (15,15,224), (15,15,225),
    (1,1,1), (1,15,10), (15,1,10), (2,20,35), (20,2,35)).foreach {
    case (rows,columns,budget) => comprobar(rows,columns,budget)
  }
  val central = AIStrategy(new Game(15,15,5,10), 3).chosenPattern
  assert(central.forall(p => p.row >= 5 && p.row <= 8 && p.column >= 5 && p.column <= 8))
  assert(throws[IllegalArgumentException](AIStrategy(new Game(3,3,2,10),0)))
  assert(throws[IllegalArgumentException](AIStrategy(new Game(3,3,10,10),1)))
  assert(throws[IllegalArgumentException](AIStrategy(new Game(3,3,-1,10),1)))

  // Evaluación controlada: comprobar que se elige por generaciones y después por vivas.
  val resultados = List((2,9), (3,1), (3,5), (2,100))
  class Evaluador extends Game(5,5,3,10):
    var evaluados = List.empty[GameOfLifeMatrix]
    override def simulate(board: GameOfLifeMatrix): (Int, Int) =
      val resultado = resultados(evaluados.length)
      evaluados = evaluados :+ board
      resultado
  val evaluador = new Evaluador
  val mejor = AIStrategy(evaluador,4)
  assert(evaluador.evaluados.length == 4)
  assert(mejor.chosenPattern.toSet == evaluador.evaluados(2).aliveCells.toSet)

  val game = new Game(3,3,2,10)
  val ai = new AIStrategy(List(Position(1,1),Position(0,0)))
  assert(ai.choosePosition(game.initialState,Player.Player1,Some("ignorado")) == Position(1,1))
  val colocado = game.placeCell(game.initialState,Position(1,1)).toOption.get
  assert(ai.choosePosition(colocado,Player.Player1,None) == Position(0,0))
  assert(ai.choosePosition(colocado,Player.Player2,None) == Position(1,1))
  println("OK: IA central, presupuestos, tablero completo, rectángulos, evaluación y orden de colocación")
