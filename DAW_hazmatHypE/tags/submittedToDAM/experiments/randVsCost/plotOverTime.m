% plot resulting populations of HypE_randinit over time

%%%%%%%%%%%%%%%%%%%%%%%
generation1 = 0;
generation2 = 250;
generation3 = 2000;
numOfCommodities = 4;
instance = 1;
%%%%%%%%%%%%%%%%%%%%%%%

title = 'HypE population after different number of generations';

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_OriginalCosts_randInit.' int2str(generation1)];
H1 = dlmread(filename);
H1_nondom = H1(find(paretofront(H1(:,1:3))),:);

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_OriginalCosts_randInit.' int2str(generation2)];
H2 = dlmread(filename);
H2_nondom = H2(find(paretofront(H2(:,1:3))),:);

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_OriginalCosts_randInit.' int2str(generation3)];
H3 = dlmread(filename);
H3_nondom = H3(find(paretofront(H3(:,1:3))),:);


mysubplot3(H1_nondom,H2_nondom,H3_nondom, ['HypE\_randinit@generation ' int2str(generation1)], ...
            ['HypE\_randinit@generation ' int2str(generation2)], ...
            ['HypE\_randinit@generation ' int2str(generation3)], title, 'onlynondominated.eps');

subplot(2,2,4)
plot3(1,2,3, 'dr');

hold off;