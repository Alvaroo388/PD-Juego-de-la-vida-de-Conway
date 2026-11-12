package gameoflife

case class GameOfLifeMatrix(override val rows: Int, override val columns: Int, override val data: List[Cell])
    extends Matrix[Cell](rows, columns, data):

    override def update(p: Position, value: Cell): GameOfLifeMatrix = // usamos el metodo update de la clase padre y para conseguir la nueva lista de datos
      val actualizada = super.update(p, value)
      GameOfLifeMatrix(rows, columns, actualizada.data)


    def aliveCells: List[Position] =
      val (vivas, _) = partition(celda => celda == Alive) // usamos partition de la clase padre para ver qué celdas cumplen una función
      vivas

    def aliveCount: Int = aliveCells.length // longitud de la lista de las células vivas

    def countAliveNeighbors(p: Position): Int =
      val vecinas_vivas = filterNeighbors(p, celda => celda == Alive) // creamos una lista con las vecinas vivas y calculamos su longitud
      vecinas_vivas.length
    

    def nextGeneration: GameOfLifeMatrix =
      val(posiciones, _) = partition(_ => true) // partition con la condicion true devuelve la lista de todas las posiciones
      @scala.annotation.tailrec
      def aux(restante: List[Position], acc: List[Cell]): List[Cell] =
        restante match {
          case Nil => acc.reverse 
          case posicion :: resto => 
            val actual = get(posicion) // obtenemos el valor asociado a la posicion
            val vecinas_vivas = countAliveNeighbors(posicion) // obtenemos el numero de vecinas vivas para aplicar las reglas
            if actual == Alive then // Para celula viva
              if vecinas_vivas == 2 || vecinas_vivas == 3 then aux(resto, actual::acc) // si el numero de vecinas vivas es dos o tres se mantiene viva
              else aux(resto, Dead :: acc) // si no, se añade una celula muerta en su lugar a la nueva lista de celulas
            else // Para celula muerta
              if vecinas_vivas == 3 then aux(resto, Alive :: acc) // si el numero de vecinas vivas es tres, esta nace, se añade una celula alive en su lugar a la nueva lista
              else aux(resto, actual :: acc)   // si no, se añade la celula actual a la nueva lista
                      
                     
        }
      val nuevaData = aux(posiciones, List.empty[Cell]) // usamos el metodo auxiliar para obtener la nueva lista de datos
      new GameOfLifeMatrix(rows, columns, nuevaData) // creamos el nuevo tablero mateniendo filas y columnas con la nueva lista de datos
    
    
    

    //¡No tocar!
    def show(title: String = ""): String =
      val reset  = "\u001b[0m"
      val green  = "\u001b[92m"
      val dark   = "\u001b[38;5;237m"
      val dim    = "\u001b[90m"
      val frame  = "\u001b[38;5;67m"
      val yellow = "\u001b[93m"
      def square(c: Cell): String = c match
        case Alive => s"$green██$reset"
        case Dead  => s"$dark██$reset"
      val titleLine = if title.isEmpty then Nil else List(s"  $yellow$title$reset")
      val header    = s"$dim      " + List.range(0, columns).map(c => f"$c%2d").mkString(" ") + reset
      val top       = s"$frame    ┌" + "─" * (3 * columns + 1) + s"┐$reset"
      val bottom    = s"$frame    └" + "─" * (3 * columns + 1) + s"┘$reset"
      val lines     = data.grouped(columns).toList.zipWithIndex.map: (row, r) =>
        s"$dim ${f"$r%2d"} $reset$frame│$reset " + row.map(c => square(c) + " ").mkString + s"$frame│$reset"
      (titleLine ++ List(header, top) ++ lines ++ List(bottom)).mkString("\n")

    override def toString: String = show()

object GameOfLifeMatrix:
    def empty(rows: Int, columns: Int): GameOfLifeMatrix = // usamos el constructor para un valor por defecto de la clase padre
      val matriz = Matrix[Cell](rows, columns, Dead)
      GameOfLifeMatrix(rows, columns, matriz.data)

    def withAlive(rows: Int, columns: Int, alive: List[Position]): GameOfLifeMatrix = // usamos el constructor par valores marcados de la clase padre
      val matriz = Matrix[Cell](rows, columns, Dead, alive, Alive)
      GameOfLifeMatrix(rows, columns, matriz.data)
