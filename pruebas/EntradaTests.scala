package gameoflife

object EntradaTests extends App:
  assert(Entrada.validarEntero(" 12 ") == Right(12))
  assert(Entrada.validarEntero("-3") == Right(-3))
  List("", "texto", "1.5", "999999999999").foreach { texto =>
    assert(Entrada.validarEntero(texto).isLeft)
  }
  assert(Entrada.validarPosicion(" 1 2 ") == Right(Position(1,2)))
  assert(Entrada.validarPosicion("-1 99") == Right(Position(-1,99)))
  // Con split(" ") se exige un único espacio entre las coordenadas.
  List("", "1", "1 2 3", "1  2", "1\t2", "uno 2", "1 dos").foreach { texto =>
    assert(Entrada.validarPosicion(texto).isLeft)
  }
  assert(Entrada.validarPresupuesto("1",1,225) == Right(1))
  assert(Entrada.validarPresupuesto("225",1,225) == Right(225))
  List("0", "226", "mal").foreach { texto =>
    assert(Entrada.validarPresupuesto(texto,1,225).isLeft)
  }
  assert(Entrada.parsearSiNo(" SÍ ") == Right(true))
  assert(Entrada.parsearSiNo(" NO ") == Right(false))
  assert(Entrada.parsearSiNo("quizás").isLeft)
  assert(Entrada.parsearHumanoOIA(" HUMANO ") == Right(true))
  assert(Entrada.parsearHumanoOIA(" IA ") == Right(false))
  assert(Entrada.parsearHumanoOIA("otra").isLeft)

  Console.withOut(new java.io.ByteArrayOutputStream()):
    Console.withIn(new java.io.ByteArrayInputStream("hola\n\n".getBytes("UTF-8"))):
      assert(Entrada.leerLinea("") == scala.util.Success(Some("hola")))
      assert(Entrada.leerLinea("") == scala.util.Success(Some("")))
      assert(Entrada.leerLinea("") == scala.util.Success(None))
    val entradaRota = new java.io.InputStream:
      override def read(): Int = throw new java.io.IOException("Fallo simulado")
    Console.withIn(entradaRota):
      assert(Entrada.leerLinea("").isFailure)

  println("OK: validaciones puras, errores, lectura, línea vacía, cierre y fallo de entrada")
