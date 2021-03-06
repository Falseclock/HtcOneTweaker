package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;
import java.util.Locale;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.TweakerService;
import kz.virtex.htc.tweaker.XMain;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XModuleResources;
import android.os.AsyncResult;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallerInfo;
import com.android.internal.telephony.Connection;
import com.htc.widget.HtcIconButton;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Recorder
{
	// AndroidAppHelper.currentApplication(). Then, use that context to create a
	// context for your own app (see Context.createContext(...))

	private static Boolean is_incoming;
	private static Connection mConnection;
	private static boolean CallRecording;
	private static boolean AutoRecording;
	private static boolean RecordIn;
	private static boolean RecordOut;
	private static int RecordCaller;
	private static int AutoRecordingStorage;
	private static int SlotToRecord;

	public static void hookIsEnableAudioRecord(final LoadPackageParam paramLoadPackageParam)
	{
		Misc.x("hookIsEnableAudioRecord");
		findAndHookMethod("com.htc.soundrecorder.PausableAudioRecorder", paramLoadPackageParam.classLoader, "isEnableAudioRecord", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				// Misc.x("isEnableAudioRecord: " + param.getResult());
				// XposedHelpers.setBooleanField(param.thisObject,
				// "mIsInCallRecording", true);
				// param.setResult(Boolean.valueOf(true));
			}
		});
		findAndHookMethod("com.htc.soundrecorder.PausableAudioRecorder", paramLoadPackageParam.classLoader, "isInCallRecording", String.class, new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				// param.setResult(Boolean.valueOf(true));
			}
		});
		findAndHookMethod("com.htc.soundrecorder.InCallRecorder", paramLoadPackageParam.classLoader, "start", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				// Misc.x("InCallRecorder: invoke");
			}
		});
		findAndHookMethod("com.htc.soundrecorder.PausableAudioRecorder", paramLoadPackageParam.classLoader, "initAudioRecorder", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				// Object mRecorder =
				// XposedHelpers.getObjectField(param.thisObject, "mRecorder");
				// XposedHelpers.callMethod(mRecorder, "setAudioSource", 4);
			}
		});
	}

	public static void hookAudioRecord()
	{
		Misc.x("hookAudioRecord");
		final Class<?> AudioRecord = XposedHelpers.findClass("android.media.AudioRecord", null);

		XposedHelpers.findAndHookConstructor(AudioRecord, int.class, int.class, int.class, int.class, int.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				// Misc.x("AudioRecord");
				// param.args[0] = 4;
			}
		});

		XposedHelpers.findAndHookMethod("android.media.MediaRecorder", null, "setAudioSource", int.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				// Misc.x("MediaRecorder want's source" + param.args[0]);
				// param.args[0] = 4;
			}
		});

		XposedHelpers.findAndHookMethod(AudioRecord, "audioParamCheck", int.class, int.class, int.class, int.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				// Misc.x("audioParamCheck");
				// param.args[0] = 4;
			}
		});
	}

	public static void hookPausableAudioRecorderStart(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.soundrecorder.PausableAudioRecorder", paramLoadPackageParam.classLoader, "start", "long", "java.lang.String", "java.lang.String", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

				int i = Integer.parseInt(preferences.getString("encodeOption", "1"));

				param.args[1] = "audio/amr";

				if (i == 1) {
					param.args[1] = "audio/aac";
				}
				if (i == 2) {
					param.args[1] = "audio/amr-wb";
				}
			}
		});
	}

	private static boolean isToAutoRecord(Context paramContext, ClassLoader classLoader)
	{
		CallRecording = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC, 0));
		AutoRecording = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
		RecordIn = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER_IN, 1));
		RecordOut = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER_OUT, 1));
		RecordCaller = Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_CALLER, 0);
		AutoRecordingStorage = Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_STORAGE, 1);
		SlotToRecord = Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_SLOT, 0);

		if (!CallRecording)
			return false;

		if (!AutoRecording)
			return false;

		// Record by slot
		if (SlotToRecord != 0) {
			if (mConnection != null) {
				int PhoneType = mConnection.getCall().getPhone().getPhoneType();
				Class<?> PhoneUtils = XposedHelpers.findClass("com.android.phone.PhoneUtils", classLoader);

				int slot = (Integer) XposedHelpers.callStaticMethod(PhoneUtils, "getSimSlotTypeByPhoneType", PhoneType);
				if (slot != SlotToRecord) {
					return false;
				}
			}
		}

		if (is_incoming != null) {
			if (!RecordIn && is_incoming) // Если не записывать входящие и
											// звонок
											// входящий
				return false;

			if (!RecordOut && !is_incoming) // Если не записывать исходящие и
											// звонок исходящий
				return false;
		}

		String localName = null;

		if (mConnection != null) {
			Call localCall = mConnection.getCall();

			Connection localConnection = localCall.getEarliestConnection();
			if (localConnection != null) {
				Object localObject = localConnection.getUserData();
				if ((localObject instanceof CallerInfo)) {
					CallerInfo localCallerInfo = (CallerInfo) localObject;

					localName = localCallerInfo.name;
				}
			}
		}

		// Если только из записной книжки и имя не определено
		if (RecordCaller == 1 && localName == null)
			return false;

		// Если только неизвестные номера и имя задано
		if (RecordCaller == 2 && localName != null)
			return false;

		// Если записывать всех
		if (RecordCaller == 0)
			return true;

		// Во всех остальных случая возвращаем true
		return true;
	}

	// Метод для разрешения записи с телефонной линии
	// Меняет статическую переменную в HtcFeatureList
	public static void hookEnableCallRecording(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.phone.PhoneApp", paramLoadPackageParam.classLoader, "onCreate", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				ContentResolver cr = (ContentResolver) XposedHelpers.callMethod(param.thisObject, "getContentResolver");
				Class<?> Features = XposedHelpers.findClass("com.android.phone.HtcFeatureList", paramLoadPackageParam.classLoader);

				boolean CallRecording = Misc.toBoolean(Settings.System.getInt(cr, Const.TWEAK_CALL_REC, 0));

				if (CallRecording) {
					XposedHelpers.setStaticBooleanField(Features, "FEATURE_SUPPORT_VOICE_RECORDING", true);
					// XposedHelpers.setStaticBooleanField(Features,
					// "FEATURE_DISABLE_GSM_VOICE_RECORDING", false);
				}
			}
		});

		// Record button
		findAndHookMethod("com.android.phone.InCallScreen", paramLoadPackageParam.classLoader, "updateScreen", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getApplicationContext");

				boolean AutoRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
				boolean Recording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC, 0));

				if (Recording && !AutoRecording) {
					LinearLayout screen = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mControlPanel");

					final Object mControlPanel = XposedHelpers.getObjectField(param.thisObject, "mControlPanel");
					Object mInCallScreenMode = XposedHelpers.getObjectField(param.thisObject, "mInCallScreenMode");
					boolean isSingleAlive = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isSingleAlive");
					boolean isConference = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isConference");
					boolean isMultiple = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isMultiple");
					final XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);

					// Russsian language fix to fit 4 buttons
					if (Locale.getDefault().getLanguage().equalsIgnoreCase("ru")) {
						Object mMuteButton = XposedHelpers.getObjectField(mControlPanel, "mMuteButton");
						XposedHelpers.callMethod(mMuteButton, "setText", "Микрофон");
					}

					Button recordButton;

					recordButton = (Button) screen.findViewById(R.id.RecordingButton);

					Class<?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					final Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");

					if (recordButton == null) {
						recordButton = new HtcIconButton(context);
						recordButton.setId(R.id.RecordingButton);

						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 1.0f);
						recordButton.setLayoutParams(layoutParams);

						recordButton.setText(modRes.getString(R.string.menu_start_record));

						XposedHelpers.callMethod(recordButton, "setIconDrawable", modRes.getDrawable(R.drawable.icon_btn_recorder_on_dark));

						recordButton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View paramView)
							{
								PowerManager pm = (PowerManager) paramView.getContext().getSystemService(Context.POWER_SERVICE);
								boolean isScreenOn = pm.isScreenOn();

								if (isScreenOn) {
									if ((Boolean) XposedHelpers.callMethod(Recorder, "isRecording")) {
										XposedHelpers.callMethod(Recorder, "stop");

										updateRecordButton(paramView, true, modRes);
									} else {
										XposedHelpers.callMethod(Recorder, "start");

										updateRecordButton(paramView, false, modRes);
									}
								}
							}
						});
						screen.addView(recordButton);
					}

					recordButton.setVisibility(View.GONE);
					updateRecordButton(recordButton, true, modRes);

					if (isSingleAlive || isConference || isMultiple) {
						recordButton.setVisibility(View.VISIBLE);
						updateRecordButton(recordButton, true, modRes);
					} else {
						recordButton.setVisibility(View.GONE);
						updateRecordButton(recordButton, false, modRes);
					}

					XposedHelpers.callMethod(mControlPanel, "updateIconButtonTextSize");
				}
			}
		});
	}

	private static void updateRecordButton(Object button, boolean startRecordState, XModuleResources modRes)
	{
		if (startRecordState) {
			XposedHelpers.callMethod(button, "setPressed", false);
			XposedHelpers.callMethod(button, "setColorOn", false);
			XposedHelpers.callMethod(button, "setText", modRes.getString(R.string.menu_start_record));
			XposedHelpers.callMethod(button, "setIconDrawable", modRes.getDrawable(R.drawable.icon_btn_recorder_on_dark));

		} else {
			XposedHelpers.callMethod(button, "setPressed", true);
			XposedHelpers.callMethod(button, "setColorOn", true);
			XposedHelpers.callMethod(button, "setText", modRes.getString(R.string.menu_stop_record));
			XposedHelpers.callMethod(button, "setIconDrawable", modRes.getDrawable(R.drawable.icon_btn_recorder_stop_on_dark));
		}
	}

	// Метод по реализации начала и остановки автозаписи
	public static void hookAutomateCallRecording(final LoadPackageParam paramLoadPackageParam)
	{
		// Начать автозапись
		findAndHookMethod("com.android.phone.CallNotifier", paramLoadPackageParam.classLoader, "onCallConnected", "android.os.AsyncResult", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				AsyncResult asyncResult = (AsyncResult) param.args[0];
				mConnection = (Connection) asyncResult.result;
				Object application = XposedHelpers.getObjectField(param.thisObject, "mApplication");
				Context context = (Context) XposedHelpers.callMethod(application, "getApplicationContext");

				is_incoming = mConnection.isIncoming();

				// Если условия автозаписи нас устраивают
				if (isToAutoRecord(context, paramLoadPackageParam.classLoader)) {
					Class<?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");

					if (!(Boolean) XposedHelpers.callMethod(Recorder, "isRecording")) {
						XposedHelpers.callMethod(Recorder, "start");
					}
				}
			}
		});

		// заканчивать автозапись. Должен срабатывать автоматически, но иногда
		// система не закрывает звонок
		// поэтому делаем это еще раз, так как ошибок это не вызывает
		findAndHookMethod("com.android.phone.CallNotifier", paramLoadPackageParam.classLoader, "onDisconnect", "android.os.AsyncResult", new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Object application = XposedHelpers.getObjectField(param.thisObject, "mApplication");
				Context context = (Context) XposedHelpers.callMethod(application, "getApplicationContext");

				// Если условия автозаписи у нас сработали
				if (isToAutoRecord(context, paramLoadPackageParam.classLoader)) {
					Context tweakContext = context.createPackageContext(Const.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
					Intent intent = new Intent(tweakContext, TweakerService.class);
					intent.setAction(TweakerService.ACTION_CLEANUP_RECORDS);
					tweakContext.startService(intent);

					is_incoming = null;
					mConnection = null;

					Class<?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");
					// Проверяем еще раз на то, что запись идет
					if ((Boolean) XposedHelpers.callMethod(Recorder, "isRecording")) {
						XposedHelpers.callMethod(Recorder, "stop");
					}
				}
			}
		});
	}

	// Переименовываем файл записи
	public static void hookAutomateCallRecordingFilename(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader, "getIncallRecordingFileName", "com.android.internal.telephony.CallManager", new XC_MethodHook()
		{
			protected void afterHookedMethod(final MethodHookParam param) throws Throwable
			{
				// Context context = mContext;
				/*
				 * Context context = (Context)
				 * XposedHelpers.getObjectField(param.thisObject, "mContext");
				 * 
				 * 
				 * boolean CallRecording =
				 * Misc.toBoolean(Settings.System.getInt(
				 * context.getContentResolver(), Const.TWEAK_CALL_REC, 0));
				 * boolean AutoRecording =
				 * Misc.toBoolean(Settings.System.getInt(
				 * context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
				 * int AutoRecordingStorage =
				 * Settings.System.getInt(context.getContentResolver(),
				 * Const.TWEAK_CALL_REC_AUTO_STORAGE, 1);
				 */

				if (CallRecording && AutoRecording && is_incoming != null) {
					String recordFile = (String) param.getResult();

					// Если звонок входящий
					if (is_incoming) {
						if (AutoRecordingStorage == 1) {
							recordFile = Const.AUTO_REC_INCOMING + recordFile;
						} else {
							recordFile = Const.AUTO_REC_MAIN + recordFile + "-IN";
						}
					} else {
						if (AutoRecordingStorage == 1) {
							recordFile = Const.AUTO_REC_OUTGOING + recordFile;
						} else {
							recordFile = Const.AUTO_REC_MAIN + recordFile + "-OUT";
						}
					}

					param.setResult(recordFile);
				}
			}
		});
	}

	public static void getStorageRoot(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.soundrecorder.util.FileNameGen", paramLoadPackageParam.classLoader, "getRootPathFile", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
				boolean CallRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC, 0));
				boolean AutoRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
				int AutoRecordingStorage = Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_STORAGE, 1);

				if (CallRecording && AutoRecording) {
					File RootPathFile = (File) param.getResult();
					File recordPathIn;
					File recordPathOut;

					if (AutoRecordingStorage == 1) {
						recordPathIn = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_INCOMING);
						recordPathOut = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_OUTGOING);
					} else {
						recordPathIn = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN);
						recordPathOut = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN);
					}

					if (!recordPathIn.exists()) {
						if (!recordPathIn.mkdirs()) {
							XposedBridge.log("Problem creating incoming folder");
						}
					}
					if (!recordPathOut.exists()) {
						if (!recordPathOut.mkdirs()) {
							XposedBridge.log("Problem creating outgoing folder");
						}
					}

					File nomedia = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN, ".nomedia");
					if (!nomedia.exists()) {
						if (!nomedia.createNewFile()) {
							XposedBridge.log("Problem creating nomedia file");
						}
					}
				}
			}
		});
	}
}
