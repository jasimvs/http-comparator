package me.jasim.comparator.app.api

import com.typesafe.config.ConfigFactory

/**
  * Created by jsulaiman on 5/14/17.
  */
object Config {

  /**
    * Throws exception if unable to read config file
    */
  def loadConfig(file: String) = {
    val conf = ConfigFactory.load(file)
    val cl = conf.getConfig("http-comparator")
    val ip = cl.getString("ip")
    val port = cl.getInt("port")

    MovieReservationConfig(ip, port)
  }

}

case class MovieReservationConfig(ip: String, port: Int)
