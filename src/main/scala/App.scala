import java.awt
import javax.swing.ImageIcon
import scala.swing
import scala.swing.*
import scala.swing.BorderPanel.Position.*
import scala.swing.TabbedPane.Page
import scala.swing.event.*


object App {

  // Modelos y encabezados de las tablas para jugadores, alineaciones, goles, partidos, estadios, equipos y torneos
  val modelTBPlayers: Array[(String, String, String, String, String, String, String, String, String)] = ImportadorDatos.obtenerJugadores()
  val headersPlayers: Array[String] = Array("ID Jugador", "Nombre", "Apellido", "Nacimiento", "(F-1 / M-0)", "Arquero", "Defensa", "MedioCampista", "Delantero")

  val headersAlignaments: Array[String] = Array("ID Equipo", "ID Torneo", "ID Jugador", "Posicion", "Numero Camiseta")
  val modelTBAlignements: Array[(String, String, String, String, String)] = ImportadorDatos.obtenerAlineaciones()

  val headersGoals: Array[String] = Array("ID Gol", "ID Equipo", "ID Jugador", "ID Torneo", "ID Partido", "Minuto", "Minuto Regulacion", "M. Gol Fuera Tiempo", "Periodo Partido", "Auto-Gol", "Gol Penal")
  val modelTBGoals: Array[(String, String, String, String, String, String, String, String, String, String, String)] = ImportadorDatos.obtenerGoles()

