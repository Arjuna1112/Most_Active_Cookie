package com.quant.most_active_cookie;

import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.parser.FileParser;
import com.quant.most_active_cookie.service.CookieServiceImpl;
import com.quant.most_active_cookie.model.CommandLineInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.time.LocalDate.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class MostActiveCookieApplicationTests {

	private CookieServiceImpl cookieFilter;
	private PrintStream stdout = System.out;
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	@BeforeEach
	public void initialize() {
		cookieFilter = new CookieServiceImpl(new FileParser());
		// Used to store sys out which gets printed on execution
		PrintStream output = new PrintStream(byteArrayOutputStream);
		System.setOut(output);
	}

	@AfterEach
	public void readOutput() {
		System.setOut(stdout);
	}

	/** When date is not exist,
	 * output is empty
	 * No Cookie Returned
	 */
	@Test
	public void filterMAC_InvalidDate_ThrowException() throws CustomParseException {
		CommandLineInput commandInput = new CommandLineInput("src/main/resources/cookie_log.csv", parse("2021-12-09"));
		assertThatThrownBy(() -> cookieFilter.processMostActiveCookie(commandInput))
				.isInstanceOf(CustomParseException.class);
	}

	/** When file path is invalid,
	 * throw exception
	 */
	@Test
	public void filterMAC_InvalidFilePath_ThrowException() {
		CommandLineInput commandInput = new CommandLineInput("cookie_log.csv", parse("2018-12-09"));
		assertThatThrownBy(() -> cookieFilter.processMostActiveCookie(commandInput))
				.isInstanceOf(CustomParseException.class);
	}

	/** When file path and date are valid,
	 * for one cookie
	 * Fetch Most Active Cookie (MAC)
	 */
	@Test
	public void filterMAC_ValidFilePathAndDate() throws CustomParseException {
		CommandLineInput commandInput = new CommandLineInput("src/main/resources/cookie_log.csv", parse("2018-12-09"));
		cookieFilter.processMostActiveCookie(commandInput);
		assertThat(byteArrayOutputStream.toString().contains("*** Most Active Cookie *** AtY0laUfhglK3lC7"));
		assertThat(!byteArrayOutputStream.toString().contains("*** Most Active Cookie *** SAZuXPGUrfbcn5UA"));
	}

	/** When file path and date are valid,
	 * for multiple cookie
	 * Fetch Most Active Cookie (MAC)
	 */
	@Test
	public void filterMAC_HasMoreThanOne()
			throws CustomParseException {
		CommandLineInput commandInput = new CommandLineInput("src/main/resources/cookie_log.csv", parse("2018-12-06"));
		cookieFilter.processMostActiveCookie(commandInput);
		assertThat(byteArrayOutputStream.toString().contains("*** Most Active Cookie *** 8xYHIASHaBa79xzf"));
		assertThat(byteArrayOutputStream.toString().contains("*** Most Active Cookie *** 1dSLJdsaDJLDsdSd"));
		assertThat(!byteArrayOutputStream.toString().contains("*** Most Active Cookie *** AtY0laUfhglK3lC7"));
	}

}
