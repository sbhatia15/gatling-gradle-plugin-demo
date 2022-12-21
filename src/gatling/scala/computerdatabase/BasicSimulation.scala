package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.UUID
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val baseUrl = System.getProperty("baseUrl", "http://volcano.aps1.o2.verizonmedia.com")
  val repeat = System.getProperty("repeat", "10")
  val connections = System.getProperty("connections", "100")

  val httpProtocol = http
    .disableCaching
    .baseUrl(baseUrl) // Here is the root for all relative URLs
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
    .header("x-forwarded-for","2607:fb90:8daf:679d:b835:94ff:fe4a:851f, 69.147.64.149,69.147.64.80")

  val api = "/volcano?AdBreakId=1665665987&bcid=5b152b6e7cce6e408cbfd920&pid=5bd9ec3034f69d6f92b23e52&secure=true&rssId=c6f68d2d-d06b-3d6c-a453-f817c840ba70&v=2&f=json&s2s=true&output=vast3&mode=YXS&m.podmax=60000&m.type=live&show_name=LEM&m.spaceid=1183300001&experience=y20&gdpr=false&site=finance&licensor_id=a077000000KfSioAAF&us_privacy=1YNN&m.url=https%3A%2F%2Ffinance.yahoo.com%2Fthomas-test-nov-15-200648297.html&width=822&pl=up&region=US&lang=en-US&device=desktop&plseq=1&height=619&sid="

  val maybeCheckServer = baseUrl.contains("https") match {
    case false => header("server").is("envoy")
    case true => header("server").isNull
  }

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .repeat(repeat.toInt) {
      exec(http("Ad Request")
        .get(api + UUID.randomUUID().toString())
        .check(bodyLength.gt(5000))
        .check(maybeCheckServer))
    }

  setUp(scn.inject(atOnceUsers(connections.toInt)).protocols(httpProtocol))
}
