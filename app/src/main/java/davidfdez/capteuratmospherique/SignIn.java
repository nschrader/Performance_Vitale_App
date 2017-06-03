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

public class SignIn extends AppCompatActivity {

    private EditText et1, et2;
    private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        et1 = (EditText) findViewById(R.id.input_email);
        et2 = (EditText) findViewById(R.id.input_password);
        t1 = (TextView) findViewById(R.id.link_signup);
    }

    public void signIn(View v) {  //hacer que no se repita comprobando si ya existe
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
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
            Toast.makeText(this, "User Signed In",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User already exists",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public boolean checkIfUserExists(String User) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
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

    public void logIn(View v) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String user = et1.getText().toString();
        String pass = et2.getText().toString();
        Cursor fila = bd.rawQuery(
                "select id,password from User where id = '" + user + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getString(0).equals(user) && fila.getString(1).equals(pass)) {
                Toast.makeText(this, "Password matchs the user",
                        Toast.LENGTH_SHORT).show();
                final ProgressDialog progressDialog = new ProgressDialog(SignIn.this,
                        R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setMessage("Loging in...");
                progressDialog.setIndeterminate(false);

                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);


            } else
                Toast.makeText(this, "Error",
                        Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Error",
                    Toast.LENGTH_SHORT).show();

        bd.close();
    }
    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", et1.getText().toString());
        startActivity(i);
        finish();
    }

    public void reset(View v) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("delete from User");
        bd.close();

    }
    public void onBackPressed() {
        // Disable going back to the SignIn
        moveTaskToBack(true);
    }
}