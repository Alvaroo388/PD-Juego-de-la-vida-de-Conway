# Álvaro: estado del proyecto y notas para la memoria

**Actualizado: 12/11/2026 · Obligatorio e IA funcionando · Memoria y revisión pendientes**

Este documento sirve de apoyo para revisar los cambios de Marino y redactar una memoria breve. No es la memoria de entrega. El enunciado original sigue siendo la referencia.

## 1. Situación actual

| Bloque | Qué hay hecho | Estado |
| --- | --- | --- |
| Matrices de Álvaro | `Matrix` y `GameOfLifeMatrix`, integradas en `main` | ✅ 37 pruebas originales pasan |
| Jugadores | Rival y nombre de cada jugador | ✅ |
| `Game` | Estado inicial, colocación, turnos, simulación, ganador y partida completa | ✅ |
| Estrategia manual | Tablero, errores, lectura de coordenadas y reintentos | ✅ |
| `Main` | Presupuesto 1–225, elección humano/IA, resultados, animación y cierre de entrada | ✅ |
| IA | Búsqueda aleatoria en zona central y selección por simulación | ✅ |
| Matrices con `List` | `MatrixList` y `GameOfLifeMatrixList` conservan `???` | ⏳ Opcional de Álvaro |
| Gestión de efectos | No se ha desarrollado como extra independiente | ⏳ Por decidir |

**Estado de Git:** las matrices ya estaban integradas. Los cambios posteriores se agrupan en commits locales de configuración, implementación, pruebas y documentación. No se ha hecho push; queda compartirlos y revisarlos antes de integrar.

## 2. Qué hemos añadido en la parte de Marino

```text
Main: configura presupuesto y estrategias
  └─ Game.play
       ├─ placementLoop → Strategy.choosePosition → placeCell
       ├─ simulate del tablero de cada jugador
       └─ determineWinner → GameResult
  └─ muestra resultados y ofrece Animation.animate
```

Se han trabajado `Player.scala`, `Game.scala`, `Strategy.scala` y `Main.scala`. Se conservan las firmas y los tipos dados. No se han modificado las implementaciones de matrices de Álvaro, `Animation.scala` ni el dibujo `show`.

| Decisión | Motivo y comportamiento |
| --- | --- |
| Estado inmutable con `copy` y `Map.updated` | Cada jugada crea otro estado; el tablero y presupuesto del rival se conservan. |
| Errores de jugada con `Either` | `Left` lleva el motivo; `Right` lleva el estado nuevo. Game no imprime esos errores. |
| Separación de validaciones | La estrategia manual comprueba dos enteros; `placeCell` comprueba presupuesto, límites y casilla libre. |
| Colocación recursiva de cola | Una jugada rechazada conserva estado y turno; `Some(mensaje)` transmite el error. Se omite a quien ya terminó. |
| Simulación con un `Set` de tableros | Detecta estados fijos y ciclos de cualquier longitud, no solo repeticiones consecutivas. |
| Generación inicial = 0 | Bloque `(1,4)`, vacío `(1,0)`, intermitente `(2,3)` y célula aislada `(2,0)`. También se respeta `maxGenerations`. |
| Ganador por generaciones y luego vivas | Si ambos criterios empatan, `None`. Puede ganar un tablero que termina vacío. |
| `GameResult.finalState` | Guarda los patrones tras la colocación, no los tableros al acabar la simulación; permite mostrarlos y animarlos. |
| Cierre de entrada | Si `readLine` devuelve `null`, se lanza `EOFException`; Main la captura y termina con un mensaje sencillo. |

**Para la memoria de matrices:** explicar por parte de Álvaro la recursión de cola, los acumuladores y `reverse` para preservar el orden de filas, la herencia y el cálculo simultáneo de la nueva generación desde el tablero anterior. Son las decisiones de su implementación, no trabajo atribuido a Marino.

## 3. IA: explicación y límites de la heurística

**Idea propuesta por Marino:** agrupar aleatoriamente las células en el interior del tablero para favorecer interacciones y dejar margen hasta los bordes. No se ha reducido el presupuesto máximo exigido por el enunciado.

