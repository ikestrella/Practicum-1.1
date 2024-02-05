import com.github.tototoshi.csv.*
import org.nspl.awtrenderer.*
import org.nspl.data.HistogramData
import org.nspl.{data, *}
import org.saddle.{Index, Series, Vec}

import java.io.File

implicit object CustomFormat extends DefaultCSVFormat {
  override val delimiter: Char = ';'
}

object CreateGraph {
  @main
  def generarGraficos(): Unit = {
    val path2DataFile: String = "C:\\dsPartidosYGoles.csv"
    val reader = CSVReader.open(new File(path2DataFile))
    val contentFile: List[Map[String, String]] = reader.allWithHeaders()
    reader.close()


    val path2DataFile2: String = "C:\\dsAlineacionesXTorneo-2.csv"
    val reader2 = CSVReader.open(new File(path2DataFile2))
    val contentFile2: List[Map[String, String]] = reader2.allWithHeaders()
    reader2.close()

    charting(contentFile)
    chartBarPlot(datosGraficaM(contentFile))
    chartting(contentFile2)

    chartBarPlotStadium()
    chartBarTeamsGenre()
    chartBarWTournaments()
    chartBarPstXAlignment()
  }

  //Tabla Goles
  def charting(data: List[Map[String, String]]): Unit = {
    val listNroShirt: List[Double] = data
      .filter(row => row("goals_minute_regulation") != "NA")
      .map(row => row("goals_minute_regulation").toDouble)

    val histForwardShirtNumber = xyplot(HistogramData(listNroShirt, 20) -> bar())(
      par
        .xlab("Minutos")
        .ylab("frecuencia")
        .main("Goles por minuto")
    )
    pngToFile(new File("D:\\GxP.png"), histForwardShirtNumber.build, 400)
    renderToByteArray(histForwardShirtNumber.build, width = 400)
  }


  //Tabla Torneos
  def chartBarPlot(data: List[(String, Int)]): Unit = {
    val data4Chart: List[(String, Double)] = data
      .map(t2 => (t2._1, t2._2.toDouble))

    val indices = Index(data4Chart.map(value => value._1.substring(0, 4)).toArray) //con esta funcion se obtiene las 4 primeras letras de la lista de tuplas
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.95)),
      color = RedBlue(86, 186))(par //enviamos los valores enviados cambia de color - Azul para el menor - Rojo para el mayor
      .xlab("Año Torneo")
      .ylab("freq.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("Goles Torneo"))
    pngToFile(new File("D:\\GxT.png"), bar1.build, 400)
  }

  //Tabla Stadiums
  def chartBarPlotStadium(): Unit = {
    val data4Chart = ImportadorDatos.stattsStadiumsXCountry()

    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Country")
      .ylab("Quantity.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("StadiumsXCountry")
    )
    pngToFile(new File("D:\\GxS.png"), bar1.build, 400)
  }

  //Tabla Teams
  def chartBarTeamsGenre(): Unit =
    val data4Chart = ImportadorDatos.stattsTeamsGenere()

    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Genre")
      .ylab("Quantity.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("QuantityTeamsGenre"))

    pngToFile(new File("D:\\T-Gnr.png"), bar1.build, 400)


  //Tabla Torneos
  def chartBarWTournaments(): Unit =
    val data4Chart = ImportadorDatos.stattsWinnersTournaments()

    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Country")
      .ylab("Quantity.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("Winners Tournaments"))

    pngToFile(new File("D:\\WxT.png"), bar1.build, 400)

  def chartBarPstXAlignment() =
    val data4Chart = ImportadorDatos.stattsPositionXAlignment()

    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Position")
      .ylab("Quantity.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("Common Position"))

    pngToFile(new File("D:\\PxA.png"), bar1.build, 400)

  //Tabla Jugadores
  def chartting(data: List[Map[String, String]]): Unit = {
    val listNroShirt: List[Double] = data
      .filter(row => row("squads_position_name") == "forward" && row("squads_shirt_number") != "0")
      .map(row => row("squads_shirt_number").toDouble)

    val histForwardShirtNumber = xyplot(HistogramData(listNroShirt, 25) -> bar())(
      par
        .xlab("Shirt number")
        .ylab("freq.")
        .main("Forward shirt number")
    )
    pngToFile(new File("D:\\CmC.png"), histForwardShirtNumber.build, 400)
    renderToByteArray(histForwardShirtNumber.build, width = 400)
  }

  def datosGraficaM(data: List[Map[String, String]]): List[(String, Int)] = {
    val dataGoles: List[(String, Int)] = data
      .filter(_("tournaments_tournament_name").contains("Men"))
      .map(row => (
        row("tournaments_tournament_name"),
        row("matches_match_id"),
        row("matches_home_team_score"),
        row("matches_away_team_score")
      )) // se obtiene el nombreTorneo, idPartido, Goles
      .distinct //saca los repetidos
      .map(t4 => (t4._1, t4._3.toInt + t4._4.toInt)) //suma goles de visitantes y locales
      .groupBy(_._1) //agrupa por nombre del torneo
      .map(t2 => (t2._1, t2._2.map(_._2).sum))
      .toList
      .sortBy(_._1)
    dataGoles
  }

  def datosGrafica(data: List[Map[String, String]]): List[(String, Int)] = {
    val dataGoles: List[(String, Int)] = data
      .filter(_("tournaments_tournament_name").contains("Women"))
      .map(row => (
        row("tournaments_tournament_name"),
        row("matches_match_id"),
        row("matches_home_team_score"),
        row("matches_away_team_score")
      )) // se obtiene el nombreTorneo, idPartido, Goles
      .distinct //saca los repetidos
      .map(t4 => (t4._1, t4._3.toInt + t4._4.toInt)) //suma goles de visitantes y locales
      .groupBy(_._1) //agrupa por nombre del torneo
      .map(t2 => (t2._1, t2._2.map(_._2).sum))
      .toList
      .sortBy(_._1)
    dataGoles
  }
}