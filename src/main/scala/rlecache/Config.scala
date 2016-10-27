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

    /** `upstream.*` configuration of the upstream endpoint */
    object upstream extends ConfigTree(getConfig("upstream")) {
      import config._

      /** `rle-cache.upstream.interval` configuration of finite duration between polls of upstream endpoint */
      val interval = Duration.fromNanos(getDuration("interval").toNanos())
    }
  }
}
