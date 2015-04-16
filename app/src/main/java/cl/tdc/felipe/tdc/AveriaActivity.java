package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Felipe on 13/02/2015.
 */
public class AveriaActivity extends Activity {
    public static Activity actividad;

    private String name;
    private Bitmap b = null, bmini = null;
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    final CharSequence[] opcionCaptura = {
            "Tomar Fotografía"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_averia);
        actividad = this;

        name = Environment.getExternalStorageDirectory() + "/TDC@/captura.jpg";
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v){
        MainActivity.actividad.finish();
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    public void onClick_enviar(View v){


        //Obtenemos Datos del formulario
        String sin = ((Spinner)findViewById(R.id.cb_siniestro)).getSelectedItem().toString();
        String elem = ((Spinner)findViewById(R.id.cb_elemento)).getSelectedItem().toString();
        String comen = ((TextView)findViewById(R.id.et_comentario)).getText().toString();

        //Debemos Checkear que este lo "no" opcional

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_averia);
        dialog.setTitle("¿Todo Correcto?");


        // set the custom dialog components - text, image and button
        TextView elemento = (TextView) dialog.findViewById(R.id.dialogaveria_elemento);
        TextView siniestro = (TextView) dialog.findViewById(R.id.dialogaveria_siniestro);
        TextView comentario = (TextView) dialog.findViewById(R.id.dialogaveria_comentario);
        elemento.setText(elem);
        siniestro.setText(sin);
        comentario.setText(comen);
        ImageView image = (ImageView) dialog.findViewById(R.id.dialogaveria_captura);
        image.setImageBitmap(b);

        ImageButton dialogOk = (ImageButton) dialog.findViewById(R.id.dialogaveria_ok);
        ImageButton dialogNok = (ImageButton) dialog.findViewById(R.id.dialogaveria_nok);
        // if button is clicked, close the custom dialog
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogNok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /** Botón Cámara **/
    public void onClick_foto(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoja una Opcion:");
        builder.setIcon(R.drawable.ic_camera);
        builder.setItems(opcionCaptura, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int code = TAKE_PICTURE;
                if (item==TAKE_PICTURE) {
                    Uri output = Uri.fromFile(new File(name));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                } else if (item==SELECT_PICTURE){
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = SELECT_PICTURE;
                }
                startActivityForResult(intent, code);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    b = (Bitmap) data.getParcelableExtra("data");
                }
            } else {
                b = BitmapFactory.decodeFile(name);

            }
        } else if (requestCode == SELECT_PICTURE){
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                b = BitmapFactory.decodeStream(bis);

            } catch (FileNotFoundException e) {}
        }
        try{
            //b = Bitmap.createScaledBitmap(b, 640, 480, true);
            bmini = Bitmap.createScaledBitmap(b, 64, 64, true);
        }catch(Exception ex){}


    }
}
