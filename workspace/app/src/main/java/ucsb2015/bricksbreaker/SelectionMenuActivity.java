package ucsb2015.bricksbreaker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SelectionMenuActivity extends ActionBarActivity {


    public final static String LEVEL = "GameLevel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*********************************************************
     *
     * Function:
     * - Enter Game Page
     * Message:
     * - game level
     *
     *********************************************************/
    public void enterGame(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message;

        switch(view.getId()){
            case R.id.level_1:
                message = "level_1";
                intent.putExtra(LEVEL, message);
                break;
            case R.id.level_2:
                message = "level_2";
                intent.putExtra(LEVEL, message);
                break;
            case R.id.level_3:
                message = "level_3";
                intent.putExtra(LEVEL, message);
                break;
        }

        startActivity(intent);
    }

    /*********************************************************
     *
     * Function:
     *      Enter Profile Page
     *
     *********************************************************/
    public void enterProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
