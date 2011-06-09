/********************/
/* FICHIER node.h */
/********************/

#ifndef _ARC_
#define _ARC_

#include<Sommet.h>
#include<list>
#include<vector>
#include<fstream>
#include<iostream>

class Graph;


using namespace::std;

/** Class arc */

class Arc
{
 private:
  int num; // numero de l'arete
  double cout; // poids de l'arete
  double coutReduit;
  double risqueTot; // lasomme des risques associe a cet arc sur toutes les regions
  Sommet* sommetOrigine; // premiere extremite de l'arete
  Sommet* sommetDest; // deuxieme extremite de l'arete
  list<list<vector<int> > > com_reg_risque; // chaque ligne represente une commodite
				   // chaque element de la liste (vecteur) contient la regio et le risque.
  
  public:
  Arc();
  Arc(Sommet* v1, Sommet* v2, int n);
  ~Arc();

  Sommet* retournerSommetOrigine();
  Sommet* retournerSommetDest();
  double retournerCout();
  void affecterCout(double);
  int retournerRisque(int c, int q); // retourner le risque associe a la commodite c sur la region q en utilisant l'arc
  void affecterRisque(int c, int r, int risque); // affecter le risque risque associe a la com c et la reg r sur l'arc.
  int retournerNum();
  double retournerCoutReduit();
  void affecterCoutReduit(double cr);
  int retournerRisqueTot(int c);
};

#endif