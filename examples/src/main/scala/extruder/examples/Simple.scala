package extruder.examples

import cats.syntax.either._
import cats.syntax.validated._
import com.typesafe.config.ConfigFactory
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import extruder.core._
import extruder.typesafe.TypesafeConfig

import scala.collection.JavaConverters._
import scala.concurrent.duration.{Duration, FiniteDuration}
import extruder.refined._
import extruder.core.MapConfig._

case class CC(a: String = "test", b: String = "test2", c: Int, d: Option[CC2], e: CC3, f: Set[Int], dur: Duration, finDur: FiniteDuration)
case class CC2(x: String = "test4", y: Option[Int] = Some(232), z: CC3)
case class CC3(a: Option[String])
case class CC4(a: Option[CC3])

case class Testing( s: Option[Long], d: Set[String], i: Int = 1)

sealed trait Sealed
case object ObjImpl extends Sealed
case class CCImpl(a: String, i: Long, u: URL, s: Set[Int], cc: CC4) extends Sealed

case class Hello(s: Sealed)

trait Thing[T] {
  def t: T
}
case class ThingImpl(t: String) extends Thing[String]


object Simple extends App {
  val config = Map(
    "cc.c" -> "2000",
    "cc.a" -> "sdfsf",
    "cc.e.cc3.a" -> "test3",
    "cc.d.cc2.z.cc3.a" -> "testing",
    "cc3.a" -> "hello",
    "cc.f" -> "2, 3",
    "cc.dur" -> "Inf",
    "cc.findur" -> "22 days"
  )

  println(decode[CC](config))

  val sealedObjResolvers = Map("type" -> "ObjImpl")

  println(decode[Sealed](sealedObjResolvers).map(encode[Sealed]))

  println(encode[Sealed](ObjImpl))


  println(MapConfig.decode[CC](config).map(MapConfig.encode[CC]))

  println(MapConfig.decode[Sealed](sealedObjResolvers))


  println(SystemPropertiesConfig.decode[CC])

  println(TypesafeConfig.decode[CC](ConfigFactory.parseMap(config.asJava)))

  println(MapConfig.decode[Int Refined Positive](Map("" -> "23")).map(MapConfig.encode[Int Refined Positive]))
}

