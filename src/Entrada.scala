package gameoflife

import scala.io.StdIn
import scala.util.Try

// Este objeto maneja todos los casos de lectura y validacion para simplificar la gestion de los efectos dentro de los demas archivos
object Entrada:
  // Esta primera funcion distingue entre si ha ocurrido un fallo de lectura, un cierre o si hay texto disponible
  def leerLinea(prompt: String): Try[Option[String]] =
    Try(Option(StdIn.readLine(prompt)))

  // Todas estas funciones trabaja directamente con texto ya leido de la funcion anterior y verifican si es una entrada valida o no
  def validarEntero(texto: String): Either[String, Int] =
    // Deuelve Right o Left con el numero o el mensaje de error
    Try(texto.trim.toInt).toEither match
      case Right(numero) => Right(numero)
      case Left(_) => Left(s"'$texto' no es un número entero válido.")

  def validarPosicion(texto: String): Either[String, Position] =
    texto.trim.split(" ").toList match
      case filaTexto :: columnaTexto :: Nil =>
        // primero convertimos la fila 
        validarEntero(filaTexto) match
          case Left(error) => Left(error)
          case Right(fila) =>
            // si funciona intentamos convertir la columna
            validarEntero(columnaTexto) match
              case Left(error) => Left(error)
              case Right(columna) => Right(Position(fila, columna))
      case _ => Left("Formato incorrecto: introduce fila y columna separadas por espacios.")
    // El resto de excepciones se comprueban después en Game.placeCell

  def validarPresupuesto(texto: String, min: Int, max: Int): Either[String, Int] =
    // Comoprobamos que es un entero dentro del rango
    validarEntero(texto) match
      case Left(error) => Left(error)
      case Right(numero) =>
        if numero >= min && numero <= max then Right(numero)
        else Left(s"Introduce un número entero entre $min y $max.")

  def parsearSiNo(texto: String): Either[String, Boolean] =
    // quitamos espacios y pasamos todo a minusculas para aceptar varias entradas distintas
    texto.trim.toLowerCase match
      case "s" | "si" | "sí" => Right(true)
      case "n" | "no" => Right(false)
      case _ => Left("Responde s (sí) o n (no).")

  // igual que el anterior pero con h / ia
  def parsearHumanoOIA(texto: String): Either[String, Boolean] =
    texto.trim.toLowerCase match
      case "h" | "humano" => Right(true)
      case "ia" => Right(false)
      case _ => Left("Responde h (humano) o ia.")
