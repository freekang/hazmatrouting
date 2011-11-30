% plot resulting populations of HypE with rand initialization
% after 100,000 funevals to compare different population sizes

figure(77);
hold all; grid on;

filename = 'results/graph_ns4_1_OriginalCosts_HypE_popsize25_randInit.4000';
H25 = dlmread(filename);
H25_nondom = H25(find(paretofront(H25(:,1:3))),:);
plot3(H25_nondom(:,1),H25_nondom(:,2),H25_nondom(:,3), 'x');

filename = 'results/graph_ns4_1_OriginalCosts_HypE_popsize50_randInit.2000';
H50 = dlmread(filename);
H50_nondom = H50(find(paretofront(H50(:,1:3))),:);
plot3(H50_nondom(:,1),H50_nondom(:,2),H50_nondom(:,3), 'o');

filename = 'results/graph_ns4_1_OriginalCosts_randInit.1000';
H100 = dlmread(filename);
H100_nondom = H100(find(paretofront(H100(:,1:3))),:);
plot3(H100_nondom(:,1),H100_nondom(:,2),H100_nondom(:,3), 's');

filename = 'results/graph_ns4_1_OriginalCosts_HypE_popsize200_randInit.500';
H200 = dlmread(filename);
H200_nondom = H200(find(paretofront(H200(:,1:3))),:);
plot3(H200_nondom(:,1),H200_nondom(:,2),H200_nondom(:,3), 'd');

allnondom = [H25; H50; H100; H200];
allnondom = allnondom(find(paretofront(allnondom(:,1:3))),:);
plot3(allnondom(:,1),allnondom(:,2),allnondom(:,3), 'pk');
xlabel('f_1')
ylabel('f_2')
zlabel('f_3')
view(45,30);



mysubplot3(H50_nondom, H100_nondom, H200_nondom, 'HypE, popsize 50', 'HypE, popsize 100', 'HypE, popsize 200', 'Nondominated solutions of 10 runs after 100,000 funevals', 'diffPopsizes.eps');


hold off;



