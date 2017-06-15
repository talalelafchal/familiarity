import com.nezo.quote.Quote;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestQuotes extends TestCase {

	@Before
	protected void setUp() {
	}

	@Test
	public void testQuoteCreated() throws Exception {
		Assert.assertNotNull(Quote.getInstance().getQuote(0));
	}

	@Test
	public void testQuoteWasStored() throws Exception {
		String quote = "These aren't the droids you're looking for.";
		Assert.assertEquals(quote, Quote.getInstance().getQuote(0));
		String quote2 = "If someone says \"Run\", Run!";
		Assert.assertEquals(quote2, Quote.getInstance().getQuote(14));
	}

	@Test
	public void testQuotesWereStored() throws Exception {
		List<String> quotes = Quote.getInstance().getListOfQuotes();
		Assert.assertEquals(15, quotes.size());
	}
	@Test
	public void testSingleton() throws Exception{
		Assert.assertEquals(Quote.getInstance(), Quote.getInstance());
	}
}
