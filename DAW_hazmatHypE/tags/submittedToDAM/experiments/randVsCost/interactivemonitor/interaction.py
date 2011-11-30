import matplotlib.pyplot as plt
from Pycluster import *
from scipy.spatial.distance import *
from scipy import stats
import numpy as np
import AnnoteFinder
from pylab import connect
from intmon_internal import intMonIO as io
from intmon import wait
import math
import onClick
from comparativePrefStatements import *

def parallelCoordinatesPlot(objVectors, generation, run, params):
    color = ['b', 'g', 'r', 'c', 'm', 'y', 'k']
    
    if (params.interaction == 1):
        plt.clf()
        plt.hold('on')
        
        # sort objective vectors wrt last objective for nicer plotting
        I = np.argsort(objVectors[:,objVectors.shape[1]-1])
        objVectors = objVectors[I,:]
        
        # add (kind of) id of solution as last column for nicer printing:
        maximum = objVectors.max(0).max(0)
        minimum = objVectors.min(0).min(0)
        objVectors = np.column_stack((objVectors,
            np.linspace(minimum, maximum, objVectors.shape[0]).transpose()))

        i = 0
        for objVector in objVectors:
            i = i + 1
            l = len(objVector)
            
            x = np.hstack((range(l-1), len(objVector)-1.5))
            plt.plot(x, objVector, color[i % len(color) - 1])
        
        # add ids on the right:
        i = 0
        for s in objVectors:
            plt.text(len(s)-1.45, s[len(s)-1], i, fontsize=12)
            i = i + 1
            
        plt.tick_params(axis='both', which='major', direction='out')
        plt.grid('on', 'major')
        plt.xlabel('objectives')
        plt.ylabel('values')
        
        # create and place xtick labels
        ticksOnXAxis = []
        i = 1
        while i < objVectors.shape[1]:
            ticksOnXAxis.append('f' + str(i))
            i = i + 1
        plt.xticks( np.arange(objVectors.shape[1]-1), ticksOnXAxis )
        
        # make room at the top for placing some interactive boxes:
        ymin, ymax = plt.ylim()   # return the current ylim
        plt.ylim( ( ymin, ymax + (ymax-ymin)*0.1 ) )

        # draw first box for using preference statements
        plt.fill([0, 0, 1, 1], [ymax, ymax + (ymax-ymin)*0.1, ymax + (ymax-ymin)*0.1, ymax], 'b' )
        plt.text(0.5, ymax+(ymax-ymin)*0.05, 'preference statements',
                                            horizontalalignment='center',
                                            verticalalignment='center')

    else:
        # if no interaction, plot entire population in the same colors
        # that changes at every interaction step
        plt.hold('on')
        for objVector in objVectors:
            l = len(objVector)
            plt.plot(range(l), objVector, color[((generation / params.plottingPeriod ) % len(color)) - 1])
    
    plt.hold('off')

def beautify2DPlot(generation, run):
    plt.grid('on')
    plt.title('Generation ' + str(generation))
    
    
def plot(objVectors, centroidIDs, clusterIDs, generation, run, params):
    """ plots 'objVectors' and emphasizes the solutions indicated by
        'centroidIDs'. In case of 2 objectives, the clusters, given by
        clusterIDs, are plotted by connecting every point with the centroid
        of its cluster as well. The return parameter is an objective of type
        'onClick' which allows to later on ask which objective vector the user
        has picked.
    """
    if (len(objVectors > 0)) and (len(objVectors[0,:]) == 2):
        # plot all points
        h, = plt.plot(objVectors[:,0],objVectors[:,1], 'x')
        # plot centroids differently
        plt.plot(objVectors[centroidIDs,0], objVectors[centroidIDs, 1], 'o' + plt.get(h, 'color'))

        s = plt.axis()  # get size of plot for annotations
        
        # plot the centroids's ids as well
        for i in centroidIDs:
            plt.text(objVectors[i,0] + (0.1/(s[1]-s[0])), objVectors[i,1] + (0.1/(s[3]-s[2])), i, color=plt.get(h, 'color'))
            
        # connect all points with the centroids of their cluster
        for i in range(len(objVectors[:,0])):
            x = [objVectors[i,0], objVectors[centroidIDs[clusterIDs[i]],0]]
            y = [objVectors[i,1], objVectors[centroidIDs[clusterIDs[i]],1]]
            plt.plot(x, y, ':' + plt.get(h, 'color'), linewidth=0.25)
            
        beautify2DPlot(generation, run)
        
        # allow for interactive clicking:
        if params.interaction == 1:
            # 1) add possibility to get objective vector and ids by clicking right:
            #annotes = range(len(objVectors))
            #af = AnnoteFinder.AnnoteFinder(objVectors[:,0],objVectors[:,1], annotes)
            #connect('button_press_event', af)

            # 2) choose best solution by clicking left:
            oc = onClick.onClick(objVectors)
            connect('button_press_event', oc)
            
            return oc
        
        
    else:
        parallelCoordinatesPlot(objVectors, generation, run, params)
        # allow for interactive clicking:
        if params.interaction == 1:
            # 1) add possibility to get objective vector and ids by clicking right:
            annotes = range(len(objVectors))
            af = AnnoteFinder.AnnoteFinder(objVectors[:,0],objVectors[:,1], annotes)
            connect('button_press_event', af)

            # 2) choose best solution by clicking left:
            oc = onClick.onClick(objVectors)
            connect('button_press_event', oc)
            
            return oc
    
    
