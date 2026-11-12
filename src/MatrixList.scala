package gameoflife

class MatrixList[T](val rows: Int, val columns: Int, val data: List[T]):
  require(rows >= 0 && columns >= 0 && data.length == rows * columns,
    s"Una matriz $rows x $columns necesita ${rows * columns} datos (hay ${data.length})")

  private def index(p: Position): Int = p.row * columns + p.column
  
  private def positionOf(i: Int): Position = Position(i / columns, i % columns)

  def inside(p: Position): Boolean =
    p.row >= 0 && p.row < rows && p.column >= 0 && p.column < columns // mismo metodo que en matrix

  def get(p: Position): T =
    if !inside(p) then throw new IndexOutOfBoundsException // si no esta dentro se lanza la excepcion
    data(index(p)) // accedemos a la posicion indicada en la lista data para obtener el valor correspondiente


  def update(p: Position, value: T): MatrixList[T] = {
    if !inside(p) then throw new IndexOutOfBoundsException // si no esta dentro se lanza la excepcion
    val nueva_data = data.zipWithIndex.map { // recorremos con map una lista de tuplas con los valores y los indices que les corresponden y coinciden con index
      case(valor, indice) =>
        if indice == index(p) then value // si el indice corresponde a la posicion el valor se sustituye por el indicado en update, si no se mantiene igual
        else valor
    }
    MatrixList(rows, columns, nueva_data) // devolvemos la nueva MatrixList con mismas filas y columnas pero una lista de valores actualizada
  }

  def neighbors(p: Position): List[Position] = // esta funcion busca encontrar las celdas vecinas dentro del tablero
    val lista: List[Position] = List(Position(p.row - 1, p.column - 1), Position(p.row - 1, p.column), Position(p.row - 1, p.column + 1), Position(p.row, p.column - 1), Position(p.row, p.column + 1), Position(p.row + 1, p.column - 1), Position(p.row + 1, p.column), Position(p.row + 1, p.column + 1))
    // lista de las posibles celdas vecinas, dentro o fuera del tablero
    lista.filter(inside)  // nos quedamos con las que estén dentro


  def partition(f: T => Boolean): (List[Position], List[Position]) =
    val conIndice = data.zipWithIndex // lista de valores con su indice
    // nos quedamos con los valores que cumplen la condicion (y en otra lista con las que no la cumplen) y despues sustituimos la tupla (valor, indice) por la posicion correspondiente a esa celda
    val cumplen = conIndice.filter((valor, indice) => f(valor)).map{(valor, indice) => positionOf(indice)}
    val incumplen = conIndice.filter((valor, indice) => !f(valor)).map{(valor, indice) => positionOf(indice)}
    (cumplen, incumplen)


  def filterNeighbors(p: Position, f: T => Boolean): List[Position] =
    val vecinos = neighbors(p) // obtenemos la lista de vecinos
    vecinos.filter(p => f(get(p))) // nos quedamos con los vecinos cuyo valor (obtenido con get(p)) cumple la funcion



  def cells: List[(Position, T)] = // este metodo devuelve una posicion emparejada con su valor, en orden de filas
    val conIndice = data.zipWithIndex // obtenemos una lista de valores asociados a su indice
    conIndice.map((valor, indice) => (positionOf(indice), valor)) // como index ya sigue el orden por filas, solo hay que obtener la posicion asociada a un indice con positionOf(indice)

object MatrixList:
  def apply[T](rows: Int, columns: Int, data: List[T]): MatrixList[T] =
    new MatrixList(rows, columns, data)
  def apply[T](rows: Int, columns: Int, defaultValue: T): MatrixList[T] =
    new MatrixList(rows, columns, List.fill(rows * columns)(defaultValue))
  def apply[T](rows: Int, columns: Int, defaultValue: T, marked: List[Position], markedValue: T): MatrixList[T] =
    new MatrixList(rows, columns,
      List.tabulate(rows * columns)(i =>
        if marked.contains(Position(i / columns, i % columns)) then markedValue else defaultValue))
