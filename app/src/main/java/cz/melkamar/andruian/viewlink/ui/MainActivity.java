package cz.melkamar.andruian.viewlink.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import cz.melkamar.andruian.viewlink.R;
import cz.melkamar.andruian.viewlink.ui.base.BaseActivity;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener , MainMvpView {

    private MainMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        presenter = new MainPresenter(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i("onOptionsItemSelected", id+"");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_manage_sources) {
            presenter.manageDataSources();
//            // TODO dialog
//
//            NavigationView navigationView = findViewById(R.id.nav_view);
//            Menu menu = navigationView.getMenu();
//            Random random = new Random();
//            int num = random.nextInt(10000)+10000;
//            MenuItem menuItem = menu.add(R.id.nav_group_datasources, num, Menu.NONE, num+"");
//
//            View menuItemView = getLayoutInflater().inflate(R.layout.switch_item, null);
//            menuItem.setActionView(menuItemView);
//            menuItem.setCheckable(true);
//
//            DrawerMenuClickListener listener = new DrawerMenuClickListener(num);
//            menuItem.setOnMenuItemClickListener(listener);
//            ((Switch) menuItemView.findViewById(R.id.nav_switch)).setOnCheckedChangeListener(listener);
        }

//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showManageDatasources() {
        startActivity(new Intent(this, DatasourcesActivity.class));
    }

    class DrawerMenuClickListener implements MenuItem.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener{
        private final int buttonId;

        DrawerMenuClickListener(int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Toast.makeText(MainActivity.this, "Clicked "+buttonId, Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//            int id = compoundButton.getpare;
            Toast.makeText(MainActivity.this, b+" for "+buttonId, Toast.LENGTH_SHORT).show();
        }
    }
}
