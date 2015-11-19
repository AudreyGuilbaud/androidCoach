package com.example.sessionprincipale.coach;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe déterminant tout le fonctionnement de l'application
 */
public class CoachActivity extends AppCompatActivity {

    //propriétés pour le calcul
    private Integer minFemme = 15 ; // maigreur si en dessous
    private Integer maxFemme = 30 ; // graisse si au-dessus
    private Integer minHomme = 10 ; // maigreur si en dessous
    private Integer maxHomme = 25 ; // graisse si au-dessus
    private Profil monProfil ; // pour le serialize
    private String nomFic = "save" ;// pour le serialize
    private Integer sexe = 1 ; //sexe
    private String nomBase = "bdcoach.sqlite" ;
    private Integer versionBase = 1 ;
    private MySQLiteOpenHelper accesBD ;
    private SQLiteDatabase bd ;


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
                    sexe = 0 ;
                }
            }
        });
    }


    /**
     * Calcul de l'IMG
     * Cette méthode permet de calculer l'IMG à l'aide des trois champs poids, taille et age, ainsi qu'avec le bouton radio qui a préalablement initialisé la
     * variable sexe à 1 (homme) ou 0 (femme). La formule du calcul est (1,2*Poids/Taille²)+(0.23*Age)-(10,83*Sexe)-5.4. Prépare l'affichage du texte et de
     * l'image en fonction du résultat.
   */

    /**
     * Affichage de l'IMG
     */
    private void calcIMG() {
        // calcul de l'img
        float img = this.calculIMG(monProfil.getPoids(), monProfil.getTaille(), monProfil.getAge(), monProfil.getSexe()) ;
        // récupération des objets graphiques d'affichage du résultat
        TextView lblIMG = (TextView) findViewById(R.id.lblIMG);
        ImageView imgSmiley = (ImageView) findViewById(R.id.imgSmiley);
        // mémorisation des bornes
        Integer min ;
        Integer max ;
        if (monProfil.getSexe()==0) {  // femme
            min = 15 ;
            max = 30 ;
        }else{ // homme
            min = 10 ;
            max = 25 ;
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
        ((Button)findViewById(R.id.btnCalc)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String txtPoids = ((EditText) findViewById(R.id.txtPoids)).getText().toString();
                String txtTaille = ((EditText) findViewById(R.id.txtTaille)).getText().toString();
                String txtAge = ((EditText) findViewById(R.id.txtAge)).getText().toString();
                if ((!(txtPoids.equals(""))) && (!(txtTaille.equals(""))) && (!(txtAge.equals("")))) {
                    // mémorisation du profil
                    monProfil = new Profil(Float.parseFloat(txtTaille) / 100,
                            Integer.parseInt(txtPoids),
                            Integer.parseInt(txtAge),
                            sexe,
                            new java.util.Date());  // la date du jour pour historiser les mesures
                    // enregistrement dans la base de données
                    bd = accesBD.getWritableDatabase();
                    String req = "insert into profil (datemesure, taille, poids, age, sexe) values ";
                    req += "(\"" + monProfil.getDateMesure().toString() + "\","
                            + monProfil.getTaille() + "," + monProfil.getPoids() + ","
                            + monProfil.getAge() + "," + monProfil.getSexe() + ")";
                    bd.execSQL(req);
                    // affichage de l'IMG
                    calcIMG();
                } else {
                    Toast.makeText(CoachActivity.this, "Veuillez saisir tous les champs !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Récupération du profil dans la base de données
     */
    private void recupLastProfilBase() {
        bd = accesBD. getReadableDatabase ();
        Cursor curseur = bd.rawQuery("select * from profil",null);
        curseur.moveToLast() ;
        if (!curseur.isAfterLast()) {
            // récupération dans l'ordre : date (inutile), taille, poids, age, sexe
            Integer taille = (int)(curseur.getFloat(1)*100) ;
            Integer poids = curseur.getInt(2) ;
            Integer age = curseur.getInt(3) ;
            Integer sexe = curseur.getInt(4) ;
            // valorisation des objets graphiques avec les informations récupérées
            ((EditText)findViewById(R.id.txtTaille)).setText(taille.toString());
            ((EditText)findViewById(R.id.txtPoids)).setText(poids.toString());
            ((EditText)findViewById(R.id.txtAge)).setText(age.toString());
            if (sexe==1) { // homme
                ((RadioButton)findViewById(R.id.rdHomme)).setChecked(true);
            }else{ // femme
                ((RadioButton)findViewById(R.id.rdFemme)).setChecked(true);
            }
        }
        curseur.close();
    }
    /**
     * retourne le calcul de l'img
     * @param poids
     * @param taille
     * @param age
     * @param sexe
     * @return
     */
    private float calculIMG (Integer poids, Float taille, Integer age, Integer sexe) {
        return (float)((1.2*poids/(taille*taille))+(0.23*age)-(10.83*sexe)-5.4) ;
    }

    /**
     * Affiche l'historique des précédentes données
     */
    private void remplirHisto() {
        // acccès à la base en lecture
        bd = accesBD.getReadableDatabase();
        // exécution de la requête
        Cursor curseur = bd.rawQuery("select * from profil order by datemesure desc",null);
        // positionnement sur la première mesure du curseur
        curseur.moveToFirst();
        while (!curseur.isAfterLast()) {
            // construction de la ligne avec la date et l'img (2 zones de texte dans un layout)
            LinearLayout laLigne = new LinearLayout(this) ; // créaton ligne
            ((LinearLayout)findViewById(R.id.llvListeHisto)).addView(laLigne); // ajout ligne dans layout
            laLigne.setOrientation(LinearLayout.HORIZONTAL); // orientation du contenu de la ligne
            TextView leTexte = new TextView(this) ; // zone de texte à insérer dans la ligne
            laLigne.addView(leTexte); // ajout de la zone de texte dans la ligne
            // récupération dans l'ordre : date, taille, poids, age, sexe
            String dateMesure = curseur.getString(0) ;
            Float taille = curseur.getFloat(1) ;
            Integer poids = curseur.getInt(2) ;
            Integer age = curseur.getInt(3) ;
            Integer sexe = curseur.getInt(4) ;
            // calcul de l'img
            float img = this.calculIMG(poids, taille, age, sexe);
            // formatage de la date
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT+00:00' yyyy");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateMesure);
            }catch (ParseException e){};
            dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            // insertion des informations dans les zones de texte de la ligne
            leTexte.setText(dateFormat.format(convertedDate)+" : IMG = "+String.format("%.01f", img));
            // passage au tuple suivant du curseur
            curseur.moveToNext();
        }
        curseur.close();

    }




    /**
     * Instancie l'interface et permet l'affichage de l'interface au fur et à mesure des actions de l'utilisateur
     * @param savedInstanceState frame de l'application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);
        // accès à la base de données
        accesBD = new MySQLiteOpenHelper(this, nomBase, versionBase);
        // méthodes d'écoute des événements
        this.ecouteRadio();
        this.ecouteCalcul();
        // récupération éventuelle de la dernière mesure dans la base de données
        this.recupLastProfilBase();
        // remplissage de l'historique des mesures
        this.remplirHisto() ;
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
