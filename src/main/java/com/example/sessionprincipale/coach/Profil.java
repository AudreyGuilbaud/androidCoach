package com.example.sessionprincipale.coach;

/**
 * Classe permettant la mémorisation des informations saisies pour la serialisation.
 */
public class Profil {
    private Integer age ;
    private Integer poids ;
    private Integer sexe ;
    private float taille ;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getPoids() {
        return poids;
    }

    public void setPoids(Integer poids) {
        this.poids = poids;
    }

    public Integer getSexe() {
        return sexe;
    }

    public void setSexe(Integer sexe) {
        this.sexe = sexe;
    }

    public float getTaille() {
        return taille;
    }

    public void setTaille(float taille) {
        this.taille = taille;
    }
}
