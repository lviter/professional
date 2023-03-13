# Groovy

使用java jvm虚拟机运行

## 代码示例

```groovy
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

def main() {
    def params = "{llv}"
    println("1231" + params)

    def startDate = "2023-01-01"
    def endDate = "2023-01-31"
    monthDemo(startDate, endDate)


}

def monthDemo(def startDate, def endDate) {
    def df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    def dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    def now = LocalDate.now()
    if (LocalDate.parse(endDate).isAfter(now)) {
        endDate = now.format(df)
    }
    def startDate1 = now.withDayOfMonth(1).format(df)
    def endDate1 = now.format(df)
    def startDate2 = LocalDate.parse(startDate).withDayOfMonth(1).format(df)
    def endDate2 = LocalDate.parse(startDate).withDayOfMonth(1).plusMonths(1).minusDays(1).format(df)
    //如果满足查询当前月份
    if (startDate1.compareTo(startDate) == 0 && endDate1.compareTo(endDate) == 0) {
        isFullMonth = true
        startLastDate = LocalDate.parse(startDate).minusMonths(1).format(df)
        endLastDate = LocalDate.parse(endDate).minusMonths(1).format(df)
    } else if (startDate2.compareTo(startDate) == 0 && endDate2.compareTo(endDate) == 0) {
        //如果满足查询满月
        isFullMonth = true
        startLastDate = LocalDate.parse(startDate).minusMonths(1).withDayOfMonth(1).format(df)
        endLastDate = LocalDate.parse(startDate).withDayOfMonth(1).minusDays(1).format(df)
    } else {
        isFullMonth = false
    }
    def startTime = LocalDateTime.parse(startDate + " 00:00:00", dtf).format(dtf)
    def endTime = LocalDateTime.parse(endDate + " 23:59:59", dtf).format(dtf)

    println("start: " + startDate + " end: " + endDate)
    println("startTime: " + startTime + " endTime: " + endTime)
    println("是否查询满月: " + isFullMonth)
}

return main()
```