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

package com.mns.mojoinvest.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Mark Nuttall-Smith
 */
public interface Resources extends ClientBundle {

    @Source("mojo_logo.png")
    ImageResource logo();

    @Source("Mojoinvest.css")
    Style style();

    @Source("menuBarDownIcon.gif")
    public ImageResource menuBarDownIcon();

    public interface Style extends CssResource {
        // autogenerate, see
        // http://code.google.com/p/google-web-toolkit/wiki/CssResource#Automatically_generating_interfaces
        String anchor();

        String big();

        String block();

        String blue();

        String button();

        String container();

        String formButton();

        String green();

        String label();

        String listing();

        String medium();

        String orange();

        String required();

        String small();

    }

}

