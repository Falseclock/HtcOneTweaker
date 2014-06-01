package kz.virtex.htc.tweaker;

import android.content.pm.PackageParser;
import android.content.pm.PermissionInfo;

final class BasePermission
{
	final static int TYPE_NORMAL = 0;
	final static int TYPE_BUILTIN = 1;
	final static int TYPE_DYNAMIC = 2;
	final String name;
	String sourcePackage;
	final int type;
	int protectionLevel;
	PackageParser.Permission perm;
	PermissionInfo pendingInfo;
	int uid;
	int[] gids;

	BasePermission(String _name, String _sourcePackage, int _type)
	{
		name = _name;
		sourcePackage = _sourcePackage;
		type = _type;
		// Default to most conservative protection level.
		protectionLevel = PermissionInfo.PROTECTION_SIGNATURE;
	}

	public String toString()
	{
		return "BasePermission{" + Integer.toHexString(System.identityHashCode(this)) + " " + name + "}";
	}
}