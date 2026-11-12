package gameoflife

// Ejecuta el programa real en otra JVM y le proporciona las respuestas por teclado.
object MainIntegrationTests extends App:
  def ejecutar(entrada: String): String =
    val ejecutable = if System.getProperty("os.name").startsWith("Windows") then "java.exe" else "java"
    val javaExecutable = new java.io.File(System.getProperty("java.home"), "bin/" + ejecutable).getPath
    val proceso = new ProcessBuilder(
      javaExecutable, "-Dfile.encoding=UTF-8", "-cp", System.getProperty("java.class.path"), "gameoflife.Main"
    ).redirectErrorStream(true).start()
    val escritor = new java.io.OutputStreamWriter(proceso.getOutputStream, "UTF-8")
    escritor.write(entrada)
    escritor.close()
    val salida = new String(proceso.getInputStream.readAllBytes(), "UTF-8")
    assert(proceso.waitFor() == 0, salida)
    salida

  List("", "1\n", "1\nh\n", "1\nh\nh\n", "1\nh\nh\n0 0\n0 0\n").foreach { entrada =>
    assert(ejecutar(entrada).contains("Entrada cerrada. La partida ha terminado."))
  }
  val errores = ejecutar("texto\n0\n226\n1\notra\nh\nh\nmal\n-1 0\n0 0\n0 0\nquizas\nn\n")
  assert(errores.contains("Formato incorrecto"))
  assert(errores.contains("fuera de los límites"))
  assert(errores.contains("Empate:"))
  assert(errores.contains("Responde s"))
  assert(errores.contains("Responde h"))
  val generaciones = ejecutar("4\nh\nh\n1 1\n0 0\n1 2\n0 14\n2 1\n14 0\n2 2\n14 14\nn\n")
  assert(generaciones.contains("Gana Jugador 2 por tardar más generaciones"))
  assert(generaciones.contains("1 generaciones y 4 células"))
  assert(generaciones.contains("2 generaciones y 0 células"))
  val vivas = ejecutar("5\nh\nh\n1 1\n0 0\n1 2\n0 14\n2 1\n14 0\n2 2\n14 14\n14 14\n7 7\nn\n")
  assert(vivas.contains("Gana Jugador 1 por tener más células vivas finales"))
  assert(ejecutar("1\nh\nh\n0 0\n0 0\ns\n").contains("estabilizado"))
  // Presupuesto uno produce un empate conocido, aunque las coordenadas de la IA sean aleatorias.
  List("1\nh\nia\n0 0\nn\n", "1\nia\nh\n0 0\nn\n", "1\nia\nia\nn\n").foreach { entrada =>
    val salida = ejecutar(entrada)
    assert(salida.contains("Buscando un patrón"))
    assert(salida.contains("Empate:"))
    assert(!salida.contains("Entrada cerrada"))
  }
  val completo = ejecutar("225\nia\nia\nn\n")
  assert(completo.contains("Empate:"))
  assert(!completo.contains("Introduce fila columna"))
  println("OK: programa completo, ambos criterios de victoria, animación y cierre de entrada")