  val headersMatches: Array[String] = Array("ID Partido", "ID Torneo", "ID E.Visitante", "ID E.Local", "ID Estadio", "Fecha", "Hora Inicio", "Etapa Partido", "Goles Local", "Goles Visitante", "Tiempo Extra", "ConcluidoxPenales", "Local C.Penales Gol", "Visitante C.Penales Gol", "Resultado")
  val modelTBMatches: Array[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)] = ImportadorDatos.obtenerPartidos()

  val headersStadiums: Array[String] = Array("ID Estadio", "Nombre", "Ciudad", "Pais", "Capacidad")
  val modelBDStadiums: Array[(String, String, String, String, String)] = ImportadorDatos.obtenerEstadios()

  val headersTeams: Array[String] = Array("ID Equipo", "Pais", "Region", "E.Masculino", "E.Femenino")
  val modelBDTeams: Array[(String, String, String, String, String)] = ImportadorDatos.obtenerEquipos()

  val headersTournaments: Array[String] = Array("ID Torneo", "Nombre Torneo", "Año", "P.Anfitrion", "Ganador", "C.Equipos")
  val modelBDTournaments: Array[(String, String, String, String, String, String)] = ImportadorDatos.obtenerTorneos()


  // Panel principal
  val panelPrincipal: BorderPanel = new BorderPanel {

    // Panel Secundario - (Botones / Pestañas (Tabla / Busqueda))
    val panelSecundario: BoxPanel = new BoxPanel(Orientation.Vertical) {

      //Botones
      // Cambia Segun Seleccionada Tabla
      val lblText: Label = new Label("Jugadores") {
        verticalAlignment = Alignment.Center
        horizontalAlignment = Alignment.Center
      }
      val tabla: Table = new Table() {
        enabled = false
      }

      // Panel De Botones Tablas
      val panelBotontesTablas: BoxPanel = new BoxPanel(Orientation.Vertical) {
        contents += new Label("Seleccionar Tabla: ")

        // Funcion Para Crear Botones
        def radio(mutex: ButtonGroup, text: String): RadioButton = {
          val b = new RadioButton(text)
          listenTo(b)
          mutex.buttons += b
          contents += b
          b
        }

        val opciones: ButtonGroup = new ButtonGroup
        val btn1: RadioButton = radio(opciones, "Equipos")
        val btn2: RadioButton = radio(opciones, "Jugadores")
        val btn3: RadioButton = radio(opciones, "Alineaciones")
        val btn4: RadioButton = radio(opciones, "Torneos")
        val btn5: RadioButton = radio(opciones, "Partidos")
        val btn6: RadioButton = radio(opciones, "Goles")
        val btn7: RadioButton = radio(opciones, "Estadios")
        opciones.select(btn2)

        // Cambia Segun Boton Seleccionado (lblText, tabla)
        reactions += {
          case ButtonClicked(`btn1`) =>
            lblText.text = btn1.text
            tabla.model = new Table(modelBDTeams.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersTeams).model

          case ButtonClicked(`btn2`) =>
            lblText.text = btn2.text
            tabla.model = new Table(modelTBPlayers.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)), headersPlayers).model

          case ButtonClicked(`btn3`) =>
            lblText.text = btn3.text
            tabla.model = new Table(modelTBAlignements.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersAlignaments).model

          case ButtonClicked(`btn4`) =>
            lblText.text = btn4.text
            tabla.model = new Table(modelBDTournaments.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6)), headersTournaments).model

          case ButtonClicked(`btn5`) =>
            lblText.text = btn5.text
            tabla.model = new Table(modelTBMatches.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)), headersMatches).model

          case ButtonClicked(`btn6`) =>
            lblText.text = btn6.text
            tabla.model = new Table(modelTBGoals.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals).model

          case ButtonClicked(`btn7`) =>
            lblText.text = btn7.text
            tabla.model = new Table(modelBDStadiums.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersStadiums).model
        }
      }

      // Pestañas (Tabla / Busqueda ID)
      // Panel de pestañas que contiene las tablas y operaciones
      val tabs: TabbedPane = new TabbedPane {

        // Primer Pestaña - Tablas
        val panelTablas: BoxPanel = new BoxPanel(Orientation.Vertical) {

          // Panel De Statts
          contents += new BorderPanel {

            // Boton Para Ver Statts - Segun La Tabla Seleccionada
            val btnQuerie = new Button("Más")
            listenTo(btnQuerie)

            // Ventana Almacena Statts
            def ventanaSecundaria: Frame = new Frame {
              title = "Ventana Busqueda"
              contents = panelBusquedaTB
              pack()
              centerOnScreen()
              open()
              size = new Dimension(800, 400)
            }

            // Panel Donde Se Colocan las Statts
            val panelBusquedaTB: BorderPanel = new BorderPanel {}

            // Funcion Crea Panel - Contenido Del panelBusquedaTB
            def txtContent(lblArea: String, lblTabla: String): BoxPanel = {

              // Tabla Seleccionada
              val lbl_tblStats: Label = new Label(lblTabla) {
                verticalAlignment = Alignment.Center
                horizontalAlignment = Alignment.Center
              }

              // Statts Tabla
              val estadisticas: TextArea = new TextArea(5, 40) {
                editable = false
              }
              estadisticas.text = lblArea

              val panelEstadisticas = new BoxPanel(Orientation.Vertical) {
                contents += lbl_tblStats
                contents += new ScrollPane(estadisticas)
              }

              panelEstadisticas
            }

            // Imagen de Statts Segun La Tabla
            def imagen(ruta: String): Panel = {
              //Imagen Segun La Ruta Del png
              val panelBusq: Panel = new Panel {
                val imagen: ImageIcon = new ImageIcon(ruta)
                val etiqueta: Label = new Label {
                  icon = imagen
                }
                _contents += etiqueta
              }

              panelBusq
            }

            // Cambia el Contenido Segun Tabla Seleccionada
            reactions += {
              case ButtonClicked(`btnQuerie`) =>
                panelBusquedaTB.layout.clear()
                ventanaSecundaria
                lblText.text match {
                  case "Jugadores" =>
                    val sttsPly: String = ImportadorDatos.statsJugadores()
                      .map(x => "Cantidad Jugadores: " + x._1 + "\nMujeres: " + x._2 + "\nHombres: " + x._3 + "\nArqueros: " + x._4 + "\nDefensas: " + x._5 + "\nCentroCampistas: " + x._6 + "\nDelanteros: " + x._7)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsPly, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\CmC.png")) = East
                  case "Torneos" =>
                    val sttsTrn: String = ImportadorDatos.statsTorneos()
                      .map(x => "Cantidad Torneos: " + x._1 + "\nAño mas reciente: " + x._3 + "\nAño mas Antiguo: " + x._4 + "\nPromedio de equipos Por Torneo: " + x._5)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsTrn, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\WxT.png")) = East

                  case "Equipos" =>
                    val sttsEqp: String = ImportadorDatos.statsEquipos()
                      .map(x => "Cantidad Equipos: " + x._1 + "\nCantidad de Regiones: " + x._2 + "\nEquipos Hombres: " + x._3 + "\nEquipos Mujeres" + x._4 + "\nEquipos Totales: " + x._5)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsEqp, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\T-Gnr.png")) = East

                  case "Estadios" =>
                    val sttsEstd: String = ImportadorDatos.statsEstadios()
                      .map(x => "Cantidad Estadios: " + x._1 + "\nPromedio Capacidad de Estadios: " + x._2 + "\nCantidad Maxima: " + x._3 + "\nCantidad Minima: " + x._4)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsEstd, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\GxS.png")) = East
                  case "Partidos" =>
                    val sttsPtd: String = ImportadorDatos.statsPartidos()
                      .map(x => "Cantidad Partidos: " + x._1 + "\nCantidad Equipos Visitantes: " + x._2 + "\nCantidad Equipos Locales: " + x._3 + "\nCantidad Estadios: " + x._4 + "\nPartidos Terminados Por Tanda Penales: " + x._5 + "\nGoles Por Penal Locales: " + x._6 + "\nGoles Por Penal Visitantes: " + x._7 + "\nGoles Totales: " + x._8 + "\nGoles Por Penal: " + x._9)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsPtd, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\GxP.png")) = East
                  case "Goles" =>
                    val sttsGls: String = ImportadorDatos.statsGoles()
                      .map(x => "Cantidad Goles: " + x._1 + "\nJugadores Con Goles: " + x._2 + "\nCantidad Torneos: " + x._3 + "\nCantidad Partidos: " + x._4 + "\nGoles En Propia: " + x._5 + "\nGoles Totales Por Penal: " + x._6)
                      .mkString
                    panelBusquedaTB.layout(txtContent(sttsGls, lblText.text)) = Center
                    panelBusquedaTB.layout(imagen("D:\\GxT.png")) = East
                  case "Alineaciones" =>
                    panelBusquedaTB.layout(txtContent("Sin data", lblText.text)) = Center
                }
            }
            layout(btnQuerie) = East
            layout(lblText) = BorderPanel.Position.Center
            maximumSize = new Dimension(1380, 10)
            minimumSize = new Dimension(600, 15)
          }

          // Tabla Predeterminada - Jugadores
          tabla.model = new Table(modelTBPlayers.map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)), headersPlayers).model
          contents += new ScrollPane(tabla)
        }
        // Agrega Primera Pestaña (Tablas)
        pages += new Page("Tablas", panelTablas)

        // Segunda Pestaña (Busqueda ID) - Busqueda Segun Tabla Seleccionada
        val panelBusqueda: BoxPanel = new BoxPanel(Orientation.Vertical) {

          // Campo Texto -> Ingresar ID / Buscar Segun ID y Tabla Seleccionada
          val txtID: TextField = new TextField() {
            maximumSize = new Dimension(500, 24)
          }
          val btnBuscar: Button = new Button("OK")

          // Panel Tablas - Muestra Tablas Segun ID
          val panelTablasBusqueda: BoxPanel = new BoxPanel(Orientation.Vertical) {}

          // Panel Ingreso ID
          val panelIngresoID: BoxPanel = new BoxPanel(Orientation.Vertical) {
            // Label - Campo Texto - Boton
            contents += new Label("Ingrese ID de Tabla")
            contents += txtID
            contents += btnBuscar
            listenTo(btnBuscar)

            // Al Dar Al Boton - Cambia Segun ID Colocado En El Campo Texto
            reactions += {
              case ButtonClicked(`btnBuscar`) =>

                // Limpia Contenido Del Panel Tablas
                panelTablasBusqueda.contents.clear()
                lblText.text match
                  case "Equipos" =>
                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Equipo", new ScrollPane(new Table(modelBDTeams.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersTeams)))
                      pages += new Page("Goles", new ScrollPane(new Table(modelTBGoals.filter(_._2.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals)))
                      pages += new Page("Partidos", new ScrollPane(new Table(modelTBMatches.filter(x => x._3.toLowerCase.equals(txtID.text.toLowerCase) || x._4.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)), headersMatches)))
                      pages += new Page("Alineaciones", new ScrollPane(new Table(modelTBAlignements.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersAlignaments)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Goles" =>
                    val jugadorGol: String = modelTBGoals.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(_._3).last
                    val tabsTablas = new TabbedPane {
                      pages += new Page("Gol", new ScrollPane(new Table(modelTBGoals.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals)))
                      pages += new Page("Jugador", new ScrollPane(new Table(modelTBPlayers.filter(_._1.equals(jugadorGol)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)), headersPlayers)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Partidos" =>
                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Partidos", new ScrollPane(new Table(modelTBMatches.filter(x => x._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)), headersMatches)))
                      pages += new Page("Gol", new ScrollPane(new Table(modelTBGoals.filter(_._5.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Jugadores" =>
                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Jugador", new ScrollPane(new Table(modelTBPlayers.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)), headersPlayers)))
                      pages += new Page("Gol", new ScrollPane(new Table(modelTBGoals.filter(_._3.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals)))
                      pages += new Page("Alineaciones", new ScrollPane(new Table(modelTBAlignements.filter(_._3.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersAlignaments)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Estadios" =>
                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Estadio", new ScrollPane(new Table(modelBDStadiums.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersStadiums)))
                      pages += new Page("Partidos", new ScrollPane(new Table(modelTBMatches.filter(x => x._5.toLowerCase.equals(txtID.text.toLowerCase)).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)), headersMatches)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Alineaciones" =>
                    val idCompuesta: Array[String] = txtID.text.split(",").map(_.trim)
                    val txtIDC: String = txtID.text.toLowerCase
                    val idEquipo: String = idCompuesta(0)
                    val idTorneo: String = idCompuesta(1)

                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Alineacion", new ScrollPane(new Table(
                        modelTBAlignements.filter(x =>
                          x._1.toLowerCase.equals(idEquipo) &&
                            x._2.toLowerCase.equals(idTorneo)
                        ).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersAlignaments)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()

                  case "Torneos" =>
                    val tabsTablas: TabbedPane = new TabbedPane {
                      pages += new Page("Torneo", new ScrollPane(new Table(
                        modelBDTournaments.filter(_._1.toLowerCase.equals(txtID.text.toLowerCase)
                        ).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6)), headersTournaments)))

                      pages += new Page("Partidos", new ScrollPane(new Table(
                        modelTBMatches.filter(x => x._2.toLowerCase.equals(txtID.text.toLowerCase)
                        ).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)), headersMatches)))

                      pages += new Page("Goles", new ScrollPane(new Table(
                        modelTBGoals.filter(_._4.toLowerCase.equals(txtID.text.toLowerCase)
                        ).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)), headersGoals)))

                      pages += new Page("Alineaciones", new ScrollPane(new Table(
                        modelTBAlignements.filter(_._2.toLowerCase.equals(txtID.text.toLowerCase)
                        ).map(x => Array[Any](x._1, x._2, x._3, x._4, x._5)), headersAlignaments)))
                    }
                    txtID.text = ""
                    panelTablasBusqueda.contents += tabsTablas
                    panelTablasBusqueda.revalidate()
                    panelTablasBusqueda.repaint()
                  case _ =>
            }
          }

          // Separa Contenido - Panel Ingresar ID / Panel Para Tablas Segun ID
          val contenidoBusqueda = new SplitPane(Orientation.Vertical, panelIngresoID, panelTablasBusqueda)
          contents += contenidoBusqueda
        }

        // Segunda Pestaña - Busqueda Segun ID y Tabla Seleccionada
        pages += new Page("Busqueda", panelBusqueda)
      }

      // Separa - Panel De Botones (Tablas) / Panel Pestañas
      contents += new SplitPane(Orientation.Vertical, panelBotontesTablas, tabs) {
        oneTouchExpandable = true
        continuousLayout = true
      }
    }

    // Label Titulo / Panel Secundario (Botones / Pestañas (Tabla / Busqueda))
    layout(new Label("Consultas Tablas BD")) = North
    layout(panelSecundario) = BorderPanel.Position.Center
  }

  // Ventana Principal Del Programa
  @main
  def ventanaPrincipal: Frame = new Frame {
    title = "Tablas Queries"
    contents = panelPrincipal
    pack()
    centerOnScreen()
    open()
  }

  def main(args: Array[String]): Unit = {
    CreateGraph.generarGraficos()
    ventanaPrincipal
  }
}