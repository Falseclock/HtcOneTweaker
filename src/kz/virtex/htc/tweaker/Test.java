package kz.virtex.htc.tweaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.pm.FeatureInfo;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import com.android.internal.util.XmlUtils;

@SuppressLint("SdCardPath")
public class Test
{
	private static final String TAG = "virtex_TWEAKER_TEST_XML";
	static int[] mGlobalGids;
	final static SparseArray<HashSet<String>> mSystemPermissions = new SparseArray<HashSet<String>>();;
	final static HashMap<String, String> mSharedLibraries = new HashMap<String, String>();
	final static HashMap<String, FeatureInfo> mAvailableFeatures = new HashMap<String, FeatureInfo>();
	final HashMap<String, BasePermission> mPermissions =
            new HashMap<String, BasePermission>();
	
	public Test()
	{
		Log.e(TAG, "init");
		readPermissionsFromXml();
		Log.e(TAG, mAvailableFeatures.toString());
		
	}
	
	private static void readPermissionsFromXml()
	{
		File permFile = new File("/sdcard2/platform.xml");
		
        FileReader permReader = null;
        try {
            permReader = new FileReader(permFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't find or open permissions file " + permFile);
            return;
        }
        try {
        	Log.e(TAG, "Starting reading " + permFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(permReader);
            XmlUtils.beginDocument(parser, "permissions");
            while (true) {
                XmlUtils.nextElement(parser);
                if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                    break;
                }
                String name = parser.getName();
                if ("group".equals(name)) {
                    String gidStr = parser.getAttributeValue(null, "gid");
                    if (gidStr != null) {
                        int gid = Integer.parseInt(gidStr);
                        mGlobalGids = appendInt(mGlobalGids, gid);
                    } else {
                        Log.e(TAG, "<group> without gid at "
                                + parser.getPositionDescription());
                    }
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else if ("permission".equals(name)) {
                    String perm = parser.getAttributeValue(null, "name");
                    if (perm == null) {
                        Log.e(TAG, "<permission> without name at "
                                + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    perm = perm.intern();
                    readPermission(parser, perm);
                } else if ("assign-permission".equals(name)) {
                    String perm = parser.getAttributeValue(null, "name");
                    if (perm == null) {
                        Log.e(TAG, "<assign-permission> without name at "
                                + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    String uidStr = parser.getAttributeValue(null, "uid");
                    if (uidStr == null) {
                        Log.e(TAG, "<assign-permission> without uid at "
                                + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    
                    int uid = Process.getUidForName(uidStr);
                    if (uid < 0) {
                        Log.e(TAG, "<assign-permission> with unknown uid \""
                                + uidStr + "\" at "
                                + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    
                    perm = perm.intern();
                    HashSet<String> perms = mSystemPermissions.get(uid);
                    if (perms == null) {
                        perms = new HashSet<String>();
                        mSystemPermissions.put(uid, perms);
                    }
                    perms.add(perm);
                    XmlUtils.skipCurrentTag(parser);
                    
                } else if ("library".equals(name)) {
                    String lname = parser.getAttributeValue(null, "name");
                    String lfile = parser.getAttributeValue(null, "file");
                    if (lname == null) {
                        Log.e(TAG, "<library> without name at "
                                + parser.getPositionDescription());
                    } else if (lfile == null) {
                        Log.e(TAG, "<library> without file at "
                                + parser.getPositionDescription());
                    } else {
                        //Log.i(TAG, "Got library " + lname + " in " + lfile);
                        mSharedLibraries.put(lname, lfile);
                    }
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else if ("feature".equals(name)) {
                    String fname = parser.getAttributeValue(null, "name");
                    if (fname == null) {
                        Log.e(TAG, "<feature> without name at "
                                + parser.getPositionDescription());
                    } else {
                        //Log.i(TAG, "Got feature " + fname);
                        FeatureInfo fi = new FeatureInfo();
                        fi.name = fname;
                        mAvailableFeatures.put(fname, fi);
                    }
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else {
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                }
            }
            permReader.close();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Got execption parsing permissions.", e);
        } catch (IOException e) {
            Log.e(TAG, "Got execption parsing permissions.", e);
        }
    }
	
	
    static void readPermission(XmlPullParser parser, String name)
            throws IOException, XmlPullParserException {
        name = name.intern();
        
        Log.e(TAG, "readPermission - name" + name);
        /*
        BasePermission bp = mSettings.mPermissions.get(name);
        if (bp == null) {
            bp = new BasePermission(name, null, BasePermission.TYPE_BUILTIN);
            mSettings.mPermissions.put(name, bp);
        }
        */
        int outerDepth = parser.getDepth();
        int type;
        while ((type=parser.next()) != XmlPullParser.END_DOCUMENT
               && (type != XmlPullParser.END_TAG
                       || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG
                    || type == XmlPullParser.TEXT) {
                continue;
            }
            String tagName = parser.getName();
            if ("group".equals(tagName)) {
                String gidStr = parser.getAttributeValue(null, "gid");
                Log.e(TAG, "gidStr:" + gidStr);
                if (gidStr != null) {
                    //int gid = Process.getGidForName(gidStr);
                    //bp.gids = appendInt(bp.gids, gid);
                } else {
                    Log.e(TAG, "<group> without gid at "
                            + parser.getPositionDescription());
                }
            }
            XmlUtils.skipCurrentTag(parser);
        }
    }
    static int[] appendInt(int[] cur, int val) {
        if (cur == null) {
            return new int[] { val };
        }
        final int N = cur.length;
        for (int i=0; i<N; i++) {
            if (cur[i] == val) {
                return cur;
            }
        }
        int[] ret = new int[N+1];
        System.arraycopy(cur, 0, ret, 0, N);
        ret[N] = val;
        return ret;
    }
    static final class SharedLibraryEntry
    {
      final String apk;
      final String path;
      
      SharedLibraryEntry(String paramString1, String paramString2)
      {
        this.path = paramString1;
        this.apk = paramString2;
      }
    }
}
