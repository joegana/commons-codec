/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.binary;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Tests {@link org.apache.commons.codec.binary.Hex}.
 * 
 * @author Apache Software Foundation
 * @version $Id$
 */
public class HexTest extends TestCase {

    private final static boolean LOG = false;

    public HexTest(String name) {
        super(name);
    }

    /**
     * @param data
     */
    private void checkDecodeHexOddCharacters(char[] data) {
        try {
            Hex.decodeHex(data);
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    private void log(String s) {
        if (LOG) {
            System.out.println(s);
        }
    }

    public void testCustomCharset() throws UnsupportedEncodingException, DecoderException {
        SortedMap map = Charset.availableCharsets();
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        log("testCustomCharset: Checking " + keys.size() + " charsets...");
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            testCustomCharset(name, "testCustomCharset");
        }
    }

    /**
     * @param name
     * @param parent
     *            TODO
     * @throws UnsupportedEncodingException
     * @throws DecoderException
     */
    private void testCustomCharset(String name, String parent) throws UnsupportedEncodingException, DecoderException {
        if (charsetSanityCheck(name) == false) {
            log("Interesting Java charset oddity: Charset sanity check failed for " + name);
            return;
        }
        log(parent + "=" + name);
        Hex customCodec = new Hex(name);
        // source data
        String sourceString = "Hello World";
        byte[] sourceBytes = sourceString.getBytes(name);
        // test 1
        // encode source to hex string to bytes with charset
        byte[] actualEncodedBytes = customCodec.encode(sourceBytes);
        // encode source to hex string...
        String expectedHexString = Hex.encodeHexString(sourceBytes);
        // ... and get the bytes in the expected charset
        byte[] expectedHexStringBytes = expectedHexString.getBytes(name);
        Assert.assertTrue(Arrays.equals(expectedHexStringBytes, actualEncodedBytes));
        // test 2
        String actualStringFromBytes = new String(actualEncodedBytes, name);
        assertEquals(name + ", expectedHexString=" + expectedHexString + ", actualStringFromBytes=" + actualStringFromBytes,
                expectedHexString, actualStringFromBytes);
        // second test:
        Hex utf8Codec = new Hex();
        expectedHexString = "48656c6c6f20576f726c64";
        byte[] decodedUtf8Bytes = (byte[]) utf8Codec.decode(expectedHexString);
        actualStringFromBytes = new String(decodedUtf8Bytes, utf8Codec.getCharsetName());
        // sanity check:
        assertEquals(name, sourceString, actualStringFromBytes);
        // actual check:
        byte[] decodedCustomBytes = customCodec.decode(actualEncodedBytes);
        actualStringFromBytes = new String(decodedCustomBytes, name);
        assertEquals(name, sourceString, actualStringFromBytes);
    }

    private boolean charsetSanityCheck(String name) {
        final String source = "the quick brown dog jumped over the lazy fox";
        try {
            byte[] bytes = source.getBytes(name);
            String str = new String(bytes, name);
            return source.equals(str);
        } catch (UnsupportedEncodingException e) {
            // Should NEVER happen since we are getting the name from the Charset class.
            if (LOG) {
                e.printStackTrace();
            }
            return false;
        } catch (UnsupportedOperationException e) {
            // Happens with:
            // x-JIS0208 on Windows XP and Java Sun 1.6.0_14
            if (LOG) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void testDecodeArrayOddCharacters() {
        try {
            new Hex().decode(new byte[]{65});
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeBadCharacterPos0() {
        try {
            new Hex().decode("q0");
            fail("An exception wasn't thrown when trying to decode an illegal character");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeBadCharacterPos1() {
        try {
            new Hex().decode("0q");
            fail("An exception wasn't thrown when trying to decode an illegal character");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeClassCastException() {
        try {
            new Hex().decode(new int[]{65});
            fail("An exception wasn't thrown when trying to decode.");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDecodeHexOddCharacters1() {
        checkDecodeHexOddCharacters(new char[]{'A'});
    }

    public void testDecodeHexOddCharacters3() {
        checkDecodeHexOddCharacters(new char[]{'A', 'B', 'C'});
    }

    public void testDecodeHexOddCharacters5() {
        checkDecodeHexOddCharacters(new char[]{'A', 'B', 'C', 'D', 'E'});
    }

    public void testDecodeStringOddCharacters() {
        try {
            new Hex().decode("6");
            fail("An exception wasn't thrown when trying to decode an odd number of characters");
        } catch (DecoderException e) {
            // Expected exception
        }
    }

    public void testDencodeEmpty() throws DecoderException {
        assertTrue(Arrays.equals(new byte[0], Hex.decodeHex(new char[0])));
        assertTrue(Arrays.equals(new byte[0], new Hex().decode(new byte[0])));
        assertTrue(Arrays.equals(new byte[0], (byte[]) new Hex().decode("")));
    }

    public void testEncodeClassCastException() {
        try {
            new Hex().encode(new int[]{65});
            fail("An exception wasn't thrown when trying to encode.");
        } catch (EncoderException e) {
            // Expected exception
        }
    }

    public void testEncodeDecodeRandom() throws DecoderException, EncoderException {
        Random random = new Random();

        Hex hex = new Hex();
        for (int i = 5; i > 0; i--) {
            byte[] data = new byte[random.nextInt(10000) + 1];
            random.nextBytes(data);

            // static API
            char[] encodedChars = Hex.encodeHex(data);
            byte[] decodedBytes = Hex.decodeHex(encodedChars);
            assertTrue(Arrays.equals(data, decodedBytes));

            // instance API with array parameter
            byte[] encodedStringBytes = hex.encode(data);
            decodedBytes = hex.decode(encodedStringBytes);
            assertTrue(Arrays.equals(data, decodedBytes));

            // instance API with char[] (Object) parameter
            String dataString = new String(encodedChars);
            char[] encodedStringChars = (char[]) hex.encode(dataString);
            decodedBytes = (byte[]) hex.decode(encodedStringChars);
            assertTrue(Arrays.equals(StringUtils.getBytesUtf8(dataString), decodedBytes));

            // instance API with String (Object) parameter
            dataString = new String(encodedChars);
            encodedStringChars = (char[]) hex.encode(dataString);
            decodedBytes = (byte[]) hex.decode(new String(encodedStringChars));
            assertTrue(Arrays.equals(StringUtils.getBytesUtf8(dataString), decodedBytes));
        }
    }

    public void testEncodeEmpty() throws EncoderException {
        assertTrue(Arrays.equals(new char[0], Hex.encodeHex(new byte[0])));
        assertTrue(Arrays.equals(new byte[0], new Hex().encode(new byte[0])));
        assertTrue(Arrays.equals(new char[0], (char[]) new Hex().encode("")));
    }

    public void testEncodeZeroes() {
        char[] c = Hex.encodeHex(new byte[36]);
        assertEquals("000000000000000000000000000000000000000000000000000000000000000000000000", new String(c));
    }

    public void testHelloWorldLowerCaseHex() {
        byte[] b = StringUtils.getBytesUtf8("Hello World");
        final String expected = "48656c6c6f20576f726c64";
        char[] actual;
        actual = Hex.encodeHex(b);
        assertTrue(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, true);
        assertTrue(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, false);
        assertFalse(expected.equals(new String(actual)));
    }

    public void testHelloWorldUpperCaseHex() {
        byte[] b = StringUtils.getBytesUtf8("Hello World");
        final String expected = "48656C6C6F20576F726C64";
        char[] actual;
        actual = Hex.encodeHex(b);
        assertFalse(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, true);
        assertFalse(expected.equals(new String(actual)));
        actual = Hex.encodeHex(b, false);
        assertTrue(expected.equals(new String(actual)));
    }

    public void testRequiredCharset() throws UnsupportedEncodingException, DecoderException {
        testCustomCharset("UTF-8", "testRequiredCharset");
        testCustomCharset("UTF-16", "testRequiredCharset");
        testCustomCharset("UTF-16BE", "testRequiredCharset");
        testCustomCharset("UTF-16LE", "testRequiredCharset");
        testCustomCharset("US-ASCII", "testRequiredCharset");
        testCustomCharset("ISO8859_1", "testRequiredCharset");
    }
}
