function [] = mysubplotRandVsCost(A, B, A_label, B_label, titlename, filename)
figure()

subplot(2,2,1)
plot3(A(:,1),A(:,2),A(:,3), 'o');
hold all; grid on;
plot3(B(:,1),B(:,2),B(:,3), 'x');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')
if strncmp(filename, 'randVsCost_ns4_1_New_', 20) 
    view([19.5 22]);
    %xlim([0.5e4 1.2e4]);
else
    view([14.5 10]);
    xlim([0.5e4 1.2e4]);
end
    
subplot(2,2,2)
plot(A(:,1),A(:,2), 'o');
hold all; grid on;
plot(B(:,1),B(:,2), 'x');
%ylim([4,6]*1e6);
h_legend = legend(A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('total risk')
if strncmp(filename, 'randVsCost_ns4_1_New_', 20) 
    ylim([4.8 8]*1e7);
end

subplot(2,2,3)
plot(A(:,1),A(:,3), 'o');
hold all; grid on;
plot(B(:,1),B(:,3), 'x');
%ylim([2,5]*1e5);
h_legend = legend(A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
plot(A(:,2),A(:,3), 'o');
hold all; grid on;
plot(B(:,2),B(:,3), 'x');
%ylim([2,5]*1e5);
h_legend = legend(A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('total risk')
ylabel('equity risk')

% save plot as eps:
exportfig(gcf, filename);

% add title above all for plot on screen:
set(gcf,'NextPlot','add');
axes;
h = title(titlename);
set(gca,'Visible','off');
set(h,'Visible','on'); 


end
