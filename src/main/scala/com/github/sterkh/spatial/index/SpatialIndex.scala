package com.github.sterkh.spatial.index

import com.opencsv.CSVReader
import org.locationtech.jts.io.WKTReader

import java.io.{BufferedReader, FileReader}

trait SpatialIndex extends Serializable {}

object SpatialIndex {

  def readShapes[T](csvFilePath: String,
                    idColumn: Int,
                    wktColumn: Int,
                    fromLine: Int = 1,
                    separator: Char = ',',
                    quoteChar: Char = '\"'): Seq[Shape[T]] = {

    import collection.JavaConverters._

    val bufReader = new BufferedReader(new FileReader(csvFilePath))
    val csvReader = new CSVReader(bufReader, separator, quoteChar, fromLine)
    val lines: List[Array[String]] = csvReader.readAll().asScala.toList

    val wkt = new WKTReader()

    lines.map(line => {
      Shape(line(idColumn).asInstanceOf[T], wkt.read(line(wktColumn)))
    })
  }
}
