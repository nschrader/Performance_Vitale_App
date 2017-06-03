package davidfdez.capteuratmospherique;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private String user = null;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");
        setupToolbar();

        DataModel[] drawerItem = new DataModel[6];

        drawerItem[0] = new DataModel(R.drawable.home_icon, R.string.home);
        drawerItem[1] = new DataModel(R.drawable.livecharts_icon, R.string.LiveCharts);
        drawerItem[2] = new DataModel(R.drawable.charts_icon, R.string.Charts);
        drawerItem[3] = new DataModel(R.drawable.heatmap_icon, R.string.Heatmaps);
        drawerItem[4] = new DataModel(R.drawable.settings_icon, R.string.Settings);
        drawerItem[5] = new DataModel(R.drawable.aboutus_icon, R.string.aboutUsActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

        insertSQLDefaultData();
    }

    private void insertSQLDefaultData() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery("select * from Mesure", null);
        if (!raw.moveToFirst()) { //Database empty
            admin.setMeasures(1496421270283L, "", 968.0, 35.1, 25.6, 229.0, 4716.0, "4546.7948", "00452.3943");
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = null;
                mDrawerLayout.closeDrawers();
                break;
            case 1:
                Intent iALC = new Intent(MainActivity.this, LiveCharts.class);
                startActivity(iALC);
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                Intent iAC = new Intent(MainActivity.this, ChartsActivity.class);
                startActivity(iAC);
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                Intent iAH = new Intent(MainActivity.this, HeatmapActivity.class);
                startActivity(iAH);
                mDrawerLayout.closeDrawers();
                break;
            case 4:
                Intent iAS = new Intent(MainActivity.this, SettingsActivity.class);
                iAS.putExtra("user", user);
                startActivity(iAS);
                mDrawerLayout.closeDrawers();
                break;
            case 5:
                Intent iAA = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(iAA);
                mDrawerLayout.closeDrawers();
                break;
            default:
                fragment = null;
                mDrawerLayout.closeDrawers();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


}
