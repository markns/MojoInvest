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

    ImageResource selectedNavigation();

    ImageResource mojo_logo();

    // -------------------------------------------------------------------- CSS

    /**
     * Global CSS rules
     *
     * @return
     */
    @Source("mojo.css")
    CssResource mojo();

    /**
     * CSS classes to style GWT and custom widgets
     *
     * @return
     */
    @Source("widgets.css")
    CssResource widgets();


    public interface Navigation extends CssResource {
        @ClassName("selectedNavigationEntry")
        String selectedNavigationEntry();
        @ClassName("selectedDate")
        String selectedDate();
    }

    @Source("navigation.css")
    Navigation navigation();

}

