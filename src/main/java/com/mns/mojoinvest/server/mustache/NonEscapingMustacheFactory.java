package com.mns.mojoinvest.server.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;

import java.io.IOException;
import java.io.Writer;

public class NonEscapingMustacheFactory extends DefaultMustacheFactory {

    @Override
    public void encode(String value, Writer writer) {
        try {
            int position = 0;
            int length = value.length();
//            for (int i = 0; i < length; i++) {
//                char c = value.charAt(i);
//                switch (c) {
//                    case '&':
//                        if (!escapedPattern.matcher(value.substring(i, length)).find()) {
//                            position = append(value, writer, position, i, "&amp;");
//                        } else {
//                            if (position != 0) {
//                                position = append(value, writer, position, i, "&");
//                            }
//                        }
//                        break;
//                    case '\\':
//                        position = append(value, writer, position, i, "\\\\");
//                        break;
//                    case '"':
//                        position = append(value, writer, position, i, "&quot;");
//                        break;
//                    case '<':
//                        position = append(value, writer, position, i, "&lt;");
//                        break;
//                    case '>':
//                        position = append(value, writer, position, i, "&gt;");
//                        break;
//                    case '\n':
//                        position = append(value, writer, position, i, "&#10;");
//                        break;
//                }
//            }
            writer.append(value, position, length);
        } catch (IOException e) {
            throw new MustacheException("Failed to encode value: " + value);
        }
    }
}
