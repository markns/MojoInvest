package com.mns.mojoinvest.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

import java.util.logging.Logger;

/**
 * Entry point of the application.
 *
 * @author Mark Nuttall-Smith
 */
public class MainEntryPoint implements EntryPoint {

    public final MainGinjector ginjector = GWT.create(MainGinjector.class);

    public static final Logger logger = Logger.getLogger("");

    public void onModuleLoad() {

        // This is required for Gwt-Platform proxy's generator.
        DelayedBindRegistry.bind(ginjector);

        ginjector.getResources().css().ensureInjected();
        ginjector.getResources().widgets().ensureInjected();
        ginjector.getPlaceManager().revealCurrentPlace();
    }

}
