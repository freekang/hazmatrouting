#include "Sommet.h"


Sommet::Sommet(int n) {
  num = n;
}
Sommet::~Sommet() {

}


int Sommet::retournerNum() {
  return num;
}

void Sommet::ajouterArcSortant(Arc* a) {
  list_ar_sortants.push_back(a);
}

void Sommet::ajouterArcEntrant(Arc* a){
  list_ar_entrants.push_back(a);
}

list<Arc*> Sommet::retournerList_ar_sortants() {
  return list_ar_sortants;
}

int Sommet::retournerNombre_ar_sortants() {
  return list_ar_sortants.size();
}

// vector<Sommet*> Sommet::retournerSommetsSucc() {
//   return list_som_sortants;
// }
// void Sommet::ajouterSommetsSucc(Sommet* s) {
//   list_som_sortants.push_back(s);
// }