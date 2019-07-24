package models

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate}

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

    implicit def localDateType(implicit profile: JdbcProfile): profile.ColumnType[LocalDate] = {
      import profile.api._

      MappedColumnType.base[LocalDate, Date](
        localDate => new Date(localDate.getYear - 1900, localDate.getMonth.getValue - 1, localDate.getDayOfMonth),
        date => date.toLocalDate
      )
    }
  }

}
