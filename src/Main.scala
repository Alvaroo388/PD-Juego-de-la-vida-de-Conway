package gameoflife

import scala.util.{Success, Failure}

object Main extends App:
  val BoardSize      = 15
  val MaxGenerations = 1000
  // Número de candidatos evaluados por cada IA antes de empezar la colocación.
  val AIAttempts     = 10

  // Pide un numero dentro de un intervalo que usaremos para pedir el numero de celulas
  def readInt(prompt: String, min: Int, max: Int): Int = {
    @annotation.tailrec
    // Creamos una funcion auxiliar recursiva para pedir un entero dentro del intervalo y usamos las funciones de Entrada.scala para validarlo
    def leerNumero(): Int = {
      Entrada.leerLinea(prompt) match {
        case Success(Some(texto)) =>
          Entrada.validarPresupuesto(texto, min, max) match {
            case Right(numero) => numero
            case Left(mensaje) =>
              println(mensaje)
              leerNumero()
          }
        // Controlamos los dos casos de errores que puede devolver LeerLinea
        case Success(None) => throw new java.io.EOFException("Se ha cerrado la entrada")
        case Failure(error) => throw new java.io.IOException("No se ha podido leer la entrada", error)
      }
    }
    leerNumero()
  }

  // Devuelve true para humano y false para IA
  @annotation.tailrec
  def readHumanOrAI(prompt: String): Boolean = {
    // Igual que antes usamos las funciones de Entrada.scala para validar lo que introduce el jugador y controlar los errores
    Entrada.leerLinea(prompt) match {
      case Success(Some(texto)) =>
        Entrada.parsearHumanoOIA(texto) match {
          case Right(humano) => humano
          case Left(mensaje) =>
            println(mensaje)
            readHumanOrAI(prompt)
        }
      case Success(None) => throw new java.io.EOFException("Se ha cerrado la entrada")
      case Failure(error) => throw new java.io.IOException("No se ha podido leer la entrada", error)
    }
  }

  // Repite la pregunta hasta recibir una respuesta afirmativa o negativa 
  // practicamente igual que la anterior pero con si / no
  @annotation.tailrec
  def readYesNo(prompt: String): Boolean = {
    Entrada.leerLinea(prompt) match {
      case Success(Some(texto)) =>
        Entrada.parsearSiNo(texto) match {
          case Right(respuesta) => respuesta
          case Left(mensaje) =>
            println(mensaje)
            readYesNo(prompt)
        }
      case Success(None) => throw new java.io.EOFException("Se ha cerrado la entrada")
      case Failure(error) => throw new java.io.IOException("No se ha podido leer la entrada", error)
    }
  }

  // Explica el criterio que ha decidido el resultado
  def describeWinner(result: GameResult): String = {
    result.winner match {
      case None => "Empate: ambos jugadores tienen las mismas generaciones y células vivas finales."
      case Some(ganador) =>
        val rival = otherPlayer(ganador)
        if result.results(ganador)._1 > result.results(rival)._1 then // el ._1 es para obtener las generaciones porque es un (Int, Int) y nos interesa la primera posicion
          s"Gana ${playerName(ganador)} por tardar más generaciones en detenerse."
        else
          s"Gana ${playerName(ganador)} por tener más células vivas finales, a igualdad de generaciones."
    }
  }

  // Logica Principal
  println("EL JUEGO DE LA VIDA: la batalla de los patrones")
  try { // El try es por si en algun momento se cierra la entrada para atrapa el error
    // preguntamos cuantas celulas quieren usar siendo el maximo el numero de casillas
    // hemos valorado que esto no tiene mucho sentido pero tecnicamente deberia ser una opcion asi que por eso hemos puesto ese limite y no uno menor
    val presupuesto = readInt(s"Células por jugador (1-${BoardSize * BoardSize}): ", 1, BoardSize * BoardSize)
    // creamos la partida
    val game = new Game(BoardSize, BoardSize, presupuesto, MaxGenerations)
    // Cada jugador elige teclado o una IA que tiene su propio patrón calculado
    val estrategias: Map[Player, Strategy] = List(Player.Player1, Player.Player2).map { jugador =>
      val humano = readHumanOrAI(s"${playerName(jugador)}: ¿humano o IA? (h/ia): ")
      // dependiendo de lo que elija el jugador se elige la version contra jugador o contra IA
      val estrategia: Strategy = if humano then ManualStrategy else {
        println(s"Buscando un patrón para ${playerName(jugador)}...")
        AIStrategy(game, AIAttempts)
      }
      jugador -> estrategia
    }.toMap
    // Coloca las celulas, simula ambos tableros y almacena los resultados
    val resultado = game.play(estrategias)
    // Un pequeño resumen donde para cada jugador se muestra el patron inicial y las generaciones y celulas vivas al final
    List(Player.Player1, Player.Player2).foreach { jugador =>
      println(resultado.finalState.boards(jugador).show(s"Patrón inicial de ${playerName(jugador)}"))
      val (generaciones, vivas) = resultado.results(jugador)
      println(s"${playerName(jugador)}: $generaciones generaciones y $vivas células vivas finales.")
    }
    // Por ultimo se explica el resultado y se ofrece ver la animacion
    println(describeWinner(resultado))
    if readYesNo("¿Quieres ver la animación? (s/n): ") then
      Animation.animate(resultado)
  } catch {
    case _: java.io.EOFException => println("Entrada cerrada. La partida ha terminado.")
    case _: java.io.IOException => println("No se ha podido leer la entrada. La partida ha terminado.")
  }
