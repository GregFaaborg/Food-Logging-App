package com.CSCE4901.Mint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
//MY COMMENT-dania

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO Remove this comment just testing git stuff more testing git
    // Test Drive
        //test commit
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = findViewById(R.id.login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //switch to overview activity
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



}
