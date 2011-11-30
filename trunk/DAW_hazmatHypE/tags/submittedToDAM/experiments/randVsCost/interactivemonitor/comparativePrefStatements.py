
# preference statements should be given as a string of the following form:
# - PREFER s OVER t
# - where s and t can be simple statements such as f{1|...|k} {<|<=|==|>=|>} value
# - and the statements themselves can be combined with 'AND'
# Example: 'PREFER f1<4 AND f2>2 AND f3==4.2 OVER f1>4 AND f2<2'

import sys
import numpy as np
import string
from comparativePrefStatements import *

class comparativePrefStatements:
    
    ps = [] # preference statements of the form 'PREFER p OVER q'
    es = [] # equality statements of the form 'PREFER p EQUALLYTO q'

    # constructor for a population 'solutionSet' given as numpy array with
    # solutions as rows and objective values as columns while
    # the preference statements are read from a file with filename
    # 'fileName'
    def __init__(self, solutionSet, fileName=None):
        self.pop = solutionSet
        if (fileName != None):
            self.readPreferenceStatementsFromFile(fileName)

    
    def readPreferenceStatementsFromFile(self, fileName):
        self.ps = []
        self.es = []
        
        # read ids from 'fileName'
        try:
            file = open(fileName, 'r')
            for line in file:                
                if (line.find('OVER') >= 0):
                    self.ps.append(line)
                elif (line.find('EQUALLYTO') >= 0):
                    self.es.append(line)
            file.close()
        except IOError:
            print 'Problem reading file ' + fromFile

    def getLofP(self):
        return self.getAllLRSetPairs(self.ps)
    
    def getQofP(self):
        return self.getAllLRSetPairs(self.es)

    def getAllLRSetPairs(self, preferenceStatements):
        allIDs = np.arange(self.pop.shape[0])
        return self.getLRSetPairs(preferenceStatements, allIDs)

    def getLRSetPairs(self, preferenceStatements, ids):
        LRSetPairs = []
        for ps in preferenceStatements:
            LRSetPairs.append(self.getSetsLandRinAccordanceToPreference(ps, ids))
        return LRSetPairs

    def getSetsLandRinAccordanceToPreference(self, preferenceStatement,ids):
        pandq = self.extractPandQ(preferenceStatement)
        
        L = self.getSolutionsSatisfyingPbutNotQ(pandq, ids)
        R = self.getSolutionsSatisfyingQbutNotP(pandq, ids)
        
        return [L, R]

    # returns ids of solutions (in self.pop) that satisfy statement p but
    # not statement q, given in 'pandq' as
    # 'fi {<|<=|==|>=|>} c AND fj {<|<=|==|>=|>} d AND ... AND fk {<|<=|==|>=|>} e'
    def getSolutionsSatisfyingPbutNotQ(self, pandq, ids):
        satisfyingIDs = []
        for id in ids:
            if (self.hasProperty(id, pandq[0]) and not self.hasProperty(id, pandq[1])):
                satisfyingIDs.append(id)
        return satisfyingIDs

    # returns ids of solutions (in self.pop) that satisfy statement q but
    # not statement p, given in 'pandq' as
    # 'fi {<|<=|==|>=|>} c AND fj {<|<=|==|>=|>} d AND ... AND fk {<|<=|==|>=|>} e'
    def getSolutionsSatisfyingQbutNotP(self, pandq, ids):
        satisfyingIDs = []
        for id in ids:
            if (self.hasProperty(id, pandq[1]) and not self.hasProperty(id, pandq[0])):
                satisfyingIDs.append(id)
        return satisfyingIDs

    # extracts statements p and q from prefereceStatemen='PREFER p OVER q'
    def extractPandQ(self, preferenceStatement):
        preferenceStatement = string.replace(preferenceStatement, ' ', '')
        if (preferenceStatement.find('OVER') >= 0):
            splitString = string.split(preferenceStatement, 'OVER')
        elif (preferenceStatement.find('EQUALLYTO') >= 0):
            splitString = string.split(preferenceStatement, 'EQUALLYTO')
        else:
            print 'A PROBLEM OCCURED: ' + preferenceStatement + ' not correct'
        p = splitString[0][6:len(splitString[0])]
        q = splitString[1]
        return [p, q]

    # returns the set of all solutions from ids that are not contained in 
    # LR[0] (if specificity=='max' or LR[1] (if specificity=='min')
    def getNextAntichainForStrongSemantics(self, ids, LR, specificity='min'):
        for r in LR:
            toBeRemoved = []
            for id in ids:
                if specificity == 'min':
                    if id in r[1]:
                        toBeRemoved.append(id)
                if specificity == 'max':
                    if id in r[0]:
                        toBeRemoved.append(id)
            for id in toBeRemoved:
                ids.remove(id)
        return ids

    # returns 1 iff A is a subset of B, i.e., iff every a\in A is also
    # contained in B
    def isSubset(self, A, B):
        for a in A:
            if not (a in B):
                return 0
        return 1
    
    # returns 1 iff A is partly contained in B, i.e., iff there exists
    # at least one element in A \cap B, i.e., iff there exists at least
    # one element a in A that is also contained in B
    def isPartlyContained(self, A, B):
        for a in A:
            if a in B:
                return 1
        return 0

    # returns the partial order induced by the preference statements on
    # all solutions when strong semantics with the minimal specificity
    # principle are assumed
    def computePartialOrderWithStrongSemanticsMinSpecificityPrinciple(self):
        # init (make copies of preference statements for example)
        partialOrder = [] # will contain the partial order in the end
        ps = self.ps[:] # copy to be able to later on use it again
        es = self.es[:] # copy to be able to later on use it again
        solutionIDs = [] # solutions not yet used in the partial order (\Omega)
        allIDs = np.arange(self.pop.shape[0])
        for id in allIDs:
            solutionIDs.append(id)
        myLofP = self.getAllLRSetPairs(ps)
        myQofP = self.getAllLRSetPairs(es)
        
        while (len(solutionIDs) > 0):
            possibleNewAntichain = self.getNextAntichainForStrongSemantics(solutionIDs[:], myLofP, 'min')
            
            for LRpair in myQofP:
                subset = self.isSubset(LRpair[0], possibleNewAntichain)
                partlyContained = self.isPartlyContained(LRpair[1], possibleNewAntichain)
                if not subset and partlyContained:
                    # remove all entries of LRpair[1] from possibleNewAntichain
                    for s in LRpair[1]:
                        possibleNewAntichain.remove(s)
                if subset and not partlyContained:
                    # remove all entries of LRpair[0] from possibleNewAntichain
                    for s in LRpair[0]:
                        possibleNewAntichain.remove(s)
                
            if (len(possibleNewAntichain) == 0):
                break
            
            # reduce solutionIDs by the ones in possibleNewAntichain:
            for s in possibleNewAntichain:
                solutionIDs.remove(s)
                
            # update LofP and QofP according to what has been put into
            # possibleNewAntichain
            tempLofP = []
            tempQofP = []
            for LRpair in myLofP:
                # remove entries of possibleNewAntichain in LRPair[0]
                reducedLRpair = LRpair[:]
                for lr in reducedLRpair:
                    for s in possibleNewAntichain:
                        if s in reducedLRpair[0]:
                            reducedLRpair[0].remove(s)
                # if empty do not copy
                if len(reducedLRpair[0]) == 0:
                    continue
                # if not empty copy to tempLofP
                tempLofP.append(reducedLRpair)
            for LRpair in myQofP:
                # remove entries of possibleNewAntichain in LRPair[0]
                reducedLRpair = LRpair[:]
                for lr in reducedLRpair:
                    for s in possibleNewAntichain:
                        if s in reducedLRpair[0]:
                            reducedLRpair[0].remove(s)
                # if empty do not copy
                if len(reducedLRpair[0]) == 0:
                    continue
                # if not empty copy to tempQofP
                tempQofP.append(reducedLRpair)
                
            myLofP = tempLofP
            myQofP = tempQofP

            # finally, add possibleNewAntichain to partial order:
            partialOrder.append(possibleNewAntichain)

        return partialOrder


    # returns the partial order induced by the preference statements on
    # all solutions when strong semantics with the maximal specificity
    # principle are assumed
    def computePartialOrderWithStrongSemanticsMaxSpecificityPrinciple(self):
        # init (make copies of preference statements for example)
        partialOrder = [] # will contain the partial order in the end
        ps = self.ps[:] # copy to be able to later on use it again
        es = self.es[:] # copy to be able to later on use it again
        solutionIDs = [] # solutions not yet used in the partial order (\Omega)
        allIDs = np.arange(self.pop.shape[0])
        
        for id in allIDs:
            solutionIDs.append(id)
        myLofP = self.getAllLRSetPairs(ps)
        myQofP = self.getAllLRSetPairs(es)
        
        while (len(solutionIDs) > 0):
            possibleNewAntichain = self.getNextAntichainForStrongSemantics(solutionIDs[:], myLofP, 'max')
            
            for LRpair in myQofP:
                partlyContained = isPartlyContained(LRPair[0], possibleNewAntichain)
                subset = isSubset(LRpair[1], possibleNewAntichain)
                if partlyContained and not subset:
                    # remove all entries of LRpair[0] from possibleNewAntichain
                    for s in LRPair[0]:
                        possibleNewAntichain.remove(s)
                if not partlyContained and subset:
                    # remove all entries of LRpair[1] from possibleNewAntichain
                    for s in LRPair[1]:
                        possibleNewAntichain.remove(s)
                
            if (len(possibleNewAntichain) == 0):
                break
            
            # reduce solutionIDs by the ones in possibleNewAntichain:
            for s in possibleNewAntichain:
                solutionIDs.remove(s)
                
            # update LofP and QofP according to what has been put into
            # possibleNewAntichain
            tempLofP = []
            tempQofP = []
            for LRpair in myLofP:
                # remove entries of possibleNewAntichain in LRPair[1]
                reducedLRpair = LRpair[:]
                for lr in reducedLRpair:
                    for s in possibleNewAntichain:
                        if s in reducedLRpair[1]:
                            reducedLRpair[1].remove(s)
                # if empty do not copy
                if len(reducedLRpair[1]) == 0:
                    continue
                # if not empty copy to tempLofP
                tempLofP.append(reducedLRpair)
            for LRpair in myQofP:
                # remove entries of possibleNewAntichain in LRPair[1]
                reducedLRpair = LRpair[:]
                for lr in reducedLRpair:
                    for s in possibleNewAntichain:
                        if s in reducedLRpair[1]:
                            reducedLRpair[1].remove(s)
                # if empty do not copy
                if len(reducedLRpair[1]) == 0:
                    continue
                # if not empty copy to tempQofP
                tempQofP.append(reducedLRpair)
                
            myLofP = tempLofP
            myQofP = tempQofP

            # finally, add possibleNewAntichain to partial order:
            partialOrder.append(possibleNewAntichain)

        # before to return partial order, the list of antichains has to be
        # 'reverted'
        revertedPartialOrder = []
        while len(partialOrder) > 0:
            revertedPartialOrder.append(partialOrder.pop())
        
        return revertedPartialOrder

    # returns the partial order induced by the preference statements on
    # all solutions when optimistic semantics are assumed
    def computePartialOrderWithOptimisticSemantics(self):
        # init (make copies of preference statements for example)
        partialOrder = [] # will contain the partial order in the end
        ps = self.ps[:] # copy to be able to later on use it again
        es = self.es[:] # copy to be able to later on use it again
        solutionIDs = [] # solutions not yet used in the partial order (\Omega)
        allIDs = np.arange(self.pop.shape[0])
        for id in allIDs:
            solutionIDs.append(id)
        myLofP = self.getAllLRSetPairs(ps)
        myQofP = self.getAllLRSetPairs(es)
        
        while (len(solutionIDs) > 0):
            possibleNewAntichain = self.getNextAntichainForStrongSemantics(
                                                solutionIDs[:], myLofP, 'min')
            
            for LRpair in myQofP:
                partlyContainedL = self.isPartlyContained(LRpair[0],
                                                           possibleNewAntichain)
                partlyContainedR = self.isPartlyContained(LRpair[1],
                                                           possibleNewAntichain)
                if ((not partlyContainedL and partlyContainedR) 
                        or (partlyContainedL and not partlyContainedR)):
                    # remove all entries from possibleNewAntichain that are
                    # either in LRpair[0] or in LRpair[1] 
                    for s in LRpair[0]:
                        # avoid problems with entries both in LRPair[0] and
                        # LRPair[1]
                        if not (s in LRpair[1]):
                            if s in possibleNewAntichain:
                                possibleNewAntichain.remove(s)
                    for s in LRpair[1]:
                        if s in possibleNewAntichain:
                            possibleNewAntichain.remove(s)
                            
            if (len(possibleNewAntichain) == 0):
                break
            
            # reduce solutionIDs by the ones in possibleNewAntichain:
            for s in possibleNewAntichain:
                solutionIDs.remove(s)
                
            # remove satisfied preferences from LofP and QofP
            tempLofP = []
            tempQofP = []
            for LRpair in myLofP:
                copiedLRpair = LRpair[:]
                # copy only if LRpair[0] \cap possibleNewAntichain == \emptyset
                if not self.isPartlyContained(LRpair[0], possibleNewAntichain):
                    tempLofP.append(copiedLRpair)
            for LRpair in myQofP:
                copiedLRpair = LRpair[:]
                # copy only if LRpair[0] \cap possibleNewAntichain == \emptyset
                if not self.isPartlyContained(LRpair[0], possibleNewAntichain):
                    tempQofP.append(copiedLRpair)
                
            myLofP = tempLofP
            myQofP = tempQofP

            # finally, add possibleNewAntichain to partial order:
            partialOrder.append(possibleNewAntichain)

        return partialOrder

    # returns the partial order induced by the preference statements on
    # all solutions when pessimistic semantics are assumed
    def computePartialOrderWithPessimisticSemantics(self):
        # init (make copies of preference statements for example)
        partialOrder = [] # will contain the partial order in the end
        ps = self.ps[:] # copy to be able to later on use it again
        es = self.es[:] # copy to be able to later on use it again
        solutionIDs = [] # solutions not yet used in the partial order (\Omega)
        allIDs = np.arange(self.pop.shape[0])
        
        for id in allIDs:
            solutionIDs.append(id)
        myLofP = self.getAllLRSetPairs(ps)
        myQofP = self.getAllLRSetPairs(es)
        
        while (len(solutionIDs) > 0):
            possibleNewAntichain = self.getNextAntichainForStrongSemantics(
                                                solutionIDs[:], myLofP, 'max')

            for LRpair in myQofP:
                partlyContainedL = self.isPartlyContained(LRpair[0],
                                                           possibleNewAntichain)
                partlyContainedR = self.isPartlyContained(LRpair[1],
                                                           possibleNewAntichain)
                if ((not partlyContainedL and partlyContainedR) 
                        or (partlyContainedL and not partlyContainedR)):
                    # remove all entries from possibleNewAntichain that are
                    # either in LRpair[0] or in LRpair[1] 
                    for s in LRpair[0]:
                        # avoid problems with entries both in LRPair[0] and
                        # LRPair[1]
                        if not (s in LRpair[1]):
                            if s in possibleNewAntichain:
                                possibleNewAntichain.remove(s)
                    for s in LRpair[1]:
                        if s in possibleNewAntichain:
                            possibleNewAntichain.remove(s)
                            
            if (len(possibleNewAntichain) == 0):
                break
            
            # reduce solutionIDs by the ones in possibleNewAntichain:
            for s in possibleNewAntichain:
                solutionIDs.remove(s)
                
            # remove satisfied preferences from LofP and QofP
            tempLofP = []
            tempQofP = []
            for LRpair in myLofP:
                copiedLRpair = LRpair[:]
                # copy only if LRpair[1] \cap possibleNewAntichain == \emptyset
                if not self.isPartlyContained(LRpair[1], possibleNewAntichain):
                    tempLofP.append(copiedLRpair)
            for LRpair in myQofP:
                copiedLRpair = LRpair[:]
                # copy only if LRpair[1] \cap possibleNewAntichain == \emptyset
                if not self.isPartlyContained(LRpair[1], possibleNewAntichain):
                    tempQofP.append(copiedLRpair)
                
            myLofP = tempLofP
            myQofP = tempQofP

            # finally, add possibleNewAntichain to partial order:
            partialOrder.append(possibleNewAntichain)

        # before to return partial order, the list of antichains has to be
        # 'reverted'
        revertedPartialOrder = []
        while len(partialOrder) > 0:
            revertedPartialOrder.append(partialOrder.pop())
        
        return revertedPartialOrder

    # returns true iff the solution with id 'solutionID' satisfies the
    # statement given in string 'statement'
    # where 'statement' should be of the following structure:
    # 'fi {<|<=|==|>=|>} c AND fj {<|<=|==|>=|>} d AND ... AND fk {<|<=|==|>=|>} e'
    # with
    #    - i,j,k \in {1,\ldots, number of objectives}
    #    - c,d,e \in \mathds{R}
    # Note that the user has to specify the objectives as fi with i\in {1,..., max}
    # whereas within the code, the objectives are indexed from 0 to max-1.
    def hasProperty(self, solutionID, statement):
        statement = string.replace(statement, ' ', '')
        splitString = string.split(statement, 'AND')
        
        for s in splitString:
            c = string.count(s, '=')
        
            if (c == 2):
                leftEqualSign = string.find(s, '=')
                rightEqualSign = string.rfind(s, '=')

                objective = float(s[1:leftEqualSign])
                value = float(s[rightEqualSign + 1:len(s)])
            
                if (self.pop[solutionID, objective-1] != value):
                    return 0
            elif (c==1):
                posEqualSign = string.find(s, '=')
                posSmallerSign = string.rfind(s, '<')
                posLargerSign = string.rfind(s, '>')
                
                if (posSmallerSign != -1):
                    objective = float(s[1:posSmallerSign])
                elif (posLargerSign != -1):
                    objective = float(s[1:posLargerSign])
                value = float(s[posEqualSign+1:len(s)])

                if (posSmallerSign != -1 and
                                    self.pop[solutionID, objective-1] > value):
                    return 0
                if (posLargerSign != -1 and
                                    self.pop[solutionID, objective-1] < value):
                    return 0
            elif (c==0):
                posSmallerSign = string.rfind(s, '<')
                posLargerSign = string.rfind(s, '>')
                
                if (posSmallerSign != -1):
                    objective = float(s[1:posSmallerSign])
                    value = float(s[posSmallerSign+1:len(s)])
                elif (posLargerSign != -1):
                    objective = float(s[1:posLargerSign])
                    value = float(s[posLargerSign+1:len(s)])
                
                if (posSmallerSign != -1 and
                                    self.pop[solutionID, objective-1] >= value):
                    return 0
                if (posLargerSign != -1 and
                                    self.pop[solutionID, objective-1] <= value):
                    return 0
        
        return 1
    
    
# end of class comparativePrefStatements