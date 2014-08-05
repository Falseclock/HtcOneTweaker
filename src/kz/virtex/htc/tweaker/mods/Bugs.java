package kz.virtex.htc.tweaker.mods;

import android.content.pm.ApplicationInfo;
import android.os.SystemClock;
import android.util.EventLog;
import android.util.Slog;

import com.android.server.am.EventLogTags;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class Bugs
{
	public static void killServicesLocked()
	{
		final Class<?> ActiveServices = XposedHelpers.findClass("com.android.server.am.ActiveServices", null);

		XposedHelpers.findAndHookMethod(ActiveServices, "killServicesLocked", "com.android.server.am.ProcessRecord", "boolean", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean DEBUG_SERVICE = (Boolean) XposedHelpers.getStaticBooleanField(ActiveServices, "DEBUG_SERVICE");
				String TAG = (String) XposedHelpers.getStaticObjectField(ActiveServices, "TAG");

				Object app = param.args[0];
				boolean allowRestart = (Boolean) param.args[1];
				Object services = XposedHelpers.getObjectField(app, "services");
				int size = (Integer) XposedHelpers.callMethod(services, "size");

				// First clear app state from services.
				for (int i = size - 1; i >= 0; i--) {
					Object sr = XposedHelpers.callMethod(services, "valueAt", i);
					Object stats = XposedHelpers.getObjectField(sr, "stats");
					synchronized (XposedHelpers.callMethod(stats, "getBatteryStats")) {
						XposedHelpers.callMethod(stats, "stopLaunchedLocked");
					}
					Object sr_app = XposedHelpers.getObjectField(sr, "app");
					Boolean persistent = XposedHelpers.getBooleanField(sr_app, "persistent");
					Boolean stopIfKilled = XposedHelpers.getBooleanField(sr, "stopIfKilled");

					if (sr_app != null && !persistent && stopIfKilled) {
						Object sr_app_services = XposedHelpers.getObjectField(sr_app, "services");
						XposedHelpers.callMethod(sr_app_services, "remove", sr);
					}
					XposedHelpers.setObjectField(sr, "app", null);
					XposedHelpers.setObjectField(sr, "isolatedProc", null);
					XposedHelpers.setObjectField(sr, "executeNesting", 0);
					XposedHelpers.callMethod(sr, "forceClearTracker");

					Object mDestroyingServices = XposedHelpers.getObjectField(param.thisObject, "mDestroyingServices");
					Boolean check = (Boolean) XposedHelpers.callMethod(mDestroyingServices, "remove", sr);
					if (check) {
						if (DEBUG_SERVICE)
							Slog.v(TAG, "killServices remove destroying " + sr);
					}

					Object bindings = XposedHelpers.getObjectField(sr, "bindings");
					final int numClients = (Integer) XposedHelpers.callMethod(bindings, "size");
					for (int bindingi = numClients - 1; bindingi >= 0; bindingi--) {
						Object IntentBindRecord = XposedHelpers.callMethod(bindings, "valueAt", bindingi);
						if (DEBUG_SERVICE)
							Slog.v(TAG, "Killing binding " + IntentBindRecord + ": shouldUnbind=" + XposedHelpers.getObjectField(IntentBindRecord, "hasBound"));

						XposedHelpers.setObjectField(IntentBindRecord, "binder", null);
						XposedHelpers.setObjectField(IntentBindRecord, "requested", false);
						XposedHelpers.setObjectField(IntentBindRecord, "received", false);
						XposedHelpers.setObjectField(IntentBindRecord, "hasBound", false);
					}
				}

				// Clean up any connections this application has to other
				// services.
				Object connections = XposedHelpers.getObjectField(app, "connections");
				size = (Integer) XposedHelpers.callMethod(connections, "size");
				for (int i = size - 1; i >= 0; i--) {
					Object ConnectionRecord = XposedHelpers.callMethod(connections, "valueAt", i);

					XposedHelpers.callMethod(param.thisObject, "removeConnectionLocked", ConnectionRecord, app, null);
				}
				XposedHelpers.callMethod(connections, "clear");

				Object smap = XposedHelpers.callMethod(param.thisObject, "getServiceMap", XposedHelpers.getObjectField(app, "userId"));

				// Now do remaining service cleanup.
				services = XposedHelpers.getObjectField(app, "services");
				size = (Integer) XposedHelpers.callMethod(services, "size");
				for (int i = size - 1; i >= 0; i--) {
					Object sr = XposedHelpers.callMethod(services, "valueAt", i);
					Object mServicesByName = XposedHelpers.getObjectField(smap, "mServicesByName");
					if (XposedHelpers.callMethod(mServicesByName, "get", XposedHelpers.getObjectField(sr, "name")) != sr) {
						Object cur = XposedHelpers.callMethod(mServicesByName, "get", XposedHelpers.getObjectField(sr, "name"));
						Slog.wtf(TAG, "Service " + sr + " in process " + app + " not same as in map: " + cur);
						Object app_services = XposedHelpers.getObjectField(app, "services");
						XposedHelpers.callMethod(app_services, "removeAt", i);
						continue;
					}
					// Any services running in the application may need to be
					// placed back in the pending list.
					Object serviceInfo = XposedHelpers.getObjectField(sr, "serviceInfo");
					Object applicationInfo = XposedHelpers.getObjectField(serviceInfo, "applicationInfo");
					if (allowRestart && XposedHelpers.getIntField(sr, "crashCount") >= 2 && (XposedHelpers.getIntField(applicationInfo, "flags") & ApplicationInfo.FLAG_PERSISTENT) == 0) {
						Slog.w(TAG, "Service crashed " + XposedHelpers.getIntField(sr, "crashCount") + " times, stopping: " + sr);
						EventLog.writeEvent(EventLogTags.AM_SERVICE_CRASHED_TOO_MUCH, XposedHelpers.getObjectField(sr, "userId"), XposedHelpers.getObjectField(sr, "crashCount"), XposedHelpers.getObjectField(sr, "shortName"), XposedHelpers.getObjectField(app, "pid"));
						XposedHelpers.callMethod(param.thisObject, "bringDownServiceLocked", sr);
					} else if (!allowRestart) {
						XposedHelpers.callMethod(param.thisObject, "bringDownServiceLocked", sr);
					} else {
						boolean canceled = (Boolean) XposedHelpers.callMethod(param.thisObject, "scheduleServiceRestartLocked", sr, true);
						// Should the service remain running? Note that in the
						// extreme case of so many attempts to deliver a command
						// that it failed we also will stop it here.
						if (XposedHelpers.getBooleanField(sr, "startRequested") && (XposedHelpers.getBooleanField(sr, "stopIfKilled") || canceled)) {
							Object pendingStarts = XposedHelpers.getObjectField(sr, "pendingStarts");
							if ((Integer) XposedHelpers.callMethod(pendingStarts, "size") == 0) {
								XposedHelpers.setBooleanField(sr, "startRequested", false);
								if (XposedHelpers.getObjectField(sr, "tracker") != null) {
									Object tracker = XposedHelpers.getObjectField(sr, "tracker");
									Object mAm = XposedHelpers.getObjectField(param.thisObject, "mAm");
									Object mProcessStats = XposedHelpers.getObjectField(mAm, "mProcessStats");
									XposedHelpers.callMethod(tracker, "setStarted", false, XposedHelpers.callMethod(mProcessStats, "getMemFactorLocked"), SystemClock.uptimeMillis());
								}
								if (!XposedHelpers.getBooleanField(sr, "hasAutoCreateConnections")) {
									// Whoops, no reason to restart!
									XposedHelpers.callMethod(param.thisObject, "bringDownServiceLocked", sr);
								}
							}
						}
					}
				}

				if (!allowRestart) {
					Object app_services = XposedHelpers.getObjectField(app, "services");
					XposedHelpers.callMethod(app_services, "clear");

					// Make sure there are no more restarting services for this
					// process.
					Object mRestartingServices = XposedHelpers.getObjectField(param.thisObject, "mRestartingServices");

					for (int i = (Integer) XposedHelpers.callMethod(mRestartingServices, "size") - 1; i >= 0; i--) {
						Object r = XposedHelpers.callMethod(mRestartingServices, "get", i);
						String processName = (String) XposedHelpers.getObjectField(r, "processName");
						Object serviceInfo = XposedHelpers.getObjectField(r, "serviceInfo");
						Object applicationInfo = XposedHelpers.getObjectField(serviceInfo, "applicationInfo");
						Object info = XposedHelpers.getObjectField(app, "info");
						if (processName.equals((String) XposedHelpers.getObjectField(app, "processName")) && XposedHelpers.getIntField(applicationInfo, "uid") == XposedHelpers.getIntField(info, "uid")) {
							XposedHelpers.callMethod(mRestartingServices, "remove", i);
							XposedHelpers.callMethod(param.thisObject, "clearRestartingIfNeededLocked", r);
						}
					}
					Object mPendingServices = XposedHelpers.getObjectField(param.thisObject, "mPendingServices");
					for (int i = (Integer) XposedHelpers.callMethod(mPendingServices, "size") - 1; i >= 0; i--) {
						Object r = XposedHelpers.callMethod(mPendingServices, "get", i);

						String processName = (String) XposedHelpers.getObjectField(r, "processName");
						Object serviceInfo = XposedHelpers.getObjectField(r, "serviceInfo");
						Object applicationInfo = XposedHelpers.getObjectField(serviceInfo, "applicationInfo");
						Object info = XposedHelpers.getObjectField(app, "info");
						if (processName.equals((String) XposedHelpers.getObjectField(app, "processName")) && XposedHelpers.getIntField(applicationInfo, "uid") == XposedHelpers.getIntField(info, "uid")) {
							XposedHelpers.callMethod(mPendingServices, "remove", i);
						}
					}
				}
				// Make sure we have no more records on the stopping list.
				Object mDestroyingServices = XposedHelpers.getObjectField(param.thisObject, "mDestroyingServices");
				int i = (Integer) XposedHelpers.callMethod(mDestroyingServices, "size");
				while (i > 0) {
					i--;
					Object sr = XposedHelpers.callMethod(mDestroyingServices, "get", i);
					if (XposedHelpers.getObjectField(sr, "app") == app) {
						XposedHelpers.callMethod(sr, "forceClearTracker");
						XposedHelpers.callMethod(mDestroyingServices, "remove", i);
						if (DEBUG_SERVICE)
							Slog.v(TAG, "killServices remove destroying " + sr);
					}
				}
				Object executingServices = XposedHelpers.getObjectField(app, "executingServices");
				XposedHelpers.callMethod(executingServices, "clear");

				return null;
			}
		});
	}
}
