import com.github.tototoshi.csv.*
import org.nspl.awtrenderer.*
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

    chartingGxMinute(contentFile)
    chartBarPlot(contentFile)
    chartting()
    chartBarPlotStadium()
    chartBarTeamsGenre()
    chartBarWTournaments()
    chartBarQWandM(contentFile2)
  }

  //Tabla Goles
  def chartingGxMinute(data: List[Map[String, String]]): Unit = {
    val data4Chart = data
      .filter(row => row("goals_minute_regulation") != "NA")
      .map(row => (row("goals_minute_regulation").toDouble, row("goals_goal_id")))
      .map(x => x._1 -> x._2)
      .groupBy(_._1)
      .map(x => (x._1.toDouble, x._2.length.toDouble))
      .toIndexedSeq

    val plot = xyplot(data4Chart)(
      par
        .xlab("Minuto")
        .ylab("freq.")
        .xLabelRotation(-77)
        .main("Goles por Minuto")
    )

    pngToFile(new File("D:\\GxP.png"), plot.build, 400)
  }


  //Tabla Torneos
  def chartBarPlot(data: List[Map[String, String]]): Unit = {
    val data4Chart: List[(String, Double)] = data.filter(_("tournaments_tournament_name").contains("Men"))
      .map(row => (
        row("tournaments_tournament_name"),
        row("matches_match_id"),
        row("matches_home_team_score"),
        row("matches_away_team_score")
      )) 
      .distinct
      .map(t4 => (t4._1, t4._3.toInt + t4._4.toInt))
      .groupBy(_._1) 
      .map(t2 => (t2._1, t2._2.map(_._2).sum.toDouble))
      .toList
      .sortBy(_._1)

    val indices = Index(data4Chart.map(value => value._1.substring(0,4)).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.95)),
      color = RedBlue(86, 186))(par
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

  def chartBarQWandM(data: List[Map[String, String]]) =
    val data4Chart: Map[String, Double] = data
      .map(row => (row("squads_tournament_id"), row("squads_team_id"), row("squads_player_id"), row("squads_position_name")))
      .distinct
      .map(row => row._4 -> (row._1, row._2))
      .groupBy(_._1)
      .map(row => (row._1, row._2.length.toDouble))


    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Position")
      .ylab("freq.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("Positions In All Tournaments"))

    pngToFile(new File("D:\\PxA.png"), bar1.build, 400)

  //Tabla Jugadores
  def chartting(): Unit = {
    val data4Chart = ImportadorDatos.stattsGenreWandM()


    val indices = Index(data4Chart.map(value => value._1).toArray)
    val values = Vec(data4Chart.map(value => value._2).toArray)

    val series = Series(indices, values)

    val bar1 = saddle.barplotHorizontal(series,
      xLabFontSize = Option(RelFontSize(0.6)),
      color = RedBlue(data4Chart.map(_._2).min, data4Chart.map(_._2).max))(par
      .xlab("Genero")
      .ylab("Quantity.")
      .xLabelRotation(-77)
      .xNumTicks(0)
      .main("Cantidad Mujeres y Hombres"))

    pngToFile(new File("D:\\CmC.png"), bar1.build, 400)
  }
}