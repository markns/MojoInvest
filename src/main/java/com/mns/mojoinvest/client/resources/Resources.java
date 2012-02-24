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
import com.google.gwt.user.cellview.client.CellTable;

/**
 * @author Mark Nuttall-Smith
 */
public interface Resources extends ClientBundle {

    /**
     * Global CSS rules
     *
     * @return
     */
    @Source("mojo-gwt.css")
    ScratchPad css();

    interface ScratchPad extends CssResource {

        String main();

        String left();

        String right();

        String clear();


    }

    /**
     * CSS classes to style GWT and custom widgets
     *
     * @return
     */
    @Source("widgets.css")
    @CssResource.NotStrict
    CssResource widgets();


    interface TableResources extends CellTable.Resources {
        @Source({CellTable.Style.DEFAULT_CSS, "celltable.css"})
        TableStyle cellTableStyle();
    }

    interface TableStyle extends CellTable.Style {
    }
}

