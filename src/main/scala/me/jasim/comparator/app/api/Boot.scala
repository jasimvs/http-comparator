package me.jasim.comparator.app.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Stream, Task}
import me.jasim.comparator.core.ComparatorServiceImpl
import me.jasim.comparator.infra.repo.HttpComparatorInMemoryRepository
import org.http4s.server.blaze._
import org.http4s.util.StreamApp

object Boot extends StreamApp {

  val conf = Config.loadConfig("application.conf") // if config fails, let it crash

  val movieShowService = new HttpComparatorRoutes
                             with ComparatorServiceImpl
                             with HttpComparatorInMemoryRepository
                             with LazyLogging {}

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(conf.port, conf.ip)
      .mountService(movieShowService.comparatorService, "/")
      .serve
  }

}
