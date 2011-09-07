package com.mns.alphaposition.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.mns.alphaposition.client.gin.ClientGinjector;

/**
 * Entry point of the application.
 *
 * @author Mark Nuttall-Smith
 */
public class Alphaposition implements EntryPoint {

    public final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

    public void onModuleLoad() {

        // This is required for Gwt-Platform proxy's generator.
        DelayedBindRegistry.bind(ginjector);

//        ginjector.getResources().style().ensureInjected();
        ginjector.getPlaceManager().revealCurrentPlace();
    }

}
