package gameoflife

class Matrix[T](val rows: Int, val columns: Int, val data: List[T]):
  require(rows >= 0 && columns >= 0 && data.length == rows * columns,
    s"Una matriz $rows x $columns necesita ${rows * columns} datos (hay ${data.length})")

  private def index(p: Position): Int = p.row * columns + p.column

  private def positionOf(i: Int): Position = Position(i / columns, i % columns)

  def inside(p: Position): Boolean = { // comprobamos si la posicion esta dentro de rango y devolvemos true

    p.row >= 0 && p.row < rows && p.column >= 0 && p.column < columns
  }

  def get(p: Position): T =
    if !inside(p) then
      throw new IndexOutOfBoundsException(p.toString)

    @scala.annotation.tailrec
    def aux(resto: List[T], contador: Int): T =
      resto match
        case x :: y =>
          if contador == index(p) then x
          else aux(y, contador + 1)
        case Nil =>
          throw new IndexOutOfBoundsException(p.toString)

    aux(data, 0)


  def update(p: Position, value: T): Matrix[T] =
    if !inside(p) then throw new IndexOutOfBoundsException
    else if get(p) == value then Matrix[T](rows, columns, data) //para no tener que recorrer toda la lista si el valor es el mismo
    else
      val indice: Int = index(p)
      @annotation.tailrec
      def cambiarValor( lista: List[T], nueva: List[T], contador: Int): List[T] =
        lista match {
          case Nil => nueva.reverse
          case x :: y =>
            if indice == contador then cambiarValor( y, value :: nueva, contador + 1)
            else cambiarValor(y, x :: nueva, contador + 1)

        }
      val nuevaData: List[T] = cambiarValor(data, List.empty[T], 0)
      Matrix[T](rows, columns, nuevaData)



  def neighbors(p: Position): List[Position] = // obtenemos todas las posiciones posibles y comprobamos cuáles están en la matriz
    val lista : List[Position] = List(Position(p.row - 1, p.column - 1), Position(p.row - 1, p.column), Position(p.row - 1, p.column + 1), Position(p.row,     p.column - 1), Position(p.row,     p.column + 1), Position(p.row + 1, p.column - 1), Position(p.row + 1, p.column), Position(p.row + 1, p.column + 1))
    val nueva: List[Position] = List.empty[Position]
    @scala.annotation.tailrec
    def aux(posiciones: List[Position], acc: List[Position]): List[Position]=

      posiciones match {
        case Nil => acc
        case posicion :: resto =>
          if inside(posicion) then aux(resto, posicion :: acc)
          else aux(resto, acc)
          }
    aux(lista, nueva)

  def partition(f: T => Boolean): (List[Position], List[Position]) =
    @annotation.tailrec
    def aux(lista: List[T],indice: Int, cumplen: List[Position], incumplen: List[Position]): (List[Position], List[Position]) =
      lista match
        case Nil => (cumplen.reverse, incumplen.reverse)
        case x :: resto =>
          val posicion = positionOf(indice)
          if f(x) then
            aux(resto, indice + 1, posicion :: cumplen, incumplen)
          else
            aux(resto, indice + 1, cumplen, posicion :: incumplen)

    aux(data, 0, List.empty[Position], List.empty[Position])


  def filterNeighbors(p: Position, f: T => Boolean): List[Position] =
    val vecinas: List[Position] = neighbors(p)
    val (cumplen, _) : (List[Position], List[Position]) = partition(f)
    @scala.annotation.tailrec
    def aux(vecinas: List[Position], vecinas_cumplen: List[Position] ): List[Position] =
      vecinas match {
        case Nil => vecinas_cumplen
        case posicion :: resto =>
          if cumplen.contains(posicion) then aux(resto, posicion :: vecinas_cumplen)
          else aux(resto, vecinas_cumplen)
      }
    aux(vecinas, List.empty[Position])


object Matrix:

  def apply[T](rows: Int, columns: Int, data: List[T]): Matrix[T] =
    new Matrix(rows, columns, data)

  def apply[T](rows: Int, columns: Int, defaultValue: T): Matrix[T] =
    @scala.annotation.tailrec
    def aux(restantes: Int, acc: List[T]) : List[T] =
      if restantes == 0 then acc
      else aux(restantes - 1, defaultValue :: acc)
    
    new Matrix(rows, columns, aux(rows * columns, List.empty[T])) // creamos la matriz con una lista del valor por defecto
      


  def apply[T](rows: Int, columns: Int, defaultValue: T, marked: List[Position], markedValue: T): Matrix[T] =
    val inicial = Matrix[T](rows, columns, defaultValue)
    @annotation.tailrec
    def aux(pendientes: List[Position], actual: Matrix[T]): Matrix[T] =
      pendientes match
        case Nil => actual
        case posicion :: resto =>
          aux(resto, actual.update(posicion, markedValue))

    aux(marked, inicial)
