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

package com.mns.mojoinvest.shared.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Important!</b> This file has to be kept up to date and translated
 * whenever a new locale is added.
 * <p/>
 * This class holds information on all the available locales.
 *
 * @author Mark Nuttall-Smith
 */
@Singleton
public class AvailableLocales {

    /**
     * A class holding a number of information fields on a supported locale.
     *
     * @author Mark Nuttall-Smith
     */
    public static class LocaleInfo {
        private final String locale;
        private final String name;
        private final String switchMessage;

        /**
         * Creates information on a supported locale.
         *
         * @param locale        The locale identifier, an IETF language tag.
         * @param name          The name of the locale, in the locale's language.
         * @param switchMessage A message prompting to switch to that locale, in that locale's language.
         */
        private LocaleInfo(String locale, String name, String switchMessage) {
            this.locale = locale;
            this.name = name;
            this.switchMessage = switchMessage;
        }

        /**
         * @return The locale identifier, an IETF language tag.
         */
        public String getLocale() {
            return locale;
        }

        /**
         * @return The name of the locale, in the locale's language.
         */
        public String getName() {
            return name;
        }

        /**
         * @return A message prompting to switch to that locale, in that locale's language.
         */
        public String getSwitchMessage() {
            return switchMessage;
        }
    }

    private final List<LocaleInfo> localeInfos = new ArrayList<LocaleInfo>();

    @Inject
    public AvailableLocales() {
        // Always add the default locale first
        localeInfos.add(new LocaleInfo("en", "english", "Switch to english?"));
        localeInfos.add(new LocaleInfo("fr", "fran\u00e7ais", "Passer au fran\u00e7ais?"));
    }

    /**
     * Finds the best matching locale in the list of locales. Given the two-letter locale 'en', the algorithm
     * will prefer:
     * 1) 'en'
     * 2) 'en-US', 'en-CA'
     * 3) The first locale in the list
     * Given the four-letter local 'en-US', the algorithm will prefer:
     * 1) 'en-US'
     * 2) 'en'
     * 3) 'en-CA'
     * 4) The first locale in the list
     * If more than one available locale matches at the same preference level, the first one encountered will
     * be returned.
     *
     * @param locale The desired locale.
     * @return The {@link LocaleInfo} of the best matching locale.
     */
    public LocaleInfo getBestLocale(final String locale) {
        LocaleInfo result = localeInfos.get(0);
        int preferenceLevel = 4;

        String shortLocale = null;
        if (locale.length() > 2) {
            shortLocale = locale.substring(0, 2);
        }

        for (LocaleInfo info : localeInfos) {
            final String infoLocale = info.getLocale();

            if (infoLocale.equalsIgnoreCase(locale)) {
                // Perfect match, nothing can beat this.
                return info;
            }

            if (preferenceLevel <= 2) {
                // Preference is the smallest possible, only a perfect match can win.
                continue;
            }

            if (shortLocale != null) {
                // Desired locale is 4-letters
                if (infoLocale.equalsIgnoreCase(shortLocale)) {
                    result = info;
                    preferenceLevel = 2;
                } else if (preferenceLevel > 3 &&
                        infoLocale.substring(0, 2).equalsIgnoreCase(shortLocale)) {
                    result = info;
                    preferenceLevel = 3;
                }
            } else {
                // Desired locale is 2-letters
                if (infoLocale.substring(0, 2).equalsIgnoreCase(locale)) {
                    result = info;
                    preferenceLevel = 2;
                }
            }
        }

        return result;
    }

    /**
     * @return The number of available locales
     */
    public int getNbLocales() {
        return localeInfos.size();
    }

    /**
     * Access a locale given its index.
     *
     * @param index The index of the locale to access
     * @return The corresponding {@link LocaleInfo}.
     */
    public LocaleInfo getLocale(int index) {
        return localeInfos.get(index);
    }

    /**
     * Finds the index of the locale matching the passed parameter.
     *
     * @param locale The locale to look for, an IETF language tag.
     * @return The index of the found locale, -1 if none is found.
     */
    public int findLocaleIndex(String locale) {

        for (int i = 0; i < localeInfos.size(); ++i) {
            if (localeInfos.get(i).getLocale().equalsIgnoreCase(locale)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return The index of the default locale, always 0 since the first locale is the default one.
     */
    public int getDefaultLocaleIndex() {
        return 0;
    }
}
