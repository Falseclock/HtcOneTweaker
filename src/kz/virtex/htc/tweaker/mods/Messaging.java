package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.Arrays;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.TweakerBroadcastReceiver;
import kz.virtex.htc.tweaker.XMain;
import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.service.HtcTelephonyManager;
import com.htc.widget.HtcSpecificInputField;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Messaging
{
	public static Drawable Background;
	public static Context messageContext;
	private static MessageData mMessageData;
	private static String[] icons =
	{ "l_icon_indicator_slot1", "icon_indicator_slot1", "l_icon_indicator_slot1_s", "icon_indicator_slot1_s", "l_icon_indicator_slot2", "l_icon_indicator_slot2_s" };
	private static String[] tweaks =
	{ "icon_indicator_slot1", "icon_indicator_slot1", "icon_indicator_slot1_s", "icon_indicator_slot1_s", "icon_indicator_slot2", "icon_indicator_slot2_s" };
	private static Drawable[] tweakedDrawable = new Drawable[icons.length];

	public static void hookSendTextMessage()
	{
		final Class<?> SmsManager = XposedHelpers.findClass("android.telephony.SmsManager", null);

		XposedHelpers.findAndHookMethod(SmsManager, "sendTextMessageExt", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Bundle.class, int.class, new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				//Misc.x(Thread.currentThread().getStackTrace());

				Misc.x("Force messaging, initial phoneType = " + param.args[6]);

				int force = Integer.parseInt(XMain.pref.getString(Const.TWEAK_FORCE_MESS, "0"));

				if (force != 0) {
					Misc.x("Force messaging took place");
					int action = Integer.parseInt(XMain.pref.getString(Const.TWEAK_FORCE_MESS_ACTION, "0"));

					int phoneType;

					if (force == 1)
						phoneType = TelephonyManager.getMainPhoneType();
					else
						phoneType = TelephonyManager.getSubPhoneType();

					// dial through available
					if (action == 0) {
						Misc.x("Messaging through available slot is requested");
						// check if slot is available

						int state = Misc.getIccState(force == 1 ? TelephonyManager.PHONE_SLOT1 : TelephonyManager.PHONE_SLOT2);

						if (state == HtcTelephonyManager.SIM_STATE_READY) {
							Misc.x("Desired slot is available");
							Misc.x(" - mesage will queued through slot " + phoneType);
							// dial through desired slot
							param.args[6] = phoneType;
						} else {
							Misc.x("Desired slot NOT available");
							// slot not available, so lets get state of another
							// slot
							int anotherSlot;
							if (force == 1)
								anotherSlot = TelephonyManager.getSubPhoneType();
							else
								anotherSlot = TelephonyManager.getMainPhoneType();

							state = Misc.getIccState(force == 1 ? TelephonyManager.PHONE_SLOT2 : TelephonyManager.PHONE_SLOT1);

							// If another slot is ready
							if (state == HtcTelephonyManager.SIM_STATE_READY) {
								Misc.x("Another slot is available");
								Misc.x(" - mesage will queued through slot " + anotherSlot);
								// then dial through another
								param.args[6] = anotherSlot;
							} else {
								Misc.x("Messaging through another slot is NOT available");
								Misc.x(" - mesage will queued through slot " + phoneType);
								// otherwise get failed on desired slot
								param.args[6] = phoneType;
							}
						}
					} else {
						// Dial and we do not care
						Misc.x("Messaging through selected slot only: " + phoneType);
						Misc.x(" - we do not care what next will happen");
						param.args[6] = phoneType;
					}
				}
			}
		});

		XposedHelpers.findAndHookMethod(SmsManager, "sendTextMessage", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Misc.x("sendTextMessage Pending invoke!");
			}
		});

		XposedHelpers.findAndHookMethod(SmsManager, "sendTextMessage", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Bundle.class, new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Misc.x("sendTextMessage Bundle invoke!");
			}
		});
	}

	public static void hookSupport8ColorLed(final LoadPackageParam paramLoadPackageParam, String packageName)
	{
		findAndHookMethod(packageName + ".MmsConfig", paramLoadPackageParam.classLoader, "isSupport8ColorLed", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				XposedBridge.log("hookSupport8ColorLed");
				return Boolean.valueOf(true);
			}
		});
	}

	public static void hookSendSMSButton(final LoadPackageParam paramLoadPackageParam, String packageName)
	{
		findAndHookMethod(packageName + ".ui.MessageEditorPanel", paramLoadPackageParam.classLoader, "setDualModeButtonEnableandVisibiltiy", packageName + ".ui.MessageEditorPanel$DualModeButtonParams[]", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Activity mActivity = (Activity) XposedHelpers.getObjectField(param.thisObject, "mActivity");
				Context mContext = mActivity.getApplicationContext();

				boolean showSim1 = Misc.toBoolean(Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_MESS + "_sim1", 1));
				boolean showSim2 = Misc.toBoolean(Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_MESS + "_sim2", 1));

				boolean[] showSim = new boolean[2];
				showSim[0] = showSim1;
				showSim[1] = showSim2;

				HtcSpecificInputField mEditorPanel = (HtcSpecificInputField) XposedHelpers.getObjectField(param.thisObject, "mEditorPanel");
				Object[] paramArrayOfDualModeButtonParams = (Object[]) param.args[0];

				if ((paramArrayOfDualModeButtonParams != null) && (mEditorPanel != null)) {
					boolean mIsDuringSendState = XposedHelpers.getBooleanField(param.thisObject, "mIsDuringSendState");
					Object[] mDualModeButtons = (Object[]) XposedHelpers.getObjectField(param.thisObject, "mDualModeButtons");

					if (mIsDuringSendState) {
						// Misc.x("mIsDuringSendState");
						if (!showSim1)
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[0], "visibility", View.GONE);
						if (!showSim2)
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[1], "visibility", View.GONE);
						mEditorPanel.setButtonEnable(XposedHelpers.getIntField(mDualModeButtons[0], "index"), false);
						mEditorPanel.setButtonEnable(XposedHelpers.getIntField(mDualModeButtons[1], "index"), false);
						return null;
					}

					if (!XposedHelpers.getBooleanField(paramArrayOfDualModeButtonParams[0], "enabled") && !XposedHelpers.getBooleanField(paramArrayOfDualModeButtonParams[1], "enabled")) {
						// Misc.x("both not enabled");

						if (!showSim1) {
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[0], "visibility", View.GONE);
							mEditorPanel.setButtonEnable(XposedHelpers.getIntField(paramArrayOfDualModeButtonParams[0], "index"), false);
						} else {
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[0], "visibility", View.VISIBLE);
							mEditorPanel.setButtonEnable(XposedHelpers.getIntField(paramArrayOfDualModeButtonParams[0], "index"), XposedHelpers.getBooleanField(paramArrayOfDualModeButtonParams[0], "enabled"));
						}

						if (!showSim2) {
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[1], "visibility", View.GONE);
							mEditorPanel.setButtonEnable(XposedHelpers.getIntField(paramArrayOfDualModeButtonParams[1], "index"), false);
						} else {
							XposedHelpers.setIntField(paramArrayOfDualModeButtonParams[1], "visibility", View.VISIBLE);
							mEditorPanel.setButtonEnable(XposedHelpers.getIntField(paramArrayOfDualModeButtonParams[1], "index"), XposedHelpers.getBooleanField(paramArrayOfDualModeButtonParams[1], "enabled"));
						}
						return null;
					}

					// Misc.x("something or both enabled");

					Object button;

					// SIM1
					button = paramArrayOfDualModeButtonParams[0];
					XposedHelpers.setIntField(button, "visibility", XposedHelpers.getBooleanField(button, "enabled") ? View.VISIBLE : View.GONE);
					mEditorPanel.setButtonEnable(XposedHelpers.getIntField(button, "index"), XposedHelpers.getBooleanField(button, "enabled"));

					// SIM2
					button = paramArrayOfDualModeButtonParams[1];
					XposedHelpers.setIntField(button, "visibility", XposedHelpers.getBooleanField(button, "enabled") ? View.VISIBLE : View.GONE);
					mEditorPanel.setButtonEnable(XposedHelpers.getIntField(button, "index"), XposedHelpers.getBooleanField(button, "enabled"));

					// ------------------------------------------------------
					// now do our dirty job
					int action = Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_MESS_ACTION, 0);

					// If do not show SIM2
					if (!showSim2) {
						// get our main SIM buttom state
						Object buttonSIM1 = paramArrayOfDualModeButtonParams[0];
						Object buttonSIM2 = paramArrayOfDualModeButtonParams[1];

						boolean slotReadySIM1 = XposedHelpers.getBooleanField(buttonSIM1, "slotReady");

						// If SIM 1 is not callable but could be visible
						if (!slotReadySIM1) {
							// Misc.x("SIM 1 state is NOT ready");
							switch (action)
							{
							// then show SIM2 and hide SIM1
								default:
								case 0:
									// Misc.x("show SIM2 and hide SIM1");
									XposedHelpers.setIntField(buttonSIM1, "visibility", View.GONE);
									XposedHelpers.setIntField(buttonSIM2, "visibility", View.VISIBLE);
									return null;
									// then hide SIM2, show SIM1 and make in
									// disabled
								case 1:
									// Misc.x("hide SIM2, show SIM1 and make in disabled");
									XposedHelpers.setIntField(buttonSIM1, "visibility", View.VISIBLE);
									XposedHelpers.setIntField(buttonSIM2, "visibility", View.GONE);
									return null;
							}
						} else {
							// Misc.x("SIM 1 state is OK");
							// Show SIM1 and hide SIM2
							XposedHelpers.setIntField(buttonSIM1, "visibility", View.VISIBLE);
							XposedHelpers.setIntField(buttonSIM2, "visibility", View.GONE);
						}
					}
					// If do not show SIM1
					if (!showSim1) {
						// get our main SIM buttom state
						Object buttonSIM1 = paramArrayOfDualModeButtonParams[0];
						Object buttonSIM2 = paramArrayOfDualModeButtonParams[1];

						boolean slotReadySIM2 = XposedHelpers.getBooleanField(buttonSIM2, "slotReady");

						// If SIM 2 is not callable but could be visible
						if (!slotReadySIM2) {
							// Misc.x("SIM 2 state is NOT ready");
							switch (action)
							{
							// then show SIM1 and hide SIM2
								default:
								case 0:
									// Misc.x("show SIM1 and hide SIM2");
									XposedHelpers.setIntField(buttonSIM2, "visibility", View.GONE);
									XposedHelpers.setIntField(buttonSIM1, "visibility", View.VISIBLE);
									return null;
									// then hide SIM1, show SIM2 and make in
									// disabled
								case 1:
									// Misc.x("hide SIM1, show SIM2 and make in disabled");
									XposedHelpers.setIntField(buttonSIM2, "visibility", View.VISIBLE);
									XposedHelpers.setIntField(buttonSIM1, "visibility", View.GONE);
									return null;
							}
						} else {
							// Misc.x("SIM 2 state is OK");
							// Show SIM2 and hide SIM1
							XposedHelpers.setIntField(buttonSIM2, "visibility", View.VISIBLE);
							XposedHelpers.setIntField(buttonSIM1, "visibility", View.GONE);
						}
					}
				}
				return null;
			}
		});
	}

	public static void hookNotificationRemove(final LoadPackageParam paramLoadPackageParam, String packageName)
	{
		findAndHookMethod(packageName + ".transaction.SmsReceiverService.ServiceHandler", paramLoadPackageParam.classLoader, "handleMessage", "android.os.Message", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Message paramMessage = (Message) param.args[0];
				Intent localIntent = (Intent) paramMessage.obj;
				if (localIntent != null) {
					String action = localIntent.getAction();
					XposedBridge.log("ServiceHandler: " + action);
				}
			}
		});
	}

	public static class MessageData
	{
		Context mContext;
		long mWhen;
		CharSequence mTicker;
		int mSmallIcon;
		Bitmap mLargeIcon;
		boolean mGetNewMsgNotificationSetting;
		PendingIntent mPendingIntent;
		String mContentTitle;
		String mContentText;
		int mMessageCount;
		int mThreadSize;
		ClassLoader classLoader;
		boolean paramBoolean;
		Intent paramIntentFirst;
		String paramSender;
		String paramText;
		long mThreadId;
		long mMsgId;
		long mContactId;

		public MessageData(Context context, long when, CharSequence ticker, int smallIcon, Bitmap largeIcon, PendingIntent intent, String contentTitle, String contentText, int messageCount, int threadSize, boolean getNewMsgNotificationSetting, ClassLoader paramClassLoader, boolean paramBool, Intent paramintent1, String pSender, String pText, long threadId,
				long msgId, long contactId)
		{
			mContext = context;
			mWhen = when;
			mTicker = ticker;
			mSmallIcon = smallIcon;
			mLargeIcon = largeIcon;
			mPendingIntent = intent;
			mContentTitle = contentTitle;
			mContentText = contentText;
			mMessageCount = messageCount;
			mThreadSize = threadSize;
			mGetNewMsgNotificationSetting = getNewMsgNotificationSetting;
			classLoader = paramClassLoader;
			paramBoolean = paramBool;
			paramIntentFirst = paramintent1;
			paramSender = pSender;
			paramText = pText;
			mThreadId = threadId;
			mMsgId = msgId;
			mContactId = contactId;
		}

		public Notification getNotification(Notification paramNotification, Integer paramNotificationId, String packageName) throws NameNotFoundException
		{
			if (mThreadSize > 1 || mMessageCount > 1) {
				return paramNotification;
			} else {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(TweakerBroadcastReceiver.ACTION_DELETE_MESSAGE);
				intentFilter.addAction(TweakerBroadcastReceiver.ACTION_CALL_TO_CONTACT);
				intentFilter.addAction(TweakerBroadcastReceiver.ACTION_REPLY_MESSAGE);

				mContext.registerReceiver(new TweakerBroadcastReceiver(), intentFilter);

				Intent intentDeleteMsg = new Intent();
				intentDeleteMsg.setAction(TweakerBroadcastReceiver.ACTION_DELETE_MESSAGE);
				intentDeleteMsg.putExtra("MsgId", mMsgId);
				intentDeleteMsg.putExtra("ContactId", mContactId);
				intentDeleteMsg.putExtra("ThreadId", mThreadId);
				intentDeleteMsg.putExtra("Sender", paramSender);

				@SuppressWarnings("unused")
				PendingIntent mPintentDeleteMsg = PendingIntent.getBroadcast(mContext, paramNotificationId, intentDeleteMsg, 0);

				Intent intentCallToContact = new Intent();
				intentCallToContact.setAction(TweakerBroadcastReceiver.ACTION_CALL_TO_CONTACT);
				intentCallToContact.putExtra("ContactId", mContactId);
				intentCallToContact.putExtra("Sender", paramSender);

				@SuppressWarnings("unused")
				PendingIntent mPintentCallToContact = PendingIntent.getBroadcast(mContext, paramNotificationId, intentCallToContact, 0);

				Intent intentReplyMsg = new Intent();
				intentReplyMsg.setAction(TweakerBroadcastReceiver.ACTION_REPLY_MESSAGE);
				intentReplyMsg.putExtra("ContactId", mContactId);
				intentDeleteMsg.putExtra("Sender", paramSender);
				intentDeleteMsg.putExtra("ThreadId", mThreadId);
				@SuppressWarnings("unused")
				PendingIntent mPintentReplyMsg = PendingIntent.getBroadcast(mContext, paramNotificationId, intentReplyMsg, 0);

				Notification.Builder localBuilder = new Notification.Builder(mContext);
				localBuilder.setWhen(mWhen);

				if (mGetNewMsgNotificationSetting) {
					localBuilder.setTicker(mTicker);
				}
				if (mLargeIcon != null) {
					localBuilder.setLargeIcon(mLargeIcon);
				}
				localBuilder.setSmallIcon(mSmallIcon);
				localBuilder.setContentTitle(mContentTitle);
				localBuilder.setContentText(mContentText);
				localBuilder.setStyle(new Notification.BigTextStyle().bigText(mContentText));
				localBuilder.setContentIntent(mPendingIntent);
				// localBuilder.addAction(0, "Удалить", mPintentDeleteMsg);
				// localBuilder.addAction(0, "Звонок", mPintentCallToContact);
				// localBuilder.addAction(0, "Ответ", mPintentReplyMsg);

				Class<?> MessagingNotification = XposedHelpers.findClass(packageName + ".transaction.MessagingNotification", classLoader);

				Notification localNotification = localBuilder.build();

				if ((Boolean) XposedHelpers.callStaticMethod(MessagingNotification, "getCharmMsgNotificationSetting", mContext) == true) {
					XposedHelpers.callStaticMethod(MessagingNotification, "flashCharmIndicator_JB", localNotification, 0);
				}

				XposedHelpers.callStaticMethod(MessagingNotification, "initiateEffectOfGEPNotification", mContext, paramBoolean, localNotification, false);
				String str3 = paramIntentFirst.getStringExtra("address");

				if (Misc.isSense6()) {
					XposedHelpers.callStaticMethod(MessagingNotification, "setJogBall", mContext, localNotification, str3);
				} else {
					XposedHelpers.callStaticMethod(MessagingNotification, "setJogBall_JB", mContext, localNotification, str3);
				}

				if ((Boolean) XposedHelpers.callStaticMethod(MessagingNotification, "getCharmMsgNotificationSetting", mContext) == true) {
					XposedHelpers.callStaticMethod(MessagingNotification, "flashCharmIndicator_JB", localNotification, 7);
				}

				localNotification.number = mMessageCount;

				if ((Boolean) XposedHelpers.callStaticMethod(MessagingNotification, "getNewMsgNotificationSetting", mContext)) {

					XposedHelpers.callStaticMethod(MessagingNotification, "setLSNotification", mContext, mWhen, mSmallIcon, paramSender, paramText, mPendingIntent);
				}

				return localNotification;
			}
		}
	}

	public static void hookUpdateNotification(final LoadPackageParam paramLoadPackageParam, final String packageName)
	{
		findAndHookMethod(packageName + ".transaction.MessagingNotification", paramLoadPackageParam.classLoader, "updateNotification", "android.content.Context", // 0
				"android.content.Intent", // 1
				"java.lang.String", // 2
				"int", // 3 SmallIcon
				"boolean", // 4
				"java.lang.CharSequence", // 5 Ticker
				"long", // 6 When
				"java.lang.String", // 7
				"int", // 8 Count
				"android.content.Intent", // 9
				"java.util.SortedSet", // 10

				new XC_MethodHook()
				{

					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						Object messageInfo = XposedHelpers.callMethod(param.args[10], "first");
						long mThreadId = XposedHelpers.getLongField(messageInfo, "mThreadId");
						long msgId = XposedHelpers.getLongField(messageInfo, "msgId");
						long mContactId = XposedHelpers.getLongField(messageInfo, "mContactId");
						boolean paramBoolean = (Boolean) param.args[4];
						String paramSender = (String) param.args[7];
						String paramText = (String) param.args[2];

						Context mContext = (Context) param.args[0];
						Intent mIntentFirst = (Intent) param.args[1];
						int mMessageCount = (Integer) param.args[8];

						long mWhen = (Long) param.args[6];
						CharSequence mTicker = (CharSequence) param.args[5];
						int mSmallIcon = (Integer) param.args[3];

						Class<?> MessagingNotification = XposedHelpers.findClass(packageName + ".transaction.MessagingNotification", paramLoadPackageParam.classLoader);
						boolean mGetNewMsgNotificationSetting = (Boolean) XposedHelpers.callStaticMethod(MessagingNotification, "getNewMsgNotificationSetting", mContext);

						int mThreadSize = (Integer) XposedHelpers.callStaticMethod(MessagingNotification, "getUniqueThreadSizeBySet", param.args[10]);

						Class<?> MessageUtils = XposedHelpers.findClass(packageName + ".ui.MessageUtils", paramLoadPackageParam.classLoader);

						Bitmap mLargeIcon = (Bitmap) XposedHelpers.callStaticMethod(MessageUtils, "getContactPhoto", ((Intent) param.args[9]).getLongExtra("notify_contact_id", 0L), mContext);

						if (mLargeIcon != null) {
							Bitmap localBitmap = (Bitmap) XposedHelpers.callStaticMethod(MessageUtils, "getPhotoIconWhenAppropriate", mContext.getResources(), mLargeIcon, mContext);
							if (localBitmap != null) {
								mLargeIcon = localBitmap;
							}
						}
						Intent intentReadThread = new Intent(Intent.ACTION_VIEW, Uri.parse("content://mms-sms/conversations/" + mThreadId));

						PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, intentReadThread, PendingIntent.FLAG_CANCEL_CURRENT);
						String mContentTitle = (String) param.args[7];
						String mContentText = (String) param.args[2];
						if (mMessageCount == 1) {
							CharSequence formatBigMessage = (CharSequence) XposedHelpers.callMethod(messageInfo, "formatBigMessage", mContext);
							mContentText = formatBigMessage.toString();
						}

						mMessageData = new MessageData(mContext, mWhen, mTicker, mSmallIcon, mLargeIcon, mPendingIntent, mContentTitle, mContentText, mMessageCount, mThreadSize, mGetNewMsgNotificationSetting, paramLoadPackageParam.classLoader, paramBoolean, mIntentFirst, paramSender, paramText, mThreadId, msgId, mContactId);
					}
				});

		findAndHookMethod(packageName + ".transaction.MessagingNotification", paramLoadPackageParam.classLoader, "notifyNotification", "android.content.Context", // 0
				"android.app.NotificationManager", // 1
				"int", // 2
				"java.lang.String", // 3
				"android.app.Notification", // 4
				"android.content.Intent", // 5

				new XC_MethodHook()
				{
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						if (mMessageData != null) {
							param.args[4] = mMessageData.getNotification((Notification) param.args[4], (Integer) param.args[2], packageName);
						}
					}

					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						mMessageData = null;
					}
				});
	}

	public static void hookUnread(final LoadPackageParam paramLoadPackageParam, String packageName)
	{
		findAndHookMethod(packageName + ".ui.ConversationListBaseAdapter", paramLoadPackageParam.classLoader, "bind", "android.view.View", "android.content.Context", packageName + ".ui.ConversationHeader", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				boolean isRead = (Boolean) XposedHelpers.callMethod(param.args[2], "isRead");
				View row = (View) param.args[0];

				if (Background == null) {
					Background = row.getBackground();
					if (Background == null) {
						Background = new ColorDrawable(row.getContext().getResources().getColor(android.R.color.transparent));
					}
				}

				if (isRead == false) {
					XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);

					row.setBackgroundDrawable(modRes.getDrawable(R.drawable.list_background_unread));
				} else {
					row.setBackgroundDrawable(Background);
				}
			}
		});
	}

	public static void hookContactBadge(final InitPackageResourcesParam resparam) throws Throwable
	{
		resparam.res.hookLayout(resparam.packageName, "layout", "common_list_item_conversation", new XC_LayoutInflated()
		{
			@Override
			public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable
			{
				View photo = (View) liparam.view.findViewById(liparam.res.getIdentifier("thread_list_Badge", "id", resparam.packageName));
				photo.setVisibility(View.GONE);
			}
		});
	}

	public static void hookMessageNotification(final LoadPackageParam paramLoadPackageParam, String packageName)
	{
		findAndHookMethod(packageName + ".transaction.MessagingNotification", paramLoadPackageParam.classLoader, "showReportNotification", android.content.Context.class, int.class, int.class, long.class, long.class, boolean.class, int.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) param.args[0];
				String title = context.getResources().getResourceEntryName((Integer) param.args[1]);
				if (title.equals("delivery_report_title_text")) {
					param.setResult(null);
					return;
				}
			}
		});
	}

	public static void hookSetBadgeImageResource()
	{
		final Class<?> HtcListItem = XposedHelpers.findClass("com.htc.widget.HtcListItem7Badges1LineBottomStamp", null);

		findAndHookMethod(HtcListItem, "setBadgeImageResource", "int", "int", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				int index = (Integer) param.args[0];
				int id = (Integer) param.args[1];

				// getting context
				Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");

				// Get mBadges object
				Drawable[] mBadges = (Drawable[]) XposedHelpers.getObjectField(param.thisObject, "mBadges");

				if ((index >= 0) && (index < 6)) {
					// Fetch drawable name
					String icon = context.getResources().getResourceEntryName(id);

					// If our icon
					if (Arrays.asList(icons).contains(icon)) {
						int iconIndex = Arrays.asList(icons).indexOf(icon);

						if (tweakedDrawable[iconIndex] == null) {
							int value = XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0);
							if (icon.contains("slot2")) {
								value = XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0);
							}

							// Load our package resources
							XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);

							String iconName = tweaks[iconIndex];

							int tweakId = modRes.getIdentifier(iconName, "drawable", Const.PACKAGE_NAME);
							Drawable themeIcon = modRes.getDrawable(tweakId).getConstantState().newDrawable();

							Bitmap ob = Misc.drawableToBitmap(themeIcon);
							Bitmap obm = ob.copy(ob.getConfig(), true);

							ColorFilter localColorFilter = ColorFilterGenerator.adjustColor(0, 0, 0, Misc.getHueValue(value));
							Canvas canvas = new Canvas(obm);
							Paint paint = new Paint();
							paint.setColorFilter(localColorFilter);
							canvas.drawBitmap(ob, 0f, 0f, paint);

							Drawable drawable = new BitmapDrawable(context.getResources(), obm);
							tweakedDrawable[iconIndex] = drawable;
						}

						mBadges[index] = tweakedDrawable[iconIndex];

					} else {
						mBadges[index] = context.getResources().getDrawable(id);
					}
				}
				return null;
			}
		});
	}

	public static void hookDualModeButtonStyle(final LoadPackageParam paramLoadPackageParam, final String packageName)
	{
		findAndHookMethod("com.htc.sense.mms.ui.MessageEditorPanel", paramLoadPackageParam.classLoader, "setDualModeButtonStyle", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				XModuleResources apkRes = XModuleResources.createInstance(paramLoadPackageParam.appInfo.sourceDir, null);

				HtcSpecificInputField mEditorPanel = (HtcSpecificInputField) XposedHelpers.getObjectField(param.thisObject, "mEditorPanel");

				if (mEditorPanel != null) {
					Activity mActivity = (Activity) XposedHelpers.getObjectField(param.thisObject, "mActivity");
					int i = -1;
					Context localContext = mActivity.getApplicationContext();

					for (int k = 0; k < mEditorPanel.getChildCount(); k++) {
						View localView = mEditorPanel.getChildAt(k);

						if ((localView != null) && ((localView instanceof LinearLayout))) {
							LinearLayout localLinearLayout = (LinearLayout) localView;
							for (int j = 0; j < localLinearLayout.getChildCount(); j++) {
								Object localObject = (Button) localLinearLayout.getChildAt(j);
								if ((localObject != null) && ((localObject instanceof Button))) {
									localObject = (Button) localObject;
									i++;
									final int[] DualModeButtonSelector =
									{ android.R.attr.listSelector };

									mEditorPanel.setButtonPressColorEnable(i, false);
									TypedArray localTypedArray = localContext.obtainStyledAttributes(com.htc.R.style.HtcListView, DualModeButtonSelector);

									Drawable localDrawable = localTypedArray.getDrawable(0);
									localTypedArray.recycle();
									((View) localObject).setBackground(localDrawable);

									int m;
									if (i == 0) {
										m = localContext.getResources().getColor(apkRes.getIdentifier("dualButton_1_color", "color", packageName));
										m = Misc.colorTransform(m, Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0)));
									} else {
										m = localContext.getResources().getColor(apkRes.getIdentifier("dualButton_2_color", "color", packageName));
										m = Misc.colorTransform(Color.parseColor("#33e5b1"), Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0)));
									}

									((TextView) localObject).setTextColor(m);
									Drawable adrawable[] = ((TextView) localObject).getCompoundDrawables();
									if (adrawable[0] != null) {
										adrawable[0].mutate().setColorFilter(m, android.graphics.PorterDuff.Mode.SRC_ATOP);
									}
									XposedHelpers.callMethod(param.thisObject, "setLandIMEDualModeButtonStyle", i, m, true);
								}
							}
						}
					}
				}
				return null;
			}
		});
	}
}
