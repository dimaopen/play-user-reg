package models

import java.sql.Timestamp
import java.time.Instant

import slick.jdbc.JdbcProfile

/**
  *
  * @author Dmitry Openkov
  */
package object dao {

  object CustomColumnTypes {
    implicit def timeInstantType(implicit profile: JdbcProfile): profile.ColumnType[Instant] = {
      import profile.api._

      MappedColumnType.base[Instant, Timestamp](
        instant => new Timestamp(instant.getEpochSecond * 1000),
        ts => ts.toInstant
      )
    }
  }

}
