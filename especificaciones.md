# Especificaciones de la práctica: El Juego de la Vida

> Resumen estructurado de [`Juego de la Vida de Conway.pdf`](Juego%20de%20la%20Vida%20de%20Conway.pdf), el enunciado original de 15 páginas. **Si hay alguna discrepancia, prevalece el PDF.** Este archivo sirve como referencia rápida para implementar y revisar la práctica.

## 1. Objetivo y reglas de la partida

La práctica convierte el Juego de la Vida de Conway en una competición de dos jugadores. Cada uno tiene un tablero **independiente** del mismo tamaño. En la fase de colocación se alternan turnos; en cada turno se pone **una** célula viva en una casilla libre del tablero propio. Cada jugador dispone de un presupuesto máximo de células. La colocación termina cuando ambos lo han agotado.

Después, cada tablero evoluciona por separado, generación a generación, hasta repetir cualquier estado anterior de esa misma simulación o alcanzar el límite de generaciones. Una repetición incluye un estado fijo, un tablero vacío o un ciclo de cualquier longitud. Gana quien **más generaciones** tarda en detenerse; si empatan, quien tiene **más células vivas en el estado final**; si persiste el empate, no hay ganador.

Las reglas clásicas **B3/S23** se aplican simultáneamente a todas las celdas:

| Estado actual | Vecinas vivas | Estado siguiente |
| --- | --- | --- |
| Viva | 2 o 3 | Viva |
| Viva | Menos de 2 o más de 3 | Muerta |
| Muerta | Exactamente 3 | Viva |
| Muerta | Cualquier otro número | Muerta |

Se cuentan las ocho vecinas horizontales, verticales y diagonales. El tablero es finito y **las posiciones exteriores siempre cuentan como muertas**: no se envuelve por los bordes.

En el programa principal, el tablero es siempre **15 × 15** y se simulan como máximo **1000 generaciones**. La clase `Game` acepta otras dimensiones y límites para facilitar las pruebas.

Patrones útiles para comprobar el comportamiento: bloque (estable), intermitente o *blinker* (período 2), planeador o *glider* (se desplaza una casilla en diagonal cada cuatro generaciones). El PDF también ilustra otras vidas estáticas, osciladores y naves.

## 2. Organización y reglas de programación

- Todos los archivos Scala empiezan con `package gameoflife`. Se deben **conservar exactamente los nombres y las firmas** de la plantilla para que compilen las pruebas.
- Emplear datos inmutables, funciones puras para la lógica, recursión en lugar de bucles con variables mutables y `Either`/`Option` para los errores previstos. Las estrategias y `Main` se ocupan de la entrada y salida.
- `Matrix[T]` es una clase genérica reutilizable; `GameOfLifeMatrix` es una `case class` que hereda de `Matrix[Cell]`. Los tableros iguales por contenido deben poder compararse y almacenarse en un `Set`.
- **En `Matrix.scala` y `GameOfLifeMatrix.scala`:** resolver el trabajo con recursión y *pattern matching* sobre `Nil` y `head :: tail`; no usar métodos de orden superior de `List` (`map`, `filter`, `count`, `partition`, `foldLeft`, `flatMap`, `zip`, etc.) ni acceso por índice `data(i)`. Se permiten `::`, `reverse`, `++`, `length` y `contains`. En el resto de la práctica sí se pueden usar métodos de orden superior.
- Para las funciones que deben ser recursivas de cola, usar `@annotation.tailrec` para que el compilador compruebe esa propiedad.
- `Cell.scala`, `Position.scala`, `State.scala` y `GameResult.scala` contienen principalmente los tipos de datos ya dados. **No modificar** el dibujo `show` ni `Animation.scala`.

### Archivos de la plantilla

| Archivo | Responsabilidad |
| --- | --- |
| `Cell.scala` | `sealed trait Cell` y los `case object` `Alive`, `Dead`; ya definido. |
| `Position.scala` | `case class Position(row: Int, column: Int)`; coordenadas desde cero; ya definida. |
| `Matrix.scala` | Matriz genérica y sus constructores; 2 puntos. |
| `GameOfLifeMatrix.scala` | Tablero y evolución de Conway; 1 punto. |
| `Player.scala`, `State.scala` | Jugadores y estado inmutable de colocación; 0,5 puntos. |
| `Strategy.scala` | Estrategia manual obligatoria; 1 punto. IA opcional en el mismo archivo. |
| `Game.scala` | Colocación (2 puntos), simulación, ganador y partida completa (1 punto). |
| `Main.scala` | Entrada, configuración, resultado y opción de animación; 0,5 puntos. |
| `Animation.scala` | Animación ya implementada; no tocar. |
| `MatrixList.scala`, `GameOfLifeMatrixList.scala` | Versiones opcionales con métodos de `List`; 0,5 puntos. |

