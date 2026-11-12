package gameoflife

case class GameOfLifeMatrixList(override val rows: Int, override val columns: Int, override val data: List[Cell])
    extends MatrixList[Cell](rows, columns, data):

    override def update(p: Position, value: Cell): GameOfLifeMatrixList = // usamos el metodo update de la clase padre y para conseguir la nueva lista de datos
        val actualizada = super.update(p, value)
        GameOfLifeMatrixList(rows, columns, actualizada.data)

    def aliveCells: List[Position] =
        val (vivas, _) = partition(celda => celda == Alive) // usamos partition de la clase padre para ver qué celdas cumplen una función
        vivas


    def aliveCount: Int = aliveCells.length // equivale a la longitud de la lista de celulas vivas

    def countAliveNeighbors(p: Position): Int =
        val vecinas_vivas = filterNeighbors(p, celda => celda == Alive) // creamos una lista con las vecinas vivas y calculamos su longitud
        vecinas_vivas.length // devolvemos la longitud de esta lista


    def nextGeneration: GameOfLifeMatrixList = // este metodo genera el nuevo tablero en cada generacion segun las reglas del juego
        val nuevaData = cells.map { (posicion, estado) =>
            val vecinas_vivas = countAliveNeighbors(posicion) // obtenemos el numero de vecinas vivas para aplicar las reglas
            if estado == Alive then // Para celula viva
                if vecinas_vivas == 2 || vecinas_vivas == 3 then (estado) // si el numero de vecinas vivas es dos o tres se mantiene viva
                else (Dead) // si no, se añade una celula muerta en su lugar
            else // Para celula muerta
                if vecinas_vivas == 3 then (Alive) // si el numero de vecinas vivas es tres, esta nace, se añade una celula alive en su lugar a la nueva lista
                else (estado) // si no, se añade la celula actual a la nueva lista

        }
        GameOfLifeMatrixList(rows, columns, nuevaData)



    def toMatrix: GameOfLifeMatrix = GameOfLifeMatrix(rows, columns, data)
    override def toString: String = toMatrix.show()

object GameOfLifeMatrixList:
    def empty(rows: Int, columns: Int): GameOfLifeMatrixList = // usamos el constructor para un valor por defecto de la clase padre
        val matriz = Matrix[Cell](rows, columns, Dead)
        GameOfLifeMatrixList(rows, columns, matriz.data)

    def withAlive(rows: Int, columns: Int, alive: List[Position]): GameOfLifeMatrixList = // usamos el constructor par valores marcados de la clase padre
        val matriz = Matrix[Cell](rows, columns, Dead, alive, Alive)
        GameOfLifeMatrixList(rows, columns, matriz.data)
