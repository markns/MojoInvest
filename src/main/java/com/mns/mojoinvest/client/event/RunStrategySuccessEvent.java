package com.mns.mojoinvest.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;

public class RunStrategySuccessEvent extends GwtEvent<RunStrategySuccessEvent.RunStrategySuccessHandler> {

    RunStrategyResult runStrategyResult;
    boolean originator;

    protected RunStrategySuccessEvent() {
        // Possibly for serialization.
    }

    public RunStrategySuccessEvent(RunStrategyResult runStrategyResult, boolean originator) {
        this.runStrategyResult = runStrategyResult;
        this.originator = originator;
    }

    public static void fire(HasHandlers source, RunStrategyResult runStrategyResult, boolean originator) {
        RunStrategySuccessEvent eventInstance = new RunStrategySuccessEvent(runStrategyResult, originator);
        source.fireEvent(eventInstance);
    }

    public static void fire(HasHandlers source, RunStrategySuccessEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasRunStrategySuccessHandlers extends HasHandlers {
        HandlerRegistration addRunStrategySuccessHandler(RunStrategySuccessHandler handler);
    }

    public interface RunStrategySuccessHandler extends EventHandler {
        public void onRunStrategySuccess(RunStrategySuccessEvent event);
    }

    private static final Type<RunStrategySuccessHandler> TYPE = new Type<RunStrategySuccessHandler>();

    public static Type<RunStrategySuccessHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<RunStrategySuccessHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RunStrategySuccessHandler handler) {
        handler.onRunStrategySuccess(this);
    }

    public RunStrategyResult getRunStrategyResult() {
        return runStrategyResult;
    }

    public boolean isOriginator() {
        return originator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RunStrategySuccessEvent other = (RunStrategySuccessEvent) obj;
        if (runStrategyResult == null) {
            if (other.runStrategyResult != null)
                return false;
        } else if (!runStrategyResult.equals(other.runStrategyResult))
            return false;
        if (originator != other.originator)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 23;
        hashCode = (hashCode * 37) + (runStrategyResult == null ? 1 : runStrategyResult.hashCode());
        hashCode = (hashCode * 37) + Boolean.valueOf(originator).hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "RunStrategySuccessEvent["
                + runStrategyResult
                + ","
                + originator
                + "]";
    }
}
