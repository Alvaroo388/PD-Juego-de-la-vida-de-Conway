package gameoflife
object ManualStrategyTests extends App:
  val player = Player.Player1
  val state = State(Map(player -> GameOfLifeMatrix.empty(3,3)), Map(player -> 2), player)
  def read(input: String, error: Option[String] = None): (Position, String) =
    val output = new java.io.ByteArrayOutputStream()
    val position = Console.withOut(output) {
      Console.withIn(new java.io.ByteArrayInputStream(input.getBytes("UTF-8"))) {
        ManualStrategy.choosePosition(state, player, error)
      }
    }
    (position, output.toString("UTF-8"))
  val normal = read("  1 2  \n", Some("Casilla ocupada"))
  assert(normal._1 == Position(1,2))
  assert(normal._2.contains("Casilla ocupada") && normal._2.contains("Jugador 1") && normal._2.contains("2 células"))
  val invalid = read("\nuno dos\n1\n1 2 3\n999999999999 0\n2 1\n")
  assert(invalid._1 == Position(2,1))
  assert(invalid._2.sliding("Formato incorrecto".length).count(_ == "Formato incorrecto") == 3)
  assert(invalid._2.sliding("no es un número entero válido".length).count(_ == "no es un número entero válido") == 2)
  assert(read("1  2\n1 2\n")._1 == Position(1,2))
  assert(read("-1 99\n")._1 == Position(-1,99))
  assert(read(List.fill(10000)("mal").mkString("\n") + "\n0 0\n")._1 == Position(0,0))
  var closed = false
  try read("")
  catch case _: java.io.EOFException => closed = true
  assert(closed)
  println("OK: lectura válida, mensajes, formatos incorrectos, desbordamiento, reintentos y cierre de entrada")
