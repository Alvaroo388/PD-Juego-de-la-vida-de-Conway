package gameoflife

class Matrix[T](val rows: Int, val columns: Int, val data: List[T]):
  require(rows >= 0 && columns >= 0 && data.length == rows * columns,
    s"Una matriz $rows x $columns necesita ${rows * columns} datos (hay ${data.length})")

  private def index(p: Position): Int = p.row * columns + p.column

  private def positionOf(i: Int): Position = Position(i / columns, i % columns)

  def inside(p: Position): Boolean = { // comprobamos si la posicion esta dentro de rango y devolvemos true

    p.row >= 0 && p.row < rows && p.column >= 0 && p.column < columns
  }

  def get(p: Position): T = // obtiene el valor asociado a una posicion
    if !inside(p) then // comprobamos que la posicions sea valida
      throw new IndexOutOfBoundsException

    @scala.annotation.tailrec
    def aux(resto: List[T], contador: Int): T =
      resto match
        case x :: y =>
          if contador == index(p) then x // cuando el contador coincide con el indice buscado, significa que esa es la celda buscad y se extrae el valor
          else aux(y, contador + 1)
        case Nil =>
          throw new IndexOutOfBoundsException

    aux(data, 0)


  def update(p: Position, value: T): Matrix[T] = // esta funcion busca actualizar el valor de una celda
    if !inside(p) then throw new IndexOutOfBoundsException // comprobamos que la posicion sea valida
    else if get(p) == value then Matrix[T](rows, columns, data) //para no tener que recorrer toda la lista si el valor es el mismo
    else
      val indice: Int = index(p)
      @annotation.tailrec
      def cambiarValor( lista: List[T], nueva: List[T], contador: Int): List[T] =
        lista match {
          case Nil => nueva.reverse
          case x :: y =>
            if indice == contador then cambiarValor( y, value :: nueva, contador + 1) // el contador aumenta en cada llamada y la lista a recorrer disminuye, cuando el contador coincide con el indice buscado, se añade el nuevo valor. En el resto de casos se dej aigual
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
        case Nil => acc // devolvemos la lista final (el orden no importa asi que no hace falta invertir)
        case posicion :: resto =>
          if inside(posicion) then aux(resto, posicion :: acc) // si la posicion es valida (esta dentro del tablero), se añade a la lista final
          else aux(resto, acc)
          }
    aux(lista, nueva) 

  def partition(f: T => Boolean): (List[Position], List[Position]) =
    @annotation.tailrec
    def aux(lista: List[T],indice: Int, cumplen: List[Position], incumplen: List[Position]): (List[Position], List[Position]) =
      lista match
        case Nil => (cumplen.reverse, incumplen.reverse) // devolvemos las dos listas invertidas
        case x :: resto =>
          val posicion = positionOf(indice) 
          if f(x) then
            aux(resto, indice + 1, posicion :: cumplen, incumplen) // cuando se cumple la funcion la posicion se añade a cumplen y el indice aumenta
          else
            aux(resto, indice + 1, cumplen, posicion :: incumplen) // cuando se incumple la funcion la posicion se añade a incumplen y el indice aumenta

    aux(data, 0, List.empty[Position], List.empty[Position]) // llamada a la funcion auxiliar con data com olista de valores, el indice a 0 y cumplen e incumplen vacías


  def filterNeighbors(p: Position, f: T => Boolean): List[Position] = // el objetivo de esta funcion es quedarnos con las celdas vecinas que cumplan una funcion
    val vecinas: List[Position] = neighbors(p)
    val (cumplen, _) : (List[Position], List[Position]) = partition(f) // nos quedamos con cumplen usando partition, no hace falta guardar incumplen
    @scala.annotation.tailrec
    def aux(vecinas: List[Position], vecinas_cumplen: List[Position] ): List[Position] =
      vecinas match {
        case Nil => vecinas_cumplen // devolvemos la lista de vecinas que cumplen la condicion
        case posicion :: resto =>
          if cumplen.contains(posicion) then aux(resto, posicion :: vecinas_cumplen) // comprobamos si la posicion esta en la lista cumplen, en ese caso la añadimos a las vecinas que cumplen la condicion
          else aux(resto, vecinas_cumplen) // si no esta en cumplen significa que incumple la condicion, por lo que se ignora esta posicion
      }
    aux(vecinas, List.empty[Position])


object Matrix:

  def apply[T](rows: Int, columns: Int, data: List[T]): Matrix[T] =
    new Matrix(rows, columns, data)

  def apply[T](rows: Int, columns: Int, defaultValue: T): Matrix[T] = // un constructor que crea la matriz con un valor por defecto en todas las celdas
    require(rows >= 0 && columns >= 0, "Las dimensiones no pueden ser negativas")
    @scala.annotation.tailrec
    def aux(restantes: Int, acc: List[T]) : List[T] =
      if restantes == 0 then acc
      else aux(restantes - 1, defaultValue :: acc) // por cada celda se añade una vez el valor por defecto a la lista de valores
    
    new Matrix(rows, columns, aux(rows * columns, List.empty[T])) // creamos la matriz con una lista del valor por defecto
      


  def apply[T](rows: Int, columns: Int, defaultValue: T, marked: List[Position], markedValue: T): Matrix[T] = // constructor que crea la matriz con valores por defecto y un valor especial en ciertas celdas
    val inicial = Matrix[T](rows, columns, defaultValue)  // creamos la matriz base con los valores por defecto usando el constructor anterior
    @annotation.tailrec
    def aux(pendientes: List[Position], actual: Matrix[T]): Matrix[T] =
      pendientes match
        case Nil => actual
        case posicion :: resto =>
          aux(resto, actual.update(posicion, markedValue)) // por cada celda en la lista de marcadas se crear una nueva matrix actualizada usando el metodo update

    aux(marked, inicial) // llamamos a la funcion auxiliar con la lista de celdas marcadas y la matrix inicial con los valores por defecto
