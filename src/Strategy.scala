package gameoflife

import scala.io.StdIn
import scala.util.{Random, Try}

trait Strategy:
  def choosePosition(state: State, player: Player, error: Option[String]): Position

// Manera en la que el jugador elige en que casilla del tablero colocar una celula
case object ManualStrategy extends Strategy:
  // pasamos el estado, el jugador que elige casilla y el error del intento anterior si lo hibiera
  def choosePosition(state: State, player: Player, error: Option[String]): Position = {
    // Si hay algun error o errores los mostramos por pantalla
    error.foreach(mensaje => println(mensaje))
    // Mostramos el nombre del jugador y sus celulas restantes ademas de su tablero actual para que elija casilla
    val titulo = s"${playerName(player)} te quedan ${state.remainingCells(player)} células"
    println(state.boards(player).show(titulo))

    @annotation.tailrec
    // Creamos una funcion auxiliar recursiva para pedir al jugador la posicion hasta que nos de un formato valido
    def leerPosicion(): Position = {
      val linea = StdIn.readLine("Introduce fila columna: ")
      // Como bien indica el enunciado -> 'Si readLine devuelve null (se ha cerrado la entrada), la partida termina'
      if linea == null then throw new java.io.EOFException("Se ha cerrado la entrada")
      // Leemos la entrada del usuario y la separamos en fila y columna
      val posicion = linea.trim.split("\\s+").toList match {
        // Miramos si el formato es valido
        case fila :: columna :: Nil =>
          Try(Position(fila.toInt, columna.toInt)).toOption
        case _ => None
      }
      // Si la comprobacion anterior devuelve None, mandamos un mensaje de que el formato era incorrecto y volvermos a pedir una posicion al jugador
      posicion match {
        case Some(p) => p
        case None =>
          println("Formato incorrecto: introduce dos números enteros separados por espacios.")
          leerPosicion()
      }
    }

    leerPosicion()
  }

case class AIStrategy(chosenPattern: List[Position]) extends Strategy:
  // Devuelve la primera posición del patrón que este jugador aún no ha colocado.
  def choosePosition(state: State, player: Player, error: Option[String]): Position = {
    val tablero = state.boards(player)
    // find respeta el orden de la lista y no necesita un contador mutable de turnos.
    chosenPattern.find(p => tablero.get(p) == Dead) match {
      case Some(posicion) => posicion
      case None => throw new IllegalStateException("No quedan posiciones del patrón por colocar")
    }
  }

// Las decisiones sobre la estrategia de la IA estan explicadas mejor en la memoria
object AIStrategy:
    // Busca varios patrones aleatorios en una zona central y conserva el mejor.
    def apply(game: Game, attempts: Int): AIStrategy = {
      // Comprobamos todos los casos en los que no se pueda generar un patron valido
      require(attempts >= 1, "Debe probarse al menos un patrón")
      require(game.rows > 0 && game.columns > 0, "El tablero debe tener filas y columnas")
      require(game.budget >= 0 && game.budget.toLong <= game.rows.toLong * game.columns,
        "El presupuesto debe estar entre cero y el número de casillas")

      // Buscamos mas o menos el doble de espacio que de celulas disponibles para que no esten ni muy juntas ni muy separadas
      // Usamos ceil para redondear hacia arriba
      val lado = math.ceil(math.sqrt(2.0 * game.budget)).toInt
      // Si el tablero es muy bajo ampliamos el ancho para que quepan las células
      val alto = math.min(game.rows, math.max(1, lado))
      val anchoNecesario = math.ceil(game.budget.toDouble / alto).toInt
      val ancho = math.min(game.columns, math.max(lado, anchoNecesario))
      // Si al contrario el tablero es muy estrecho ampliamos el elto para que quepan las células
      val altoNecesario = if ancho == 0 then 0 else math.ceil(game.budget.toDouble / ancho).toInt
      val altoZona = math.max(alto, altoNecesario)
      // para desplazar la zona hacia el centro del tablero calculamos el margen que hay que dejar hacia cada lado
      val filaInicial = (game.rows - altoZona) / 2
      val columnaInicial = (game.columns - ancho) / 2
      val posiciones = (for {
        fila <- filaInicial until filaInicial + altoZona
        columna <- columnaInicial until columnaInicial + ancho
      } yield Position(fila, columna)).toList

      // Si no se puede elegir ninguna celula o se pueden elegir todas las del tablero solo existe un patrón posible
      if game.budget == 0 then new AIStrategy(Nil)
      else if game.budget.toLong == game.rows.toLong * game.columns then
        new AIStrategy(posiciones)
      else {
        // Para evitar posiciones repetidas mezclamos una lista de casillas distintas y tomamos tantas casillas como celulas haya disponibles para elegir
        def crearPatron(): List[Position] = Random.shuffle(posiciones).take(game.budget)
        // La puntuación usa las mismas reglas y el mismo límite de generaciones que la partida
        def evaluar(patron: List[Position]): (Int, Int) =
          game.simulate(GameOfLifeMatrix.withAlive(game.rows, game.columns, patron))

        val primero = crearPatron()
        // Guardamos solo el mejor patrón y su puntuación, sin almacenar todos los intentos
        @annotation.tailrec
        def buscar(restantes: Int, mejor: List[Position], resultado: (Int, Int)): List[Position] = {
          if restantes == 0 then mejor
          else {
            val candidato = crearPatron()
            val puntuacion = evaluar(candidato)
            // Primero comparamos el numero de generaiones y despues el numero de células vivas
            if puntuacion._1 > resultado._1 ||
               (puntuacion._1 == resultado._1 && puntuacion._2 > resultado._2) then
              buscar(restantes - 1, candidato, puntuacion)
            else buscar(restantes - 1, mejor, resultado)
          }
        }

        // El primer patrón ya cuenta como un intento
        new AIStrategy(buscar(attempts - 1, primero, evaluar(primero)))
      }
    }
