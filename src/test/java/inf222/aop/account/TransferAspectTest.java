package inf222.aop.account;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import inf222.aop.account.aspect.AspectAppender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TransferAspectTest {

	private Bank bank;
	private AspectAppender aspectAppender;
	private PrintStream standardOut;
	private ByteArrayOutputStream outputStreamCaptor;

	@BeforeEach
	void setUp() {
		bank = new Bank();

		aspectAppender = new AspectAppender();
		aspectAppender.setContext(new LoggerContext());
		aspectAppender.start();

		final Logger logger = (Logger) LoggerFactory.getLogger(Bank.class);
		logger.addAppender(aspectAppender);

		standardOut = System.out;
		outputStreamCaptor = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStreamCaptor));
	}

	@AfterEach
	void cleanUp() {
		aspectAppender.stop();

		outputStreamCaptor.reset();
		System.setOut(standardOut);
	}

	@Test
	@DisplayName("Should fire advice on international transfer")
	void testAdviceOnInternationalTransfer() {
		Account ac1 = new Account("ac1", 100d, Currency.NOK);
		Account ac2 = new Account("ac2", 100d, Currency.USD);
		bank.internationalTransfer(ac1, ac2, 3d);

		assertThat(aspectAppender.events).isNotEmpty()
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo("International transfer from ac1 to ac2, 3.0 NOK converted to USD"));
	}

	@Test
	@DisplayName("Should fire advice on domestic transfer")
	void testAdviceOnDomesticTransfer() {
		Account ac3 = new Account("ac3", 1_000_000d, Currency.NOK);
		Account ac4 = new Account("ac4", 40_000d, Currency.NOK);
		bank.domesticTransfer(ac3, ac4, 160_000d);

		assertThat(aspectAppender.events).isNotEmpty()
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo("Transfer above 100000.0 from ac3 to ac4, amount: 160000.0"));
	}

	@Test
	@DisplayName("Should fire advice on transfer with error")
	void testAdviceOnDomesticTransferError() {
		Account ac5 = new Account("ac5", 10_000d, Currency.NOK);
		Account ac6 = new Account("ac6", 40_000d, Currency.NOK);
		bank.domesticTransfer(ac5, ac6, 50_000d);

		assertThat(aspectAppender.events).isNotEmpty()
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo(
								"Error in transfer from ac5 to ac6, amount: 50000.0 NOK, method: domesticTransfer(fromDAcc, toDAcc, amount)"));
	}

	@Test
	@DisplayName("Should fire advice on domestic transfer with error")
	void testAdviceOnDomesticTransferErrorAndAbove() {
		Account ac5 = new Account("ac5", 10_000d, Currency.NOK);
		Account ac6 = new Account("ac6", 40_000d, Currency.NOK);
		bank.domesticTransfer(ac5, ac6, 150_000d);

		assertThat(aspectAppender.events).isNotEmpty()
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo("Transfer above 100000.0 from ac5 to ac6, amount: 150000.0"))
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo(
								"Error in transfer from ac5 to ac6, amount: 150000.0 NOK, method: domesticTransfer(fromDAcc, toDAcc, amount)"));
	}

	@Test
	@DisplayName("Should fire advice on international transfer with error")
	void testAdviceOnInternationalTransferError() {
		Account ac1 = new Account("ac1", 100d, Currency.NOK);
		Account ac2 = new Account("ac2", 100d, Currency.USD);
		bank.internationalTransfer(ac1, ac2, 200d);

		assertThat(aspectAppender.events).isNotEmpty()
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo("International transfer from ac1 to ac2, 200.0 NOK converted to USD"))
				.anySatisfy(event -> assertThat(event.getMessage())
						.isEqualTo(
								"Error in transfer from ac1 to ac2, amount: 200.0 NOK, method: internationalTransfer(fromIAcc, toIAcc, amount)"));
	}

	@Test
	@DisplayName("Should not fire advice without annotation")
	void shouldNotFireAdviceWithoutAnnotation() {
		Account ac1 = new Account("ac1", 100d, Currency.NOK);
		Account ac2 = new Account("ac2", 100d, Currency.USD);
		bank.withdrawFromMultipleAccounts(ac1, ac2, 3d);

		assertThat(aspectAppender.events).isEmpty();
	}
}