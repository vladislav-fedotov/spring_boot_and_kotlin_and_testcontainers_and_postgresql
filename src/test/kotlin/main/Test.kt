package main

import main.Test.Initializer
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.PostgreSQLContainer
import java.lang.System.out
import java.lang.invoke.MethodHandles

@DataJpaTest // load just repositories and entities, could be replaced with @SpringBootTest if needed
@RunWith(SpringRunner::class)
@AutoConfigureTestDatabase(replace = NONE) // "Failed to replace DataSource with an embedded database for tests."
@ActiveProfiles("test") // Read application-test.yml
@ContextConfiguration(initializers = [(Initializer::class)]) // Not needed if application-test.yml contains data source properties
class Test {

    companion object {
        val logger: Logger = getLogger(MethodHandles.lookup().lookupClass())

        //        private const val POSTGRES_PORT = 5432
        /**
         * Database container will be created once for all tests
         * Entities will be removed from database after each test (the same generated key sequence will be used)
         * Use @Rule to setup it for each test
         */
        @ClassRule
        @JvmField
        var postgres: KGenericContainer = KGenericContainer("postgres:9.6.8")
//                .withExposedPorts(POSTGRES_PORT)
//                .waitingFor(LogMessageWaitStrategy()
//                        .withRegEx(".*server started*\\s"))
//                .withStartupTimeout(of(10, SECONDS))
    }

    /**
     * Set data source properties before spring init with docker container info
     * This code could be replaced with application-test.yml configuration
     */
    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            // Before Spring Boot 2.0
//            EnvironmentTestUtils.addEnvironment("it", configurableApplicationContext.environment,
//                    "spring.datasource.url=" + postgres.jdbcUrl,
//                    "spring.datasource.username=" + postgres.username,
//                    "spring.datasource.password=" + postgres.password,
//                    "spring.datasource.driver-class-name=" + postgres.driverClassName
//            )
            TestPropertyValues.of(
                    "spring.datasource.url:" + postgres.jdbcUrl,
                    "spring.datasource.username:" + postgres.username,
                    "spring.datasource.password:" + postgres.password,
                    "spring.datasource.driver-class-name:" + postgres.driverClassName
            ).applyTo(context)
        }
    }

    @Autowired
    lateinit var repository: ModelRepository

    @Test
    fun test1() {
        repository.save(Model(data = "Das Model1"))
        repository.findAll().forEach(out::println)
    }

    @Test
    fun test2() {
        repository.save(Model(data = "Das Model2"))
        repository.findAll().forEach(out::println)
        repository.findById(2).apply(out::println)
        assertEquals("Das Model2", repository.findById(2).get().data)
    }

}

//Hack for some generic issues with GenericContainer class
class KGenericContainer(imageName: String) : PostgreSQLContainer<KGenericContainer>(imageName)