val datos = ImportadorDatos.obtenerGoles()


def mediaMedianaModa(datos:List[Double]): (Double, Double, Double) ={
  val media = datos.sum / datos.length

  val mediana = datos.length match {
    case len if len % 2 == 0 =>
      val sortedDatos = datos.sorted
      val mid = len / 2
      (sortedDatos(mid - 1) + sortedDatos(mid)) / 2.0
    case len =>
      val sortedDatos = datos.sorted
      sortedDatos(len / 2)
  }

  val moda = datos.groupBy(identity).map(x => x._1 -> x._2.length).maxBy(_._2)._1
  (media, mediana, moda)
}

mediaMedianaModa(datos.map(_._10.toDouble).toList)._3.toString

datos.map(_._10.toDouble).groupBy(identity).map(x => x._1 -> x._2.length).maxBy(_._2)


