package kz.virtex.htc.tweaker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class AboutActivity extends FragmentActivity
{
	static final int[] tabs = { R.string.about, R.string.change_log };
	static final int[] layouts = { R.layout.about_info, R.layout.about_change_log };

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments representing each object in a collection. We use a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter} derivative,
	 * which will destroy and re-create fragments as needed, saving and
	 * restoring their state in the process. This is important to conserve
	 * memory and is a best practice when allowing navigation between objects in
	 * a potentially large collection.
	 */
	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

	/**
	 * The {@link android.support.v4.view.ViewPager} that will display the
	 * object collection.
	 */
	ViewPager mViewPager;

	public void goToGoogle(View view)
	{
		goToUrl("https://play.google.com/store/apps/details?id=kz.virtex.htc.tweaker");
	}

	public void goToXda(View view)
	{
		goToUrl("http://forum.xda-developers.com/showthread.php?p=53011963");
	}

	private void goToUrl(String url)
	{
		Uri uriUrl = Uri.parse(url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);

		// Create an adapter that when requested, will return a fragment
		// representing an object in
		// the collection.
		//
		// ViewPager and its adapters use support library fragments, so we must
		// use
		// getSupportFragmentManager.
		mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager(), getApplicationContext());

		// Set up action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home button should show an "Up" caret, indicating
		// that touching the
		// button will take the user one step up in the application's hierarchy.
		actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setBackgroundDrawable(new
		// ColorDrawable(getResources().getColor(android.R.color.transparent)));

		// Set up the ViewPager, attaching the adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);
		// mViewPager.setBackgroundColor(Color.RED);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed in the action
			// bar.
			// Create a simple intent that starts the hierarchical parent
			// activity and
			// use NavUtils in the Support Package to ensure proper handling of
			// Up.
			Intent upIntent = new Intent(this, AboutActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent))
			{
				// This activity is not part of the application's task, so
				// create a new task
				// with a synthesized back stack.
				TaskStackBuilder.from(this)
				// If there are ancestor activities, they should be added here.
						.addNextIntent(upIntent).startActivities();
				finish();
			}
			else
			{
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter
	{
		Context mContext;

		public DemoCollectionPagerAdapter(FragmentManager fm, Context paramContext)
		{
			super(fm);
			mContext = paramContext;
		}

		@Override
		public Fragment getItem(int i)
		{
			Fragment fragment = new DemoObjectFragment();
			Bundle args = new Bundle();
			args.putInt(DemoObjectFragment.ARG_OBJECT, i); // Our object is
															// just an
															// integer :-P
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount()
		{
			// For this contrived example, we have a 100-object collection.
			return tabs.length;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return mContext.getResources().getText(tabs[position]);
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DemoObjectFragment extends Fragment
	{
		public static final String ARG_OBJECT = "object";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			Bundle args = getArguments();
			int tab_id = args.getInt(ARG_OBJECT);

			View view =  inflater.inflate(layouts[tab_id], container, false);
			
			return view;
		}
	}
}
