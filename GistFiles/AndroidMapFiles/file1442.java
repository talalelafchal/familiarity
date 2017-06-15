/*
 * Copyright (C) 2005-2010 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


/**
 * A charset provider that only provides the {@code IBM437} character set,
 * also known as {@code CP437}.
 *
 * @author Christian Schlichtherle
 * @author Simon Lightfoot, modified for use with Android
 *
 * How to use:
 *
 * # Create a file called "java.nio.charset.spi.CharsetProvider" in
 *   src/main/resources/META-INF/servoices and place the canonical path
 *   to this class in it.
 *
 * # Now you can use Charset.forName("IBM437") in your code
 *   wherever a Charset is required.
 *
 * # Note: Make sure you add this class to Proguard if you have it enabled.
 *
 */
@SuppressWarnings("unused")
public class IBM437Provider
	extends CharsetProvider
{
	private static final Collection<Charset> mCharsets;
	private static final HashMap<String, Charset> mMapping;


	static{
		mCharsets = Collections.unmodifiableCollection(
			Collections.singletonList((Charset)new IBM437Charset()));
		mMapping = new HashMap<>();
		for(Charset charset : mCharsets){
			mMapping.put(lowerCase(charset.name()), charset);
			for(String s : charset.aliases()){
				mMapping.put(lowerCase(s), charset);
			}
		}
	}

	@Override
	public Charset charsetForName(String charsetName)
	{
		return mMapping.get(lowerCase(charsetName));
	}

	@Override
	public Iterator<Charset> charsets()
	{
		return mCharsets.iterator();
	}

	private static String lowerCase(String s)
	{
		return s.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * The {@code IBM437} character set, also known as {@code CP437}.
	 *
	 * @author Christian Schlichtherle
	 * @version $Id$
	 */
	public static final class IBM437Charset
		extends OctetCharset
	{
		private static final String NAME = "IBM437";
		private static final String[] ALIASES = { "cp437", "437", "csPC8CodePage437" }; // source: IANA

		// This table is printed from Sun's JDK 1.6.0-b105.
		// Note that this table does not match the official specification. :-(
		// However, compatibility to Sun's implementation is considered more
		// important.
		private static final char[] BYTE2CHAR = {
			// @formatter:off
			/*         0x00    0x01    0x02    0x03    0x04    0x05    0x06    0x07    0x08    0x09    0x0A    0x0B    0x0C    0x0D    0x0E    0x0F */
			/*0x00*/ 0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007, 0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x000E, 0x000F,
			/*0x10*/ 0x0010, 0x0011, 0x0012, 0x0013, 0x0014, 0x0015, 0x0016, 0x0017, 0x0018, 0x0019, 0x001A, 0x001B, 0x001C, 0x001D, 0x001E, 0x001F,
			/*0x20*/ 0x0020, 0x0021, 0x0022, 0x0023, 0x0024, 0x0025, 0x0026, 0x0027, 0x0028, 0x0029, 0x002A, 0x002B, 0x002C, 0x002D, 0x002E, 0x002F,
			/*0x30*/ 0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037, 0x0038, 0x0039, 0x003A, 0x003B, 0x003C, 0x003D, 0x003E, 0x003F,
			/*0x40*/ 0x0040, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047, 0x0048, 0x0049, 0x004A, 0x004B, 0x004C, 0x004D, 0x004E, 0x004F,
			/*0x50*/ 0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057, 0x0058, 0x0059, 0x005A, 0x005B, 0x005C, 0x005D, 0x005E, 0x005F,
			/*0x60*/ 0x0060, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067, 0x0068, 0x0069, 0x006A, 0x006B, 0x006C, 0x006D, 0x006E, 0x006F,
			/*0x70*/ 0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077, 0x0078, 0x0079, 0x007A, 0x007B, 0x007C, 0x007D, 0x007E, 0x007F,
			/*0x80*/ 0x00C7, 0x00FC, 0x00E9, 0x00E2, 0x00E4, 0x00E0, 0x00E5, 0x00E7, 0x00EA, 0x00EB, 0x00E8, 0x00EF, 0x00EE, 0x00EC, 0x00C4, 0x00C5,
			/*0x90*/ 0x00C9, 0x00E6, 0x00C6, 0x00F4, 0x00F6, 0x00F2, 0x00FB, 0x00F9, 0x00FF, 0x00D6, 0x00DC, 0x00A2, 0x00A3, 0x00A5, 0x20A7, 0x0192,
			/*0xA0*/ 0x00E1, 0x00ED, 0x00F3, 0x00FA, 0x00F1, 0x00D1, 0x00AA, 0x00BA, 0x00BF, 0x2310, 0x00AC, 0x00BD, 0x00BC, 0x00A1, 0x00AB, 0x00BB,
			/*0xB0*/ 0x2591, 0x2592, 0x2593, 0x2502, 0x2524, 0x2561, 0x2562, 0x2556, 0x2555, 0x2563, 0x2551, 0x2557, 0x255D, 0x255C, 0x255B, 0x2510,
			/*0xC0*/ 0x2514, 0x2534, 0x252C, 0x251C, 0x2500, 0x253C, 0x255E, 0x255F, 0x255A, 0x2554, 0x2569, 0x2566, 0x2560, 0x2550, 0x256C, 0x2567,
			/*0xD0*/ 0x2568, 0x2564, 0x2565, 0x2559, 0x2558, 0x2552, 0x2553, 0x256B, 0x256A, 0x2518, 0x250C, 0x2588, 0x2584, 0x258C, 0x2590, 0x2580,
			/*0xE0*/ 0x03B1, 0x00DF, 0x0393, 0x03C0, 0x03A3, 0x03C3, 0x00B5, 0x03C4, 0x03A6, 0x0398, 0x03A9, 0x03B4, 0x221E, 0x03C6, 0x03B5, 0x2229,
			/*0xF0*/ 0x2261, 0x00B1, 0x2265, 0x2264, 0x2320, 0x2321, 0x00F7, 0x2248, 0x00B0, 0x2219, 0x00B7, 0x221A, 0x207F, 0x00B2, 0x25A0, 0x00A0,
			// @formatter:on
		};

		public IBM437Charset()
		{
			super(NAME, ALIASES, BYTE2CHAR);
			Log.v("IBM437Charset", "instantiated " + displayName() + " " + aliases());
		}
	}


	/**
	 * A memory efficient base class for simple 8 bit (octet) character sets.
	 *
	 * @author Christian Schlichtherle
	 * @version $Id$
	 */
	public static abstract class OctetCharset
		extends Charset
	{

		/**
		 * Use this character in the lookup table provided to the constructor for
		 * every character that does not have a replacement in 16 bit Unicode.
		 */
		protected static final char REPLACEMENT = 0xFFFD;

		private final char[] byte2char;
		private final char[][] char2byte;

		protected OctetCharset(final String cname, final String[] aliases, final char[] byte2char)
		{
			super(cname, aliases);

			// Construct sparse inverse lookup table.
			final char[][] char2byte = new char[256][];
			for(char i = 0; i < 256; i++){
				final char c = byte2char[i];
				if(c == REPLACEMENT){
					continue;
				}

				final int hi = c >>> 8;
				final int lo = c & 0xFF;
				char[] table = char2byte[hi];
				if(table == null){
					table = new char[256];
					Arrays.fill(table, REPLACEMENT);
					char2byte[hi] = table;
				}
				table[lo] = i;
			}

			this.byte2char = byte2char;
			this.char2byte = char2byte;
		}

		public boolean contains(Charset cs)
		{
			return this.getClass().isInstance(cs);
		}

		public CharsetEncoder newEncoder()
		{
			return new Encoder();
		}

		protected class Encoder
			extends CharsetEncoder
		{
			protected Encoder()
			{
				super(OctetCharset.this, 1, 1);
			}

			protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out)
			{
				final char[][] c2b = char2byte;
				while(in.hasRemaining()){
					if(!out.hasRemaining()){
						return CoderResult.OVERFLOW;
					}
					final char c = in.get();
					final int hi = c >>> 8;
					final int lo = c & 0xFF;
					final char[] table = c2b[hi];
					final char b;
					if(table == null || (b = table[lo]) == REPLACEMENT){ // char is unsigned!
						in.position(in.position() - 1); // push back
						return CoderResult.unmappableForLength(1);
					}
					out.put((byte) b); // char is unsigned!
				}
				return CoderResult.UNDERFLOW;
			}
		}

		public CharsetDecoder newDecoder()
		{
			return new Decoder();
		}

		protected class Decoder
			extends CharsetDecoder
		{
			protected Decoder()
			{
				super(OctetCharset.this, 1, 1);
			}

			protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out)
			{
				final char[] b2c = byte2char;
				while(in.hasRemaining()){
					if(!out.hasRemaining()){
						return CoderResult.OVERFLOW;
					}
					final char c = b2c[in.get() & 0xFF];
					if(c == REPLACEMENT){
						in.position(in.position() - 1); // push back
						return CoderResult.unmappableForLength(1);
					}
					out.put(c);
				}
				return CoderResult.UNDERFLOW;
			}
		}
	}
}
