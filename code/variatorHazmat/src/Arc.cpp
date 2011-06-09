#include "Arc.h"


#include<math.h>
#include<cstring>
#include<cstdio>
#include<cstdlib>
#include<string>

Arc::Arc(){

}

Arc::Arc(Sommet *v1, Sommet *v2, int n){
  sommetOrigine = v1;
  sommetDest = v2;
  num = n;
  
 
}
Arc::~Arc(){

}

Sommet* Arc::retournerSommetOrigine(){
  return sommetOrigine;
}

Sommet* Arc::retournerSommetDest(){
  return sommetDest;
}

int Arc::retournerRisque(int c, int q) {
 
  list<list<vector<int> > >::iterator it = com_reg_risque.begin();
  
  int i = 0;
  while (i != c && it != com_reg_risque.end()) {
    i++;
    it++;
  }
  if (it != com_reg_risque.end()) {
    list<vector<int> >::iterator itt = (*it).begin();
    while (itt != (*it).end()) {
      if ((*itt)[0] == q) {
	return (*itt)[1];
      }
      itt++;
    }
  }
//   else {
//     cout << endl;
//   } 
//    cout<<"Erreur: la commodite "<<c<<" sur l'arc "<<num<< " n'impose pas de risque sur la region "<<q<<endl;
  // si la commodite sur l'arc n'impose pas de risuqe sur la region q, on renvoie 0
  return 0;
}

void Arc::affecterRisque(int c, int r, int risque) {
  
  list<list<vector<int> > >::iterator it = com_reg_risque.begin();  
  int i = 0;
  while (i != c && it != com_reg_risque.end()) {
    i++;
    it++;
  }
  vector<int> v(2);
  v[0] = r;
  v[1] = risque;
  // si la commodite existe, affecter le risque d'une nouvelle resgion
  if (it != com_reg_risque.end()) {
    (*it).push_back(v);
  }
  else {
    // cette commodite n'as pas encore ete inseree
    // la rajouter
    list<vector<int> > nouvListe;
    nouvListe.push_back(v);
    com_reg_risque.push_back(nouvListe);
  }
}

int Arc::retournerNum() {
  return num;
}

double Arc::retournerCoutReduit() {
  return coutReduit;
}

void Arc::affecterCoutReduit(double cr) {
  coutReduit = cr;
}

double Arc::retournerCout() {
  return cout;
}

void Arc::affecterCout(double cou) {
  cout = cou;
}

int Arc::retournerRisqueTot(int c) {
  
  list<list<vector<int> > >::iterator it = com_reg_risque.begin();
  int i = 0;
  while (i != c && it != com_reg_risque.end()) {
    i++;
    it++;
  }
  int risque = 0;
  if (it != com_reg_risque.end()) {
    // faire la somme des risques de toutes les regions
    list<vector<int> >::iterator itt = (*it).begin();
    while (itt != (*it).end()) {
      risque += (*itt)[1];
      itt++;
    }
  }
  /*else {
    cout<<"Fonction Arc::retournerRisqueTot(): ERREUR, commodite introuvable\n";
  }  */  
  return risque;
}