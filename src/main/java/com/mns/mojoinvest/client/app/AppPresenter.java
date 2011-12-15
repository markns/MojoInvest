package com.mns.mojoinvest.client.app;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.*;

public class AppPresenter extends
        Presenter<AppPresenter.MyView, AppPresenter.MyProxy>
		implements AppUiHandlers {

	@ProxyStandard
	@NameToken(NameTokens.app)
	@UseGatekeeper(SignedInGatekeeper.class)
	public interface MyProxy extends ProxyPlace<AppPresenter> {
	}

	public interface MyView extends View, HasUiHandlers<AppUiHandlers> {

        public void resetAndFocus();

		public void setDefaultValues();

//		public void loadPerformanceData(Integer start, Integer length,
//                                        List<Performance> performances);

//		public void refreshPerformances();

//		public void setLocationData(List<Location> locations);
//
//		public void setShowData(List<Show> shows);

	}

	private final PlaceManager placeManager;
	private final DispatchAsync dispatcher;
	private ClientState clientState;

	@Inject
	public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        PlaceManager placeManager, DispatchAsync dispatcher,
                        final ClientState clientState) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		this.dispatcher = dispatcher;
		this.clientState = clientState;

		getView().setUiHandlers(this);

	}

	@Override
	protected void onReset() {
		super.onReset();
		getView().resetAndFocus();

	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent,
                this);
//		requestPerformances();
//
//		dispatcher.execute(new ReadShowsAction(clientState.currentTheaterKey),
//				new DispatchCallback<ReadShowsResult>() {
//					@Override
//					public void onSuccess(ReadShowsResult result) {
//						Main.logger.info(result.toString());
//						// TODO have just getLocations() instead of
//						// getLocations().locations, by using piriti-restlet
//						getView().setShowData(result.getShows().shows);
//
//					}
//				});
//
//		dispatcher.execute(new ReadLocationsAction(
//				clientState.currentTheaterKey),
//				new DispatchCallback<ReadLocationsResult>() {
//					@Override
//					public void onSuccess(ReadLocationsResult result) {
//						Main.logger.info(result.toString());
//						// TODO have just getLocations() instead of
//						// getLocations().locations, by using piriti-restlet
//						getView().setLocationData(
//								result.getLocations().locations);
//
//					}
//				});
//
	}

//	public void requestPerformances() {
//		// Strings.isNullOrEmpty(clientState.currentTheaterKey)
//		if (!(null == clientState.currentTheaterKey || clientState.currentTheaterKey
//				.isEmpty())) {
//			dispatcher.execute(new GetPerformancesAction(
//					clientState.currentTheaterKey),
//					new DispatchCallback<GetPerformancesResult>() {
//						@Override
//						public void onSuccess(GetPerformancesResult result) {
//							if (!result.getErrorText().isEmpty()) {
//								// TODO have a general handler for this
//								Window.alert(result.getErrorText());
//								return;
//							}
//							// getView().setPerformances(result.getPerformances());
//							// TODO result.getPageStart()
//							getView().loadPerformanceData(
//									Constants.visibleRangeStart,
//									result.getPerformances().size(),
//									result.getPerformances());
//						}
//					});
//		}
//
//	}
//
//	@Override
//	public void onRangeOrSizeChanged(Integer visibleRangeStart,
//			Integer visibleRangeLength) {
//		// usually, this should have requested a new set of data from server for
//		// visible range. Not needed on upcoming performances, just fetch them
//		// all
//		requestPerformances();
//	}

//	public void onPerformanceSelected(Performance p) {
//		Main.logger.info("Selected performance " + p.performanceKey
//				+ " with show " + p.showName);
//	}

//	@Override
//	public void createPerformance(Date date, String showName,
//			String locationName) {
//		Main.logger.info("Requested performance scheduling on "
//				+ date.toString() + ": show " + showName + " at location "
//				+ locationName + " for theater "
//				+ clientState.currentTheaterKey);
//		Performance p = new Performance();
//		p.date = date;
//		p.showName = showName;
//		p.locationName = locationName;
//
//		dispatcher.execute(new ManagePerformanceAction(
//				clientState.currentTheaterKey,
//				Constants.ManageActionType.CREATE, p),
//				new DispatchCallback<ManagePerformanceResult>() {
//					@Override
//					public void onSuccess(ManagePerformanceResult result) {
//						if (!result.getErrorText().isEmpty()) {
//							// TODO have a general handler for this
//							Window.alert(result.getErrorText());
//							return;
//						}
//						getView().setDefaultValues();
//						getView().refreshPerformances();
//					}
//				});
//
//	}

//	@Override
//	public void updatePerformance(String performanceKey, Date date,
//			String showName, String locationName) {
//		Main.logger.info("Requested performance update for " + performanceKey
//				+ " with date " + date.toString() + " the show " + showName
//				+ " at location " + locationName + " for theater "
//				+ clientState.currentTheaterKey);
//
//		Performance p = new Performance();
//		p.performanceKey = performanceKey;
//		p.date = date;
//		p.showName = showName;
//		p.locationName = locationName;
//
//		dispatcher.execute(new ManagePerformanceAction(
//				clientState.currentTheaterKey,
//				Constants.ManageActionType.UPDATE, p),
//				new DispatchCallback<ManagePerformanceResult>() {
//					@Override
//					public void onSuccess(ManagePerformanceResult result) {
//						if (!result.getErrorText().isEmpty()) {
//							// TODO have a general handler for this
//							Window.alert(result.getErrorText());
//							return;
//						}
//						getView().setDefaultValues();
//						getView().refreshPerformances();
//					}
//				});
//
//	}

//	@Override
//	public void deletePerformance(String performanceKey) {
//		Main.logger.info("Requested performance update for " + performanceKey);
//
//		Performance p = new Performance();
//		p.performanceKey = performanceKey;
//
//		dispatcher.execute(new ManagePerformanceAction(
//				clientState.currentTheaterKey,
//				Constants.ManageActionType.DELETE, p),
//				new DispatchCallback<ManagePerformanceResult>() {
//					@Override
//					public void onSuccess(ManagePerformanceResult result) {
//						if (!result.getErrorText().isEmpty()) {
//							// TODO have a general handler for this
//							Window.alert(result.getErrorText());
//							return;
//						}
//						getView().setDefaultValues();
//						getView().refreshPerformances();
//					}
//				});
//
//	}

}
