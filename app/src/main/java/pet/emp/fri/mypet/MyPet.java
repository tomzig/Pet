package pet.emp.fri.mypet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MyPet extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pet);
    }

    public void showAbout(View v) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void showSettings(View v) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void showAllPets(View v) {
        Intent intent = new Intent(this, AllPets.class);
        startActivity(intent);
    }
}
