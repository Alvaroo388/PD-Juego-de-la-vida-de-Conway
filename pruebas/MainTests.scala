package gameoflife

// Prueba las funciones de lectura sin necesitar un teclado real.
object MainTests extends App:
  def conEntrada[A](entrada: String)(accion: => A): A =
    Console.withOut(new java.io.ByteArrayOutputStream()):
      Console.withIn(new java.io.ByteArrayInputStream(entrada.getBytes("UTF-8"))):
        accion

  // Inicializamos Main con la entrada cerrada antes de probar sus funciones por separado.
  conEntrada("") { Main }

  assert(conEntrada("1\n")(Main.readInt("Número: ", 1, 225)) == 1)
  assert(conEntrada("225\n")(Main.readInt("Número: ", 1, 225)) == 225)
  assert(conEntrada("\ntexto\n0\n226\n999999999999\n 12 \n")(
    Main.readInt("Número: ", 1, 225)) == 12)
  assert(conEntrada("-3\n")(Main.readInt("Número: ", -5, -1)) == -3)
  List("s", "si", "sí", " SÍ ").foreach { respuesta =>
    assert(conEntrada(respuesta + "\n")(Main.readYesNo("¿Sí? ")))
  }
  List("n", "no", " NO ").foreach { respuesta =>
    assert(!conEntrada(respuesta + "\n")(Main.readYesNo("¿Sí? ")))
  }
  assert(conEntrada("quizás\n\ns\n")(Main.readYesNo("¿Sí? ")))
  List("h", "humano", " HUMANO ").foreach { respuesta =>
    assert(conEntrada(respuesta + "\n")(Main.readHumanOrAI("Estrategia: ")))
  }
  assert(!conEntrada(" IA \n")(Main.readHumanOrAI("Estrategia: ")))
  assert(conEntrada("otra\nh\n")(Main.readHumanOrAI("Estrategia: ")))
  assert(throws[java.io.EOFException](conEntrada("")(Main.readInt("Número: ", 1, 225))))
  assert(throws[java.io.EOFException](conEntrada("")(Main.readYesNo("¿Sí? "))))
  assert(throws[java.io.EOFException](conEntrada("")(Main.readHumanOrAI("Estrategia: "))))
  assert(conEntrada(List.fill(10000)("mal").mkString("\n") + "\n1\n")(
    Main.readInt("Número: ", 1, 225)) == 1)

  val state = new Game(2, 2, 0, 10).initialState
  def resultado(r1: (Int, Int), r2: (Int, Int)): GameResult =
    val game = new Game(2, 2, 0, 10)
    GameResult(state, Map(Player.Player1 -> r1, Player.Player2 -> r2), game.determineWinner(r1, r2))
  assert(Main.describeWinner(resultado((5, 4), (5, 4))).contains("Empate"))
  assert(Main.describeWinner(resultado((10, 3), (7, 50))).contains("Jugador 1"))
  assert(Main.describeWinner(resultado((7, 50), (10, 3))).contains("Jugador 2"))
  assert(Main.describeWinner(resultado((10, 3), (7, 50))).contains("más generaciones"))
  assert(Main.describeWinner(resultado((5, 8), (5, 4))).contains("más células vivas"))
  assert(Main.describeWinner(resultado((5, 4), (5, 8))).contains("Jugador 2"))
  println("OK: lecturas, límites, reintentos, cierre de entrada y explicación del ganador")