## 3. `Matrix[T]` — 2 puntos

Los datos se almacenan en **una única `List[T]` en orden de filas**. La posición `(r, c)` corresponde al índice `r * columns + c`. El constructor de `Matrix` ya comprueba que `data.length == rows * columns`; también están hechos los métodos privados `index`, `positionOf` y el `apply` que recibe directamente `data`. `Matrix` no es una `case class`: para comparar resultados se comparan sus listas `data`.

| Elemento pendiente | Contrato |
| --- | --- |
| `Matrix(rows, columns, defaultValue)` | Crear una matriz del tamaño indicado con ese valor en todas las celdas. |
| `Matrix(rows, columns, defaultValue, marked, markedValue)` | Usar el valor por defecto, salvo en las posiciones de `marked`, que llevan `markedValue`. |
| `inside(p)` | Indicar si la fila y la columna están dentro de sus respectivos rangos. |
| `get(p)` | Devolver el valor de `p`; lanzar `IndexOutOfBoundsException` si está fuera. |
| `update(p, value)` | Devolver una matriz nueva del mismo tamaño con solo esa celda cambiada; lanzar `IndexOutOfBoundsException` si está fuera. La original no cambia. |
| `partition(f)` | Devolver dos listas de posiciones, primero las que cumplen `f` y después las que no, **ambas en orden de filas**. |
| `neighbors(p)` | Devolver vecinas válidas, sin repetir ni incluir `p`; el orden no importa. En esquina hay 3, en borde 5, en interior 8 y en una matriz 1 × 1 ninguna. |
| `filterNeighbors(p, f)` | Devolver las vecinas cuyo valor cumple `f`; no contar la propia celda. El orden no importa. |

Los **dos constructores pendientes, `get`, `update` y `partition`** deben ser recursivos de cola. La matriz genérica debe funcionar también con **300 × 300 = 90 000 celdas**. Casos de referencia: `Matrix(2, 3, 0, List(Position(0, 2), Position(1, 0)), 7).data` produce `List(0, 0, 7, 7, 0, 0)`; en una matriz de 2 × 3, `(0, 3)` está fuera aunque su índice lineal coincida con otra celda.

## 4. `GameOfLifeMatrix` — 1 punto

Es `case class GameOfLifeMatrix(override val rows, override val columns, override val data)` y extiende `Matrix[Cell]`. `show(title)` y `toString` ya están implementados. Métodos pendientes:

| Método | Contrato |
| --- | --- |
| `empty(rows, columns)` | Tablero con todas las células `Dead`. |
| `withAlive(rows, columns, alive)` | Tablero con vivas exactamente las posiciones indicadas. |
| `update(p, value)` | Misma operación inmutable heredada, pero devuelve `GameOfLifeMatrix`. |
| `aliveCells` | Posiciones de células vivas en orden de filas. |
| `aliveCount` | Número de células vivas. |
| `countAliveNeighbors(p)` | Número de vecinas vivas, esté `p` viva o muerta. |
| `nextGeneration` | Nuevo tablero aplicando B3/S23 a todas las celdas **a partir del tablero actual**, sin modificaciones parciales del estado usado para calcularlo. |

Casos de comprobación: una célula aislada muere; el bloque permanece igual; el intermitente alterna horizontal y vertical; una L de tres células se transforma en un bloque; el planeador avanza diagonalmente cada cuatro generaciones. Dos `GameOfLifeMatrix` con el mismo contenido son iguales.

## 5. Jugadores, estado y estrategias

`Player` es un `enum` con `Player1` y `Player2`. Completar `otherPlayer(p)` y `playerName(p)`, que devuelve `"Jugador 1"` o `"Jugador 2"`. `State` ya tiene `boards: Map[Player, GameOfLifeMatrix]`, `remainingCells: Map[Player, Int]` y `turn: Player`; debe tratarse como inmutable y producir estados nuevos mediante `copy` y `Map.updated`.

