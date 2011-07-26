/**
 * Copyright 2010 Mark Nuttall-Smith
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

package com.mns.alphaposition.shared.util;

/**
 * @author Mark Nuttall-Smith
 */
public class Validation {

    /**
     * Make sure the passed string is a potentially valid email address.
     *
     * @param text The email address to check
     * @return {@code true} if the email address is correctly formatted and potentially valid, {@code false} otherwise
     */
    public static boolean validateEmail(String text) {

        if (text.length() > 255) {
            return false;
        }
        int atPos = text.indexOf('@');
        if (atPos < 1 || atPos > 64 || atPos >= text.length() - 1) {
            return false;
        }
        if (text.charAt(atPos - 1) == '.' || text.charAt(text.length() - 1) == '.') {
            return false;
        }

        // Check local part
        boolean nextDotValid = false;
        for (int i = 0; i < atPos; ++i) {
            char c = text.charAt(i);
            boolean isOk = false;
            isOk = isOk || Character.isLetter(c);
            isOk = isOk || Character.isDigit(c);
            isOk = isOk || c == '!' || c == '#' || c == '$' || c == '%' || c == '&'
                    || c == '*' || c == '+' || c == '-' || c == '/' || c == '=' || c == '?'
                    || c == '^' || c == '_' || c == '`' || c == '{' || c == '|' || c == '}' || c == '~';
            if (c == '.') {
                isOk = nextDotValid;
                nextDotValid = false;
            } else {
                nextDotValid = true;
            }

            if (!isOk) {
                return false;
            }
        }

        // Check domain part
        nextDotValid = false;
        int nbDomains = 1;
        for (int i = atPos + 1; i < text.length(); ++i) {
            char c = text.charAt(i);
            boolean isOk = false;
            isOk = isOk || Character.isLetter(c);
            if (nextDotValid) {
                isOk = isOk || Character.isDigit(c);
                isOk = isOk || c == '-';
            }

            if (c == '.') {
                isOk = nextDotValid;
                if (text.charAt(i - 1) == '-') {
                    isOk = false;
                }
                nextDotValid = false;
                nbDomains++;
            } else {
                nextDotValid = true;
            }

            if (!isOk) {
                return false;
            }
        }

        if (nbDomains < 2 || nbDomains > 5) {
            return false;
        }

        return true;
    }

    /**
     * Make sure the passed string is non-null and of the correct length.
     *
     * @param string    String string to validate
     * @param minLength Minimum string length (inclusive)
     * @param maxLength Maximum string length (inclusive)
     * @return <code>true</code> if the string is valid, <code>false</code> otherwise
     */
    public static boolean validateString(String string,
                                         int minLength, int maxLength) {
        int length = string.length();
        return string != null && length >= minLength && length <= maxLength;
    }

}
