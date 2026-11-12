# Juego de la Vida de Conway

**Práctica de Programación Declarativa**

**Autores:** Álvaro Martínez del Campo y Marino Rodríguez Moreno.

Este repositorio contiene una implementación en Scala de la práctica descrita en el [enunciado oficial](Juego%20de%20la%20Vida%20de%20Conway.pdf). El trabajo convierte el Juego de la Vida de Conway en una competición entre dos jugadores. Cada uno dispone de un tablero independiente y coloca, por turnos, un número limitado de células vivas. Después, ambos tableros evolucionan por separado según las reglas B3/S23: una célula viva sobrevive con dos o tres vecinas vivas y una célula muerta nace con exactamente tres.

La simulación se detiene cuando se repite un estado o se alcanza el límite de generaciones. Gana el jugador cuyo patrón tarda más generaciones en detenerse; en caso de empate, vence el que conserva más células vivas. El programa principal utiliza tableros de 15 × 15 y un máximo de 1000 generaciones.

El código de la aplicación está en `src/` y las pruebas en `pruebas/`. El repositorio incluye una estrategia manual, una estrategia opcional de IA y una implementación adicional de las matrices mediante métodos de `List`.