def extractCentroids(ids):
    """ returns all entries i in 'ids' for which ids(i)==i (0\leq i\leq |ids|-1)
    """
    centroids = []
    for i in range(len(ids)):
        if ids[i] == i:
            centroids.append(i)
    return centroids
    

def do_kmedoidsclustering(objVectors, params):
    """ performs k-medoids clustering of the points in 'objVectors' (rows: points;
        columns: objective values) and returns the ids, i.e., row numbers, of
        the selected 'centers' of each cluster in 'centroidsIDs' as well as the
        cluster number of each other point which corresponds to the number of the
        cluster's centroid/medoid.
    """
    # compute distances between objVectors in vector-form:
    distance = pdist(objVectors, 'euclidean')
    # do clustering in terms of k-medoids clustering (with one of the given
    # points as 'centers' of the clusters
    clusterIDs, error, nfound = kmedoids(distance, nclusters=6, npass=10000, initialid=None)
    print error
    print nfound
    
    # extract ids of centroids/medoids
    centroidIDs = extractCentroids(clusterIDs)
    
    # replace ids of solutions within centroidIDs with the number of the
    # corresponding cluster (for consistency purposes with other approaches)
    clusterIDs = replaceClusterIDs(clusterIDs, centroidIDs)
    
    return centroidIDs, clusterIDs


def replaceClusterIDs(clusterIDs, centroidIDs):
    """ Goes through clusterIDs and replaces each occurance of
        x with the index of the same x in centroidIDs.
    """
    
    newclusterIDs = range(len(clusterIDs)) # init
    for i in range(len(clusterIDs)):
        newclusterIDs[i] = getIndex(centroidIDs, clusterIDs[i])
    
    return newclusterIDs

def getIndex(A, a):
    """ returns first index i where A[i]=a and -1 if A does not 
        contain a.
    """
    for i in range(len(A)):
        if A[i] == a:
            return i
    
    return -1 # in case a not found

def extractIDs(IDs, id):
    """ extracts the indices i in IDs for which clusterIDs(i)==id
    """
    extractedIDs = []
    for i in range(len(IDs)):
        if IDs[i] == id:
            extractedIDs.append(i)
    return extractedIDs

def do_kmeansclustering(objVectors, params):
    """ performs a simple clustering of the points in 'objVectors' (rows: points;
        columns: objective values) based on kmeans clustering and returns the
        ids, i.e., row numbers, of the selected 'centers' of each cluster in
        'centroidsIDs' (the objective vector closest to the cluster's mean)
        as well as the cluster number of each other point which corresponds to
        the number of the cluster's centroid.
    """
    # compute distances between objVectors in vector-form:
    #distance = pdist(objVectors, 'euclidean')
    # do clustering in terms of k-means clustering
    nclusters=6
    #
    # TODO: check kmeans from scipy.cluster.vq
    #
    clusterIDs, error, nfound = kcluster(objVectors, nclusters, mask=None, weight=None, transpose=0, npass=10000, method='a', dist='e', initialid=None)

    # extract all solutions in each cluster and compute solution that is closest
    # to mean    
    extractedClusters = []
    for id in range(nclusters):
        extractedClusters.append(extractIDs(clusterIDs, id))
    
    # compute cluster representatives as solution closest to cluster mean:
    centroidIDs = range(nclusters) # initialization
    i = 0
    for cluster in extractedClusters:
        
        # compute solution closest to cluster mean m:
        if len(cluster) == 1:
            centroidIDs[i] = cluster[0]
        else:            
            m = np.mean(objVectors[cluster],0)
            diff = objVectors[cluster] - m
            
            squaredDistancesToMean = range(len(cluster))
            for j in range(len(cluster)):
                squaredDistancesToMean[j] = diff[j,0]*diff[j,0]+diff[j,1]*diff[j,1]
            
            centroidIDs[i] = cluster[np.argmin(squaredDistancesToMean)]
            
        i += 1
    
    return centroidIDs, clusterIDs


