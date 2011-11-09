import numpy as np
import matplotlib.pyplot as plt
from comparativePrefStatements import *

class onClick:
    def __init__(self, ov, xtol=None, ytol=None):
        self.toggle = 1
        self.ps = ''
        self.objectiveVectors = ov
        if (len(ov[0]) == 2):
            if xtol is None:
                xtol = ((max(ov[:,0]) - min(ov[:,0]))/float(len(ov)))/2
            if ytol is None:
                ytol = ((max(ov[:,1]) - min(ov[:,1]))/float(len(ov)))/2
        else:
            xtol = 0.0
            ytol = (np.max(ov) - np.min(ov)) / float(len(ov)) / 4
            
        print ov
            
        self.xtol = xtol
        self.ytol = ytol
        self.lastChosenIndex = -2
    
    def __call__(self, event):
        clickX = event.xdata
        clickY = event.ydata
        # needed for detecting clicks inside top left box:
        ymin, ymax = plt.ylim()
        
        # if left mouse click: remove plot window and continue with optimization
        # where the closest solution to the mouse click is extracted:
        if (event.button == 1) and event.inaxes:
            if (len(self.objectiveVectors[0]) == 2):
                for i in range(len(self.objectiveVectors)):
                    if clickX-self.xtol < self.objectiveVectors[i,0] < clickX+self.xtol and  clickY-self.ytol < self.objectiveVectors[i,1] < clickY+self.ytol :
                        self.lastChosenIndex = i
                        plt.close()
            else:
                # sort objective vectors for easy selecting them on the right
                I = np.argsort(self.objectiveVectors[:,self.objectiveVectors.shape[1]-1])
                objVectorsSorted = self.objectiveVectors[I,:]
                # add (kind of) id of solution as last column like in plot:
                maximum = objVectorsSorted.max(0).max(0)
                minimum = objVectorsSorted.min(0).min(0)
                
                objVectorsSorted = np.column_stack((objVectorsSorted,
                                    np.linspace(minimum, maximum,
                                    objVectorsSorted.shape[0]).transpose()))
                
                # detect click only when on the right:
                if (clickX > objVectorsSorted.shape[1]-2 + 0.25):
                    # find correct objective vector among the sorted ones:
                    for i in range(len(objVectorsSorted)):
                        yleft = objVectorsSorted[i, objVectorsSorted.shape[1] - 2]
                        yright = objVectorsSorted[i, objVectorsSorted.shape[1] - 1]
                        
                        xrel = (clickX - (objVectorsSorted.shape[1]-2)) / 0.5
                        yrel = yleft + xrel * (yright - yleft)
                         
                        if clickY-self.ytol < yrel < clickY+self.ytol :
                            # before returning, choose corresponding index
                            # in the original sorting!
                            self.lastChosenIndex = I[i]
                        
                            plt.close()
                # or if in box on top left corner:
                if (0 < clickX < 1 and ymax - (ymax-ymin)*0.1 < clickY < ymax ):
                    self.lastChosenIndex = -1 # indicate that pref statements
                                              # should be used to build weight
                                              # function of hypervolume
                    plt.close()
                    
        # if right mouse button clicked in top left box, compute
        # partial order from preference statements and write the order
        # to the window
        if (event.button == 3) and event.inaxes:
            # detect whether click was in box on top left corner:
            if (0 < clickX < 1 and ymax - (ymax-ymin)*0.1 < clickY < ymax ):
                # sort objective vectors for easy selecting them on the right
                I = np.argsort(self.objectiveVectors[:,self.objectiveVectors.shape[1]-1])
                objVectorsSorted = self.objectiveVectors[I,:]
                cps = comparativePrefStatements(objVectorsSorted, "testPrefStatements.txt")
                poset = cps.computePartialOrderWithOptimisticSemantics()
                if (self.toggle == 1):
                    plt.text(1.1, ymax - (ymax-ymin)*0.05,
                                            self.ps,
                                            horizontalalignment='left',
                                            verticalalignment='center',
                                            color='w')
                    plt.fill([1, 1, objVectorsSorted.shape[1]-0.5, objVectorsSorted.shape[1]-0.5], [ymax, ymax - (ymax-ymin)*0.1, ymax - (ymax-ymin)*0.1, ymax], color='w', alpha=1.0)
                    plt.text(1.1, ymax - (ymax-ymin)*0.05,
                                            poset,
                                            horizontalalignment='left',
                                            verticalalignment='center',
                                            color='k')
                    self.ps = poset
                    self.toggle = 0
                else:
                    plt.text(1.1, ymax - (ymax-ymin)*0.05,
                                            self.ps,
                                            horizontalalignment='left',
                                            verticalalignment='center',
                                            color='k')
                    plt.fill([1, 1, objVectorsSorted.shape[1]-0.5, objVectorsSorted.shape[1]-0.5], [ymax, ymax - (ymax-ymin)*0.1, ymax - (ymax-ymin)*0.1, ymax], color='k', alpha=1.0)
                    plt.text(1.1, ymax - (ymax-ymin)*0.05,
                                            poset,
                                            horizontalalignment='left',
                                            verticalalignment='center',
                                            color='w')
                    self.ps = poset
                    self.toggle = 1
                plt.draw()
    
    def getLastChosenIndex(self):
        return self.lastChosenIndex
    
    # returns the objective vector of the chosen solution; if no solution has
    # been chosen (e.g. by closing the window without clicking on a solution),
    # a vector of nan's is returned
    def getLastChosenObjectiveVector(self):
        if (self.lastChosenIndex >= 0):
            return self.objectiveVectors[self.lastChosenIndex]
        elif (self.lastChosenIndex == -1):
            onesvec = np.ones(len(self.objectiveVectors[0]))
            return onesvec * (-1)
        else:
            onesvec = np.ones(len(self.objectiveVectors[0]))
            return onesvec * float('nan')