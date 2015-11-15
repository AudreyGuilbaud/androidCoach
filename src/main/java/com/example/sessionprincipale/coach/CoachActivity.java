package com.example.sessionprincipale.coach;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Classe déterminant tout le fonctionnement de l'application
 */
public class CoachActivity extends AppCompatActivity {

    //propriétés pour le calcul
    private Integer minFemme = 15 ; // maigreur si en dessous
    private Integer maxFemme = 30 ; // graisse si au-dessus
    private Integer minHomme = 10 ; // maigreur si en dessous
    private Integer maxHomme = 25 ; // graisse si au-dessus
    private Integer sexe ; // 0 pour femme, 1 pour homme

    /**
     * Détermination du sexe
     * Quand l'utilisateur choisit, via les boutons radios, "homme" ou "femme", cette méthode se charge d'afficher un message lui confirmant son action et
     * initie par la même occasion la variable sexe (0 si c'est une femme, 1 si c'est un homme), nécessaire au calcul de l'IMG.
     */
    private void ecouteRadio() {
        ((RadioGroup)findViewById(R.id.grpRadioSexe)).setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (((RadioButton)findViewById(R.id.rdHomme)).isChecked()) {
                    Toast.makeText(CoachActivity.this, "Homme", Toast.LENGTH_SHORT).show();
                    sexe = 1 ;
                } else {
                    Toast.makeText(CoachActivity.this, "Femme", Toast.LENGTH_SHORT).show();
                    sexe = 0;
                }
            }
        });
    }


    /**
     * Calcul de l'IMG
     * Cette méthode permet de calculer l'IMG à l'aide des trois champs poids, taille et age, ainsi qu'avec le bouton radio qui a préalablement initialisé la
     * variable sexe à 1 (homme) ou 0 (femme). La formule du calcul est (1,2*Poids/Taille²)+(0.23*Age)-(10,83*Sexe)-5.4. Prépare l'affichage du texte et de
     * l'image en fonction du résultat.
     * @param poids sera récupéré par le champ poids rempli par l'utilisateur
     * @param taille sera récupérée par le champ taille rempli par l'utilisateur
     * @param age sera récupéré par le champ age rempli par l'utilisateur
     */

        private void calcIMG(Integer poids, Integer taille, Integer age) {
            float ftaille = (float) taille/100 ; // conversion en m
            float img = (float)((1.2*poids/(ftaille*ftaille))+(0.23*age)-(10.83*sexe)-5.4) ;
//        float img = (float)(poids/(taille*taille)) ;
            // récupération des objets graphiques d'affichage du résultat
            TextView lblIMG = (TextView) findViewById(R.id.lblIMG);
            ImageView imgSmiley = (ImageView) findViewById(R.id.imgSmiley);
            // mémorisation des bornes
            Integer min ;
            Integer max ;
            if (sexe==0) {  // femme
                min = 15 ;
                max = 30 ;
            }else{ // homme
                min = 10 ;
                max = 25;
            }
            // analyse de l'img
            lblIMG.setTextColor(Color.RED);
            String message ;
            if (img<min) { // maigre
                message = "trop faible" ;
                imgSmiley.setImageResource(R.drawable.maigre);
            }else{
                if (img>max) { // graisse
                    message = "trop élevé" ;
                    imgSmiley.setImageResource(R.drawable.graisse);
                }else{ // normal
                    lblIMG.setTextColor(Color.GREEN);
                    message = "normal" ;
                    imgSmiley.setImageResource(R.drawable.normal);
                }
            }
            // construction du message complet à afficher
            lblIMG.setText(String.format("%.01f", img)+" : IMG "+message);
        }

    /**
     * Actions qui s'exécutent au clic sur le bouton Calculer
     * Transtypage des propriétés Poids, Taille et Age
     * Appel de la méthode calcIMG
     * Si tous les champs ne sont pas remplis correctement : affichage d'un message adéquat.
     */

    private void ecouteCalcul() {
        ((Button) findViewById(R.id.btnCalc)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String txtPoids = ((EditText) findViewById(R.id.txtPoids)).getText().toString();
                String txtTaille = ((EditText) findViewById(R.id.txtTaille)).getText().toString();
                String txtAge = ((EditText) findViewById(R.id.txtAge)).getText().toString();
                if ((!(txtPoids.equals(""))) && (!(txtTaille.equals(""))) && (!(txtAge.equals("")))) {
                    calcIMG(Integer.parseInt(txtPoids), Integer.parseInt(txtTaille), Integer.parseInt(txtAge)) ;
                } else {
                    Toast.makeText(CoachActivity.this, "Veuillez saisir tous les champs !",
                            Toast.LENGTH_SHORT).show();
                        Log.d("Erreur", "champs non saisis") ;
                }
            }
        });
    }

    /**
     * Instancie l'interface et permet l'affichage de l'interface au fur et à mesure des actions de l'utilisateur
     * @param savedInstanceState frame de l'application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);
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
        this.ecouteRadio();
        this.ecouteCalcul();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coach, menu);
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
}
