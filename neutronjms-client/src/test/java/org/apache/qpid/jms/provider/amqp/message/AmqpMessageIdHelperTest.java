/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.qpid.jms.provider.amqp.message;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.qpid.jms.exceptions.IdConversionException;
import org.apache.qpid.jms.provider.amqp.message.AmqpMessageIdHelper;
import org.apache.qpid.jms.test.QpidJmsTestCase;
import org.junit.Before;
import org.junit.Test;

public class AmqpMessageIdHelperTest extends QpidJmsTestCase {
    private AmqpMessageIdHelper _messageIdHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        _messageIdHelper = new AmqpMessageIdHelper();
    }

    /**
     * Test that {@link AmqpMessageIdHelper#hasMessageIdPrefix(String)} returns true for strings that begin "ID:"
     */
    @Test
    public void testHasIdPrefixWithPrefix() {
        String myId = "ID:something";
        assertTrue("'ID:' prefix should have been identified", _messageIdHelper.hasMessageIdPrefix(myId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#hasMessageIdPrefix(String)} returns false for string beings "ID" without colon.
     */
    @Test
    public void testHasIdPrefixWithIDButNoColonPrefix() {
        String myIdNoColon = "IDsomething";
        assertFalse("'ID' prefix should not have been identified without trailing colon", _messageIdHelper.hasMessageIdPrefix(myIdNoColon));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#hasMessageIdPrefix(String)} returns false for null
     */
    @Test
    public void testHasIdPrefixWithNull() {
        String nullString = null;
        assertFalse("null string should not result in identification as having the prefix", _messageIdHelper.hasMessageIdPrefix(nullString));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#hasMessageIdPrefix(String)} returns false for strings that doesnt have "ID:" anywhere
     */
    @Test
    public void testHasIdPrefixWithoutPrefix() {
        String myNonId = "something";
        assertFalse("string without 'ID:' anywhere should not have been identified as having the prefix", _messageIdHelper.hasMessageIdPrefix(myNonId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#hasMessageIdPrefix(String)} returns false for strings has lowercase "id:" prefix
     */
    @Test
    public void testHasIdPrefixWithLowercaseID() {
        String myLowerCaseNonId = "id:something";
        assertFalse("lowercase 'id:' prefix should not result in identification as having 'ID:' prefix", _messageIdHelper.hasMessageIdPrefix(myLowerCaseNonId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} strips "ID:" from strings that do begin "ID:"
     */
    @Test
    public void testStripMessageIdPrefixWithPrefix() {
        String myIdWithoutPrefix = "something";
        String myId = "ID:" + myIdWithoutPrefix;
        assertEquals("'ID:' prefix should have been stripped", myIdWithoutPrefix, _messageIdHelper.stripMessageIdPrefix(myId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} only strips one "ID:" from strings that
     * begin "ID:ID:...."
     */
    @Test
    public void testStripMessageIdPrefixWithDoublePrefix() {
        String myIdWithSinglePrefix = "ID:something";
        String myIdWithDoublePrefix = "ID:" + myIdWithSinglePrefix;
        assertEquals("'ID:' prefix should only have been stripped once", myIdWithSinglePrefix, _messageIdHelper.stripMessageIdPrefix(myIdWithDoublePrefix));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} does not alter strings that begins "ID" without a colon.
     */
    @Test
    public void testStripMessageIdPrefixWithIDButNoColonPrefix() {
        String myIdNoColon = "IDsomething";
        assertEquals("string without 'ID:' prefix should have been returned unchanged", myIdNoColon, _messageIdHelper.stripMessageIdPrefix(myIdNoColon));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} returns null if given null;
     */
    @Test
    public void testStripMessageIdPrefixWithNull() {
        String nullString = null;
        assertNull("null string should have been returned", _messageIdHelper.stripMessageIdPrefix(nullString));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} does not alter string that doesn't begin "ID:"
     */
    @Test
    public void testStripMessageIdPrefixWithoutIDAnywhere() {
        String myNonId = "something";
        assertEquals("string without 'ID:' anywhere should have been returned unchanged", myNonId, _messageIdHelper.stripMessageIdPrefix(myNonId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#stripMessageIdPrefix(String)} does not alter string with lowercase "id:"
     */
    @Test
    public void testStripMessageIdPrefixWithLowercaseID() {
        String myLowerCaseNonId = "id:something";
        assertEquals("string with lowercase 'id:' prefix should have been returned unchanged", myLowerCaseNonId, _messageIdHelper.stripMessageIdPrefix(myLowerCaseNonId));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns null if given null
     */
    @Test
    public void testToBaseMessageIdStringWithNull() {
        String nullString = null;
        assertNull("null string should have been returned", _messageIdHelper.toBaseMessageIdString(nullString));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} throws an IAE if given an unexpected object type.
     */
    @Test
    public void testToBaseMessageIdStringThrowsIAEWithUnexpectedType() {
        try {
            _messageIdHelper.toBaseMessageIdString(new Object());
            fail("expected exception not thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns the given
     * basic string unchanged
     */
    @Test
    public void testToBaseMessageIdStringWithString() {
        String stringMessageId = "myIdString";

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(stringMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", stringMessageId, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded string, when the given string happens to already begin with
     * the {@link AmqpMessageIdHelper#AMQP_UUID_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForUUID() {
        String uuidStringMessageId = AmqpMessageIdHelper.AMQP_UUID_PREFIX + UUID.randomUUID();
        String expected = AmqpMessageIdHelper.AMQP_STRING_PREFIX + uuidStringMessageId;

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(uuidStringMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded string, when the given string happens to already begin with
     * the {@link AmqpMessageIdHelper#AMQP_ULONG_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForLong() {
        String longStringMessageId = AmqpMessageIdHelper.AMQP_ULONG_PREFIX + Long.valueOf(123456789L);
        String expected = AmqpMessageIdHelper.AMQP_STRING_PREFIX + longStringMessageId;

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(longStringMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded string, when the given string happens to already begin with
     * the {@link AmqpMessageIdHelper#AMQP_BINARY_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForBinary() {
        String binaryStringMessageId = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + "0123456789ABCDEF";
        String expected = AmqpMessageIdHelper.AMQP_STRING_PREFIX + binaryStringMessageId;

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(binaryStringMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded string (effectively twice), when the given string happens to already begin with
     * the {@link AmqpMessageIdHelper#AMQP_STRING_PREFIX}.
     */
    @Test
    public void testToBaseMessageIdStringWithStringBeginningWithEncodingPrefixForString() {
        String stringMessageId = AmqpMessageIdHelper.AMQP_STRING_PREFIX + "myStringId";
        String expected = AmqpMessageIdHelper.AMQP_STRING_PREFIX + stringMessageId;

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(stringMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded UUID when given a UUID object.
     */
    @Test
    public void testToBaseMessageIdStringWithUUID() {
        UUID uuidMessageId = UUID.randomUUID();
        String expected = AmqpMessageIdHelper.AMQP_UUID_PREFIX + uuidMessageId.toString();

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(uuidMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded ulong when given a Long object.
     */
    @Test
    public void testToBaseMessageIdStringWithLong() {
        Long longMessageId = Long.valueOf(123456789L);
        String expected = AmqpMessageIdHelper.AMQP_ULONG_PREFIX + longMessageId.toString();

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(longMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded ulong when given a BigInteger object.
     */
    @Test
    public void testToBaseMessageIdStringWithBigInteger() {
        BigInteger bigIntMessageId = BigInteger.valueOf(123456789L);
        String expected = AmqpMessageIdHelper.AMQP_ULONG_PREFIX + bigIntMessageId.toString();

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(bigIntMessageId);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toBaseMessageIdString(String)} returns a string
     * indicating an AMQP encoded binary when given a ByteBuffer object.
     */
    @Test
    public void testToBaseMessageIdStringWithByteBufferBinary() {
        byte[] bytes = new byte[] { (byte) 0x00, (byte) 0xAB, (byte) 0x09, (byte) 0xFF };
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        String expected = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + "00AB09FF";

        String baseMessageIdString = _messageIdHelper.toBaseMessageIdString(buf);
        assertNotNull("null string should not have been returned", baseMessageIdString);
        assertEquals("expected base id string was not returned", expected, baseMessageIdString);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns a ulong
     * (represented as a BigInteger) when given a string indicating an
     * encoded AMQP ulong id.
     */
    @Test
    public void testToIdObjectWithEncodedUlong() throws Exception {
        BigInteger longId = BigInteger.valueOf(123456789L);
        String provided = AmqpMessageIdHelper.AMQP_ULONG_PREFIX + "123456789";

        Object idObject = _messageIdHelper.toIdObject(provided);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", longId, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns binary
     * (represented as a ByteBuffer) when given a string indicating an
     * encoded AMQP binary id, using upper case hex characters
     */
    @Test
    public void testToIdObjectWithEncodedBinaryUppercaseHexString() throws Exception {
        byte[] bytes = new byte[] { (byte) 0x00, (byte) 0xAB, (byte) 0x09, (byte) 0xFF };
        ByteBuffer binaryId = ByteBuffer.wrap(bytes);

        String provided = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + "00AB09FF";

        Object idObject = _messageIdHelper.toIdObject(provided);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", binaryId, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns null
     * when given null.
     */
    @Test
    public void testToIdObjectWithNull() throws Exception {
        assertNull("null object should have been returned", _messageIdHelper.toIdObject(null));
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns binary
     * (represented as a ByteBuffer) when given a string indicating an
     * encoded AMQP binary id, using lower case hex characters.
     */
    @Test
    public void testToIdObjectWithEncodedBinaryLowercaseHexString() throws Exception {
        byte[] bytes = new byte[] { (byte) 0x00, (byte) 0xAB, (byte) 0x09, (byte) 0xFF };
        ByteBuffer binaryId = ByteBuffer.wrap(bytes);

        String provided = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + "00ab09ff";

        Object idObject = _messageIdHelper.toIdObject(provided);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", binaryId, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns a UUID
     * when given a string indicating an encoded AMQP uuid id.
     */
    @Test
    public void testToIdObjectWithEncodedUuid() throws Exception {
        UUID uuid = UUID.randomUUID();
        String provided = AmqpMessageIdHelper.AMQP_UUID_PREFIX + uuid.toString();

        Object idObject = _messageIdHelper.toIdObject(provided);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", uuid, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns a string
     * when given a string without any type encoding prefix.
     */
    @Test
    public void testToIdObjectWithStringContainingNoEncodingPrefix() throws Exception {
        String stringId = "myStringId";

        Object idObject = _messageIdHelper.toIdObject(stringId);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", stringId, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} returns the remainder of the
     * provided string after removing the {@link AmqpMessageIdHelper#AMQP_STRING_PREFIX} prefix.
     */
    @Test
    public void testToIdObjectWithStringContainingStringEncodingPrefix() throws Exception {
        String suffix = "myStringSuffix";
        String stringId = AmqpMessageIdHelper.AMQP_STRING_PREFIX + suffix;

        Object idObject = _messageIdHelper.toIdObject(stringId);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", suffix, idObject);
    }

    /**
     * Test that when given a string with with the {@link AmqpMessageIdHelper#AMQP_STRING_PREFIX} prefix
     * and then additionally the {@link AmqpMessageIdHelper#AMQP_UUID_PREFIX}, the
     * {@link AmqpMessageIdHelper#toIdObject(String)} method returns the remainder of the provided string
     *  after removing the {@link AmqpMessageIdHelper#AMQP_STRING_PREFIX} prefix.
     */
    @Test
    public void testToIdObjectWithStringContainingStringEncodingPrefixAndThenUuidPrefix() throws Exception {
        String encodedUuidString = AmqpMessageIdHelper.AMQP_UUID_PREFIX + UUID.randomUUID().toString();
        String stringId = AmqpMessageIdHelper.AMQP_STRING_PREFIX + encodedUuidString;

        Object idObject = _messageIdHelper.toIdObject(stringId);
        assertNotNull("null object should not have been returned", idObject);
        assertEquals("expected id object was not returned", encodedUuidString, idObject);
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} throws an
     * {@link IdConversionException} when presented with an encoded binary hex string
     * of uneven length (after the prefix) that thus can't be converted due to each
     * byte using 2 characters
     */
    @Test
    public void testToIdObjectWithStringContainingBinaryHexThrowsICEWithUnevenLengthString() {
        String unevenHead = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + "123";

        try {
            _messageIdHelper.toIdObject(unevenHead);
            fail("expected exception was not thrown");
        } catch (IdConversionException iae) {
            // expected
            String msg = iae.getCause().getMessage();
            assertTrue("Message was not as expected: " + msg, msg.contains("even length"));
        }
    }

    /**
     * Test that {@link AmqpMessageIdHelper#toIdObject(String)} throws an
     * {@link IdConversionException} when presented with an encoded binary hex
     * string (after the prefix) that contains characters other than 0-9
     * and A-F and a-f, and thus can't be converted
     */
    @Test
    public void testToIdObjectWithStringContainingBinaryHexThrowsICEWithNonHexCharacters() {

        // char before '0'
        char nonHexChar = '/';
        String nonHexString = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + nonHexChar + nonHexChar;

        try {
            _messageIdHelper.toIdObject(nonHexString);
            fail("expected exception was not thrown");
        } catch (IdConversionException ice) {
            // expected
            String msg = ice.getCause().getMessage();
            assertTrue("Message was not as expected: " + msg, msg.contains("non-hex"));
        }

        // char after '9', before 'A'
        nonHexChar = ':';
        nonHexString = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + nonHexChar + nonHexChar;

        try {
            _messageIdHelper.toIdObject(nonHexString);
            fail("expected exception was not thrown");
        } catch (IdConversionException ice) {
            // expected
            String msg = ice.getCause().getMessage();
            assertTrue("Message was not as expected: " + msg, msg.contains("non-hex"));
        }

        // char after 'F', before 'a'
        nonHexChar = 'G';
        nonHexString = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + nonHexChar + nonHexChar;

        try {
            _messageIdHelper.toIdObject(nonHexString);
            fail("expected exception was not thrown");
        } catch (IdConversionException ice) {
            // expected
            String msg = ice.getCause().getMessage();
            assertTrue("Message was not as expected: " + msg, msg.contains("non-hex"));
        }

        // char after 'f'
        nonHexChar = 'g';
        nonHexString = AmqpMessageIdHelper.AMQP_BINARY_PREFIX + nonHexChar + nonHexChar;

        try {
            _messageIdHelper.toIdObject(nonHexString);
            fail("expected exception was not thrown");
        } catch (IdConversionException ice) {
            // expected
            String msg = ice.getCause().getMessage();
            assertTrue("Message was not as expected: " + msg, msg.contains("non-hex"));
        }
    }
}
