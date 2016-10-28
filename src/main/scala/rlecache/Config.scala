package rlecache

import com.typesafe.config._
import scala.concurrent.duration._

/** Wrapper for configuration hierarchy. */
class ConfigTree(protected val config: Config)

/** Configuration options of the RLE cache. */
object Config extends ConfigTree(ConfigFactory.load()) {
  import config._

  /** `rle-cache.*` configuration of the RLE cache */
  object rlecache extends ConfigTree(getConfig("rle-cache")) {
    import config._

    /** `rle-cache.interface` configuration of listening interface */
    val interface = getString("interface")

    /** `rle-cache.port` configuration of listening port */
    val port = getInt("port")

    /** `upstream.*` configuration of the upstream endpoint */
    object upstream extends ConfigTree(getConfig("upstream")) {
      import config._

      /** `rle-cache.upstream.endpoint` configuration of upstream endpoint URI */
      val endpoint = getString("endpoint")

      /** `rle-cache.upstream.interval` configuration of finite duration between polls of upstream endpoint */
      val interval = Duration.fromNanos(getDuration("interval").toNanos())

      /** `rle-cache.upstream.maximum-line-length` configuration of maximum length of one line in the upstream response */
      val maximumLineLength = getInt("maximum-line-length")

    }
  }
}
