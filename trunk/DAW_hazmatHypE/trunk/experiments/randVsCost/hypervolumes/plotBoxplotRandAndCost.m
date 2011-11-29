% plot resulting populations of HypE with rand and cost initialization
% after 100,000 funevals to compare initialization schemes

addpath('../');

hypervolumes = zeros(10, 8);
labels = {'rand_250'; 'rand_2000'; 'cost_250'; 'cost_2000'; 'rand_250'; 'rand_2000'; 'cost_250'; 'cost_2000'};

filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize50_randInit.250';
hypervolumes(:,1) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize50_randInit.2000';
hypervolumes(:,2) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize50_costInit.250';
hypervolumes(:,3) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_OriginalCosts_HypE_popsize50_costInit.2000';
hypervolumes(:,4) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_New_HypE_popsize50_randInit.250';
hypervolumes(:,5) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_New_HypE_popsize50_randInit.2000';
hypervolumes(:,6) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_New_HypE_popsize50_costInit.250';
hypervolumes(:,7) = -1 * dlmread(filename);

filename = 'HYP_ns4_1_New_HypE_popsize50_costInit.2000';
hypervolumes(:,8) = -1 * dlmread(filename);



boxplot(hypervolumes(:,1:4), 'Labels', labels(1:4));
% save plot as eps:
exportfig(gcf, 'initBoxPlots_OriginalCosts.eps');


figure;
boxplot(hypervolumes(:,5:8), 'Labels', labels(5:8));
% save plot as eps:
exportfig(gcf, 'initBoxPlots_New.eps');


