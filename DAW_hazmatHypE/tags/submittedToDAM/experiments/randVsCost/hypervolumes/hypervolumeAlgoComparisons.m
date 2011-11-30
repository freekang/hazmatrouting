addpath('../');

hypervolumes = zeros(10, 6);

% HypE popsize 25
filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize25_randInit.4000';
hypervolumes(:,1) = -1 * dlmread(filename);
% HypE popsize 50
filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize50_randInit.2000';
hypervolumes(:,2) = -1 * dlmread(filename);
% HypE popsize 100
filename = 'HYP_ns4_1_OriginalCosts_randInit.1000';
hypervolumes(:,3) = -1 * dlmread(filename);
% HypE popsize 200
filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize200_randInit.500';
hypervolumes(:,4) = -1 * dlmread(filename);
% NSGA-II
filename = 'HYP_ns4_1_OriginalCosts_nsga2_randInit.1000';
hypervolumes(:,5) = -1 * dlmread(filename);
% W-HypE
filename = 'HYP_ns4_1_OriginalCosts_whype_box2_popsize25_randInit.4000';
hypervolumes(:,6) = -1 * dlmread(filename);




boxplot(hypervolumes, 'Labels', {'HypE_25'; 'HypE_50'; 'HypE_100'; 'HypE_200'; 'NSGA-II'; 'W-HypE'});



% save plot as eps:
exportfig(gcf, 'algoComparison.eps');

