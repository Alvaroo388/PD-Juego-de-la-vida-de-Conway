# Reparto de la práctica: Juego de la Vida de Conway

## Resumen

| Persona | Responsabilidad | Archivos principales | Puede empezar por |
| --- | --- | --- | --- |
| Álvaro | Matriz genérica, tablero y reglas de Conway | `src/Matrix.scala`, `src/GameOfLifeMatrix.scala` | `Matrix.scala` y sus pruebas |
| Marino | Jugadores, lógica de la partida e interacción | `src/Player.scala`, `src/Game.scala`, `src/Strategy.scala`, `src/Main.scala` | `Player.scala`, estrategia manual y lógica de colocación |

**Dificultad:** baja = función pequeña o integración sencilla; media = varios casos que coordinar; alta = recursión, reglas o estados con casos límite. La dificultad es una estimación de trabajo, no la puntuación del enunciado.

Podemos trabajar a la vez porque cada persona tiene archivos distintos. Marino puede desarrollar `Game.scala` contra las firmas de la plantilla mientras Álvaro termina las matrices; las pruebas completas de la partida se harán cuando ambos cambios estén integrados. Mantendremos `main` como versión integrada y abriremos una PR por bloque para que el otro la revise.

## Tareas de Álvaro

- [ ] **[Alta · obligatoria]** Completar los dos constructores pendientes de `Matrix` y sus métodos `inside`, `get`, `update`, `partition`, `neighbors` y `filterNeighbors`. En `Matrix.scala`, usar recursión y *pattern matching* sobre listas, sin métodos de orden superior ni acceso por índice. Hacer recursivos de cola los constructores, `get`, `update` y `partition`.
- [ ] **[Media · obligatoria]** Ejecutar `MatrixTests` y revisar expresamente los límites de la matriz, la inmutabilidad, el orden de filas y el caso de 90 000 elementos.
- [ ] **[Alta · obligatoria]** Completar `GameOfLifeMatrix`: `empty`, `withAlive`, `update`, `aliveCells`, `aliveCount`, `countAliveNeighbors` y `nextGeneration`. Calcular cada nueva celda a partir del tablero anterior y tratar las posiciones exteriores como muertas.
- [ ] **[Media · obligatoria]** Ejecutar `GameOfLifeMatrixTests` y comprobar con patrones conocidos el bloque, el intermitente, el planeador y los bordes. No modificar `show`.
- [ ] **[Media · compartida]** Revisar la PR de Marino y contrastar los resultados de `simulate` con patrones conocidos y con el límite de generaciones.
- [ ] **[Media · opcional, 0,5 puntos]** Completar `MatrixList.scala` y `GameOfLifeMatrixList.scala` usando métodos de `List`; ejecutar `MatrixListTests` y comparar ambas versiones para la memoria.
- [ ] **[Baja · compartida]** Escribir en la memoria las decisiones sobre recursión, `partition`, `nextGeneration` y, si se hace, la versión con `List`.

## Tareas de Marino

- [ ] **[Baja · obligatoria]** Completar `otherPlayer` y `playerName` en `Player.scala`. Mantener `State.scala` y `GameResult.scala` como tipos de datos.
- [ ] **[Media · obligatoria]** Completar `ManualStrategy.choosePosition`: mostrar error y tablero, leer `fila columna`, repetir recursivamente ante un formato incorrecto y terminar si la entrada se cierra. La validez de una jugada corresponde a `Game.placeCell`.
- [ ] **[Alta · obligatoria]** Completar en `Game.scala` `initialState`, `hasFinished`, `placeCell` y `placementLoop`. Validar presupuesto, posición y casilla libre; alternar turnos; repetir con `Some(error)` tras una jugada rechazada. `placementLoop` debe ser recursivo de cola.
- [ ] **[Alta · obligatoria]** Completar `simulate`: detectar cualquier estado repetido, detenerse también en `maxGenerations` y devolver `(generaciones, células vivas)`. El tablero inicial es la generación 0 y la función debe ser recursiva de cola.
- [ ] **[Media · obligatoria]** Completar `determineWinner` y `play`: comparar primero generaciones y después células vivas, contemplar el empate y construir el resultado completo.
- [ ] **[Media · obligatoria]** Completar `Main.scala`: tablero de 15 × 15, máximo de 1000 generaciones, presupuesto válido entre 1 y 225, elección de estrategias, resultados explicados y opción de animación. No modificar `Animation.scala`.
- [ ] **[Alta · obligatoria]** Añadir y ejecutar pruebas propias de `Game` y de la interacción: presupuesto cero, posición inválida, casilla ocupada, turnos, bloque, intermitente, tablero vacío, límite de generaciones y desempates. Las pruebas suministradas no cubren estas piezas.
- [ ] **[Media · compartida]** Revisar las PR de Álvaro antes de integrar `Matrix` y `GameOfLifeMatrix`; después, probar una partida completa con ambas implementaciones.
- [ ] **[Alta · opcional, 1 punto]** Implementar `AIStrategy` con un patrón válido y una heurística razonada; explicar en la memoria qué evalúa y dónde puede fallar.
- [ ] **[Media · opcional, 0,5 puntos]** Valorar la gestión de efectos con los tipos vistos en clase y documentar las decisiones si se implementa.
- [ ] **[Baja · compartida]** Escribir en la memoria las decisiones sobre colocación, simulación, estrategias y coordinación del grupo.

## Integración y entrega

1. Álvaro abre PR de `feat/matrix`; Marino puede avanzar a la vez en `Player`, `ManualStrategy` y `Game`.
2. Tras revisar e integrar la matriz, Álvaro abre PR de `feat/game-of-life-matrix`. Marino actualiza su rama con `main` antes de integrar `Game`.
3. Marino integra la lógica de `Game`, la estrategia manual y el programa principal en PR pequeñas. Ambos ejecutan las pruebas y revisan una partida completa.
4. Solo después de tener la parte obligatoria funcionando, decidimos qué opcionales abordar.
5. Ambos revisamos la memoria y preparamos la entrega: ZIP con los archivos `.scala` de la práctica, sin las pruebas, y PDF con una memoria breve. Conservamos nombres y firmas de la plantilla.
