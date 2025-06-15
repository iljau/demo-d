package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.TransactionManager;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface BirthdayRepository extends JpaRepository<Birthday, Long> {
	Optional<Birthday> findByName(String name) throws EntityNotFoundException;
	List<Birthday> findByBirthDate(Date birthDate);
}

@Entity
class Birthday {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
	public Date birthDate;
	public String name;

	public Birthday() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}


class BirthdayNotFoundException extends RuntimeException {
	BirthdayNotFoundException(Long id) {
		super("Could not find birthday " + id);
	}

	BirthdayNotFoundException(String name) {
		super("Could not find birthdays for " + name);
	}

	BirthdayNotFoundException(Date birthDate) {
		super("Could not find birthdays on " + birthDate);
	}
}


@RestControllerAdvice
class BirthdayNotFoundAdvice {
	@ExceptionHandler(BirthdayNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String birthdayNotFoundHandler(BirthdayNotFoundException ex) {
		return ex.getMessage();
	}
}

@Configuration
@EnableJdbcRepositories
class ApplicationConfig extends AbstractJdbcConfiguration {
	@Bean
	DataSource dataSource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.HSQL).build();
	}

	@Bean
	NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	TransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}

// https://spring.io/guides/tutorials/rest
@RestController
class BirthdayController {
	private final BirthdayRepository repository;

	BirthdayController(BirthdayRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/birthdays")
	List<Birthday> all() {
		return repository.findAll();
	}

	@PostMapping("/birthdays")
	Birthday newBirthday(@RequestBody Birthday newBirthday) {
		return repository.save(newBirthday);
	}

	// Single item
	@GetMapping("/birthdays/{id}")
	Birthday one(@PathVariable Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new BirthdayNotFoundException(id));
	}

	@GetMapping("/birthdays/byName/{name}")
	Birthday byName(@PathVariable String name) {
		return repository.findByName(name)
				.orElseThrow(() -> new BirthdayNotFoundException(name));
	}

	@GetMapping("/birthdays/byBirthDate/{birthDate}")
	List<Birthday> byName(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate) {
		return repository.findByBirthDate(birthDate);
	}


	@PutMapping("/birthdays/{id}")
	Birthday replaceBirthday(@RequestBody Birthday newBirthday, @PathVariable Long id) {

		return repository.findById(id)
				.map(birthday -> {
					birthday.setBirthDate(newBirthday.getBirthDate());
					birthday.setName(newBirthday.getName());
					return repository.save(birthday);
				})
				.orElseGet(() -> {
					return repository.save(newBirthday);
				});
	}

	@DeleteMapping("/birthdays/{id}")
	void deleteBirthday(@PathVariable Long id) {
		repository.deleteById(id);
	}

}

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
