package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallerInfo;
import com.android.internal.telephony.Connection;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Recorder
{
	private static Boolean is_incoming;
	private static Context mContext;
	private static Connection mConnection;

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

				if (i == 1)
				{
					param.args[1] = "audio/aac";
				}
				if (i == 2)
				{
					param.args[1] = "audio/amr-wb";
				}
			}
		});
	}

	private static boolean isToAutoRecord(Context paramContext)
	{
		boolean CallRecording = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC, 0));
		boolean AutoRecording = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
		boolean RecordIn = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER_IN, 1));
		boolean RecordOut = Misc.toBoolean(Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER_OUT, 1));
		int RecordCaller = Settings.System.getInt(paramContext.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_CALLER, 0);

		if (!CallRecording)
			return false;

		if (!AutoRecording)
			return false;

		if (is_incoming != null)
		{
			if (!RecordIn && is_incoming) // Если не записывать входящие и
											// звонок
											// входящий
				return false;

			if (!RecordOut && !is_incoming) // Если не записывать исходящие и
											// звонок исходящий
				return false;
		}

		String localName = null;

		if (mConnection != null)
		{
			Call localCall = mConnection.getCall();

			Connection localConnection = localCall.getEarliestConnection();
			if (localConnection != null)
			{
				Object localObject = localConnection.getUserData();
				if ((localObject instanceof CallerInfo))
				{
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
				Class <?> Features = XposedHelpers.findClass("com.android.phone.HtcFeatureList", paramLoadPackageParam.classLoader);

				boolean CallRecording = Misc.toBoolean(Settings.System.getInt(cr, Const.TWEAK_CALL_REC, 0));

				if (CallRecording)
				{
					XposedHelpers.setStaticBooleanField(Features, "FEATURE_SUPPORT_VOICE_RECORDING", true);
				}
			}
		});

		findAndHookMethod("com.android.phone.InCallScreen", paramLoadPackageParam.classLoader, "updateScreen", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getApplicationContext");

				boolean AutoRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));

				if (!AutoRecording)
				{
					ViewGroup screen = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mInCallPanel");

					Object mInCallScreenMode = XposedHelpers.getObjectField(param.thisObject, "mInCallScreenMode");
					boolean isSingleAlive = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isSingleAlive");
					boolean isConference = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isConference");
					boolean isMultiple = (Boolean) XposedHelpers.callMethod(mInCallScreenMode, "isMultiple");
					final XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);

					Button recordButton;

					recordButton = (Button) screen.findViewById(R.id.RecordingButton);

					Class <?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					final Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");

					if (recordButton == null)
					{
						recordButton = new Button(context);
						recordButton.setId(R.id.RecordingButton);

						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(170, 124);
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
						layoutParams.setMargins(Misc.densify(0), Misc.densify(10), Misc.densify(10), Misc.densify(0));
						recordButton.setLayoutParams(layoutParams);
						recordButton.setBackgroundDrawable(modRes.getDrawable(R.drawable.record_start));
						recordButton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View paramView)
							{
								PowerManager pm = (PowerManager) paramView.getContext().getSystemService(Context.POWER_SERVICE);
								boolean isScreenOn = pm.isScreenOn();

								if (isScreenOn)
								{
									if ((Boolean) XposedHelpers.callMethod(Recorder, "isRecording"))
									{
										XposedHelpers.callMethod(Recorder, "stop");
										paramView.setBackgroundDrawable(modRes.getDrawable(R.drawable.record_start));
									}
									else
									{
										XposedHelpers.callMethod(Recorder, "start");
										paramView.setBackgroundDrawable(modRes.getDrawable(R.drawable.record_stop));
									}
								}
							}
						});
						screen.addView(recordButton);
					}

					recordButton.setVisibility(View.GONE);

					if (isSingleAlive || isConference || isMultiple)
					{
						recordButton.setVisibility(View.VISIBLE);
					}
					else
					{
						recordButton.setBackgroundDrawable(modRes.getDrawable(R.drawable.record_start));
						recordButton.setVisibility(View.GONE);
					}
				}
			}
		});
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

				mContext = context;
				is_incoming = mConnection.isIncoming();

				// Если условия автозаписи нас устраивают
				if (isToAutoRecord(context))
				{
					Class <?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");

					if (!(Boolean) XposedHelpers.callMethod(Recorder, "isRecording"))
					{
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
				if (isToAutoRecord(context))
				{
					Context tweakContext = context.createPackageContext(Const.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
					Intent intent = new Intent(tweakContext, TweakerService.class);
					intent.setAction(TweakerService.ACTION_CLEANUP_RECORDS);
					tweakContext.startService(intent);

					is_incoming = null;
					mConnection = null;

					Class <?> VoiceRecorderHelper = XposedHelpers.findClass("com.android.phone.util.VoiceRecorderHelper", paramLoadPackageParam.classLoader);
					Object Recorder = XposedHelpers.callStaticMethod(VoiceRecorderHelper, "getInstance");
					// Проверяем еще раз на то, что запись идет
					if ((Boolean) XposedHelpers.callMethod(Recorder, "isRecording"))
					{
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
				Context context = mContext;

				boolean CallRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC, 0));
				boolean AutoRecording = Misc.toBoolean(Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO, 0));
				int AutoRecordingStorage = Settings.System.getInt(context.getContentResolver(), Const.TWEAK_CALL_REC_AUTO_STORAGE, 1);

				if (CallRecording && AutoRecording && is_incoming != null)
				{
					String recordFile = (String) param.getResult();

					// Если звонок входящий
					if (is_incoming)
					{
						if (AutoRecordingStorage == 1)
						{
							recordFile = Const.AUTO_REC_INCOMING + recordFile;
						}
						else
						{
							recordFile = Const.AUTO_REC_MAIN + recordFile + "-IN";
						}
					}
					else
					{
						if (AutoRecordingStorage == 1)
						{
							recordFile = Const.AUTO_REC_OUTGOING + recordFile;
						}
						else
						{
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

				if (CallRecording && AutoRecording)
				{
					File RootPathFile = (File) param.getResult();
					File recordPathIn;
					File recordPathOut;

					if (AutoRecordingStorage == 1)
					{
						recordPathIn = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_INCOMING);
						recordPathOut = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_OUTGOING);
					}
					else
					{
						recordPathIn = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN);
						recordPathOut = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN);
					}

					if (!recordPathIn.exists())
					{
						if (!recordPathIn.mkdirs())
						{
							XposedBridge.log("Problem creating incoming folder");
						}
					}
					if (!recordPathOut.exists())
					{
						if (!recordPathOut.mkdirs())
						{
							XposedBridge.log("Problem creating outgoing folder");
						}
					}

					File nomedia = new File(RootPathFile.getPath() + "/" + Const.AUTO_REC_MAIN, ".nomedia");
					if (!nomedia.exists())
					{
						if (!nomedia.createNewFile())
						{
							XposedBridge.log("Problem creating nomedia file");
						}
					}
				}
			}
		});
	}
}
