package lan.jing.backend

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [SpringDataWebAutoConfiguration::class])
@MapperScan("lan.jing.backend.mapper")
class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