def do_interactionStep(pop, params, state, generation, run):
    """ performs interaction step including plotting of objective values """
    print '      plotting and interaction step'
        
    f_current = pop.getObjectiveValuesOfCurrentPop()
        
    # kmedoids clustering:
    #centroidids, clusterids = do_kmedoidsclustering(f_current, params)
    # kmeans clusterin (does not work at the moment):
    centroidids, clusterids = do_kmeansclustering(f_current, params)
    
    oc = plot(f_current, centroidids, clusterids, generation, run, params)

    
    if (params.interaction == 1):
        ## blocking
        plt.show()
    else:
        # non-blocking plotting to see population over time
        plt.ion()
        plt.draw()

    chosenSolution = oc.getLastChosenObjectiveVector()
    
    if (math.isnan(chosenSolution[0])):
        # do nothing (user probably just closed the window)
        # in this case, the current weight function is used again
        print "no new information about user's preferences, hence weight function is kept"
    else:
        newWeightStrings = []
        
        if params.interactiontype == 'box':
            # compute new weight based on box idea:
            #
            # TODO: to be implemendted
            #
            print 'not implemented yet'
        elif params.interactiontype == 'gau':
            # compute new weight function as a gaussian around the selected
            # solution:
            length = np.linalg.norm(pop.getSpreadOfCurrentPop())
            
            #
            # TODO: get #samples and seed from W-HypE's parameter file and
            # write it back
            #
            newWeightStrings.append('seed ' + str(2037115396 + generation))
            newWeightStrings.append('nrOfSamples 10000')
                
    
            if (chosenSolution[0] == -1):  # ie if prefstatements should be used
                cps = comparativePrefStatements(f_current, "testPrefStatements.txt")
                poset = cps.computePartialOrderWithOptimisticSemantics()
                
                for s in poset[0]:
                    weightString = getWeightString(f_current[s], pop, f_current, length, params)
                    newWeightStrings.append(weightString)
            
            else:
                weightString = getWeightString(chosenSolution, pop, f_current, length, params)
                newWeightStrings.append(weightString)
        
        # write new weight function to file for selector:
        io.printToFile(newWeightStrings, params.parameter_file_selector)
            
        # reset selector in order to read new weights
        
        # first reset:
        state.write(params.sta_file_selector, 10)
        while 1:
            selState = state.read(params.sta_file_selector)
            if selState != 11:
                state.write(params.sta_file_selector, 10)
            else:
                params.log('    selector reset\n')
                break
            wait(params.poll);
        # then init (since variator is still running, just write state 1
        # for selector):
        params.log('    selector reset to read new weights\n')
        state.write(params.sta_file_selector, 1)
        

def getWeightString(chosenSolution, population, f_current, length, params):
    # compute direction of Gaussian weight function
    if (params.gauDirection == 'diag'):
        dir = np.zeros(params.dimension)
        for i in range(0, params.dimension):
            dir[i] = 1
    elif (params.gauDirection == 'nadir'):
        nad = population.computeLocalNadir(f_current)
        dir = nad - chosenSolution
        # normalize to get length of sqrt(2) as in diagonal case:
        dir = dir / np.linalg.norm(dir) * np.sqrt(params.dimension)
    elif (params.gauDirection == 'ideal'):
        ideal = population.computeLocalIdeal(f_current)
        dir = chosenSolution - ideal
        # normalize to get length of sqrt(2) as in diagonal case:
        dir = dir / np.linalg.norm(dir) * np.sqrt(params.dimension)
    
    # new weight for the two Gaussian weight functions
    w1 = 'dist gau 0.8 mu '
    w2 = 'dist gau 0.2 mu '
    for i in range(0, params.dimension):
        w1 = w1 + str(chosenSolution[i]) + ' '
        w2 = w2 + str(chosenSolution[i]) + ' '
    w1 = w1 + 'dir '
    w2 = w2 + 'dir '
    for i in range(0, params.dimension):    
        w1 = w1 + str(dir[i]) + ' '
        w2 = w2 + str(dir[i]) + ' '
    w1 = w1 + ('sigma_eps ' + str(0.01*length)
                        + ' sigma_t ' + str(0.5*length) )
    w2 = w2 + ('sigma_eps ' + str(0.1*length)
                        + ' sigma_t ' + str(0.5*length) )
    return w1 + '\n' + w2

# end of do_interactionStep(population, params, generation, run)

def finalPlot():
    plt.show()
    
    
