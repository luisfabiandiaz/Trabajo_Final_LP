//> using dep "org.scalafx::scalafx:20.0.0-R31"

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.Pane
import scalafx.scene.text.Text
import scalafx.scene.input.MouseEvent
import scalafx.Includes.* // <-- LA SOLUCIÓN AL ERROR: Importa las conversiones implícitas de eventos

object InterfazJuego extends JFXApp3 {
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Saboteur - Tablero Principal"
      width = 900
      height = 600
      
      scene = new Scene {
        fill = rgb(40, 40, 40)
        
        val contenedor = new Pane()
        
        // Constantes del tamaño de la cuadrícula (igual al tamaño de las cartas)
        val anchoCarta = 60
        val altoCarta = 90
        
        // Carta de Inicio (Alineada a la grilla: 120 es múltiplo de 60, 270 es múltiplo de 90)
        val cartaInicio = new Rectangle {
          x = 120
          y = 270
          width = anchoCarta
          height = altoCarta
          fill = LightBlue
        }
        val textoInicio = new Text {
          x = 125
          y = 320
          text = "Inicio"
          fill = Black
        }
        
        // Carta de Destino (Alineada a la grilla: 720 es múltiplo de 60, 270 es múltiplo de 90)
        val cartaDestino = new Rectangle {
          x = 720
          y = 270
          width = anchoCarta
          height = altoCarta
          fill = DarkRed
        }
        val textoDestino = new Text {
          x = 725
          y = 320
          text = "Destino"
          fill = White
        }

        contenedor.children = List(cartaInicio, textoInicio, cartaDestino, textoDestino)
        
        // --- EVENTO DE CLIC CON GRID SNAPPING ---
        onMouseClicked = (event: MouseEvent) => {
          // 1. Obtenemos las coordenadas del mouse como enteros
          val mouseX = event.x.toInt
          val mouseY = event.y.toInt
          
          // 2. Aplicamos la fórmula matemática para ajustar a la cuadrícula
          val gridX = (mouseX / anchoCarta) * anchoCarta
          val gridY = (mouseY / altoCarta) * altoCarta
          
          // 3. Dibujamos la carta exactamente en las coordenadas de la grilla
          val nuevaCarta = new Rectangle {
            x = gridX
            y = gridY
            width = anchoCarta
            height = altoCarta
            fill = SaddleBrown
            stroke = Black
          }
          
          val textoTunel = new Text {
            x = gridX + 10
            y = gridY + 50
            text = "Túnel"
            fill = White
          }
          
          contenedor.children.addAll(nuevaCarta, textoTunel)
          
          println(s"Clic en ($mouseX, $mouseY) -> Carta ajustada a grilla ($gridX, $gridY)")
        }

        content = contenedor
      }
    }
  }
}