% plot resulting populations of HypE and NSGA-II runs
% after certain number of generations

%%%%%%%%%%%%%%%%%%%%%%%
generation = 250;
numOfCommodities = 4;
instance = 1;
%%%%%%%%%%%%%%%%%%%%%%%

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_OriginalCosts_randInit.' int2str(generation)];
H = dlmread(filename);
H_nondom = H(find(paretofront(H(:,1:3))),:);
figure(77);
plot3(H_nondom(:,1),H_nondom(:,2),H_nondom(:,3), 'o');
hold all; grid on;

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_OriginalCosts_nsga2_randInit.' int2str(generation)];
N = dlmread(filename);
N_nondom = N(find(paretofront(N(:,1:3))),:);
plot3(N_nondom(:,1),N_nondom(:,2),N_nondom(:,3), 'o');
hold off;


title = ['all solutions after ' int2str(generation) ' generations'];
mysubplot(H,N, 'HypE\_randinit', 'NSGA-II\_randinit', title, 'allsolutions.eps');
title = ['nondominated solutions after ' int2str(generation) ' generations'];
mysubplot(H_nondom,N_nondom, 'HypE\_randinit', 'NSGA-II\_randinit', title, 'onlynondominated.eps');

hold off;