`Strategy` declara `choosePosition(state: State, player: Player, error: Option[String]): Position`. La estrategia **no valida la jugada**: eso corresponde a `Game.placeCell`.

### `ManualStrategy` — parte obligatoria de estrategias, 1 punto

- Si `error` es `Some(mensaje)`, mostrarlo.
- Mostrar con `show` el tablero del jugador, indicando turno y células restantes.
- Leer con `StdIn.readLine` una línea `fila columna` y convertirla en `Position`.
- Si el formato no son dos enteros, avisar y volver a leer **recursivamente**, sin devolver todavía una posición.
- Si `readLine` devuelve `null` porque se cerró la entrada, la partida termina.
- Una posición bien escrita pero fuera del tablero, o ya ocupada, se devuelve: `placeCell` la rechazará y `placementLoop` volverá a llamar a la estrategia con `Some(mensaje)`.

## 6. `Game`: colocación — 2 puntos

`Game(rows, columns, budget, maxGenerations)` contiene la configuración y la lógica. Sus métodos no deben leer del teclado ni imprimir resultados. En el programa principal se usará `Game(15, 15, presupuesto, 1000)`, aunque las pruebas usan tableros menores.

| Método | Contrato |
| --- | --- |
| `initialState` | Dos tableros vacíos, `budget` células disponibles para cada jugador y turno inicial de `Player1`. |
| `hasFinished(state, player)` | Verdadero si al jugador no le quedan células por colocar. |
| `placeCell(state, p): Either[String, State]` | `Left(mensaje)` si el jugador de turno ya terminó, `p` está fuera o la casilla propia ya está viva. Si es válida, `Right(nuevoEstado)` con la célula colocada, presupuesto reducido y turno del rival, salvo que este ya haya terminado. El tablero rival no cambia. |
| `placementLoop(state, strategies): State` | Bucle **recursivo de cola** hasta que ambos agoten el presupuesto. Nunca preguntar a quien ya terminó. Tras un `Left`, volver a preguntar al **mismo jugador** pasando `Some(mensaje)`; de ordinario, pasar `None`. Si `budget == 0`, terminar sin preguntar. |

Los dos jugadores pueden usar la misma coordenada porque tienen tableros distintos. `placeCell` comprueba las reglas sin depender del teclado, para poder probarlo de forma aislada.

## 7. `Game`: simulación, ganador y partida — 1 punto

- `simulate(board): (Int, Int)` debe ser **recursiva de cola**. El tablero inicial es la **generación 0**. Avanza hasta que un tablero ya haya aparecido en esta simulación o se alcance `maxGenerations`. Devuelve el número de la generación de parada y las células vivas en ese estado.
- Casos especificados: bloque → `(1, 4)`; tablero vacío → `(1, 0)`; intermitente → `(2, 3)`; una célula aislada → `(2, 0)`; planeador con límite de 3 → `(3, 5)`.
- `determineWinner(r1, r2): Option[Player]` compara primero generaciones y, si empatan, células vivas. Devuelve `None` si también empatan estas. Ejemplos: `(10, 3)` vence a `(7, 50)`; `(5, 8)` vence a `(5, 4)`.
- `play(strategies): GameResult` ejecuta la colocación desde `initialState`, simula ambos tableros y determina el ganador. `GameResult` conserva `finalState` (los patrones **iniciales** al terminar la colocación), `results: Map[Player, (Int, Int)]` y `winner`.
- Ejemplo del enunciado: en 5 × 5 y con 4 células por jugador, un bloque obtiene `(1, 4)`; cuatro células aisladas en las esquinas obtienen `(2, 0)`. Ganan las esquinas por durar una generación más, aunque acaben sin células vivas.

## 8. `Main` y animación — 0,5 puntos

`Main` extiende `App` y contiene las constantes `BoardSize = 15` y `MaxGenerations = 1000` de la plantilla. Debe:

1. Pedir un presupuesto entero entre **1 y 225**, repitiendo la pregunta si es inválido.
2. Permitir elegir para cada jugador una estrategia humana o, si se hizo la parte opcional, de IA.
3. Jugar mediante `game.play` y mostrar con `show` los dos patrones iniciales.
4. Mostrar para cada jugador las generaciones y células vivas finales, y explicar si el ganador se decidió por generaciones, por células vivas o si hubo empate.
5. Ofrecer la animación con `Animation.animate(result)`.

