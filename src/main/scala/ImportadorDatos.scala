import cats.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import doobie.*
import doobie.implicits.*


object ImportadorDatos {
  val xa = Transactor.fromDriverManager[IO](
    driver = "com.mysql.cj.jdbc.Driver", //JDBC DRIVER
    url = "jdbc:mysql://localhost:3306/practicumbdfr", //URL CONEXION
    user = "root",
    password = "ExLolIKE.",
    logHandler = None
  )

  def obtenerJugadores(): Array[(String, String, String, String, String, String, String, String, String)] =
    sql"SELECT * FROM players"
      .query[(String, String, String, String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerPartidos(): Array[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)] =
    sql"SELECT * FROM matches"
      .query[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerGoles(): Array[(String, String, String, String, String, String, String, String, String, String, String)] =
    sql"SELECT * FROM goals"
      .query[(String, String, String, String, String, String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerEstadios(): Array[(String, String, String, String, String)] =
    sql"SELECT * FROM stadiums"
      .query[(String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerEquipos(): Array[(String, String, String, String, String)] =
    sql"SELECT * FROM teams"
      .query[(String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerTorneos(): Array[(String, String, String, String, String, String)] =
    sql"SELECT * FROM tournaments"
      .query[(String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def obtenerAlineaciones(): Array[(String, String, String, String, String)] =
    sql"SELECT * FROM alignments"
      .query[(String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()


  def statsJugadores(): Array[(String, String, String, String, String, String, String)] =
    sql"""SELECT COUNT(player_id),
          SUM(players_female ),
          COUNT(player_id) -SUM(players_female),
          SUM(players_goal_keeper),
          SUM(players_defender),
          SUM(players_midfielder),
          SUM(players_forward)
        FROM players;
          """
      .query[(String, String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def statsGoles(): Array[(String, String, String, String, String, String)] =
    sql"""SELECT
      COUNT(DISTINCT goals_goal_id) AS distinct_goals,
      COUNT(DISTINCT goals_player_id) AS distinct_players,
      COUNT(DISTINCT matches_tournament_id) AS distinct_tournaments,
      COUNT(DISTINCT matches_match_id) AS distinct_matches,
      SUM(goals_own_goal) AS total_own_goals,
      SUM(goals_penalty) AS total_penalties
    FROM goals;
       """.query[(String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def statsPartidos(): Array[(String, String, String, String, String, String, String, String, String)] =
    sql"""SELECT
      COUNT(*) AS total_matches,
      COUNT(DISTINCT matches_away_team_id) AS distinct_away_teams,
      COUNT(DISTINCT matches_home_team_id) AS distinct_home_teams,
      COUNT(DISTINCT matches_stadium_id) AS distinct_stadiums,
      SUM(matches_penalty_shootout) AS total_penalty_shootouts,
      SUM(matches_home_team_score_penalties) AS total_home_team_penalties,
      SUM(matches_away_team_score_penalties) AS total_away_team_penalties,
      SUM(matches_home_team_score + matches_away_team_score) AS total_goals,
      SUM(matches_home_team_score_penalties + matches_away_team_score_penalties) AS total_penalties
    FROM matches;
       """.query[(String, String, String, String, String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def statsEstadios(): Array[(String, String, String, String)] =
    sql"""SELECT COUNT(matches_stadium_id),
           AVG(stadiums_stadium_capacity),
           MAX(stadiums_stadium_capacity),
           MIN(stadiums_stadium_capacity)
    FROM stadiums;
           """.query[(String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def statsEquipos(): Array[(String, String, String, String, String)] =
    sql"""SELECT COUNT(team_id),
           COUNT(DISTINCT team_region_name),
           SUM(men_team),
           SUM(women_team),
           SUM(men_team + women_team)
    FROM teams;
       """.query[(String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def statsTorneos(): Array[(String, String, String, String, String)] =
    sql"""SELECT COUNT(matches_tournament_id),
           COUNT(DISTINCT tournaments_tournament_name),
           MAX(tournaments_year),
           MIN(tournaments_year),
           AVG(tournaments_count_teams)
    FROM tournaments;
       """.query[(String, String, String, String, String)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def stattsPlayerAlignments(): Array[(String, String, Int)] =
    sql"""SELECT squads_tournament_id, squads_team_id, COUNT(*) AS tournament_team_players_count
          FROM alignments
          GROUP BY squads_tournament_id, squads_team_id
          ORDER BY 1,2;""".query[(String, String, Int)]
      .to[Array]
      .transact(xa)
      .unsafeRunSync()

  def stattsStadiumsXCountry(): List[(String, Double)] =
    sql"""SELECT stadiums_country_name, COUNT(*)
          FROM stadiums
          GROUP BY 1
          ORDER BY 1;""".query[(String, Double)]
      .to[List]
      .transact(xa)
      .unsafeRunSync()

  def stattsTeamsGenere(): List[(String, Double)] =
    sql"""SELECT "Men Teams", SUM(men_team)
          FROM teams
          UNION
          SELECT "Women Teams", SUM(women_team)
          FROM teams;""".query[(String, Double)]
      .to[List]
      .transact(xa)
      .unsafeRunSync()

  def stattsWinnersTournaments(): List[(String, Double)] =
    sql"""SELECT tournaments_winner, COUNT(tournaments_winner)
          FROM tournaments
          GROUP BY 1;""".query[(String, Double)]
      .to[List]
      .transact(xa)
      .unsafeRunSync()

  def stattsGenreWandM(): List[(String, Double)] =
    sql"""SELECT "Mujeres", SUM(players_female)
         |FROM players
         |UNION
         |SELECT "Hombres", COUNT(players_female) - SUM(players_female)
         |FROM players;""".stripMargin.query[(String, Double)]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
}