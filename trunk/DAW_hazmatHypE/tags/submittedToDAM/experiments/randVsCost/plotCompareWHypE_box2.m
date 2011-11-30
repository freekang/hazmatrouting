% plot resulting populations of W-HypE vs. HypE
% after certain number of generations

%%%%%%%%%%%%%%%%%%%%%%%
generation = 4000;
%%%%%%%%%%%%%%%%%%%%%%%

% sampling box:
Supperlower = [7e3 3e7 0.5e6; 1e4 4e7 1.1e6];

figure(2345);
S = UniformSampling(10000,Supperlower(1,:), Supperlower(2,:));
plot3(S(:,1),S(:,2),S(:,3), 'y.', 'MarkerSize', 2);
hold all; grid on;

% first W-HypE:
%%%%%%%%%%%%%%%
filename = ['results/graph_ns4_1_OriginalCosts_WHypE_box2_popsize25_randInit.' int2str(generation)];
W = dlmread(filename);
W_nondom = W(find(paretofront(W(:,1:3))),:);
plot3(W_nondom(:,1),W_nondom(:,2),W_nondom(:,3), 'o');

filename = ['results/graph_ns4_1_OriginalCosts_HypE_popsize25_randInit.' int2str(generation)];
H = dlmread(filename);
H_nondom = H(find(paretofront(H(:,1:3))),:);
plot3(H_nondom(:,1),H_nondom(:,2),H_nondom(:,3), 'o');


all = [W_nondom; H_nondom];
all = all(find(paretofront(all(:,1:3))),:);
plot3(all(:,1),all(:,2),all(:,3), 'sr');
hold off;

title = ['nondominated solutions after ' int2str(generation) ' generations'];
mysubplot3sampling(W_nondom, H_nondom, Supperlower, 'W-HypE', 'HypE', 'sample box', title, 'WHypEVsHypE_ns4_1_OriginalCosts2.eps');

hold off;