`Animation.scala` ya está resuelto. Usa `nextGeneration` para dibujar ambos tableros y detiene cada uno en las generaciones indicadas por `simulate`. Para animarla correctamente en la ventana *Run* de IntelliJ hay que activar **Emulate terminal in output console**; también funciona en terminales compatibles con ANSI.

## 9. Apartados opcionales

### Matrices con métodos de `List` — 0,5 puntos

Completar `MatrixList[T]` y `GameOfLifeMatrixList` con los mismos contratos que las versiones recursivas. Los constructores ya están implementados. `MatrixList.cells` devuelve `(Position, valor)` en orden de filas. Aquí sí se pueden usar `map`, `filter`, `partition`, `flatMap`, `zipWithIndex`, acceso por índice, etc. `MatrixListTests` compara ambas implementaciones. En la memoria, comparar longitud y facilidad de razonamiento de las dos versiones.

### IA — 1 punto

`AIStrategy(chosenPattern)` contiene **exactamente `budget` posiciones**, distintas y dentro del tablero. `choosePosition` entrega, en orden, la siguiente que ese jugador aún no haya colocado. Ignora `error` porque el patrón debe ser válido. El companion `AIStrategy(game, attempts)` busca un patrón y construye la estrategia; `attempts >= 1`. Se propone generar patrones aleatorios, evaluarlos con `game.simulate` y escoger el mejor, pero se admite otra heurística razonada. La memoria debe explicar qué mide, cómo lo aproxima y cuándo puede fallar.

### Gestión de efectos — 0,5 puntos

Encapsular entrada/salida y cálculos que puedan fallar con los tipos vistos en clase. Explicar las decisiones en la memoria. Abstraer la regla del autómata en un `trait` es un extra **no puntuable**.

## 10. Pruebas y orden recomendado

El enunciado recomienda este orden: **`Matrix` → `GameOfLifeMatrix` → `Player`/`State` → colocación de `Game` con estrategias de prueba → simulación → `ManualStrategy` → `Main`**. Probar cada pieza antes de avanzar.

| Archivo de `pruebas/` | Cobertura |
| --- | --- |
| `TestKit.scala` | Utilidades; no modificar. |
| `MatrixTests.scala` | Matriz genérica, inmutabilidad, límites y recursión de cola con 90 000 celdas. |
| `GameOfLifeMatrixTests.scala` | Tablero, igualdad, vecinas, dibujo y reglas B3/S23. |
| `MatrixListTests.scala` | Parte opcional y comparación con las matrices recursivas. |

Son pruebas con `assert`, sin framework: cada archivo es un objeto `App` ejecutable; muestran `OK`, `XX` o pendiente cuando quedan `???`. Se pueden añadir pruebas, especialmente para `Game`, estrategias y `Main`, que no tienen un archivo de pruebas suministrado.

## 11. Entrega y evaluación

Hay que subir **dos archivos**: (1) un `.zip` con los `.scala` de la práctica en la misma estructura que la plantilla y **sin las pruebas**; (2) un **PDF con una memoria muy breve**. El código debe comentar las funciones y las partes complejas sin documentar en exceso, mantener una estructura correcta e incluir un programa principal funcional.

La memoria debe tratar: decisiones y dificultades relevantes; heurística y límites si se hizo la IA; extras realizados; coordinación y reparto entre integrantes. No hace falta copiar funciones ni insertar capturas de código.

| Apartado | Puntos |
| --- | ---: |
| `Matrix` | 2 |
| `GameOfLifeMatrix` | 1 |
| Jugadores y estado | 0,5 |
| Estrategia manual | 1 |
| `Game`: colocación | 2 |
| `Game`: simulación, ganador y partida | 1 |
| `Main` | 0,5 |
| **Total obligatorio** | **8** |
| Matrices con métodos de `List` (opcional) | 0,5 |
| IA (opcional) | 1 |
| Gestión de efectos (opcional) | 0,5 |

El enunciado no da una fecha concreta en este PDF. Advierte que la entrega fuera de plazo se califica como suspensa y que un retraso inferior a 24 horas penaliza un 30 % de la nota final. También indica que, si se sospecha uso de IA, puede hacerse una evaluación individual en la que cada estudiante defienda su implementación.
