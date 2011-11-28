
generations = [0 250 500 750 1000 1250 1500 1750 2000];

hypervolumes = zeros(10, size(generations, 2));

i = 1;
for g = generations
    filename = ['HYP_ns4_1_OriginalCosts_HypE_popsize50_randInit.' int2str(g)];
    hypervolumes(:,i) = -1 * dlmread(filename);
    i = i + 1;
end


%boxplot(hypervolumes, 'Labels', {'0'; '250'; '500'; '750'; '1000'; '1250'; '1500'; '1750'; '2000'});

figure(3)
boxplot(hypervolumes(:,2:end), 'Labels', {'250'; '500'; '750'; '1000'; '1250'; '1500'; '1750'; '2000'});

ylim([9.72e26 9.745e26]);
xlabel('generations');
ylabel('hypervolume');

% save plot as eps:
exportfig(gcf, 'hypervolumeOverTime_50.eps');

