enum Rol:
  case BUSCADOR, SABOTEADOR

enum TipoAccion:
  case ROMPER_HERRAMIENTA, REPARAR_HERRAMIENTA, MAPA, DERRUMBE

sealed trait Carta {
  def id: Int
  def nombre: String
}

case class CartaTunel(
    id: Int,
    nombre: String,
    arriba: Boolean,
    abajo: Boolean,
    izquierda: Boolean,
    derecha: Boolean,
    esCallejonSinSalida: Boolean,
    tieneOro: Boolean
) extends Carta

case class CartaAccion(
    id: Int,
    nombre: String,
    tipoEfecto: TipoAccion
) extends Carta

case class Jugador(
    id: Int,
    nombre: String,
    rol: Rol,
    mano: List[Carta],
    herramientasRotas: List[String]
) {
  // Un jugador no puede excavar si tiene herramientas rotas
  def estaBloqueado(): Boolean = herramientasRotas.nonEmpty
}

// Única definición de Posicion con sus métodos auxiliares
case class Posicion(x: Int, y: Int) {
  def arriba: Posicion    = Posicion(x, y + 1)
  def abajo: Posicion     = Posicion(x, y - 1)
  def izquierda: Posicion = Posicion(x - 1, y)
  def derecha: Posicion   = Posicion(x + 1, y)
}

case class Mazo(
    cartasRobo: List[Carta],
    cartasDescarte: List[Carta]
) {
  def estaVacio(): Boolean = cartasRobo.isEmpty
}

case class Tablero(
    cuadricula: Map[Posicion, CartaTunel],
    posicionInicio: Posicion,
    posicionesMeta: List[Posicion]
) {

  def validarColocacion(pos: Posicion, nuevaCarta: CartaTunel): Boolean = {
    // 1. La posición debe estar vacía
    if (cuadricula.contains(pos)) return false

    // 2. Obtener posibles cartas vecinas desde la cuadrícula
    val vecinoArriba = cuadricula.get(pos.arriba)
    val vecinoAbajo  = cuadricula.get(pos.abajo)
    val vecinoIzq    = cuadricula.get(pos.izquierda)
    val vecinoDer    = cuadricula.get(pos.derecha)

    // 3. La carta no puede estar "flotando"; debe tener al menos un vecino
    val tieneVecino = vecinoArriba.isDefined || 
                      vecinoAbajo.isDefined || 
                      vecinoIzq.isDefined || 
                      vecinoDer.isDefined
                      
    if (!tieneVecino) return false

    // 4. Validar que los bordes coincidan con los vecinos existentes
    val coincideArriba = vecinoArriba.forall(_.abajo == nuevaCarta.arriba)
    val coincideAbajo  = vecinoAbajo.forall(_.arriba == nuevaCarta.abajo)
    val coincideIzq    = vecinoIzq.forall(_.derecha == nuevaCarta.izquierda)
    val coincideDer    = vecinoDer.forall(_.izquierda == nuevaCarta.derecha)

    // 5. Todos los lados adyacentes deben coincidir
    val coincidenBordes = coincideArriba && coincideAbajo && coincideIzq && coincideDer

    coincidenBordes
  }
}

case class Juego(
    listaJugadores: List[Jugador],
    tablero: Tablero,
    mazo: Mazo,
    turnoActual: Int
) {
  def verificarFinPartida(): Boolean = {
    // Retorna true si los buscadores encuentran el oro o el mazo se agota 
    false
  }
}

@main def probarLogicaJuego(): Unit = {
  println("--- INICIANDO PRUEBAS DE SABOTEUR ---")

  // 1. Creamos algunas cartas de túnel de prueba
  // Nota: (id, nombre, arriba, abajo, izquierda, derecha, esCallejon, tieneOro)
  val cartaInicio = CartaTunel(1, "Inicio (Cruz)", true, true, true, true, false, false)
  val tunelHorizontal = CartaTunel(2, "Túnel Horizontal", false, false, true, true, false, false)
  val tunelVertical = CartaTunel(3, "Túnel Vertical", true, true, false, false, false, false)
  val curvaAbajoDer = CartaTunel(4, "Curva Abajo-Derecha", false, true, false, true, false, false)

  // 2. Configuramos el tablero inicial
  val posInicio = Posicion(0, 0)
  // El tablero empieza solo con la carta de inicio en (0,0)
  val cuadriculaInicial = Map(posInicio -> cartaInicio)
  
  // Posiciones de meta imaginarias para inicializar el tablero
  val metas = List(Posicion(8, 0), Posicion(8, 2), Posicion(8, -2))
  val tablero = Tablero(cuadriculaInicial, posInicio, metas)

  println("\nTablero inicializado con la carta de Inicio en (0,0).")

  // --- PRUEBA 1: Jugada Válida ---
  // Intentamos poner un túnel horizontal a la derecha (1, 0)
  // La carta de inicio tiene 'derecha = true', y el túnel horizontal tiene 'izquierda = true'. Debería encajar.
  val posDerecha = Posicion(1, 0)
  val prueba1 = tablero.validarColocacion(posDerecha, tunelHorizontal)
  println(s"Prueba 1 (Válida) - Colocar horizontal a la derecha: $prueba1 (Esperado: true)")

  // --- PRUEBA 2: Jugada Inválida (No coincide el borde) ---
  // Intentamos poner un túnel horizontal arriba (0, 1)
  // La carta de inicio tiene 'arriba = true', pero el túnel horizontal tiene 'abajo = false'. NO debería encajar.
  val posArriba = Posicion(0, 1)
  val prueba2 = tablero.validarColocacion(posArriba, tunelHorizontal)
  println(s"Prueba 2 (Inválida) - Colocar horizontal arriba: $prueba2 (Esperado: false)")

  // --- PRUEBA 3: Jugada Inválida (Carta Flotante) ---
  // Intentamos poner un túnel en (2, 0) cuando (1, 0) aún está vacío.
  // No tiene ningún vecino adyacente.
  val posLejos = Posicion(2, 0)
  val prueba3 = tablero.validarColocacion(posLejos, tunelVertical)
  println(s"Prueba 3 (Inválida) - Colocar carta flotante sin vecinos: $prueba3 (Esperado: false)")

  // --- PRUEBA 4: Jugada Inválida (Sobrescribir carta) ---
  // Intentamos poner una carta en (0,0), donde ya está la carta de inicio.
  val prueba4 = tablero.validarColocacion(posInicio, curvaAbajoDer)
  println(s"Prueba 4 (Inválida) - Sobrescribir carta en (0,0): $prueba4 (Esperado: false)")

  println("--- FIN DE LAS PRUEBAS ---")
}