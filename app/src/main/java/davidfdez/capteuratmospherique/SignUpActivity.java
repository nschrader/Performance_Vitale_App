package davidfdez.capteuratmospherique;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SignUpActivity extends AppCompatActivity {
    private TextView et1, et2, et3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et1 = (EditText) findViewById(R.id.input_name);
        et2 = (EditText) findViewById(R.id.input_password);
        et3 = (TextView) findViewById(R.id.link_login);


        et3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signIn(View v) {  //hacer que no se repita comprobando si ya existe
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String cod = et1.getText().toString();
        String pass = et2.getText().toString();

        ContentValues registro = new ContentValues();
        registro.put("id", cod);
        registro.put("password", pass);

        if (!checkIfUserExists(cod)) {
            bd.insert("User", null, registro);
            bd.close();
            et1.setText("");
            et2.setText("");
            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setMessage("Signing in...");
            progressDialog.setIndeterminate(false);

            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onSignupSuccess();
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);

            Toast.makeText(this, "User Signed In",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User already exists",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public boolean checkIfUserExists(String User) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        boolean exists = false;
        Cursor fila = bd.rawQuery(
                "select id from User where id = '" + User + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getString(0).equals(User)) {
                exists = true;
            }
        } else {
            exists = false;
        }
        return exists;
    }


    public void onSignupSuccess() {
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
        finish();
    }


}
