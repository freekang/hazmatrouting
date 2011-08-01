#include <iostream>
#include <cstdio>

#include <Graph.h>
using namespace std;


int main(int argc,char**argv) {
  cout<<"A test program "<<endl;
  
  /** Fichier de donnees */
  char*inputFileName = new char[30];
  sprintf(inputFileName,"%s",argv[1]);
  
  Graph* G = new Graph();
  
  //Read the Graph
  cout<<"Debut lecture instance\n";
  G->lecture_instance(inputFileName);
  cout<<"Lecture instance reussie\n";
  // Display the Graph
  G->displayGraph();
  
  return 0;
}