| Paso | Implementación y razón |
| --- | --- |
| 1. Delimitar la zona | Lado inicial `ceil(sqrt(2 * budget))`: intenta dejar aproximadamente la mitad de casillas libres. Es una heurística, no una densidad óptima demostrada. |
| 2. Adaptar y centrar | Se limita la zona al tablero y se amplía la otra dimensión si hace falta, también en rectángulos estrechos. Siempre debe caber el presupuesto. |
| 3. Generar candidatos | `Random.shuffle(posiciones).take(budget)` proporciona exactamente ese número de posiciones válidas y distintas. |
| 4. Evaluar | Se construye el tablero y se llama a `game.simulate`: la puntuación es la pareja de generaciones y vivas finales. |
| 5. Seleccionar | Recursión de cola que conserva el mejor candidato: generaciones primero; vivas para desempatar. En igualdad se conserva el anterior. |
| 6. Jugar | La `case class` guarda el patrón y entrega su primera posición aún muerta en el tablero propio; no necesita contador mutable. |

`Main.AIAttempts = 10`: cada IA prueba diez candidatos antes de jugar. Es configurable; más intentos aumentan el tiempo de cálculo y las oportunidades de encontrar un buen candidato. El resultado puede cambiar entre ejecuciones por el azar.

**Casos especiales:** presupuesto cero produce un patrón vacío para pruebas; con 225 células en 15 × 15 se ocupan todas las casillas directamente. En esos casos solo existe un patrón y no se simulan candidatos para buscar alternativas. En Main se sigue exigiendo presupuesto entre 1 y 225.

**Límites que conviene reconocer en la memoria:** una zona compacta puede causar sobrepoblación; restringir la búsqueda al centro excluye otros patrones; diez muestras no garantizan el mejor resultado; los candidatos pueden repetirse entre intentos; el límite de generaciones recorta la evaluación. La simulación es exacta hasta ese límite, pero la búsqueda del mejor patrón es aproximada. No hay aprendizaje automático ni búsqueda exhaustiva.

## 4. Pruebas y comprobación

Se han añadido siete objetos de pruebas: `PlayerTests`, `GameTests`, `PlacementLoopTests`, `ManualStrategyTests`, `MainTests`, `MainIntegrationTests` y `AIStrategyTests`. Se ejecutan como objetos `App`, igual que los proporcionados.

- **Nueve suites pasan:** las siete nuevas y las dos originales de matrices obligatorias. Cubren límites, inmutabilidad, turnos, errores, ciclos, desempates, lecturas y cierre de entrada; también humano–humano, humano–IA, IA–humano, IA–IA y animación.
- **IA:** se verifican presupuesto exacto, ausencia de duplicados, zona central, rectángulos, tablero completo, orden de colocación y elección por puntuación con evaluación controlada.
- **Opcional de matrices:** `MatrixListTests` tiene 1/7 y la parte de `GameOfLifeMatrixList` 0/4; las diez pendientes llegan a métodos con `???`.
- **Entorno usado:** compilación con Scala 3.10 y Java 17, con salida en carpetas temporales. Hay avisos de `App` obsoleto (la plantilla lo exige) y de una anotación de tipos en `Matrix.scala:82`; no han impedido las pruebas. La animación se comprobó por ejecución y salida; conviene verla personalmente en una terminal compatible con ANSI.

## 5. Qué queda y qué contar en la memoria

| Pendiente | Acción |
| --- | --- |
| Revisión entre compañeros | Álvaro revisa los cambios de Marino y ambos prueban una partida. No dar por hechas revisiones formales de PR que aún no se han realizado. |
| Extras restantes | Decidir/completar matrices con `List` y gestión de efectos. `Either`, `Option` y `Try` básicos no bastan para atribuirse automáticamente el extra de efectos. |
| Memoria breve | Resumir decisiones, dificultades reales, heurística y límites de IA, extras efectivamente hechos y coordinación. Evitar copiar funciones o añadir capturas de código. |
| Coordinación | Mantener el reparto real: Álvaro, matrices y reglas; Marino, jugadores, Game, interacción e IA; revisión y entrega compartidas. Usar este documento como notas, no como relato de tareas aún pendientes. |
| Git y entrega | Revisar los archivos antes de commit. Las carpetas `.metals/`, `.scala-build/` y `.vscode/` son locales. Entregar ZIP con los `.scala` de la plantilla **sin pruebas**, más PDF de memoria; los documentos de coordinación no forman parte del ZIP de código. |

**Prioridad siguiente:** revisión conjunta y decisión sobre los extras. La parte obligatoria y la IA ya permiten jugar; falta cerrar documentación, integración y entrega.
