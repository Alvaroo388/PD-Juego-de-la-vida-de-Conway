package gameoflife

class Matrix[T](val rows: Int, val columns: Int, val data: List[T]):
  require(rows >= 0 && columns >= 0 && data.length == rows * columns,
    s"Una matriz $rows x $columns necesita ${rows * columns} datos (hay ${data.length})")

  private def index(p: Position): Int = p.row * columns + p.column

  private def positionOf(i: Int): Position = Position(i / columns, i % columns)

  def inside(p: Position): Boolean = // comprobamos si la posicion esta dentro de rango y devolvemos true
    p.row < rows && p.column < columns // no hace falta comprobar que son positivos porque la matrix lo requiere

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
  def update(p: Position, value: T): Matrix[T] = ??? //TODO

  def neighbors(p: Position): List[Position] = ??? //TODO

  def partition(f: T => Boolean): (List[Position], List[Position]) = ??? //TODO

  def filterNeighbors(p: Position, f: T => Boolean): List[Position] = ??? //TODO
object Matrix:

  def apply[T](rows: Int, columns: Int, data: List[T]): Matrix[T] =
    new Matrix(rows, columns, data)

  def apply[T](rows: Int, columns: Int, defaultValue: T): Matrix[T] = ??? //TODO

  def apply[T](rows: Int, columns: Int, defaultValue: T, marked: List[Position], markedValue: T): Matrix[T] = ??? //TODO
