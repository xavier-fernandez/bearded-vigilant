/*
 * (C) Copyright 2015 Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
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
 *
 * Contributors:
 *      Xavier Fernández Salas (xavier.fernandez.salas@gmail.com)
 */

package com.bearded.common.time;

import android.test.InstrumentationTestCase;

import org.joda.time.DateTime;

public class TimeUtilsTest extends InstrumentationTestCase {

    private final static String ISO8601_EXAMPLE_STRING = "2015-05-01T11:06:54.601+02:00";

    /**
     * Test method for:
     *
     * @see TimeUtils#millisecondsFromNow(DateTime)
     */
    public void testMillisecondsFromNowUsingDateTime() throws Exception {
        final long timeToWait = 50l;
        final DateTime previousDate = DateTime.now().minus(timeToWait);
        final long millisecondsFromDate = TimeUtils.millisecondsFromNow(previousDate);
        assertTrue(millisecondsFromDate > timeToWait);
        // Check if the result milliseconds is less than one second, removing the possibility
        // of receiving extremely big results.
        // If the method last more than one second, there would be a big performance issue.
        assertTrue(millisecondsFromDate < 1000);
    }

    /**
     * Test method for:
     *
     * @see TimeUtils#millisecondsFromNow(long)
     */
    public void testMillisecondsFromNowUsingLong() throws Exception {
        final long millisecondsInThePast = 50l;
        final long previousTimestamp = DateTime.now().minus(millisecondsInThePast).getMillis();
        final long millisecondsFromTimestamp = TimeUtils.millisecondsFromNow(previousTimestamp);
        assertTrue(millisecondsFromTimestamp >= millisecondsInThePast);
        // Check if the result milliseconds is less than one second, removing the possibility
        // of receiving extremely big results.
        // If the method last more than one second, there would be a big performance issue.
        assertTrue(millisecondsFromTimestamp < 1000);
    }

    /**
     * Test method for:
     *
     * @see TimeUtils#nowToISOString()
     */
    public void testNowToISOString() throws Exception {
        final String timeInISO8601 = TimeUtils.nowToISOString();
        assertNotNull(timeInISO8601);
        // Check if the time in ISO 8601 haves valid length.
        // This test checks also that the method uses the extended representation with milliseconds.
        // found in http://es.wikipedia.org/wiki/ISO_8601
        assertTrue(timeInISO8601.length() == ISO8601_EXAMPLE_STRING.length());

    }

    /**
     * Test method for:
     *
     * @see TimeUtils#timestampToISOString(DateTime)
     */
    public void testTimestampToISOStringUsingDateTime() throws Exception {
        final DateTime dateTime = DateTime.now();
        assertEquals(dateTime.toString(), TimeUtils.timestampToISOString(dateTime));
    }

    /**
     * Test method for:
     *
     * @see TimeUtils#timestampToISOString(long)
     */
    public void testTimestampToISOStringUsingLong() throws Exception {
        final DateTime dateTime = DateTime.now();
        assertEquals(dateTime.toString(), TimeUtils.timestampToISOString(dateTime.getMillis()));

    }
}